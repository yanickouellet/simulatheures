package ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainForm {
    private JPanel contentPane;
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
    private JLabel lblMenu;
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

    public static void main(String[] args) {
        final JFrame frame = new JFrame("MainForm");
        frame.setContentPane(new MainForm().contentPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        Timer t = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.repaint();
            }
        });

        t.start();
    }

    private void createUIComponents() {
        mapPane = new MapPanel();
    }
}


