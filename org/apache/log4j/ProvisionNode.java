/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j;

import java.util.Vector;
import org.apache.log4j.Logger;

class ProvisionNode
extends Vector {
    ProvisionNode(Logger logger) {
        this.addElement(logger);
    }
}

