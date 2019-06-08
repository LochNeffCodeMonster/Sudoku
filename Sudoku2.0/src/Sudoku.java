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
	private JMenuItem getHint;
	
	private JPanel timePanel = new JPanel();
	private JTextField timeClock;
	private String time;
	private int seconds = 0; 
	private Timer timer;
	
	private String[][] readGame;
	private int[][] game = new int[9][9];
	private int[][] solvedGame;
	
	private JFileChooser fileChooser;
	private File file;
	
	private Color defaultBackground = Color.ORANGE;
	private boolean partyModeOn = false;
	private ArrayList<Color> colorList;
	
	private int availableHints = 3;
	private Cell currentCell;
	private int counter;
	
	/**
	 * Class constructor generates an instance of the GUI  
	 */
	public Sudoku() {
		
		setTitle("Sudoku Puzzle 2.0");
		setSize(2000, 2000);
		setFont(new Font("Impact", Font.BOLD, 25));
		setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        gameBoard = gameBoard();
        add(gameBoard, BorderLayout.CENTER);
        
        gameMenu = gameMenu();
        setJMenuBar(gameMenu);
        
        timePanel = timePanel();
        add(timePanel, BorderLayout.SOUTH);
        
        generateColors();
            
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
	}
	
	/**
	 * The gameBoard method builds the GUI, which is a 9x9 grid where the user will input values 
	 * @return	gameBoard - an instance of the GUI
	 */
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
	
	/**
	 * The highlightCells method changes the background to the defaultBackground color of the Cells
	 * located in alternating blocks to form a checkerboard pattern
	 */
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
	
	/**
	 * The gameMenu method builds the JMenu containing various JMenuItems.
	 * @return gameMenu	JMenu for game.
	 */
	private JMenuBar gameMenu() {
		
		// Initiate variables
		gameMenu = new JMenuBar();
		JMenu fileMenu, partyMenu, subMenu, helpMenu;
		JMenuItem menuItem;
		
		// Build the "File" JMenu and JMenuItems
		fileMenu = new JMenu("File");
		
		menuItem = new JMenuItem("New");
		fileMenu.add(menuItem);
		menuItem.addActionListener(this);
				
		menuItem = new JMenuItem("Read");
		fileMenu.add(menuItem);
		menuItem.addActionListener(this);
					
		menuItem = new JMenuItem("Save");
		fileMenu.add(menuItem);
		menuItem.addActionListener(this);
		
		subMenu = new JMenu("Random");
		menuItem = new JMenuItem("Easy");
		subMenu.add(menuItem);
		menuItem.addActionListener(this);
	
		menuItem = new JMenuItem("Medium");
		subMenu.add(menuItem);
		menuItem.addActionListener(this);
		
		menuItem = new JMenuItem("Hard");
		subMenu.add(menuItem);
		menuItem.addActionListener(this);
		fileMenu.add(subMenu);
		
		fileMenu.addSeparator();
		
		menuItem = new JMenuItem("Exit");
		fileMenu.add(menuItem);
		menuItem.addActionListener(this);
		
		// Build the "Party" JMenu and JMenuItems
		partyMenu = new JMenu("Party");
		
		menuItem = new JMenuItem("Start");
		partyMenu.add(menuItem);
		menuItem.addActionListener(this);
				
		menuItem = new JMenuItem("Stop");
		partyMenu.add(menuItem);
		menuItem.addActionListener(this);
		
		// Build the "Help" JMenu and JMenuItems
		helpMenu = new JMenu("Help");
				
		getHint = new JMenuItem("Hint (3)");
		getHint.setEnabled(false);
		helpMenu.add(getHint);
		getHint.addActionListener(this);
		helpMenu.setEnabled(true);
				
		gameMenu.add(fileMenu);
		gameMenu.add(partyMenu);
		gameMenu.add(helpMenu);
		return gameMenu;
	}
	
	/**
	 * The timePanel method builds the JPanel used to store the in game clock.
	 * @return timePanel		JPanel used to store the in game clock. 
	 */
	private JPanel timePanel() {
		
		timeClock = new JTextField();
		timeClock.setText("0:00");
		timePanel.add(timeClock, BorderLayout.EAST);
		return timePanel;
	}
	
	/**
	 * The getTime method builds a String represenation of the format "minutes:seconds"; 
	 * 	of the current time elapsed in game.
	 * @param time	number of seconds elapsed
	 * @return	game time represented as a String with the format "mm:ss".
	 */
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

	/**
	 * The gameTimer method creates an instance of the timer used to keep track of how many
	 * 	seconds have elapsed so far in the game.
	 */
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
	
	/**
	 * The resetTimer methods removes all currently elapsed time from the game clock.
	 */
	public void resetTimer() {
		seconds = 0;
		timeClock.setText("0:00");
		timer.cancel();
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
	 */
	private void readPuzzleFile(File filename) {
		readGame = new String[9][9];
		System.out.println("Reading file: "+ filename);
		Scanner puzzleScanner = null;
		try {
	        puzzleScanner = new Scanner(filename, "UTF-8");
		} catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
		for(int row = 0; row < 9; row++) {
			String line = puzzleScanner.nextLine();
			String[] token = line.split("  ");
			for(int col = 0; col < token.length; col++) {
				readGame[row][col] = token[col];
			}
		}
		// Amount of time spent on a puzzle in seconds is located on line 10, a new puzzle has a value of 0
		String timeLine = puzzleScanner.nextLine();
		String[] timeToken = timeLine.split("  ");
		int timeValue = Integer.parseInt(timeToken[0]);
		this.seconds = timeValue;
		newGame(readGame);
		updateStatusLabels();
	}
	
	/**
	 * The writeToFile method saves the current state of the GUI to a user selected File. 
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
	    					int value = cells[i][j].getValue();
	    					if(cells[i][j].getOriginal() == true) {
	    						puzzleString += value + "*" + " ";
	    					}
	    					else {
	    						puzzleString += value + "  ";
	    					}
	    				}
	    				puzzleString.trim();
	    				puzzleStream.println(puzzleString);
	    			}
	    			puzzleStream.println(seconds);
	    			puzzleStream.close();
	    			System.out.println("Overwrote: " + fileToSave);
			} catch (IOException e) {
				System.err.println("Error: File: " + fileToSave + " not found!");
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
					cells[row][col].setOriginal(true);
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
				cells[row][col].setOriginal(false);
				cells[row][col].setValue("0");
				counter = 0;
			}
		}
		resetHint();
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
		
		// Iterate over Cells located in the same row.
		for(int j = 0; j < 9; j++) {
			int a = cells[row][j].getValue();
			if(a != 0) {
				values.add(a);
			}
		}
		// Iterate over Cells located in the same column.
		for(int i = 0; i < 9; i++) {
			int b = cells[i][col].getValue();
			if(b != 0) {
				values.add(b);
			}
		}
		// Determine which block the current Cell is located in.  
		tempX = row / 3;
		tempY = col / 3;
		int startX = tempX * 3;
		int startY = tempY * 3;
		
		// Iterate over Cells located in the same block. 
		for(int x = startX; x < startX + 3; x++) {
			for(int y = startY; y < startY + 3; y++) {
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
	 * The setValue method updates the value for a specified Cell. 
	 * @param cell    a Cell containing the value input by the user. 
	 */
	public void setValue(Cell cell) {
		int x = cell.getRowX();
		int y = cell.getColumnY();
		game[x][y] = cell.getValue();
	}
	
	/**
	 * The generateColors method produces a list of Colors used when partyModeOn is true. 
	 * @return	colorList - list of Colors
	 */
	public ArrayList<Color> generateColors() {
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
	 * The partyMode method changes the background of each highlighted Cell,
	 * 	to a randomly selected color from the colorList.
	 */
	public void partyMode(boolean mode) {
		
		partyModeOn = mode;
		
		if(partyModeOn == true) {
			Random rand = new Random();
			// Change the background of each highlighted Cell to a randomly selected color.  
			for(int row = 0; row < 9; row++) {
				for(int col = 0; col < 9; col++) {
					int randomColor = rand.nextInt(14);
					
					// Highlight Cells in block[0][1]. 
					if(row < 3) {
						if(col > 2 && col < 6) {
							cells[row][col].setBackground(colorList.get(randomColor));
						}
					}
					// Highlight Cells in block[1][0] and block[1][2].
					if(row > 2 && row < 6) {
						if(col < 3 || col > 5) {
							cells[row][col].setBackground(colorList.get(randomColor));
						}
					}
					// Highlight Cells in block[2][1]. 
					if(row > 5) {
						if(col > 2 && col < 6) {
							cells[row][col].setBackground(colorList.get(randomColor));
						}
					}
				}
			}
		}
		// Return highlighted Cells to their defualt background color. 
		else if(partyModeOn == false) {
			highlightCells();
		}
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
			clearGame();
			partyMode(false);
			resetTimer();
			resetHint();
		}
		// Generate JFileChooser for opening a file.
		else if (arg0.getActionCommand().equals("Read")) {
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
		// Start party mode. 
		else if (arg0.getActionCommand().equals("Start")) {
			partyMode(true);
		}
		// Stop party mode.
		else if (arg0.getActionCommand().equals("Stop")) {
			partyMode(false);
		}
		// Generate a random game containing 32 prefilled Cells.
		else if (arg0.getActionCommand().equals("Easy")) {
			clearGame();
			getHint.setEnabled(true);
			genRandomGame(32);
		}
		// Generate a random game containing 26 prefilled Cells.
		else if (arg0.getActionCommand().equals("Medium")) {
			getHint.setEnabled(true);
			genRandomGame(26);
		}
		// Generate a random game containing 20 prefilled Cells. 
		else if (arg0.getActionCommand().equals("Hard")) {
			getHint.setEnabled(true);
			genRandomGame(20);
		}
		// Provide the user with a random value
		else if(arg0.getActionCommand().contains("Hint")) {			
			if(availableHints > 0) {
				getHint();
				availableHints--;
				if(availableHints == 0) {
					getHint.setText("Hint ("+ availableHints + ")");
					getHint.setEnabled(false);
				}
				else {
					getHint.setText("Hint ("+ availableHints + ")");
				}
			}		
		}	
		// Process the value input by the user, then update the cell if it is a valid input
		else if(arg0.getActionCommand().matches("[1-9]+")) {
			Object jB = arg0.getSource();
			JButton jB1 = (JButton) jB;
			String s = jB1.getText();
			currentCell.processKeyValue(arg0, s);
		}
	}
	
	/**
	 * Generates a random COMPLETE sudoku game and ensures the game is solvable;
	 * 		then displaying randomly selected values to the GUI.. 
	 */
	public void genRandomGame(int valuesPlaced) {
		
		// Reset the gameboard 
		resetHint();
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
					genRandomGame(valuesPlaced);
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
		Random rand = new Random();
		String cValue;
		int rRow, rCol;
		int randCellValue;
		int valueCounter = 0;
		
		// Generates values to be placed on the board.
		while (valueCounter < valuesPlaced) {
			
			//Generates random row and column.
			rRow = rand.nextInt(9);
			rCol = rand.nextInt(9);
			
			// Grabs the value of the number in the cell of the random row and column. 
			randCellValue = solvedGame[rRow][rCol];
			cValue = Integer.toString(randCellValue);
			// randCellValue !=0 is to help catch errors and the second part of the if-statement
			//		makes sure the generated cell is empty.
			if (randCellValue != 0 && cells[rRow][rCol].getEmptyStatus() != false) {
				// Increment values placed. 
					valueCounter++;
					cells[rRow][rCol].setText(Integer.toString(randCellValue));
					cells[rRow][rCol].setEmptyStatus(false);
					cells[rRow][rCol].setValue(cValue);
					cells[rRow][rCol].setOriginal(true);
					cells[rRow][rCol].setEnabled(false);
					game[rRow][rCol] = randCellValue;
			}
		}
		updateStatusLabels();
		gameTimer();
	}
	
	/**
	 * The getHint method allows the user to receive a hint, at a random location on the game board.
	 */
	public void getHint() {
		
		Random rand = new Random();
		boolean valid = false;
		int randRow, randColumn;
		Cell randCell;
		
		// While the number generated is not valid. 
		while(!valid) {
			randRow = rand.nextInt(9);
			randColumn = rand.nextInt(9);
			randCell = cells[randRow][randColumn];
			if(randCell.getEmptyStatus() == true && solvedGame[randRow][randColumn] != 0) {
				randCell.setEmptyStatus(false);
				randCell.setIsHintActive(true);
				randCell.setEnabled(false);
				randCell.setBackground(Color.GREEN);
				randCell.setText(Integer.toString(solvedGame[randRow][randColumn]));
				game[randRow][randColumn] = solvedGame[randRow][randColumn];
				valid = true;
			}
			updateStatusLabels();
		}
	}
	
	/**
	 * The resetHint method resets the colors of the cells and number of available hints.
	 **/
	public void resetHint() {
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j ++) {
				// Returns Cell backgrounds to their original color
				cells[i][j].setBackground(cells[i][j].getColor());
				availableHints = 3;
				getHint.setText("Hint ("+ availableHints + ")");
			}
		}
	}
}
