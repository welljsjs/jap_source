/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j.spi;

import org.apache.log4j.spi.LoggerRepository;

public interface RepositorySelector {
    public LoggerRepository getLoggerRepository();
}

