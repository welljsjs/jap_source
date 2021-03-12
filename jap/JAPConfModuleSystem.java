/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.util.JAPMessages;
import gui.GUIUtils;
import gui.JAPHelpContext;
import jap.AbstractJAPConfModule;
import jap.JAPConf;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

public class JAPConfModuleSystem
implements JAPHelpContext.IHelpContext {
    private JPanel m_rootPanel;
    private JPanel m_configurationCardsPanel;
    private JTree m_configurationTree;
    private Hashtable m_registratedModules = new Hashtable();
    private Hashtable m_treeNodesToSymbolicNames = new Hashtable();
    private Hashtable m_symbolicNamesToTreeNodes = new Hashtable();
    private Hashtable m_symbolicNamesToHelpContext = new Hashtable();
    private JAPHelpContext.IHelpContext m_currentHelpContext;

    public JAPConfModuleSystem() {
        this.m_configurationCardsPanel = new JPanel(new CardLayout());
        DefaultTreeModel defaultTreeModel = new DefaultTreeModel(new DefaultMutableTreeNode("root"));
        DefaultTreeCellRenderer defaultTreeCellRenderer = new DefaultTreeCellRenderer();
        defaultTreeCellRenderer.setClosedIcon(GUIUtils.loadImageIcon("arrow.gif", true));
        defaultTreeCellRenderer.setOpenIcon(GUIUtils.loadImageIcon("arrow90.gif", true));
        defaultTreeCellRenderer.setLeafIcon(null);
        DefaultTreeSelectionModel defaultTreeSelectionModel = new DefaultTreeSelectionModel(){
            private static final long serialVersionUID = 1L;

            public void setSelectionPath(TreePath treePath) {
                String string = (String)JAPConfModuleSystem.this.m_treeNodesToSymbolicNames.get(treePath.getLastPathComponent());
                if (string != null) {
                    super.setSelectionPath(treePath);
                }
            }
        };
        defaultTreeSelectionModel.setSelectionMode(1);
        this.m_configurationTree = new JTree(defaultTreeModel);
        this.m_configurationTree.setSelectionModel(defaultTreeSelectionModel);
        this.m_configurationTree.setRootVisible(false);
        this.m_configurationTree.setEditable(false);
        this.m_configurationTree.setCellRenderer(defaultTreeCellRenderer);
        this.m_configurationTree.setBorder(new CompoundBorder(LineBorder.createBlackLineBorder(), new EmptyBorder(5, 5, 5, 5)));
        this.m_configurationTree.addTreeWillExpandListener(new TreeWillExpandListener(){

            public void treeWillCollapse(TreeExpansionEvent treeExpansionEvent) throws ExpandVetoException {
                throw new ExpandVetoException(treeExpansionEvent);
            }

            public void treeWillExpand(TreeExpansionEvent treeExpansionEvent) {
            }
        });
        this.m_configurationTree.addTreeSelectionListener(new TreeSelectionListener(){

            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                String string;
                if (treeSelectionEvent.isAddedPath() && (string = (String)JAPConfModuleSystem.this.m_treeNodesToSymbolicNames.get(treeSelectionEvent.getPath().getLastPathComponent())) != null) {
                    JAPConfModuleSystem.this.m_currentHelpContext = (JAPHelpContext.IHelpContext)JAPConfModuleSystem.this.m_symbolicNamesToHelpContext.get(string);
                    ((CardLayout)JAPConfModuleSystem.this.m_configurationCardsPanel.getLayout()).show(JAPConfModuleSystem.this.m_configurationCardsPanel, string);
                }
            }
        });
        this.m_rootPanel = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        this.m_rootPanel.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 1;
        gridBagLayout.setConstraints(this.m_configurationTree, gridBagConstraints);
        this.m_rootPanel.add(this.m_configurationTree);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 1;
        gridBagLayout.setConstraints(this.m_configurationCardsPanel, gridBagConstraints);
        this.m_rootPanel.add(this.m_configurationCardsPanel);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DefaultMutableTreeNode addConfigurationModule(DefaultMutableTreeNode defaultMutableTreeNode, AbstractJAPConfModule abstractJAPConfModule, String string) {
        DefaultMutableTreeNode defaultMutableTreeNode2 = new DefaultMutableTreeNode(abstractJAPConfModule.getTabTitle());
        JAPConfModuleSystem jAPConfModuleSystem = this;
        synchronized (jAPConfModuleSystem) {
            defaultMutableTreeNode.add(defaultMutableTreeNode2);
            this.m_configurationCardsPanel.add((Component)abstractJAPConfModule.getRootPanel(), string);
            this.m_registratedModules.put(defaultMutableTreeNode2, abstractJAPConfModule);
            this.m_treeNodesToSymbolicNames.put(defaultMutableTreeNode2, string);
            this.m_symbolicNamesToTreeNodes.put(string, defaultMutableTreeNode2);
            this.m_symbolicNamesToHelpContext.put(string, abstractJAPConfModule);
        }
        return defaultMutableTreeNode2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DefaultMutableTreeNode addComponent(DefaultMutableTreeNode defaultMutableTreeNode, Component component, String string, String string2, final String string3) {
        DefaultMutableTreeNode defaultMutableTreeNode2 = new DefaultMutableTreeNode(JAPMessages.getString(string));
        JAPConfModuleSystem jAPConfModuleSystem = this;
        synchronized (jAPConfModuleSystem) {
            defaultMutableTreeNode.add(defaultMutableTreeNode2);
            if (component != null) {
                this.m_configurationCardsPanel.add(component, string2);
                this.m_treeNodesToSymbolicNames.put(defaultMutableTreeNode2, string2);
                this.m_symbolicNamesToTreeNodes.put(string2, defaultMutableTreeNode2);
                this.m_symbolicNamesToHelpContext.put(string2, new JAPHelpContext.IHelpContext(){

                    public String getHelpContext() {
                        return string3;
                    }

                    public Component getHelpExtractionDisplayContext() {
                        return JAPConf.getInstance().getContentPane();
                    }
                });
            }
        }
        return defaultMutableTreeNode2;
    }

    public DefaultMutableTreeNode getConfigurationTreeRootNode() {
        return (DefaultMutableTreeNode)this.m_configurationTree.getModel().getRoot();
    }

    public JTree getConfigurationTree() {
        return this.m_configurationTree;
    }

    public String getHelpContext() {
        return this.m_currentHelpContext.getHelpContext();
    }

    public Component getHelpExtractionDisplayContext() {
        return JAPConf.getInstance().getContentPane();
    }

    public AbstractJAPConfModule getCurrentModule() {
        return null;
    }

    public JPanel getRootPanel() {
        return this.m_rootPanel;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void initObservers() {
        JAPConfModuleSystem jAPConfModuleSystem = this;
        synchronized (jAPConfModuleSystem) {
            Enumeration enumeration = this.m_registratedModules.elements();
            while (enumeration.hasMoreElements()) {
                ((AbstractJAPConfModule)enumeration.nextElement()).initObservers();
            }
        }
    }

    protected void revalidate() {
        this.m_configurationCardsPanel.revalidate();
        this.m_configurationTree.revalidate();
        this.m_rootPanel.revalidate();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void selectNode(String string) {
        JAPConfModuleSystem jAPConfModuleSystem = this;
        synchronized (jAPConfModuleSystem) {
            DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)this.m_symbolicNamesToTreeNodes.get(string);
            if (defaultMutableTreeNode != null) {
                this.m_configurationTree.setSelectionPath(new TreePath(defaultMutableTreeNode.getPath()));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean processOkPressedEvent() {
        boolean bl = true;
        JAPConfModuleSystem jAPConfModuleSystem = this;
        synchronized (jAPConfModuleSystem) {
            Enumeration enumeration = this.m_registratedModules.elements();
            while (enumeration.hasMoreElements()) {
                AbstractJAPConfModule abstractJAPConfModule = (AbstractJAPConfModule)enumeration.nextElement();
                if (abstractJAPConfModule.okPressed()) continue;
                bl = false;
            }
        }
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void processCancelPressedEvent() {
        JAPConfModuleSystem jAPConfModuleSystem = this;
        synchronized (jAPConfModuleSystem) {
            Enumeration enumeration = this.m_registratedModules.elements();
            while (enumeration.hasMoreElements()) {
                ((AbstractJAPConfModule)enumeration.nextElement()).cancelPressed();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void processResetToDefaultsPressedEvent() {
        JAPConfModuleSystem jAPConfModuleSystem = this;
        synchronized (jAPConfModuleSystem) {
            Enumeration enumeration = this.m_registratedModules.elements();
            while (enumeration.hasMoreElements()) {
                ((AbstractJAPConfModule)enumeration.nextElement()).resetToDefaultsPressed();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void processUpdateValuesEvent(boolean bl) {
        JAPConfModuleSystem jAPConfModuleSystem = this;
        synchronized (jAPConfModuleSystem) {
            Enumeration enumeration = this.m_registratedModules.elements();
            while (enumeration.hasMoreElements()) {
                ((AbstractJAPConfModule)enumeration.nextElement()).updateValues(bl);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void createSavePoints() {
        JAPConfModuleSystem jAPConfModuleSystem = this;
        synchronized (jAPConfModuleSystem) {
            Enumeration enumeration = this.m_registratedModules.elements();
            while (enumeration.hasMoreElements()) {
                ((AbstractJAPConfModule)enumeration.nextElement()).createSavePoint();
            }
        }
    }
}

