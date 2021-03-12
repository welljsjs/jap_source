/*
 * Decompiled with CFR 0.150.
 */
package jap.pay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class CoinstackProgressBarUI
extends BasicProgressBarUI {
    private static final int Y_OFFSET = 6;
    private static final int X_OFFSET = 6;
    private static final int[] X_SHIFT = new int[]{0, 3, 1, -2, -1, 0, -1, 0};
    private Image m_imgCoinImage;
    private int m_yFactor;
    private int m_xPos;
    private int m_yPos;
    private int m_imageHeight;
    private int m_imageWidth;
    private int m_height;
    private int m_width;

    public CoinstackProgressBarUI(ImageIcon imageIcon, int n, int n2) {
        this.m_imgCoinImage = imageIcon.getImage();
        this.m_imageHeight = this.m_imgCoinImage.getHeight(null);
        this.m_imageWidth = this.m_imgCoinImage.getWidth(null);
        this.m_yFactor = this.m_imageHeight / 3;
        this.m_width = 12 + this.m_imageWidth + 4 + 3;
        this.m_height = 12 + this.m_imageHeight + this.m_yFactor * (n2 - n - 1);
    }

    public void paint(Graphics graphics, JComponent jComponent) {
        JProgressBar jProgressBar = (JProgressBar)jComponent;
        this.m_height = 12 + this.m_imageHeight + this.m_yFactor * (jProgressBar.getMaximum() - jProgressBar.getMinimum() - 1);
        graphics.setColor(Color.gray);
        this.m_xPos = 6;
        this.m_yPos = this.m_height - 6;
        int n = this.m_yPos - (this.m_imageHeight + this.m_yFactor * (jProgressBar.getMaximum() - jProgressBar.getMinimum() - 1));
        graphics.drawLine(this.m_xPos, this.m_yPos, this.m_xPos, n);
        int n2 = this.m_yPos - (this.m_yPos - n) / 2;
        graphics.drawLine(this.m_xPos, this.m_yPos, this.m_xPos + 3, this.m_yPos);
        graphics.drawLine(this.m_xPos, n, this.m_xPos + 3, n);
        graphics.drawLine(this.m_xPos, n2, this.m_xPos + 3, n2);
        if (jProgressBar.getValue() == jProgressBar.getMinimum()) {
            return;
        }
        int n3 = 10;
        int n4 = this.m_height - 6 - this.m_imageHeight + 1;
        for (int i = 0; i < jProgressBar.getValue() - jProgressBar.getMinimum(); ++i) {
            graphics.drawImage(this.m_imgCoinImage, n3 += X_SHIFT[i % X_SHIFT.length], n4, null);
            n4 -= this.m_yFactor;
        }
    }

    public Dimension getMinimumSize(JComponent jComponent) {
        return new Dimension(this.m_width, this.m_height);
    }

    public Dimension getPreferredSize(JComponent jComponent) {
        return new Dimension(this.m_width, this.m_height);
    }
}

