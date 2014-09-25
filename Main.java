package se206_a03;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.Native;

public class Main extends JFrame {
	private static Main instance;
	public File original;
	public static Main getInstance(){
		if (instance == null){
			instance = new Main();
		}
		return instance;
	}
	
	//define frames
	JTabbedPane vamixTabs = new JTabbedPane();

	private Main() {
		// set up screen
		this.setTitle("VAMIX - Video Audio Mixer");
		setSize(900, 400);
		this.setMinimumSize(new Dimension(900, 500));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		vamixTabs.add("Playback", Playback.getInstance());
		vamixTabs.add("Edit", Edit.getInstance());
		vamixTabs.add("Text", Text.getInstance());
		vamixTabs.add("Download", Download.getInstance());
		vamixTabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				//pause the playback video when switching to a new tab
				if (!(vamixTabs.getSelectedComponent().equals(Playback.getInstance()))) {
					Playback.getInstance().pauseVideo();
				}
				//updated the selected file label when switching to a different tab
				if (!vamixTabs.getSelectedComponent().equals(Main.getInstance()) 
						&& original != null) {
					Text.getInstance().filenameLabel.setText("Selected file: "
							+ original.getName());
					Text.getInstance().filenameLabel.setVisible(true);
					Text.getInstance().filenameLabel.setFont(new Font(Font.SANS_SERIF,0,10));
					Edit.getInstance().filenameLabel.setText("Selected file: "
							+ original.getName());
					Edit.getInstance().filenameLabel.setVisible(true);
					Edit.getInstance().filenameLabel.setFont(new Font(Font.SANS_SERIF,0,10));
				}
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
