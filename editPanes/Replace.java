package se206_a03.editPanes;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import se206_a03.Main;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class Replace extends JPanel {
	private static Replace instance;
	
	private JTextField input;
	private JTextField output;
	
	private String infile;
	private String outfile;
	
	private String defaultlocation;
	
	public static Replace getInstance(){
		if (instance == null){
			instance = new Replace();
		}
		return instance;
	}
	
	private Replace() {
		String file = Main.getInstance().original.getAbsolutePath();
		defaultlocation = file.substring(0,file.lastIndexOf('/') + 1);
		setSize(370,180);
		setLayout(null);
		
		setLabels();
		setChoosers();
		setTextFields();
	}
	
	private void setLabels(){
		JLabel lblOutputFile = new JLabel("Output File");
		lblOutputFile.setBounds(12, 76, 117, 15);
		add(lblOutputFile);
		
		JLabel lblChoseReplacingAudio = new JLabel("Choose replacing audio");
		lblChoseReplacingAudio.setBounds(12, 12, 190, 15);
		add(lblChoseReplacingAudio);
	}
	
	private void setChoosers(){
		JButton btnNewButton = new JButton("Choose File");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfile = new JFileChooser();
				jfile.setCurrentDirectory(new File(defaultlocation));

				int response = jfile.showOpenDialog(null);
				if (response == JFileChooser.APPROVE_OPTION) {
					infile = jfile.getSelectedFile().toString();
					String basename = infile.substring(infile.lastIndexOf('/') + 1, infile.length());
					input.setText(basename);
				}

				jfile.setVisible(true);
			}
		});
		btnNewButton.setBounds(214, 39, 117, 25);
		add(btnNewButton);
		
		
		JButton btnSaveFile = new JButton("Save To");
		btnSaveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfile = new JFileChooser();
				jfile.setCurrentDirectory(new File(defaultlocation));

				int response = jfile.showSaveDialog(null);
				if (response == JFileChooser.APPROVE_OPTION) {
					outfile = jfile.getSelectedFile().toString();
					String basename = outfile.substring(outfile.lastIndexOf('/') + 1, outfile.length());
					output.setText(basename);
				}

				jfile.setVisible(true);
			}
			
		});
		btnSaveFile.setBounds(214, 103, 117, 25);
		add(btnSaveFile);
	}
	
	private void setTextFields(){
		input = new JTextField();
		input.setBounds(12, 39, 190, 25);
		input.setEditable(false);
		add(input);

		output = new JTextField();
		output.setBounds(12, 103, 190, 25);
		add(output);
	}
	
	/**
	 * Checks that user does not override the original file
	 */
	private boolean checkOverrideOriginal() {
		if (Main.getInstance().original.getAbsolutePath().equals(outfile)) {
			return true;
		}
		return false;
	}
	
	public boolean startProcess() {
		// If a name was given but did not use fileChooser then file is
		// stored in default location
		if(outfile == null){
			outfile = defaultlocation + output.getText();
		}
		// If file does not have a file "extension, add one to the end"
		if(!(outfile.charAt(outfile.length()-4) == '.')){
			outfile=outfile+".mp4";
		}
		
		if (input.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(null, "Please give a input filename!","Error!",JOptionPane.ERROR_MESSAGE);
			return false;
		} else if (output.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(null, "Please give a output filename!","Error!",JOptionPane.ERROR_MESSAGE);
			return false;
		} else if (checkOverrideOriginal()) {
			JOptionPane.showMessageDialog(null,"You cannot override the original file!", "Error!",JOptionPane.ERROR_MESSAGE);
			return false;
		} else {
			//Checks if the user wants to override
			File f = new File(outfile);
			if (f.exists()) {
				String basename = outfile.substring(outfile.lastIndexOf('/') + 1, outfile.length());
				int exists = JOptionPane.showConfirmDialog(null, basename+" already exists \nDo you want to override?", "Override?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if (exists != JOptionPane.OK_OPTION){
					return false;
				}
			}
			
			return true;
		}
	}
	
	public String getOutputFile(){
		return outfile;
	}
	
	public String getReplaceFile(){
		return infile;
	}
}
