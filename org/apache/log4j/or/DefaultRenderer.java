/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j.or;

import org.apache.log4j.or.ObjectRenderer;

class DefaultRenderer
implements ObjectRenderer {
    DefaultRenderer() {
    }

    public String doRender(Object object) {
        return object.toString();
    }
}

