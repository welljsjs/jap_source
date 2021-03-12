/*
 * Decompiled with CFR 0.150.
 */
package gui;

import java.awt.Rectangle;
import javax.swing.JViewport;

public class UpperLeftStartViewport
extends JViewport {
    public void scrollRectToVisible(Rectangle rectangle) {
        rectangle.y = 0;
        super.scrollRectToVisible(rectangle);
    }
}

