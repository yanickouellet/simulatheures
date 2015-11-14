package ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainForm {
    private JPanel contentPane;
    private JPanel mapPane;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JButton button5;
    private JButton button6;
    private JButton statistiquesButton;
    private JButton itinérairesButton;
    private JButton circuitsButton;
    private JButton button10;
    private JButton button11;
    private JButton button12;
    private JTextField textField1;
    private JTextField textField2;
    private JButton b1Button;
    private JButton b2Button;
    private JButton b3Button;
    private JSpinner spinner1;
    private JTree tree1;
    private JCheckBox checkBox1;
    private JCheckBox checkBox2;
    private JCheckBox checkBox3;
    private JButton button7;
    private JButton button8;

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


