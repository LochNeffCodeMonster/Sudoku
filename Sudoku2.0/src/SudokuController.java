import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Class SudokuController is a class used for responding to various events invoked by the user. 
 * @author JonathanNeff & Dylan Woodworth 
 *
 */
public class SudokuController implements ActionListener, KeyListener, MouseListener {

	/**
	 * Class properties:
	 * game    a 2D matrix containing the values currently saved in each Cell.
	 */
  
    private int hintsLeft = 3;
    private int counter = 0;
    private Cell currentCell;
    private Sudoku game; 
	
    /**
     * Class constructor instantiates a game controller for responding to various events. 
     * @param sudoku 
     * @param game    a 2D matrix containing the values currently saved in each Cell.
     * @param timerPanel 
     */
	public SudokuController(Sudoku sudoku) {
		
	}
	
	/**
	 * The actionPerformed method responds to action events originating from the JMenuBar. 
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {

		// Clear the game user interface (GUI).
		if (arg0.getActionCommand().equals("New")) {
			game.getHelpMenu().setVisible(false);
			game.clearGame();
			game.partyMode();
			game.resetTimer();
		}
		
		// Generate JFileChooser for opening a file.
		else if (arg0.getActionCommand().equals("Read")) {
			game.getHelpMenu().setVisible(false);
			game.createFileDisplay();
			game.gameTimer();
		}
		
		// Generate JFileCooser for saving a file. 
		else if (arg0.getActionCommand().equals("Save")) {
			game.writeToFile();
		}
		
		// Exit the GUI. 
		else if (arg0.getActionCommand().equals("Exit")) {
			System.exit(0);
		}
		
		// Start or stop party mode. 
		else if (arg0.getActionCommand().equals("Start Party")) {
			if (game.getPartyMode() == false) {
				game.setPartyMode(true);
			}
			game.partyMode();
		}
		
		// Generate new party mode. 
		else if (arg0.getActionCommand().equals("After Party")) {
			if (game.getPartyMode() == true) {
				game.partyMode();
			}
		}
		
		else if (arg0.getActionCommand().equals("Stop Party")) {
			if (game.getPartyMode() == true) {
				game.setPartyMode(false);
			}
			game.partyMode();
		}
		/**
		else if (arg0.getActionCommand().equals("Random")) {
			game.resetHint();			
			game.getHelpMenu().setVisible(true);
			game.clearGame();
			game.genRandomGame();
			hintsLeft = 3;
			if(counter > 0) {
				menuPanel.getHintMenuItem().setText("Get Hint ("+hintsLeft+" Left)");

			}
			counter +=1;
		}
		
		else if(arg0.getActionCommand().contains("Get Hint")) {			
			if(hintsLeft > 0) {
				game.getHint();
				hintsLeft--;
				menuPanel.getHintMenuItem().setText("Get Hint ("+hintsLeft+" Left)");
			}
			
			else {
				System.out.println("You're out of hints!");
			}	
		}	**/
		
		else if(arg0.getActionCommand().matches("[1-9]+")) {
			Object jB = arg0.getSource();
			JButton jB1 = (JButton) jB;
			String s = jB1.getText();
			currentCell.processKeyValue(arg0, s);
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		}
	
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		Object cell = arg0.getSource();
		Cell clickedCell = (Cell) cell;
		clickedCell.processKeyValue(arg0);
		game.setValue(clickedCell);
		game.updateStatusLabels();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		Object cell = arg0.getSource();
		Cell clickedCell = (Cell) cell;
		currentCell = clickedCell;
		//game.buttonEnabler(currentCell);
		
		
		//Object cell = arg0.getSource();
		//Cell clickedCell = (Cell) cell;
		//if(clickedCell.getIsHintActive() == true) {
		//	game.resetHint();
		//}	
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		if(arg0.isControlDown() == true) {
			Object cell = arg0.getSource();
			Cell clickedCell = (Cell) cell;
			clickedCell.processKeyValue(arg0);
		}
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}