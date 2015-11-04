package ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainForm {
    private JPanel contentPane;
    private JPanel mapPane;

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


