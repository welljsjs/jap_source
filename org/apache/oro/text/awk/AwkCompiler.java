/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.awk;

import org.apache.oro.text.awk.AwkPattern;
import org.apache.oro.text.awk.CatNode;
import org.apache.oro.text.awk.CharacterClassNode;
import org.apache.oro.text.awk.NegativeCharacterClassNode;
import org.apache.oro.text.awk.OrNode;
import org.apache.oro.text.awk.PlusNode;
import org.apache.oro.text.awk.QuestionNode;
import org.apache.oro.text.awk.StarNode;
import org.apache.oro.text.awk.SyntaxNode;
import org.apache.oro.text.awk.SyntaxTree;
import org.apache.oro.text.awk.TokenNode;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;

public final class AwkCompiler
implements PatternCompiler {
    public static final int DEFAULT_MASK = 0;
    public static final int CASE_INSENSITIVE_MASK = 1;
    public static final int MULTILINE_MASK = 2;
    static final char _END_OF_INPUT = '\uffff';
    private boolean __inCharacterClass;
    private boolean __caseSensitive;
    private boolean __multiline;
    private boolean __beginAnchor;
    private boolean __endAnchor;
    private char __lookahead;
    private int __position;
    private int __bytesRead;
    private int __expressionLength;
    private char[] __regularExpression;
    private int __openParen;
    private int __closeParen;

    private static boolean __isMetachar(char c) {
        return c == '*' || c == '?' || c == '+' || c == '[' || c == ']' || c == '(' || c == ')' || c == '|' || c == '.';
    }

    static boolean _isWordCharacter(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '_';
    }

    static boolean _isLowerCase(char c) {
        return c >= 'a' && c <= 'z';
    }

    static boolean _isUpperCase(char c) {
        return c >= 'A' && c <= 'Z';
    }

    static char _toggleCase(char c) {
        if (AwkCompiler._isUpperCase(c)) {
            return (char)(c + 32);
        }
        if (AwkCompiler._isLowerCase(c)) {
            return (char)(c - 32);
        }
        return c;
    }

    private void __match(char c) throws MalformedPatternException {
        if (c == this.__lookahead) {
            this.__lookahead = this.__bytesRead < this.__expressionLength ? this.__regularExpression[this.__bytesRead++] : (char)65535;
        } else {
            throw new MalformedPatternException("token: " + c + " does not match lookahead: " + this.__lookahead + " at position: " + this.__bytesRead);
        }
    }

    private void __putback() {
        if (this.__lookahead != '\uffff') {
            --this.__bytesRead;
        }
        this.__lookahead = this.__regularExpression[this.__bytesRead - 1];
    }

    private SyntaxNode __regex() throws MalformedPatternException {
        SyntaxNode syntaxNode = this.__branch();
        if (this.__lookahead == '|') {
            this.__match('|');
            return new OrNode(syntaxNode, this.__regex());
        }
        return syntaxNode;
    }

    private SyntaxNode __branch() throws MalformedPatternException {
        CatNode catNode;
        SyntaxNode syntaxNode = this.__piece();
        if (this.__lookahead == ')') {
            if (this.__openParen > this.__closeParen) {
                return syntaxNode;
            }
            throw new MalformedPatternException("Parse error: close parenthesis without matching open parenthesis at position " + this.__bytesRead);
        }
        if (this.__lookahead == '|' || this.__lookahead == '\uffff') {
            return syntaxNode;
        }
        CatNode catNode2 = catNode = new CatNode();
        catNode._left = syntaxNode;
        while (true) {
            syntaxNode = this.__piece();
            if (this.__lookahead == ')') {
                if (this.__openParen > this.__closeParen) {
                    catNode._right = syntaxNode;
                    break;
                }
                throw new MalformedPatternException("Parse error: close parenthesis without matching open parenthesis at position " + this.__bytesRead);
            }
            if (this.__lookahead == '|' || this.__lookahead == '\uffff') {
                catNode._right = syntaxNode;
                break;
            }
            catNode._right = new CatNode();
            catNode = (CatNode)catNode._right;
            catNode._left = syntaxNode;
        }
        return catNode2;
    }

    private SyntaxNode __piece() throws MalformedPatternException {
        SyntaxNode syntaxNode = this.__atom();
        switch (this.__lookahead) {
            case '+': {
                this.__match('+');
                return new PlusNode(syntaxNode);
            }
            case '?': {
                this.__match('?');
                return new QuestionNode(syntaxNode);
            }
            case '*': {
                this.__match('*');
                return new StarNode(syntaxNode);
            }
            case '{': {
                return this.__repetition(syntaxNode);
            }
        }
        return syntaxNode;
    }

    private int __parseUnsignedInteger(int n, int n2, int n3) throws MalformedPatternException {
        int n4;
        int n5;
        StringBuffer stringBuffer = new StringBuffer(4);
        for (n5 = 0; Character.digit(this.__lookahead, n) != -1 && n5 < n3; ++n5) {
            stringBuffer.append(this.__lookahead);
            this.__match(this.__lookahead);
        }
        if (n5 < n2 || n5 > n3) {
            throw new MalformedPatternException("Parse error: unexpected number of digits at position " + this.__bytesRead);
        }
        try {
            n4 = Integer.parseInt(stringBuffer.toString(), n);
        }
        catch (NumberFormatException numberFormatException) {
            throw new MalformedPatternException("Parse error: numeric value at position " + this.__bytesRead + " is invalid");
        }
        return n4;
    }

    private SyntaxNode __repetition(SyntaxNode syntaxNode) throws MalformedPatternException {
        CatNode catNode = null;
        this.__match('{');
        int n = this.__parseUnsignedInteger(10, 1, Integer.MAX_VALUE);
        int[] arrn = new int[]{this.__position};
        if (this.__lookahead == '}') {
            CatNode catNode2;
            this.__match('}');
            if (n == 0) {
                throw new MalformedPatternException("Parse error: Superfluous interval specified at position " + this.__bytesRead + ".  Number of occurences was set to zero.");
            }
            if (n == 1) {
                return syntaxNode;
            }
            catNode = catNode2 = new CatNode();
            catNode2._left = syntaxNode;
            while (--n > 1) {
                syntaxNode = syntaxNode._clone(arrn);
                catNode2._right = new CatNode();
                catNode2 = (CatNode)catNode2._right;
                catNode2._left = syntaxNode;
            }
            catNode2._right = syntaxNode._clone(arrn);
        } else if (this.__lookahead == ',') {
            this.__match(',');
            if (this.__lookahead == '}') {
                CatNode catNode3;
                this.__match('}');
                if (n == 0) {
                    return new StarNode(syntaxNode);
                }
                if (n == 1) {
                    return new PlusNode(syntaxNode);
                }
                catNode = catNode3 = new CatNode();
                catNode3._left = syntaxNode;
                while (--n > 0) {
                    syntaxNode = syntaxNode._clone(arrn);
                    catNode3._right = new CatNode();
                    catNode3 = (CatNode)catNode3._right;
                    catNode3._left = syntaxNode;
                }
                catNode3._right = new StarNode(syntaxNode._clone(arrn));
            } else {
                int n2 = this.__parseUnsignedInteger(10, 1, Integer.MAX_VALUE);
                this.__match('}');
                if (n2 < n) {
                    throw new MalformedPatternException("Parse error: invalid interval; " + n2 + " is less than " + n + " at position " + this.__bytesRead);
                }
                if (n2 == 0) {
                    throw new MalformedPatternException("Parse error: Superfluous interval specified at position " + this.__bytesRead + ".  Number of occurences was set to zero.");
                }
                if (n == 0) {
                    CatNode catNode4;
                    if (n2 == 1) {
                        return new QuestionNode(syntaxNode);
                    }
                    catNode = catNode4 = new CatNode();
                    catNode4._left = syntaxNode = new QuestionNode(syntaxNode);
                    while (--n2 > 1) {
                        syntaxNode = syntaxNode._clone(arrn);
                        catNode4._right = new CatNode();
                        catNode4 = (CatNode)catNode4._right;
                        catNode4._left = syntaxNode;
                    }
                    catNode4._right = syntaxNode._clone(arrn);
                } else if (n == n2) {
                    CatNode catNode5;
                    if (n == 1) {
                        return syntaxNode;
                    }
                    catNode = catNode5 = new CatNode();
                    catNode5._left = syntaxNode;
                    while (--n > 1) {
                        syntaxNode = syntaxNode._clone(arrn);
                        catNode5._right = new CatNode();
                        catNode5 = (CatNode)catNode5._right;
                        catNode5._left = syntaxNode;
                    }
                    catNode5._right = syntaxNode._clone(arrn);
                } else {
                    int n3;
                    CatNode catNode6;
                    catNode = catNode6 = new CatNode();
                    catNode6._left = syntaxNode;
                    for (n3 = 1; n3 < n; ++n3) {
                        syntaxNode = syntaxNode._clone(arrn);
                        catNode6._right = new CatNode();
                        catNode6 = (CatNode)catNode6._right;
                        catNode6._left = syntaxNode;
                    }
                    syntaxNode = new QuestionNode(syntaxNode._clone(arrn));
                    n3 = n2 - n;
                    if (n3 == 1) {
                        catNode6._right = syntaxNode;
                    } else {
                        catNode6._right = new CatNode();
                        catNode6 = (CatNode)catNode6._right;
                        catNode6._left = syntaxNode;
                        while (--n3 > 1) {
                            syntaxNode = syntaxNode._clone(arrn);
                            catNode6._right = new CatNode();
                            catNode6 = (CatNode)catNode6._right;
                            catNode6._left = syntaxNode;
                        }
                        catNode6._right = syntaxNode._clone(arrn);
                    }
                }
            }
        } else {
            throw new MalformedPatternException("Parse error: unexpected character " + this.__lookahead + " in interval at position " + this.__bytesRead);
        }
        this.__position = arrn[0];
        return catNode;
    }

    private SyntaxNode __backslashToken() throws MalformedPatternException {
        SyntaxNode syntaxNode;
        this.__match('\\');
        if (this.__lookahead == 'x') {
            this.__match('x');
            syntaxNode = this._newTokenNode((char)this.__parseUnsignedInteger(16, 2, 2), this.__position++);
        } else if (this.__lookahead == 'c') {
            this.__match('c');
            char c = Character.toUpperCase(this.__lookahead);
            c = (char)(c > '?' ? c - 64 : c + 64);
            syntaxNode = new TokenNode(c, this.__position++);
            this.__match(this.__lookahead);
        } else if (this.__lookahead >= '0' && this.__lookahead <= '9') {
            this.__match(this.__lookahead);
            if (this.__lookahead >= '0' && this.__lookahead <= '9') {
                this.__putback();
                int n = this.__parseUnsignedInteger(10, 2, 3);
                n = Integer.parseInt(Integer.toString(n), 8);
                syntaxNode = this._newTokenNode((char)n, this.__position++);
            } else {
                this.__putback();
                if (this.__lookahead == '0') {
                    this.__match('0');
                    syntaxNode = new TokenNode('\u0000', this.__position++);
                } else {
                    int n = Character.digit(this.__lookahead, 10);
                    syntaxNode = this._newTokenNode(this.__lookahead, this.__position++);
                    this.__match(this.__lookahead);
                }
            }
        } else if (this.__lookahead == 'b') {
            syntaxNode = new TokenNode('\b', this.__position++);
            this.__match('b');
        } else {
            char c = this.__lookahead;
            switch (this.__lookahead) {
                case 'n': {
                    c = '\n';
                    break;
                }
                case 'r': {
                    c = '\r';
                    break;
                }
                case 't': {
                    c = '\t';
                    break;
                }
                case 'f': {
                    c = '\f';
                }
            }
            switch (c) {
                case 'd': {
                    CharacterClassNode characterClassNode = new CharacterClassNode(this.__position++);
                    characterClassNode._addTokenRange(48, 57);
                    syntaxNode = characterClassNode;
                    break;
                }
                case 'D': {
                    NegativeCharacterClassNode negativeCharacterClassNode = new NegativeCharacterClassNode(this.__position++);
                    negativeCharacterClassNode._addTokenRange(48, 57);
                    syntaxNode = negativeCharacterClassNode;
                    break;
                }
                case 'w': {
                    CharacterClassNode characterClassNode = new CharacterClassNode(this.__position++);
                    characterClassNode._addTokenRange(48, 57);
                    characterClassNode._addTokenRange(97, 122);
                    characterClassNode._addTokenRange(65, 90);
                    characterClassNode._addToken(95);
                    syntaxNode = characterClassNode;
                    break;
                }
                case 'W': {
                    NegativeCharacterClassNode negativeCharacterClassNode = new NegativeCharacterClassNode(this.__position++);
                    negativeCharacterClassNode._addTokenRange(48, 57);
                    negativeCharacterClassNode._addTokenRange(97, 122);
                    negativeCharacterClassNode._addTokenRange(65, 90);
                    negativeCharacterClassNode._addToken(95);
                    syntaxNode = negativeCharacterClassNode;
                    break;
                }
                case 's': {
                    CharacterClassNode characterClassNode = new CharacterClassNode(this.__position++);
                    characterClassNode._addToken(32);
                    characterClassNode._addToken(12);
                    characterClassNode._addToken(10);
                    characterClassNode._addToken(13);
                    characterClassNode._addToken(9);
                    syntaxNode = characterClassNode;
                    break;
                }
                case 'S': {
                    NegativeCharacterClassNode negativeCharacterClassNode = new NegativeCharacterClassNode(this.__position++);
                    negativeCharacterClassNode._addToken(32);
                    negativeCharacterClassNode._addToken(12);
                    negativeCharacterClassNode._addToken(10);
                    negativeCharacterClassNode._addToken(13);
                    negativeCharacterClassNode._addToken(9);
                    syntaxNode = negativeCharacterClassNode;
                    break;
                }
                default: {
                    syntaxNode = this._newTokenNode(c, this.__position++);
                }
            }
            this.__match(this.__lookahead);
        }
        return syntaxNode;
    }

    private SyntaxNode __atom() throws MalformedPatternException {
        SyntaxNode syntaxNode;
        if (this.__lookahead == '(') {
            this.__match('(');
            ++this.__openParen;
            syntaxNode = this.__regex();
            this.__match(')');
            ++this.__closeParen;
        } else if (this.__lookahead == '[') {
            syntaxNode = this.__characterClass();
        } else if (this.__lookahead == '.') {
            this.__match('.');
            NegativeCharacterClassNode negativeCharacterClassNode = new NegativeCharacterClassNode(this.__position++);
            if (this.__multiline) {
                negativeCharacterClassNode._addToken(10);
            }
            syntaxNode = negativeCharacterClassNode;
        } else if (this.__lookahead == '\\') {
            syntaxNode = this.__backslashToken();
        } else if (!AwkCompiler.__isMetachar(this.__lookahead)) {
            syntaxNode = this._newTokenNode(this.__lookahead, this.__position++);
            this.__match(this.__lookahead);
        } else {
            throw new MalformedPatternException("Parse error: unexpected character " + this.__lookahead + " at position " + this.__bytesRead);
        }
        return syntaxNode;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    private SyntaxNode __characterClass() throws MalformedPatternException {
        this.__match('[');
        this.__inCharacterClass = true;
        if (this.__lookahead == '^') {
            this.__match('^');
            var4_1 = new NegativeCharacterClassNode(this.__position++);
        } else {
            var4_1 = new CharacterClassNode(this.__position++);
        }
        while (this.__lookahead != ']' && this.__lookahead != '\uffff') {
            if (this.__lookahead != '\\') ** GOTO lbl24
            var3_4 = this.__backslashToken();
            --this.__position;
            if (var3_4 instanceof TokenNode) {
                var1_2 = ((TokenNode)var3_4)._token;
                var4_1._addToken(var1_2);
                if (!this.__caseSensitive) {
                    var4_1._addToken(AwkCompiler._toggleCase(var1_2));
                }
            } else {
                var5_5 = (CharacterClassNode)var3_4;
                for (var2_3 = '\u0000'; var2_3 < '\u0100'; var2_3 = (char)(var2_3 + '\u0001')) {
                    if (!var5_5._matches(var2_3)) continue;
                    var4_1._addToken(var2_3);
                }
                continue;
lbl24:
                // 1 sources

                var1_2 = this.__lookahead;
                var4_1._addToken(this.__lookahead);
                if (!this.__caseSensitive) {
                    var4_1._addToken(AwkCompiler._toggleCase(this.__lookahead));
                }
                this.__match(this.__lookahead);
            }
            if (this.__lookahead != '-') continue;
            this.__match('-');
            if (this.__lookahead == ']') {
                var4_1._addToken(45);
                break;
            }
            if (this.__lookahead == '\\') {
                var3_4 = this.__backslashToken();
                --this.__position;
                if (var3_4 instanceof TokenNode == false) throw new MalformedPatternException("Parse error: invalid range specified at position " + this.__bytesRead);
                var2_3 = ((TokenNode)var3_4)._token;
            } else {
                var2_3 = this.__lookahead;
                this.__match(this.__lookahead);
            }
            if (var2_3 < var1_2) {
                throw new MalformedPatternException("Parse error: invalid range specified at position " + this.__bytesRead);
            }
            var4_1._addTokenRange(var1_2 + '\u0001', var2_3);
            if (this.__caseSensitive) continue;
            var4_1._addTokenRange(AwkCompiler._toggleCase((char)(var1_2 + '\u0001')), AwkCompiler._toggleCase(var2_3));
        }
        this.__match(']');
        this.__inCharacterClass = false;
        return var4_1;
    }

    SyntaxNode _newTokenNode(char c, int n) {
        if (!this.__inCharacterClass && !this.__caseSensitive && (AwkCompiler._isUpperCase(c) || AwkCompiler._isLowerCase(c))) {
            CharacterClassNode characterClassNode = new CharacterClassNode(n);
            characterClassNode._addToken(c);
            characterClassNode._addToken(AwkCompiler._toggleCase(c));
            return characterClassNode;
        }
        return new TokenNode(c, n);
    }

    SyntaxTree _parse(char[] arrc) throws MalformedPatternException {
        SyntaxTree syntaxTree;
        this.__closeParen = 0;
        this.__openParen = 0;
        this.__regularExpression = arrc;
        this.__bytesRead = 0;
        this.__expressionLength = arrc.length;
        this.__inCharacterClass = false;
        this.__position = 0;
        this.__match(this.__lookahead);
        if (this.__lookahead == '^') {
            this.__beginAnchor = true;
            this.__match(this.__lookahead);
        }
        if (this.__expressionLength > 0 && arrc[this.__expressionLength - 1] == '$') {
            --this.__expressionLength;
            this.__endAnchor = true;
        }
        if (this.__expressionLength > 1 || this.__expressionLength == 1 && !this.__beginAnchor) {
            CatNode catNode = new CatNode();
            catNode._left = this.__regex();
            catNode._right = new TokenNode('\u0100', this.__position++);
            syntaxTree = new SyntaxTree(catNode, this.__position);
        } else {
            syntaxTree = new SyntaxTree(new TokenNode('\u0100', 0), 1);
        }
        syntaxTree._computeFollowPositions();
        return syntaxTree;
    }

    public Pattern compile(char[] arrc, int n) throws MalformedPatternException {
        this.__endAnchor = false;
        this.__beginAnchor = false;
        this.__caseSensitive = (n & 1) == 0;
        this.__multiline = (n & 2) != 0;
        SyntaxTree syntaxTree = this._parse(arrc);
        AwkPattern awkPattern = new AwkPattern(new String(arrc), syntaxTree);
        awkPattern._options = n;
        awkPattern._hasBeginAnchor = this.__beginAnchor;
        awkPattern._hasEndAnchor = this.__endAnchor;
        return awkPattern;
    }

    public Pattern compile(String string, int n) throws MalformedPatternException {
        this.__endAnchor = false;
        this.__beginAnchor = false;
        this.__caseSensitive = (n & 1) == 0;
        this.__multiline = (n & 2) != 0;
        SyntaxTree syntaxTree = this._parse(string.toCharArray());
        AwkPattern awkPattern = new AwkPattern(string, syntaxTree);
        awkPattern._options = n;
        awkPattern._hasBeginAnchor = this.__beginAnchor;
        awkPattern._hasEndAnchor = this.__endAnchor;
        return awkPattern;
    }

    public Pattern compile(char[] arrc) throws MalformedPatternException {
        return this.compile(arrc, 0);
    }

    public Pattern compile(String string) throws MalformedPatternException {
        return this.compile(string, 0);
    }
}

