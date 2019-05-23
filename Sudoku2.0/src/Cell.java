import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JTextField;

/**
 * Class Cell is a class used for storing and updating values at a specified position in the game user interface (GUI). 
 * @author JonathanNeff, Dylan Woodworth, 
 *
 */
@SuppressWarnings("serial")
public class Cell extends JTextField {
	
	/**
	 * Class properties:
	 * rowX    x-coordinate indicating which row the Cell is located in.
	 * columnY    y-coordinate indicating which column the Cell is located in. 
	 * value    numeric value currently entered into the Cell; the default value is equal to zero. 
	 * isCellEmpty    a boolean value indicating whether or not the Cell contains a numeric value. 
	 * cellString    a String representation of legalValues; to be displayed by the tool tip.  
	 * legalValues    an ArrayList of legal values that may be entered into the Cell by the user. 
	 * background    
	 * 
	 */
	private int rowX, columnY; 
	private int value = 0;
	private boolean originalValue = false;
	private boolean isCellEmpty;  
	private String cellString; 
	private ArrayList<Integer> legalValues; 
	private Color background; 
	private static boolean isHintActive;
	
	/**
	 * Class constructor instantiates a Cell, located at a specified position on the SudokuBoard. 
	 * @param x    x-coordinate indicating which row the Cell is located in. 
	 * @param y    y-coordinate indicating which column the Cell is located in. 
	 */
	public Cell(int x, int y) {
		this.rowX = x;
		this.columnY = y;
		isCellEmpty = true;
		background = Color.WHITE;
		
		setPreferredSize(new Dimension(50, 50));
        setBorder(BorderFactory.createRaisedBevelBorder());
        setHorizontalAlignment(JTextField.CENTER);
        setFont(new Font("Impact", Font.BOLD, 25));
        
        setText("");
        setOpaque(true);
        setEditable(true);
        setToolTipText("");
	}
	
	/**
	 * The getValue method returns the current value stored in the Cell. 
	 * @return value    the numeral currently stored in the Cell. 
	 */
	public int getValue() {
		return value;
	}
	 
	/**
	 * The setValue method assigns a numeric value to the Cell.  
	 * @param value    the numeral to be stored in the Cell. 
	 */
	public void setValue(String value) {
		if(value.contains("0")) {
			setText("");
			this.value = 0;
		}
		else {
			setText(value);
	        this.value = Integer.parseInt(value);
		}
	}
	
	/**
	 * The getRowX method returns the x-coordinate indicating which row the Cell is located in. 
	 * @return rowX    numeral of x-coordinate.
	 */
	public int getRowX() {
        return rowX;
	}
	 
	/**
	 * The getColumnY method returns the y-coordinate indicating which column the Cell is located in. 
	 * @return columnY    numeral of y-coordinate. 
	 */
	public int getColumnY() {
        return columnY;
	}
	
	/**
	 * The updateToolTip method renews the legal values that may be entered into a Cell by the user,
	 * 		and shown by the tool tip.
	 * @param values    an array of numerals 
	 */
	public void updateToolTip(ArrayList<Integer> values) {
		legalValues = values;
		cellString = "";
		int length = values.size(); 
		
		// Generates the cellString to be displayed by the tool tip.   
		int counter = 0;
		for(Integer n: values) {
			counter ++;
			String temp = Integer.toString(n);
			if(counter == length) {
				cellString += temp;
			}
			else {
			cellString += temp + ", ";
			}
		}
		if(length == 0 && isCellEmpty == true) {
			setToolTipText("Legal value used elsewhere in block!");
		}
		else {
			// Update message displayed by the tool tip. 
			setToolTipText(cellString);
		}
	}
	
	/**
	 * The processKeyValue method computes whether or not the numeral input by the user is a valid entry, if so, 
	 * 		it sets the value equal to the integer value of the keystroke. 
	 */
    public void processKeyValue(KeyEvent ev) {
		if(isCellEmpty == true) {
			if(ev.getKeyChar() == KeyEvent.VK_1 && legalValues.contains(1)) {
				setValue("1");
			}
			else if(ev.getKeyChar() == KeyEvent.VK_2 && legalValues.contains(2)) {
				setValue("2");
			}
			else if(ev.getKeyChar() == KeyEvent.VK_3 && legalValues.contains(3)) {
				setValue("3");
			}
			else if(ev.getKeyChar() == KeyEvent.VK_4 && legalValues.contains(4)) {
				setValue("4");
			}
			else if(ev.getKeyChar() == KeyEvent.VK_5 && legalValues.contains(5)) {
				setValue("5");
			}
			else if(ev.getKeyChar() == KeyEvent.VK_6 && legalValues.contains(6)) {
				setValue("6");
			}
			else if(ev.getKeyChar() == KeyEvent.VK_7 && legalValues.contains(7)) {
				setValue("7");
			}
			else if(ev.getKeyChar() == KeyEvent.VK_8 && legalValues.contains(8)) {
				setValue("8");
			}
			else if(ev.getKeyChar() == KeyEvent.VK_9 && legalValues.contains(9)) {
				setValue("9");
			}
			else {
				
				Thread rejectThread = new Thread(){
					public void run() {
						int i = 0;
						Color current = getBackground();

						while(i < 1) {
						setBackground(Color.RED);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						setBackground(current);
						i+=1;
						}
					}
				};
			rejectThread.start();
			ev.consume();
			return;
			}
			
			// Set the Cell as being no longer empty.
			setEmptyStatus(false);
			ev.consume();
			return;
		}
		
		// Clear the numeral value from a Cell, and set back to empty. 
		else if(ev.getKeyChar() == KeyEvent.VK_BACK_SPACE && isCellEmpty == false) {
			legalValues.add(value); // Add numeral back to ArrayList of legalValues. 
			setValue("0"); 
			setEmptyStatus(true);
		}
		
		ev.consume();
		return;
	}	
    
    /**
     * The processKeyValue method is used for removing mutable values from the game board when the control key is pressed,
     * 		and the mouse hovers over a valid Cell. 
     * @param ev
     */
    public void processKeyValue(MouseEvent ev) {
    		if(ev.isControlDown() == true && isCellEmpty == false) {
			legalValues.add(value); // Add numeral back to ArrayList of legalValues. 
			setValue("0"); 
			setEmptyStatus(true);
		}
    }
    
    public void processKeyValue(ActionEvent ev, String s) {
    		int i = Integer.parseInt(s);
    		if(legalValues.contains(i)) {
			setValue(s);
			
			// Set the Cell as being no longer empty.
			setEmptyStatus(false);
			return;
		}
    }
	
	/**
	 * The setEmptyStatus method updates the current state of the Cell as either being empty or not empty. 
	 * @param logic    boolean value indicating whether or not the Cell is empty.  
	 */
	public void setEmptyStatus(boolean logic) {
		isCellEmpty = logic;
	}		
	
	/**
	 * The getEmptyStatus method returns the current state of the Cell as either being empty or not empty. 
	 * @return isCellEmpty    boolean value indicating whether or no the cell is empty. 
	 */
	public boolean getEmptyStatus() {
		return isCellEmpty;
	}
	
	/**
	 * The getLegalValues method returns an ArrayList of legal values. 
	 * @return legalValues    the ArrayList of legal values.
	 */
	public ArrayList<Integer> getLegalValues(){
		return legalValues;
	}
	
	/**
	 * The setColor method saves the original background Color of the Cell. 
	 * @param color    original background Color of a Cell. 
	 */
	public void setColor(Color color) {
		background = color;
	}
	
	/**
	 * The getColor method returns the original background Color of the Cell. 
	 * @return background    original background Color of a Cell. 
	 */
	public Color getColor() {
		return background;
	}
	
	/**
	 * The setIsHintActive method updates the hint tool to either active or inactive. 
	 * @param logic    boolean value indicating whether or not the hint tool is active. 
	 */
	public void setIsHintActive(boolean logic) {
		isHintActive = logic;
	}
	
	/**
	 * The getIsHintActive method returns whether or not the hint tool is active. 
	 * @return isHintActive    boolean value indicating whether or not the hint tool is active. 
	 */
	public boolean getIsHintActive() {
		return isHintActive;
	}	
}
