package UI;

import javax.swing.*;

public class MainForm {
    private JPanel contentPane;
    private JPanel mapPane;

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainForm");
        frame.setContentPane(new MainForm().contentPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        mapPane = new MapPanel();
    }
}


