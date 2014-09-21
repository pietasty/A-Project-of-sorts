package se206_a03;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.io.IOException;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

public class Playback extends JPanel {
	private static Playback instance;
	
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private EmbeddedMediaPlayer video;
	private JPanel videoPanel;
	private JSlider videoScroller;
	private JLabel videoTimer;
	
	private JButton play;
	private JButton pause;
	private JButton stop;

	private JButton back;
	private JButton forward;
	//records how fast to fast forward or back.
	private int speed;
	private Timer timer;
	
	private JButton mute;
	private JButton sound;
	private JSlider volume;
	
	private JButton chooser;
	private String mediaFile;

	public static Playback getInstance() {
		if (instance == null) {
			instance = new Playback();
		}
		return instance;
	}

	private Playback() {
		addHierarchyBoundsListener(new HierarchyBoundsAdapter() {
			public void ancestorResized(HierarchyEvent e) {
				resize();
			}
		});
		this.setVisible(true);
		setSize(900, 500);
		setLayout(null);
		
		addPlayer();
		addVideoScroller();
		addTimeLabel();
		
		playButton();
		pauseButton();
		stopButton();
		
		backButton();
		forwardButton();
		speed = 0;
		timer = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(video.getTime() == 0 || video.getTime() == video.getLength()){
					speed = 0;
					timer.stop();
				}
				switch (speed){
				case -3:
					video.skip(-5000);
					break;
				case -2:
					video.skip(-2000);
					break;
				case -1:
					video.skip(-1000);
					break;
				case 1:
					video.skip(1000);
					break;
				case 2:
					video.skip(2000);
					break;
				case 3:
					video.skip(5000);
					break;
				default:
				}
			}
		});
		
		muteButton();
		soundButton();
		volumeAdjuster();
		fileChooser();
	}

	private void addPlayer() {
		videoPanel = new JPanel(new BorderLayout());
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		video = mediaPlayerComponent.getMediaPlayer();
		
		videoPanel.add(mediaPlayerComponent, BorderLayout.CENTER);
		videoPanel.setBounds(0, 0, 900, 400);
		videoPanel.setVisible(true);

		add(videoPanel);
	}
	
	private void addVideoScroller(){
		videoScroller = new JSlider(JSlider.HORIZONTAL);
		videoScroller.setValue(0);
		videoScroller.setBounds(0,400,835,25);
		add(videoScroller);
	}
	
	private void addTimeLabel(){
		videoTimer = new JLabel("00:00:00");
		videoTimer.setBounds(835, 400, 60, 25);
		add(videoTimer);
		
		Timer clock = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int time = (int) (video.getTime()/1000);
				videoScroller.setValue(time);
				videoScroller.setMaximum((int)video.getLength()/1000);
				
				if(time == 0){
					videoTimer.setText("00:00:00");
				} else {
					int second = time%60;
					int min = time/60;
					int minute = min%60;
					int hour = min/60;
					String h = Integer.toString(hour);
					String m = Integer.toString(minute);
					String s = Integer.toString(second);
					if(second < 10){
						s="0"+s;
					}
					if(minute < 10){
						m="0"+m;
					}
					if(hour < 10){
						h="0"+h;
					}
					videoTimer.setText(h+":"+m+":"+s);
				}
			}
		});
		clock.start();
	}

	private void playButton() {
		play = new JButton();
		
		setIcon(play,"/se206_a03/icons/play.png");
		
		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.stop();
				if (speed != 0) {
					speed = 0;
				} else if (!(video.isPlaying())) {
					video.pause();
					pause.setVisible(true);
					play.setVisible(false);
				} else {
					video.playMedia(mediaFile);
					pause.setVisible(true);
					play.setVisible(false);
					toggleStopButtons(true);
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
				speed = 0;
				timer.stop();
				video.stop();
				pause.setVisible(false);
				play.setVisible(true);
				toggleStopButtons(false);
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
				if(speed>-3){
					speed--;
				}
				play.setVisible(true);
				pause.setVisible(false);
				timer.start();
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
				if(speed<3){
					speed++;
				}
				play.setVisible(true);
				pause.setVisible(false);
				timer.start();
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
				} else {
					setIcon(sound,"/se206_a03/icons/highsound.png");
				}
			}
		});
		
		volume.setBounds(701,435,150,32);
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
					mediaFile = jfile.getSelectedFile().toString();
					video.playMedia(mediaFile);
					pause.setVisible(true);
					play.setVisible(false);
					play.setEnabled(true);
					toggleStopButtons(true);
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
	
	private void toggleStopButtons(boolean b){
		stop.setEnabled(b);
		back.setEnabled(b);
		forward.setEnabled(b);
		mute.setEnabled(b);
		sound.setEnabled(b);
	}
	
	private void resize(){
		int x = Main.getInstance().getWidth();
		int y = Main.getInstance().getHeight();
		
		play.setLocation((x/2)-34, y-65);
		pause.setLocation((x/2)-34, y-65);
		stop.setLocation((x/2)+2, y-65);
		back.setLocation((x/2)-71, y-65);
		forward.setLocation((x/2)+39, y-65);
		chooser.setLocation(12,y-65);
		sound.setLocation(x-44, y-65);
		mute.setLocation(x-81,y-65);
		volume.setLocation(x-231, y-65);
		videoPanel.setSize(x, y-100);
		videoTimer.setLocation(x-65,y-100);
		videoScroller.setBounds(0, y-100, x-65, 25);
	}
	
	public void playDownloadedVideo(String downloadedFile){
		mediaFile = downloadedFile;
		video.stop();
		video.playMedia(mediaFile);
		pause.setVisible(true);
		play.setVisible(false);
		play.setEnabled(true);
		toggleStopButtons(true);
	}
	
	public void pauseVideo(){
		if (video.isPlaying()){
			video.pause();
			play.setVisible(true);
			pause.setVisible(false);
		}
	}
}
