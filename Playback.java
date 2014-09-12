package se206_a03;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSlider;

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class Playback extends JPanel {
	private static Playback instance;
	
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private EmbeddedMediaPlayer video;
	
	private JButton play;
	private JButton pause;
	private JButton stop;
	private JButton back;
	private JButton forward;
	
	private JButton mute;
	private JButton sound;
	private JSlider volume;
	
	private JButton chooser;
	private String mediafile;

	public static Playback getInstance() {
		if (instance == null) {
			instance = new Playback();
		}
		return instance;
	}

	private Playback() {
		this.setVisible(true);
		setSize(900, 500);
		setLayout(null);
		
		addPlayer();
		playButton();
		pauseButton();
		stopButton();
		backButton();
		forwardButton();
		
		muteButton();
		soundButton();
		volumeAdjuster();
		fileChooser();
	}

	private void addPlayer() {
		JPanel videoPanel = new JPanel(new BorderLayout());
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		video = mediaPlayerComponent.getMediaPlayer();

		videoPanel.add(mediaPlayerComponent, BorderLayout.CENTER);
		videoPanel.setBounds(0, 0, 900, 400);
		videoPanel.setVisible(true);

		add(videoPanel);
	}

	private void playButton() {
		play = new JButton();
		
		setIcon(play,"/se206_a03/icons/play.png");
		
		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (video.canPause()){
					video.pause();
					pause.setVisible(true);
					play.setVisible(false);
				} else {
					video.playMedia(mediafile);
					pause.setVisible(true);
					play.setVisible(false);
					toggleButtons(true);
				}
			}
		});
		
		play.setEnabled(false);
		play.setBounds(416, 435, 32, 32);
		add(play);
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
		pause.setBounds(416, 435, 32, 32);
		add(pause);
	}
	
	private void stopButton(){
		stop = new JButton();
		
		setIcon(stop,"/se206_a03/icons/stop.png");
		
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				video.stop();
				pause.setVisible(false);
				play.setVisible(true);
				toggleButtons(false);
			}
		});
		
		stop.setEnabled(false);
		stop.setBounds(452, 435, 32, 32);
		add(stop);
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
		back.setBounds(379, 435, 32, 32);
		add(back);
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
		forward.setBounds(489, 435, 32, 32);
		add(forward);
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
					setIcon(mute,"/se206_a03/icons/highsound.png");
				}
			}
		});
		
		mute.setEnabled(false);
		mute.setBounds(819,435,32,32);
		add(mute);
	}
	
	private void soundButton(){
		sound = new JButton();
		
		setIcon(sound,"/se206_a03/icons/highsound.png");
		
		sound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(volume.isVisible()){
					if(video.getVolume() == 0){
						video.mute(true);
						setIcon(mute,"/se206_a03/icons/highsound.png");
					} else {
						setIcon(mute,"/se206_a03/icons/mute.png");
					}
					volume.setVisible(false);
					mute.setVisible(true);
				} else {
					if (video.isMute()){
						volume.setValue(0);
						video.mute(false);
					} else {
						volume.setValue(video.getVolume());
					}
					volume.setVisible(true);
					mute.setVisible(false);
				}
			}
		});
		
		sound.setEnabled(false);
		sound.setBounds(856, 435, 32, 32);
		add(sound);
	}
	
	private void volumeAdjuster(){
		volume = new JSlider(JSlider.HORIZONTAL,0,100,100);
		volume.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int vol = volume.getValue();
				video.setVolume(vol);
				
				if(vol == 0 ){
					setIcon(sound,"/se206_a03/icons/mute.png");
				} else if (vol > 50){
					setIcon(sound,"/se206_a03/icons/highsound.png");
				} else {
					setIcon(sound,"/se206_a03/icons/lowsound.png");
				}
			}
		});
		
		volume.setBounds(700,435,151,32);
		volume.setVisible(false);
		add(volume);
	}
	
	//TODO needs to check if file is playable or not!
	private void fileChooser(){
		chooser = new JButton("Choose File");
		chooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Opens JFileChooser when button pressed
				JFileChooser jfile = new JFileChooser();

				int response = jfile.showOpenDialog(null);
				if (response == JFileChooser.APPROVE_OPTION) {
					mediafile = jfile.getSelectedFile().toString();
					video.playMedia(mediafile);
					pause.setVisible(true);
					play.setVisible(false);
					play.setEnabled(true);
					toggleButtons(true);
				}

				jfile.setVisible(true);
			}
		});
		chooser.setBounds(12, 435, 116, 32);
		add(chooser);
	}
	
	
	
	private void setIcon(JButton button,String location){
		try {
			Image img = ImageIO.read(getClass().getResource(location));
			button.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}
	}
	
	private void toggleButtons(boolean b){
		stop.setEnabled(b);
		back.setEnabled(b);
		forward.setEnabled(b);
		mute.setEnabled(b);
		sound.setEnabled(b);
	}
}
