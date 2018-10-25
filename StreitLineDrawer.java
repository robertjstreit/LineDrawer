/**
Grade: 1, 2, 6, 7, 8 Extra Credit: 3, 4
*/
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.*;
import java.io.*;

/**
The point class contains the point constructor for each point, along with get and set functions
for each of the data members of the point objects. It also has a toString function so the 
points can be saved and loaded back into the program. 
*/
class Point {
	private int x;
	private int y;
	private Color color;
	private int colorCode;
	private boolean start;
	public Point() {
		x = 0;
		y = 0;
	}
	public Point(int x, int y, int colorCode, boolean start) {
		setX(x);
		setY(y);
		setColor(colorCode);
		setStart(start);
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public Color getColor() {
		return color;
	}
	public boolean getStart() {
		return start;
	}
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public void setColor(int colorCode) {
		if (colorCode == 0) {
			color = (Color.RED);
		} else if (colorCode == 1) {
			color = (Color.GREEN);
		} else if (colorCode == 2) {
			color = (Color.BLUE);
		} else if (colorCode == 3) {
			color = (Color.BLACK);
		}
		this.colorCode = colorCode;
	}
	public void setStart(boolean start) {
		this.start = start;
	}
	@Override
	public String toString() {
		return String.format("%d %d %d %s",x, y, colorCode, getStart());
	}
}

/**
The LinePanel paints all of the points on the screen. It positions each point based on
mouse clicks. It also has several key events to make a new set of points, change the colors
of new points, as well as a mouse movement event to update the position of the cursor. 
*/
class LinePanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
	private ArrayList<Point> points;
	private String message;
	private int colorCode;
	private Color color;
	private int pointSize;
	private int mouseX;
	private int mouseY;
	private boolean continuousMode;
	private boolean drawLines;
	public int getPointSize() {
		return pointSize;
	}
	public void setPointSize(int pointSize) {
		this.pointSize = pointSize;
	}
	public boolean getContinuousMode() {
		return continuousMode;
	}
	public void setContinuousMode(boolean cm) {
		continuousMode = cm;
	}
	public boolean getDrawLines() {
		return drawLines;
	}
	public void setDrawLines(boolean dl) {
		drawLines = dl;
	}
	public LinePanel(ArrayList<Point> points) {
		this.points = points;
		message = "Welcome";
		addMouseListener(this);
		addMouseMotionListener(this);
		continuousMode = false;
		drawLines = true;
		colorCode = 3;
		pointSize = 10;
		setFocusable(true);
		addKeyListener(this);
	}
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			continuousMode = false;
			repaint();
		}
		for (Point p : points) {	
			if (e.getKeyCode() == KeyEvent.VK_R) {
				colorCode = 0;
			} else if (e.getKeyCode() == KeyEvent.VK_G) {
				colorCode = 1;
			} else if (e.getKeyCode() == KeyEvent.VK_B) {
				colorCode = 2;
			} else if (e.getKeyCode() == KeyEvent.VK_K) {
				colorCode = 3;
			}
		}
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Point prevPoint = null;
		for (Point p : points) {
			g.setColor(p.getColor());
			g.fillOval(p.getX(),p.getY(),getPointSize(),getPointSize());
			if ((prevPoint != null) && (p.getStart() == false) && (drawLines == true)) {
				g.drawLine(prevPoint.getX() + 4, prevPoint.getY() + 4, p.getX() + 4,p.getY() + 4);
			}
			prevPoint = p;
		}
		if ((prevPoint != null) && (continuousMode) && (drawLines == true)) {
			g.drawLine(prevPoint.getX() + 4, prevPoint.getY() + 4, mouseX + 4, mouseY + 4);
		}
		g.setColor(getBackground());
		g.fillRect(290,270,100,50);
		g.setColor(Color.BLACK);
		g.drawString(message,300,300);
	}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {
		Point p = new Point(e.getX(),e.getY(),colorCode,(!continuousMode));
		points.add(p);
		continuousMode = true;
		repaint();
	}
	public void mouseDragged(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {
		message = String.format("Current location: (%d,%d)", e.getX(),e.getY());
		mouseX = e.getX();
		mouseY = e.getY();
		requestFocusInWindow();
		repaint();
	}
}

/*
PointsIO has a readFile function which allows the user to load a previously saved set
of points. 
*/
class PointsIO {
	public static boolean readFile(ArrayList<Point> points, File f) {
		try {
			points.clear();
			Scanner sc = new Scanner(f);
			String line;
			String[] parts;
			Color color;
			while (sc.hasNextLine()) {
				line = sc.nextLine();
				parts = line.split(" ");
				Point p = new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Boolean.parseBoolean(parts[3]));
				points.add(p);
			}
			sc.close();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}

/**
The LineFrame creates the user interface. It has menus and menu items which allow the user
to save, load, set point size, clear points, and other such things. 
*/
class LineFrame extends JFrame {
	private ArrayList<Point> points;
	private LinePanel lpan;
	public void setupMenu() {
		JMenuBar mbar = new JMenuBar();
		JMenu mnuFile = new JMenu("File");
		JMenuItem miSave = new JMenuItem("Save");
		miSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					JFileChooser jfc = new JFileChooser();
					if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
						FileWriter fw = new FileWriter(jfc.getSelectedFile());
						for (Point p : points) {
							fw.write(p.toString() + "\r\n");
						}
						fw.close();						
					}
				} catch (Exception ex) {
					
				}
			}
		});
		mnuFile.add(miSave);
		JMenuItem miOpen = new JMenuItem("Open");
		miOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					JFileChooser jfc = new JFileChooser();
					if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						if (PointsIO.readFile(points,jfc.getSelectedFile())) {
							JOptionPane.showMessageDialog(null, "Points were read successfully.");
						} else {
							JOptionPane.showMessageDialog(null,  "Could not read points.");
						}
						repaint();
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage());
				}
			}
		});
		mnuFile.add(miOpen);
		JMenuItem miExit = new JMenuItem("Exit");
		miExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnuFile.add(miExit);
		JMenu mnuEdit = new JMenu("Edit");
		JMenuItem miClear = new JMenuItem("Clear");
		miClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				points.clear();
				repaint();
			}
		});
		mnuEdit.add(miClear);
		JMenu mnuSize = new JMenu("Size");
		JMenuItem miLarge = new JMenuItem("Large");
		miLarge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lpan.setPointSize(15);
				repaint();
			}
		});
		mnuSize.add(miLarge);
		JMenuItem miMedium = new JMenuItem("Medium");
		miMedium.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lpan.setPointSize(10);
				repaint();
			}
		});
		mnuSize.add(miMedium);
		JMenuItem miSmall = new JMenuItem("Small");
		miSmall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lpan.setPointSize(5);
				repaint();
			}
		});
		mnuSize.add(miSmall);
		JMenu mnuLines = new JMenu("Lines");
		JMenuItem miYes = new JMenuItem("Yes");
		miYes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lpan.setDrawLines(true);
				repaint();
			}
		});
		mnuLines.add(miYes);
		JMenuItem miNo = new JMenuItem("No");
		miNo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lpan.setDrawLines(false);
				repaint();
			}
		});
		mnuLines.add(miNo);
		mbar.add(mnuFile);
		mbar.add(mnuEdit);
		mbar.add(mnuSize);
		mbar.add(mnuLines);
		setJMenuBar(mbar);
	}
	public void setupUI() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(100,100,500,500);
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		lpan = new LinePanel(points);
		c.add(lpan, BorderLayout.CENTER);
		setupMenu();
	}
	public LineFrame(ArrayList<Point> points) {
		this.points = points;
		setupUI();
	}
}

/**
The main function simply instantiates and initializes the list of points and lineFrame, while
also making the lineFrame visible. 
*/
public class StreitLineDrawer {
	public static void main(String[] args) {
		ArrayList<Point> points = new ArrayList<Point>();
		LineFrame lineFrame = new LineFrame(points);
		lineFrame.setVisible(true);
	}
}
