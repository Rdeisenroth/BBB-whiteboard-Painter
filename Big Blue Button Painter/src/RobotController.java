import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.TreeMap;

/**
 * A Robot Controller for the Input simulations
 * 
 * @author Ruben Deisenroth, 07/2020
 *
 */
public class RobotController {

	private Robot robot;
	// Mauspositionen
	private TreeMap<String, Point> Positions = new TreeMap<>() {
		{
			put("color_palete", new Point(1888, 561));
			put("silber", new Point(1846, 561));
			put("magenta", new Point(1804, 561));
			put("violett", new Point(1761, 561));
			put("blau", new Point(1720, 561));
			put("dodger_blau", new Point(1677, 561));
			put("cyan", new Point(1634, 561));
			put("hellgruen", new Point(1591, 561));
			put("gelbgruen", new Point(1548, 561));
			put("orange", new Point(1505, 561));
			put("rot", new Point(1462, 561));
			put("weiß", new Point(1419, 561));
			put("schwarz", new Point(1376, 561));
		}
	};

	public RobotController() throws AWTException {
		robot = createRobot();
	}

	public static Robot createRobot() throws AWTException {
		Robot robot = new Robot();
		return robot;
	}

	/**
	 * hits alt+tab x times
	 * 
	 * @param tabCount
	 */
	public void switchWindow(int tabCount) {
		robot.keyPress(KeyEvent.VK_ALT);
		for (int i = 0; i < tabCount; i++) {
			robot.keyPress(KeyEvent.VK_TAB);
			robot.keyRelease(KeyEvent.VK_TAB);
			robot.delay(500);
		}
		robot.keyRelease(KeyEvent.VK_ALT);
		robot.delay(100);
	}

	/**
	 * Clicks at given point
	 * 
	 * @param x
	 * @param y
	 */
	void click(int x, int y) {
		robot.mouseMove(x, y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		robot.delay(10);
	}

	/**
	 * Draw a Line between two points
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	void drawLine(int x1, int y1, int x2, int y2) {
//		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseMove(x1, y1);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		if (x1 != x2 || y1 != y2) {
			robot.delay(10);
//		Thread.sleep(100);
			robot.mouseMove(x2, y2);
		}
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		robot.delay(10);
	}

	/**
	 * Selects color in bbb Whiteboard
	 * 
	 * @param name
	 */
	void selectColor(String name) {
		Point color_pallete = Positions.get("color_palete");
		click(color_pallete.x, color_pallete.y);
		Point color_pos = Positions.get(name);
		click(color_pos.x, color_pos.y);
	}
}
