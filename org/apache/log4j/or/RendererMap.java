/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j.or;

import java.util.Hashtable;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.or.DefaultRenderer;
import org.apache.log4j.or.ObjectRenderer;
import org.apache.log4j.spi.RendererSupport;

public class RendererMap {
    Hashtable map = new Hashtable();
    static ObjectRenderer defaultRenderer = new DefaultRenderer();
    static /* synthetic */ Class class$org$apache$log4j$or$ObjectRenderer;

    public static void addRenderer(RendererSupport rendererSupport, String string, String string2) {
        LogLog.debug("Rendering class: [" + string2 + "], Rendered class: [" + string + "].");
        ObjectRenderer objectRenderer = (ObjectRenderer)OptionConverter.instantiateByClassName(string2, class$org$apache$log4j$or$ObjectRenderer == null ? (class$org$apache$log4j$or$ObjectRenderer = RendererMap.class$("org.apache.log4j.or.ObjectRenderer")) : class$org$apache$log4j$or$ObjectRenderer, null);
        if (objectRenderer == null) {
            LogLog.error("Could not instantiate renderer [" + string2 + "].");
            return;
        }
        try {
            Class class_ = Loader.loadClass(string);
            rendererSupport.setRenderer(class_, objectRenderer);
        }
        catch (ClassNotFoundException classNotFoundException) {
            LogLog.error("Could not find class [" + string + "].", classNotFoundException);
        }
    }

    public String findAndRender(Object object) {
        if (object == null) {
            return null;
        }
        return this.get(object.getClass()).doRender(object);
    }

    public ObjectRenderer get(Object object) {
        if (object == null) {
            return null;
        }
        return this.get(object.getClass());
    }

    public ObjectRenderer get(Class class_) {
        ObjectRenderer objectRenderer = null;
        for (Class class_2 = class_; class_2 != null; class_2 = class_2.getSuperclass()) {
            objectRenderer = (ObjectRenderer)this.map.get(class_2);
            if (objectRenderer != null) {
                return objectRenderer;
            }
            objectRenderer = this.searchInterfaces(class_2);
            if (objectRenderer == null) continue;
            return objectRenderer;
        }
        return defaultRenderer;
    }

    ObjectRenderer searchInterfaces(Class class_) {
        ObjectRenderer objectRenderer = (ObjectRenderer)this.map.get(class_);
        if (objectRenderer != null) {
            return objectRenderer;
        }
        Class<?>[] arrclass = class_.getInterfaces();
        for (int i = 0; i < arrclass.length; ++i) {
            objectRenderer = this.searchInterfaces(arrclass[i]);
            if (objectRenderer == null) continue;
            return objectRenderer;
        }
        return null;
    }

    public ObjectRenderer getDefaultRenderer() {
        return defaultRenderer;
    }

    public void clear() {
        this.map.clear();
    }

    public void put(Class class_, ObjectRenderer objectRenderer) {
        this.map.put(class_, objectRenderer);
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

