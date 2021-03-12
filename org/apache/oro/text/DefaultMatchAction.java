/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text;

import org.apache.oro.text.MatchAction;
import org.apache.oro.text.MatchActionInfo;

final class DefaultMatchAction
implements MatchAction {
    DefaultMatchAction() {
    }

    public void processMatch(MatchActionInfo matchActionInfo) {
        matchActionInfo.output.println(matchActionInfo.line);
    }
}

