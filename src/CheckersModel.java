import java.io.BufferedReader;  
import java.io.File;  
import java.io.FileNotFoundException;  
import java.io.FileReader;  
import java.io.IOException;  
import java.util.ArrayList;  
import java.util.Random;  
      
public class CheckersModel {     
                
    /* Our model stores all information about game, and handles all game logic.     
     *   
     * The public interface consist of:     
     *      Constructor, initializes all pieces to NONE  
     *      getPieceColour(location:int[]) returns the colour of the piece at specified location  
     *      getPieceKinged(location:int[]) returns true if piece kinged at specified location  
     *      getPieceSelected(location:int[]) returns true if piece selected at specified location  
     *      setPiece(location:int[]) sets the colour and kinged status of piece at passed location     
     *      selectPiece(location:int[]) sets the selected attribute of the piece to true     
     *      deselectAll() sets selected attribute of all pieces to false     
     *      defaultBoard() sets up a default board, 12 white & 12 black pieces  
     *      getNumberOfPieces(colour:String) returns number of pieces of specified colour on board  
     *      getValidMoves(location:int[]) returns array of valid locations of moves for piece  
     *          at passed location  
     *      movePiece(location:int[], newLocation:int[]) moves the piece at location to newLocation, taking  
     *          piece if necessary, kinging piece if necessary and handling multiple-jumping  
     *      loadGame(file:File) loads a previously saved game from passed text file  
     *      otherPieceCanJump(location:int[]) returns true if passed piece can't jump but another one can
     *      isValidLocation(location:int[]) returns true if passed 2-element array is a valid location  
     *        
     *------CHANGELOG REVISION 1 ----------------------------------------------------------------------  
     *        
     *      We have added 2 new public boolean attributes, isWhiteTurn and jumpOnlyMode. isWhiteTurn is true  
     *      when it is the white player's turn to play during gameplay. jumpOnlyMode is true when a player  
     *      has just jumped a piece and can perform a second jump. Both are public and accessed by the ViewController.  
     *       
     *      Locations are now 2-element int arrays, of the row followed by the column. Previously they were  
     *      integers in the bounds [0,31], but our code is greatly simplified by choosing to use a  
     *      (row,column) tuple. The only disadvantage is that we must check that each location is valid  
     *      before accessing its piece.  
     *        
     *      getPiece(location) replaced with separate getPieceColour, getPieceKinged, and getKingSelected  
     *      methods so that our view controller class never needs to access Piece methods. This improves our  
     *      uses hierarchy immensely, and simplifies the calls in the ViewController.  
     *        
     *      isValidLocation(location:int[]) checks whether a passed location corresponds with a valid square,  
     *      and returns a boolean. It is a public method so that the ViewController can call it  
     *      before accessing pieces at locations.  
     *         
     *      getValidMoves(location:int[]) returns an int[] of size at most 4 of locations (also int[]'s) that  
     *      the piece at the passed location can move to, including jumps. No variables are updated.  
     *        
     *      movePiece(location:int[], newLocation:int[]) moves the piece at location to its newLocation (gathered  
     *      from a previous call to getValidMoves), taking a piece if necessary, and kinging the moved piece  
     *      if necessary. It updates isWhiteTurn and jumpOnlyMode, and returns void.  
     *        
     *      boolean loadGame(file:File) takes a text file as input and loads the corresponding game. If file is  
     *      invalid, then it returns false, otherwise true.  
     *        
     *------------------------------------------------------------------------------------------------- 
     * 
     *------CHANGELOG REVISION 2 ---------------------------------------------------------------------- 
     * 
     *      2 public booleans have been added, playerIsWhite and vsComputer. playerIsWhite stores 
     *      the colour the player chooses if playing vs computer, and vsComputer is true if game is against 
     *      AI. 
     *       
     *      getValidMoves(location:int[]) has been modified to solve testing case 4.3; now when a non-king 
     *      jumps into a kinging spot it can't jump again, as per requirements. 
     *       
     *      movePiece(location:int[], newLocation:int[]) now calls the computer to do a second (or third etc)
     *      jump if they have the opportunity
     *       
     *      computerMove(location:int[]) finds all possible moves for the computer to make and randomly selects 
     *      one, then makes it. If computer is in midst of double/triple-jumping then the passed location is not 
     *      null, and stores the current location of the piece, to ensure no other jumping moves are selected. 
     *       
     *      loadGame(file:File) now loads in the new boolean attributes. 
     *       
     *      otherPieceCanJump(location:int[]) returns false if either the current piece has a jumping move 
     *      or if no pieces have jumping moves. If some other piece on the board can jump, then true is returned 
     *      and the player must choose one of those jumping moves. 
     * 
     *------------------------------------------------------------------------------------------------- 
     *         
     */
                
    //array of pieces is main data structure of model, one piece per square     
    private Piece piecesArray[][] = new Piece[8][8];     
      
    public boolean isWhiteTurn = true; //true if white player's turn  
    public boolean jumpOnlyMode = false; //true if player is double-jumping  
    public boolean playerIsWhite = true; //true if player chooses white vs computer        
    public boolean vsComputer = false; //true if player playing against computer  
        
    /**PUBLIC********************************************/
    /* default constructor, initializes each piece using     
    /* the Piece constructor with default arguments     
    /* Rev.1:no change except accommodating new location type 
    /* Rev.2: no changes made in revision 2 
    /****************************************************/
    CheckersModel() {     
        for (int row = 0; row < 8; row++)     
            for (int col = 0; col < 8; col++)    
                if (isValidLocation(new int[] {row,col}))    
                    piecesArray[row][col] = new Piece("NONE",false);     
    }     
               
    /**PUBLIC********************************************/
    /* returns the piece's colour at specified location  
    /* Rev.1: new method in revision 1     
    /* Rev.2: no changes made in revision 2 
    /****************************************************/
    public String getPieceColour(int[] loc) {  
          
        //return the pieces colour if location is valid  
        if (isValidLocation(loc))  
            return piecesArray[loc[0]][loc[1]].getColour();  
              
        //returns null otherwise  
        return null;  
    }  
          
    /**PUBLIC********************************************/
    /* returns true if piece kinged at specified location  
    /* Rev.1: new method in revision 1        
    /* Rev.2: no changes made in revision 2 
    /****************************************************/
    public boolean getPieceKinged(int[] loc) {  
              
        //return true if piece is kinged and location is valid  
        if (isValidLocation(loc))  
            return piecesArray[loc[0]][loc[1]].getKinged();  
              
        //returns false otherwise  
        return false;  
    }  
          
    /**PUBLIC********************************************/
    /* returns true if piece selected at specified location  
    /* Rev.1: new method in revision 1  
    /* Rev.2: no changes made in revision 2 
    /****************************************************/
    public boolean getPieceSelected(int[] loc) {  
              
        //return true if piece is selected and location is valid  
        if (isValidLocation(loc))  
            return piecesArray[loc[0]][loc[1]].getSelected();  
                  
        //returns false otherwise  
        return false;  
    }  
          
    /**PUBLIC********************************************/
    /* sets the piece at passed location to have specified     
    /* colour and kinged status.     
    /* Rev.1: no change beyond accommodating new location type  
    /* Rev.2: no changes made in revision 2 
    /****************************************************/
    public void setPiece(int loc[], String colour, boolean isKinged) {     
              
        //make sure location is valid     
        if (isValidLocation(loc)) 
            piecesArray[loc[0]][loc[1]] = new Piece(colour,isKinged);     
    }     
                
    /**PUBLIC********************************************/
    /* selects the piece at the specified location  
    /* Rev.1: no change beyond accommodating new location type     
    /* Rev.2: no changes made in revision 2 
    /****************************************************/
    public void selectPiece(int loc[]) {     
          
        //make sure loc is valid    
        if (isValidLocation(loc))  
            piecesArray[loc[0]][loc[1]].setSelected(true);       
    }     
            
    /**PUBLIC********************************************/
    /* de-selects all pieces  
    /* Rev.1: no change beyond accommodating new location type     
    /* Rev.2: no changes made in revision 2 
    /****************************************************/
    public void deselectAll() {     
        for (int row = 0; row < 8; row++)    
            for (int col = 0; col < 8; col++)    
                if (isValidLocation(new int[] {row,col}))    
                    piecesArray[row][col].setSelected(false);     
    }     
                
    /**PUBLIC********************************************/
    /* sets up a default board, with 12 white and 12      
    /* black pieces in their proper locations.     
    /* Rev.1: no change beyond accommodating new location type  
    /* Rev.2: no changes made in revision 2 
    /****************************************************/
    public void defaultBoard() {     
              
        //go through each piece and set its colour to the proper value     
        for (int row = 0; row < 8; row++) {     
            for (int col = 0; col < 8; col++) {    
                if (isValidLocation(new int[] {row,col})) {    
                    if (row <= 2)     
                        this.setPiece(new int[] {row,col},"BLACK",false);     
                    else if (row >= 5)     
                        this.setPiece(new int[] {row,col},"WHITE",false);     
                    else
                        this.setPiece(new int[] {row,col},"NONE",false);    
                }    
            }    
        }    
    }     
                
    /**PUBLIC********************************************/
    /* returns the number of pieces of the specified colour  
    /* Rev.1: no change beyond accommodating new location type  
    /* Rev.2: no changes made in revision 2 
    /****************************************************/
    public int getNumberOfPieces(String colour) {     
        int numberOfPieces = 0;     
                    
        //for each piece increment counter if colour matches specified colour     
        for (int row = 0; row < 8; row++) {    
            for (int col = 0; col < 8; col++) {    
                if (isValidLocation(new int[] {row,col}))    
                    if (piecesArray[row][col].getColour().equals(colour))    
                        numberOfPieces++;    
            }    
        }    
        return numberOfPieces;     
    }     
              
    /**PUBLIC********************************************/
    /* returns array of valid moves, null if no valid moves  
    /* Rev.1: new method in revision 1     
    /* Rev.2: solves testing case 4.3; now when a non-king 
    /* jumps into a kinging spot it can't jump again, as 
    /* per requirements. 
    /****************************************************/
    public int[][] getValidMoves(int[] loc) {    
      
        //validMoves array, at most a piece can have 4 valid moves. Initialize to null.  
        int[][] validMoves = new int[4][2];    
        for (int i = 0; i < validMoves.length; i++)  
            validMoves[i] = null;  
              
        //Information of the current piece (the one clicked)  
        int rowNum = loc[0];  
        int colNum = loc[1];  
      
        //becomes true if a jump found, and one can only jump if in jump-only mode  
        boolean hasJump = false;  
        if (this.jumpOnlyMode)  
            hasJump = true;  
      
        //If current piece is WHITE  
        if (getPieceColour(loc).equals("WHITE")) {   
      
            // Checks square to the upper right ^>  
            if (rowNum - 1 >= 0 && colNum + 1 <= 7) {  
      
                // If that square is unoccupied, then it is a valid move  
                if (getPieceColour(new int[] {rowNum-1, colNum+1}).equals("NONE") && !hasJump)   
                    validMoves[0] = new int[] {rowNum-1,colNum+1};  
      
                // If that square is occupied with an opponent's piece that can be jumped  
                else if (rowNum - 2 >= 0 && colNum + 2 <= 7 && getPieceColour(new int[] {rowNum-1, colNum+1}).equals("BLACK")  
                        && getPieceColour(new int[] {rowNum-2, colNum+2}).equals("NONE")) {   
                    validMoves[0] = new int[] {rowNum-2,colNum+2};   
                    hasJump = true;  
                }  
            }  
      
            // Checks the square to the upper left <^  
            if (rowNum-1 >= 0 && colNum-1 >= 0) {  
      
                //if square unoccupied  
                if (getPieceColour(new int[]{rowNum-1, colNum-1}).equals("NONE"))   
                    validMoves[1] = new int[] {rowNum-1,colNum-1};    
      
                //if square occupied with opponent's piece, check if jumpable  
                else if (rowNum-2 >= 0 && colNum-2 >= 0 && getPieceColour(new int[]{rowNum-1, colNum-1}).equals("BLACK")  
                        && getPieceColour(new int[]{rowNum-2, colNum-2}).equals("NONE"))   {  
                    validMoves[1] = new int[] {rowNum-2,colNum-2};   
                    hasJump = true;  
                }  
            }  
      
            // If current piece is a king, then it will gain extra possibilities   
            if (getPieceKinged(loc)) {   
      
                //Checks lower right square v>  
                if (rowNum+1 <= 7 && colNum+1 <= 7) {  
      
                    //if square unoccupied  
                    if (getPieceColour(new int[] {rowNum+1, colNum+1}).equals("NONE"))   
                        validMoves[2] = new int[] {rowNum+1, colNum+1};   
      
                    //if square occupied with opponent's piece, check if jumpable  
                    else if (rowNum+2 <= 7 && colNum+2 <= 7 && getPieceColour(new int[] {rowNum+1, colNum+1}).equals("BLACK")  
                            && getPieceColour(new int[] {rowNum+2, colNum+2}).equals("NONE")) {  
                        validMoves[2] = new int[] {rowNum+2,colNum+2};   
                        hasJump = true;  
                    }  
                }  
      
                //check lower left square  
                if (rowNum+1 <= 7 && colNum-1 >= 0) {  
      
                    //if square unoccupied  
                    if (getPieceColour(new int[]{rowNum+1, colNum-1}).equals("NONE"))   
                        validMoves[3] = new int[] {rowNum+1,colNum-1};   
      
                    //if square occupied with opponent's piece, check if jumpable  
                    else if (rowNum+2 <= 7 & colNum-2 >= 0 && getPieceColour(new int[]{rowNum+1, colNum-1}).equals("BLACK")  
                            && getPieceColour(new int[]{rowNum+2, colNum-2}).equals("NONE") ) {  
                        validMoves[3] = new int[] {rowNum+2,colNum-2};   
                        hasJump = true;  
                    }  
                }  
            }  
        }   
      
        // If current piece is BLACK  
        else  {   
      
            //check lower right square  
            if (rowNum+1 <= 7 && colNum+1 <= 7) {  
      
                //if square unoccupied  
                if (getPieceColour(new int[] {rowNum+1, colNum+1}).equals("NONE"))   
                    validMoves[0] = new int[] {rowNum+1, colNum+1};   
      
                //if square occupied with opponent's piece, check if jumpable  
                else if (rowNum+2 <= 7 && colNum+2 <= 7 && getPieceColour(new int[]{rowNum+1, colNum+1}).equals("WHITE")  
                        && getPieceColour(new int[]{rowNum+2, colNum+2}).equals("NONE")) {  
                    validMoves[0] = new int[] {rowNum+2,colNum+2};   
                    hasJump = true;  
                }  
            }  
      
            //check below left square  
            if (rowNum+1 <= 7 && colNum-1 >= 0) {  
      
                    
                //if square unoccupied  
                if (getPieceColour(new int[]{rowNum+1, colNum-1}).equals("NONE"))  
                    validMoves[1] = new int[] {rowNum+1,colNum-1};  
      
                //if square occupied with opponent's piece, check if jumpable  
                else if (rowNum+2 <= 7 && colNum-2 >= 0 && getPieceColour(new int[]{rowNum+1, colNum-1}).equals("WHITE")  
                        && getPieceColour(new int[]{rowNum+2, colNum-2}).equals("NONE")) {  
                    validMoves[1] = new int[] {rowNum+2,colNum-2};   
                    hasJump = true;  
                }  
            }  
      
            //additional moves if piece is king  
            if (getPieceKinged(loc)) {  
      
                //check upper right square  
                if (rowNum-1 >= 0 && colNum+1 <= 7) {  
      
                    //if square unoccupied  
                    if (getPieceColour(new int[]{rowNum-1, colNum+1}).equals("NONE"))   
                        validMoves[2] = new int[] {rowNum-1,colNum+1};         
      
                    //if square occupied with opponent's piece, check if jumpable  
                    else if (rowNum-2 >= 0 && colNum+2 <= 7 && getPieceColour(new int[]{rowNum-1, colNum+1}).equals("WHITE")  
                            && getPieceColour(new int[]{rowNum-2, colNum+2}).equals("NONE")) {  
                        validMoves[2] = new int[] {rowNum-2,colNum+2};   
                        hasJump = true;  
                    }  
                }  
      
                //check upper left square  
                if (rowNum-1 >= 0 && colNum-1 >= 0) {  
      
                    //if square unoccupied  
                    if (getPieceColour(new int[]{rowNum-1, colNum-1}).equals("NONE"))   
                        validMoves[3] = new int[] {rowNum-1,colNum-1};    
      
                    //if square occupied with opponent's piece, check if jumpable  
                    else if (rowNum-2 >= 0 && colNum-2 >= 0 && getPieceColour(new int[]{rowNum-1, colNum-1}).equals("WHITE")  
                            && getPieceColour(new int[]{rowNum-2, colNum-2}).equals("NONE"))  {  
                        validMoves[3] = new int[] {rowNum-2,colNum-2};   
                        hasJump = true;  
                    }  
                }  
            }      
        }   
      
        //add all the non null moves and non jumping moves (if there is a jump) to an arraylist  
        ArrayList<int[]> validList = new ArrayList<int[]>();  
        for (int i = 0; i < 4; i++) 
            if (validMoves[i] != null) 
                if (!hasJump || (hasJump && (Math.abs(validMoves[i][0] - loc[0]) > 1)))  
                    validList.add(validMoves[i]);  
          
        //if no moves, return null  
        if (validList.size() == 0)  
            return null;  
              
        //otherwise return the valid moves  
        return validList.toArray(new int[validList.size()][2]);    
    }    
              
    /**PUBLIC********************************************/
    /* moves a piece from loc to newLoc, kinging and taking    
    /* piece if necessary     
    /* Rev.1: new method in revision 1 
    /* Rev.2: calls computer to double jump if necessary 
    /****************************************************/
    public void movePiece(int[] loc, int[] newLoc) {    
      
        if (isValidLocation(loc) && isValidLocation(newLoc)) {    
      
            //if white player's turn    
            if (isWhiteTurn) {    
                if (newLoc[0] == 0 || getPieceKinged(loc))    
                    setPiece(newLoc,"WHITE",true);    
                else
                    setPiece(newLoc,"WHITE",false);    
            }    
      
            //if black player's turn    
            else if (!isWhiteTurn) {    
                if (newLoc[0] == 7 || getPieceKinged(loc))    
                    setPiece(newLoc,"BLACK",true);    
                else
                    setPiece(newLoc,"BLACK",false);    
            }    
      
            //if player took a piece    
            if (Math.abs(newLoc[0] - loc[0]) > 1) {    
      
                //take the piece    
                int[] takenLoc = new int[] {(Math.min(loc[0],newLoc[0]) + 1), (Math.min(loc[1],newLoc[1]) + 1)};    
                setPiece(takenLoc,"NONE",false);  
                    
                //reset jump mode to false    
                this.jumpOnlyMode = false;    
    
                //if there are moves in new location, AND player did not become a king in this turn, check if they're jumping moves    
                if (!(!getPieceKinged(loc) && (newLoc[0] == 0 || newLoc[0] == 7)) && getValidMoves(newLoc) != null) {    
                          
                    //for each possible move, check if it's a jump    
                    for (int i = 0; i < getValidMoves(newLoc).length; i++)  {  
      
                        //if there's a jump, enter jump only mode and take it, if computer 
                        if (Math.abs(getValidMoves(newLoc)[i][0] - newLoc[0]) > 1) {  
                            this.jumpOnlyMode = true;  
                            if (vsComputer && !isWhiteTurn == playerIsWhite) 
                            	computerMove(newLoc);
                        }  
                    }  
                }  
                  
                //delete original piece    
                setPiece(loc,"NONE",false);  
                    
                //switch turn if player can't jump again  
                if (!this.jumpOnlyMode)  
                    this.isWhiteTurn = !this.isWhiteTurn;  
                 
            }  
                
            //player didn't take a piece    
            else {  
                //delete original piece    
                setPiece(loc,"NONE",false);  
                    
                //switch turns and let computer move  
                this.isWhiteTurn = !this.isWhiteTurn;  
              
            }      
        }  
    }  
        
    /**PUBLIC********************************************/
    /* makes a move for the computer; AI. If jumping again 
    /* in a move, the location jumping from is passed, 
    /* otherwise it's null.   
    /* Rev.2: new method in revision 2  
    /****************************************************/
    public void computerMove(int[] doubleJumpPieceLoc) {  
    
        //if piece is double jumping, make move for that particular piece and switch turns  
        if (doubleJumpPieceLoc != null && this.jumpOnlyMode) {  
            movePiece(doubleJumpPieceLoc, getValidMoves(doubleJumpPieceLoc)[0]);  
            this.isWhiteTurn = !this.isWhiteTurn;  
            return;   
        }  
          
        //stores all possible moves the computer has  
        ArrayList<int[][]> possibleMoves = new ArrayList<int[][]>();  
            
        //iterate over all locations  
        for (int row = 0; row < 8; row++) {  
            for (int col = 0; col < 8; col++) {  
  
                //check location  
                int[] loc = new int[] {row,col};  
                if (isValidLocation(loc))  
                    if ((getPieceColour(loc).equals("WHITE") && !this.playerIsWhite) || (getPieceColour(loc).equals("BLACK") && this.playerIsWhite))  
                        if (getValidMoves(loc) != null && !otherPieceCanJump(loc)) 
                            //add all possible moves to list  
                            for (int[] move : getValidMoves(loc))  
                                possibleMoves.add(new int[][] {loc,move});  
            }  
        }  
  
        //randomly select a move, and move the piece  
        Random random = new Random();  
        if (possibleMoves.size() > 0) {  
            int i =  Math.abs(random.nextInt()) % possibleMoves.size();  
            movePiece(possibleMoves.get(i)[0],possibleMoves.get(i)[1]);  
        }  
    }  
      
    /**PUBLIC********************************************/
      
    /**PUBLIC********************************************/
    /* loads game from passed text file, returns true  
    /* if load was successful, false if error.  
    /* Rev.1: new method in revision 1 
    /* Rev.1: now loads new boolean attributes as well  
    /****************************************************/
    public boolean loadGame(File gameFile) {  
          
        //our reader  
        BufferedReader reader;  
              
        //read all pieces from file, and set board up  
        try {  
            reader = new BufferedReader(new FileReader(gameFile));  
            String lineString;  
            while ((lineString = reader.readLine()) != null) {  
                      
                //check that each line is in proper format  
                if (lineString.matches("[0,1,2,3,4,5,6,7],[0,1,2,3,4,5,6,7],(WHITE|BLACK|NONE),(true|false)")  
                        || lineString.matches("(ISWHITETURN|VSCOMPUTER|PLAYERISWHITE),(false|true)")) {  
                          
                    //split our line by its commas  
                    String lineStringArray[] = lineString.split(",");  
      
                    //if line has all data, set the piece properly  
                    if (lineStringArray.length == 4) {  
                        int[] loc = new int[] {Integer.parseInt(lineStringArray[0]),Integer.parseInt(lineStringArray[1])};  
                              
                        if (isValidLocation(loc))  
                            setPiece(loc,lineStringArray[2],Boolean.parseBoolean(lineStringArray[3]));  
                              
                        //if file tampered and invalid location, return false  
                        else {  
                            reader.close();  
                            return false;  
                        }  
      
                    }  
                    //if not on piece line, set boolean appropriately  
                    else if (lineStringArray.length == 2) {  
                        if (lineStringArray[0].equals("ISWHITETURN"))  
                            this.isWhiteTurn = Boolean.parseBoolean(lineStringArray[1]);  
                        else if (lineStringArray[0].equals("VSCOMPUTER"))  
                            this.vsComputer = Boolean.parseBoolean(lineStringArray[1]);  
                        else if (lineStringArray[0].equals("PLAYERISWHITE"))  
                            this.playerIsWhite = Boolean.parseBoolean(lineStringArray[1]);  
                    }  
                }  
                      
                //if file has been illegally modified, return false  
                else {  
                    reader.close();  
                    return false;  
                }  
            }  
            //close our reader when done  
            reader.close();  
              
        } catch (FileNotFoundException e1) {  
            System.out.println("FileNotFound Error");  
            return false;  
        } catch (IOException e) {  
            System.out.println("IOException Error");  
            return false;  
        }  
            
        //if no errors, return true 
        return true;  
    }  
         
    /**PUBLIC*******************************************/
    /* return true if player has another piece that can 
    /* jump, false if passed location piece can jump or 
    /* if no other pieces can jump.  
    /* Rev.2: new method in revision 2    
    /***************************************************/
    public boolean otherPieceCanJump(int[] loc) {  
    
        //if passed piece has a jump, return false 
        if (getValidMoves(loc) != null && Math.abs(getValidMoves(loc)[0][0] - loc[0]) > 1)  
            return false;  
    
        //check all other pieces to see if any can jump 
        for (int row = 0; row < 8; row++) {  
            for (int col = 0; col < 8; col++) {  
                int[] otherPieceLoc = new int[] {row,col};  
                    
                //ensure location is valid, and return true if a piece can jump 
                if (isValidLocation(otherPieceLoc))  
                    if (isWhiteTurn && getPieceColour(otherPieceLoc).equals("WHITE") || !isWhiteTurn && getPieceColour(otherPieceLoc).equals("BLACK")) 
                        if (getValidMoves(otherPieceLoc) != null && Math.abs(getValidMoves(otherPieceLoc)[0][0] - otherPieceLoc[0]) > 1)  
                            return true;  
            }  
        }  
          
        //if no pieces can jump, return false 
        return false;  
    } 
      
    /**PUBLIC*******************************************/
      
    /**PUBLIC********************************************/
    /* return true if location is a red square (i.e. valid)  
    /* Rev.1: new method in revision 1    
    /* Rev.2: no changes made in revision 2 
    /***************************************************/
    public boolean isValidLocation(int[] loc) {    
                  
        //check if location is valid    
        if (loc.length == 2 && loc[0] < 8 && loc[1] < 8 && loc[0] % 2 != loc[1] % 2)    
            return true;    
                
        //if invalid, return false  
        return false;    
    }   
}