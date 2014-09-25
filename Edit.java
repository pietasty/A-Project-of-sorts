package se206_a03;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.JButton;

import se206_a03.editPanes.Extract;
import se206_a03.editPanes.OverLay;
import se206_a03.editPanes.Replace;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Edit extends JPanel{
	
	private static Edit instance;
	private JButton play = new JButton();
	private JButton pause = new JButton();
	private JButton stop = new JButton();
	private JButton back = new JButton();
	private JButton forward = new JButton();
	private JButton mute = new JButton();
	private JProgressBar progressBar = new JProgressBar();
	private String outputFile;
	
	private JButton extractAudio;
	private JButton replaceAudio;
	private JButton overlayAudio;
	
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private EmbeddedMediaPlayer video;
	protected JLabel filenameLabel = new JLabel("");
	private JButton chooser = new JButton("Choose file");
	
	private ExtractWorker extractWorker;
	private ReplaceWorker replaceWorker;
	private OverLayWorker overlayWorker;
	
	//This timer is used to figure out if the video has an audio track.
	//This is due to the fact that there is a slight delay in when you play the video
	//in that you can't check the audio count straight after you play the video.
	private Timer t;
	//This variable records the Audio track count
	private int audioTrackCount;
	
	public static Edit getInstance() {
		if (instance == null) {
			instance = new Edit();
		}
		return instance;
	}
	
	private Edit() {
		//Timer used to find the number of Audio Tracks in the main file.
		t = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (video.isPlaying()){
					audioTrackCount = video.getAudioTrackCount();
					video.stop();
					t.stop();
				}
			}
		});
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		//add video preview
		JPanel videoPanel = new JPanel();
		videoPanel.setLayout(new BoxLayout(videoPanel, BoxLayout.Y_AXIS));
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		video = mediaPlayerComponent.getMediaPlayer();

		videoPanel.add(mediaPlayerComponent);
		//invoke methods to set up buttons and add to videoPanel
		JPanel playPanel = new JPanel();
		playPanel.setLayout(new BoxLayout(playPanel, BoxLayout.X_AXIS));
		playPanel.setMaximumSize(new Dimension(450,20));
		setupPlaybackButtons();
		playPanel.add(new JLabel("               "));
		addPlaybackButtons(playPanel);
		videoPanel.add(playPanel);
		videoPanel.add(new JLabel(" "));
		videoPanel.setMaximumSize(new Dimension(450,350));
		videoPanel.setVisible(true);

		add(videoPanel);
		
		//add buttons
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 3, 10, 0));
		buttonPanel.setMaximumSize(new Dimension(450,40));
		add(buttonPanel);
		
		//Extract audio button.
		extractAudio = new JButton("Extract Audio");
		extractAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Warns user if it does not have an audio track.
				if (audioTrackCount == 0){
					JOptionPane.showMessageDialog(null, "This File does not have an Audio Track", "Warning!", JOptionPane.WARNING_MESSAGE);
				}
				
				pressStopButton();
				
				//Brings up the extract pop up
				String[] options = {"Extract","Cancel"};
				JPanel panel = Extract.getInstance();
				panel.setPreferredSize(new Dimension(420,180));
				boolean status = false;
				//Checks if user gives valid input or not.
				while (!status) {
					int n = JOptionPane.showOptionDialog(null, panel, "Extract Audio", JOptionPane.YES_NO_OPTION, JOptionPane.NO_OPTION, null, options, options[0]);
					if (n == 0){
						status = Extract.getInstance().startProcess();
					} else {
						break;
					}
					//If valid input, execute extract.
					if(status){
						enableEditButtons(false);
						String fullname = Extract.getInstance().getOutputFile();
						String start = Extract.getInstance().getStartTime();
						String to = Extract.getInstance().getToTime();
						boolean b = Extract.getInstance().checkSelectedWholeFile();
						extractWorker = new ExtractWorker(fullname,b,start,to);
						extractWorker.execute();
					}
				}
			}
		});
		extractAudio.setEnabled(false);
		buttonPanel.add(extractAudio);
		
		//Replace audio button
		replaceAudio = new JButton("Replace Audio");
		replaceAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//warns user if there is not audio track
				if (audioTrackCount == 0){
					JOptionPane.showMessageDialog(null, "This File does not have an Audio Track", "Warning!", JOptionPane.WARNING_MESSAGE);
				}
				
				pressStopButton();
				
				//pop up for replace audio
				String[] options = {"Replace Audio","Cancel"};
				JPanel panel = Replace.getInstance();
				panel.setPreferredSize(new Dimension(370,180));
				boolean status = false;
				//Checks if user gives valid input or not
				while (!status) {
					int n = JOptionPane.showOptionDialog(null, panel, "Replace Audio", JOptionPane.YES_NO_OPTION, JOptionPane.NO_OPTION, null, options, options[0]);
					if (n == 0){
						status = Replace.getInstance().startProcess();
					} else {
						break;
					}
					//If valid input then replace Audio!
					if (status){
						enableEditButtons(false);
						String input = Replace.getInstance().getReplaceFile();
						String output = Replace.getInstance().getOutputFile();
						replaceWorker = new ReplaceWorker(input,output);
						replaceWorker.execute();
					}

				}
			}
		});
		replaceAudio.setEnabled(false);
		buttonPanel.add(replaceAudio);
		
		//Overlay audio button
		overlayAudio = new JButton("Overlay Audio");
		overlayAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Warns user if the file does not have an audio track
				if (audioTrackCount == 0){
					JOptionPane.showMessageDialog(null, "This File does not have an Audio Track", "Warning!", JOptionPane.WARNING_MESSAGE);
				}
				
				pressStopButton();
				
				//brings up the overlay pop up
				String[] options = {"Overlay Audio","Cancel"};
				JPanel panel = OverLay.getInstance();
				panel.setPreferredSize(new Dimension(370,180));
				boolean status = false;
				//Checks for valid input
				while (!status) {
					int n = JOptionPane.showOptionDialog(null, panel, "Overlay Audio", JOptionPane.YES_NO_OPTION, JOptionPane.NO_OPTION, null, options, options[0]);
					if (n == 0){
						status = OverLay.getInstance().startProcess();
					} else {
						break;
					}
					//if valid input then execute the command
					if(status){
						enableEditButtons(false);
						String input = OverLay.getInstance().getOverlayFile();
						String output = OverLay.getInstance().getOutputFile();
						overlayWorker = new OverLayWorker(input,output);
						overlayWorker.execute();
					}
				}
			}
		});
		overlayAudio.setEnabled(false);
		buttonPanel.add(overlayAudio);
		
		// create file chooser panel
		JPanel choosePanel = new JPanel();
		choosePanel.setLayout(new BorderLayout());
		choosePanel.setMaximumSize(new Dimension(450, 30));
		choosePanel.add(chooser, BorderLayout.WEST); // add choose file
		choosePanel.add(new JLabel(" "), BorderLayout.SOUTH);
		// set action listener to open up file chooser
		chooser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				// open file chooser in new window
				JFileChooser fileSelector = new JFileChooser();
				fileSelector.showOpenDialog(Edit.this);
				try {
					if (Main.getInstance().checkFile(fileSelector.getSelectedFile().toString())) {
						// get File object of selected file
						Main.getInstance().original = fileSelector.getSelectedFile().getAbsoluteFile();
						// set label GUI component
						filenameLabel.setText("Selected file: "+ Main.getInstance().original.getName());
						filenameLabel.setVisible(true);
						filenameLabel.setFont(new Font(Font.SANS_SERIF, 0, 10));
						// get path of file (excluding file name)
						Playback.getInstance().enablePlay();
						enableEditButtons(true);
						findNumberOfAudioTrack();
					} else {
						JOptionPane.showMessageDialog(null, "Please select a Video or Audio file", "Error!", JOptionPane.ERROR_MESSAGE);
					}
				} catch (NullPointerException e) {
					return; // return since no file was selected
				}
			}
		});

		choosePanel.add(filenameLabel, BorderLayout.EAST);
		filenameLabel.setPreferredSize(new Dimension(250, 30));
		videoPanel.add(choosePanel); // add file chooser panel to GUI
		JPanel progressPanel = new JPanel();
		progressPanel.setMaximumSize(new Dimension(450,50));
		progressBar.setPreferredSize(new Dimension(450,20));
		progressPanel.add(progressBar);
		add(new JLabel(" "));
		add(progressPanel);
	}

	/**
	 * Playback components are defined here. The Text preview is simpler than Playback
	 * since the preview doesn't require as many features. The fast forward and rewind
	 * only skips for a second, not continuously.
	 */
	private void playButton() {
		play = new JButton();
		
		setIcon(play,"/se206_a03/icons/play.png");
		
		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (video.isPlayable()){
					video.pause();
					pause.setVisible(true);
					play.setVisible(false);
				} else {
					video.playMedia(outputFile);
					pause.setVisible(true);
					play.setVisible(false);
					toggleStopButtons(true);
				}
			}
		});
		
		play.setEnabled(false);
	}
	private void pauseButton(){
		pause = new JButton();
		
		setIcon(pause,"/se206_a03/icons/pause.png");
		
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				video.pause();
				pause.setVisible(false);
				play.setVisible(true);
			}
		});
		
		pause.setVisible(false);
	}
	private void stopButton(){
		stop = new JButton();
		setIcon(stop,"/se206_a03/icons/stop.png");
		
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pressStopButton();
			}
		});
		stop.setEnabled(false);
	}
	private void muteButton(){
		mute = new JButton();
		
		setIcon(mute,"/se206_a03/icons/mute.png");
		
		mute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(video.isMute()){
					video.mute(false);
					setIcon(mute,"/se206_a03/icons/mute.png");
				} else {
					video.mute(true);
					setIcon(mute,"/se206_a03/icons/lowsound.png");
				}
			}
		});
		
		mute.setEnabled(false);
	}
	private void forwardButton(){
		forward = new JButton();
		
		setIcon(forward,"/se206_a03/icons/forward.png");
		
		forward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				video.skip(1000);
			}
		});
		
		forward.setEnabled(false);
	}
	private void backButton(){
		back = new JButton();
		
		setIcon(back,"/se206_a03/icons/back.png");
		
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				video.skip(-1000);
			}
		});
		
		back.setEnabled(false);
	}
	
	/**
	 * This methods changes the icon of a JButton
	 */
	private void setIcon(JButton button,String location){
		try {
			Image img = ImageIO.read(getClass().getResource(location));
			button.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}
	}
	
	/**
	 * This method gets called when we want to stop the video!
	 */
	public void pressStopButton(){
		video.stop();
		pause.setVisible(false);
		play.setVisible(true);
		toggleStopButtons(false);
	}
	
	/**
	 * This method toggle buttons when the video has been
	 * stopped.
	 */
	private void toggleStopButtons(boolean b){
		stop.setEnabled(b);
		back.setEnabled(b);
		forward.setEnabled(b);
		mute.setEnabled(b);
	}
	
	/**
	 * This method toggles on the video buttons when one 
	 * of the edit functionally works.
	 */
	private void enableVideoButtons(){
		play.setEnabled(true);
		play.setVisible(false);
		pause.setVisible(true);
		stop.setEnabled(true);
		back.setEnabled(true);
		forward.setEnabled(true);
		mute.setEnabled(true);
	}
	
	/**
	 * This function enables the edit buttons once an input file has been given
	 */
	public void enableEditButtons(Boolean b){
		extractAudio.setEnabled(b);
		replaceAudio.setEnabled(b);
		overlayAudio.setEnabled(b);
	}
	
	/**
	 * This method takes a JPanel as input and adds all the buttons
	 * in the correct order to the panel.
	 * @param panel
	 */
	public void addPlaybackButtons(JPanel panel) {
		panel.add(play);
		panel.add(pause);
		panel.add(stop);
		panel.add(forward);
		panel.add(back);
		panel.add(mute);
	}
	
	/**
	 * This helper method calls all the methods each button has to set up
	 * their action listeners and icons
	 */
	public void setupPlaybackButtons() {
		playButton();
		pauseButton();
		stopButton();
		muteButton();
		forwardButton();
		backButton();
	}
	
	/**
	 * This function is called when the user puts in a new "main" file
	 * and it determines the number of audio tracks in that file
	 */
	public void findNumberOfAudioTrack(){
		video.playMedia(Main.getInstance().original.getAbsolutePath());
		t.start();
	}
	
	
	
	/**
	 * Swing Worker Class for the extract function
	 */
	class ExtractWorker extends SwingWorker<Integer, Void> {
		private Process process;
		private String fullname;
		private String starttime;
		private String length;
		private boolean wholeFile;

		public ExtractWorker(String output,boolean check, String st, String len ) {
			starttime = st;
			length = len;
			wholeFile = check;
			fullname = output;
		}

		@Override
		protected Integer doInBackground() throws Exception {	
			//The commands
			ProcessBuilder builder;
			if (wholeFile) {
				builder = new ProcessBuilder("avconv", "-i",
						Main.getInstance().original.getAbsolutePath(), "-ac",
						"2", "-vn", "-y", fullname);
			} else {
				builder = new ProcessBuilder("avconv", "-i",
						Main.getInstance().original.getAbsolutePath(), "-ss",
						starttime, "-t", length, "-ac", "2", "-vn", "-y",
						fullname);
			}

			// Sets up the builder and process
			builder.redirectErrorStream(true);
			try {
				process = builder.start();
				InputStream stdout = process.getInputStream();
				BufferedReader stdoutBuffered = new BufferedReader(
						new InputStreamReader(stdout));
				String line = null;
				while ((line = stdoutBuffered.readLine()) != null && !isCancelled()) {
					publish();
				}
				stdoutBuffered.close();
			} catch (IOException e) {
			}
			return process.waitFor();
		}
		
		//Updates progressBar so user knows the process is going on
		protected void process(List<Void> chunks){
			progressBar.setIndeterminate(true);
		}
		
		//Reports to the user if extraction was successful or not
		protected void done() {
			try {
				if (get() == 0){
					JOptionPane.showMessageDialog(null, "Audio Extracted Successfully!");
					outputFile = Extract.getInstance().getOutputFile();
					video.playMedia(outputFile);
					enableVideoButtons();
				} else if (get() > 0) {
					JOptionPane.showMessageDialog(null, "Error occurred in extraction");
				}
			} catch (InterruptedException | ExecutionException e) {
				JOptionPane.showMessageDialog(null, "Error occurred in extraction");
			}
			progressBar.setIndeterminate(false);
			enableEditButtons(true);
		}
	}
	
	
	
	/**
	 * Swing Worker for the Replace Audio function
	 */
	class ReplaceWorker extends SwingWorker<Integer,Void>{
		private Process process;
		private String infile;
		private String outfile;
		
		private ReplaceWorker(String in, String out){
			infile = in;
			outfile = out;
		}
		@Override
		protected Integer doInBackground() throws Exception {
			ProcessBuilder builder;
			//The commands
			builder  = new ProcessBuilder("avconv", "-i", Main.getInstance().original.getAbsolutePath(), 
					"-i" , infile, "-c:v", "copy", "-c:a", "copy", "-map", "0:v", "-map", "1:a" ,"-y", outfile);
			
			// Sets up the builder and process
			builder.redirectErrorStream(true);
			try {
				process = builder.start();
				InputStream stdout = process.getInputStream();
				BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
				String line = null;
				while ((line = stdoutBuffered.readLine()) != null && !isCancelled()) {
					publish();
				}
				stdoutBuffered.close();
			} catch (IOException e) {
			}
			return process.waitFor();
		}
		
		//Updates the progress bar so the user knows that processes are going on
		protected void process(List<Void> chunks){
			progressBar.setIndeterminate(true);
		}
		
		//Reports to the user if Replacement of audio is done correctly or not
		protected void done(){
			try {
				if(get() == 0){
					JOptionPane.showMessageDialog(null, "Audio Replaced Successfully!");
					outputFile = Replace.getInstance().getOutputFile();
					video.playMedia(outputFile);
					enableVideoButtons();
				} else if (get() > 0) {
					JOptionPane.showMessageDialog(null, "Error occurred in Replacement of Audio!");
				}
			} catch (InterruptedException | ExecutionException e) {
				JOptionPane.showMessageDialog(null, "Error occurred in Replacement of Audio!");
			}
			progressBar.setIndeterminate(false);
			enableEditButtons(true);
		}
	}
	
	
	/**
	 * Swing Worker for the Overlay Audio function
	 */
	class OverLayWorker extends SwingWorker<Integer,Void>{
		private Process process;
		private String infile;
		private String outfile;
		
		private OverLayWorker(String in, String out){
			infile = in;
			outfile = out;
		}
		
		@Override
		protected Integer doInBackground() throws Exception {
			ProcessBuilder builder;
			//The command
			builder  = new ProcessBuilder("avconv", "-i", Main.getInstance().original.getAbsolutePath(), 
					"-i" , infile,"-filter_complex", "amix=inputs=2", "-strict","experimental" ,"-y",outfile);
			
			// Sets up the builder and process
			builder.redirectErrorStream(true);
			try {
				process = builder.start();
				InputStream stdout = process.getInputStream();
				BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
				String line = null;
				while ((line = stdoutBuffered.readLine()) != null && !isCancelled()) {
					publish();
				}
				stdoutBuffered.close();
			} catch (IOException e) {
			}
			
			return process.waitFor();
		}
		
		//Updates progress bar so the user knows that a process is running
		protected void process(List<Void> chunks){
			progressBar.setIndeterminate(true);
		}
		
		//Reports to the user based on the result.
		protected void done(){
			try {
				if (get() == 0){
					JOptionPane.showMessageDialog(null, "Audio Overlayed Successfully!");
					outputFile = OverLay.getInstance().getOutputFile();
					video.playMedia(outputFile);
					enableVideoButtons();
				} else if (get() > 0) {
					JOptionPane.showMessageDialog(null, "Error occurred in Overlaying of Audio!");
				}
			} catch (InterruptedException | ExecutionException e) {
				JOptionPane.showMessageDialog(null, "Error occurred in Overlaying of Audio!");
			}
			progressBar.setIndeterminate(false);
			enableEditButtons(true);
		}
	}
}
