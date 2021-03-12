/*
 * Decompiled with CFR 0.150.
 */
package gui;

import anon.util.JAPMessages;
import gui.dialog.JAPDialog;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import logging.LogHolder;
import logging.LogType;

public class MapBox
extends JAPDialog
implements ChangeListener {
    private static final String MSG_ERROR_WHILE_LOADING = (class$gui$MapBox == null ? (class$gui$MapBox = MapBox.class$("gui.MapBox")) : class$gui$MapBox).getName() + "_errorLoading";
    private static final String MSG_PLEASE_WAIT = (class$gui$MapBox == null ? (class$gui$MapBox = MapBox.class$("gui.MapBox")) : class$gui$MapBox).getName() + "_pleaseWait";
    private static final String MSG_CLOSE = (class$gui$MapBox == null ? (class$gui$MapBox = MapBox.class$("gui.MapBox")) : class$gui$MapBox).getName() + "_close";
    private static final String MSG_TITLE = (class$gui$MapBox == null ? (class$gui$MapBox = MapBox.class$("gui.MapBox")) : class$gui$MapBox).getName() + "_title";
    private static final String MSG_ZOOM = (class$gui$MapBox == null ? (class$gui$MapBox = MapBox.class$("gui.MapBox")) : class$gui$MapBox).getName() + "_zoom";
    private JLabel m_lblMap;
    private JSlider m_sldZoom;
    private JButton m_btnClose;
    private String m_sImageURL;
    private static final String KEY = "ABQIAAAAvDhPn6b_F550GDisnEZpIxQrda7TSvuNFYSGo_31R-LaV_0iCRRJ7r3yduvtz_ZgBJjj2VOFap_JoQ";
    private String m_sLatitude;
    private String m_sLongitude;
    private String m_sImageSize = "550x550";
    static /* synthetic */ Class class$gui$MapBox;

    public MapBox(Component component, String string, String string2, int n) {
        super(component, "");
        this.m_sLongitude = string2;
        this.m_sLatitude = string;
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        this.getContentPane().setLayout(gridBagLayout);
        gridBagConstraints.anchor = 18;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        this.m_lblMap = new JLabel();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagLayout.setConstraints(this.m_lblMap, gridBagConstraints);
        this.getContentPane().add(this.m_lblMap);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.insets = new Insets(20, 10, 5, 10);
        JLabel jLabel = new JLabel(JAPMessages.getString(MSG_ZOOM));
        gridBagLayout.setConstraints(jLabel, gridBagConstraints);
        this.getContentPane().add(jLabel);
        this.m_sldZoom = new JSlider(1, 0, 15, n);
        this.m_sldZoom.setPaintTicks(true);
        this.m_sldZoom.setMajorTickSpacing(1);
        this.m_sldZoom.setMinorTickSpacing(1);
        this.m_sldZoom.setSnapToTicks(true);
        this.m_sldZoom.setPaintLabels(true);
        this.m_sldZoom.setRequestFocusEnabled(false);
        this.m_sldZoom.addChangeListener(this);
        gridBagConstraints.insets = new Insets(5, 10, 20, 10);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = 3;
        gridBagLayout.setConstraints(this.m_sldZoom, gridBagConstraints);
        this.getContentPane().add(this.m_sldZoom);
        this.m_btnClose = new JButton(JAPMessages.getString(MSG_CLOSE));
        this.m_btnClose.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                MapBox.this.dispose();
            }
        });
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagLayout.setConstraints(this.m_btnClose, gridBagConstraints);
        this.getContentPane().add(this.m_btnClose);
        this.refresh();
        this.pack();
        this.setResizable(false);
    }

    public void setGeo(String string, String string2) throws IOException {
        this.m_sLongitude = string2;
        this.m_sLatitude = string;
        this.refresh();
    }

    public void setVisible(boolean bl) {
        super.setVisible(bl);
    }

    public void stateChanged(ChangeEvent changeEvent) {
        JSlider jSlider = (JSlider)changeEvent.getSource();
        if (!jSlider.getValueIsAdjusting()) {
            this.refresh();
        }
    }

    private void refresh() {
        this.m_lblMap.setIcon(null);
        this.m_lblMap.setText(JAPMessages.getString(MSG_PLEASE_WAIT) + "...");
        this.m_lblMap.repaint();
        this.m_sImageURL = "http://maps.google.com/staticmap?markers=" + this.m_sLatitude + "," + this.m_sLongitude + "&zoom=" + (this.m_sldZoom.getValue() + 2) + "&size=" + this.m_sImageSize + "&key=" + KEY;
        LogHolder.log(7, LogType.MISC, "Getting map: " + this.m_sImageURL);
        String string = JAPMessages.getString(MSG_TITLE, new String[]{this.m_sLatitude, this.m_sLongitude});
        this.setTitle(string);
        try {
            ImageIcon imageIcon = new ImageIcon(new URL(this.m_sImageURL));
            if (imageIcon.getIconHeight() == -1) {
                this.dispose();
                JAPDialog.showErrorDialog(this.getParentComponent(), JAPMessages.getString(MSG_ERROR_WHILE_LOADING));
            } else {
                this.m_lblMap.setText("");
                this.m_lblMap.setIcon(imageIcon);
            }
        }
        catch (MalformedURLException malformedURLException) {
            this.dispose();
            JAPDialog.showErrorDialog(this.getParentComponent(), malformedURLException.getMessage());
        }
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private class SiteParser
    extends HTMLEditorKit.ParserCallback {
        private SiteParser() {
        }

        public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet mutableAttributeSet, int n) {
            this.handleStartTag(tag, mutableAttributeSet, n);
        }

        public void handleStartTag(HTML.Tag tag, MutableAttributeSet mutableAttributeSet, int n) {
            if (tag == HTML.Tag.IMG) {
                try {
                    if (mutableAttributeSet.getAttribute(HTML.Attribute.ID).toString().equals("map")) {
                        LogHolder.log(7, LogType.MISC, "Map image found: " + mutableAttributeSet.getAttribute(HTML.Attribute.SRC).toString());
                        MapBox.this.m_sImageURL = mutableAttributeSet.getAttribute(HTML.Attribute.SRC).toString();
                    }
                }
                catch (NullPointerException nullPointerException) {
                    // empty catch block
                }
            }
        }
    }
}

