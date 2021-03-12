/*
 * Decompiled with CFR 0.150.
 */
package gui;

import java.awt.Component;
import java.net.URL;

public final class JAPHelpContext {
    public static final String INDEX = "index";
    public static final IHelpContext INDEX_CONTEXT = JAPHelpContext.createHelpContext("index");

    public static IHelpContext createHelpContext(String string) {
        return JAPHelpContext.createHelpContext(string, null);
    }

    public static IHelpContext createHelpContext(String string, Component component) {
        final Component component2 = component;
        final String string2 = string;
        return new IHelpContext(){

            public Component getHelpExtractionDisplayContext() {
                return component2;
            }

            public String getHelpContext() {
                return string2;
            }
        };
    }

    public static interface IURLHelpContext
    extends IHelpContext {
        public String getURLMessage();

        public URL getHelpURL();

        public String getHelpContext();

        public Component getHelpExtractionDisplayContext();
    }

    public static interface IHelpContext {
        public String getHelpContext();

        public Component getHelpExtractionDisplayContext();
    }
}

