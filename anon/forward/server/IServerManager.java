/*
 * Decompiled with CFR 0.150.
 */
package anon.forward.server;

import anon.forward.server.ForwardScheduler;

public interface IServerManager {
    public Object getId();

    public void startServerManager(ForwardScheduler var1) throws Exception;

    public void shutdown();
}

