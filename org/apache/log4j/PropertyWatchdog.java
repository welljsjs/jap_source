/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.FileWatchdog;

class PropertyWatchdog
extends FileWatchdog {
    PropertyWatchdog(String string) {
        super(string);
    }

    public void doOnChange() {
        new PropertyConfigurator().doConfigure(this.filename, LogManager.getLoggerRepository());
    }
}

