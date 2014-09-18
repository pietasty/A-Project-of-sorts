package se206_a03;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BoxLayout;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JEditorPane;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Text extends JPanel{
	public Text() {
		setLayout(new BorderLayout());
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		JPanel topPanel = new JPanel();
		mainPanel.add(topPanel);
		topPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel startLabel = new JLabel("Text at start of video:");
		topPanel.add(startLabel, BorderLayout.NORTH);
		
		JEditorPane startText = new JEditorPane();
		topPanel.add(startText,BorderLayout.CENTER);
		topPanel.setMaximumSize(new Dimension(600,100));
		
		mainPanel.add(new JLabel(" "));
		
		JPanel bottomPanel = new JPanel();
		mainPanel.add(bottomPanel);
		bottomPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel endLabel = new JLabel("Text at end of video:  ");
		bottomPanel.add(endLabel,BorderLayout.NORTH);
		
		JEditorPane endText = new JEditorPane();
		bottomPanel.add(endText,BorderLayout.CENTER);
		bottomPanel.setMaximumSize(new Dimension(600,100));
		
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setMaximumSize(new Dimension(600,45));
		panel.add(new JLabel(" "), BorderLayout.NORTH);
		panel.add(saveButton, BorderLayout.WEST);
		mainPanel.add(panel, BorderLayout.SOUTH);
		
		add(mainPanel, BorderLayout.CENTER);
	}

}
