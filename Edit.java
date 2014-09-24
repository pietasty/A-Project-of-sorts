package se206_a03;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;

import se206_a03.editPanes.Extract;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class Edit extends JPanel{
	
	private static Edit instance;
	
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
			}
		});
		buttonPanel.add(replaceAudio);
		
		JButton overlayAudio = new JButton("Overlay Audio");
		overlayAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		buttonPanel.add(overlayAudio);
		
		//create file chooser panel
				JPanel choosePanel = new JPanel();
				choosePanel.setLayout(new BorderLayout());
				choosePanel.setMaximumSize(new Dimension(400,30));
				choosePanel.add(chooser, BorderLayout.WEST); //add choose file
				//set action listener to open up file chooser
				chooser.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent event) {
						//open file chooser in new window
						JFileChooser fileSelector = new JFileChooser();
						fileSelector.showOpenDialog(Edit.this);
						try {
							//get File object of selected file
							Main.getInstance().original = fileSelector.getSelectedFile().getAbsoluteFile();
							//get absolute path (includes name of file) in fileDir
							fileDir = fileSelector.getSelectedFile().getAbsolutePath();
							//set label GUI component
							filenameLabel.setText("Selected file: " + Main.getInstance().original.getName());
							filenameLabel.setVisible(true);
							filenameLabel.setFont(new Font(Font.SANS_SERIF,0,10));
							//get path of file (excluding file name)
							filepath = fileDir.substring(0, fileDir.lastIndexOf(File.separator));
							//set the outputFile (unsaved file) as a dot file of selectedFile
							inputFile = new File(filepath + "/." + Main.getInstance().original.getName());
							Playback.getInstance().playDownloadedVideo(fileDir);
						} catch (NullPointerException e) {
							return; //return since no file was selected
						}
					}			
				});
				
				choosePanel.add(filenameLabel, BorderLayout.EAST);
				filenameLabel.setPreferredSize(new Dimension(250,30));
				videoPanel.add(choosePanel); //add file chooser panel to GUI
	}

}
