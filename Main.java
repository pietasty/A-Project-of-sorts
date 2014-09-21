package se206_a03;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.Native;

public class Main extends JFrame {
	private static Main instance;
	
	public static Main getInstance(){
		if (instance == null){
			instance = new Main();
		}
		return instance;
	}
	
	//define frames
	JTabbedPane vamixTabs = new JTabbedPane();
	Edit editTab = new Edit();
	Text textTab = new Text();

	private Main() {
		// set up screen
		this.setTitle("VAMIX - Video Audio Mixer");
		setSize(900, 400);
		this.setMinimumSize(new Dimension(900, 500));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		vamixTabs.add("Playback", Playback.getInstance());
		vamixTabs.add("Edit", editTab);
		vamixTabs.add("Text", textTab);
		vamixTabs.add("Download", Download.getInstance());
		vamixTabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Playback.getInstance().pauseVideo();
			}
		});
		this.getContentPane().add(vamixTabs);
	}

	public static void main(String[] args) {
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main play = Main.getInstance();
					play.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
