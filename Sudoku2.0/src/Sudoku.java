import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
//import com.sun.tools.javac.util.List;

@SuppressWarnings("serial")
public class Sudoku extends JFrame implements ActionListener, KeyListener, MouseListener {
	
	public static void main(String[] args) {
		new Sudoku();
	}
	
	// These are the updated variables
	private JPanel gameBoard;
	private Cell[][] cells;
	
	private JMenuBar gameMenu;
	private JMenu helpMenu;
	private JMenuItem getHint;
	
	//Until here
	
	private JPanel timePanel = new JPanel();
	
	private String[][] readGame = new String[9][9];
	private int[][] game = new int[9][9];
	private int[][] solvedGame;
	
	
	private JTextField timeClock;
	private String time;
	private int seconds = 0; 
	private Timer timer;
	

	private JFileChooser fileChooser;
	private File file;
	
	private Color defaultBackground = Color.ORANGE;
	private boolean partyOn = false;
	private int counter = 0;
	
	private ArrayList<Color> colorList;
	
	private int hintsLeft = 3;
    private int counter2 = 0;
    private Cell currentCell;
	
	/**
	 * Class constructor generates an instance of the GUI  
	 */
	public Sudoku() {
		
		setTitle("Sudoku Puzzle 2.0");
		setSize(1500, 1500);
		setFont(new Font("Impact", Font.BOLD, 25));
		setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        gameBoard = gameBoard();
        add(gameBoard, BorderLayout.CENTER);
        
        gameMenu = gameMenu();
        setJMenuBar(gameMenu);
        
        timePanel = timePanel();
        add(timePanel, BorderLayout.SOUTH);
        
        createColors();
            
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
	}
	
	
	private JPanel gameBoard() {
		
		// Nine 3x3 subgrids that compose the 9x9 grid
		JPanel[][] blocks;
		
		// Create 9x9 grid to hold the blocks
		gameBoard = new JPanel();
		gameBoard.setLayout(new GridLayout(3, 3, 2, 2));
		gameBoard.setBorder(BorderFactory.createBevelBorder(1));
		
		// Create blocks to hold the cells
		blocks = new JPanel[3][3];
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				blocks[i][j] = new JPanel(new GridLayout(3, 3));
				blocks[i][j].setBorder(BorderFactory.createBevelBorder(1));
				gameBoard.add(blocks[i][j], BorderLayout.CENTER);
			}
		}
		
		// Create cells to hold digits
		cells = new Cell[9][9];
		for(int x = 0; x < 9; x++) {
			for(int y = 0; y < 9; y++) {
				cells[x][y] = new Cell(x, y);
				
				// Add listeners to newly created cell
				cells[x][y].addActionListener(this);
				cells[x][y].addKeyListener(this);
				cells[x][y].addMouseListener(this);
				
				int row = 0; int column = 0;
						
				// Determine row number of block, for which the newly created cell belongs 
				if(x >= 0 && x <= 2) {
					row = 0;
				}
				else if(x >= 3 && x <= 5) {
					row = 1;
				}
				else if(x >= 6 && x <= 8) {
					row = 2;
				}
						
				// Determine column number of block, for which the newly created cell belongs 
				if(y >= 0 && y <= 2) {
					column = 0;
				}
				else if(y >= 3 && y <= 5) {
					column = 1;
				}
				else if(y >= 6 && y <= 8) {
					column = 2;
				}
				blocks[row][column].add(cells[x][y]);
			}
		}
		highlightCells();
		return gameBoard;		
	}
	
	private void highlightCells() {
		
		// Cells are highlighted by alternating blocks to form a checkerboard pattern 
		for(int row = 0; row < 9; row++) {
			for(int col = 0; col < 9; col++) {
										
				// Highlight cells in block[0][1]. 
				if(row < 3) {
					if(col > 2 && col < 6) {
						cells[row][col].setBackground(defaultBackground);
						cells[row][col].setColor(defaultBackground);
					}
				}
										
				// Highlight cells in block[1][0] and block[1][2].
				if(row > 2 && row < 6) {
					if(col < 3 || col > 5) {
						cells[row][col].setBackground(defaultBackground);
						cells[row][col].setColor(defaultBackground);
					}
				}
											
				// Highlight cells in block[2][1]. 
				if(row > 5) {
					if(col > 2 && col < 6) {
						cells[row][col].setBackground(defaultBackground);
						cells[row][col].setColor(defaultBackground);
					}
				}
			}
		}
	}
	
	private JMenuBar gameMenu() {
		
		// Initiate variables
		gameMenu = new JMenuBar();
		JMenu fileMenu, partyMenu;
		JMenuItem newItem, readItem, saveItem, exitItem, startPartyItem, newPartyItem, stopPartyItem, generateRandomGame;
		
		// Generate "File" JMenu and JMenuItem's
		fileMenu = new JMenu("File");
		
		newItem = new JMenuItem("New");
		fileMenu.add(newItem);
		newItem.addActionListener(this);
				
		readItem = new JMenuItem("Read");
		fileMenu.add(readItem);
		readItem.addActionListener(this);
					
		saveItem = new JMenuItem("Save");
		fileMenu.add(saveItem);
		saveItem.addActionListener(this);
				
		generateRandomGame = new JMenuItem("Random");
		fileMenu.add(generateRandomGame);
		generateRandomGame.addActionListener(this);
		
		exitItem = new JMenuItem("Exit");
		fileMenu.add(exitItem);
		exitItem.addActionListener(this);
		
		// Generate "Party" JMenu and JMenuItem's
		partyMenu = new JMenu("Party");
		
		startPartyItem = new JMenuItem("Start Party");
		partyMenu.add(startPartyItem);
		startPartyItem.addActionListener(this);
				
		newPartyItem = new JMenuItem("After Party");
		partyMenu.add(newPartyItem);
		newPartyItem.addActionListener(this);
				
		stopPartyItem = new JMenuItem("Stop Party");
		partyMenu.add(stopPartyItem);
		stopPartyItem.addActionListener(this);
		
		// Generate "Help" JMenu and JMenuItem's
		helpMenu = new JMenu("Help");
				
		getHint = new JMenuItem("Get Hint (3 left)");
		helpMenu.add(getHint);
		getHint.addActionListener(this);
		
		helpMenu.setVisible(false);
				
		gameMenu.add(fileMenu);
		gameMenu.add(partyMenu);
		gameMenu.add(helpMenu);
		
		return gameMenu;
	}
	
	private JPanel timePanel() {
		
		timeClock = new JTextField();
		timeClock.setText("0:00");
		timePanel.add(timeClock, BorderLayout.EAST);
		
		return timePanel;
	}
	
	private String getTime(int time) {
		int minutes = time / 60;
		int seconds = time % 60;
		if(seconds < 10) {
			return minutes + ":" + 0 + seconds;
		}
		else {
			return minutes + ":" + seconds;
		}
	}

	public void gameTimer(){
	    TimerTask repeatedTask = new TimerTask() {
	        public void run() {
	        		seconds++;
	        		time = getTime(seconds);
	            timeClock.setText(time);
	        }
	    };
	    timer = new Timer("Timer");
	    long delay  = 1000L;
	    long period = 1000L;
	    timer.scheduleAtFixedRate(repeatedTask, delay, period);
	}
	
	public void resetTimer() {
		seconds = 0;
		timeClock.setText("0:00");
		timer.cancel();
	}
	
	/**
	 * @return the helpMenu
	 */
	public JMenu getHelpMenu() {
		return helpMenu;
	}
	/**
	 * 
	 * @return the menuItem getHint
	 */
	public JMenuItem getHintMenuItem() {
		return getHint;
	}
	
	/**
	 * The createFileDisplay method creates the display for the JFileChooser. 
	 */
	public void createFileDisplay() {
		JPanel display = new JPanel();
		String directoryPath = Paths.get("./Resources").toAbsolutePath().normalize().toString();
		fileChooser = new JFileChooser(directoryPath);
		fileChooser.setDialogTitle("Sudoku Puzzle's");
		display.setPreferredSize(new Dimension(278, 179));
		display.setLayout(null);
		fileResponse();
	}
	
	/**
	 * The fileResponse method verifies the user selected a valid File, if so, initiate the File to be read. 
	 */
	private void fileResponse() {
		int returnValue = fileChooser.showOpenDialog(null);
		if(returnValue == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
			// Read the File.	
			try { 
				readPuzzleFile(file);
				} catch (Exception error) {
			}
		}
		// Reset the file chooser for the next time it's shown.
        fileChooser.setSelectedFile(null);
	}
	
	/**
	 * The readPuzzleFile method reads in the selected File chosen by the user, and sets the game board. 
	 * @param filename    name of user selected File. 
	 * @return game    a 2D matrix representation of a sudoku puzzle. 
	 */
	private void readPuzzleFile(File filename) {
		System.out.println("Reading file: "+ filename);
		Scanner puzzle = null;
		try {
	        puzzle = new Scanner(filename, "UTF-8");
		} catch (FileNotFoundException e) {
	        e.printStackTrace();
	    	}
		for(int row = 0; row < 9; row++) {
			String line = puzzle.nextLine();
			String[] token = line.split("  ");
			for(int col = 0; col < token.length; col++) {
				readGame[row][col] = token[col];
			}
		}
		// Amount of time spent on a puzzle in seconds is located on line 10, a new puzzle has a value of 0
		String timeLine = puzzle.nextLine();
		String[] timeToken = timeLine.split("  ");
		int timeValue = Integer.parseInt(timeToken[0]);
		seconds = timeValue;
		
		newGame(readGame);
		updateStatusLabels();
	}
	
	/**
	 * The writeToFile method saves the current state of the game user interface to a user selected File. 
	 */
	public void writeToFile() {
		String directoryPath = Paths.get("./Resources").toAbsolutePath().normalize().toString();
		JFileChooser fileSaveChooser = new JFileChooser(new File(directoryPath));
		fileSaveChooser.setDialogTitle("File Saver");   

		int userSelection = fileSaveChooser.showSaveDialog(null);
		 
		if (userSelection == JFileChooser.APPROVE_OPTION) {
		    File fileToSave = fileSaveChooser.getSelectedFile();
		    FileWriter puzzleWrite;
			try {
				puzzleWrite = new FileWriter(fileToSave, true);
				BufferedWriter buffer = new BufferedWriter(puzzleWrite);
	    			PrintWriter puzzleStream = new PrintWriter(buffer); 
	    			for(int i = 0; i < 9; i++) {
	    				String puzzleString = new String();
	    				for(int j = 0; j < 9; j++) {
	    					int x = cells[i][j].getValue();
	    					puzzleString += x + "  ";
	    					}
	    				puzzleString.trim();
	    				puzzleStream.println(puzzleString);
	    			}
	    			puzzleStream.close();
	    			System.out.println("Overwrote: " +fileToSave);
			} catch (IOException e) {
				System.err.println("Error: File: " +fileToSave+ " not found!");
				e.printStackTrace();
			}
		    System.out.println("Save as file: " + fileToSave.getAbsolutePath());
			}
		}
	
	/**
	 * The newGame method applies the initial values read in from a user selected File, to the GUI. 
	 * @param game
	 */
	private void newGame(String[][] readGame) {
		for(int row = 0; row < 9; row++) {
			for(int col = 0; col < 9; col++) {
				String token = readGame[row][col];
				if(token.contains("*")) {
					char temp = token.charAt(0);
					int value = Character.getNumericValue(temp);
					String str = Character.toString(temp);
					cells[row][col].setValue(str);
					cells[row][col].setEnabled(false);
					game[row][col] = value;
				}
				else {
					int value = Integer.parseInt(readGame[row][col]);
					if(value != 0) {
						cells[row][col].setValue(token);
						game[row][col] = value;
						counter++;
					}
				}
			}
		}
	}
	
	/**
	 * The clearGame method removes all values from the GUI. 
	 */
	public void clearGame() {
		for(int row = 0; row < 9; row++) {
			for(int col = 0; col < 9; col++) {
				cells[row][col].setEnabled(true);
				cells[row][col].setValue("0");
				counter = 0;
			}
		}
	}

	/**
	 * The updateStatusLabels method passes each Cell individually to the updateCell method. 
	 */
	public void updateStatusLabels() {
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				updateCell(i, j);
			}
		}
	}
	
	/**
	 * The updateCell method determines the legal values that can be entered into a Cell. 
	 * @param row    x-coordinate indicating which row the Cell is located in.
	 * @param col    y-coordinate indicating which column the Cell is located in.
	 */
	private void updateCell(int row, int col) {
		ArrayList<Integer> values = new ArrayList<>();
		ArrayList<Integer> legalValues = new ArrayList<>();
		
		int tempX = 0;
		int tempY = 0;
		
		// Iterate through Cells located in same row.
		for(int j = 0; j < 9; j++) {
			int a = cells[row][j].getValue();
			if(a != 0) {
				values.add(a);
			}
		}
		
		// Iterate through Cells located in the same column.
		for(int i = 0; i < 9; i++) {
			int b = cells[i][col].getValue();
			if(b != 0) {
				values.add(b);
			}
		}
		
		// Determine which block the Cell is located in.  
		if(row < 3) {
			if(col < 3) {
				tempX = 0; tempY = 0;
			}
			else if(col > 2 && col < 6) {
				tempX = 0; tempY = 3;
			}
			else {
				tempX = 0; tempY = 6;
			}
		}
		else if(row > 2 && row < 6) {
			if(col < 3) {
				tempX = 3; tempY = 0;
			}
			else if(col > 2 && col < 6) {
				tempX = 3; tempY = 3;
			}
			else {
				tempX = 3; tempY = 6;
			}
		}
		else if(row > 5) {
			if(col < 3) {
				tempX = 6; tempY = 0;
			}
			else if(col > 2 && col < 6) {
				tempX = 6; tempY = 3;
			}
			else {
				tempX = 6; tempY = 6;
			}
		}
		
		// Updated code
		int tempX = row / 3;
		int tempY = col / 3;
		int startX = tempX * 3;
		int startY = tempY * 3;
		
		
		// Iterate through Cells located in the same block. 
		for(int x = tempX; x < tempX + 3; x++) {
			for(int y = tempY; y < tempY + 3; y++) {
				int c = cells[x][y].getValue();
				if(c != 0) {
					values.add(c);
				}
			}
		}
		
		// Add legal values to the ArrayList legalValues, if and only if, the value is not found in the same row, column or block
		//		of the current Cell. 
		for(int i = 1; i < 10; i++) {
			if(!values.contains(i)) {
				legalValues.add(i);
				}
			}
		cells[row][col].updateToolTip(legalValues);
	}
	
	/**
	 * The setValue method updates a value at a specified location in the 2D matrix game. 
	 * @param cell    the Cell containing the value entered in by the user. 
	 */
	public void setValue(Cell cell) {
		int x = cell.getRowX();
		int y = cell.getColumnY();
		game[x][y] = cell.getValue();
	}
	
	public ArrayList<Color> createColors() {
		colorList = new ArrayList<Color>();
		Integer[] red = {255, 255, 138, 65, 0, 0, 127, 0, 0, 255, 255, 255, 64, 176};
		Integer[] green = {20, 0, 43, 105, 0, 191, 255, 255, 255, 255, 165, 99, 224, 196};
		Integer[] blue = {147, 255, 226, 225, 255, 255, 212, 255, 0, 0, 0, 71, 208, 222};
		for(int i = 0; i < red.length; i++) {
			Color newColor = new Color(red[i], green[i], blue[i]);
			colorList.add(newColor);
		}
		return colorList;	
	}
	
	/**
	 * The partyMode method changes the background Color of the highlighted cells, 
	 * 		to a randomly selected Color. 
	 */
	public void partyMode() {
		
		if(partyOn == true) {
			
			// Party mode is on, change background Color of highlighted cells to a randomly selected Color.  
			for(int row = 0; row < 9; row++) {
				for(int col = 0; col < 9; col++) {
					Random rand = new Random();
					int randomColor = rand.nextInt(14);
					
					// Highlight cells in block[0][1]. 
					if(row < 3) {
						if(col > 2 && col < 6) {
							cells[row][col].setBackground(colorList.get(randomColor));
						}
					}
					// Highlight cells in block[1][0] and block[1][2].
					if(row > 2 && row < 6) {
						if(col < 3 || col > 5) {
							cells[row][col].setBackground(colorList.get(randomColor));
						}
					}
					// Highlight cells in block[2][1]. 
					if(row > 5) {
						if(col > 2 && col < 6) {
							cells[row][col].setBackground(colorList.get(randomColor));
						}
					}
				}
			}
		}	
		// Party mode is turned off, return Cells to their original background Color. 
		else if(partyOn == false) {
			highlightCells();
		}
	}
	
	/**
	 * The getPartyMode returns a boolean value indicating whether or not party mode is on. 
	 * @return partyOn    boolean value
	 */
	public boolean getPartyMode() {
		return partyOn;
	}
	
	/**
	 * The setPartyMode sets the party mode. 
	 * @param logic    boolean value. 
	 */
	public void setPartyMode(boolean logic) {
		partyOn = logic;
	}


	@Override
	public void mouseClicked(MouseEvent arg0) {
		Object cell = arg0.getSource();
		Cell clickedCell = (Cell) cell;
		currentCell = clickedCell;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		if(arg0.isControlDown() == true) {
			Object cell = arg0.getSource();
			Cell clickedCell = (Cell) cell;
			clickedCell.processKeyValue(arg0);
			updateStatusLabels();
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
		setValue(clickedCell);
		updateStatusLabels();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// Clear the game user interface (GUI).
		if (arg0.getActionCommand().equals("New")) {
			/**getHelpMenu().setVisible(false);**/
			clearGame();
			setPartyMode(false);
			partyMode();
			resetTimer();
		}
				
		// Generate JFileChooser for opening a file.
		else if (arg0.getActionCommand().equals("Read")) {
			/**getHelpMenu().setVisible(false);**/
			createFileDisplay();
			gameTimer();
		}
				
		// Generate JFileCooser for saving a file. 
		else if (arg0.getActionCommand().equals("Save")) {
			writeToFile();
		}
				
		// Exit the GUI. 
		else if (arg0.getActionCommand().equals("Exit")) {
			System.exit(0);
		}
				
		// Start or stop party mode. 
		else if (arg0.getActionCommand().equals("Start Party")) {
			if (getPartyMode() == false) {
				setPartyMode(true);
			}
			partyMode();
		}
				
		// Generate new party mode. 
		else if (arg0.getActionCommand().equals("After Party")) {
			if (getPartyMode() == true) {
				partyMode();
			}
		}
				
		else if (arg0.getActionCommand().equals("Stop Party")) {
			if (getPartyMode() == true) {
				setPartyMode(false);
			}
			partyMode();
		}
		
		else if (arg0.getActionCommand().equals("Random")) {
			resetHint();			
			getHelpMenu().setVisible(true);
			clearGame();
			genRandomGame();
		}
			/**
			hintsLeft = 3;
			if(counter2 > 0) {
				menuPanel.getHintMenuItem().setText("Get Hint ("+hintsLeft+" Left)");

			}
			counter2 +=1;
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
	
	public void genRandomGame2() {
		
		clearGame();
		
		int valueCounter = 0;
		int[][] newGame = new int[9][9];
		Random rand = new Random();
		
		for(int i = 0; i < 9; i++) {
			ArrayList<Integer> blockValues = new ArrayList<Integer>();
			blockValues.add(1);blockValues.add(2);blockValues.add(3);blockValues.add(4);
			blockValues.add(5);blockValues.add(6);blockValues.add(7);blockValues.add(8);blockValues.add(9);
			int rowStart = valueCounter / 9;
			int rowEnd = rowStart + 3;
			int colStart = valueCounter % 9;
			int colEnd = colStart + 3;
			
			for(int j = rowStart; j < rowEnd; j++) {
				for(int k = colStart; k < colEnd; k++) {
					int shuffles = rand.nextInt(5);
					for(int s = 0; s < shuffles; s++) {
						Collections.shuffle(blockValues);
					}
					int currentValue;
					boolean boo = false;
					int arrayCounter = 0;
					while(boo == false) {
						currentValue = blockValues.get(arrayCounter);
						boo = validate(currentValue, newGame, j, k);
						
						if(boo == false) {
							arrayCounter++;
							if(arrayCounter == blockValues.size()) {
								genRandomGame();
							}
						}
						else {
							newGame[j][k] = currentValue;
							blockValues.remove(arrayCounter);
							valueCounter++;
						}
					}
				}
			}		
		}
		System.out.println(newGame[1][1]);
	}
	
	public boolean validate(int value, int[][] game, int row, int col) {
		
		for(int i = 0; i < 9; i++) {
			if(value == game[row][i]) {
				return false;
			}
			else if(value == game[i][col]) {
				return false;
			}
		}
		return true;
	}
	
	
	
	/**
	 * Generates a random COMPLETE sudoku game and ensures the game is solvable;
	 * 		then displaying randomly selected values to the GUI.. 
	 */
	public void genRandomGame() {
		
		// Clear all values from GUI. 
		clearGame();
		
		// Creates a temporary 2D matrix for storing values.
		int[][] newGame = new int[9][9];
		
		updateStatusLabels();
		
		// Used to store the legal values for the current Cell. 
		ArrayList<Integer> legalValues;
		int randomNum = 0;
		
		// Used for detecting an infinite loop.
		double startTime = System.currentTimeMillis();
		double stopTime;

		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				stopTime = System.currentTimeMillis();
				
				// Helps detect whether or not the algorithm is stuck and looping infinitely, while trying
				//		to generate a solvable game.
				if(stopTime-startTime > 1000) {
					
					// If the algorithm is stuck, restart method. 
					genRandomGame();
					return;
				}
				
				// Acquire the legal values for a specified Cell.
				legalValues = cells[x][y].getLegalValues();
				if (legalValues.size() == 0) {
					if (y > 0) {
						y -= 1;
					} 
					else if (y == 0) {
						x -= 1;
					}
				}
				try {
					// Creates a random value in the range from 0 -> size of legalValues list,
					//	 if the list is greater than size 0.
					randomNum = ThreadLocalRandom.current().nextInt(0, legalValues.size());

				} catch (Exception ex) {
					updateStatusLabels();
					break;
				}
				
				// Change the value of the cell, if the value is valid.
				cells[x][y].setValue(Integer.toString(legalValues.get(randomNum)));
				
				// Change the value of the gameBoard.
				newGame[x][y] = legalValues.get(randomNum);
				updateStatusLabels();
				solvedGame = newGame;
			}
		}
		
		// Clear the values in all of the cells because the game is starting.
		clearGame();
		
		// Counter for how many values are currently displayed on the board.
		int valuesPlaced = 0;
		
		Random rand = new Random();
		int randRow, randColumn, randNum;
		int randCellValue;
		
		// Generates values to be placed on the board.
		while (valuesPlaced < 40) {
			
			//Generates random row and column.
			randRow = rand.nextInt(9);
			randColumn = rand.nextInt(9);
			
			// Grabs the value of the number in the cell of the random row and column. 
			randCellValue = solvedGame[randRow][randColumn];
			
			// randCellValue !=0 is to help catch errors and the second part of the if-statement
			//		makes sure the generated cell is empty.
			if (randCellValue != 0 && cells[randRow][randColumn].getEmptyStatus() != false) {
				
				// Increment values placed. 
				valuesPlaced++;
				cells[randRow][randColumn].setText(Integer.toString(randCellValue));
				game[randRow][randColumn] = randCellValue;
			}
		}
	}
	
	public void generateNewGame() {
		// Clear all values from GUI. 
		clearGame();
				
		// Creates a temporary 2D matrix for storing values.
		int[][] newGame = new int[9][9];
				
		updateStatusLabels();
		
		
	}
	
	/**
	 * The getHint method allows the user to receive a hint, at a random location on the game board.
	 */
	public void getHint() {
		boolean valid = false;
		Random rand = new Random();
		int randRow, randColumn;
		Cell randCell;
		
		// While the number generated is not valid. 
		while(!valid) {
			randRow = rand.nextInt(9);
			randColumn = rand.nextInt(9);
			randCell = cells[randRow][randColumn];
			if(randCell.getEmptyStatus() == true && solvedGame[randRow][randColumn] != 0) {
				randCell.setIsHintActive(true);
				randCell.setBackground(Color.GREEN);
				randCell.setText(Integer.toString(solvedGame[randRow][randColumn]));
				game[randRow][randColumn] = solvedGame[randRow][randColumn];
				valid = true;
			}
			updateStatusLabels();
		}
	}
	
	/**
	 * The resetHint method resets the colors of the cells. 
	 **/
	public void resetHint() {
		for(int i =0; i < 9; i++) {
			for(int j = 0; j < 9; j ++) {
				//changes the colors to the original colors
				cells[i][j].setBackground(cells[i][j].getColor());
			}
		}
	}
}
		
	
	
	
	



