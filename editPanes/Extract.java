package se206_a03.editPanes;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import se206_a03.Main;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

public class Extract extends JPanel {
	private static Extract instance;

	private JTextField output;
	// Stores the full file name selected from Jfile chooser
	private String fullname;

	private JButton chooser;
	private JCheckBox wholeFile;

	private JLabel from;
	private JTextField starthh;
	private JTextField startmm;
	private JTextField startss;

	private JLabel to;
	private JTextField endhh;
	private JTextField endmm;
	private JTextField endss;

	private JLabel time;

	// Stores the location of where the Mainfile
	private String defaultlocation;

	public static Extract getInstance() {
		if (instance == null) {
			instance = new Extract();
		}
		return instance;
	}

	private Extract() {
		// Default location of where the save file goes to is where the file
		// being edited is
		String file = Main.getInstance().original.getAbsolutePath();
		defaultlocation = file.substring(0, file.lastIndexOf('/') + 1);
		setSize(420, 180);
		setLayout(null);

		createLabels();
		setOutput();
		setCheckBox();
		setTimeTextFields();
		toggleAdvance(false);
	}

	// Sets up the JLabels
	private void createLabels() {
		JLabel lblOutput = new JLabel("OutputFile");
		lblOutput.setBounds(12, 12, 86, 25);
		add(lblOutput);

		from = new JLabel("Extract From:");
		from.setBounds(12, 81, 111, 15);
		add(from);

		to = new JLabel("To:");
		to.setBounds(209, 81, 70, 15);
		add(to);

		time = new JLabel("Enter the Time in this format: hh:mm:ss");
		time.setBounds(12, 139, 312, 15);
		add(time);
	}

	// Sets up the JTextfield and save to button
	private void setOutput() {
		output = new JTextField();
		output.setBounds(100, 12, 179, 25);
		add(output);

		chooser = new JButton("Save To");
		chooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Opens JFileChooser when button pressed
				JFileChooser jfile = new JFileChooser();
				jfile.setCurrentDirectory(new File(defaultlocation));

				int response = jfile.showSaveDialog(null);
				if (response == JFileChooser.APPROVE_OPTION) {
					// records the fullname
					fullname = jfile.getSelectedFile().toString();
					String basename = fullname.substring(
							fullname.lastIndexOf('/') + 1, fullname.length());
					output.setText(basename);
				}

				jfile.setVisible(true);

			}
		});
		;
		chooser.setBounds(291, 12, 117, 25);
		add(chooser);

	}

	// Sets up the checkbox to see user would want to specify the time
	private void setCheckBox() {
		wholeFile = new JCheckBox("Extract audio from the whole file");
		wholeFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (wholeFile.isSelected()) {
					toggleAdvance(false);
				} else {
					toggleAdvance(true);
				}
			}
		});
		wholeFile.setBounds(12, 50, 267, 23);
		wholeFile.setSelected(true);
		add(wholeFile);
	}

	// Sets up all the text fields for the time inputs.
	private void setTimeTextFields() {
		starthh = new JTextField();
		restrictTextField(starthh);
		starthh.setBounds(12, 108, 30, 19);
		starthh.setText("00");
		add(starthh);

		startmm = new JTextField();
		restrictTextField(startmm);
		startmm.setBounds(54, 108, 30, 19);
		startmm.setText("00");
		add(startmm);

		startss = new JTextField();
		restrictTextField(startss);
		startss.setBounds(100, 108, 30, 19);
		startss.setText("00");
		add(startss);

		endhh = new JTextField();
		restrictTextField(endhh);
		endhh.setBounds(209, 108, 30, 19);
		endhh.setText("00");
		add(endhh);

		endmm = new JTextField();
		restrictTextField(endmm);
		endmm.setBounds(251, 108, 30, 19);
		endmm.setText("00");
		add(endmm);

		endss = new JTextField();
		restrictTextField(endss);
		endss.setBounds(294, 108, 30, 19);
		endss.setText("00");
		add(endss);
	}

	// Toggles on and off the advance options of specifying a time
	private void toggleAdvance(boolean b) {
		from.setVisible(b);
		starthh.setVisible(b);
		startmm.setVisible(b);
		startss.setVisible(b);
		to.setVisible(b);
		endhh.setVisible(b);
		endmm.setVisible(b);
		endss.setVisible(b);
		time.setVisible(b);
	}

	// This restricts the user from only entering numbers and keeping the
	// maximum number of inputs to two numbers
	private void restrictTextField(JTextField jtf) {
		jtf.setDocument(new JTextFieldLimit(2));
		jtf.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (!(Character.isDigit(c)) || (c == KeyEvent.VK_BACK_SLASH)
						|| (c == KeyEvent.VK_DELETE)) {
					e.consume();
				}

			}
		});
	}

	/**
	 * Checks if the user gave a valid format for time
	 */
	private boolean checkValidTime() {
		// Checks they entered two digits.
		if (starthh.getText().length() != 2 || startmm.getText().length() != 2
				|| startss.getText().length() != 2
				|| endhh.getText().length() != 2
				|| endmm.getText().length() != 2
				|| endss.getText().length() != 2) {
			return false;
			// Checks that none of the numbers are greater than 60
		} else if (Integer.parseInt(starthh.getText()) > 60
				|| Integer.parseInt(startmm.getText()) > 60
				|| Integer.parseInt(startss.getText()) > 60
				|| Integer.parseInt(endhh.getText()) > 60
				|| Integer.parseInt(endmm.getText()) > 60
				|| Integer.parseInt(endss.getText()) > 60) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Checks if the user gave the starting time to be less than the ending time
	 */
	private boolean checkValidMath() {
		if (!(wholeFile.isSelected())) {
			if (Integer.parseInt(endhh.getText())
					- Integer.parseInt(starthh.getText()) < 0
					|| Integer.parseInt(endmm.getText())
							- Integer.parseInt(startmm.getText()) < 0
					|| Integer.parseInt(endss.getText())
							- Integer.parseInt(startss.getText()) < 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks that user does not override the original file
	 */
	private boolean checkOverrideOriginal() {
		if (Main.getInstance().original.getAbsolutePath().equals(fullname)) {
			return true;
		}
		return false;
	}

	/**
	 * This process does the error handling for extract
	 * return true if the user gives a valid input
	 */
	public boolean startProcess() {
		// If a name was given but did not use fileChooser then file is
		// stored in default location
		if (fullname == null) {
			fullname = defaultlocation + output.getText();
		}
		// If file does not have a file "extension", add one to the end"
		if (!(fullname.charAt(fullname.length() - 4) == '.')) {
			fullname = fullname + ".mp3";
		}
		
		// Needs to give an output filename.
		if (output.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(null,"Please give a output filename!", "Error!",JOptionPane.ERROR_MESSAGE);
			return false;
			// Checks if format is correct
		} else if (!checkValidTime()) {
			JOptionPane.showMessageDialog(null,"Please enter a valid time between 0 and 60 \nand in the correct format","Error!", JOptionPane.ERROR_MESSAGE);
			return false;
			// Checks Their math is correct.
		} else if (!(checkValidMath())) {
			JOptionPane.showMessageDialog(null, "Your math is horrible","Error!", JOptionPane.ERROR_MESSAGE);
			return false;
		//Checks the user does not override the original file
		} else if (checkOverrideOriginal()) {
			JOptionPane.showMessageDialog(null,"You cannot override the original file!", "Error!",JOptionPane.ERROR_MESSAGE);
			return false;
		} else {
			//Checks if the user wants to override
			File f = new File(fullname);
			if (f.exists()) {
				String basename = fullname.substring(fullname.lastIndexOf('/') + 1, fullname.length());
				int exists = JOptionPane.showConfirmDialog(null, basename+" already exists \nDo you want to override?", "Override?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if (exists != JOptionPane.OK_OPTION){
					return false;
				}
			}
			
			return true;
		}
	}

	// This function returns the output filename
	public String getOutputFile() {
		return fullname;
	}
	
	//Checks if the user wants to download the whole file
	public boolean checkSelectedWholeFile(){
		return wholeFile.isSelected();
	}
	
	//Returns the start time
	public String getStartTime(){
		return starthh.getText() + ":" + startmm.getText() + ":"
				+ startss.getText();
	}
	
	//Returns how long user wants to extract for
	public String getToTime(){
		//Math to figure out the length
		int tohh = Integer.parseInt(endhh.getText())
				- Integer.parseInt(starthh.getText());
		int tomm = Integer.parseInt(endmm.getText())
				- Integer.parseInt(startmm.getText());
		int toss = Integer.parseInt(endss.getText())
				- Integer.parseInt(startss.getText());
		String h = Integer.toString(tohh);
		String m = Integer.toString(tomm);
		String s = Integer.toString(toss);
		if (toss < 10) {
			s = "0" + s;
		}
		if (tomm < 10) {
			m = "0" + m;
		}
		if (tohh < 10) {
			h = "0" + h;
		}
		return h + ":" + m + ":" + s;
	}
}
