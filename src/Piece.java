 public class Piece { 
       
    /* This class represents a checkers piece, with all necessary attributes. 
     * 
     * Public interface consists of:  
     *      Constructor allowing for caller to pass in all information 
     *      getColour returns the colour of the piece 
     *      getKinged returns true if piece is kinged, false otherwise 
     *      getSelected returns true if piece is selected, false otherwise 
     *      setSelected(boolean) selects piece if passed true, deselects it otherwise
     *      
     *------CHANGELOG, REVISION 1-----------------------------------------------------      
     *      No changes were made 
     *--------------------------------------------------------------------------------
     *
     *------CHANGELOG, REVISION 2-----------------------------------------------------      
     *      No changes were made 
     *--------------------------------------------------------------------------------
     */
 
    private String colour; //colour is either "WHITE", "BLACK" or "NONE" 
    private boolean isKinged; //true if piece is a king 
    private boolean isSelected; //true if piece has been selected 
          
    /**PUBLIC********************************************/
    /* constructor for when all information is provided by caller 
    /****************************************************/
    Piece(String colour, boolean isKinged) { 
        
        //set colour, ensures that only specified values allowed 
        if (colour.equals("WHITE") || colour.equals("BLACK")) 
            this.colour = colour; 
        else
            this.colour = "NONE"; 
          
        //kings and selects piece if specified 
        this.isKinged = isKinged; 
    }
      
    /**PUBLIC********************************************/
    /* returns string of piece's colour 
    /****************************************************/
    public String getColour() { 
        return this.colour; 
    } 
      
    /**PUBLIC********************************************/
    /* returns true if piece is a king, false otherwise 
    /****************************************************/
    public boolean getKinged() { 
        return this.isKinged; 
    } 
      
    /**PUBLIC********************************************/
    /* returns true if piece is selected, false otherwise 
    /****************************************************/
    public boolean getSelected() { 
        return this.isSelected; 
    } 
      
    /**PUBLIC********************************************/
    /* selects piece if true is passed, deselects if otherwise 
    /****************************************************/
    public void setSelected(boolean selected) { 
        this.isSelected = selected; 
    } 
      
}