package se206_a03.editPanes;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Replace extends JPanel {
	private JTextField textField;
	private JTextField textField_1;
	public Replace() {
		setLayout(null);
		
		JLabel lblChoseReplacingAudio = new JLabel("Choose replacing audio");
		lblChoseReplacingAudio.setBounds(12, 12, 190, 15);
		add(lblChoseReplacingAudio);
		
		textField = new JTextField();
		textField.setBounds(12, 39, 190, 25);
		add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Choose File");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton.setBounds(214, 39, 117, 25);
		add(btnNewButton);
		
		JLabel lblOutputFile = new JLabel("Output File");
		lblOutputFile.setBounds(12, 76, 117, 15);
		add(lblOutputFile);
		
		textField_1 = new JTextField();
		textField_1.setBounds(12, 103, 190, 25);
		add(textField_1);
		textField_1.setColumns(10);
		
		JButton btnSaveFile = new JButton("Save To");
		btnSaveFile.setBounds(214, 103, 117, 25);
		add(btnSaveFile);
	}
}
