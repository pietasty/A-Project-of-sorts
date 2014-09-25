package se206_a03;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingWorker;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JProgressBar;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Download extends JPanel {
	// Instance for Singleton Pattern
	private static Download instance;
	// J Components
	private JButton download;
	private JButton pause;
	private JTextField url;
	private JButton cancel;
	private JCheckBox open;
	private JProgressBar progressBar;
	private DownloadWorker worker;
	private JButton saveTo;
	private JCheckBox play;
	
	private JLabel downloadlabel;
	private JLabel urllabel;
	
	//Zero means new download, while One means continue download
	private int status;
	//Records the location of where the file is saved to
	private String location;
	//The default location (Downloads folder) if the user does not choose a loaction to save
	private final String defaultlocation;
	//Stores the url and the fileName
	private String text;
	private String fileName;

	public static Download getInstance() {
		if (instance == null) {
			instance = new Download();
		}
		return instance;
	}

	/**
	 * Create the panel. This is a bit messy sorry =(
	 */
	private Download() {
		setSize(900, 400);
		setLayout(null);
		//Allows resizing when absolute positioning is used
		addHierarchyBoundsListener(new HierarchyBoundsAdapter() {
			public void ancestorResized(HierarchyEvent e) {
				resize();
			}
		});
		status = 0;
		//Sets the default location to the downloads folder
		defaultlocation = System.getProperty("user.home") + "/Downloads/";
		location = defaultlocation;
		
		//Adds the JComponents
		setLabels();
		downloadButton();
		pauseButton();
		urlTextField();
		setProgressBar();
		setOpenSource();
		cancelButton();
		saveToButton();
		playCheckBox();
	}

	private void setLabels() {
		// Download Label
		downloadlabel  = new JLabel("Download");
		downloadlabel.setFont(new Font("Dialog", Font.BOLD, 20));
		downloadlabel.setSize(112, 24);
		add(downloadlabel);

		// Url Label
		urllabel = new JLabel("Enter URL");
		urllabel.setFont(new Font("Dialog", Font.BOLD, 15));
		urllabel.setSize(82,18);
		add(urllabel);
	}
	
	//Adds the Download Button
	private void downloadButton() {
		download = new JButton("Download");
		download.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Changes what buttons is available to press
				cancel.setEnabled(true);
				download.setVisible(false);
				pause.setVisible(true);
				progressBar.setValue(0);

				text = url.getText();
				
				File f;
				//Checks whether the user has changed the location of where they want to save the file
				if(location.equals(defaultlocation)){
					fileName = text.substring(text.lastIndexOf('/') + 1, text.length());
					f = new File(defaultlocation + fileName);
				} else {
					f = new File(location+fileName);
				}
				
				//Checks if file already exists or not
				if (f.exists()) {
					// Asks user if they want to override or resume download
					String[] option = { "resume download", "override" };
					JComboBox select = new JComboBox(option);
					String warning = "The file " + fileName + " already exists!" + System.lineSeparator() + "Do you want to resume download or override?";
					Object[] obj = { warning, select };
					JOptionPane.showMessageDialog(null, obj, "",JOptionPane.OK_OPTION);
					if (select.getSelectedIndex() == 0) {
						status = 1;
					} else {
						f.delete();
					}
				}
				// executes SwingWorker
				worker = new DownloadWorker();
				worker.execute();
			}
		});
		download.setSize(117,25);
		download.setEnabled(false);
		add(download);
	}
	
	//Adds the pause button
	private void pauseButton(){
		pause = new JButton("Pause");
		pause.addActionListener(new ActionListener() {
			//This stops the download but does not remove the file.
			public void actionPerformed(ActionEvent e) {
				status = 1;
				cancel.setEnabled(false);
				download.setVisible(false);
				pause.setVisible(true);
				worker.cancel(true);
			}
		});
		pause.setSize(117, 25);
		pause.setVisible(false);
		add(pause);
	}
	
	//Adds the text field for url input
	//TODO remove the set text
	private void urlTextField(){
		url = new JTextField();
		url.setSize(345, 19);
		url.setText("http://ccmixter.org/content/Zapac/Zapac_-_Test_Drive.mp3");
		add(url);
	}
	
	//Adds the progressBar
	private void setProgressBar(){
		progressBar = new JProgressBar();
		progressBar.setSize(445,25);
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setStringPainted(true);
		add(progressBar);
	}
	
	//Adds the check box to check if the downloaded file is open source
	private void setOpenSource(){
		open = new JCheckBox("Confirm it's open source");
		open.addActionListener(new ActionListener() {
			//Enables/disables the download button based on is selected or not
			public void actionPerformed(ActionEvent e) {
				if (open.isSelected()) {
					download.setEnabled(true);
				} else {
					download.setEnabled(false);
				}
			}
		});
		open.setSize(214, 23);
		add(open);
	}
	
	//Adds the cancel button
	private void cancelButton(){
		cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			//This stops the download and removes the file
			public void actionPerformed(ActionEvent e) {
				status = 0;
				cancel.setEnabled(false);
				download.setVisible(true);
				pause.setVisible(false);
				worker.cancel(true);
				File f = new File(location+fileName);
				f.delete();
			}

		});
		cancel.setSize(117,25);
		cancel.setEnabled(false);
		add(cancel);
	}
	
	//Adds the save to button
	private void saveToButton(){
		saveTo = new JButton("Save to...");
		saveTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Opens up Jfile chooser
				JFileChooser jfile = new JFileChooser();
				
				text = url.getText();
				fileName = text.substring(text.lastIndexOf('/') + 1,text.length());
				//Sets the location to default location or the last place where a download occurred
				File workingDirectory = new File(location);
				jfile.setCurrentDirectory(workingDirectory);
				
				jfile.setSelectedFile(new File(fileName));
				int response = jfile.showSaveDialog(getParent());
				//Changes the file name and location
				if (response == JFileChooser.APPROVE_OPTION) {
					String file = jfile.getSelectedFile().toString();
					fileName = file.substring(file.lastIndexOf('/') + 1, file.length());
					location = file.substring(0,file.lastIndexOf('/') + 1);
				}

				jfile.setVisible(true);
			}
		});
		saveTo.setSize(117, 25);
		add(saveTo);
		
	}
	
	//Simple check box that allows the user to play the downloaded file after it is downloaded.
	private void playCheckBox(){
		play = new JCheckBox("Play after download");
		play.setSize(181, 23);
		add(play);
	}
	
	
	/**
	 * SwingWorker for the download class My doInBackground returns the exit
	 * status, while the intermediate results is the progress of the download
	 * 
	 */
	class DownloadWorker extends SwingWorker<Integer, Integer> {
		private Process process;
		
		@Override
		protected Integer doInBackground() throws Exception {
			// Wget Command
			ProcessBuilder builder;

			if (status != 0) {
				builder = new ProcessBuilder("wget","-P",location, "-c", text);
			} else {
				builder = new ProcessBuilder("wget","-P",location, text);
			}

			// Sets up the builder and process
			builder.redirectErrorStream(true);
			try {
				process = builder.start();
				InputStream stdout = process.getInputStream();
				BufferedReader stdoutBuffered = new BufferedReader(
						new InputStreamReader(stdout));

				// Reads the output from wget
				String line = null;
				while ((line = stdoutBuffered.readLine()) != null
						&& !isCancelled()) {
					// Does lots of string manipulation to find the progress of download
					if (line.contains("%")) {
						int place = line.indexOf("%");
						String progress = line.substring(place - 3, place);
						progress = progress.trim();
						if (progress.matches("[0-9]+")) {
							if (isCancelled()) {
								process.destroy();
								return null;
							}
							int percent = Integer.parseInt(progress);
							publish(percent);
						}
					}
				}

				stdoutBuffered.close();
			} catch (IOException e) {

			}
			return process.waitFor();
		}

		// Updates the progress bar
		protected void process(List<Integer> chunks) {
			for (Integer i : chunks) {
				progressBar.setValue(i);
			}
		}

		protected void done() {
			// Sets GUI components
			cancel.setEnabled(false);
			download.setVisible(true);
			pause.setVisible(false);
			download.setEnabled(false);
			progressBar.setValue(100);
			open.setSelected(false);

			// Checks if progress is cancelled
			if (isCancelled()) {
				progressBar.setValue(0);
			} else {
				try {
					// If not report to user based on exit status
					switch (get()) {
					case 0:
						JOptionPane.showMessageDialog(null,"Download is complete!!!");
						if (play.isSelected()){
							Playback.getInstance().playDownloadedVideo(location+fileName);
							System.out.println(location+fileName);
						}
						break;
					case 1:
						JOptionPane.showMessageDialog(null,"Generic Error Code");
						break;
					case 2:
						JOptionPane.showMessageDialog(null, "Parse Error");
						break;
					case 3:
						JOptionPane.showMessageDialog(null, "File I/O Error");
						break;
					case 4:
						JOptionPane.showMessageDialog(null, "Network Error");
						break;
					case 5:
						JOptionPane.showMessageDialog(null,"SSL Verification Failure");
						break;
					case 6:
						JOptionPane.showMessageDialog(null,"Username/Password Authentication failure");
						break;
					case 7:
						JOptionPane.showMessageDialog(null, "Protocol Errors");
						break;
					case 8:
						JOptionPane.showMessageDialog(null,"Server issued an Error Response");
						break;
					default:
						JOptionPane.showMessageDialog(null,"Unknown Error has occured");
						break;
					}
					play.setSelected(false);
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//Allows the panel to resize
	private void resize(){
		int x = Main.getInstance().getWidth();
		
		//Math done to set the location of the J Components
		download.setLocation((x/2)+128, 170);
		pause.setLocation((x/2)+128,170);
		url.setLocation((x/2)-100, 44);
		cancel.setLocation((x/2)-200, 170);
		open.setLocation((x/2)-200,97);
		progressBar.setLocation((x/2)-200,133);
		saveTo.setLocation((x/2)+128, 96);
		play.setLocation((x/2)-200, 70);
		downloadlabel.setLocation((x/2)-56, 12);
		urllabel.setLocation((x/2)-200, 44);
	}
}
