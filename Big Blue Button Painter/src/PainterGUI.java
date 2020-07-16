import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * A GUI That Paints a given Image to a Big Blue Button Whiteboard. Make sure
 * the bbb tab is the next window in tab order and resolution is 1920*1080 and
 * bbb is set to 100% zoom
 * 
 * @author Ruben Deisenroth, 07/2020
 *
 */
public class PainterGUI extends JFrame {

	private JPanel contentPane;
	private ImageViewer ImageViewer = new ImageViewer();
	private JLabel lblNewLabel;

	// Farben von BBB
	Color schwarz = new Color(0, 0, 0);
	Color weiß = new Color(255, 255, 255);
	Color rot = new Color(255, 0, 0);
	Color orange = new Color(255, 136, 0);
	Color gelbgruen = new Color(204, 255, 0);
	Color hellgruen = new Color(0, 255, 0);
	Color cyan = new Color(0, 255, 255);
	Color dodger_blau = new Color(0, 136, 255);
	Color blau = new Color(0, 0, 255);
	Color violett = new Color(136, 0, 255);
	Color magenta = new Color(255, 0, 255);
	Color silber = new Color(192, 192, 192);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			PainterGUI frame = new PainterGUI();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
//			}
//		});
	}

	/**
	 * Create the frame.
	 */
	public PainterGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("Datei");
		menuBar.add(mnNewMenu);

		JMenuItem mntmNewMenuItem = new JMenuItem("Importieren");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BildImportieren();
			}
		});
		mnNewMenu.add(mntmNewMenuItem);

		JMenu mnNewMenu_1 = new JMenu("Optionen");
		menuBar.add(mnNewMenu_1);

		JCheckBoxMenuItem chckbxmntmNewCheckItem = new JCheckBoxMenuItem("Seitenverhältnis beibehalten");
		chckbxmntmNewCheckItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImageViewer.shouldKeepAspectRatio(chckbxmntmNewCheckItem.isSelected());
			}
		});
		chckbxmntmNewCheckItem.setSelected(true);
		mnNewMenu_1.add(chckbxmntmNewCheckItem);

		JCheckBoxMenuItem chckbxmntmNewCheckItem_1 = new JCheckBoxMenuItem("Schwarz-Weiß Vorschau");
		chckbxmntmNewCheckItem_1.setSelected(true);
		chckbxmntmNewCheckItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImageViewer.drawBlackAndWhite(chckbxmntmNewCheckItem_1.isSelected());
			}
		});
		mnNewMenu_1.add(chckbxmntmNewCheckItem_1);

		JCheckBoxMenuItem chckbxmntmNewCheckItem_2 = new JCheckBoxMenuItem("Nur erlaubte Farben zeigen");
		chckbxmntmNewCheckItem_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxmntmNewCheckItem_2.isSelected()) {
					chckbxmntmNewCheckItem_1.setSelected(false);
					ImageViewer.drawBlackAndWhite(chckbxmntmNewCheckItem_1.isSelected());
					ImageViewer.setAllowedColors(schwarz, weiß, rot, orange, gelbgruen, hellgruen, cyan, dodger_blau, blau, violett, magenta, silber);
				} else {
					ImageViewer.clearAllowedColors();
				}
			}
		});
		mnNewMenu_1.add(chckbxmntmNewCheckItem_2);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		contentPane.add(ImageViewer, BorderLayout.CENTER);

		JButton btnNewButton = new JButton("Draw");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!ImageViewer.hasImage()) {
					JOptionPane.showMessageDialog(null, "No image loaded to draw!");
					return;
				}
//				Robot robot;
				try {
					RobotController rc = new RobotController();
					rc.switchWindow(1);
					Point initPos = MouseInfo.getPointerInfo().getLocation();
					BufferedImage image = ImageViewer.getScaledImage();
					ArrayList<Color> colorsToDraw = new ArrayList<>();
					if (chckbxmntmNewCheckItem_2.isSelected()) {
						colorsToDraw.addAll(Arrays.asList(silber, rot, orange, gelbgruen, hellgruen, cyan, dodger_blau, blau, violett, magenta, weiß, schwarz));
					} else {
						colorsToDraw.add(schwarz);
					}
					String[] ColorNames = { "silber", "rot", "orange", "gelbgruen", "hellgruen", "cyan", "dodger_blau", "blau", "violett", "magenta", "weiß", "schwarz" };
					for (int i = 0; i < colorsToDraw.size(); i++) {
						if (!colorsToDraw.isEmpty()) {
							rc.selectColor(ColorNames[i]);
						}
						Color currentColor = colorsToDraw.get(i);
						if (currentColor == weiß) {
							continue;
						}
						for (int y = 0; y < image.getHeight(); y++) {
							int hlinepxcnt = -1;
							int prevpx = -1;
							for (int x = 0; x < image.getWidth(); x++) {
								final int pixel = image.getRGB(x, y);
								final int red = (pixel & 0x00ff0000) >> 16;
								final int green = (pixel & 0x0000ff00) >> 8;
								final int blue = pixel & 0x000000ff;
								final int prevred = (prevpx & 0x00ff0000) >> 16;
								final int prevgreen = (prevpx & 0x0000ff00) >> 8;
								final int prevblue = prevpx & 0x000000ff;
//							System.out.println("red: " + red + ", green " + green + ". blue: " + blue);
//							if (red == 0 && green == 0 && blue == 0) {
								// Schwarz
								if (pixel == prevpx) {
									hlinepxcnt++;
									if (x != image.getWidth() - 1)
										continue;
								}

								if (hlinepxcnt > -1 && prevred == currentColor.getRed() && prevgreen == currentColor.getGreen() && prevblue == currentColor.getBlue()) {
									rc.drawLine(initPos.x + x - hlinepxcnt, initPos.y + y, initPos.x + x, initPos.y + y);
								}
								prevpx = pixel;
								hlinepxcnt = 0;
//								rc.click(initPos.x + x, initPos.y + y);
//							}
							}
						}
						Thread.sleep(500);
					}
					rc.switchWindow(1);
				} catch (AWTException | InterruptedException e1) {
					JOptionPane.showMessageDialog(null, "An Error Was thrown: " + e1.getMessage());
					e1.printStackTrace();
				}

			}
		});
		contentPane.add(btnNewButton, BorderLayout.SOUTH);

		lblNewLabel = new JLabel("ETA: ");
		contentPane.add(lblNewLabel, BorderLayout.NORTH);

		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				// This is only called when the user releases the mouse button.
				updateEta();
			}
		});
	}

	/**
	 * Updates the Estimated Time (outdated method)
	 */
	private void updateEta() {
		BufferedImage image = ImageViewer.getScaledImage();
		if (image != null) {
			int eta = 0;
			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {
					final int pixel = image.getRGB(x, y);
					final int red = (pixel & 0x00ff0000) >> 16;
					final int green = (pixel & 0x0000ff00) >> 8;
					final int blue = pixel & 0x000000ff;
//					System.out.println("red: " + red + ", green " + green + ". blue: " + blue);
					if (red == 0 && green == 0 && blue == 0) {
						eta += 10;
					}
				}
			}
			int seconds = (int) (eta / 1000) % 60;
			int minutes = (int) ((eta / (1000 * 60)) % 60);
			int hours = (int) ((eta / (1000 * 60 * 60)) % 24);
			int days = (int) (eta / (1000 * 60 * 60 * 24));
			StringBuilder etaStr = new StringBuilder("ETA: ");
			ArrayList<String> etaArgs = new ArrayList<>();
			if (days != 0) {
				etaArgs.add(days + "d");
			}
			if (hours != 0) {
				etaArgs.add(hours + "h");
			}
			if (minutes != 0) {
				etaArgs.add(minutes + "m");
			}
			if (seconds != 0) {
				etaArgs.add(seconds + "s");
			}
			lblNewLabel.setText("ETA: " + String.join(" ", etaArgs));
		}
	}

	/**
	 * Creates a robot
	 * 
	 * @return
	 * @throws AWTException
	 */
	public static Robot createRobot() throws AWTException {
		Robot robot = new Robot();
		robot.setAutoWaitForIdle(true);
		robot.setAutoDelay(10);
		return robot;
	}

	/**
	 * Wechselt zu anderem Fenster
	 * 
	 * @param tabCount
	 * @param robot
	 */
	public static void switchWindow(int tabCount, Robot robot) {
		robot.keyPress(KeyEvent.VK_ALT);
		for (int i = 0; i < tabCount; i++) {
			robot.keyPress(KeyEvent.VK_TAB);
			robot.keyRelease(KeyEvent.VK_TAB);
			robot.delay(500);
		}
		robot.keyRelease(KeyEvent.VK_ALT);
	}

	/**
	 * Öffnet Bild importieren Dialog und so
	 */
	public void BildImportieren() {
		final JFileChooser chooser = new JFileChooser("Verzeichnis wählen");
//		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
//		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		final int result = chooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File openedFile = chooser.getSelectedFile();
			try {
				var img = ImageIO.read(openedFile);
				ImageViewer.changeImage(img);
				setTitle(openedFile.getName());
				updateEta();
				JOptionPane.showMessageDialog(this, "Image was changed to " + openedFile.getName());

			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Could not open File " + openedFile.getName());
				e.printStackTrace();
			}
			return;
		}
		JOptionPane.showMessageDialog(this, "No image was selected, aborting change...");
	}

}
