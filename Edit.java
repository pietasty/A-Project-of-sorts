package se206_a03;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Edit extends JPanel{
	
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private EmbeddedMediaPlayer video;
	
	public Edit() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		//add video preview
		JPanel videoPanel = new JPanel();
		videoPanel.setLayout(new BoxLayout(videoPanel, BoxLayout.Y_AXIS));
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		video = mediaPlayerComponent.getMediaPlayer();

		videoPanel.add(mediaPlayerComponent);
		videoPanel.add(new JLabel(" "));
		videoPanel.setMaximumSize(new Dimension(450,250));
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
			}
		});
		buttonPanel.add(extractAudio);
		
		JButton replaceAudio = new JButton("Replace Audio");
		replaceAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		buttonPanel.add(replaceAudio);
		
		JButton overlayAudio = new JButton("Overlay Audio");
		overlayAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		buttonPanel.add(overlayAudio);
	}

}
