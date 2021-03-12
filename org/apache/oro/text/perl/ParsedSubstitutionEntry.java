/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.perl;

import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Substitution;

final class ParsedSubstitutionEntry {
    int _numSubstitutions;
    Pattern _pattern;
    Perl5Substitution _substitution;

    ParsedSubstitutionEntry(Pattern pattern, Perl5Substitution perl5Substitution, int n) {
        this._numSubstitutions = n;
        this._substitution = perl5Substitution;
        this._pattern = pattern;
    }
}

