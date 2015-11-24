package ui;

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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

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
    private JButton btnCreateRoute;
    private JButton btnCreateCircuit;
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
    private JButton btnCreateNode;
    private JButton btnCreateSegment;
    private JLabel lblPosition;
    private JLabel lblStart;
    private JLabel lblEnd;
    private JLabel lblSpeed;
    private JLabel lblTime;
    private JLabel lblTitleSection;
    private JPanel pnlDomainObjects;
    private JButton btnSelection;
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

        lblPosition.setText(state.getCurrentPosition().toString() + " Zoom: " + state.getZoomLevel());
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
            spnSegmentMinDuration.setValue(Math.round(distribution.getMinValue()));
            spnSegmentAvgDuration.setValue(Math.round(distribution.getAverageValue()));
            spnSegmentMaxDuration.setValue(Math.round(distribution.getMaxValue()));
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
            distribution.setMinValue((Integer) spnSegmentMinDuration.getValue());
            distribution.setAverageValue((Integer) spnSegmentAvgDuration.getValue());
            distribution.setMaxValue((Integer) spnSegmentMaxDuration.getValue());
        } else if (elem instanceof Node) {
            ((Node) elem).setName(txtNodeName.getText());
        }
    }

    public void editBusRoute(BusRoute route) {
        txtCircuitName.setText(route.getName());
        ckbCircuitIsLoop.setSelected(route.getIsLoop());
        pnlEditCircuit.setVisible(true);
        pnlEditSource.setVisible(true);
        TriangularDistribution distribution = route.getBusSource().getDistribution();
        spnSourceMinDuration.setValue(Math.round(distribution.getMinValue()));
        spnSourceAvgDuration.setValue(Math.round(distribution.getAverageValue()));
        spnSourceMaxDuration.setValue(Math.round(distribution.getMaxValue()));
    }

    public void saveBusRoute(BusRoute route) {
        route.setName(txtCircuitName.getText());
        route.setIsLoop(ckbCircuitIsLoop.isSelected());

        Source source = route.getBusSource();
        source.setNumberMaxVehicule((Integer) spnSourceNumberMaxVehicule.getValue());
        source.setTimeBeforeFirstVehicule((Integer) spnTimeBeforeFirstVehicule.getValue());
        TriangularDistribution distribution = source.getDistribution();
        distribution.setMinValue((Integer) spnSourceMinDuration.getValue());
        distribution.setAverageValue((Integer) spnSourceAvgDuration.getValue());
        distribution.setMaxValue((Integer) spnSourceMaxDuration.getValue());
    }

    public void hideEditPanels() {
        btnDeleteSelected.setVisible(false);
        pnlEditNode.setVisible(false);
        pnlEditSegment.setVisible(false);
        pnlEditSource.setVisible(false);
        pnlEditCircuit.setVisible(false);
    }

    public void setController(Controller controller) {
        this.controller = controller;
        MapPanel pane = (MapPanel) mapPane;
        pane.setDrawer(new MapDrawer(controller.getState()));
    }

    public MainForm() {
        prepareGUI();

        try {
            imgPause = new ImageIcon(ImageIO.read(getClass().getResource("/bottom_bar_icons/appbar.control.pause-25x25.png")));
            imgStart= new ImageIcon(ImageIO.read(getClass().getResource("/bottom_bar_icons/appbar.control.play-25x25.png")));
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
                controller.click(e.getPoint(), mapPane.getWidth(), mapPane.getHeight());

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
                    saveElement(state.getSelectedElement());
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
                        timer.start();
                    }
                } else {
                    controller.startSimulation();
                }
            }
        });
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.stopSimulation();
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
    }

    private void $$$setupUI$$$() {
        createUIComponents();
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
}


