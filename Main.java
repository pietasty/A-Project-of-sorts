package se206_a03;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

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
	Download downloads = new Download();
	Edit editTab = new Edit();
	Text textTab = new Text();

	private Main() {
		//set up screen
		this.setTitle("VAMIX - Video Audio Mixer");
		setSize(900, 400);
		this.setMinimumSize(new Dimension(900,500));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		vamixTabs.add("Playback", Playback.getInstance());
		vamixTabs.add("Edit",editTab);
		vamixTabs.add("Text",textTab);
		vamixTabs.add("Download",downloads);
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
