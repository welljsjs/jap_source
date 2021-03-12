/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.HTTPClientModuleConstants;
import HTTPClient.ModuleException;
import HTTPClient.Request;
import HTTPClient.Response;
import HTTPClient.RoRequest;
import java.io.IOException;

public interface HTTPClientModule
extends HTTPClientModuleConstants {
    public int requestHandler(Request var1, Response[] var2) throws IOException, ModuleException;

    public void responsePhase1Handler(Response var1, RoRequest var2) throws IOException, ModuleException;

    public int responsePhase2Handler(Response var1, Request var2) throws IOException, ModuleException;

    public void responsePhase3Handler(Response var1, RoRequest var2) throws IOException, ModuleException;

    public void trailerHandler(Response var1, RoRequest var2) throws IOException, ModuleException;
}

