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
import java.io.File;
import java.io.IOException;

public class Edit extends JPanel{
	
	private static Edit instance;
	private JButton play = new JButton();
	private JButton pause = new JButton();
	private JButton stop = new JButton();
	private JButton back = new JButton();
	private JButton forward = new JButton();
	private JButton mute = new JButton();
	private JButton sound = new JButton();
	private JProgressBar progressBar = new JProgressBar();
	private File outputFile;
	
	public static Edit getInstance() {
		if (instance == null) {
			instance = new Edit();
		}
		return instance;
	}
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private EmbeddedMediaPlayer video;
	protected JLabel filenameLabel = new JLabel("");
	private JButton chooser = new JButton("Choose file");
	private String fileDir; //absolute path (includes file name
	private String filepath; //path of file without file name
	private File inputFile;
	
	private Edit() {
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
		JButton extractAudio = new JButton("Extract Audio");
		extractAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] options = {"Extract","Cancel"};
				JPanel panel = Extract.getInstance();
				panel.setPreferredSize(new Dimension(420,180));
				int status = -1;
				while (status == -1 ) {
					int n = JOptionPane.showOptionDialog(null, panel, "Extract Audio", JOptionPane.YES_NO_OPTION, JOptionPane.NO_OPTION, null, options, options[0]);
					if (n == 0){
						status = Extract.getInstance().startProcess();
					} else {
						break;
					}
					if (status == 0){
						JOptionPane.showMessageDialog(null, "Audio Extracted Successfully!");
						video.playMedia(Extract.getInstance().getOutputFile());
					} else if (status > 0) {
						JOptionPane.showMessageDialog(null, "Error occurred in extraction");
					}
				}
			}
		});
		buttonPanel.add(extractAudio);
		
		JButton replaceAudio = new JButton("Replace Audio");
		replaceAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String[] options = {"Replace Audio","Cancel"};
				JPanel panel = Replace.getInstance();
				panel.setPreferredSize(new Dimension(370,180));
				int status = -1;
				while (status == -1 ) {
					int n = JOptionPane.showOptionDialog(null, panel, "Replace Audio", JOptionPane.YES_NO_OPTION, JOptionPane.NO_OPTION, null, options, options[0]);
					if (n == 0){
						status = Replace.getInstance().startProcess();
					} else {
						break;
					}
					if (status == 0){
						JOptionPane.showMessageDialog(null, "Audio Replaced Successfully!");
						video.playMedia(Replace.getInstance().getOutputFile());
					} else if (status > 0) {
						JOptionPane.showMessageDialog(null, "Error occurred in Replacement of Audio!");
					}
				}
			}
		});
		buttonPanel.add(replaceAudio);
		
		JButton overlayAudio = new JButton("Overlay Audio");
		overlayAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] options = {"Overlay Audio","Cancel"};
				JPanel panel = OverLay.getInstance();
				panel.setPreferredSize(new Dimension(370,180));
				int status = -1;
				while (status == -1 ) {
					int n = JOptionPane.showOptionDialog(null, panel, "Overlay Audio", JOptionPane.YES_NO_OPTION, JOptionPane.NO_OPTION, null, options, options[0]);
					if (n == 0){
						status = OverLay.getInstance().startProcess();
					} else {
						break;
					}
					if (status == 0){
						JOptionPane.showMessageDialog(null, "Audio Overlayed Successfully!");
						video.playMedia(OverLay.getInstance().getOutputFile());
					} else if (status > 0) {
						JOptionPane.showMessageDialog(null, "Error occurred in Overlaying of Audio!");
					}
				}
			}
		});
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
					// get File object of selected file
					Main.getInstance().original = fileSelector
							.getSelectedFile().getAbsoluteFile();
					// get absolute path (includes name of file) in fileDir
					fileDir = fileSelector.getSelectedFile().getAbsolutePath();
					// set label GUI component
					filenameLabel.setText("Selected file: "
							+ Main.getInstance().original.getName());
					filenameLabel.setVisible(true);
					filenameLabel.setFont(new Font(Font.SANS_SERIF, 0, 10));
					// get path of file (excluding file name)
					filepath = fileDir.substring(0,
							fileDir.lastIndexOf(File.separator));
					// set the outputFile (unsaved file) as a dot file of
					// selectedFile
					inputFile = new File(filepath + "/."
							+ Main.getInstance().original.getName());
					Playback.getInstance().enablePlay();
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
					video.playMedia(outputFile.toString());
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
				video.stop();
				pause.setVisible(false);
				play.setVisible(true);
				toggleStopButtons(false);
			}
		});
	}
	private void muteButton(){
		mute = new JButton();
		
		setIcon(mute,"/se206_a03/icons/mute.png");
		
		mute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(video.isMute()){
					if(video.getVolume() == 0){
						video.setVolume(100);
						setIcon(sound,"/se206_a03/icons/lowsound.png");
					}
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
	
	private void setIcon(JButton button,String location){
		try {
			Image img = ImageIO.read(getClass().getResource(location));
			button.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}
	}
	
	private void toggleStopButtons(boolean b){
		stop.setEnabled(b);
		back.setEnabled(b);
		forward.setEnabled(b);
		mute.setEnabled(b);
		sound.setEnabled(b);
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
}
