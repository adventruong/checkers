import java.awt.Color;    
import java.awt.Dimension;    
import java.awt.GridLayout;    
import java.awt.event.ActionEvent;    
import java.awt.event.ActionListener;    
import java.io.File;    
import java.io.FileNotFoundException;    
import java.io.PrintWriter;    
    
import javax.swing.BorderFactory;    
import javax.swing.BoxLayout;    
import javax.swing.ImageIcon;    
import javax.swing.JButton;    
import javax.swing.JFileChooser;    
import javax.swing.JFrame;    
import javax.swing.JLabel;    
import javax.swing.JOptionPane;    
import javax.swing.JPanel;    
import javax.swing.SwingConstants;   
import javax.swing.Timer; 
          
public class CheckersViewController {    
          
    /* This view controller class handles user interaction, and displays changes.     
     *     
     * The public interface consists of:     
     *      main(String[]) initiates our game (by calling initializeGame)        
     *               
     * The private methods consist of:      
     *      initializeGame() creates necessary elements to start a new game    
     *      newGameClicked() closes current game and starts a new one      
     *      squareClicked(location:String) handles user clicking a checker square       
     *      saveButtonClicked() saves the game to text file    
     *      quitButtonClicked() presents user with choice to quit or resign 
     *      loadGame() loads a selected game    
     *      updateUI(model:Model) checks the passed model and changes pieces in UI accordingly    
     *      showInitializationDialog() returns true if user wants to use default setup       
     *      runDelayed(numSeconds:int, r:Runnable) creates small delay for computer moves 
     *        
     *              
     *-----CHANGELOG - REVISION 1---------------------------------------------------------    
     *         
     *     The separate View and Controller classes of rev.0 have been combined into one class    
     *     that handles user input as well as updating the UI. This eliminates all cycles in    
     *     our uses relationship, and simplifies the code dramatically. Since our application    
     *     is simple, this does not violate the MVC pattern explained in class.    
     *         
     *     No new attributes added, the previously global ImageIcon attributes were moved    
     *     into the updateUI method, as they are not needed elsewhere.    
     *         
     *     All methods are now private (except Main), since our ViewController is at the    
     *     top of the uses hierarchy.    
     *         
     *     initializeGame() no longer displays the column and row labels, as there is no    
     *     text entry option now for custom setup. The UI produced has been modified in     
     *     this method with background colours, and a label showing the player's turn was     
     *     added. The change of the buttonArray from single dimensional to 2-dimensional also    
     *     affects this method, but the outcome is the same.    
     *         
     *     squareClicked() handles clicks in game mode, calling the necessary model methods    
     *     to get valid moves for a piece (and display them), and to move the piece if necessary    
     *     (and show the new UI by calling updateUI).    
     *         
     *     saveButtonClicked() saves the current game into a text file, the user can decide what    
     *     to call this file and where to save it.    
     *         
     *     loadGame() gets a selected file from a popup file browser, and calls upon the model    
     *     to load the game from this file. If there's an error, a dialog alerts the user and    
     *     appropriate action is taken.    
     *         
     *     updateUI() now updates the selected squares as well, and updates the label to show    
     *     whose turn it is.    
     *     
     * -------------------------------------------------------------------------------------  
     *   
     *-----CHANGELOG - REVISION 2-----------------------------------------------------------   
     *     
     *      inGameMode is a new attribute, true if player is playing checkers. delay is a new attribute that is true if the computer is 
     *      making a move.  Other global attributes are the saveButton and the quitButton.  
     *        
     *      We have removed all code, methods, and attributes related to the custom setup mode  
     *      as it is no longer required.  
     *        
     *      initializeGame() now adds the save and quit buttons to the UI, and there are no longer  
     *      any custom setup mode UI elements.  
     *        
     *      newGameClicked() now handles option for player vs computer. If this is chosen, the   
     *      player is given a choice of their piece colour, and that's stored in the model. If   
     *      the player chooses to be black, the first computer move is made.  
     *        
     *      squareClicked(location:String) now calls the computer to move and checks if the 
     *      game is over, displaying appropriate output if so.  
     *        
     *      saveButtonClicked() now saves the new attributes of the model.  
     *        
     *      quitButtonClicked() presents user with choice to resign or quit.  
     *        
     *      showInitilizationDialog() now has options for player vs player, player vs computer  
     *      and load game.  
     *       
     *      runDelayed(numSeconds:int, r:Runnable) creates delay for computer moves 
     *     
     * -------------------------------------------------------------------------------------  
     */
                 
    private CheckersModel model; //our private model      
                
    private boolean inGameMode= false; //true when in gameplay  
    private boolean delay = false; //true when computer is making a move
             
    //our global (but private to this class) elements of the UI     
    private JFrame mainFrame; //our frame to hold UI     
    private JButton buttonArray[][] = new JButton[8][8]; //stores a button for each checker square    
    private JLabel statusLabel = new JLabel(""); //our label to tell user what's currently happening    
    private JLabel turnLabel = new JLabel(""); //our label to tell user who's turn it is    
    private JButton saveButton = new JButton(""); //button to save game    
    private JButton quitButton = new JButton(""); //button to resign a game        
       
    /**PUBLIC********************************************/
    /* our main method, simply calls initialize game (on a    
    /* new ViewController), in a non-static way     
    /* Rev.1: no changes made in revision 1  
    /* Rev.2: no changes made in revision 2  
    /****************************************************/
    public static void main(String[] args) {     
        CheckersViewController viewController = new CheckersViewController();    
        viewController.initializeGame();    
    }     
         
    /*==PRIVATE==========================================*/
    /* initializes our model, and updates the UI    
    /* Rev.1: UI changes, handling for button array being    
    /* 2D, code cleaned up   
    /* Rev.2: no changes made in revision 2   
    /*===================================================*/
    private void initializeGame() {    
                 
        //initiate our model    
        this.model = new CheckersModel();  
         
        //initialize new frame to hold our UI      
        this.mainFrame = new JFrame("Checkers");      
        this.mainFrame.setPreferredSize(new Dimension(650,700));      
        this.mainFrame.setMinimumSize(new Dimension(500,650)); //smallest size that looks acceptable     
        this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);      
        this.mainFrame.setResizable(true);     
         
        //totalPanel is a JPanel containing the board panel and the button panel      
        JPanel totalPanel = new JPanel();      
        totalPanel.setLayout(new BoxLayout(totalPanel,BoxLayout.Y_AXIS));        
        totalPanel.setBorder(BorderFactory.createEmptyBorder(15,15,5,15));    
                 
        //this is the panel that has our board      
        JPanel checkerSquaresPanel = new JPanel(new GridLayout(8,8));      
                 
        //add all our buttons to our checkersquare panel    
        for (int row = 0; row < 8; row ++) {    
            for (int col = 0; col < 8; col++) {    
                         
                //if we're on a valid location, set button to red, and action command to its row followed by its column    
                if (model.isValidLocation(new int[] {row,col})) {    
                    buttonArray[row][col] = new JButton();      
                    buttonArray[row][col].setBorderPainted(false);      
                    buttonArray[row][col].setOpaque(true);    
                    buttonArray[row][col].setBackground(new Color(139,0,0));      
                    buttonArray[row][col].setActionCommand(Integer.toString(row) + Integer.toString(col));    
         
                    //action listener for our button, calls appropriate method to handle it      
                    buttonArray[row][col].addActionListener(new ActionListener() {      
                        public void actionPerformed(ActionEvent arg0) {      
                            squareClicked(arg0.getActionCommand());      
                        }      
                    });    
                }    
         
                //if we're on an invalid location, set button to white and action command to "-1"    
                else {    
                    buttonArray[row][col] = new JButton();      
                    buttonArray[row][col].setBorderPainted(false);      
                    buttonArray[row][col].setOpaque(true);    
                    buttonArray[row][col].setBackground(Color.white);      
                    buttonArray[row][col].setActionCommand(Integer.toString(row) + Integer.toString(col));    
         
                    //action listener for our invalid button, calls appropriate method to handle it      
                    buttonArray[row][col].addActionListener(new ActionListener() {      
                        public void actionPerformed(ActionEvent arg0) {      
                            squareClicked(arg0.getActionCommand());      
                        }      
                    });    
                }    
                       
                //add our button to our checkers panel    
                checkerSquaresPanel.add(this.buttonArray[row][col]);    
            }    
        }    
                  
        //add our checker board to our panel      
        totalPanel.add(checkerSquaresPanel);             
         
        //action listener for button to initiate new game      
        JButton newGameButton = new JButton("New Game");      
        newGameButton.addActionListener(new ActionListener() {      
            public void actionPerformed(ActionEvent arg0) {      
                newGameClicked();      
            }      
        });      
                 
        //button for saving the game    
        this.saveButton.setText("Save Game");    
        this.saveButton.setEnabled(false);    
        this.saveButton.addActionListener(new ActionListener() {    
            public void actionPerformed(ActionEvent arg0) {    
                saveButtonClicked();    
            }    
        });    
                
        //button for having the option whether to quit a game or call a draw    
        this.quitButton.setText("Quit");    
        this.quitButton.setEnabled(false);    
        this.quitButton.addActionListener(new ActionListener(){    
            public void actionPerformed(ActionEvent arg0){    
                quitButtonClicked();    
            }    
        });    
                
        //set up our default status label text and colour    
        this.statusLabel.setText("Press the \"New Game\" button to start.");    
        this.statusLabel.setHorizontalAlignment(SwingConstants.LEFT);     
        this.statusLabel.setForeground(Color.white);    
        this.turnLabel.setHorizontalAlignment(SwingConstants.LEFT);    
        this.turnLabel.setForeground(Color.white);    
                 
        //panel for the buttons at the bottom for initiating new games etc.      
        JPanel buttonPanel = new JPanel();      
        buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));    
         
        //create panel to store the status label     
        JPanel statusPanel = new JPanel();    
        statusPanel.setLayout(new BoxLayout(statusPanel,BoxLayout.X_AXIS));    
                 
        //add our elements to their proper panels      
        statusPanel.add(this.turnLabel);    
        statusPanel.add(this.statusLabel);     
        buttonPanel.add(newGameButton);      
        buttonPanel.add(this.saveButton);    
        buttonPanel.add(this.quitButton);    
                
        //set colours for background    
        Color bgColor = new Color(70,110,90);    
        buttonPanel.setBackground(bgColor);    
        statusPanel.setBackground(bgColor);    
        checkerSquaresPanel.setBackground(bgColor);    
        totalPanel.setBackground(bgColor);    
                 
        //add our button panel to our total panel, and the total panel to our frame    
        totalPanel.add(statusPanel);    
        totalPanel.add(buttonPanel);      
        mainFrame.add(totalPanel);      
         
        //set our frame to visible      
        mainFrame.pack();     
        mainFrame.setVisible(true);      
    }    
  
    /*==PRIVATE==========================================*/
    /* closes current game, resets everything and sets up     
    /* board in default way or custom, as selected by user.     
    /* Rev.1: no changes made in revision 1   
    /* Rev.2: handles if user wants to play vs computer,  
    /* getting their preference of colour and initiating game  
    /*===================================================*/
    private void newGameClicked() {     
         
        //close our current game, and delete its resources (garbage collection will delete them)     
        this.mainFrame.dispose();         
        this.saveButton = new JButton("");    
        this.quitButton = new JButton("");    
                
        //initialize a new game     
        this.initializeGame();       
        this.inGameMode = false;    
        String newGameOption = showInitializationDialog();     
         
        //if user closes dialog box    
        if (newGameOption.equals("CLOSE"))    
            return;    
         
        //if default setup    
        else if (newGameOption.equals("VSPLAYER")) {     
            this.model.defaultBoard();     
            this.statusLabel.setText("Default setup initiated.");    
            this.turnLabel.setText("White player's turn.                   ");    
            this.saveButton.setEnabled(true);    
            this.quitButton.setEnabled(true);    
            this.inGameMode = true;   
            //update our UI     
            updateUI(model);   
        }    
         
        //if player vs computer, let player choose colour  
        else if (newGameOption.equals("VSCOMPUTER")) {  
            this.model.defaultBoard();  
            model.vsComputer = true;  
                
            //get the user's choice of colour  
            String options[] = new String[] {"White","Black"};  
            int choice = JOptionPane.showOptionDialog(mainFrame, "Do you want to play as white or black?","Choose Piece Colour",    
                    JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,null,options,options[0]);  
            if (choice == 0)  {
                this.model.playerIsWhite = true;  
                //update our UI     
                updateUI(model);   
            }
            else if (choice == 1) {  
                this.model.playerIsWhite = false;  
                //update our UI     
                updateUI(model); 
                model.computerMove(null);  
                delay = true;
                runDelayed(10, new Runnable() { 
                        public void run() { 
                        updateUI(model); 
                        delay = false;
                        } 
                });
            }  
            else
                newGameClicked();  
                
            //set UI elements appropriately  
            this.statusLabel.setText("Game vs computer initiated.");  
            this.turnLabel.setText("White player's turn.                   ");  
            this.saveButton.setEnabled(true);  
            this.quitButton.setEnabled(true);  
            this.inGameMode = true;  
        }  
    
        //if user wishes to load game    
        else {    
            this.statusLabel.setText("Select a saved game.");    
            loadGame();    
        }    
           
    }     
         
    /*==PRIVATE==========================================*/
    /* handles square clicks, in custom setup and for     
    /* game play. locationString gives location in terms    
    /* of {row,column} in an int array.    
    /* Rev.1: handles square clicks when in game mode,    
    /* getting the valid moves when piece clicked, and    
    /* calling necessary model methods when piece is moved.  
    /* Rev.2: calls computer to move if necessary     
    /*===================================================*/
    private void squareClicked(String locationString) {     
            
        //get our location, and convert to array of row,col     
        int loc[] = new int[] {Integer.parseInt(locationString)/10, Integer.parseInt(locationString)%10};    
            
        //user is in game mode    
        if (inGameMode) {    
            
        	//check to see if computer is making move
        	if (delay){
        		this.statusLabel.setText("It is still the computers turn.");
        		return;
        	}
        	
            //user clicks invalid location    
            if (!model.isValidLocation(loc))    
                this.statusLabel.setText("Invalid. You may only click red squares.");    
         
            //user clicks valid location    
            else {    
         
                //if user clicks selected location    
                if (model.getPieceSelected(loc)) {    
                             
                    //if piece is already selected (and not blank) and not in jump only mode, deselect everything    
                    if (!model.getPieceColour(loc).equals("NONE") && !model.jumpOnlyMode) {    
                        model.deselectAll();    
                        this.statusLabel.setText("Piece deselected.");        
                    }    
                             
                    //otherwise, move the selected piece to the clicked location    
                    else if (model.getPieceColour(loc).equals("NONE")) {    
                        for (int row = 0; row < 8; row++) {    
                            for (int col = 0; col < 8; col++) {    
                                //there can only be one selected piece that's not blank at any given time, so this works    
                                if (model.isValidLocation(new int[] {row,col})) {    
                                    if (!(model.getPieceColour(new int[] {row,col}).equals("NONE")) && model.getPieceSelected(new int[] {row,col}))    
                                        model.movePiece(new int[] {row,col},loc);  
                                }    
                            }    
                        }    
                                 
                        //if player entered jump only mode    
                        if (model.jumpOnlyMode) {    
                                     
                            //deselect all, and then select piece you are jumping with    
                            this.statusLabel.setText("Jump again.");    
                            model.deselectAll();    
                            model.selectPiece(loc);    
                                     
                            //select all valid moves for jumping again    
                            if (model.getValidMoves(loc) != null) {    
                                for (int i = 0; i < model.getValidMoves(loc).length; i++)    
                                    model.selectPiece(model.getValidMoves(loc)[i]);    
                            }    
                        }    
                                 
                        //deselect everything if no more moves    
                        else {    
                            model.deselectAll();    
                            this.statusLabel.setText("Piece moved.");    
                        }    
                    }    
                }    
                         
                //user clicks unselected location and must jump    
                else if (!(model.getPieceSelected(loc)) && model.jumpOnlyMode)    
                    statusLabel.setText("You must choose a selected square.");    
         
                //user clicks unselected location and not in jump only mode    
                else {    
                    //if piece is not selected and is colour of current player's turn, deselect everything then select it, and show valid moves    
                    if (model.getPieceColour(loc).equals("WHITE") == model.isWhiteTurn && !(model.getPieceColour(loc).equals("NONE"))) {    
                        model.deselectAll();    
                        model.selectPiece(loc);    
         
                        //get the valid moves for that piece    
                        int validMoves[][] = model.getValidMoves(loc);    
                                 
                        //no valid moves    
                        if (validMoves == null)  
                            this.statusLabel.setText("No valid moves found.");  
                           
                        //player has other piece that can jump  
                        if (model.otherPieceCanJump(loc)) {  
                            validMoves = null;  
                            this.statusLabel.setText("Another piece can jump, you must jump with it.");  
                        }  
                            
                        //select all valid moves    
                        if (validMoves != null) {    
                            for (int i = 0; i < validMoves.length; i++)    
                                model.selectPiece(validMoves[i]);    
         
                            this.statusLabel.setText("Valid moves highlighted.");    
                        }    
                              
                    }    
                }    
            }    
            //update our UI   
            updateUI(model);   
              
            //move computer with a short delay so user not disoriented 
            if (model.vsComputer && !model.isWhiteTurn == model.playerIsWhite)  { 
                model.computerMove(null);  
                delay = true;
                runDelayed(10, new Runnable() { 
                        public void run() { 
                        updateUI(model); 
                        delay = false;
                        statusLabel.setText("Piece moved."); 
                        } 
                }); 
                
            } 
            
            //check to see if either player has no moves  
            boolean noWhiteMoves = true;  
            boolean noBlackMoves = true;  
            for (int row = 0; row < 8; row++) {  
                for (int col = 0; col < 8; col++) {  
                    int[] pieceLoc = new int[] {row,col};  
                    if (model.isValidLocation(pieceLoc)) {  
                        if (model.getPieceColour(pieceLoc).equals("WHITE") && model.getValidMoves(pieceLoc) != null)  
                            noWhiteMoves = false;  
                        else if (model.getPieceColour(pieceLoc).equals("BLACK") && model.getValidMoves(pieceLoc) != null)  
                            noBlackMoves = false;  
                    }  
                }  
            }  
                
            //if player is double jumping, don't bother checking for game over since they still have moves  
            if (model.jumpOnlyMode) {  
                noWhiteMoves = false;  
                noBlackMoves= false;  
            }  
            //don't check other player's moves if not their turn  
            if (model.isWhiteTurn)  
                noBlackMoves = false;  
            if (!model.isWhiteTurn)  
                noWhiteMoves = false;  
                
            //options for after the game is over   
            String[] gameOverOptions = {"New Game","Quit"};  
       
            //if game is over and white has lost   
            if (this.model.getNumberOfPieces("WHITE") == 0 || noWhiteMoves) {   
                    
                //update status labels  
                this.statusLabel.setText("Black player wins.");  
                this.turnLabel.setText("");  
                updateUI(model); 
                    
                //show the end game dialog     
                int choice = JOptionPane.showOptionDialog(mainFrame, "Black Player Wins","Game Over",     
                        JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,null,gameOverOptions,gameOverOptions[0]);     
                     
                //checks whether the user wants to start a new game or quit  
                if (choice == 0)   
                    newGameClicked();   
                else
                    this.mainFrame.dispose();  
                      
            }   
    
            //black player has lost  
            else if (this.model.getNumberOfPieces("BLACK") == 0 || noBlackMoves) {     
                    
                //update status labels  
                this.statusLabel.setText("White player wins.");  
                this.turnLabel.setText("");  
                updateUI(model); 
                    
                //show end game dialog     
                int choice = JOptionPane.showOptionDialog(mainFrame, "White Player Wins","Game Over",     
                        JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,null,gameOverOptions,gameOverOptions[0]);     
                    
                //checks whether the user wants to start a new game or quit  
                if (choice == 0)   
                    newGameClicked();   
                else
                    this.mainFrame.dispose();  
            }   
        }    
          
    }     
         
    /*==PRIVATE==========================================*/
    /* Saves the game to a text file for later loading    
    /* Rev.1: new method in revision 1    
    /* Rev.2: now saves new attributes in model  
    /*===================================================*/
    private void saveButtonClicked() {     
                 
        //show save dialog    
        JFileChooser saveChooser = new JFileChooser();    
                 
        //user doesn't hit cancel    
        if (saveChooser.showSaveDialog(this.mainFrame) == JFileChooser.APPROVE_OPTION) {    
                     
            //get the selected path and add a .txt file extension      
            File file = new File(saveChooser.getSelectedFile().getPath() + ".txt");    
                     
            //write to our save file the state of the game    
            try {    
                PrintWriter printWriter = new PrintWriter(file);    
                         
                //for each piece, print its location, colour, and kinged status    
                for (int row = 0; row < 8; row++) {    
                    for (int col = 0; col < 8; col++) {    
                        int[] loc = new int[] {row,col};    
                        if (model.isValidLocation(loc))    
                            printWriter.println(row + "," + col + "," + model.getPieceColour(loc) + "," + model.getPieceKinged(loc));    
                    }    
                }    
                         
                //add lines for necessary attributes  
                printWriter.println("ISWHITETURN," + model.isWhiteTurn);  
                printWriter.println("VSCOMPUTER," + model.vsComputer);  
                printWriter.println("PLAYERISWHITE," + model.playerIsWhite);    
                printWriter.close();    
            } catch (FileNotFoundException e) {    
                JOptionPane.showMessageDialog(null,"Error. File could not be saved.","File system error",JOptionPane.ERROR_MESSAGE);    
                newGameClicked();    
            }    
        }    
    }    
            
    /*==PRIVATE==========================================*/
    /* Allows a player to resign a game during their turn  
    /* Rev.2: new method in revision 2  
    /*===================================================*/
    private void quitButtonClicked(){    
            
        //dialog options  
        String[] quitResignOptions = {"Quit","Resign"};      
            
        //show the dialog      
        int quitResign = JOptionPane.showOptionDialog(mainFrame, "Would you like to resign or quit the game?","Quit",      
                JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,null,quitResignOptions,quitResignOptions[0]);  
            
        //user chooses quit   
        if (quitResign == 0)  
              this.mainFrame.dispose();  
            
        //if user clicks on resign    
        else if (quitResign == 1) {   
                    
            //initialize choice to quitting, this will be overriden by popup dialog  
            int choice = 0;  
                
            String[] resignOptions = {"Quit","New Game"};  
                
            //if white player resigned  
            if (model.isWhiteTurn == true)  
                choice = JOptionPane.showOptionDialog(mainFrame, "Black Player Wins","Game Over",     
                    JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,null,resignOptions,resignOptions[0]);     
    
            //black player resigned  
            else    
                choice = JOptionPane.showOptionDialog(mainFrame, "White Player Wins","Game Over",     
                        JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,null,resignOptions,resignOptions[0]);     
                     
           //user clicked new game  
           if (choice == 1)  
                newGameClicked();   
    
           //quit game otherwise  
           else
               this.mainFrame.dispose();  
       }  
    
      //if user closes dialog box, resume game  
      else
          return;       
      
    }    
            
    /*==PRIVATE==========================================*/
    /* Loads a previously saved game file    
    /* Rev.1: new method in revision 1    
    /* Rev.2: no changes made in revision 2  
    /*===================================================*/
    private void loadGame() {    
                 
        //show a dialog to choose game to load    
        this.turnLabel.setText("");    
        final JFileChooser fileChooser = new JFileChooser();    
        int returnVal = fileChooser.showOpenDialog(this.mainFrame);    
                 
        //if they cancel, open the new game dialog    
        if (returnVal == 1)    
            newGameClicked();    
                 
        //otherwise, check that the file is valid (i.e. .txt)    
        else {    
                     
            //get path    
            String filePath = fileChooser.getSelectedFile().getName();    
                     
            //load the game if file is correct format    
            if (filePath.substring(filePath.lastIndexOf('.')).equals(".txt")) {    
                         
                //load our game    
                boolean loadedProperly = this.model.loadGame(fileChooser.getSelectedFile());    
                         
                //if loaded properly, tell user and enable save button    
                if (loadedProperly && model.getNumberOfPieces("WHITE") <= 12 && model.getNumberOfPieces("BLACK") <= 12) {    
                             
                    this.statusLabel.setText("Game loaded");    
                    this.inGameMode = true;    
                    this.saveButton.setEnabled(true);    
                    this.quitButton.setEnabled(true);    
                }    
                         
                //if file modified, show popup and reset game    
                else {    
                    JOptionPane.showMessageDialog(null,"Error. File has been modified illegally.","File loading error",JOptionPane.ERROR_MESSAGE);    
                    newGameClicked();    
                }    
            }    
                     
            //if they select a file that is not .txt, show error message and invoke newGameClicked()    
            else {    
                JOptionPane.showMessageDialog(null,"Error. Invalid file format. Must be .txt file.","File loading error",JOptionPane.ERROR_MESSAGE);    
                newGameClicked();    
            }      
        }    
        updateUI(this.model);    
    }    
             
    /*==PRIVATE==========================================*/
    /* displays a popup dialog to determine if new game will     
    /* be using default or custom setup. returns true if user     
    /* wants default setup, false if user wants custom setup.    
    /* Rev.1: Option to load game added     
    /* Rev.2: options for vs computer or vs player  
    /*===================================================*/
    private String showInitializationDialog() {      
                 
        String[] options = {"vs Person","vs Computer","Load Game"};    
         
        //show the dialog      
        int choice = JOptionPane.showOptionDialog(mainFrame, "Would you like to use the default setup, or a custom one?","New Game",      
                JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,null,options,options[0]);      
         
        //return string depending on user click    
        if (choice == 0)    
            return "VSPLAYER";  
        else if (choice == 1)  
            return "VSCOMPUTER";  
        else if (choice == 2)  
            return "LOAD";    
        else
            return "CLOSE";  
    }      
             
    /*==PRIVATE==========================================*/
    /* creates small delay when computer moves so user 
    /* not disoriented      
    /* Rev.2: new method in revision 2 
    /*===================================================*/
    private void runDelayed(int numSeconds, final Runnable r) { 
  
        //create our timer   
        final Timer timer = new Timer(numSeconds * 100, null); 
  
        //add the action listener 
        timer.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
                try { 
                    r.run(); 
                } finally { 
                    timer.stop(); 
                } 
            } 
        }); 
  
        //start the timer 
        timer.start(); 
    } 
      
    /*==PRIVATE==========================================*/
    /* checks each piece in the passed model, and sets the     
    /* button images for each square accordingly    
    /* Rev.1: checks if square selected. Fetching images now    
    /* within method, instead of as global variables.  
    /* Rev.2: updates whose turn it is  
    /*===================================================*/
    private void updateUI(CheckersModel model) {      
         
        //iterate through all pieces in our model     
        for (int row = 0; row < 8; row++) {      
            for (int col = 0; col < 8; col++) {    
                           
                //check that location is valid    
                if (model.isValidLocation(new int[] {row,col})) {    
                           
                    //piece is black king    
                    if (model.getPieceKinged(new int[] {row,col}) && model.getPieceColour(new int[] {row,col}).equals("BLACK")) {      
                        if (model.getPieceSelected(new int[] {row,col}))    
                            buttonArray[row][col].setIcon(new ImageIcon((getClass().getResource("/images/BlackKingSelected.png"))));    
                        else
                            buttonArray[row][col].setIcon(new ImageIcon((getClass().getResource("/images/BlackKing.png"))));    
                    }    
         
                    //piece is black (not king)      
                    else if (model.getPieceColour(new int[] {row,col}).equals("BLACK")) {     
                        if (model.getPieceSelected(new int[] {row,col}))    
                            buttonArray[row][col].setIcon(new ImageIcon((getClass().getResource("/images/BlackSelected.png"))));    
                        else
                            buttonArray[row][col].setIcon(new ImageIcon((getClass().getResource("/images/Black.png"))));     
                    }    
         
                    //piece is white king     
                    else if (model.getPieceKinged(new int[] {row,col}) && model.getPieceColour(new int[] {row,col}).equals("WHITE")) {       
                        if (model.getPieceSelected(new int[] {row,col}))    
                            buttonArray[row][col].setIcon(new ImageIcon((getClass().getResource("/images/WhiteKingSelected.png"))));    
                        else
                            buttonArray[row][col].setIcon(new ImageIcon((getClass().getResource("/images/WhiteKing.png"))));    
                    }    
         
                    //piece is white (not king)     
                    else if (model.getPieceColour(new int[] {row,col}).equals("WHITE")) {    
                        if (model.getPieceSelected(new int[] {row,col}))    
                            buttonArray[row][col].setIcon(new ImageIcon((getClass().getResource("/images/WhiteSelected.png"))));    
                        else
                            buttonArray[row][col].setIcon(new ImageIcon((getClass().getResource("/images/White.png"))));    
                    }    
         
                    //no piece on square     
                    else {      
                        if (model.getPieceSelected(new int[] {row,col}))    
                            buttonArray[row][col].setIcon(new ImageIcon((getClass().getResource("/images/EmptySelected.png"))));    
                        else
                            buttonArray[row][col].setIcon(null);    
                    }    
                }    
            }      
        }    
    
        //if in game mode, show current colour's turn    
        if (this.inGameMode) {    
            if (model.isWhiteTurn)    
                this.turnLabel.setText("White player's turn.                   ");    
            else
                this.turnLabel.setText("Black player's turn.                   ");    
        }    
    
    }    
    
}