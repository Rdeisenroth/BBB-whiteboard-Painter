import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * A fully functional auto scaling image viewer Panel that can efficiently scale
 * pictures to the desired size
 * 
 * @author Ruben Deisenroth, 07/2020
 *
 */
public class ImageViewer extends JPanel {

	private BufferedImage image;
	private BufferedImage scaledImage;
	private Dimension prevDimension = new Dimension(getWidth(), getHeight());
	private boolean keepAspectRatio = true;
	private boolean drawBlackAndWhite = true;
	private ArrayList<Color> allowedColors = new ArrayList<>();

	/**
	 * Creates an empty ImageViewer
	 */
	public ImageViewer() {
		super();
	}

	/**
	 * Creates an empty ImageViewer
	 * 
	 * @param keepAspectRatio if we want to keep the aspect ration when scaling
	 */
	public ImageViewer(boolean keepAspectRatio) {
		super();
		this.keepAspectRatio = keepAspectRatio;
	}

	/**
	 * Creates an Image Viewer
	 * 
	 * @param image the Image to display
	 */
	public ImageViewer(Image image) {
		super();
		changeImage(image);
	}

	/**
	 * Creates an Image Viewer
	 * 
	 * @param image           the Image to display
	 * @param keepAspectRatio if we want to keep the aspect ration when scaling
	 */
	public ImageViewer(Image image, boolean keepAspectRatio) {
		super();
		this.keepAspectRatio = keepAspectRatio;
		changeImage(image);
	}

	/**
	 * Creates an Image Viewer
	 * 
	 * @param imageFile the File to use as the Image
	 * @throws IOException when the file cannot be red
	 */
	public ImageViewer(File imageFile) throws IOException {
		super();
		changeImage(ImageIO.read(imageFile));
	}

	/**
	 * Creates an Image Viewer
	 * 
	 * @param imageFile       the File to use as the Image
	 * @param keepAspectRatio if we want to keep the aspect ration when scaling
	 * @throws IOException when the file cannot be red
	 */
	public ImageViewer(File imageFile, boolean keepAspectRatio) throws IOException {
		super();
		this.keepAspectRatio = keepAspectRatio;
		changeImage(ImageIO.read(imageFile));
	}

	/**
	 * Override the Paint Method
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (image != null) {
			if (prevDimension.width != getWidth() || prevDimension.height != getHeight()) {
				// Do image Resizing
				if (getWidth() == image.getWidth() && getHeight() == image.getHeight()) {
					scaledImage = image;
				} else {
					scaledImage = toCompatibleImage(image, getWidth(), getHeight());
				}
				// Update Prev Dimension
				prevDimension.width = getWidth();
				prevDimension.height = getHeight();
				if (drawBlackAndWhite) {
					scaledImage = toBlackAndWhiteImage(scaledImage);
				} else if (!allowedColors.isEmpty()) {
					scaledImage = toAllowedColors(scaledImage, allowedColors.toArray(new Color[allowedColors.size()]));
				}
			}
			g.drawImage(scaledImage, 0, 0, scaledImage.getWidth(), scaledImage.getHeight(), null);
			System.out.println("Height: " + getHeight() + ", Image Height: " + scaledImage.getHeight());
		}
	}

	/**
	 * Creates an Image that is most likely to be displayable by the gpu and scales
	 * it
	 * 
	 * @param image  the image to convert
	 * @param width  the new image width
	 * @param height the new image height
	 * @return the converted Image
	 */
	private BufferedImage toCompatibleImage(BufferedImage image, int width, int height) {
		// obtain the current system graphical settings
		GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

		int realwidth = width;
		int realheight = height;
		if (keepAspectRatio) {
			double widthRatio = (double) width / (double) image.getWidth();
			double heightRatio = (double) height / (double) image.getHeight();
			double ratio = Math.min(widthRatio, heightRatio);
			realwidth = (int) (image.getWidth() * ratio);
			realheight = (int) (image.getHeight() * ratio);
		}
		/*
		 * if image is already compatible and optimized for current system settings,
		 * simply return it
		 */
		if (image.getColorModel().equals(gfxConfig.getColorModel()) && image.getWidth() == realwidth && image.getHeight() == realheight)
			return image;

		// image is not optimized, so create a new image that is
		BufferedImage newImage = gfxConfig.createCompatibleImage(realwidth, realheight, image.getTransparency());

		// get the graphics context of the new image to draw the old image on
		Graphics2D g2d = newImage.createGraphics();

		// actually draw the image and dispose of context no longer needed
		g2d.drawImage(image, 0, 0, realwidth, realheight, null);
		g2d.dispose();
		// return the new optimized image
		return newImage;
	}

	/**
	 * Converts a given Image to a Black and White only Image
	 * 
	 * @param image the image to convert
	 * @return the converted Image
	 */
	private BufferedImage toBlackAndWhiteImage(BufferedImage image) {
		BufferedImage BlackAndWhite = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
		Graphics2D g2d = BlackAndWhite.createGraphics();
		g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		g2d.dispose();
		return BlackAndWhite;
	}

	/**
	 * Convers an image to an allowed color Pallete
	 * 
	 * @param image  the image to convert
	 * @param colors the colors of the Pallete
	 * @return the converted image
	 */
	private BufferedImage toAllowedColors(BufferedImage image, Color... colors) {
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				final int pixel = image.getRGB(x, y);
				final int red = (pixel & 0x00ff0000) >> 16;
				final int green = (pixel & 0x0000ff00) >> 8;
				final int blue = pixel & 0x000000ff;
				Color closest = colors[0];
				for (Color c : colors) {
//					System.out.println("Color distance1: " + ColorDistance(pixel, c.getRGB()));
//					System.out.println("Color distance2: " + ColorDistance(pixel, closest.getRGB()));
					if (ColorDistance(pixel, c.getRGB()) < ColorDistance(pixel, closest.getRGB())) {
						closest = c;
					}
				}
				image.setRGB(x, y, closest.getRGB());
			}
		}
		return image;
	}

	/**
	 * Calculates color distance
	 * 
	 * @param a Color 1
	 * @param b Color 2
	 * @return the distance
	 */
	public static int ColorDistance(int a, int b) {
		final int r1 = (a & 0x00ff0000) >> 16;
		final int g1 = (a & 0x0000ff00) >> 8;
		final int b1 = a & 0x000000ff;
		final int r2 = (b & 0x00ff0000) >> 16;
		final int g2 = (b & 0x0000ff00) >> 8;
		final int b2 = b & 0x000000ff;
		final int distr = Math.abs(r1 - r2);
		final int distg = Math.abs(g1 - g2);
		final int distb = Math.abs(b1 - b2);
		final int sum = distr + distg + distb;
		return sum;
	}

	/**
	 * Calculates color distance
	 * 
	 * @param a Color 1
	 * @param b Color 2
	 * @return the distance
	 */
	public static int ColorDistance(Color a, Color b) {
		return Math.abs(a.getRed() - b.getRed()) + Math.abs(a.getGreen() - b.getGreen()) + Math.abs(a.getBlue() - b.getBlue());
	}

	/**
	 * Changes the Image
	 * 
	 * @param image the new Image
	 */
	public void changeImage(Image image) {
		BufferedImage input = (BufferedImage) image;
		this.image = this.scaledImage = toCompatibleImage(input, input.getWidth(), input.getHeight());
		this.prevDimension.setSize(input.getWidth(), input.getHeight());
		repaint();
	}

	/**
	 * Clears the Image
	 */
	public void clear() {
		image = null;
		scaledImage = null;
		prevDimension = new Dimension(getWidth(), getHeight());
	}

	/**
	 * Clears the image and resets the Settings
	 */
	public void reset() {
		image = null;
		scaledImage = null;
		prevDimension = new Dimension(getWidth(), getHeight());
		keepAspectRatio = true;
		drawBlackAndWhite = true;
	}

	/**
	 * Change if the aspect ratio should be kept when scaling
	 * 
	 * @param KeepAspectRatio whether it shall now be enabled or not
	 */
	public void shouldKeepAspectRatio(boolean KeepAspectRatio) {
		this.keepAspectRatio = KeepAspectRatio;
		this.prevDimension.height++;
		repaint();
	}

	/**
	 * Change the Black an White Mode
	 * 
	 * @param enable whether it shall now be enabled or not
	 */
	public void drawBlackAndWhite(boolean enable) {
		drawBlackAndWhite = enable;
		this.prevDimension.height++;
		repaint();
	}

	/**
	 * Returns true if an image has been set
	 * 
	 * @return
	 */
	public boolean hasImage() {
		return image != null;
	}

	/**
	 * Gets the currently stored image
	 * 
	 * @return the currently stored image or null if not present
	 */
	public BufferedImage getImage() {
		BufferedImage processedImage = image;
		if (drawBlackAndWhite) {
			processedImage = toBlackAndWhiteImage(processedImage);
		} else if (!allowedColors.isEmpty()) {
			processedImage = toAllowedColors(processedImage, allowedColors.toArray(new Color[allowedColors.size()]));
		}
		return processedImage;
	}

	/**
	 * Gets the currently stored image
	 * 
	 * @return the currently stored image or null if not present
	 */
	public BufferedImage getScaledImage() {
		return scaledImage;
	}

	/**
	 * Gets the currently stored image
	 * 
	 * @return the currently stored image or null if not present
	 */
	public BufferedImage getOriginalImage() {
		return image;
	}

	/**
	 * Resets color Palete
	 */
	public void clearAllowedColors() {
		allowedColors.clear();
		this.prevDimension.height++;
		repaint();
	}

	/**
	 * Sets color Palete
	 * 
	 * @param colors the allowed Colors
	 */
	public void setAllowedColors(Color... colors) {
		allowedColors.clear();
		for (Color c : colors) {
			allowedColors.add(c);
		}
		this.prevDimension.height++;
		repaint();
	}
}
