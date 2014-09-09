package se206_a03;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class Main extends JFrame {
		//define frames
		JFrame mainFrame = new JFrame();
		JTabbedPane vamixTabs = new JTabbedPane();
		Download downloads = new Download();
		Playback playTab = new Playback();
		Edit editTab = new Edit();
		Text textTab = new Text();
		
		public Main() {
			//set up screen
			this.setTitle("VAMIX - Video Audio Mixer");
			setSize(600, 350);
			this.setMinimumSize(new Dimension(600,300));
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			JPanel master = new JPanel();
			vamixTabs.add("Playback", playTab);
			vamixTabs.add("Edit",editTab);
			vamixTabs.add("Text",textTab);
			this.getContentPane().add(vamixTabs);
		}
		
		public static void main(String[] args) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
				try {	
					Main play = new Main();
					play.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				}
			});
		}
		
}
