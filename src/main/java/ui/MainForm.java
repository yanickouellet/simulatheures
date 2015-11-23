package ui;

import domain.ApplicationState;
import domain.Controller;
import domain.EditionMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
    private JLabel lblProperties;
    private JLabel lblPosition;
    private JLabel lblStart;
    private JLabel lblEnd;
    private JLabel lblSpeed;
    private JLabel lblTime;
    private JLabel lblTitleSection;
    private JPanel pnlDomainObjects;
    private JButton btnSelection;
    private JToolBar tlbBottomBar;
    private JLabel lblError;
    private JLabel lblMessage;
    private JButton btnDeleteSelected;
    private JButton btnValidate;
    private JPanel pnlBottomBar;

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

        btnDeleteSelected.setVisible(state.getSelectedElement() != null);

        mainFrame.repaint();
    }

    public void setController(Controller controller) {
        this.controller = controller;
        MapPanel pane = (MapPanel) mapPane;
        pane.setDrawer(new MapDrawer(controller.getState()));
    }

    public MainForm() {
        prepareGUI();

        pnlDomainObjects.setVisible(false);

        mapPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                controller.click(e.getPoint(), mapPane.getWidth(), mapPane.getHeight());
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
                controller.setControllerMode(EditionMode.None);
            }
        });
        btnCreateNode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setControllerMode(EditionMode.AddNode);
            }
        });
        btnCreateSegment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setControllerMode(EditionMode.AddSegment);
            }
        });
        btnDeleteSelected.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.deleteSelectedElement();
            }
        });
        btnCreateCircuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setControllerMode(EditionMode.AddBusRoute);
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
                controller.validate();
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


