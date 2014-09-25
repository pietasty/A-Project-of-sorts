package se206_a03.editPanes;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.SwingWorker;

import se206_a03.Main;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

public class Extract extends JPanel {
	private static Extract instance;
	
	private ExtractWorker extract;
	
	private JTextField output;
	//Stores the output file location if selected from jfile chooser
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
	
	//Stores the location of where the Mainfile 
	private String defaultlocation;
	
	public static Extract getInstance(){
		if(instance == null){
			instance = new Extract();
		}
		return instance;
	}
	
	private Extract(){
		String file = Main.getInstance().original.getAbsolutePath();
		defaultlocation = file.substring(0,file.lastIndexOf('/') + 1);
		setSize(420,180);
		setLayout(null);
		
		createLabels();
		setOutput();
		setCheckBox();
		setTimeTextFields();
		toggleButtons(false);
	}
	
	private void createLabels(){
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
	
	private void setOutput(){
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
					fullname = jfile.getSelectedFile().toString();
					String basename = fullname.substring(fullname.lastIndexOf('/') + 1, fullname.length());
					output.setText(basename);
				}

				jfile.setVisible(true);

			}
		});;
		chooser.setBounds(291, 12, 117, 25);
		add(chooser);

	}
	
	private void setCheckBox(){
		wholeFile = new JCheckBox("Extract audio from the whole file");
		wholeFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(wholeFile.isSelected()){
					toggleButtons(false);
				} else {
					toggleButtons(true);
				}
			}
		});
		wholeFile.setBounds(12, 50, 267, 23);
		wholeFile.setSelected(true);
		add(wholeFile);
	}
	
	private void setTimeTextFields(){
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
	
	private void toggleButtons(boolean b){
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
	
	private void restrictTextField(JTextField jtf){
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
	
	private boolean checkValidTime(){
		if (starthh.getText().length() != 2 ||
				startmm.getText().length() != 2 ||
				startss.getText().length() != 2 ||
				endhh.getText().length() != 2 ||
				endmm.getText().length() != 2 ||
				endss.getText().length() != 2) {
			return false;
		} else if (Integer.parseInt(starthh.getText()) >60 ||
				Integer.parseInt(startmm.getText()) >60 ||
				Integer.parseInt(startss.getText()) >60 ||
				Integer.parseInt(endhh.getText()) >60 ||
				Integer.parseInt(endmm.getText()) >60 ||
				Integer.parseInt(endss.getText()) >60){
			return false;
		} else {
			return true;
		}
	}
	
	private boolean checkValidMath(){
		if (!(wholeFile.isSelected())) {
			if (Integer.parseInt(endhh.getText()) - Integer.parseInt(starthh.getText()) < 0 ||
					Integer.parseInt(endmm.getText()) - Integer.parseInt(startmm.getText()) < 0 ||
					Integer.parseInt(endss.getText()) - Integer.parseInt(startss.getText()) < 0){
				return false;
			}
		}
		return true;
	}
	
	public int startProcess() {
		if (output.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(null, "Please give a output filename!","Error!",JOptionPane.ERROR_MESSAGE);
			return -1;
		} else if (!checkValidTime()){
			JOptionPane.showMessageDialog(null, "Please enter a valid time between 0 and 60 \nand in the correct format","Error!",JOptionPane.ERROR_MESSAGE);
			return -1;
		} else if(!(checkValidMath())) {
			JOptionPane.showMessageDialog(null, "Your math is horrible","Error!",JOptionPane.ERROR_MESSAGE);
			return -1;
		} else {
			String start = "";
			String to = "";
			if(!(wholeFile.isSelected())){
				start = starthh.getText()+":"+startmm.getText()+":"+startss.getText();
				int tohh = Integer.parseInt(endhh.getText()) - Integer.parseInt(starthh.getText());
				int tomm = Integer.parseInt(endmm.getText()) - Integer.parseInt(startmm.getText());
				int toss = Integer.parseInt(endss.getText()) - Integer.parseInt(startss.getText());
				String h = Integer.toString(tohh);
				String m = Integer.toString(tomm);
				String s = Integer.toString(toss);
				if(toss < 10){
					s="0"+s;
				}
				if(tomm < 10){
					m="0"+m;
				}
				if(tohh < 10){
					h="0"+h;
				}
				to = h+":"+m+":"+s;
			}
			
			if(fullname == null){
				fullname = defaultlocation + output.getText();
			}
			if(!(fullname.charAt(fullname.length()-4) == '.')){
				fullname=fullname+".mp3";
			}
			
			extract = new ExtractWorker(start,to);
			extract.execute();
			try {
				return extract.get();
			} catch (InterruptedException | ExecutionException e) {
				return 1;
			}
		}
	}
	
	class ExtractWorker extends SwingWorker<Integer, Void> {
		private Process process;
		private String starttime;
		private String length;
		
		public ExtractWorker(String st, String len){
			starttime = st;
			length = len;
		}
		
		@Override
		protected Integer doInBackground() throws Exception {
			
			ProcessBuilder builder;
			if (wholeFile.isSelected()){
				builder  = new ProcessBuilder("avconv","-i",Main.getInstance().original.getAbsolutePath(),"-ac","2","-vn","-y",fullname);
			} else {
				builder = new ProcessBuilder("avconv","-i",Main.getInstance().original.getAbsolutePath(),"-ss",starttime,"-t",length,"-ac","2","-vn","-y",fullname);
			}
			
			// Sets up the builder and process
			builder.redirectErrorStream(true);
			try {
				process = builder.start();
				InputStream stdout = process.getInputStream();
				BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
				
				String line = null;
				while ((line = stdoutBuffered.readLine()) != null && !isCancelled()) {
					System.out.println(line);
				}

				stdoutBuffered.close();
			} catch (IOException e) {

			}
			
			return process.waitFor();
		}
	}
	
	public String getOutputFile(){
		return fullname;
	}
}
