package ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import domain.ApplicationState;
import domain.Controller;
import domain.EditionMode;
import domain.TriangularDistribution;
import domain.network.*;
import domain.network.NetworkElement;
import domain.network.Node;
import domain.network.Segment;
import domain.network.BusRoute;
import ui.tree.ColoredMutableTreeNode;
import ui.tree.ColoredTreeCellRenderer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MainForm {
    private JFrame mainFrame;
    private JPanel contentPane;
    private JMenuBar menuBar;
    private JPanel mapPane;
    private JButton btnFile;
    private JButton btnOpen;
    private JButton btnSave;
    private JButton btnClose;
    private JButton btnConfiguation;
    private JButton btnUndo;
    private JButton btnStatistics;
    private JButton btnRoutes;
    private JButton btnCircuits;
    private JToggleButton btnCreateRoute;
    private JToggleButton btnCreateCircuit;
    private JButton btnRedo;
    private JTextField txtStart;
    private JTextField txtEnd;
    private JButton btnStart;
    private JButton btnPlay;
    private JButton btnStop;
    private JSpinner spnSpeed;
    private JTree displayTree;
    private JCheckBox chkDisplay3;
    private JCheckBox chkDisplay2;
    private JCheckBox ckbDisplay1;
    private JButton btnZoomOut;
    private JButton btnZoomIn;
    private JLabel lblTools;
    private JToggleButton btnCreateNode;
    private JToggleButton btnCreateSegment;
    private JLabel lblPosition;
    private JLabel lblStart;
    private JLabel lblEnd;
    private JLabel lblSpeed;
    private JLabel lblTime;
    private JLabel lblTitleSection;
    private JPanel pnlDomainObjects;
    private JToggleButton btnSelection;
    private JLabel lblError;
    private JLabel lblMessage;
    private JButton btnDeleteSelected;
    private JButton btnValidate;
    private JTextField txtNodeName;
    private JSpinner spnSegmentMinDuration;
    private JSpinner spnSegmentAvgDuration;
    private JSpinner spnSegmentMaxDuration;
    private JSpinner spnSourceMinDuration;
    private JSpinner spnSourceAvgDuration;
    private JSpinner spnSourceMaxDuration;
    private JSpinner spnTimeBeforeFirstVehicule;
    private JSpinner spnCircuitNumber;
    private JTextField txtCircuitName;
    private JCheckBox ckbCircuitIsLoop;
    private JPanel pnlEditNode;
    private JPanel pnlEditSegment;
    private JPanel pnlEditCircuit;
    private JPanel pnlEditSource;
    private JSpinner spnSourceNumberMaxVehicule;
    private JPanel pnlEditRoute;
    private JSpinner spnTimeBeforeFirstPerson;
    private JSpinner spnRouteNumberMaxPerson;
    private JSpinner spnRouteMinDuration;
    private JSpinner spnRouteAvgDuration;
    private JSpinner spnRouteMaxDuration;
    private JPanel pnlBottomBar;
    private Timer timer;

    private ImageIcon imgPause;
    private ImageIcon imgStart;

    Controller controller;

    Point dragOrigin;

    public static void main(String[] args) {
        MainForm form = new MainForm();
        Controller controller = new Controller(form);
        form.setController(controller);

    }

    public void update() {
        ApplicationState state = controller.getState();

        lblPosition.setText(state.getCurrentPosition().toString() + " Zoom: " + state.getZoomLevel() + " %");
        lblMessage.setText(state.getMessage());

        ImageIcon img = timer != null && timer.isRunning() ? imgPause : imgStart;
        btnPlay.setIcon(img);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Circuits");
        for (BusRoute r : state.getNetwork().getRoutes()) {
            ColoredMutableTreeNode node = new ColoredMutableTreeNode(r, r.getColor());
            root.add(node);
        }
        DefaultTreeModel model = (DefaultTreeModel) displayTree.getModel();
        model.setRoot(root);

        if (state.getCurrentMode() != EditionMode.None) {
            hideEditPanels();
        }

        if (state.getCurrentMode() == EditionMode.Simulation) {
            if (state.getSimulation() != null) {
                long minute = Math.round(state.getCurrentMinute());
                lblTime.setText(state.getSimulation().getStartAt().plusMinutes(minute).toString());
            }
        } else {
            if (timer != null)
                timer.stop();
            timer = null;
        }

        mainFrame.repaint();

        if (state.getCurrentMode() == EditionMode.Simulation) {
            if (timer == null) {
                timer = new Timer(17, e -> {
                    int value = (int) spnSpeed.getValue();
                    value = value < 1 ? 1 : value;
                    controller.increaseSimulationTime(value / 100d);
                });
                timer.start();
            }
        }
    }

    public void pauseSimulation() {
        if (timer != null)
            timer.stop();
    }

    public void editElement(NetworkElement elem) {
        hideEditPanels();
        btnDeleteSelected.setVisible(elem != null);

        if (elem instanceof Segment) {
            Segment segment = (Segment) elem;
            TriangularDistribution distribution = segment.getDistribution();
            spnSegmentMinDuration.setValue((int) Math.round(distribution.getMinValue()));
            spnSegmentAvgDuration.setValue((int) Math.round(distribution.getAverageValue()));
            spnSegmentMaxDuration.setValue((int) Math.round(distribution.getMaxValue()));
            pnlEditSegment.setVisible(true);
        } else if (elem instanceof Node) {
            Node node = (Node) elem;
            txtNodeName.setText(node.getName());
            pnlEditNode.setVisible(true);
        }
    }

    public void saveElement(NetworkElement elem) {
        if (elem instanceof Segment) {
            TriangularDistribution distribution = ((Segment) elem).getDistribution();
            distribution.setMinValue((int) spnSegmentMinDuration.getValue());
            distribution.setAverageValue((int) spnSegmentAvgDuration.getValue());
            distribution.setMaxValue((int) spnSegmentMaxDuration.getValue());
        } else if (elem instanceof Node) {
            ((Node) elem).setName(txtNodeName.getText());
        }
    }

    public void editBusRoute(BusRoute route) {
        hideEditPanels();

        txtCircuitName.setText(route.getName());
        ckbCircuitIsLoop.setSelected(route.getIsLoop());
        ckbCircuitIsLoop.setEnabled(route.isLoopable());
        pnlEditCircuit.setVisible(true);
        pnlEditSource.setVisible(true);
        spnSourceNumberMaxVehicule.setValue(route.getBusSource().getNumberMaxVehicule());
        spnTimeBeforeFirstVehicule.setValue(route.getBusSource().getTimeBeforeFirstVehicule());
        TriangularDistribution distribution = route.getBusSource().getDistribution();
        spnSourceMinDuration.setValue((int) Math.round(distribution.getMinValue()));
        spnSourceAvgDuration.setValue((int) Math.round(distribution.getAverageValue()));
        spnSourceMaxDuration.setValue((int) Math.round(distribution.getMaxValue()));
    }

    public void saveBusRoute(BusRoute route) {
        route.setName(txtCircuitName.getText());
        route.setIsLoop(ckbCircuitIsLoop.isSelected());

        Source source = route.getBusSource();
        source.setNumberMaxVehicule((int) spnSourceNumberMaxVehicule.getValue());
        source.setTimeBeforeFirstVehicule((int) spnTimeBeforeFirstVehicule.getValue());
        TriangularDistribution distribution = source.getDistribution();
        distribution.setMinValue((int) spnSourceMinDuration.getValue());
        distribution.setAverageValue((int) spnSourceAvgDuration.getValue());
        distribution.setMaxValue((int) spnSourceMaxDuration.getValue());
    }

    public void hideEditPanels() {
        btnDeleteSelected.setVisible(false);
        pnlEditNode.setVisible(false);
        pnlEditSegment.setVisible(false);
        pnlEditSource.setVisible(false);
        pnlEditCircuit.setVisible(false);
        pnlEditRoute.setVisible(false);
    }

    public void setController(Controller controller) {
        this.controller = controller;
        MapPanel pane = (MapPanel) mapPane;
        pane.setDrawer(new MapDrawer(controller.getState()));
    }

    public MainForm() {
        $$$setupUI$$$();
        prepareGUI();

        try {
            imgPause = new ImageIcon(ImageIO.read(getClass().getResource("/bottom_bar_icons/pause-25x25.png")));
            imgStart = new ImageIcon(ImageIO.read(getClass().getResource("/bottom_bar_icons/play-25x25.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        displayTree.setCellRenderer(new ColoredTreeCellRenderer());
        spnSpeed.setValue(100);
        pnlDomainObjects.setVisible(false);
        hideEditPanels();

        mapPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                controller.click(e.getPoint(), mapPane.getWidth(), mapPane.getHeight(), e.getButton() == MouseEvent.BUTTON1);

                ApplicationState state = controller.getState();
                if (state.getCurrentMode() == EditionMode.None) {
                    editElement(state.getSelectedElement());
                } else {
                    hideEditPanels();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                dragOrigin = e.getPoint();
            }
        });
        mapPane.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);

                Point p = e.getPoint();
                int dx = (int) dragOrigin.getX() - e.getX();
                int dy = (int) dragOrigin.getY() - e.getY();
                controller.dragMap(dx, dy, mapPane.getWidth(), mapPane.getHeight());
                dragOrigin = p;
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);

                controller.mouseOver(e.getPoint(), mapPane.getWidth(), mapPane.getHeight());
            }
        });
        btnZoomOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.adjustZoom(false);
            }
        });
        btnZoomIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.adjustZoom(true);
            }
        });
        btnSelection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setEditionMode(EditionMode.None);
            }
        });
        btnCreateNode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setEditionMode(EditionMode.AddNode);
            }
        });
        btnCreateSegment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setEditionMode(EditionMode.AddSegment);
            }
        });
        btnDeleteSelected.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.deleteSelectedElement();
                hideEditPanels();
            }
        });
        btnCreateCircuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setEditionMode(EditionMode.AddBusRoute);
            }
        });
        btnValidate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        btnValidate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ApplicationState state = controller.getState();
                if (state.getCurrentMode() == EditionMode.None) {
                    if (state.getSelectedElement() != null)
                        saveElement(state.getSelectedElement());
                    else if (state.getCurrentBusRoute() != null)
                        saveBusRoute(state.getCurrentBusRoute());
                } else {
                    controller.validate();
                }
            }
        });
        btnPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (controller.getState().getSimulation() != null) {

                    if (timer.isRunning()) {
                        timer.stop();
                    } else {
                        disableAllTools();
                        timer.start();
                    }
                } else {
                    LocalTime startAt = LocalTime.parse(txtStart.getText());
                    LocalTime endsAt = LocalTime.parse(txtEnd.getText());
                    controller.startSimulation(startAt, endsAt);
                    disableAllTools();
                }
            }
        });
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                controller.stopSimulation();
                enableAllTools();
            }
        });
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.restartSimulation();
            }
        });
        displayTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultTreeModel model = (DefaultTreeModel) displayTree.getModel();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) displayTree.getLastSelectedPathComponent();
                if (node == null || model.getRoot() == node) return;

                BusRoute route = (BusRoute) node.getUserObject();
                controller.setCurrentBusRoute(route);

                if (controller.getState().getCurrentMode() == EditionMode.None) {
                    editBusRoute(route);
                }
            }
        });
        spnSegmentMinDuration.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                TriangularDistribution distribution = ((Segment) controller.getState().getSelectedElement()).getDistribution();
                distribution.setMinValue((int) spnSegmentMinDuration.getValue());
            }
        });
        spnSegmentAvgDuration.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                TriangularDistribution distribution = ((Segment) controller.getState().getSelectedElement()).getDistribution();
                distribution.setAverageValue((int) spnSegmentAvgDuration.getValue());
            }
        });
        spnSegmentMaxDuration.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                TriangularDistribution distribution = ((Segment) controller.getState().getSelectedElement()).getDistribution();
                distribution.setMaxValue((int) spnSegmentMaxDuration.getValue());
            }
        });
        txtNodeName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                ((Node) controller.getState().getSelectedElement()).setName(txtNodeName.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                ((Node) controller.getState().getSelectedElement()).setName(txtNodeName.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                ((Node) controller.getState().getSelectedElement()).setName(txtNodeName.getText());
            }
        });

        txtCircuitName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                controller.getState().getCurrentBusRoute().setName(txtCircuitName.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                controller.getState().getCurrentBusRoute().setName(txtCircuitName.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                controller.getState().getCurrentBusRoute().setName(txtCircuitName.getText());
            }
        });
        spnSourceMinDuration.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                TriangularDistribution distribution = controller.getState().getCurrentBusRoute().getBusSource().getDistribution();
                distribution.setMinValue((int) spnSourceMinDuration.getValue());
            }
        });
        spnSourceAvgDuration.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                TriangularDistribution distribution = controller.getState().getCurrentBusRoute().getBusSource().getDistribution();
                distribution.setAverageValue((int) spnSourceAvgDuration.getValue());
            }
        });
        spnSourceMaxDuration.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                TriangularDistribution distribution = controller.getState().getCurrentBusRoute().getBusSource().getDistribution();
                distribution.setMaxValue((int) spnSourceMaxDuration.getValue());
            }
        });

        ckbCircuitIsLoop.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                controller.getState().getCurrentBusRoute().setIsLoop(ckbCircuitIsLoop.isSelected());
            }
        });
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = "Voulez-vous vraiment quitter l'application?";
                String title = "Quitter";
                // display the JOptionPane showConfirmDialog
                int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION)
                {
                    System.exit(0);
                }
            }
        });
    }

    private void createUIComponents() {
        mapPane = new MapPanel();
        menuBar = getMenuBar();
    }

    private void prepareGUI() {
        mainFrame = new JFrame("MainForm");
        mainFrame.setTitle("SimulatHeures");
        mainFrame.setContentPane(contentPane);
        mainFrame.setJMenuBar(menuBar);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);

        new Timer(16, e -> mainFrame.repaint()).start();

        initComponents();
    }

    private void initComponents() {
        // Menu 2
        btnFile.addActionListener(e -> btnFileActionPerformed(e));
        btnOpen.addActionListener(e -> btnOpenActionPerformed(e));
        btnSave.addActionListener(e -> btnSaveActionPerformed(e));
        btnClose.addActionListener(e -> btnCloseActionPerformed(e));
        btnConfiguation.addActionListener(e -> btnConfiguationActionPerformed(e));
        btnUndo.addActionListener(e -> btnUndoActionPerformed(e));
        btnRedo.addActionListener(e -> btnRedoActionPerformed(e));
        // Tools
        btnSelection.addActionListener(e -> btnSelectionActionPerformed(e));
        btnCreateNode.addActionListener(e -> btnCreateNodePerformed(e));
        btnCreateSegment.addActionListener(e -> btnCreateSegmentPerformed(e));
        btnCreateCircuit.addActionListener(e -> btnCreateCircuitActionPerformed(e));
        btnCreateRoute.addActionListener(e -> btnCreateRouteActionPerformed(e));
        // Display menu
        btnCircuits.addActionListener(e -> btnCircuitsActionPerformed(e));
        btnRoutes.addActionListener(e -> btnRoutesActionPerformed(e));
        btnStatistics.addActionListener(e -> btnStatisticsActionPerformed(e));
        // Simulation menu
        btnStart.addActionListener(e -> btnStartActionPerformed(e));
        btnPlay.addActionListener(e -> btnPlayActionPerformed(e));
        btnStop.addActionListener(e -> btnStopActionPerformed(e));
        // Simulation display
        ckbDisplay1.addActionListener(e -> ckbDisplay1ActionPerformed(e));
        chkDisplay2.addActionListener(e -> ckbDisplay2ActionPerformed(e));
        chkDisplay3.addActionListener(e -> ckbDisplay3ActionPerformed(e));
        btnZoomOut.addActionListener(e -> btnZoomOutActionPerformed(e));
        btnZoomIn.addActionListener(e -> btnZoomInActionPerformed(e));
    }

    // Menu 2

    private void btnFileActionPerformed(ActionEvent evt) {

    }

    private void btnOpenActionPerformed(ActionEvent evt) {

    }

    private void btnSaveActionPerformed(ActionEvent evt) {

    }

    private void btnCloseActionPerformed(ActionEvent evt) {

    }

    private void btnConfiguationActionPerformed(ActionEvent evt) {

    }

    private void btnUndoActionPerformed(ActionEvent evt) {

    }

    private void btnRedoActionPerformed(ActionEvent evt) {

    }

    // Tools

    private void btnSelectionActionPerformed(ActionEvent evt) {

    }

    private void btnCreateNodePerformed(ActionEvent evt) {

    }

    private void btnCreateSegmentPerformed(ActionEvent evt) {

    }

    private void btnCreateCircuitActionPerformed(ActionEvent evt) {

    }

    private void btnCreateRouteActionPerformed(ActionEvent evt) {

    }

    // Display menu

    private void btnCircuitsActionPerformed(ActionEvent evt) {
        pnlDomainObjects.setVisible(!pnlDomainObjects.isVisible());
        lblTitleSection.setText("Circuits");
        controller.setEditionMode(EditionMode.None);
        btnSelection.setSelected(true);
    }

    private void btnRoutesActionPerformed(ActionEvent evt) {

    }

    private void btnStatisticsActionPerformed(ActionEvent evt) {

    }

    // Simulation menu

    private void btnStartActionPerformed(ActionEvent evt) {

    }

    private void btnPlayActionPerformed(ActionEvent evt) {

    }

    private void btnStopActionPerformed(ActionEvent evt) {

    }

    // Simulation display

    private void ckbDisplay1ActionPerformed(ActionEvent evt) {

    }

    private void ckbDisplay2ActionPerformed(ActionEvent evt) {

    }

    private void ckbDisplay3ActionPerformed(ActionEvent evt) {

    }

    private void btnZoomOutActionPerformed(ActionEvent evt) {

    }

    private void btnZoomInActionPerformed(ActionEvent evt) {

    }

    private JMenuBar getMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu menu1 = new JMenu("Fichier");
        JMenu menu2 = new JMenu("Edition");
        JMenu menu3 = new JMenu("Affichage");
        JMenu menu4 = new JMenu("Outils");
        JMenu menu5 = new JMenu("?");

        bar.add(menu1);
        bar.add(menu2);
        bar.add(menu3);
        bar.add(menu4);
        bar.add(menu5);

        return bar;
    }

    private void disableAllTools(){
        btnSelection.setEnabled(false);
        btnCreateNode.setEnabled(false);
        btnCreateSegment.setEnabled(false);
        btnCreateCircuit.setEnabled(false);
        btnCreateRoute.setEnabled(false);

        btnCircuits.setEnabled(false);
        btnRoutes.setEnabled(false);
        btnStatistics.setEnabled(false);

        btnValidate.setEnabled(false);
    }

    private void enableAllTools(){
        btnSelection.setEnabled(true);
        btnCreateNode.setEnabled(true);
        btnCreateSegment.setEnabled(true);
        btnCreateCircuit.setEnabled(true);
        btnCreateRoute.setEnabled(true);

        btnCircuits.setEnabled(true);
        btnRoutes.setEnabled(true);
        btnStatistics.setEnabled(true);

        btnValidate.setEnabled(true);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setMinimumSize(new Dimension(900, 500));
        contentPane.setPreferredSize(new Dimension(900, 500));
        final JToolBar toolBar1 = new JToolBar();
        toolBar1.setEnabled(true);
        toolBar1.setFloatable(false);
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(toolBar1, gbc);
        btnFile = new JButton();
        btnFile.setIcon(new ImageIcon(getClass().getResource("/new/new-48x48.png")));
        btnFile.setText("");
        btnFile.setToolTipText("Nouveau");
        toolBar1.add(btnFile);
        btnOpen = new JButton();
        btnOpen.setIcon(new ImageIcon(getClass().getResource("/new/open-48x48.png")));
        btnOpen.setText("");
        btnOpen.setToolTipText("Ouvrir");
        toolBar1.add(btnOpen);
        btnSave = new JButton();
        btnSave.setIcon(new ImageIcon(getClass().getResource("/new/save-48x48.png")));
        btnSave.setText("");
        btnSave.setToolTipText("Sauvegarder");
        toolBar1.add(btnSave);
        btnClose = new JButton();
        btnClose.setIcon(new ImageIcon(getClass().getResource("/new/close-48x48.png")));
        btnClose.setText("");
        btnClose.setToolTipText("Fermer");
        toolBar1.add(btnClose);
        btnConfiguation = new JButton();
        btnConfiguation.setIcon(new ImageIcon(getClass().getResource("/new/settings-48x48.png")));
        btnConfiguation.setText("");
        btnConfiguation.setToolTipText("Préférences");
        btnConfiguation.setVerifyInputWhenFocusTarget(false);
        toolBar1.add(btnConfiguation);
        btnUndo = new JButton();
        btnUndo.setIcon(new ImageIcon(getClass().getResource("/new/undo-48x48.png")));
        btnUndo.setText("");
        btnUndo.setToolTipText("Annuler");
        toolBar1.add(btnUndo);
        btnRedo = new JButton();
        btnRedo.setIcon(new ImageIcon(getClass().getResource("/new/redo-48x48.png")));
        btnRedo.setText("");
        btnRedo.setToolTipText("Restaurer");
        toolBar1.add(btnRedo);
        lblError = new JLabel();
        lblError.setForeground(new Color(-4516835));
        lblError.setText("");
        toolBar1.add(lblError);
        lblMessage = new JLabel();
        lblMessage.setText("");
        toolBar1.add(lblMessage);
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        toolBar1.add(spacer1);
        mapPane.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 6, new Insets(0, 0, 0, 0), -1, -1));
        mapPane.setBackground(new Color(-4737097));
        mapPane.setEnabled(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(mapPane, gbc);
        final JToolBar toolBar2 = new JToolBar();
        toolBar2.setVisible(false);
        mapPane.add(toolBar2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        toolBar2.add(panel1);
        ckbDisplay1 = new JCheckBox();
        ckbDisplay1.setText("Affichage 1");
        panel1.add(ckbDisplay1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chkDisplay2 = new JCheckBox();
        chkDisplay2.setText("Affichage 2");
        panel1.add(chkDisplay2, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chkDisplay3 = new JCheckBox();
        chkDisplay3.setText("Affichage 3");
        panel1.add(chkDisplay3, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mapPane.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 5, 3, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pnlDomainObjects = new JPanel();
        pnlDomainObjects.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(pnlDomainObjects, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lblTitleSection = new JLabel();
        lblTitleSection.setText("(section)");
        pnlDomainObjects.add(lblTitleSection, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        displayTree = new JTree();
        displayTree.setForeground(new Color(-12828863));
        pnlDomainObjects.add(displayTree, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(120, 50), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        mapPane.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        lblPosition = new JLabel();
        lblPosition.setForeground(new Color(-15779397));
        lblPosition.setText("[0.0, 0.0]");
        mapPane.add(lblPosition, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnZoomIn = new JButton();
        btnZoomIn.setIcon(new ImageIcon(getClass().getResource("/plus-20x20.png")));
        btnZoomIn.setText("");
        mapPane.add(btnZoomIn, new com.intellij.uiDesigner.core.GridConstraints(2, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(30, 30), 0, false));
        btnZoomOut = new JButton();
        btnZoomOut.setIcon(new ImageIcon(getClass().getResource("/minus-20x20.png")));
        btnZoomOut.setText("");
        mapPane.add(btnZoomOut, new com.intellij.uiDesigner.core.GridConstraints(2, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(30, 30), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        mapPane.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer4 = new com.intellij.uiDesigner.core.Spacer();
        mapPane.add(spacer4, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer5 = new com.intellij.uiDesigner.core.Spacer();
        mapPane.add(spacer5, new com.intellij.uiDesigner.core.GridConstraints(1, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JToolBar toolBar3 = new JToolBar();
        toolBar3.setFloatable(false);
        toolBar3.setOrientation(1);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridheight = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        contentPane.add(toolBar3, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 10, new Insets(0, 0, 0, 0), -1, -1));
        panel3.setVisible(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        contentPane.add(panel3, gbc);
        lblStart = new JLabel();
        lblStart.setText("D :");
        panel3.add(lblStart, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtStart = new JTextField();
        txtStart.setText("05:00");
        panel3.add(txtStart, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), new Dimension(50, -1), 0, false));
        lblEnd = new JLabel();
        lblEnd.setText("F :");
        panel3.add(lblEnd, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtEnd = new JTextField();
        txtEnd.setText("01:00");
        panel3.add(txtEnd, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), new Dimension(50, -1), 0, false));
        btnStart = new JButton();
        btnStart.setEnabled(true);
        btnStart.setIcon(new ImageIcon(getClass().getResource("/bottom_bar_icons/restart-25x25.png")));
        btnStart.setText("");
        btnStart.setToolTipText("Retour au début");
        panel3.add(btnStart, new com.intellij.uiDesigner.core.GridConstraints(0, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnPlay = new JButton();
        btnPlay.setIcon(new ImageIcon(getClass().getResource("/bottom_bar_icons/play-25x25.png")));
        btnPlay.setText("");
        btnPlay.setToolTipText("Jouer");
        panel3.add(btnPlay, new com.intellij.uiDesigner.core.GridConstraints(0, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnStop = new JButton();
        btnStop.setIcon(new ImageIcon(getClass().getResource("/bottom_bar_icons/stop-25x25.png")));
        btnStop.setText("");
        btnStop.setToolTipText("Arrêter");
        panel3.add(btnStop, new com.intellij.uiDesigner.core.GridConstraints(0, 6, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(0, 7, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lblSpeed = new JLabel();
        lblSpeed.setText("Vitesse exec.:");
        panel4.add(lblSpeed, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer6 = new com.intellij.uiDesigner.core.Spacer();
        panel4.add(spacer6, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        spnSpeed = new JSpinner();
        panel4.add(spnSpeed, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, -1), new Dimension(50, -1), new Dimension(50, -1), 0, false));
        lblTime = new JLabel();
        lblTime.setText("0:00");
        panel3.add(lblTime, new com.intellij.uiDesigner.core.GridConstraints(0, 9, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setIcon(new ImageIcon(getClass().getResource("/bottom_bar_icons/clock-25x25.png")));
        label1.setText("");
        panel3.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 8, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setEnabled(false);
        splitPane1.setOrientation(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(splitPane1, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(7, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel5.setVerifyInputWhenFocusTarget(true);
        splitPane1.setRightComponent(panel5);
        btnValidate = new JButton();
        btnValidate.setText("Confirmer");
        panel5.add(btnValidate, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnDeleteSelected = new JButton();
        btnDeleteSelected.setText("Supprimer");
        panel5.add(btnDeleteSelected, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pnlEditNode = new JPanel();
        pnlEditNode.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        pnlEditNode.setVisible(true);
        panel5.add(pnlEditNode, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Noeud");
        pnlEditNode.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Nom :");
        pnlEditNode.add(label3, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        txtNodeName = new JTextField();
        pnlEditNode.add(txtNodeName, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(70, -1), null, 0, false));
        pnlEditSegment = new JPanel();
        pnlEditSegment.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        pnlEditSegment.setVisible(true);
        panel5.add(pnlEditSegment, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Segment");
        pnlEditSegment.add(label4, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Durée");
        pnlEditSegment.add(label5, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnSegmentMinDuration = new JSpinner();
        pnlEditSegment.add(spnSegmentMinDuration, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("min :");
        pnlEditSegment.add(label6, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("moy :");
        pnlEditSegment.add(label7, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("max :");
        pnlEditSegment.add(label8, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnSegmentAvgDuration = new JSpinner();
        pnlEditSegment.add(spnSegmentAvgDuration, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnSegmentMaxDuration = new JSpinner();
        pnlEditSegment.add(spnSegmentMaxDuration, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pnlEditCircuit = new JPanel();
        pnlEditCircuit.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        pnlEditCircuit.setVisible(true);
        panel5.add(pnlEditCircuit, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Circuit");
        pnlEditCircuit.add(label9, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Nom :");
        pnlEditCircuit.add(label10, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtCircuitName = new JTextField();
        pnlEditCircuit.add(txtCircuitName, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(70, -1), null, 0, false));
        ckbCircuitIsLoop = new JCheckBox();
        ckbCircuitIsLoop.setText("Boucle");
        pnlEditCircuit.add(ckbCircuitIsLoop, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pnlEditSource = new JPanel();
        pnlEditSource.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(6, 3, new Insets(0, 0, 0, 0), -1, -1));
        pnlEditSource.setVisible(true);
        panel5.add(pnlEditSource, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Source");
        pnlEditSource.add(label11, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("Fréq.");
        pnlEditSource.add(label12, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnSourceMinDuration = new JSpinner();
        pnlEditSource.add(spnSourceMinDuration, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label13 = new JLabel();
        label13.setText("moy :");
        pnlEditSource.add(label13, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label14 = new JLabel();
        label14.setText("max :");
        pnlEditSource.add(label14, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnSourceAvgDuration = new JSpinner();
        pnlEditSource.add(spnSourceAvgDuration, new com.intellij.uiDesigner.core.GridConstraints(4, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnSourceMaxDuration = new JSpinner();
        pnlEditSource.add(spnSourceMaxDuration, new com.intellij.uiDesigner.core.GridConstraints(5, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label15 = new JLabel();
        label15.setText("1er vehicule :");
        pnlEditSource.add(label15, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnTimeBeforeFirstVehicule = new JSpinner();
        pnlEditSource.add(spnTimeBeforeFirstVehicule, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label16 = new JLabel();
        label16.setText("min :");
        pnlEditSource.add(label16, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label17 = new JLabel();
        label17.setText("Nb vehicule :");
        pnlEditSource.add(label17, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnSourceNumberMaxVehicule = new JSpinner();
        pnlEditSource.add(spnSourceNumberMaxVehicule, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer7 = new com.intellij.uiDesigner.core.Spacer();
        panel5.add(spacer7, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane1.setLeftComponent(panel6);
        btnCircuits = new JButton();
        btnCircuits.setText("Circuits");
        panel6.add(btnCircuits, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(160, -1), new Dimension(160, -1), new Dimension(160, -1), 0, false));
        btnRoutes = new JButton();
        btnRoutes.setText("Itinéraires");
        panel6.add(btnRoutes, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(160, -1), new Dimension(160, -1), new Dimension(160, -1), 0, false));
        btnStatistics = new JButton();
        btnStatistics.setText("Simulations");
        panel6.add(btnStatistics, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(160, -1), new Dimension(160, -1), new Dimension(160, -1), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer8 = new com.intellij.uiDesigner.core.Spacer();
        panel6.add(spacer8, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(6, 1, new Insets(0, 0, 0, 0), -1, -1));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPane.add(panel7, gbc);
        btnCreateSegment = new JToggleButton();
        btnCreateSegment.setText("Segment");
        panel7.add(btnCreateSegment, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(100, -1), new Dimension(105, -1), new Dimension(100, -1), 0, false));
        lblTools = new JLabel();
        lblTools.setOpaque(false);
        lblTools.setText("Outils");
        panel7.add(lblTools, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnCreateRoute = new JToggleButton();
        btnCreateRoute.setText("Itinéraire");
        panel7.add(btnCreateRoute, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(105, 31), new Dimension(100, -1), 0, false));
        btnCreateCircuit = new JToggleButton();
        btnCreateCircuit.setText("Circuit");
        panel7.add(btnCreateCircuit, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(105, -1), new Dimension(100, -1), 0, false));
        btnCreateNode = new JToggleButton();
        btnCreateNode.setText("Noeud");
        panel7.add(btnCreateNode, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(100, -1), new Dimension(105, -1), new Dimension(100, -1), 0, false));
        btnSelection = new JToggleButton();
        btnSelection.setText("Sélection");
        panel7.add(btnSelection, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(105, -1), new Dimension(100, -1), 0, false));
        final JPanel spacer9 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        contentPane.add(spacer9, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}


