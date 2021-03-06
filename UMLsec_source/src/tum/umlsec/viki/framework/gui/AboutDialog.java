package tum.umlsec.viki.framework.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class AboutDialog extends JDialog {
    private String title = "About";
    private String product = "UMLsec Tool";
    private String version = "1.0";
    private String revision = "54";
    private String copyright = "Copyright (c) 2001";
    private String comments = " multiline comments \n about product";
    private JPanel contentPane = new JPanel();
    private JLabel prodLabel = new JLabel();
    private JLabel verLabel = new JLabel();
    private JLabel copLabel = new JLabel();
    private JTextArea commentField = new JTextArea();
    private JPanel btnPanel = new JPanel();
    private JButton okButton = new JButton();
    private JLabel image = new JLabel();
    private BorderLayout formLayout = new BorderLayout();
    private GridBagLayout contentPaneLayout = new GridBagLayout();
    private FlowLayout btnPaneLayout = new FlowLayout();

    /** Creates new About Dialog */
    public AboutDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initGUI();
        pack();
    }

    public AboutDialog() {
        super();
        setModal(true);
        initGUI();
        pack();
    }

    /** This method is called from within the constructor to initialize the dialog. */
    private void initGUI() {
        addWindowListener(
            new java.awt.event.WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    closeDialog(evt);
                }
            });
        getContentPane().setLayout(formLayout);
        contentPane.setLayout(contentPaneLayout);
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        prodLabel.setText(product);
        contentPane.add(prodLabel,
            new GridBagConstraints(GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE, GridBagConstraints.REMAINDER, 1,
            0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
        verLabel.setText(version + " revision " + revision);
        contentPane.add(verLabel,
            new GridBagConstraints(GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE, GridBagConstraints.REMAINDER, 1,
            0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
        copLabel.setText(copyright);
        contentPane.add(copLabel,
            new GridBagConstraints(GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE, GridBagConstraints.REMAINDER, 1,
            0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
        commentField.setBackground(getBackground());
        commentField.setForeground(copLabel.getForeground());
        commentField.setFont(copLabel.getFont());
        //commentField.setText(comments);
        commentField.setEditable(false);
        contentPane.add(commentField,
            new GridBagConstraints(GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE, GridBagConstraints.REMAINDER, 3,
            0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));
        //image.setText("[Your image]");
        getContentPane().add(image, BorderLayout.WEST);
        getContentPane().add(contentPane, BorderLayout.CENTER);
        btnPanel.setLayout(btnPaneLayout);
        okButton.setText("OK");
        okButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                    dispose();
                }
            });
        btnPanel.add(okButton);
        getContentPane().add(btnPanel, BorderLayout.SOUTH);
        setTitle(title);
        setResizable(false);
    }

    /** Closes the dialog */
    private void closeDialog(WindowEvent evt) {
        setVisible(false);
        dispose();
    }
}
