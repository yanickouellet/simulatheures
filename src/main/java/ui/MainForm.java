package ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import domain.*;
import domain.network.*;
import domain.network.NetworkElement;
import domain.network.Node;
import domain.network.Segment;
import domain.network.BusRoute;
import domain.simulation.Simulation;
import domain.simulation.StatEntry;
import ui.tree.ColoredMutableTreeNode;
import ui.tree.ColoredTreeCellRenderer;
import util.Strings;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

public class MainForm {
    private JFrame mainFrame;
    private JPanel contentPane;
    private JMenuBar menuBar;
    private JPanel mapPane;
    private JButton btnFile;
    private JButton btnOpen;
    private JButton btnSave;
    private JButton btnClose;
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
    private JButton btnDijkstra;
    private JButton btnImage;
    private JTextField txtRouteName;
    private JPanel pnlStatistics;
    private JTable tblStatistics;
    private JSpinner spnNbSim;
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

    private void setSelectedButton() {
        EditionMode mode = controller.getState().getCurrentMode();

        btnSelection.setSelected(mode == EditionMode.None);
        btnCreateNode.setSelected(mode == EditionMode.AddNode);
        btnCreateSegment.setSelected(mode == EditionMode.AddSegment);
        btnCreateCircuit.setSelected(mode == EditionMode.AddBusRoute);
        btnCreateRoute.setSelected(mode == EditionMode.AddPassengerRoute);
        btnDijkstra.setSelected(mode == EditionMode.Dijkstra);
    }

    private void addBusRoutesToTree() {
        ApplicationState state = controller.getState();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Circuits");
        for (BusRoute r : state.getBusRoutesToShowInTree()) {
            ColoredMutableTreeNode node = new ColoredMutableTreeNode(r, r.getColor());
            root.add(node);
        }
        DefaultTreeModel model = (DefaultTreeModel) displayTree.getModel();
        model.setRoot(root);
    }

    private void addPassengerRoutesToTree() {
        ApplicationState state = controller.getState();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Itinéraires");
        for (PassengerRoute r : state.getNetwork().getPassengerRoutes()) {
            ColoredMutableTreeNode node = new ColoredMutableTreeNode(r, r.getColor());
            root.add(node);
        }
        DefaultTreeModel model = (DefaultTreeModel) displayTree.getModel();
        model.setRoot(root);
    }

    private void showStats() {
        ApplicationState state = controller.getState();
        ArrayList<PassengerRoute> routes = state.getNetwork().getPassengerRoutes();

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnCount(4);
        model.addRow(new Object[]{"Itinéraire", "Temps min", "Temps moyen", "Temps max"});

        int j = 0;
        ArrayList<Double> max = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++)
            max.add(-1d);
        ArrayList<Double> min = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++)
            min.add(Double.MAX_VALUE);
        ArrayList<Double> avg = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++)
            avg.add(0d);

        for (Simulation s : state.getSimulations()) {
            j++;
            model.addRow(new Object[]{"Simulation: " + j});
            for (int i = 0; i < routes.size(); i++) {
                PassengerRoute route = routes.get(i);
                StatEntry entry = s.getStats().get(route);

                model.addRow(new Object[]{route.getName(), Math.round(entry.getMin()), Math.round(entry.getAverage()), Math.round(entry.getMax())});

                if (entry.getMax() > max.get(i))
                    max.set(i, entry.getMax());
                if (entry.getMin() < min.get(i))
                    min.set(i, entry.getMin());
                avg.set(i, avg.get(i) + entry.getAverage());
            }
        }

        if (j > 0) {
            model.addRow(new Object[]{"Total"});
            for (int i = 0; i < max.size(); i++) {
                model.addRow(new Object[]{routes.get(i).getName(), Math.round(min.get(i)), Math.round(avg.get(i) / j), Math.round(max.get(i))});
            }
        }

        tblStatistics.setModel(model);
    }

    public void panelStats() {
        if (!pnlStatistics.isVisible()) {
            pnlStatistics.setVisible(true);
            showStats();
        } else {
            pnlStatistics.setVisible(false);
        }
    }

    public void update() {
        ApplicationState state = controller.getState();

        lblPosition.setText(state.getCurrentPosition().toString() + " Zoom: " + state.getZoomLevel() + " %");
        lblMessage.setText(state.getMessage());
        setSelectedButton();

        btnDeleteSelected.setVisible(state.isDeleteVisible());

        ImageIcon img = timer != null && timer.isRunning() ? imgPause : imgStart;
        btnPlay.setIcon(img);
        if (timer == null || !timer.isRunning())
            enableAllTools();

        pnlDomainObjects.setVisible(state.getOpenedPanel() != OpenedPanel.None);
        switch (state.getOpenedPanel()) {
            case BusRoutes:
                addBusRoutesToTree();
                pnlStatistics.setVisible(false);
                break;
            case PassengerRoutes:
                addPassengerRoutesToTree();
                pnlStatistics.setVisible(false);
                break;
            case Statistics:
                pnlDomainObjects.setVisible(false);
                break;
        }

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

        btnUndo.setEnabled(!state.getPrevStack().isEmpty());
        btnRedo.setEnabled(!state.getNextStack().isEmpty());
    }

    public void pauseSimulation() {
        if (timer != null)
            timer.stop();
    }

    public void editElement(NetworkElement elem) {
        hideEditPanels();

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

    public void editRoute(PassengerRoute pRoute) {
        hideEditPanels();
        pnlEditRoute.setVisible(true);
        TriangularDistribution distribution = pRoute.getDistribution();
        spnRouteMinDuration.setValue((int) Math.round(distribution.getMinValue()));
        spnRouteAvgDuration.setValue((int) Math.round(distribution.getAverageValue()));
        spnRouteMaxDuration.setValue((int) Math.round(distribution.getMaxValue()));

        txtRouteName.setText(pRoute.getName());
        spnTimeBeforeFirstPerson.setValue(pRoute.getTimeBeforeFirst());
        spnRouteNumberMaxPerson.setValue(pRoute.getMaxPersonNumber());
    }

    public void hideEditPanels() {
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
        spnNbSim.setValue(1);
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
                        pnlStatistics.setVisible(false);
                        pnlDomainObjects.setVisible(false);
                    }
                } else {
                    LocalTime startAt = LocalTime.parse(txtStart.getText());
                    LocalTime endsAt = LocalTime.parse(txtEnd.getText());
                    controller.startSimulation((int) spnNbSim.getValue(), startAt, endsAt);
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

                switch (controller.getState().getOpenedPanel()) {
                    case BusRoutes:
                        BusRoute route = (BusRoute) node.getUserObject();
                        controller.setCurrentBusRoute(route);
                        if (controller.getState().getCurrentMode() == EditionMode.None) {
                            editBusRoute(route);
                        }
                        break;
                    case PassengerRoutes:
                        PassengerRoute pRoute = (PassengerRoute) node.getUserObject();
                        controller.setCurrentPassengerRoute(pRoute);
                        if (controller.getState().getCurrentMode() == EditionMode.None) {
                            editRoute(pRoute);
                        }
                        break;
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
        spnTimeBeforeFirstVehicule.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Source src = controller.getState().getCurrentBusRoute().getBusSource();
                src.setTimeBeforeFirstVehicule((int) spnTimeBeforeFirstVehicule.getValue());
            }
        });
        spnSourceNumberMaxVehicule.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Source src = controller.getState().getCurrentBusRoute().getBusSource();
                src.setNumberMaxVehicule((int) spnSourceNumberMaxVehicule.getValue());
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
                if (reply == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        btnDijkstra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setEditionMode(EditionMode.Dijkstra);
            }
        });
        spnRouteMinDuration.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                TriangularDistribution dist = controller.getState().getCurrentPassengerRoute().getDistribution();
                dist.setMinValue((int) spnRouteMinDuration.getValue());
            }
        });
        spnRouteAvgDuration.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                TriangularDistribution dist = controller.getState().getCurrentPassengerRoute().getDistribution();
                dist.setAverageValue((int) spnRouteAvgDuration.getValue());
            }
        });
        txtRouteName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                controller.getState().getCurrentPassengerRoute().setName(txtRouteName.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                controller.getState().getCurrentPassengerRoute().setName(txtRouteName.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                controller.getState().getCurrentPassengerRoute().setName(txtRouteName.getText());
            }
        });
        spnRouteMaxDuration.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                TriangularDistribution dist = controller.getState().getCurrentPassengerRoute().getDistribution();
                dist.setMaxValue((int) spnRouteMaxDuration.getValue());
            }
        });
        spnTimeBeforeFirstPerson.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                controller.getState().getCurrentPassengerRoute().setTimeBeforeFirst((int) spnTimeBeforeFirstPerson.getValue());
            }
        });
        spnRouteNumberMaxPerson.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                controller.getState().getCurrentPassengerRoute().setMaxPersonNumber((int) spnRouteNumberMaxPerson.getValue());
            }
        });
        btnFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private void createUIComponents() {
        mapPane = new MapPanel();
    }

    private void prepareGUI() {
        mainFrame = new JFrame("MainForm");
        mainFrame.setTitle(Strings.DefaultAppTitle);
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
        btnUndo.addActionListener(e -> btnUndoActionPerformed(e));
        btnRedo.addActionListener(e -> btnRedoActionPerformed(e));
        btnImage.addActionListener(e -> btnImageActionPerformed(e));
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

    //region Buttons listeners
    // Menu 2

    private void btnFileActionPerformed(ActionEvent evt) {

    }

    private void btnOpenActionPerformed(ActionEvent evt) {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this.mainFrame) == JFileChooser.APPROVE_OPTION) {
            controller.load(fc.getSelectedFile());
            ((MapPanel) mapPane).setDrawer(new MapDrawer(controller.getState()));
            mainFrame.setTitle(controller.getState().getAppTitle());
            update();
        }
    }

    private void btnSaveActionPerformed(ActionEvent evt) {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this.mainFrame) == JFileChooser.APPROVE_OPTION) {
            controller.save(fc.getSelectedFile());
            mainFrame.setTitle(controller.getState().getAppTitle());
        }
    }

    private void btnCloseActionPerformed(ActionEvent evt) {

    }

    private void btnUndoActionPerformed(ActionEvent evt) {
        controller.undo();
        ((MapPanel) mapPane).setDrawer(new MapDrawer(controller.getState()));
    }

    private void btnRedoActionPerformed(ActionEvent evt) {
        controller.redo();
        ((MapPanel) mapPane).setDrawer(new MapDrawer(controller.getState()));
    }

    private void btnImageActionPerformed(ActionEvent evt) {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this.mainFrame) == JFileChooser.APPROVE_OPTION) {
            controller.loadBackgroundImage(fc.getSelectedFile());
            update();
        }
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
        controller.setOpenedPanel(OpenedPanel.BusRoutes);
        controller.setEditionMode(EditionMode.AddPassengerRoute);
    }

    // Display menu

    private void btnCircuitsActionPerformed(ActionEvent evt) {
        hideEditPanels();
        if (controller.getOpenedPanel() == OpenedPanel.BusRoutes)
            controller.setOpenedPanel(OpenedPanel.None);
        else
            controller.setOpenedPanel(OpenedPanel.BusRoutes);
        controller.setEditionMode(EditionMode.None);
        update();
    }

    private void btnRoutesActionPerformed(ActionEvent evt) {
        hideEditPanels();
        if (controller.getOpenedPanel() == OpenedPanel.PassengerRoutes)
            controller.setOpenedPanel(OpenedPanel.None);
        else
            controller.setOpenedPanel(OpenedPanel.PassengerRoutes);
        controller.setEditionMode(EditionMode.None);
        update();
    }

    private void btnStatisticsActionPerformed(ActionEvent evt) {
        hideEditPanels();
        panelStats();
        if (controller.getOpenedPanel() == OpenedPanel.Statistics) {
            controller.setOpenedPanel(OpenedPanel.None);
        } else {
            controller.setOpenedPanel(OpenedPanel.Statistics);
        }
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

    //endregion


    private void disableAllTools() {
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

    private void enableAllTools() {
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
        gbc.gridwidth = 5;
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
        btnImage = new JButton();
        btnImage.setIcon(new ImageIcon(getClass().getResource("/new/image-48x48.png")));
        btnImage.setText("");
        btnImage.setToolTipText("Image");
        toolBar1.add(btnImage);
        lblError = new JLabel();
        lblError.setForeground(new Color(-4516835));
        lblError.setText("");
        toolBar1.add(lblError);
        lblMessage = new JLabel();
        lblMessage.setText("");
        toolBar1.add(lblMessage);
        final Spacer spacer1 = new Spacer();
        toolBar1.add(spacer1);
        btnValidate = new JButton();
        btnValidate.setEnabled(false);
        btnValidate.setIcon(new ImageIcon(getClass().getResource("/new/check-48x48.png")));
        btnValidate.setText("");
        btnValidate.setToolTipText("Confirmer");
        toolBar1.add(btnValidate);
        mapPane.setLayout(new GridLayoutManager(3, 6, new Insets(0, 0, 0, 0), -1, -1));
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
        mapPane.add(toolBar2, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        toolBar2.add(panel1);
        ckbDisplay1 = new JCheckBox();
        ckbDisplay1.setText("Affichage 1");
        panel1.add(ckbDisplay1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chkDisplay2 = new JCheckBox();
        chkDisplay2.setText("Affichage 2");
        panel1.add(chkDisplay2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chkDisplay3 = new JCheckBox();
        chkDisplay3.setText("Affichage 3");
        panel1.add(chkDisplay3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mapPane.add(panel2, new GridConstraints(0, 5, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pnlDomainObjects = new JPanel();
        pnlDomainObjects.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(pnlDomainObjects, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        displayTree = new JTree();
        displayTree.setForeground(new Color(-12828863));
        pnlDomainObjects.add(displayTree, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(120, 50), null, 0, false));
        final Spacer spacer2 = new Spacer();
        mapPane.add(spacer2, new GridConstraints(1, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        lblPosition = new JLabel();
        lblPosition.setForeground(new Color(-15779397));
        lblPosition.setText("[0.0, 0.0]");
        mapPane.add(lblPosition, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnZoomIn = new JButton();
        btnZoomIn.setIcon(new ImageIcon(getClass().getResource("/plus-20x20.png")));
        btnZoomIn.setText("");
        mapPane.add(btnZoomIn, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(30, 30), 0, false));
        btnZoomOut = new JButton();
        btnZoomOut.setIcon(new ImageIcon(getClass().getResource("/minus-20x20.png")));
        btnZoomOut.setText("");
        mapPane.add(btnZoomOut, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(30, 30), 0, false));
        final Spacer spacer3 = new Spacer();
        mapPane.add(spacer3, new GridConstraints(0, 2, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        mapPane.add(spacer4, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        mapPane.add(spacer5, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JToolBar toolBar3 = new JToolBar();
        toolBar3.setFloatable(false);
        toolBar3.setOrientation(1);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.gridheight = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        contentPane.add(toolBar3, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 12, new Insets(0, 0, 0, 0), -1, -1));
        panel3.setToolTipText("");
        panel3.setVisible(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 5;
        contentPane.add(panel3, gbc);
        lblStart = new JLabel();
        lblStart.setText("D :");
        panel3.add(lblStart, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtStart = new JTextField();
        txtStart.setText("05:00");
        txtStart.setToolTipText("Heure de début");
        panel3.add(txtStart, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), new Dimension(50, -1), 0, false));
        lblEnd = new JLabel();
        lblEnd.setText("F :");
        panel3.add(lblEnd, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtEnd = new JTextField();
        txtEnd.setText("01:00");
        txtEnd.setToolTipText("Heure de fin");
        panel3.add(txtEnd, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), new Dimension(50, -1), 0, false));
        btnStart = new JButton();
        btnStart.setEnabled(true);
        btnStart.setIcon(new ImageIcon(getClass().getResource("/bottom_bar_icons/restart-25x25.png")));
        btnStart.setText("");
        btnStart.setToolTipText("Retour au début");
        panel3.add(btnStart, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnPlay = new JButton();
        btnPlay.setIcon(new ImageIcon(getClass().getResource("/bottom_bar_icons/play-25x25.png")));
        btnPlay.setText("");
        btnPlay.setToolTipText("Jouer");
        panel3.add(btnPlay, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnStop = new JButton();
        btnStop.setIcon(new ImageIcon(getClass().getResource("/bottom_bar_icons/stop-25x25.png")));
        btnStop.setText("");
        btnStop.setToolTipText("Arrêter");
        panel3.add(btnStop, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lblSpeed = new JLabel();
        lblSpeed.setText("Vitesse exec.:");
        panel4.add(lblSpeed, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel4.add(spacer6, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        spnSpeed = new JSpinner();
        spnSpeed.setToolTipText("Vitesse d'exécution");
        panel4.add(spnSpeed, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(70, -1), new Dimension(70, -1), new Dimension(70, -1), 1, false));
        lblTime = new JLabel();
        lblTime.setText("0:00");
        panel3.add(lblTime, new GridConstraints(0, 9, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setIcon(new ImageIcon(getClass().getResource("/bottom_bar_icons/clock-25x25.png")));
        label1.setText("");
        panel3.add(label1, new GridConstraints(0, 8, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnNbSim = new JSpinner();
        panel3.add(spnNbSim, new GridConstraints(0, 11, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(70, -1), new Dimension(70, -1), new Dimension(70, -1), 1, false));
        final JLabel label2 = new JLabel();
        label2.setText("Nb. Simulations");
        panel3.add(label2, new GridConstraints(0, 10, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setEnabled(false);
        splitPane1.setOrientation(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(splitPane1, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(7, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel5.setVerifyInputWhenFocusTarget(true);
        splitPane1.setRightComponent(panel5);
        btnDeleteSelected = new JButton();
        btnDeleteSelected.setText("Supprimer");
        panel5.add(btnDeleteSelected, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pnlEditNode = new JPanel();
        pnlEditNode.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        pnlEditNode.setVisible(true);
        panel5.add(pnlEditNode, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Noeud");
        pnlEditNode.add(label3, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Nom :");
        pnlEditNode.add(label4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        txtNodeName = new JTextField();
        pnlEditNode.add(txtNodeName, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(70, -1), null, 0, false));
        pnlEditSegment = new JPanel();
        pnlEditSegment.setLayout(new GridLayoutManager(4, 4, new Insets(0, 0, 0, 0), -1, -1));
        pnlEditSegment.setVisible(true);
        panel5.add(pnlEditSegment, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Segment");
        pnlEditSegment.add(label5, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Durée");
        pnlEditSegment.add(label6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnSegmentMinDuration = new JSpinner();
        pnlEditSegment.add(spnSegmentMinDuration, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("min :");
        pnlEditSegment.add(label7, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("moy :");
        pnlEditSegment.add(label8, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("max :");
        pnlEditSegment.add(label9, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnSegmentAvgDuration = new JSpinner();
        pnlEditSegment.add(spnSegmentAvgDuration, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnSegmentMaxDuration = new JSpinner();
        pnlEditSegment.add(spnSegmentMaxDuration, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pnlEditCircuit = new JPanel();
        pnlEditCircuit.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        pnlEditCircuit.setVisible(true);
        panel5.add(pnlEditCircuit, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Circuit");
        pnlEditCircuit.add(label10, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Nom :");
        pnlEditCircuit.add(label11, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtCircuitName = new JTextField();
        pnlEditCircuit.add(txtCircuitName, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(70, -1), null, 0, false));
        ckbCircuitIsLoop = new JCheckBox();
        ckbCircuitIsLoop.setText("Boucle");
        pnlEditCircuit.add(ckbCircuitIsLoop, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pnlEditSource = new JPanel();
        pnlEditSource.setLayout(new GridLayoutManager(6, 3, new Insets(0, 0, 0, 0), -1, -1));
        pnlEditSource.setVisible(true);
        panel5.add(pnlEditSource, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("Source");
        pnlEditSource.add(label12, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label13 = new JLabel();
        label13.setText("Fréq.");
        pnlEditSource.add(label13, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnSourceMinDuration = new JSpinner();
        pnlEditSource.add(spnSourceMinDuration, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label14 = new JLabel();
        label14.setText("moy :");
        pnlEditSource.add(label14, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label15 = new JLabel();
        label15.setText("max :");
        pnlEditSource.add(label15, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnSourceAvgDuration = new JSpinner();
        pnlEditSource.add(spnSourceAvgDuration, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnSourceMaxDuration = new JSpinner();
        pnlEditSource.add(spnSourceMaxDuration, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label16 = new JLabel();
        label16.setText("1er vehicule :");
        pnlEditSource.add(label16, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnTimeBeforeFirstVehicule = new JSpinner();
        pnlEditSource.add(spnTimeBeforeFirstVehicule, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label17 = new JLabel();
        label17.setText("min :");
        pnlEditSource.add(label17, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label18 = new JLabel();
        label18.setText("Nb vehicule :");
        pnlEditSource.add(label18, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnSourceNumberMaxVehicule = new JSpinner();
        pnlEditSource.add(spnSourceNumberMaxVehicule, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        panel5.add(spacer7, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        pnlEditRoute = new JPanel();
        pnlEditRoute.setLayout(new GridLayoutManager(7, 3, new Insets(0, 0, 0, 0), -1, -1));
        pnlEditRoute.setVisible(true);
        panel5.add(pnlEditRoute, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label19 = new JLabel();
        label19.setText("Itinéraire");
        pnlEditRoute.add(label19, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label20 = new JLabel();
        label20.setText("Fréq.");
        pnlEditRoute.add(label20, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnRouteMinDuration = new JSpinner();
        pnlEditRoute.add(spnRouteMinDuration, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label21 = new JLabel();
        label21.setText("moy :");
        pnlEditRoute.add(label21, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label22 = new JLabel();
        label22.setText("max :");
        pnlEditRoute.add(label22, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnRouteAvgDuration = new JSpinner();
        pnlEditRoute.add(spnRouteAvgDuration, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnRouteMaxDuration = new JSpinner();
        pnlEditRoute.add(spnRouteMaxDuration, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label23 = new JLabel();
        label23.setText("1er passager :");
        pnlEditRoute.add(label23, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnTimeBeforeFirstPerson = new JSpinner();
        pnlEditRoute.add(spnTimeBeforeFirstPerson, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label24 = new JLabel();
        label24.setText("min :");
        pnlEditRoute.add(label24, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label25 = new JLabel();
        label25.setText("Nb passagers :");
        pnlEditRoute.add(label25, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spnRouteNumberMaxPerson = new JSpinner();
        pnlEditRoute.add(spnRouteNumberMaxPerson, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label26 = new JLabel();
        label26.setText("Nom :");
        pnlEditRoute.add(label26, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtRouteName = new JTextField();
        pnlEditRoute.add(txtRouteName, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(70, -1), null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane1.setLeftComponent(panel6);
        btnCircuits = new JButton();
        btnCircuits.setText("Circuits");
        panel6.add(btnCircuits, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(160, -1), new Dimension(160, -1), new Dimension(160, -1), 0, false));
        btnRoutes = new JButton();
        btnRoutes.setText("Itinéraires");
        panel6.add(btnRoutes, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(160, -1), new Dimension(160, -1), new Dimension(160, -1), 0, false));
        btnStatistics = new JButton();
        btnStatistics.setText("Simulations");
        panel6.add(btnStatistics, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(160, -1), new Dimension(160, -1), new Dimension(160, -1), 0, false));
        final Spacer spacer8 = new Spacer();
        panel6.add(spacer8, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(7, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel7.setPreferredSize(new Dimension(75, 350));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPane.add(panel7, gbc);
        btnCreateSegment = new JToggleButton();
        btnCreateSegment.setAlignmentY(0.0f);
        btnCreateSegment.setIcon(new ImageIcon(getClass().getResource("/tool_icons/segment-45x45.png")));
        btnCreateSegment.setMargin(new Insets(0, 0, 0, 0));
        btnCreateSegment.setText("");
        btnCreateSegment.setToolTipText("Segment");
        panel7.add(btnCreateSegment, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(50, 50), new Dimension(50, 50), new Dimension(50, 50), 0, false));
        lblTools = new JLabel();
        lblTools.setOpaque(false);
        lblTools.setText("Outils");
        panel7.add(lblTools, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnCreateRoute = new JToggleButton();
        btnCreateRoute.setAlignmentY(0.0f);
        btnCreateRoute.setIcon(new ImageIcon(getClass().getResource("/tool_icons/itineraire-45x45.png")));
        btnCreateRoute.setMargin(new Insets(0, 0, 0, 0));
        btnCreateRoute.setText("");
        btnCreateRoute.setToolTipText("Itinéraire");
        panel7.add(btnCreateRoute, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, 50), new Dimension(50, 50), new Dimension(50, 50), 0, false));
        btnCreateCircuit = new JToggleButton();
        btnCreateCircuit.setAlignmentY(0.0f);
        btnCreateCircuit.setIcon(new ImageIcon(getClass().getResource("/tool_icons/circuit-45x45.png")));
        btnCreateCircuit.setMargin(new Insets(0, 0, 0, 0));
        btnCreateCircuit.setText("");
        btnCreateCircuit.setToolTipText("Circuit");
        panel7.add(btnCreateCircuit, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, 50), new Dimension(50, 50), new Dimension(50, 50), 0, false));
        btnCreateNode = new JToggleButton();
        btnCreateNode.setAlignmentY(0.0f);
        btnCreateNode.setIcon(new ImageIcon(getClass().getResource("/tool_icons/noeud-45x45.png")));
        btnCreateNode.setMargin(new Insets(0, 0, 0, 0));
        btnCreateNode.setText("");
        btnCreateNode.setToolTipText("Noeud");
        panel7.add(btnCreateNode, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(50, 50), new Dimension(50, 50), new Dimension(50, 50), 0, false));
        btnSelection = new JToggleButton();
        btnSelection.setAlignmentY(0.0f);
        btnSelection.setHorizontalTextPosition(0);
        btnSelection.setIcon(new ImageIcon(getClass().getResource("/tool_icons/selection-45x45.png")));
        btnSelection.setIconTextGap(4);
        btnSelection.setMargin(new Insets(0, 0, 0, 0));
        btnSelection.setSelected(false);
        btnSelection.setText("");
        btnSelection.setToolTipText("Sélection");
        panel7.add(btnSelection, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, 50), new Dimension(50, 50), new Dimension(50, 50), 0, false));
        btnDijkstra = new JButton();
        btnDijkstra.setAlignmentY(0.0f);
        btnDijkstra.setMargin(new Insets(0, 0, 0, 0));
        btnDijkstra.setText("D");
        btnDijkstra.setToolTipText("Dijkstra");
        panel7.add(btnDijkstra, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, 50), new Dimension(50, 50), new Dimension(50, 50), 0, false));
        pnlStatistics = new JPanel();
        pnlStatistics.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        pnlStatistics.setAutoscrolls(true);
        pnlStatistics.setMinimumSize(new Dimension(330, 54));
        pnlStatistics.setOpaque(false);
        pnlStatistics.setVisible(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(pnlStatistics, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        pnlStatistics.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tblStatistics = new JTable();
        tblStatistics.setAutoCreateRowSorter(true);
        scrollPane1.setViewportView(tblStatistics);
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(btnCreateSegment);
        buttonGroup.add(btnCreateRoute);
        buttonGroup.add(btnCreateCircuit);
        buttonGroup.add(btnCreateNode);
        buttonGroup.add(btnSelection);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}


