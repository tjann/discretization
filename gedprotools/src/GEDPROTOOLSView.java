/**
 *  GEDPROTOOLS - Gene Expression Data pre PROcessing TOOLS <p>
 *
 *  Latest release available at http://lidecc.cs.uns.edu.ar/files/gedprotools.zip <p>
 *
 *  Copyright (C) 2015 - Cristian A. Gallo <p>
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2 of the License, or (at your option)
 *  any later version. <p>
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  more details. <p>
 *
 *  You should have received a copy of the GNU General Public License along with
 *  this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 *  Place - Suite 330, Boston, MA 02111-1307, USA. <br>
 *  http://www.fsf.org/licenses/gpl.txt
 */

package GEDPROTOOLS;


import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JColorChooser;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;


import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import jp.ac.naist.dynamix.bitools.Test;


/**
 * The application's main frame.
 */
public class GEDPROTOOLSView extends FrameView {



    public GEDPROTOOLSView(SingleFrameApplication app) {
        super(app);

        String path = Test.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            decodedPath = (new File(URLDecoder.decode(path, "UTF-8"))).getParentFile().getPath();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GEDPROTOOLSView.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        initComponents();
        initMainVisualization();
        

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

        
    }

    @SuppressWarnings("empty-statement")
    private void initMainVisualization()
    {

        ImageIcon imagine;
        java.net.URL imgURL = this.getClass().getResource("resources/gpt.png");
        imagine = new ImageIcon(imgURL);

        newproyect.setIconImage(imagine.getImage());
        colorDialog.setIconImage(imagine.getImage());
        setup_discretizer.setIconImage(imagine.getImage());
        wait.setIconImage(imagine.getImage());

        generalaccess(false, false);
        jButton1.setToolTipText("Open Gene Expression Datasets in CSV format");
        newProjectMenuItem.setToolTipText("Open Gene Expression Datasets in CSV format");
        saveButton.setToolTipText("Save the current Gene Expression Dataset in CSV format");
        saveAsItemMenu.setToolTipText("Save the current Gene Expression Dataset in CSV format");
        HeatMapView.setToolTipText("Change dataset view between HeapMap and Numeric");
        HeatMapScope.setToolTipText("Change the way the HeapMap is Calculated.");
        setColorsHeatmap.setToolTipText("Change the color of the HeapMap");
        estimateMissingValues.setToolTipText("Estimates missing values in the current dataset. The missing values are represented by 999.");
        comboBoxZoom.setToolTipText("Change the zoom view of the dataset");
        log2.setToolTipText("Apply log 2 to the current dataset");
        logN.setToolTipText("Apply log N to the current dataset");
        log10.setToolTipText("Apply log 10 to the current dataset");
        zscore.setToolTipText("Apply zscore to the current dataset");
        add.setToolTipText("Add the value to the current dataset");
        mult.setToolTipText("Multiplicate the value to the current dataset");
        discretize.setToolTipText("Open a dialogbox to select the discretization approach to bin the current dataset");
        adddatasetsNewproject.setToolTipText("Add GED datasets in CSV format to preprocess with GED PRO TOOLS");
        ClearList_newproyect.setToolTipText("Clear the current list of datasets");
        hasRowNames.setToolTipText("Select it if the datasets has gene names in the first column of the matrix");
        hasTargetDescription.setToolTipText("Select it if the datasets has a target description for the gene in the second column of the matrix");
        hasColumnNames.setToolTipText("Select it if the datasets has a column name for the experiments in the first row of the matrix, or if it has the class label information in the first row of the matrix required for the Supervised discretization approaches");
        selected_discretizer.setToolTipText("Select the approach to discretize the gene expression data");
        row.setToolTipText("Select it to apply the discretization with a row data scope");
        column.setToolTipText("Select it to apply the discretization with a column data scope");
        matrix.setToolTipText("Select it to apply the discretization with a matrix data scope");
        level_of_discretization.setToolTipText("Select the level of discretization for the binning");
        percentageX.setToolTipText("Select the X% parameter for the Top %X and Max-X%Max discretization approaches");
        alpha.setToolTipText("Select the alpha parameter for the MeanPlusDevStd (a_ij +/- alpha*StdDev) and Erdal et al. (t = alpha*StdDev(Column#0)) discretization approaches");
        t.setToolTipText("Select the t threshold for the Ji and  Tan method. Default is 0.3 (30%)");
        
        


      
        
    }

    


    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = GEDPROTOOLSApp.getApplication().getMainFrame();
            aboutBox = new GEDPROTOOLSAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        GEDPROTOOLSApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        mainTabbedPanel = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        datasetsview = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        HeatMapView = new javax.swing.JToggleButton();
        HeatMapScope = new javax.swing.JComboBox();
        setColorsHeatmap = new javax.swing.JButton();
        jSeparator13 = new javax.swing.JToolBar.Separator();
        zoomJLabel = new javax.swing.JLabel();
        comboBoxZoom = new javax.swing.JComboBox();
        jToolBar3 = new javax.swing.JToolBar();
        estimateMissingValues = new javax.swing.JButton();
        jSeparator17 = new javax.swing.JToolBar.Separator();
        jSeparator16 = new javax.swing.JToolBar.Separator();
        jLabel25 = new javax.swing.JLabel();
        log2 = new javax.swing.JButton();
        logN = new javax.swing.JButton();
        log10 = new javax.swing.JButton();
        zscore = new javax.swing.JButton();
        add = new javax.swing.JButton();
        addNumber = new javax.swing.JTextField();
        mult = new javax.swing.JButton();
        multNumber = new javax.swing.JTextField();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        discretize = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        newProjectMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        saveAsItemMenu = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator15 = new javax.swing.JSeparator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        filedatasetChooser = new javax.swing.JFileChooser();
        newproyect = new javax.swing.JDialog();
        jScrollPane2 = new javax.swing.JScrollPane();
        datasetselecction = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        adddatasetsNewproject = new javax.swing.JButton();
        ClearList_newproyect = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        hasRowNames = new javax.swing.JRadioButton();
        hasTargetDescription = new javax.swing.JRadioButton();
        hasColumnNames = new javax.swing.JRadioButton();
        jPanel17 = new javax.swing.JPanel();
        acceptNewproject = new javax.swing.JButton();
        cancelNewproject = new javax.swing.JButton();
        genenameschooser = new javax.swing.JFileChooser();
        saveMenuFileChooser = new javax.swing.JFileChooser();
        openMenuFileChooser = new javax.swing.JFileChooser();
        colorDialog = new javax.swing.JDialog();
        jPanel20 = new javax.swing.JPanel();
        underexpressedColor = new javax.swing.JLabel();
        underexpressedColorPreview = new javax.swing.JLabel();
        overexpressedColor = new javax.swing.JLabel();
        overexpressedColorPreview = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        cancelColorCode = new javax.swing.JButton();
        acceptColorChooser = new javax.swing.JButton();
        setup_discretizer = new javax.swing.JDialog();
        jPanel33 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        selected_discretizer = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        row = new javax.swing.JRadioButton();
        column = new javax.swing.JRadioButton();
        matrix = new javax.swing.JRadioButton();
        level_of_discretization = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        percentageX = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        alpha = new javax.swing.JTextField();
        t = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        do_discretize = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JSeparator();
        jButton7 = new javax.swing.JButton();
        wait = new javax.swing.JDialog();
        jLabel4 = new javax.swing.JLabel();

        mainPanel.setMinimumSize(new java.awt.Dimension(1024, 500));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        mainPanel.setLayout(new java.awt.BorderLayout());

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.BorderLayout());

        mainTabbedPanel.setEnabled(false);
        mainTabbedPanel.setName("mainTabbedPanel"); // NOI18N

        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setLayout(new java.awt.BorderLayout());

        progressBar.setName("progressBar"); // NOI18N
        jPanel5.add(progressBar, java.awt.BorderLayout.PAGE_END);

        datasetsview.setMaximumSize(new java.awt.Dimension(20, 20));
        datasetsview.setName("datasetsview"); // NOI18N
        datasetsview.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                datasetsviewStateChanged(evt);
            }
        });
        jPanel5.add(datasetsview, java.awt.BorderLayout.CENTER);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridLayout(2, 0, 0, 4));

        jToolBar2.setRollover(true);
        jToolBar2.setName("Visualize Data ToolBar"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(GEDPROTOOLS.GEDPROTOOLSApp.class).getContext().getResourceMap(GEDPROTOOLSView.class);
        HeatMapView.setText(resourceMap.getString("HeatMapView.text")); // NOI18N
        HeatMapView.setFocusable(false);
        HeatMapView.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        HeatMapView.setName("HeatMapView"); // NOI18N
        HeatMapView.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        HeatMapView.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                HeatMapViewStateChanged(evt);
            }
        });
        jToolBar2.add(HeatMapView);

        HeatMapScope.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "row", "column", "matrix" }));
        HeatMapScope.setEnabled(false);
        HeatMapScope.setMaximumSize(new java.awt.Dimension(77, 21));
        HeatMapScope.setMinimumSize(new java.awt.Dimension(77, 21));
        HeatMapScope.setName("HeatMapScope"); // NOI18N
        HeatMapScope.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                HeatMapScopeMouseClicked(evt);
            }
        });
        HeatMapScope.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                HeatMapScopeItemStateChanged(evt);
            }
        });
        HeatMapScope.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HeatMapScopeActionPerformed(evt);
            }
        });
        jToolBar2.add(HeatMapScope);

        setColorsHeatmap.setText(resourceMap.getString("setColorsHeatmap.text")); // NOI18N
        setColorsHeatmap.setEnabled(false);
        setColorsHeatmap.setFocusable(false);
        setColorsHeatmap.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        setColorsHeatmap.setName("setColorsHeatmap"); // NOI18N
        setColorsHeatmap.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        setColorsHeatmap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setColorsHeatmapActionPerformed(evt);
            }
        });
        jToolBar2.add(setColorsHeatmap);

        jSeparator13.setMaximumSize(new java.awt.Dimension(10, 32767));
        jSeparator13.setMinimumSize(new java.awt.Dimension(10, 0));
        jSeparator13.setName("jSeparator13"); // NOI18N
        jSeparator13.setPreferredSize(new java.awt.Dimension(10, 0));
        jToolBar2.add(jSeparator13);

        zoomJLabel.setText(resourceMap.getString("zoomJLabel.text")); // NOI18N
        zoomJLabel.setName("zoomJLabel"); // NOI18N
        jToolBar2.add(zoomJLabel);

        comboBoxZoom.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "200%", "150%", "100%", "75%", "50%", "25%" }));
        comboBoxZoom.setSelectedIndex(2);
        comboBoxZoom.setMaximumSize(new java.awt.Dimension(77, 21));
        comboBoxZoom.setMinimumSize(new java.awt.Dimension(77, 21));
        comboBoxZoom.setName("comboBoxZoom"); // NOI18N
        comboBoxZoom.setPreferredSize(new java.awt.Dimension(77, 21));
        comboBoxZoom.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboBoxZoomItemStateChanged(evt);
            }
        });
        jToolBar2.add(comboBoxZoom);

        jPanel1.add(jToolBar2);

        jToolBar3.setRollover(true);
        jToolBar3.setName("Transform Data ToolBar"); // NOI18N

        estimateMissingValues.setText(resourceMap.getString("estimateMissingValues.text")); // NOI18N
        estimateMissingValues.setEnabled(false);
        estimateMissingValues.setFocusable(false);
        estimateMissingValues.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        estimateMissingValues.setName("estimateMissingValues"); // NOI18N
        estimateMissingValues.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        estimateMissingValues.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                estimateMissingValuesActionPerformed(evt);
            }
        });
        jToolBar3.add(estimateMissingValues);

        jSeparator17.setName("jSeparator17"); // NOI18N
        jToolBar3.add(jSeparator17);

        jSeparator16.setName("jSeparator16"); // NOI18N
        jToolBar3.add(jSeparator16);

        jLabel25.setText(resourceMap.getString("jLabel25.text")); // NOI18N
        jLabel25.setName("jLabel25"); // NOI18N
        jToolBar3.add(jLabel25);

        log2.setText(resourceMap.getString("log2.text")); // NOI18N
        log2.setEnabled(false);
        log2.setFocusable(false);
        log2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        log2.setName("log2"); // NOI18N
        log2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        log2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                log2ActionPerformed(evt);
            }
        });
        jToolBar3.add(log2);

        logN.setText(resourceMap.getString("logN.text")); // NOI18N
        logN.setEnabled(false);
        logN.setFocusable(false);
        logN.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        logN.setName("logN"); // NOI18N
        logN.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        logN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logNActionPerformed(evt);
            }
        });
        jToolBar3.add(logN);

        log10.setText(resourceMap.getString("log10.text")); // NOI18N
        log10.setEnabled(false);
        log10.setFocusable(false);
        log10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        log10.setName("log10"); // NOI18N
        log10.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        log10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                log10ActionPerformed(evt);
            }
        });
        jToolBar3.add(log10);

        zscore.setText(resourceMap.getString("zscore.text")); // NOI18N
        zscore.setEnabled(false);
        zscore.setFocusable(false);
        zscore.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zscore.setName("zscore"); // NOI18N
        zscore.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        zscore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zscoreActionPerformed(evt);
            }
        });
        jToolBar3.add(zscore);

        add.setText(resourceMap.getString("add.text")); // NOI18N
        add.setEnabled(false);
        add.setFocusable(false);
        add.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add.setName("add"); // NOI18N
        add.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActionPerformed(evt);
            }
        });
        jToolBar3.add(add);

        addNumber.setText(resourceMap.getString("addNumber.text")); // NOI18N
        addNumber.setEnabled(false);
        addNumber.setMaximumSize(new java.awt.Dimension(31, 20));
        addNumber.setMinimumSize(new java.awt.Dimension(31, 20));
        addNumber.setName("addNumber"); // NOI18N
        addNumber.setPreferredSize(new java.awt.Dimension(31, 20));
        jToolBar3.add(addNumber);

        mult.setText(resourceMap.getString("mult.text")); // NOI18N
        mult.setEnabled(false);
        mult.setFocusable(false);
        mult.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        mult.setName("mult"); // NOI18N
        mult.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mult.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multActionPerformed(evt);
            }
        });
        jToolBar3.add(mult);

        multNumber.setText(resourceMap.getString("multNumber.text")); // NOI18N
        multNumber.setEnabled(false);
        multNumber.setMaximumSize(new java.awt.Dimension(31, 20));
        multNumber.setMinimumSize(new java.awt.Dimension(31, 20));
        multNumber.setName("multNumber"); // NOI18N
        multNumber.setPreferredSize(new java.awt.Dimension(31, 20));
        jToolBar3.add(multNumber);

        jSeparator7.setName("jSeparator7"); // NOI18N
        jToolBar3.add(jSeparator7);

        jSeparator3.setName("jSeparator3"); // NOI18N
        jToolBar3.add(jSeparator3);

        discretize.setFont(resourceMap.getFont("discretize.font")); // NOI18N
        discretize.setText(resourceMap.getString("discretize.text")); // NOI18N
        discretize.setEnabled(false);
        discretize.setFocusable(false);
        discretize.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        discretize.setName("discretize"); // NOI18N
        discretize.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        discretize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discretizeActionPerformed(evt);
            }
        });
        jToolBar3.add(discretize);

        jPanel1.add(jToolBar3);

        jPanel5.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        mainTabbedPanel.addTab(resourceMap.getString("jPanel5.TabConstraints.tabTitle"), jPanel5); // NOI18N

        mainTabbedPanel.setSelectedComponent(jPanel5);

        jPanel2.add(mainTabbedPanel, java.awt.BorderLayout.CENTER);

        mainPanel.add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setLayout(new java.awt.BorderLayout());

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setRollover(true);
        jToolBar1.setName("File ToolBar"); // NOI18N

        jButton1.setIcon(resourceMap.getIcon("jButton1.icon")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setToolTipText(resourceMap.getString("jButton1.toolTipText")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setName("jButton1"); // NOI18N
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        saveButton.setIcon(resourceMap.getIcon("saveButton.icon")); // NOI18N
        saveButton.setText(resourceMap.getString("saveButton.text")); // NOI18N
        saveButton.setEnabled(false);
        saveButton.setFocusable(false);
        saveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveButton.setName("saveButton"); // NOI18N
        saveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(saveButton);

        jPanel6.add(jToolBar1, java.awt.BorderLayout.WEST);

        mainPanel.add(jPanel6, java.awt.BorderLayout.NORTH);

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        newProjectMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newProjectMenuItem.setIcon(resourceMap.getIcon("newProjectMenuItem.icon")); // NOI18N
        newProjectMenuItem.setText(resourceMap.getString("newProjectMenuItem.text")); // NOI18N
        newProjectMenuItem.setName("newProjectMenuItem"); // NOI18N
        newProjectMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newProjectMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newProjectMenuItem);

        jSeparator1.setName("jSeparator1"); // NOI18N
        fileMenu.add(jSeparator1);

        saveAsItemMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveAsItemMenu.setText(resourceMap.getString("saveAsItemMenu.text")); // NOI18N
        saveAsItemMenu.setEnabled(false);
        saveAsItemMenu.setName("saveAsItemMenu"); // NOI18N
        saveAsItemMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsItemMenuActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsItemMenu);

        jSeparator2.setName("jSeparator2"); // NOI18N
        fileMenu.add(jSeparator2);

        jSeparator15.setName("jSeparator15"); // NOI18N
        fileMenu.add(jSeparator15);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(GEDPROTOOLS.GEDPROTOOLSApp.class).getContext().getActionMap(GEDPROTOOLSView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        helpMenu.add(jMenuItem1);

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N
        statusPanel.setLayout(new java.awt.BorderLayout());

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N
        statusPanel.add(statusPanelSeparator, java.awt.BorderLayout.CENTER);

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N
        statusPanel.add(statusMessageLabel, java.awt.BorderLayout.PAGE_START);

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N
        statusPanel.add(statusAnimationLabel, java.awt.BorderLayout.PAGE_END);

        filedatasetChooser.setDialogTitle(resourceMap.getString("filedatasetChooser.dialogTitle")); // NOI18N
        filedatasetChooser.setName("filedatasetChooser"); // NOI18N

        newproyect.setTitle(resourceMap.getString("newproyect.title")); // NOI18N
        newproyect.setIconImage(null);
        newproyect.setIconImages(null);
        newproyect.setMinimumSize(new java.awt.Dimension(800, 500));
        newproyect.setModal(true);
        newproyect.setName("newproyect"); // NOI18N

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        datasetselecction.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("datasetselecction.border.title"))); // NOI18N
        datasetselecction.setName("datasetselecction"); // NOI18N
        datasetselecction.setLayout(new java.awt.GridLayout(1, 1));

        jPanel7.setMinimumSize(new java.awt.Dimension(100, 20));
        jPanel7.setName("jPanel7"); // NOI18N
        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        adddatasetsNewproject.setText(resourceMap.getString("adddatasetsNewproject.text")); // NOI18N
        adddatasetsNewproject.setMaximumSize(new java.awt.Dimension(150, 23));
        adddatasetsNewproject.setMinimumSize(new java.awt.Dimension(150, 23));
        adddatasetsNewproject.setName("adddatasetsNewproject"); // NOI18N
        adddatasetsNewproject.setPreferredSize(new java.awt.Dimension(150, 23));
        adddatasetsNewproject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adddatasetsNewprojectActionPerformed(evt);
            }
        });
        jPanel7.add(adddatasetsNewproject);
        adddatasetsNewproject.getAccessibleContext().setAccessibleName(resourceMap.getString("jButton7.AccessibleContext.accessibleName")); // NOI18N

        ClearList_newproyect.setText(resourceMap.getString("ClearList_newproyect.text")); // NOI18N
        ClearList_newproyect.setName("ClearList_newproyect"); // NOI18N
        ClearList_newproyect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClearList_newproyectActionPerformed(evt);
            }
        });
        jPanel7.add(ClearList_newproyect);

        datasetselecction.add(jPanel7);

        jScrollPane2.setViewportView(datasetselecction);

        newproyect.getContentPane().add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel8.setName("jPanel8"); // NOI18N
        jPanel8.setLayout(new java.awt.BorderLayout());

        jPanel18.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel18.border.title"))); // NOI18N
        jPanel18.setName("jPanel18"); // NOI18N
        jPanel18.setLayout(new java.awt.GridLayout(0, 1));

        hasRowNames.setText(resourceMap.getString("hasRowNames.text")); // NOI18N
        hasRowNames.setName("hasRowNames"); // NOI18N
        hasRowNames.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                hasRowNamesStateChanged(evt);
            }
        });
        jPanel18.add(hasRowNames);

        hasTargetDescription.setText(resourceMap.getString("hasTargetDescription.text")); // NOI18N
        hasTargetDescription.setEnabled(false);
        hasTargetDescription.setName("hasTargetDescription"); // NOI18N
        jPanel18.add(hasTargetDescription);

        hasColumnNames.setText(resourceMap.getString("hasColumnNames.text")); // NOI18N
        hasColumnNames.setName("hasColumnNames"); // NOI18N
        jPanel18.add(hasColumnNames);

        jPanel8.add(jPanel18, java.awt.BorderLayout.NORTH);

        jPanel17.setName("jPanel17"); // NOI18N

        acceptNewproject.setText(resourceMap.getString("acceptNewproject.text")); // NOI18N
        acceptNewproject.setName("acceptNewproject"); // NOI18N
        acceptNewproject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptNewprojectActionPerformed(evt);
            }
        });
        jPanel17.add(acceptNewproject);

        cancelNewproject.setText(resourceMap.getString("cancelNewproject.text")); // NOI18N
        cancelNewproject.setName("cancelNewproject"); // NOI18N
        cancelNewproject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelNewprojectActionPerformed(evt);
            }
        });
        jPanel17.add(cancelNewproject);

        jPanel8.add(jPanel17, java.awt.BorderLayout.EAST);

        newproyect.getContentPane().add(jPanel8, java.awt.BorderLayout.PAGE_END);

        genenameschooser.setName("genenameschooser"); // NOI18N

        saveMenuFileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        saveMenuFileChooser.setName("saveMenuFileChooser"); // NOI18N

        openMenuFileChooser.setName("openMenuFileChooser"); // NOI18N

        colorDialog.setTitle(resourceMap.getString("colorDialog.title")); // NOI18N
        colorDialog.setIconImages(null);
        colorDialog.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        colorDialog.setName("colorDialog"); // NOI18N

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel20.border.title"))); // NOI18N
        jPanel20.setName("jPanel20"); // NOI18N
        jPanel20.setLayout(new java.awt.GridLayout(2, 2));

        underexpressedColor.setText(resourceMap.getString("underexpressedColor.text")); // NOI18N
        underexpressedColor.setName("underexpressedColor"); // NOI18N
        underexpressedColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                underexpressedColorMouseClicked(evt);
            }
        });
        jPanel20.add(underexpressedColor);

        underexpressedColorPreview.setBackground(resourceMap.getColor("underexpressedColorPreview.background")); // NOI18N
        underexpressedColorPreview.setText(resourceMap.getString("underexpressedColorPreview.text")); // NOI18N
        underexpressedColorPreview.setName("underexpressedColorPreview"); // NOI18N
        underexpressedColorPreview.setOpaque(true);
        underexpressedColorPreview.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                underexpressedColorPreviewMouseClicked(evt);
            }
        });
        jPanel20.add(underexpressedColorPreview);

        overexpressedColor.setText(resourceMap.getString("overexpressedColor.text")); // NOI18N
        overexpressedColor.setName("overexpressedColor"); // NOI18N
        overexpressedColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                overexpressedColorMouseClicked(evt);
            }
        });
        jPanel20.add(overexpressedColor);

        overexpressedColorPreview.setBackground(resourceMap.getColor("overexpressedColorPreview.background")); // NOI18N
        overexpressedColorPreview.setText(resourceMap.getString("overexpressedColorPreview.text")); // NOI18N
        overexpressedColorPreview.setName("overexpressedColorPreview"); // NOI18N
        overexpressedColorPreview.setOpaque(true);
        overexpressedColorPreview.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                overexpressedColorPreviewMouseClicked(evt);
            }
        });
        jPanel20.add(overexpressedColorPreview);

        colorDialog.getContentPane().add(jPanel20, java.awt.BorderLayout.CENTER);

        jPanel19.setName("jPanel19"); // NOI18N

        cancelColorCode.setText(resourceMap.getString("cancelColorCode.text")); // NOI18N
        cancelColorCode.setName("cancelColorCode"); // NOI18N
        cancelColorCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelColorCodeActionPerformed(evt);
            }
        });
        jPanel19.add(cancelColorCode);

        acceptColorChooser.setText(resourceMap.getString("acceptColorChooser.text")); // NOI18N
        acceptColorChooser.setName("acceptColorChooser"); // NOI18N
        acceptColorChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptColorChooserActionPerformed(evt);
            }
        });
        jPanel19.add(acceptColorChooser);

        colorDialog.getContentPane().add(jPanel19, java.awt.BorderLayout.PAGE_END);

        setup_discretizer.setTitle(resourceMap.getString("setup_discretizer.title")); // NOI18N
        setup_discretizer.setAlwaysOnTop(true);
        setup_discretizer.setIconImage(null);
        setup_discretizer.setIconImages(null);
        setup_discretizer.setMinimumSize(new java.awt.Dimension(500, 350));
        setup_discretizer.setModal(true);
        setup_discretizer.setName("setup_discretizer"); // NOI18N
        setup_discretizer.setResizable(false);

        jPanel33.setName("jPanel33"); // NOI18N
        jPanel33.setLayout(null);

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        jPanel33.add(jLabel7);
        jLabel7.setBounds(20, 10, 130, 14);

        selected_discretizer.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Mean", "Median", "Max - X%Max", "Top %X", "MeanPlusEstDev", "Equal Frequency Discretization", "Equal Width Discretization", "Transitional State Discrimination", "Erdal's et al. method", "Soinov's change state", "Ji and  Tan method", "Kmeans clustering", "Gallo et al. method", "BiKmeans", "Fayyad and Irani (SUPERVISED)", "Entropy Based for 2 Classes (SUPERVISED)" }));
        selected_discretizer.setName("selected_discretizer"); // NOI18N
        selected_discretizer.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                selected_discretizerItemStateChanged(evt);
            }
        });
        jPanel33.add(selected_discretizer);
        selected_discretizer.setBounds(190, 10, 232, 20);

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        jPanel33.add(jLabel9);
        jLabel9.setBounds(20, 50, 140, 14);

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        jPanel33.add(jLabel11);
        jLabel11.setBounds(20, 90, 150, 14);

        row.setSelected(true);
        row.setText(resourceMap.getString("row.text")); // NOI18N
        row.setName("row"); // NOI18N
        row.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rowStateChanged(evt);
            }
        });
        row.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rowItemStateChanged(evt);
            }
        });
        jPanel33.add(row);
        row.setBounds(190, 50, 80, 23);

        column.setText(resourceMap.getString("column.text")); // NOI18N
        column.setName("column"); // NOI18N
        column.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                columnStateChanged(evt);
            }
        });
        column.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                columnItemStateChanged(evt);
            }
        });
        jPanel33.add(column);
        column.setBounds(280, 50, 80, 23);

        matrix.setText(resourceMap.getString("matrix.text")); // NOI18N
        matrix.setName("matrix"); // NOI18N
        matrix.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                matrixStateChanged(evt);
            }
        });
        matrix.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                matrixItemStateChanged(evt);
            }
        });
        jPanel33.add(matrix);
        matrix.setBounds(390, 50, 70, 23);

        level_of_discretization.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2" }));
        level_of_discretization.setEnabled(false);
        level_of_discretization.setName("level_of_discretization"); // NOI18N
        jPanel33.add(level_of_discretization);
        level_of_discretization.setBounds(190, 90, 56, 20);

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        jPanel33.add(jLabel2);
        jLabel2.setBounds(20, 130, 130, 14);

        percentageX.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0\t%", "1\t%", "2\t%", "3\t%", "4\t%", "5\t%", "6\t%", "7\t%", "8\t%", "9\t%", "10\t%", "11\t%", "12\t%", "13\t%", "14\t%", "15\t%", "16\t%", "17\t%", "18\t%", "19\t%", "20\t%", "21\t%", "22\t%", "23\t%", "24\t%", "25\t%", "26\t%", "27\t%", "28\t%", "29\t%", "30\t%", "31\t%", "32\t%", "33\t%", "34\t%", "35\t%", "36\t%", "37\t%", "38\t%", "39\t%", "40\t%", "41\t%", "42\t%", "43\t%", "44\t%", "45\t%", "46\t%", "47\t%", "48\t%", "49\t%", "50\t%", "51\t%", "52\t%", "53\t%", "54\t%", "55\t%", "56\t%", "57\t%", "58\t%", "59\t%", "60\t%", "61\t%", "62\t%", "63\t%", "64\t%", "65\t%", "66\t%", "67\t%", "68\t%", "69\t%", "70\t%", "71\t%", "72\t%", "73\t%", "74\t%", "75\t%", "76\t%", "77\t%", "78\t%", "79\t%", "80\t%", "81\t%", "82\t%", "83\t%", "84\t%", "85\t%", "86\t%", "87\t%", "88\t%", "89\t%", "90\t%", "91\t%", "92\t%", "93\t%", "94\t%", "95\t%", "96\t%", "97\t%", "98\t%", "99\t%", "100\t%", " " }));
        percentageX.setSelectedIndex(50);
        percentageX.setEnabled(false);
        percentageX.setName("percentageX"); // NOI18N
        jPanel33.add(percentageX);
        percentageX.setBounds(190, 130, 54, 20);

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel33.add(jLabel3);
        jLabel3.setBounds(20, 170, 130, 14);

        alpha.setText(resourceMap.getString("alpha.text")); // NOI18N
        alpha.setEnabled(false);
        alpha.setMaximumSize(new java.awt.Dimension(31, 20));
        alpha.setMinimumSize(new java.awt.Dimension(31, 20));
        alpha.setName("alpha"); // NOI18N
        alpha.setPreferredSize(new java.awt.Dimension(31, 20));
        alpha.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                alphaPropertyChange(evt);
            }
        });
        jPanel33.add(alpha);
        alpha.setBounds(190, 170, 60, 20);

        t.setText(resourceMap.getString("t.text")); // NOI18N
        t.setEnabled(false);
        t.setMaximumSize(new java.awt.Dimension(31, 20));
        t.setMinimumSize(new java.awt.Dimension(31, 20));
        t.setName("t"); // NOI18N
        t.setPreferredSize(new java.awt.Dimension(31, 20));
        jPanel33.add(t);
        t.setBounds(190, 210, 31, 20);

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        jPanel33.add(jLabel1);
        jLabel1.setBounds(20, 210, 130, 14);

        setup_discretizer.getContentPane().add(jPanel33, java.awt.BorderLayout.CENTER);

        jPanel4.setName("jPanel4"); // NOI18N

        do_discretize.setText(resourceMap.getString("do_discretize.text")); // NOI18N
        do_discretize.setName("do_discretize"); // NOI18N
        do_discretize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                do_discretizeActionPerformed(evt);
            }
        });
        jPanel4.add(do_discretize);

        jSeparator6.setName("jSeparator6"); // NOI18N
        jPanel4.add(jSeparator6);

        jButton7.setText(resourceMap.getString("jButton7.text")); // NOI18N
        jButton7.setName("jButton7"); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton7);

        setup_discretizer.getContentPane().add(jPanel4, java.awt.BorderLayout.SOUTH);

        wait.setAlwaysOnTop(true);
        wait.setIconImages(null);
        wait.setMinimumSize(new java.awt.Dimension(300, 20));
        wait.setModal(true);
        wait.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        wait.setName("wait"); // NOI18N
        wait.setResizable(false);
        wait.setUndecorated(true);

        jLabel4.setText(resourceMap.getString("wait.text")); // NOI18N
        jLabel4.setName("wait"); // NOI18N
        wait.getContentPane().add(jLabel4, java.awt.BorderLayout.CENTER);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonDeleteFileActionPerformed(java.awt.event.ActionEvent evt, JPanel J) {

        int j = 0;

        if (fileselection.length > 1)
        {

            for (int i=0; i<fileselection.length; i++)
            {
                if (fileselection[i]==J)
                    j = i;
            }

            JPanel tmp1[] = fileselection;
            File tmp2[] = files;
            fileselection = new JPanel[tmp1.length-1];
            files = new File[tmp2.length-1];

            for (int i=0; i<tmp1.length; i++)
            {
                if (i<j)
                {
                    fileselection[i] = tmp1 [i];
                    files[i] = tmp2 [i];
                }
                else if (i>j)
                {
                    fileselection[i-1] = tmp1 [i];
                    files[i-1] = tmp2 [i];
                }
            }

            GridLayout gl = (GridLayout) datasetselecction.getLayout();
            datasetselecction.remove(J);
            gl.setRows(gl.getRows()-1);

            if (up)
                    newproyect.setSize(new Dimension (newproyect.getSize().height-1, newproyect.getSize().width));
            else
                    newproyect.setSize(new Dimension (newproyect.getSize().height+1, newproyect.getSize().width));
            up=!up;

            datasetselecction.repaint();

            for (int i=0; i<fileselection.length; i++)
                fileselection[i].repaint();
        }
        else
            ClearList_newproyectActionPerformed(null);
    }


    private void adddatasetsNewprojectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adddatasetsNewprojectActionPerformed
        // TODO add your handling code here:

        filedatasetChooser.setMultiSelectionEnabled(true);
        int returnVal = filedatasetChooser.showOpenDialog(newproyect);
        int offset_panels;

        offset_panels = 0;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            
            File file[] = filedatasetChooser.getSelectedFiles();

            if (fileselection == null) {
                fileselection = new JPanel [file.length];
                files = file;
            }
            else
            {
                JPanel tmp1[] = fileselection;
                File tmp2[] = files;
                fileselection = new JPanel[file.length+tmp1.length];
                files = new File[file.length+tmp2.length];
                for (int i=0; i<file.length+tmp2.length; i++)
                {
                    if (i<tmp2.length)
                    {
                        fileselection[i] = tmp1 [i];
                        files[i] = tmp2 [i];
                    }
                    else
                    {
                        files[i] = file[i-tmp2.length];
                    }
                }
                offset_panels = tmp1.length;
            }

            for (int i=0; i<file.length; i++)
            {

                String [] op = {"Time Series", "Steady State"};                
                FlowLayout fl = new FlowLayout();
                fl.setAlignment(FlowLayout.RIGHT);
                final javax.swing.JPanel p = new javax.swing.JPanel(fl);
                JButton b = new JButton("Delete");
                b.addActionListener(new java.awt.event.ActionListener() {
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                jButtonDeleteFileActionPerformed(evt, p);
                            }
                        });


                p.add(new JLabel(file[i].getName(), javax.swing.SwingConstants.LEFT));
                JComboBox cb = new JComboBox(op);
                cb.setSelectedIndex(0);
                p.add(cb);
                p.add(b);

                fileselection[i+offset_panels] = p;

                GridLayout gl = (GridLayout) datasetselecction.getLayout();

                datasetselecction.add(p);
                gl.setRows(gl.getRows()+1);
            }
            if (up)
                newproyect.setSize(new Dimension (newproyect.getSize().height-1, newproyect.getSize().width));
            else
                newproyect.setSize(new Dimension (newproyect.getSize().height+1, newproyect.getSize().width));
            up=!up;

            
            for (int i=0; i<fileselection.length; i++)
                fileselection[i].repaint();
            datasetselecction.repaint();
        }

    }//GEN-LAST:event_adddatasetsNewprojectActionPerformed

    private void newProjectMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newProjectMenuItemActionPerformed

        if (ActualProject != null)
        {
            if (!dialogSimple("Opening new datasets will cause the loose of your unsaved progress by closing the current datasets.\n Do you want to continue anyways?", "Open new datasets..."))

                return;
        }


        HeatMapView.setSelected(false);
        generalaccess(true, false);
        opening = true;

        HeatMapScope.setSelectedIndex(0);

        datasetsview.removeAll();
        ActualProject = null;
        generalaccess(false, false);
        newproyect.setSize(800, 500);
        newproyect.setVisible(true);
    }//GEN-LAST:event_newProjectMenuItemActionPerformed

    private void ClearList_newproyectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearList_newproyectActionPerformed
        // TODO add your handling code here:
        if (fileselection != null)
        {
            GridLayout gl = (GridLayout) datasetselecction.getLayout();
            for (int i=0; i<fileselection.length; i++)
            {
                datasetselecction.remove(fileselection[i]);

                gl.setRows(gl.getRows()-1);
            }
            fileselection = null;
            files = null;
            if (up)
                newproyect.setSize(new Dimension (newproyect.getSize().height-1, newproyect.getSize().width));
            else
                newproyect.setSize(new Dimension (newproyect.getSize().height+1, newproyect.getSize().width));
            up=!up;

            datasetselecction.repaint();
            

        }


    }//GEN-LAST:event_ClearList_newproyectActionPerformed

    private void cancelNewprojectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelNewprojectActionPerformed
        //ClearList_newproyectActionPerformed(null);

        newproyect.setVisible(false);
    }//GEN-LAST:event_cancelNewprojectActionPerformed

    private void acceptNewprojectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceptNewprojectActionPerformed
        // TODO add you
        if (files==null)
        {
            dialogMessg("Please select at least one Gene Expression Data file in order to continue", "No files selected...");
            return;
        }


        HeatMapView.setSelected(false);
        generalaccess(true, false);
        opening = true;
        
        HeatMapScope.setSelectedIndex(0);
        
        String filepath [] = new String [files.length];
        boolean time[] = new boolean[files.length];
        datasetsview.removeAll();
        
       
        
        for (int i=0; i<files.length; i++)
        {
            filepath[i] = files[i].getAbsolutePath();            
            if (((JComboBox)(fileselection[i].getComponent(1))).getSelectedIndex() == 0)
                time[i] = true;
            else
                time[i] = false;
        }
        
        try {
            
            //ActualProject = new Project(filepath, genenames.getAbsolutePath(), time);
            ActualProject = new Project(filepath, time, hasRowNames.isSelected(), hasColumnNames.isSelected(), hasTargetDescription.isSelected());
            ActualProject.datasets = files;               

        } catch (FileNotFoundException ex) {
            dialogMessg("One of the dataset files or gene name file does not exits.", "Invalid dataset file or gene name file...");
            generalaccess(false, false);
            ActualProject = null;
            return;
        } catch (IOException ex) {
            dialogMessg("An Error occur reading the selected files.\n Check that the dataset files are in CSV format. Also Check if they have gene names, gene target description info and/or column names. If so please select the correct options. ", "Format Error...");
            generalaccess(false, false);
            ActualProject = null;
            return;
        } catch (NumberFormatException ex) {
            dialogMessg("An Error occur reading the selected files.\n Check that the dataset files are in CSV format. Also Check if they have gene names, gene target description info and/or column names. If so please select the correct options. ", "Format Error...");
            generalaccess(false, false);
            ActualProject = null;
            return;
        }
        catch (datasetsDiferentsSizeWithoutNamesException ex) {
            dialogMessg("An Error occur reading the selected files.\n Check that the dataset files are of the same number of genes due that they do not have row names.", "Format Error...");
            generalaccess(false, false);
            ActualProject = null;
            return;
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            dialogMessg("An Error occur reading the selected files.\n Check that the dataset files are in CSV format. Also Check if they have gene names, gene target description info and/or column names. If so please select the correct options. ", "Format Error...");
            generalaccess(false, false);
            ActualProject = null;
            return;
        }
        catch (Exception ex) {
            dialogMessg("An Error occur reading the selected files.\n Check that the dataset files are in CSV format. Also Check if they have gene names, gene target description info and/or column names. If so please select the correct option. ", "Format Error...");
            generalaccess(false, false);
            ActualProject = null;
            return;
        }

        for (int i=0; i<ActualProject.datasets.length; i++)
        {
            JvTable mainTableData = new JvTable(new TableModelDatasets(ActualProject, i));
            mainTableData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            mainTableData.setDefaultRenderer(Color.class, new HeatMapRenderer());
            javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(mainTableData);
            javax.swing.JTable rowTable = new RowGeneTable(mainTableData, ActualProject);
            rowTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            //((RowGeneTable)rowTable).updateRowHeights();
            
            //javax.swing.JScrollPane scrollPane2 = new javax.swing.JScrollPane(rowTable);;
            scrollPane.setRowHeaderView(rowTable);            
            scrollPane.setCorner(javax.swing.JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());
            if (ActualProject.timeseries[i])
                datasetsview.add(ActualProject.datasets[i].getName()+" [Time Series]", scrollPane);
            else
                datasetsview.add(ActualProject.datasets[i].getName()+" [Steady State]", scrollPane);

        }
        newproyect.setVisible(false);
       

   

     
        opening = false;

    }//GEN-LAST:event_acceptNewprojectActionPerformed


       private void generalaccess(boolean t, boolean t2)
       {
            
           
            mainTabbedPanel.setEnabled(t);
            discretize.setEnabled(t);
            
            saveAsItemMenu.setEnabled(t);
            
            saveButton.setEnabled(t);
            HeatMapView.setEnabled(t);
            comboBoxZoom.setEnabled(t);
            setColorsHeatmap.setEnabled(HeatMapView.isSelected());
            HeatMapScope.setEnabled(HeatMapView.isSelected());
            zoomJLabel.setEnabled(t);
            estimateMissingValues.setEnabled(t);
            log2.setEnabled(t);
            logN.setEnabled(t);
            log10.setEnabled(t);
            zscore.setEnabled(t);
            add.setEnabled(t);
            mult.setEnabled(t);
            addNumber.setEnabled(t);
            multNumber.setEnabled(t);


            
   
          
       }
   


    private void dialogMessg(String Message, String title)
    {
        final JOptionPane optionPane = new JOptionPane(
            Message,
            JOptionPane.ERROR_MESSAGE,
            JOptionPane.DEFAULT_OPTION);
            final JDialog dialog = new JDialog(this.getFrame(),
                             title,
                             true);
            dialog.setContentPane(optionPane);
            dialog.setDefaultCloseOperation(
                JDialog.DO_NOTHING_ON_CLOSE);
            dialog.setAlwaysOnTop(true);


            optionPane.addPropertyChangeListener(
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent e) {
                        String prop = e.getPropertyName();

                        if (dialog.isVisible()
                         && (e.getSource() == optionPane)
                         && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
                            //If you were going to check something
                            //before closing the window, you'd do
                            //it here.
                            dialog.setVisible(false);
                        }
                    }
                });

            dialog.pack();
            dialog.setLocation(300, 300);
            dialog.setVisible(true);


            dialog.setVisible(false);


    }

    private boolean dialogSimple(String Message, String title)
    {
        final JOptionPane optionPane = new JOptionPane(
            Message,
            JOptionPane.QUESTION_MESSAGE,
            JOptionPane.YES_NO_OPTION);
            final JDialog dialog = new JDialog(this.getFrame(),
                             title,
                             true);
            dialog.setContentPane(optionPane);
            dialog.setDefaultCloseOperation(
                JDialog.DO_NOTHING_ON_CLOSE);

            dialog.setAlwaysOnTop(true);

            optionPane.addPropertyChangeListener(
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent e) {
                        String prop = e.getPropertyName();

                        if (dialog.isVisible()
                         && (e.getSource() == optionPane)
                         && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
                            //If you were going to check something
                            //before closing the window, you'd do
                            //it here.
                            dialog.setVisible(false);
                        }
                    }
                });

            dialog.pack();
            dialog.setLocation(300, 300);
            dialog.setVisible(true);


            if (optionPane.getValue() != null && ((Integer)optionPane.getValue()).intValue() == JOptionPane.OK_OPTION)
            {
                dialog.setVisible(false);
                return true;
            }
            dialog.setVisible(false);
            return false;

    }

    private void saveAsItemMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsItemMenuActionPerformed
        // TODO add your handling code here:
        File f;
        int returnVal;
       
        saveMenuFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        saveMenuFileChooser.setApproveButtonText("Save");
        saveMenuFileChooser.setMultiSelectionEnabled(false);
        saveMenuFileChooser.setDialogTitle("Save the Gene Expression Dataset as...");
        
        returnVal = saveMenuFileChooser.showOpenDialog(saveAsItemMenu);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
           
            
            f = saveMenuFileChooser.getSelectedFile();


            //guarda al dataset actual
            int d = datasetsview.getSelectedIndex();
            if (d!=-1)
            {
                try {
                    BufferedWriter salida = new BufferedWriter(new FileWriter(f));


                     salida.write(", ");
                     if (ActualProject.hastargetdescription)
                     {
                         salida.write(", ");
                     }
                     for (int j=0; j<ActualProject.datamatrix[d][0].length; j++)
                     {
                         salida.write(ActualProject.D_columnas[d].get(j)+", ");
                     }
                     salida.write("\n");
                     for (int i=0; i<ActualProject.filas; i++)
                     {
                         for (int j=0; j<ActualProject.datamatrix[d][0].length; j++)
                         {
                             if (j==0)
                             {
                                 salida.write(ActualProject.genenames[i]+", ");
                                 if (ActualProject.hastargetdescription)
                                     salida.write(ActualProject.targetdescription[i]+", ");
                             }
                             salida.write(ActualProject.datamatrix[d][i][j] + ", ");
                         }
                         salida.write("\n");

                     }
                     salida.close();
                }
                catch (Exception ex) {
                    dialogMessg("An Error occur saving the file.", "Saving Error...");
                }
            }

            /* guarda a todos los datasets juntos
            if (ActualProject.datasets.length==1){

                try {
                    BufferedWriter salida = new BufferedWriter(new FileWriter(f));

                     
                     salida.write(", ");
                     if (ActualProject.hastargetdescription)
                     {                         
                         salida.write(", ");
                     }
                     for (int j=0; j<ActualProject.datamatrix[0][0].length; j++)
                     {
                         salida.write(ActualProject.D_columnas[0].get(j)+", ");
                     }
                     salida.write("\n");
                     for (int i=0; i<ActualProject.filas; i++)
                     {
                         for (int j=0; j<ActualProject.datamatrix[0][0].length; j++)
                         {
                             if (j==0)
                             {
                                 salida.write(ActualProject.genenames[i]+", ");
                                 if (ActualProject.hastargetdescription)
                                     salida.write(ActualProject.targetdescription[i]+", ");
                             }
                             salida.write(ActualProject.datamatrix[0][i][j] + ", ");
                         }
                         salida.write("\n");

                     }
                     salida.close();
                }
                catch (Exception ex) {
                    dialogMessg("An Error occur saving the file.", "Saving Error...");
                }
            }
            else
            {
                
                File fk = null;
                for (int k=0; k<ActualProject.datasets.length; k++)
                {
                     fk = new File(f.getAbsolutePath()+ActualProject.datasets[k].getName());
                     try {
                        BufferedWriter salida = new BufferedWriter(new FileWriter(fk));


                         salida.write(", ");
                         if (ActualProject.hastargetdescription)
                         {
                             salida.write(", ");
                         }
                         for (int j=0; j<ActualProject.datamatrix[k][0].length; j++)
                         {
                             salida.write(ActualProject.D_columnas[k].get(j)+", ");
                         }
                         salida.write("\n");
                         for (int i=0; i<ActualProject.filas; i++)
                         {
                             for (int j=0; j<ActualProject.datamatrix[k][0].length; j++)
                             {
                                 if (j==0)
                                 {
                                     salida.write(ActualProject.genenames[i]+", ");
                                     if (ActualProject.hastargetdescription)
                                         salida.write(ActualProject.targetdescription[i]+", ");
                                 }
                                 salida.write(ActualProject.datamatrix[k][i][j] + ", ");
                             }
                             salida.write("\n");

                         }
                         salida.close();
                    }
                    catch (Exception ex) {
                        dialogMessg("An Error occur saving the file.", "Saving Error...");
                    }
                }

            }
            */
        }
    }//GEN-LAST:event_saveAsItemMenuActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        saveAsItemMenuActionPerformed(evt);
    }//GEN-LAST:event_saveButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        newProjectMenuItemActionPerformed(evt);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void HeatMapViewStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_HeatMapViewStateChanged
        if (ActualProject != null)
        {
            ActualProject.heatMapOn = HeatMapView.isSelected();

            ActualProject.heatMapScope = HeatMapScope.getSelectedIndex();
            datasetsview.repaint();
            setColorsHeatmap.setEnabled(HeatMapView.isSelected());
            HeatMapScope.setEnabled(HeatMapView.isSelected());
            estimateMissingValues.setEnabled(!HeatMapView.isSelected());
        }
    }//GEN-LAST:event_HeatMapViewStateChanged


    private boolean opening = false;

    private void comboBoxZoomItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboBoxZoomItemStateChanged
        if (datasetsview.getTabCount()>0 && !opening)
        {
            float factor = 1;
            switch (comboBoxZoom.getSelectedIndex())
            {
                case 0:{
                        factor = 2;
                        break;
                }
                case 1:{
                        factor = 1.5f;
                        break;
                }
                case 2:{
                        factor = 1;
                        break;
                }
                case 3:{
                        factor = 0.75f;
                        break;
                }
                case 4:{
                        factor = 0.5f;
                        break;
                }
                case 5:{
                        factor = 0.25f;
                        break;
                }
                case 6:{
                        factor = 0.10f;
                        break;
                }
            }            
            ((JvTable) ((javax.swing.JScrollPane)datasetsview.getComponentAt(datasetsview.getSelectedIndex())).getViewport().getComponent(0)).setZoom(factor);
            ((JvTable) ((javax.swing.JScrollPane)datasetsview.getComponentAt(datasetsview.getSelectedIndex())).getRowHeader().getComponent(0)).setZoom(factor);
            
        }
    }//GEN-LAST:event_comboBoxZoomItemStateChanged

    private void datasetsviewStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_datasetsviewStateChanged
        comboBoxZoomItemStateChanged(null);
    }//GEN-LAST:event_datasetsviewStateChanged

    private void setColorsHeatmapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setColorsHeatmapActionPerformed
        colorDialog.setBounds(100, 100, 320, 240);
        colorDialog.setResizable(false);
        
        overexpressedColorPreview.setOpaque(true);
        underexpressedColorPreview.setOpaque(true);
        overexpressedColorPreview.setBackground(ActualProject.colours.dup(ActualProject.colours.heatMapAboveAverage));
        underexpressedColorPreview.setBackground(ActualProject.colours.dup(ActualProject.colours.heatMapBelowAverage));
        overexpressedColorPreview.repaint();
        underexpressedColorPreview.repaint();        

        colorDialog.setVisible(true);
        
        
        
        
    }//GEN-LAST:event_setColorsHeatmapActionPerformed

    private void cancelColorCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelColorCodeActionPerformed
        colorDialog.setVisible(false);
    }//GEN-LAST:event_cancelColorCodeActionPerformed

    private void acceptColorChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceptColorChooserActionPerformed
        ActualProject.colours.heatMapAboveAverage = overexpressedColorPreview.getBackground();
        ActualProject.colours.heatMapBelowAverage = underexpressedColorPreview.getBackground();
        for (int i=0; i<ActualProject.numberofdatasets; i++)
            datasetsview.getComponentAt(i).repaint();
        colorDialog.setVisible(false);
    }//GEN-LAST:event_acceptColorChooserActionPerformed

    private void underexpressedColorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_underexpressedColorMouseClicked
        Color c;
        underexpressedColorPreview.setBackground(((c=JColorChooser.showDialog(underexpressedColor, "Choose a color ...", underexpressedColorPreview.getBackground()))==null) ?  underexpressedColorPreview.getBackground() : c );
    }//GEN-LAST:event_underexpressedColorMouseClicked

    private void overexpressedColorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_overexpressedColorMouseClicked
        Color c;
        overexpressedColorPreview.setBackground(((c=JColorChooser.showDialog(overexpressedColor, "Choose a color ...", overexpressedColorPreview.getBackground()))==null) ?  overexpressedColorPreview.getBackground() : c );
    }//GEN-LAST:event_overexpressedColorMouseClicked

    private void underexpressedColorPreviewMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_underexpressedColorPreviewMouseClicked
        Color c;
        underexpressedColorPreview.setBackground(((c=JColorChooser.showDialog(underexpressedColor, "Choose a color ...", underexpressedColorPreview.getBackground()))==null) ?  underexpressedColorPreview.getBackground() : c );
    }//GEN-LAST:event_underexpressedColorPreviewMouseClicked

    private void overexpressedColorPreviewMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_overexpressedColorPreviewMouseClicked
        Color c;
        overexpressedColorPreview.setBackground(((c=JColorChooser.showDialog(overexpressedColor, "Choose a color ...", overexpressedColorPreview.getBackground()))==null) ?  overexpressedColorPreview.getBackground() : c );
    }//GEN-LAST:event_overexpressedColorPreviewMouseClicked

    private void estimateMissingValuesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_estimateMissingValuesActionPerformed
        /*for (int k=0; k<ActualProject.numberofdatasets; k++)
        {
            jp.ac.naist.dynamix.bitools.ExpressionMatrix em = new jp.ac.naist.dynamix.bitools.ExpressionMatrix(toDoubleMatrix(ActualProject.datamatrix[k]));
            jp.ac.naist.dynamix.mpca.BPCAFill BPCA = new jp.ac.naist.dynamix.mpca.BPCAFill(em);
            while (!BPCA.isFinished())
                BPCA.doStep();
            ActualProject.datamatrix[k] = toFloatMatrix(BPCA.getMatrixResult());
            datasetsview.getComponentAt(k).repaint();
        }*/
        showwait();
        int k = datasetsview.getSelectedIndex();
        if (k!=-1)
        {
            jp.ac.naist.dynamix.bitools.ExpressionMatrix em = new jp.ac.naist.dynamix.bitools.ExpressionMatrix(toDoubleMatrix(ActualProject.datamatrix[k]));
            jp.ac.naist.dynamix.mpca.BPCAFill BPCA = new jp.ac.naist.dynamix.mpca.BPCAFill(em);
            while (!BPCA.isFinished())
            BPCA.doStep();
            ActualProject.datamatrix[k] = toFloatMatrix(BPCA.getMatrixResult());
            datasetsview.getComponentAt(k).repaint();
        }

        hidewait();

    }//GEN-LAST:event_estimateMissingValuesActionPerformed

    

  
    private boolean hasNegativeValue(int dataset)
    {
        for (int i=0; i<ActualProject.datamatrix[dataset].length; i++)
            for (int j=0; j<ActualProject.datamatrix[dataset][0].length; j++)
                if (ActualProject.datamatrix[dataset][i][j]<=0)
                    return true;
        return false;
    }

    private void log2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_log2ActionPerformed
        boolean hasNeg = false;


        int d = datasetsview.getSelectedIndex();
        if (hasNegativeValue(d))
                hasNeg  = true;
        else
            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length; j++)
                    if (ActualProject.datamatrix[d][i][j]!=999)
                        ActualProject.datamatrix[d][i][j] = (float) (Math.log10(ActualProject.datamatrix[d][i][j])/Math.log10(2));
        
        if (hasNeg)
            dialogMessg("The current dataset contains negative or 0 values. The log was not applied", "Negative values in dataset...");
        else
        {
            ActualProject.updateStat();
            datasetsview.getComponentAt(d).repaint();
        }
        

    }//GEN-LAST:event_log2ActionPerformed

    private void logNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logNActionPerformed
        boolean hasNeg = false;


        int d = datasetsview.getSelectedIndex();
        if (hasNegativeValue(d))
                hasNeg  = true;
        else
            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length; j++)
                    if (ActualProject.datamatrix[d][i][j]!=999)
                        ActualProject.datamatrix[d][i][j] = (float) Math.log(ActualProject.datamatrix[d][i][j]);

        if (hasNeg)
            dialogMessg("The current dataset contains negative or 0 values. The log was not applied", "Negative values in dataset...");
        else
        {
            ActualProject.updateStat();
            datasetsview.getComponentAt(d).repaint();
        }
    }//GEN-LAST:event_logNActionPerformed

    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
        try {
            float a = Float.parseFloat(addNumber.getText());
            int d = datasetsview.getSelectedIndex();
            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length; j++)
                    if (ActualProject.datamatrix[d][i][j]!=999)
                        ActualProject.datamatrix[d][i][j] = (ActualProject.datamatrix[d][i][j]) + a;

            ActualProject.updateStat();
            datasetsview.getComponentAt(d).repaint();

        }
        catch (NumberFormatException e)
        {
            dialogMessg("There is an error in the format of the number to add to the dataset", "Format number error...");
        }


    }//GEN-LAST:event_addActionPerformed

    private void multActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multActionPerformed
         try {

            float a = Float.parseFloat(multNumber.getText());
            int d = datasetsview.getSelectedIndex();
            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length; j++)
                    if (ActualProject.datamatrix[d][i][j]!=999)
                        ActualProject.datamatrix[d][i][j] = (ActualProject.datamatrix[d][i][j]) * a;

            ActualProject.updateStat();
            datasetsview.getComponentAt(d).repaint();
            hidewait();
        }
        catch (NumberFormatException e)
        {
            dialogMessg("There is an error in the format of the number to mult with the dataset", "Format number error...");

        }
    }//GEN-LAST:event_multActionPerformed

    private void hasRowNamesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_hasRowNamesStateChanged
        if (hasRowNames.isSelected())
            hasTargetDescription.setEnabled(true);
        else
        {
            hasTargetDescription.setSelected(false);
            hasTargetDescription.setEnabled(false);
        }

    }//GEN-LAST:event_hasRowNamesStateChanged

    
    
    boolean numberRuleContinue = true;
    private void log10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_log10ActionPerformed
        // TODO add your handling code here:



        boolean hasNeg = false;


        int d = datasetsview.getSelectedIndex();
        if (hasNegativeValue(d))
                hasNeg  = true;
        else
            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length; j++)
                    if (ActualProject.datamatrix[d][i][j]!=999)
                        ActualProject.datamatrix[d][i][j] = (float) Math.log10(ActualProject.datamatrix[d][i][j]);

        if (hasNeg)
            dialogMessg("The current dataset contains negative or 0 values. The log was not applied", "Negative values in dataset...");
        else
        {
            ActualProject.updateStat();
            datasetsview.getComponentAt(d).repaint();
        }



    }//GEN-LAST:event_log10ActionPerformed

    private void zscoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zscoreActionPerformed
        // TODO add your handling code here:
        
        showwait();

        int d = datasetsview.getSelectedIndex();
        
        for (int i=0; i<ActualProject.datamatrix[d].length; i++)
            for (int j=0; j<ActualProject.datamatrix[d][0].length; j++)
                if (ActualProject.datamatrix[d][i][j]!=999)
                    ActualProject.datamatrix[d][i][j] = (float) (ActualProject.datamatrix[d][i][j] - ActualProject.dataMatrixStat[d][i][2])/ActualProject.dataMatrixStat[d][i][3];

        
        ActualProject.updateStat();
        datasetsview.getComponentAt(d).repaint();

        hidewait();
        
    }//GEN-LAST:event_zscoreActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        setup_discretizer.setVisible(false);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void showwait()
    { /*
        wait.setSize(400, 100);
        wait.setLocation(400, 400);
        wait.setVisible(true);
       *
       * */


       this.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    private void hidewait()
    {
        /*
        wait.setVisible(false);
         * */
         this.getComponent().setCursor(Cursor.getDefaultCursor());
    }

    private void do_discretizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_do_discretizeActionPerformed


        int d = datasetsview.getSelectedIndex();

        try {
            float i = Float.parseFloat(alpha.getText());
        }
        catch (Exception e)
        {
             dialogMessg("The value of the alpha parameter must be a real value >= 0", "Error in the alpha parameter of the method MeanPlusStdDev...");
             return;
        }

        try {
            float j = Float.parseFloat(t.getText());
        }
        catch (Exception e)
        {
             dialogMessg("The value of the t threshold must be a real value >= 0", "Error in the t threshold of the Ji and Tan method...");
             return;
        }

        try {
            if (selected_discretizer.getSelectedIndex() == 14 || selected_discretizer.getSelectedIndex() == 15)
                for (int j=0; j<ActualProject.D_columnas[d].size(); j++)
                    Integer.parseInt(ActualProject.D_columnas[d].get(j));
        }
        catch (Exception e)
        {
             dialogMessg("The class label information for the supervised discretization approach must be integer values. Correct the names of the columns in the current dataset to perform a supervised discretization.", "Error in the class laber information for supervised discretization...");
             return;
        }

        float i = Float.parseFloat(alpha.getText());
        float j = Float.parseFloat(t.getText());
        if (i<0)
        {
            dialogMessg("The value of the alpha parameter must be a real value >= 0", "Error in the alpha parameter of the method MeanPlusStdDev...");
            return;
        }
        if (j<0)
        {
            dialogMessg("The value of the t threshold must be a real value >= 0", "Error in the t threshold of the Ji and Tan method...");
            return;
        }

        

        if (row.isSelected() && ActualProject.datamatrix[d][0].length <2 )
        {
             dialogMessg("The current dataset has less than 2 columns. Can't apply a discretization method with a data scope of rows", "Number of columns in the data...");
             return;
        }
        if (column.isSelected() && ActualProject.datamatrix[d].length <2 )
        {
             dialogMessg("The current dataset has less than 2 rows. Can't apply a discretization method with a data scope of columns", "Number of rows in the data...");
             return;
        }

        if (matrix.isSelected() && ActualProject.datamatrix[d].length*ActualProject.datamatrix[d][0].length <2 )
        {
             dialogMessg("The current dataset has less than 2 values. Can't apply a discretization method with a data scope of matrix", "Number of values in the data...");
             return;
        }

        if(selected_discretizer.getSelectedIndex()>=7 && selected_discretizer.getSelectedIndex()<=10 && !ActualProject.timeseries[d])
        {
            dialogMessg("You have selected a discretizer of time series data whereas the current dataset is not a time series", "Not a time series...");
            return;
        }
            
        

        setup_discretizer.setVisible(false);
        showwait();
        switch (selected_discretizer.getSelectedIndex())
        {
               case 0:{ //Mean
                        mean(d);
                        break;
               }
               case 1:{ //Median
                        median(d);
                        break;
               }
               case 2:{ //Max - X%Max
                        MaxXMax(d);
                        break;
               }
               case 3:{ //Top %X
                        TopX(d);
                        break;
               }
               case 4:{ //MeanPlusSstDev
                        meanPlusStdDev(d);
                        break;
               }               
               case 5:{ //EFD
                        EFD(d);
                        break;
               } 
               case 6:{ //EWD
                        EWD(d);
                        break;
               } 
               case 7:{ //TSD
                        TSD(d);
                        break;
               }
               case 8:{ //Erdal's et al. method
                        Erdal(d);
                        break;
               }
               case 9:{ //Soinov's change state
                        Soinov(d);
                        break;
               }
               case 10:{ //Ji and  Tan method
                        JiTan(d);
                        break;
               }
               case 11:{ //k means
                        kmeans(d);
                        break;
               }
               case 12:{ //Gallo et al. method
                        Gallo(d);
                        break;
               }
               case 13:{ //BiKmeans
                        BiKmeans(d);
                        break;
               }
               case 14:{ //Fayyad and Irani
                        FayyadIrani(d);
                        break;
               }
               case 15:{ //Entropy Based Dicretization for 2 classes
                        EntropyBased(d);
                        break;
               }

                
                
        }
        hidewait();

    }//GEN-LAST:event_do_discretizeActionPerformed

    private void EntropyBased(int d)
    {
        HashSet <Integer> S = new HashSet<Integer>();

        int min=Integer.MAX_VALUE;
        int max=Integer.MIN_VALUE;
        
        for (int j=0; j<ActualProject.datamatrix[d][0].length; j++)
        {
            int val = Integer.parseInt(ActualProject.D_columnas[d].get(j));
            S.add(val);
            if (val<min)
                min = val;

            if (val > max)
                max = val;
        }

        if  (S.size() !=2 )
        {
            dialogMessg("The number of classes must be 2 in order to apply this approach. The discretization was not applied.", "Error in the Entropy Based discretizatino approach..");
            return;

        }

        int classes[] = new int [ActualProject.datamatrix[d][0].length];

        for (int j=0; j<classes.length; j++)
        {
            int val = Integer.parseInt(ActualProject.D_columnas[d].get(j));
            if (val == min)
                classes[j] = 0;
            else if (val == max)
                classes[j] = 1;
        }

        EntropyBasedDicretizerOptimal2Clases  EBD2 = new EntropyBasedDicretizerOptimal2Clases();
        EBD2.load(ActualProject.datamatrix[d], classes);

        for (int i=0; i<ActualProject.datamatrix[d].length; i++)
        {
            for (int j=0; j<ActualProject.datamatrix[d][0].length; j++)
                ActualProject.datamatrix[d][i][j] = EBD2.discretize(i, j);
        }


        ActualProject.updateStat();
        HeatMapScope.setSelectedIndex(0);
        datasetsview.getComponentAt(d).repaint();

    }


    private void FayyadIrani(int d)
    {
        try{
        for (int i=0; i<ActualProject.datamatrix[d].length; i++)
        {
            EntropyDiscretizer ED = new EntropyDiscretizer();
            Case [] cases = new Case[ActualProject.datamatrix[d][0].length];
            for (int j=0; j<ActualProject.datamatrix[d][0].length; j++)
                cases[j] = new Case(Integer.parseInt(ActualProject.D_columnas[d].get(j)), ActualProject.datamatrix[d][i][j]);

            ED.buildDiscretizer(cases);

            for (int j=0; j<ActualProject.datamatrix[d][0].length; j++)
                ActualProject.datamatrix[d][i][j] = ED.discretize(ActualProject.datamatrix[d][i][j]);
        }

        ActualProject.updateStat();
        HeatMapScope.setSelectedIndex(0);
        datasetsview.getComponentAt(d).repaint();

        }
        catch (Exception e)
        {
            hidewait();
            dialogMessg(e.getMessage(), "Error in the Fayyad and Irani discretizatino approach");

        }

    }


    private void BiKmeans(int d)
    {
        int k = level_of_discretization.getSelectedIndex()+2;
        float [][] kmeansrows = new float [ActualProject.datamatrix[d].length][ActualProject.datamatrix[d][0].length]; //matrix with same dim as data
        float [][] kmeanscolumns = new float [ActualProject.datamatrix[d].length][ActualProject.datamatrix[d][0].length]; //another matrix with same dim as data

        for (int i=0; i<ActualProject.datamatrix[d].length; i++) //for as many rows
            kmeansrows[i] = kmeans_1d(ActualProject.datamatrix[d][i], k+1); //fills in all the rows of kmeans row with kmeans_1d results

        float [][] tmp = copydatasetsTrans(ActualProject.datamatrix[d]); //copy array of original data
        for (int i=0; i<tmp.length; i++) //for as many rows
            tmp[i] = kmeans_1d(tmp[i], k+1); //assign to the rows of the copy kmeans(copy[row], k=discr. levels)

        tmp = copydatasetsTrans(tmp);
        kmeanscolumns = tmp; //kmeanscolumns matrix becomes this weird thing

        for (int i=0; i<ActualProject.datamatrix[d].length; i++) //for as many rows
            for (int j=0; j<ActualProject.datamatrix[d][0].length; j++) //for as many columns
            {
                int k_d=1;
                while ((k_d)*(k_d)<=(kmeansrows[i][j]+1)*(kmeanscolumns[i][j]+1)) //find k_d = (kmrowval ij + 1)(kmcol=weird thing ij + 1)
                    k_d++;

                if ((kmeansrows[i][j]+1) == (k+1) && (kmeanscolumns[i][j]+1)== (k+1))
                    ActualProject.datamatrix[d][i][j] = k_d-2;
                else
                    ActualProject.datamatrix[d][i][j] = k_d-1;
            }
            
        ActualProject.updateStat();
        HeatMapScope.setSelectedIndex(0);
        datasetsview.getComponentAt(d).repaint();

    }

    private void kmeans(int d)
    {
        int k = level_of_discretization.getSelectedIndex()+2;
        if (row.isSelected())
        {
            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                ActualProject.datamatrix[d][i] = kmeans_1d(ActualProject.datamatrix[d][i], k);

            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(0);
        }
        else if (column.isSelected())
        {
            float [][] tmp = copydatasetsTrans(ActualProject.datamatrix[d]);
            for (int i=0; i<tmp.length; i++)
                tmp[i] = kmeans_1d(tmp[i], k);

            tmp = copydatasetsTrans(tmp);
            ActualProject.datamatrix[d] = tmp;

            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(1);
        }
        else if (matrix.isSelected())
        {
            float [] tmp = copydatasetsAlong(ActualProject.datamatrix[d]);

            tmp = kmeans_1d(tmp, k);
            ActualProject.datamatrix[d] = copydatasetsAlongTrans(tmp, ActualProject.datamatrix[d].length, ActualProject.datamatrix[d][0].length);
           
            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(2);
        }
        
              

        datasetsview.getComponentAt(d).repaint();
            
    }



    private float [] kmeans_1d(float[] data, int k)
    {


        float [] orddata = new float[data.length];
        int   [] originalindex = new int[data.length];
        int   [] clusterid = new int [data.length];
        float [] centroids = new float [k];


        for (int i=0; i<data.length; i++)
        {
            orddata[i] = data[i];
            originalindex[i] = i;
            clusterid[i] = 0;
        }

        //sort
        float tmp;
        int tmpI;
        for (int i=0; i<orddata.length; i++)
            for (int j=i+1; j<orddata.length; j++)
            {
                if (orddata[j]<orddata[i])
                {
                    tmp = orddata[i];
                    orddata[i] = orddata[j];
                    orddata[j] = tmp;

                    tmpI = originalindex[i];
                    originalindex[i] = originalindex[j];
                    originalindex[j] = tmpI;
                }
            }


        //calculate initial centroids
        float step = (float)Math.abs(orddata[orddata.length-1]-orddata[0])/(float)k; //step = half the diff of first and last time values of node
        float centroid = orddata[0] + step/2; //first time point, half the step distance
        for (int i=0; i<k; i++) //for the two points
        {
            centroids[i] = centroid; //assign the first element of centroids to be at first, "first time point, half the step distance"
            centroid+=step;          //centroid increase by step, so the next one would be just a step ahead
        }

        // main loop
        // assing points to centroids
        // recalculate centroids
        // until nothing changes

        boolean change;
        do
        {
            change = false;

            //assign points to centroids
            for (int i=0; i<orddata.length; i++)
            {
                int cluster = 0;
                float min_d = Float.MAX_VALUE;
                for (int j=0; j<k; j++)
                    if (Math.abs(orddata[i]-centroids[j])<min_d)
                    {
                        min_d = Math.abs(orddata[i]-centroids[j]);
                        cluster = j;
                    }
                if (clusterid[i] != cluster)
                {
                   clusterid[i] = cluster;
                   change = true;
                }
            }

            //recalculate centroids
            int samplepercentroid[] = new int[k];
            for (int j=0; j<k; j++)
            {
                centroids[j] = 0;
                samplepercentroid[j] = 0;
            }

            for (int i=0; i<orddata.length; i++)
            {
                centroids[clusterid[i]] += orddata[i];
                samplepercentroid[clusterid[i]]++;
            }

            for (int j=0; j<k; j++)
                centroids[j] /= samplepercentroid[j];

        }
        while (change);

        float result[] = new float[orddata.length];

        for (int i=0; i<orddata.length; i++)
            result[originalindex[i]] = clusterid[i];


        return result;
    }



    private void Gallo(int d)
    {
            int sample,gene;
            float m1, m2, obj, var1, var2, prom1, prom2;
            int pos;
            int filas = ActualProject.datamatrix[d].length;
            int NumOfSamples = ActualProject.datamatrix[d][0].length;
            float [] MEDG = new float [filas];
            float [][] OrdGEDMDiscrete = copydatasets (ActualProject.datamatrix[d]);

            for (int i=0; i<OrdGEDMDiscrete.length; i++)
                Arrays.sort(OrdGEDMDiscrete[i]);


            for (gene=0; gene<filas; gene++)
            {

                if (NumOfSamples>2)
                {
                     m1=OrdGEDMDiscrete[gene][0];
                     m2=0;
                     pos=0;
                     for (sample=1; sample<NumOfSamples; sample++)
                     {
                       m2=m2 + OrdGEDMDiscrete[gene][sample];
                     }
                     prom1 = m1;
                     prom2 = m2/(float)(NumOfSamples-1);
                     var1 = 0;
                     var2 = 0;
                     for (sample=1; sample<NumOfSamples; sample++)
                         var2 = var2 + (OrdGEDMDiscrete[gene][sample]-prom2)*(OrdGEDMDiscrete[gene][sample]-prom2);
                     //var2 = var2;
                     obj = var1+var2/(float)(NumOfSamples-1); //varianza
                     //obj = var1+var2; //n1
                     pos = 0;
                     for (sample=1; sample<NumOfSamples-1; sample++)
                     {
                       m2=m2 - OrdGEDMDiscrete[gene][sample];
                       m1 = m1 + OrdGEDMDiscrete[gene][sample];
                       prom1 = m1/(float)(sample+1);
                       prom2 = m2/(float)(NumOfSamples-(sample+1));
                       var1 = var2 = 0;
                       for (int vi=0; vi<NumOfSamples; vi++){
                           if (vi<=sample)
                           {
                               var1=var1+(OrdGEDMDiscrete[gene][vi]-prom1)*(OrdGEDMDiscrete[gene][vi]-prom1);
                           }
                           else
                           {
                               var2=var2+(OrdGEDMDiscrete[gene][vi]-prom2)*(OrdGEDMDiscrete[gene][vi]-prom2);
                           }
                       }
                       var1 = var1/(float)(sample+1);
                       var2 = var2/(float)(NumOfSamples-(sample+1));

                       if (var1+var2<obj){
                           obj = var1+var2;
                           pos = sample;
                       }
                     }

                     MEDG[gene] = (OrdGEDMDiscrete[gene][pos]+OrdGEDMDiscrete[gene][pos+1])/(float)2;

                }
                else if (NumOfSamples==2)
                {
                     MEDG[gene] = (OrdGEDMDiscrete[gene][0]+OrdGEDMDiscrete[gene][1])/(float)2;
                }
                else if (NumOfSamples==1)
                {
                     MEDG[gene] = OrdGEDMDiscrete[gene][0];
                }

            }

            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                    if (ActualProject.datamatrix[d][i][j] > MEDG[i])
                        ActualProject.datamatrix[d][i][j] = 1;
                    else
                        ActualProject.datamatrix[d][i][j] = 0;

            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(0);

            datasetsview.getComponentAt(d).repaint();

    }

    private void JiTan(int d)
    {
        float discretizedged [][] = new float[ActualProject.datamatrix[d].length][ActualProject.datamatrix[d][0].length-1];
        Vector<String> newcolumnsnames = new Vector<String>();

        float T = Float.parseFloat(t.getText());

        for (int j=0; j<ActualProject.datamatrix[d][0].length-1; j++)
            newcolumnsnames.add(ActualProject.D_columnas[d].get(j)+"-"+ActualProject.D_columnas[d].get(j+1));

        for (int i=0; i<ActualProject.datamatrix[d].length; i++)
            for (int j=0; j<ActualProject.datamatrix[d][0].length-1; j++)
                if(ActualProject.datamatrix[d][i][j] != 0)
                    discretizedged[i][j]=Math.abs(ActualProject.datamatrix[d][i][j+1]-ActualProject.datamatrix[d][i][j])/ActualProject.datamatrix[d][i][j];
                else if (ActualProject.datamatrix[d][i][j+1] > 0)
                    discretizedged[i][j]=1;
                else if (ActualProject.datamatrix[d][i][j+1] < 0)
                    discretizedged[i][j]=-1;
                else if (ActualProject.datamatrix[d][i][j+1] == 0)
                    discretizedged[i][j]=0;

         for (int i=0; i<discretizedged.length; i++)
            for (int j=0; j<discretizedged[0].length; j++)
                if (discretizedged[i][j]>=T)
                    discretizedged[i][j] = 1;
                else if (discretizedged[i][j]<=-T)
                    discretizedged[i][j] = -1;
                else
                    discretizedged[i][j] = 0;

        opening = true;
        datasetsview.removeAll();


        ActualProject.datamatrix[d] = discretizedged;
        ActualProject.D_columnas[d] = newcolumnsnames;

        for (int i=0; i<ActualProject.datasets.length; i++)
        {
            JvTable mainTableData = new JvTable(new TableModelDatasets(ActualProject, i));
            mainTableData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            mainTableData.setDefaultRenderer(Color.class, new HeatMapRenderer());
            javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(mainTableData);
            javax.swing.JTable rowTable = new RowGeneTable(mainTableData, ActualProject);
            rowTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            //((RowGeneTable)rowTable).updateRowHeights();

            //javax.swing.JScrollPane scrollPane2 = new javax.swing.JScrollPane(rowTable);;
            scrollPane.setRowHeaderView(rowTable);
            scrollPane.setCorner(javax.swing.JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());
            if (ActualProject.timeseries[i])
                datasetsview.add(ActualProject.datasets[i].getName()+" [Time Series]", scrollPane);
            else
                datasetsview.add(ActualProject.datasets[i].getName()+" [Steady State]", scrollPane);

        }

        datasetsview.setSelectedIndex(d);

        opening = false;

        ActualProject.updateStat();
        HeatMapScope.setSelectedIndex(0);
        datasetsview.getComponentAt(d).repaint();
    }

     private void Soinov(int d)
    {
        float discretizedged [][] = new float[ActualProject.datamatrix[d].length][ActualProject.datamatrix[d][0].length-1];
        Vector<String> newcolumnsnames = new Vector<String>();
        mean(d);


        for (int j=0; j<ActualProject.datamatrix[d][0].length-1; j++)
            newcolumnsnames.add(ActualProject.D_columnas[d].get(j)+"-"+ActualProject.D_columnas[d].get(j+1));

        for (int i=0; i<ActualProject.datamatrix[d].length; i++)
            for (int j=0; j<ActualProject.datamatrix[d][0].length-1; j++)
                    discretizedged[i][j]=ActualProject.datamatrix[d][i][j+1]-ActualProject.datamatrix[d][i][j];



        opening = true;
        datasetsview.removeAll();


        ActualProject.datamatrix[d] = discretizedged;
        ActualProject.D_columnas[d] = newcolumnsnames;

        for (int i=0; i<ActualProject.datasets.length; i++)
        {
            JvTable mainTableData = new JvTable(new TableModelDatasets(ActualProject, i));
            mainTableData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            mainTableData.setDefaultRenderer(Color.class, new HeatMapRenderer());
            javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(mainTableData);
            javax.swing.JTable rowTable = new RowGeneTable(mainTableData, ActualProject);
            rowTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            //((RowGeneTable)rowTable).updateRowHeights();

            //javax.swing.JScrollPane scrollPane2 = new javax.swing.JScrollPane(rowTable);;
            scrollPane.setRowHeaderView(rowTable);
            scrollPane.setCorner(javax.swing.JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());
            if (ActualProject.timeseries[i])
                datasetsview.add(ActualProject.datasets[i].getName()+" [Time Series]", scrollPane);
            else
                datasetsview.add(ActualProject.datasets[i].getName()+" [Steady State]", scrollPane);

        }

        datasetsview.setSelectedIndex(d);

        opening = false;

        ActualProject.updateStat();
        HeatMapScope.setSelectedIndex(0);
        datasetsview.getComponentAt(d).repaint();
    }


     private void Erdal(int d)
    {
        float discretizedged [][] = new float[ActualProject.datamatrix[d].length][ActualProject.datamatrix[d][0].length-1];
        Vector<String> newcolumnsnames = new Vector<String>();

        float threshold = Float.parseFloat(alpha.getText()) * ActualProject.dataMatrixStatCol[d][0][3];



        for (int j=0; j<ActualProject.datamatrix[d][0].length-1; j++)
            newcolumnsnames.add(ActualProject.D_columnas[d].get(j)+"-"+ActualProject.D_columnas[d].get(j+1));

        for (int i=0; i<ActualProject.datamatrix[d].length; i++)
            for (int j=0; j<ActualProject.datamatrix[d][0].length-1; j++)
                if (Math.abs(ActualProject.datamatrix[d][i][j+1] - ActualProject.datamatrix[d][i][j]) >= threshold)
                    discretizedged[i][j]=1;
                else
                    discretizedged[i][j]=0;



        opening = true;
        datasetsview.removeAll();


        ActualProject.datamatrix[d] = discretizedged;
        ActualProject.D_columnas[d] = newcolumnsnames;

        for (int i=0; i<ActualProject.datasets.length; i++)
        {
            JvTable mainTableData = new JvTable(new TableModelDatasets(ActualProject, i));
            mainTableData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            mainTableData.setDefaultRenderer(Color.class, new HeatMapRenderer());
            javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(mainTableData);
            javax.swing.JTable rowTable = new RowGeneTable(mainTableData, ActualProject);
            rowTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            //((RowGeneTable)rowTable).updateRowHeights();

            //javax.swing.JScrollPane scrollPane2 = new javax.swing.JScrollPane(rowTable);;
            scrollPane.setRowHeaderView(rowTable);
            scrollPane.setCorner(javax.swing.JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());
            if (ActualProject.timeseries[i])
                datasetsview.add(ActualProject.datasets[i].getName()+" [Time Series]", scrollPane);
            else
                datasetsview.add(ActualProject.datasets[i].getName()+" [Steady State]", scrollPane);

        }

        datasetsview.setSelectedIndex(d);

        opening = false;

        ActualProject.updateStat();
        HeatMapScope.setSelectedIndex(0);
        datasetsview.getComponentAt(d).repaint();
    }

    private void TSD(int d)
    {
        float discretizedged [][] = new float[ActualProject.datamatrix[d].length][ActualProject.datamatrix[d][0].length-1];
        Vector<String> newcolumnsnames = new Vector<String>();
        zscoreActionPerformed(null);


        for (int j=0; j<ActualProject.datamatrix[d][0].length-1; j++)
            newcolumnsnames.add(ActualProject.D_columnas[d].get(j)+"-"+ActualProject.D_columnas[d].get(j+1));

        for (int i=0; i<ActualProject.datamatrix[d].length; i++)
            for (int j=0; j<ActualProject.datamatrix[d][0].length-1; j++)
                if (ActualProject.datamatrix[d][i][j+1]>=ActualProject.datamatrix[d][i][j])
                    discretizedged[i][j]=1;
                else
                    discretizedged[i][j]=0;



        opening = true;
        datasetsview.removeAll();


        ActualProject.datamatrix[d] = discretizedged;
        ActualProject.D_columnas[d] = newcolumnsnames;

        for (int i=0; i<ActualProject.datasets.length; i++)
        {
            JvTable mainTableData = new JvTable(new TableModelDatasets(ActualProject, i));
            mainTableData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            mainTableData.setDefaultRenderer(Color.class, new HeatMapRenderer());
            javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(mainTableData);
            javax.swing.JTable rowTable = new RowGeneTable(mainTableData, ActualProject);
            rowTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            //((RowGeneTable)rowTable).updateRowHeights();

            //javax.swing.JScrollPane scrollPane2 = new javax.swing.JScrollPane(rowTable);;
            scrollPane.setRowHeaderView(rowTable);
            scrollPane.setCorner(javax.swing.JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());
            if (ActualProject.timeseries[i])
                datasetsview.add(ActualProject.datasets[i].getName()+" [Time Series]", scrollPane);
            else
                datasetsview.add(ActualProject.datasets[i].getName()+" [Steady State]", scrollPane);

        }

        datasetsview.setSelectedIndex(d);

        opening = false;

        ActualProject.updateStat();
        HeatMapScope.setSelectedIndex(0);
        datasetsview.getComponentAt(d).repaint();
    }


    private void EWD(int d)
    {
        if (row.isSelected())        {


            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                {
                    float step = Math.abs(ActualProject.dataMatrixStat[d][i][1] - ActualProject.dataMatrixStat[d][i][0]) / (level_of_discretization.getSelectedIndex()+2);
                    if (ActualProject.datamatrix[d][i][j] == ActualProject.dataMatrixStat[d][i][1])
                        ActualProject.datamatrix[d][i][j] = (int)((ActualProject.datamatrix[d][i][j] - ActualProject.dataMatrixStat[d][i][0])/step) - 1;
                    else
                        ActualProject.datamatrix[d][i][j] = (int)((ActualProject.datamatrix[d][i][j] - ActualProject.dataMatrixStat[d][i][0])/step);
                }

            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(0);
            datasetsview.getComponentAt(d).repaint();
        }
        else
        if (column.isSelected())
        {
            

            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                {
                    float step = Math.abs(ActualProject.dataMatrixStatCol[d][j][1] - ActualProject.dataMatrixStatCol[d][j][0]) / (level_of_discretization.getSelectedIndex()+2);
                    if (ActualProject.datamatrix[d][i][j] == ActualProject.dataMatrixStatCol[d][j][1])
                        ActualProject.datamatrix[d][i][j] = (int)((ActualProject.datamatrix[d][i][j] - ActualProject.dataMatrixStatCol[d][j][0])/step) - 1;
                    else
                        ActualProject.datamatrix[d][i][j] = (int)((ActualProject.datamatrix[d][i][j] - ActualProject.dataMatrixStatCol[d][j][0])/step);
                }


            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(1);
            datasetsview.getComponentAt(d).repaint();

        }
        else
        if (matrix.isSelected())
        {
            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                {
                    float step = Math.abs(ActualProject.dataMatrixStatMatrix[d][1] - ActualProject.dataMatrixStatMatrix[d][0]) / (level_of_discretization.getSelectedIndex()+2);
                    if (ActualProject.datamatrix[d][i][j] == ActualProject.dataMatrixStatMatrix[d][1])
                        ActualProject.datamatrix[d][i][j] = (int)((ActualProject.datamatrix[d][i][j] - ActualProject.dataMatrixStatMatrix[d][0])/step) - 1;
                    else
                        ActualProject.datamatrix[d][i][j] = (int)((ActualProject.datamatrix[d][i][j] - ActualProject.dataMatrixStatMatrix[d][0])/step);
                }

            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(2);

            datasetsview.getComponentAt(d).repaint();


        }
    }


    private void TopX(int d)
    {
        if (row.isSelected())        {

            float top_filas[] = new float[ActualProject.datamatrix[d].length];

            float [][] orddata = copydatasets (ActualProject.datamatrix[d]);
            for (int i=0; i<orddata.length; i++)
            {
                Arrays.sort(orddata[i]);
                top_filas[i] =orddata[i][(int)((float)(orddata[i].length-1)*((float)1-(float)percentageX.getSelectedIndex()/(float)100))];

                
            }


            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                    if (ActualProject.datamatrix[d][i][j] >= top_filas[i])
                        ActualProject.datamatrix[d][i][j] = 1;
                    else
                        ActualProject.datamatrix[d][i][j] = 0;
            
            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(0);
            datasetsview.getComponentAt(d).repaint();
        }
        else
        if (column.isSelected())
        {
            float top_columns[] = new float [ActualProject.datamatrix[d][0].length];
            float [][] orddata = copydatasetsTrans (ActualProject.datamatrix[d]);
            for (int j=0; j<orddata.length; j++)
            {
                Arrays.sort(orddata[j]);
                top_columns[j] = orddata[j][(int)((float)(orddata[j].length-1)*((float)1-(float)percentageX.getSelectedIndex()/(float)100))];

                

            }

            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                    if (ActualProject.datamatrix[d][i][j] >= top_columns[j])
                        ActualProject.datamatrix[d][i][j] = 1;
                    else
                        ActualProject.datamatrix[d][i][j] = 0;
            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(1);
            datasetsview.getComponentAt(d).repaint();

        }
        else
        if (matrix.isSelected())
        {
            float top_matrix;
            float [] orddata = copydatasetsAlong(ActualProject.datamatrix[d]);
            Arrays.sort(orddata);
            top_matrix = orddata[(int)((float)(orddata.length-1)*((float)1-(float)percentageX.getSelectedIndex()/(float)100))];

            
            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                    if (ActualProject.datamatrix[d][i][j] >= top_matrix)
                        ActualProject.datamatrix[d][i][j] = 1;
                    else
                        ActualProject.datamatrix[d][i][j] = 0;
            
            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(2);
            
            datasetsview.getComponentAt(d).repaint();


        }
    }


    private void MaxXMax(int d)
    {
        if (row.isSelected())        {

            float max_filas[] = new float[ActualProject.datamatrix[d].length];

            float [][] orddata = copydatasets (ActualProject.datamatrix[d]);
            for (int i=0; i<orddata.length; i++)
            {
                Arrays.sort(orddata[i]);
                max_filas[i] =orddata[i][orddata[i].length-1];

                
            }


            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                    if (ActualProject.datamatrix[d][i][j] >= (max_filas[i] - max_filas[i]*(float)percentageX.getSelectedIndex()/(float)100))
                        ActualProject.datamatrix[d][i][j] = 1;
                    else
                        ActualProject.datamatrix[d][i][j] = 0;

            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(0);
            
            datasetsview.getComponentAt(d).repaint();
        }
        else
        if (column.isSelected())
        {
            float max_columns[] = new float [ActualProject.datamatrix[d][0].length];
            float [][] orddata = copydatasetsTrans (ActualProject.datamatrix[d]);
            for (int j=0; j<orddata.length; j++)
            {
                Arrays.sort(orddata[j]);
                max_columns[j] = orddata[j][orddata[j].length-1];

                

            }

            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                    if (ActualProject.datamatrix[d][i][j] >= (max_columns[j] - max_columns[j]*(float)percentageX.getSelectedIndex()/(float)100))
                        ActualProject.datamatrix[d][i][j] = 1;
                    else
                        ActualProject.datamatrix[d][i][j] = 0;

            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(1);
            
            datasetsview.getComponentAt(d).repaint();

        }
        else
        if (matrix.isSelected())
        {
            float max_matrix;
            float [] orddata = copydatasetsAlong(ActualProject.datamatrix[d]);
            Arrays.sort(orddata);
            max_matrix = orddata[orddata.length-1];

            
            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                    if (ActualProject.datamatrix[d][i][j] >= (max_matrix - max_matrix*(float)percentageX.getSelectedIndex()/(float)100))
                        ActualProject.datamatrix[d][i][j] = 1;
                    else
                        ActualProject.datamatrix[d][i][j] = 0;

            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(2);
            
            datasetsview.getComponentAt(d).repaint();


        }
    }

    private float [][] copydatasets (float data[][])
    {
        float dataset[][] = new float[data.length][data[0].length];
        
        
        for (int i=0; i<data.length; i++)
            for (int j=0; j<data[0].length; j++)
                dataset[i][j] = data[i][j];
        
        return dataset;
    }

    private float [][] copydatasetsTrans (float data[][])
    {
        float dataset[][] = new float[data[0].length][data.length];


        for (int i=0; i<data.length; i++)
            for (int j=0; j<data[0].length; j++)
                dataset[j][i] = data[i][j];

        return dataset;
    }

    private float [] copydatasetsAlong (float data[][])
    {
        float dataset[] = new float[data[0].length*data.length];


        for (int i=0; i<data.length; i++)
            for (int j=0; j<data[0].length; j++)
            {
                dataset[i*(data[0].length)+j] = data[i][j];
            }

        return dataset;
    }

    private float [][] copydatasetsAlongTrans (float data[], int f, int c)
    {
        float dataset[][] = new float[f][c];


        for (int i=0; i<dataset.length; i++)
            for (int j=0; j<dataset[0].length; j++)
            {
                dataset[i][j] = data[i*(dataset[0].length)+j];
            }

        return dataset;
    }

    private void median(int d)
    {
        if (row.isSelected())        {

            float medias_filas[] = new float[ActualProject.datamatrix[d].length];
            float [][] orddata = copydatasets (ActualProject.datamatrix[d]);
            for (int i=0; i<orddata.length; i++)
            {
                Arrays.sort(orddata[i]);
                if (orddata[0].length % 2 == 0)
                    medias_filas[i] = (orddata[i][(int)(orddata[0].length/2) - 1] + orddata[i][(int)(orddata[0].length/2)])/2;
                else
                    medias_filas[i] = orddata[i][(int)(orddata[0].length/2)];

                
            }


            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                    if (ActualProject.datamatrix[d][i][j] >= medias_filas[i])
                        ActualProject.datamatrix[d][i][j] = 1;
                    else
                        ActualProject.datamatrix[d][i][j] = 0;

            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(0);
            
            datasetsview.getComponentAt(d).repaint();
        }
        else
        if (column.isSelected())
        {
            float medias_columns[] = new float [ActualProject.datamatrix[d][0].length];
            float [][] orddata = copydatasetsTrans (ActualProject.datamatrix[d]);
            for (int j=0; j<orddata.length; j++)
            {
                Arrays.sort(orddata[j]);
                 if (orddata[0].length % 2 == 0)
                    medias_columns[j] = (orddata[j][(int)(orddata[0].length/2) - 1] + orddata[j][(int)(orddata[0].length/2)])/2;
                else
                    medias_columns[j] = orddata[j][(int)(orddata[0].length/2)];

                
            }

            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                    if (ActualProject.datamatrix[d][i][j] >= medias_columns[j])
                        ActualProject.datamatrix[d][i][j] = 1;
                    else
                        ActualProject.datamatrix[d][i][j] = 0;

            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(1);
            
            datasetsview.getComponentAt(d).repaint();

        }
        else
        if (matrix.isSelected())
        {
            float median_matrix;
            float [] orddata = copydatasetsAlong(ActualProject.datamatrix[d]);
            Arrays.sort(orddata);
            if (orddata.length % 2 == 0)
                    median_matrix = (orddata[(int)(orddata.length/2) - 1] + orddata[(int)(orddata.length/2)])/2;
            else
                    median_matrix = orddata[(int)(orddata.length/2)];

            
            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                    if (ActualProject.datamatrix[d][i][j] >= median_matrix)
                        ActualProject.datamatrix[d][i][j] = 1;
                    else
                        ActualProject.datamatrix[d][i][j] = 0;

            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(2);
            
            datasetsview.getComponentAt(d).repaint();


        }
    }

    private void EFD(int d)
    {
        if (row.isSelected())        {

            
            float [][] orddata = copydatasets (ActualProject.datamatrix[d]);
            for (int i=0; i<orddata.length; i++)
            {
                Arrays.sort(orddata[i]);
            }


            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                {
                    float step = (float)ActualProject.datamatrix[d][0].length/(float)(level_of_discretization.getSelectedIndex()+2);
                    float limit = step - 1;
                    int bin = 0;
                    while (orddata[i][(int)limit] < ActualProject.datamatrix[d][i][j])
                    {
                        limit +=step;
                        bin++;
                        if (limit >= ActualProject.datamatrix[d][0].length)
                            limit = ActualProject.datamatrix[d][0].length-1;
                    }

                    ActualProject.datamatrix[d][i][j] = bin;
                }


            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(0);
            
            datasetsview.getComponentAt(d).repaint();
        }
        else
        if (column.isSelected())
        {
            
            float [][] orddata = copydatasetsTrans (ActualProject.datamatrix[d]);
            for (int j=0; j<orddata.length; j++)
            {
                Arrays.sort(orddata[j]);          
            }

            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                {
                    float step = (float)ActualProject.datamatrix[d].length/(float)(level_of_discretization.getSelectedIndex()+2);
                    float limit = step - 1;
                    int bin = 0;
                    while (orddata[j][(int)limit] < ActualProject.datamatrix[d][i][j])
                    {
                        limit +=step;
                        bin++;
                        if (limit >= ActualProject.datamatrix[d].length)
                            limit = ActualProject.datamatrix[d].length-1;
                    }

                    ActualProject.datamatrix[d][i][j] = bin;
                }

            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(1);
            
            datasetsview.getComponentAt(d).repaint();

        }
        else
        if (matrix.isSelected())
        {
            float median_matrix;
            float [] orddata = copydatasetsAlong(ActualProject.datamatrix[d]);
            Arrays.sort(orddata);            


            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                {
                    float step = ((float)ActualProject.datamatrix[d].length*ActualProject.datamatrix[d][0].length)/(float)(level_of_discretization.getSelectedIndex()+2);
                    float limit = step - 1;
                    int bin = 0;
                    while (orddata[(int)limit] < ActualProject.datamatrix[d][i][j])
                    {
                        limit +=step;
                        bin++;
                        if (limit >= ActualProject.datamatrix[d].length*ActualProject.datamatrix[d][0].length)
                            limit = ActualProject.datamatrix[d].length*ActualProject.datamatrix[d][0].length-1;
                    }

                    ActualProject.datamatrix[d][i][j] = bin;
                }

            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(2);
            
            datasetsview.getComponentAt(d).repaint();


        }
    }

    private void mean(int d)
    {
        if (row.isSelected())
        {
            float promedios_filas[] = new float[ActualProject.datamatrix[d].length];
            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
            {
                promedios_filas[i] = 0;
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                    promedios_filas[i] += ActualProject.datamatrix[d][i][j];
                promedios_filas[i] /= ActualProject.datamatrix[d][0].length;
            }

            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                    if (ActualProject.datamatrix[d][i][j] >= promedios_filas[i])
                        ActualProject.datamatrix[d][i][j] = 1;
                    else
                        ActualProject.datamatrix[d][i][j] = 0;

            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(0);
            
            datasetsview.getComponentAt(d).repaint();
        }
        else
        if (column.isSelected())
        {
            float promedios_columnas[] = new float[ActualProject.datamatrix[d][0].length];
            for (int j=0; j<ActualProject.datamatrix[d][0].length; j++)
            {
                promedios_columnas[j] = 0;
                for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                    promedios_columnas[j] += ActualProject.datamatrix[d][i][j];
                promedios_columnas[j] /= ActualProject.datamatrix[d].length;
            }

            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                    if (ActualProject.datamatrix[d][i][j] >= promedios_columnas[j])
                        ActualProject.datamatrix[d][i][j] = 1;
                    else
                        ActualProject.datamatrix[d][i][j] = 0;

            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(1);
            
            datasetsview.getComponentAt(d).repaint();

        }
        else
        if (matrix.isSelected())
        {
            float promedio_matrix = 0;
            for (int j=0; j<ActualProject.datamatrix[d][0].length; j++)
                for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                    promedio_matrix += ActualProject.datamatrix[d][i][j];

            promedio_matrix /= ActualProject.datamatrix[d][0].length*ActualProject.datamatrix[d].length;

            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                    if (ActualProject.datamatrix[d][i][j] >= promedio_matrix)
                        ActualProject.datamatrix[d][i][j] = 1;
                    else
                        ActualProject.datamatrix[d][i][j] = 0;

            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(2);
            
            datasetsview.getComponentAt(d).repaint();


        }
    }

    private void meanPlusStdDev(int d)
    {
        float Alpha = Float.parseFloat(alpha.getText());
        if (row.isSelected())
        {
            float promedios_filas[] = new float[ActualProject.datamatrix[d].length];
            float devstd_filas[]  = new float[ActualProject.datamatrix[d].length];
            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
            {
                promedios_filas[i] = 0;
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                    promedios_filas[i] += ActualProject.datamatrix[d][i][j];
                promedios_filas[i] /= ActualProject.datamatrix[d][0].length;

                
            }

            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
            {
                devstd_filas[i] = 0;
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                    devstd_filas[i] += Math.pow(ActualProject.datamatrix[d][i][j] - promedios_filas[i], 2);
                devstd_filas[i] /= ActualProject.datamatrix[d][0].length;
                devstd_filas[i] = (float) Math.sqrt(devstd_filas[i]);
                
            }

            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                    if (ActualProject.datamatrix[d][i][j] > promedios_filas[i] + Alpha*devstd_filas[i])
                        ActualProject.datamatrix[d][i][j] = 1;
                    else if (ActualProject.datamatrix[d][i][j] < promedios_filas[i] - Alpha*devstd_filas[i])
                        ActualProject.datamatrix[d][i][j] = -1;
                    else
                        ActualProject.datamatrix[d][i][j] = 0;


            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(0);
            
            datasetsview.getComponentAt(d).repaint();
        }
        else
        if (column.isSelected())
        {
            float promedios_columnas[] = new float[ActualProject.datamatrix[d][0].length];
            float devstd_columnas[] = new float[ActualProject.datamatrix[d][0].length];
            for (int j=0; j<ActualProject.datamatrix[d][0].length; j++)
            {
                promedios_columnas[j] = 0;
                for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                    promedios_columnas[j] += ActualProject.datamatrix[d][i][j];
                promedios_columnas[j] /= ActualProject.datamatrix[d].length;

                
            }

            for (int j=0; j<ActualProject.datamatrix[d][0].length; j++)
            {
                devstd_columnas[j] = 0;
                for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                    devstd_columnas[j] += Math.pow(ActualProject.datamatrix[d][i][j] - promedios_columnas[j], 2);
                devstd_columnas[j] /= ActualProject.datamatrix[d].length;
                devstd_columnas[j] = (float) Math.sqrt(devstd_columnas[j]);

                
            }

            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                    if (ActualProject.datamatrix[d][i][j] > promedios_columnas[j] + Alpha*devstd_columnas[j])
                        ActualProject.datamatrix[d][i][j] = 1;
                    else if (ActualProject.datamatrix[d][i][j] < promedios_columnas[j] - Alpha*devstd_columnas[j])
                        ActualProject.datamatrix[d][i][j] = -1;
                    else
                        ActualProject.datamatrix[d][i][j] = 0;


            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(1);
            
            datasetsview.getComponentAt(d).repaint();

        }
        else
        if (matrix.isSelected())
        {
            float promedio_matrix = 0;
            float devstd_matrix = 0;
            for (int j=0; j<ActualProject.datamatrix[d][0].length; j++)
                for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                    promedio_matrix += ActualProject.datamatrix[d][i][j];

            promedio_matrix /= ActualProject.datamatrix[d][0].length*ActualProject.datamatrix[d].length;

            

            for (int j=0; j<ActualProject.datamatrix[d][0].length; j++)
                for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                    devstd_matrix += Math.pow(ActualProject.datamatrix[d][i][j] - promedio_matrix, 2);

            devstd_matrix /= ActualProject.datamatrix[d][0].length*ActualProject.datamatrix[d].length;
            devstd_matrix = (float) Math.sqrt(devstd_matrix);

           

            for (int i=0; i<ActualProject.datamatrix[d].length; i++)
                for (int j=0; j<ActualProject.datamatrix[d][0].length;j++)
                    if (ActualProject.datamatrix[d][i][j] > promedio_matrix + Alpha*devstd_matrix)
                        ActualProject.datamatrix[d][i][j] = 1;
                    else if (ActualProject.datamatrix[d][i][j] < promedio_matrix - Alpha*devstd_matrix)
                        ActualProject.datamatrix[d][i][j] = -1;
                    else
                        ActualProject.datamatrix[d][i][j] = 0;

            ActualProject.updateStat();
            HeatMapScope.setSelectedIndex(2);
            
            datasetsview.getComponentAt(d).repaint();


        }
    }

    private void rowStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rowStateChanged
        if (row.isSelected())
        {
            column.setSelected(false);
            matrix.setSelected(false);
            row.setSelected(true);
        }
        else
         {
             if (!column.isSelected() && !matrix.isSelected())
                 row.setSelected(true);
         }
        
    }//GEN-LAST:event_rowStateChanged

    private void updateleveldiscretization()
    {
        level_of_discretization.removeAllItems();
        if (row.isSelected()){
            int d = datasetsview.getSelectedIndex();
            for (int i=2; i<=ActualProject.datamatrix[d][0].length; i++)
            {
                level_of_discretization.addItem(i);
            }
            if (ActualProject.datamatrix[d][0].length<2)
            {
                level_of_discretization.addItem(2);
            }
        }
        else if (column.isSelected())
        {
            int d = datasetsview.getSelectedIndex();
            for (int i=2; i<=ActualProject.datamatrix[d].length; i++)
            {
                level_of_discretization.addItem(i);
            }
            if (ActualProject.datamatrix[d].length<2)
            {
                level_of_discretization.addItem(2);
            }
        }
        else if (matrix.isSelected())
        {
            int d = datasetsview.getSelectedIndex();

            int max = ActualProject.datamatrix[d].length;
            if (max<ActualProject.datamatrix[d][0].length)
                max = ActualProject.datamatrix[d][0].length;


            for (int i=2; i<=max; i++)
            {
                level_of_discretization.addItem(i);
            }

            if (ActualProject.datamatrix[d].length< 2 && ActualProject.datamatrix[d][0].length < 2)
            {
                level_of_discretization.addItem(2);
            }
        }
            
    }


    private void columnStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_columnStateChanged
         if (column.isSelected())
        {
            row.setSelected(false);
            matrix.setSelected(false);
            column.setSelected(true);
        }
         else
         {
             if (!row.isSelected() && !matrix.isSelected())
                 column.setSelected(true);
         }
         
    }//GEN-LAST:event_columnStateChanged

    private void matrixStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_matrixStateChanged
         if (matrix.isSelected())
        {
            row.setSelected(false);
            column.setSelected(false);
            matrix.setSelected(true);
        }
         else
         {
             if (!row.isSelected() && !column.isSelected())
                 matrix.setSelected(true);
         }
         
    }//GEN-LAST:event_matrixStateChanged

    private void selected_discretizerItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_selected_discretizerItemStateChanged


        switch (selected_discretizer.getSelectedIndex())
        {
               case 0:{ //Mean
                        level_of_discretization.setSelectedIndex(0);
                        level_of_discretization.setEnabled(false);
                        alpha.setEnabled(false);
                        percentageX.setEnabled(false);
                        row.setEnabled(true);
                        column.setEnabled(true);
                        matrix.setEnabled(true);
                        t.setEnabled(false);
                        
                        break;
               }
               case 1:{ //Median
                        level_of_discretization.setSelectedIndex(0);
                        level_of_discretization.setEnabled(false);
                        percentageX.setEnabled(false);
                        alpha.setEnabled(false);
                        row.setEnabled(true);
                        column.setEnabled(true);
                        matrix.setEnabled(true);
                        t.setEnabled(false);
                        
                        break;
               }
               case 2:{ //Max - X%Max
                        level_of_discretization.setSelectedIndex(0);
                        level_of_discretization.setEnabled(false);
                        percentageX.setEnabled(true);
                        alpha.setEnabled(false);
                        row.setEnabled(true);
                        column.setEnabled(true);
                        matrix.setEnabled(true);
                        t.setEnabled(false);
                        
                        break;
               }
               case 3:{ //Top %X
                        level_of_discretization.setSelectedIndex(0);
                        level_of_discretization.setEnabled(false);
                        percentageX.setEnabled(true);
                        alpha.setEnabled(false);
                        row.setEnabled(true);
                        column.setEnabled(true);
                        matrix.setEnabled(true);
                        t.setEnabled(false);
                        
                        break;
               }
               case 4:{ //MeanPlusEstDev
                        level_of_discretization.setSelectedIndex(0);
                        level_of_discretization.setEnabled(false);
                        percentageX.setEnabled(false);
                        alpha.setEnabled(true);
                        row.setEnabled(true);
                        column.setEnabled(true);
                        matrix.setEnabled(true);
                        t.setEnabled(false);
                        
                        break;
               }               
               case 5:{ //EFD
                        level_of_discretization.setSelectedIndex(0);
                        level_of_discretization.setEnabled(true);
                        percentageX.setEnabled(false);
                        alpha.setEnabled(false);
                        row.setEnabled(true);
                        column.setEnabled(true);
                        matrix.setEnabled(true);
                        t.setEnabled(false);
                        
                        break;
               } 
               case 6:{ //EWD
                        level_of_discretization.setSelectedIndex(0);
                        level_of_discretization.setEnabled(true);
                        percentageX.setEnabled(false);
                        alpha.setEnabled(false);
                        row.setEnabled(true);
                        column.setEnabled(true);
                        matrix.setEnabled(true);
                        t.setEnabled(false);
                        
                        break;
               } 
               case 7:{ //TSD
                        level_of_discretization.setEnabled(false);
                        percentageX.setEnabled(false);
                        row.setEnabled(false);
                        row.setSelected(true);
                        alpha.setEnabled(false);
                        column.setEnabled(false);
                        matrix.setEnabled(false);
                        t.setEnabled(false);
                        
                        break;
               }
               case 8:{ //Erdal's et al. method
                        level_of_discretization.setEnabled(false);
                        percentageX.setEnabled(false);
                        row.setEnabled(false);
                        row.setSelected(true);
                        alpha.setEnabled(true);
                        column.setEnabled(false);
                        matrix.setEnabled(false);
                        t.setEnabled(false);
                        
                        break;
               }
               case 9:{ //Soinov's change state
                        level_of_discretization.setSelectedIndex(0);
                        level_of_discretization.setEnabled(false);
                        percentageX.setEnabled(false);
                        row.setEnabled(false);
                        row.setSelected(true);
                        alpha.setEnabled(false);
                        column.setEnabled(false);
                        matrix.setEnabled(false);
                        t.setEnabled(false);
                        
                        break;
               }
               case 10:{ //Ji and  Tan method
                        level_of_discretization.setSelectedIndex(0);
                        level_of_discretization.setEnabled(false);
                        percentageX.setEnabled(false);
                        row.setEnabled(false);
                        row.setSelected(true);
                        column.setEnabled(false);
                        matrix.setEnabled(false);
                        t.setEnabled(true);
                        
                        break;
               }
               case 11:{ //k means
                        level_of_discretization.setSelectedIndex(0);
                        level_of_discretization.setEnabled(true);
                        percentageX.setEnabled(false);
                        row.setEnabled(true);
                        alpha.setEnabled(false);
                        column.setEnabled(true);
                        matrix.setEnabled(true);
                        t.setEnabled(false);
                        
                        break;
               }
               case 12:{ //Gallo et al. method
                        level_of_discretization.setSelectedIndex(0);
                        level_of_discretization.setEnabled(false);
                        percentageX.setEnabled(false);
                        row.setEnabled(false);
                        row.setSelected(true);
                        alpha.setEnabled(false);
                        column.setEnabled(false);
                        matrix.setEnabled(false);
                        t.setEnabled(false);
                        
                        break;
               }
               case 13:{ //BiKmeans
                        level_of_discretization.setSelectedIndex(0);
                        level_of_discretization.setEnabled(true);
                        percentageX.setEnabled(false);
                        row.setEnabled(false);
                        row.setSelected(true);
                        alpha.setEnabled(false);
                        column.setEnabled(false);
                        matrix.setEnabled(false);
                        t.setEnabled(false);
                        
                        break;
               }
               case 14:{ //FI supervised
                        level_of_discretization.setSelectedIndex(0);
                        level_of_discretization.setEnabled(false);
                        percentageX.setEnabled(false);
                        row.setEnabled(false);
                        row.setSelected(false);
                        alpha.setEnabled(false);
                        column.setEnabled(false);
                        matrix.setEnabled(false);
                        t.setEnabled(false);
                        
                        break;
               }
               case 15:{ //Entropy Based supervised
                        level_of_discretization.setSelectedIndex(0);
                        level_of_discretization.setEnabled(false);
                        percentageX.setEnabled(false);
                        row.setEnabled(false);
                        row.setSelected(false);
                        alpha.setEnabled(false);
                        column.setEnabled(false);
                        matrix.setEnabled(false);
                        t.setEnabled(false);

                        break;
               }
                
                
        }
    }//GEN-LAST:event_selected_discretizerItemStateChanged

    private void alphaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_alphaPropertyChange
       
    }//GEN-LAST:event_alphaPropertyChange

    private void HeatMapScopeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HeatMapScopeActionPerformed
        // TODO add your handling code here:
       
    }//GEN-LAST:event_HeatMapScopeActionPerformed

    private void HeatMapScopeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HeatMapScopeMouseClicked
        // TODO add your handling code here:
       
    }//GEN-LAST:event_HeatMapScopeMouseClicked

    private void HeatMapScopeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_HeatMapScopeItemStateChanged
        int d = datasetsview.getSelectedIndex();
        ActualProject.heatMapScope = HeatMapScope.getSelectedIndex();
        ActualProject.updateStat();
        datasetsview.getComponentAt(d).repaint();
    }//GEN-LAST:event_HeatMapScopeItemStateChanged

    private void rowItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rowItemStateChanged
        // TODO add your handling code here:
        updateleveldiscretization();
    }//GEN-LAST:event_rowItemStateChanged

    private void columnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_columnItemStateChanged
        // TODO add your handling code here:
        updateleveldiscretization();
    }//GEN-LAST:event_columnItemStateChanged

    private void matrixItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_matrixItemStateChanged
        updateleveldiscretization();
    }//GEN-LAST:event_matrixItemStateChanged

    private void discretizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discretizeActionPerformed
        int d = datasetsview.getSelectedIndex();


        level_of_discretization.removeAllItems();
        
        updateleveldiscretization();
        setup_discretizer.setLocation(300, 300);
        setup_discretizer.setSize(500, 350);
        setup_discretizer.setResizable(false);


        setup_discretizer.setVisible(true);
    }//GEN-LAST:event_discretizeActionPerformed
String decodedPath;
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        if (java.awt.Desktop.isDesktopSupported()) {
            try {
                
                File myFile = new File(decodedPath+File.separator + "usermanual.pdf");
                java.awt.Desktop.getDesktop().open(myFile);
            } catch (IOException ex) {
                // no application registered for PDFs
            }
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private double [][] toDoubleMatrix(float m[][])
    {
        double res[][] = new double[m.length][m[0].length];
        for (int i=0; i<m.length; i++)
            for (int j=0; j<m[0].length; j++)
            {
                res[i][j] = m[i][j];
            }
        return res;
    }

    private float [][] toFloatMatrix(double m[][])
    {
        float res[][] = new float[m.length][m[0].length];
        for (int i=0; i<m.length; i++)
            for (int j=0; j<m[0].length; j++)
            {
                res[i][j] = (float) m[i][j];
            }
        return res;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ClearList_newproyect;
    private javax.swing.JComboBox HeatMapScope;
    private javax.swing.JToggleButton HeatMapView;
    private javax.swing.JButton acceptColorChooser;
    private javax.swing.JButton acceptNewproject;
    private javax.swing.JButton add;
    private javax.swing.JTextField addNumber;
    private javax.swing.JButton adddatasetsNewproject;
    private javax.swing.JTextField alpha;
    private javax.swing.JButton cancelColorCode;
    private javax.swing.JButton cancelNewproject;
    private javax.swing.JDialog colorDialog;
    private javax.swing.JRadioButton column;
    private javax.swing.JComboBox comboBoxZoom;
    private javax.swing.JPanel datasetselecction;
    private javax.swing.JTabbedPane datasetsview;
    private javax.swing.JButton discretize;
    private javax.swing.JButton do_discretize;
    private javax.swing.JButton estimateMissingValues;
    private javax.swing.JFileChooser filedatasetChooser;
    private javax.swing.JFileChooser genenameschooser;
    private javax.swing.JRadioButton hasColumnNames;
    private javax.swing.JRadioButton hasRowNames;
    private javax.swing.JRadioButton hasTargetDescription;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator13;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JToolBar.Separator jSeparator16;
    private javax.swing.JToolBar.Separator jSeparator17;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JComboBox level_of_discretization;
    private javax.swing.JButton log10;
    private javax.swing.JButton log2;
    private javax.swing.JButton logN;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTabbedPane mainTabbedPanel;
    private javax.swing.JRadioButton matrix;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton mult;
    private javax.swing.JTextField multNumber;
    private javax.swing.JMenuItem newProjectMenuItem;
    private javax.swing.JDialog newproyect;
    private javax.swing.JFileChooser openMenuFileChooser;
    private javax.swing.JLabel overexpressedColor;
    private javax.swing.JLabel overexpressedColorPreview;
    private javax.swing.JComboBox percentageX;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton row;
    private javax.swing.JMenuItem saveAsItemMenu;
    private javax.swing.JButton saveButton;
    private javax.swing.JFileChooser saveMenuFileChooser;
    private javax.swing.JComboBox selected_discretizer;
    private javax.swing.JButton setColorsHeatmap;
    private javax.swing.JDialog setup_discretizer;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTextField t;
    private javax.swing.JLabel underexpressedColor;
    private javax.swing.JLabel underexpressedColorPreview;
    private javax.swing.JDialog wait;
    private javax.swing.JLabel zoomJLabel;
    private javax.swing.JButton zscore;
    // End of variables declaration//GEN-END:variables


    private boolean saved = false;
    private boolean up = false;
    private JPanel [] fileselection = null;
    private File [] files = null;
    private File genenames = null;
    Integer Problem = null;
    //Graph visualization
   
    
    
    public Project ActualProject = null;

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;

     


    


    

   

  
}
