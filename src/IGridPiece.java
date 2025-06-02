import java.util.*;
import javalib.worldimages.*;

//An IGridPiece is one of a rowHead, a columnHead, a buffer, or a pixel
//represents any of the pieces in the pixelGraph network
interface IGridPiece {
  //the brightness value of the position above this GridPiece
  double brightnessUp();
  
  //the brightness value of the position to the right of this GridPiece
  double brightnessRight();
  
  //the brightness value of the position below this GridPiece
  double brightnessDown();
  
  //the brightness value of the position to the left of this GridPiece
  double brightnessLeft();
  
  //the brightness value of this piece
  double brightness();
  
  //EFFECT: calculate the energy of this piece and store it (if applicable)
  void calcEnergy();
  
  //EFFECT: given a piece 'right' that is right to this piece, fix both connections
  void connectPieceRight(IGridPiece right);
  
  //EFFECT: given a piece 'down' that is below this piece, fix both connections
  void connectPieceDown(IGridPiece down);
  
  //EFFECT: helper for connectPieceRight, points the left field of this piece to the passed 'left'
  void pointLeftToThat(IGridPiece left);
  
  //EFFECT: helper for connectPieceDown, points the up field of this piece to the passed 'up'
  void pointUpToThat(IGridPiece up);
  
  //is this piece consistent with all of its pointers
  boolean validSetup();
  
  //is the piece above this piece the same as the given piece
  boolean sameAbovePiece(IGridPiece piece);
  
  //is the piece below this piece the same as the given piece
  boolean sameBelowPiece(IGridPiece piece);
  
  //is the piece right of this piece the same as the given piece
  boolean sameRightPiece(IGridPiece piece);
  
  //is the piece left of this piece the same as the given piece
  boolean sameLeftPiece(IGridPiece piece);
  
  //is the piece to the right of this piece the same as the piece above the given piece
  boolean isRightOfThisSameAsTopOfThat(IGridPiece piece);
  
  //is the piece to the left of this piece the same as the piece above the given piece
  boolean isLeftOfThisSameAsTopOfThat(IGridPiece piece);
  
  //is the piece to the right of this piece the same as the piece below the given piece
  boolean isRightOfThisSameAsBottomOfThat(IGridPiece piece);
  
  //is the piece to the left of this piece the same as the piece below the given piece
  boolean isLeftOfThisSameAsBottomOfThat(IGridPiece piece);
  
  //EFFECT: computes the SeamInfo for this GridPiece if applicable
  void computeSeam(boolean vertical);

  //returns a seamInfo object with the least total weight of this pixel
  //and its left and right neighbors
  SeamInfo minSeamInfoOfThree(boolean vertical);

  //returns the minimum of the SeamInfo of this SeamInfo and that SeamInfo
  //compared by total weight
  SeamInfo minSeam(SeamInfo that);
  
  //minimum seamInfo in this row
  SeamInfo minSeamInRowAcc(SeamInfo minSoFar);
  
  //minimum seamInfo in this row
  SeamInfo minSeamInRow();
  
  SeamInfo minSeamInColAcc(SeamInfo minSoFar);
  
  SeamInfo minSeamInCol();
  
  //EFFECT: given an integer n, where negative is to the left and positive is to the right,
  //accumulate all the |n| pixels to the left or right this pixel and add them to soFar
  ArrayList<Pixel> getPixelsNToSide(int n, ArrayList<Pixel> soFar);
  
  ArrayList<Pixel> getPixelsNUpDown(int n, ArrayList<Pixel> soFar);
  
  //EFFECT: given an image, place this gridPiece onto the image at the x, y using its color
  void placeOnImage(ComputedPixelImage bg, int x, int y);
  
  //EFFECT: given an image, place this gridPiece onto the image at the x, y using its energy
  void placeOnEnergyImage(ComputedPixelImage bg, int x, int y);
  
  //EFFECT: given an image, place this gridPiece onto the image at the x, y using its weight
  void placeOnWeightImage(ComputedPixelImage bg, int x, int y, double maxWeight);
  
  //does this IGridPiece have a Piece to the right of it
  boolean hasNext();
  
  //the piece to the right of this IGridPiece
  IGridPiece next();
  
  //does this piece have a piece below it
  boolean hasNextCol();
  
  //the next piece below this piece
  IGridPiece nextCol();
  
  //EFFECT: given a grid piece, connects it above the right of this grid piece if possible
  void connectRightOfThistoDownOfThat(IGridPiece that);
  
  //EFFECT: given a grid piece, connects it above the left of this grid piece if possible
  void connectLeftOfThistoDownOfThat(IGridPiece that);
  
  //EFFECT: given a grid piece, connects it to the left of 
  //the piece above of this grid piece if possible
  void connectUpOfThistoRightOfThat(IGridPiece that);
  
  //EFFECT: given a grid piece, connects it to the left of 
  //the piece below of this grid piece if possible
  void connectDownOfThistoRightOfThat(IGridPiece that);
  
  //return the max seamWeight of this pieces seamWeight and the given prevMax
  double maxSeamWeight(double prevMax);
}

//AEdgePiece represents all pieces that boarder the pixel network, 
//Row Heads, Column Heads, and buffers
abstract class AEdgePiece implements IGridPiece {
  
  //an edge piece is always the same as the piece above it
  public boolean sameAbovePiece(IGridPiece piece) {
    return true;
  }
  
  //an edge piece is always the same as the piece below it
  public boolean sameBelowPiece(IGridPiece piece) {
    return true;
  }
  
  //an edge piece is always the same as the piece to the right of it
  public boolean sameRightPiece(IGridPiece piece) {
    return true;
  }
  
  //an edge piece is always the same as the piece to the left of it
  public boolean sameLeftPiece(IGridPiece piece) {
    return true;
  }
  
  //when checking in the diagonal, an edge piece always returns true
  public boolean isRightOfThisSameAsTopOfThat(IGridPiece piece) {
    return true;
  }
  
  //when checking in the diagonal, an edge piece always returns true
  public boolean isLeftOfThisSameAsTopOfThat(IGridPiece piece) {
    return true;
  }
  
  //when checking in the diagonal, an edge piece always returns true
  public boolean isRightOfThisSameAsBottomOfThat(IGridPiece piece) {
    return true;
  }
  
  //when checking in the diagonal, an edge piece always returns true
  public boolean isLeftOfThisSameAsBottomOfThat(IGridPiece piece) {
    return true;
  }
  
  //edge pieces always return 0 when asked the brightness of adjacent pieces
  public double brightnessUp() {
    return 0.0;
  }

  //edge pieces always return 0 when asked the brightness of adjacent pieces
  public double brightnessRight() {
    return 0.0;
  }

  //edge pieces always return 0 when asked the brightness of adjacent pieces
  public double brightnessDown() {
    return 0.0;
  }

  //edge pieces always return 0 when asked the brightness of adjacent pieces
  public double brightnessLeft() {
    return 0.0;
  }

  //edge pieces always have a brightness of 0
  public double brightness() {
    return 0.0;
  }
  
  //edge pieces do not have energy and therefore do not do anything here
  public void calcEnergy() {
    return;
  }

  //edge pieces do not have seams and therefore do not do anything here
  public void computeSeam(boolean vertical) {
    return;
  }

  //returns a seamInfo object with the least total weight of this pixel
  //and its left and right neighbors, returns null because seams point to
  //null on top row
  public SeamInfo minSeamInfoOfThree(boolean vertical) {
    return null;
  }

  //returns the minimum of the SeamInfo of this SeamInfo and that SeamInfo
  //compared by total weight. Edge piece doesn't have seam so always returns that.
  public SeamInfo minSeam(SeamInfo that) {
    return that;
  }
  
  //edge piece does not have seam so always returns minSoFar
  public SeamInfo minSeamInRowAcc(SeamInfo minSoFar) {
    return minSoFar;
  }
  
  //if minSeamInRow gets to the end of a row, the program is looking for 
  //a seam to remove from a row with no seams, errors.
  public SeamInfo minSeamInRow() {
    throw new IllegalStateException("row had no seams in it");
  }
  
  //edge piece does not have seam so always returns minSoFar
  public SeamInfo minSeamInColAcc(SeamInfo minSoFar) {
    return minSoFar;
  }

  //if minSeamInCol gets to the end of a col, the program is looking for 
  //a seam to remove from a col with no seams, errors.
  public SeamInfo minSeamInCol() {
    throw new IllegalStateException("col had no seams in it");
  }

  //edge piece means there are no more pixels to add to soFar in a given direction,
  //returns soFar
  public ArrayList<Pixel> getPixelsNToSide(int n, ArrayList<Pixel> soFar) {
    return soFar;
  }
  
  //edge piece means there are no more pixels to add to soFar in a given direction,
  //returns soFar
  public ArrayList<Pixel> getPixelsNUpDown(int n, ArrayList<Pixel> soFar) {
    return soFar;
  }
  
  //edge pieces are not drawn onto images
  public void placeOnImage(ComputedPixelImage bg, int x, int y) {
    return;
  }
  
  //edge pieces are not drawn onto images
  public void placeOnEnergyImage(ComputedPixelImage bg, int x, int y) {
    return;
  }
  
  //edge pieces are not drawn onto images
  public void placeOnWeightImage(ComputedPixelImage bg, int x, int y, double maxWeight) {
    return;
  }
  
  //If this method is called on an edge piece, no connections need to be fixed
  public void connectRightOfThistoDownOfThat(IGridPiece that) {
    return;
  }
  
  //If this method is called on an edge piece, no connections need to be fixed
  public void connectLeftOfThistoDownOfThat(IGridPiece that) {
    return;
  }
  
  //If this method is called on an edge piece, no connections need to be fixed
  public void connectUpOfThistoRightOfThat(IGridPiece that) {
    return;
  }
  
  //If this method is called on an edge piece, no connections need to be fixed
  public void connectDownOfThistoRightOfThat(IGridPiece that) {
    return;
  }
  
  //edge pieces do not have seams, the max will always be prevMax
  public double maxSeamWeight(double prevMax) {
    return prevMax;
  }
}

//A RowHead represents a header on the left side of the network that
//points to the first pixel in that row.
class RowHead extends AEdgePiece {
  IGridPiece next;
  
  //constructs empty row
  RowHead() {
    this.next = new Buffer();
  }
  
  //EFFECT: given a piece 'right' that is right to this piece, fix both connections
  public void connectPieceRight(IGridPiece right) {
    this.next = right;
    this.next.pointLeftToThat(this);
  }

  //given a piece 'up' that is above this piece, fix both connections
  public void connectPieceDown(IGridPiece below) {
    throw new IllegalArgumentException("cannot add piece below row head.");
  }

  //helper for connectPieceRight, points the left field of this piece to the passed 'left'
  //errors since a RowHead does not have a left
  public void pointLeftToThat(IGridPiece left) {
    throw new IllegalArgumentException("row head cannot point left.");
  }

  //helper for connectPieceUp, points the down field of this piece to the passed 'down'
  //errors since a RowHead does not have a down
  public void pointUpToThat(IGridPiece up) {
    throw new IllegalArgumentException("row head cannot point up.");
  }

  //is the next of this the same as piece
  public boolean sameRightPiece(IGridPiece piece) {
    //pieces are same by intensional equality 
    return this.next == piece;
  }

  //checks the heads one and only connection
  public boolean validSetup() {
    return this.next.sameLeftPiece(this);
  }
  
  //Head does not have seam, asks the piece it points to
  public SeamInfo minSeamInRow() {
    return this.next.minSeamInRow();
  }
  
  //A row Header always has a next to the right
  public boolean hasNext() {
    return true;
  }
  
  //the next is the piece it points to
  public IGridPiece next() {
    return this.next;
  }
  
  //row headers do not have pieces below
  public boolean hasNextCol() {
    return false;
  }
  
  //errors if user tries to get piece below
  public IGridPiece nextCol() {
    throw new IllegalArgumentException("this piece does not have a next below it");
  }
}

//A ColHead represents a header on top of the network that
//points to the first pixel in that column.
class ColHead extends AEdgePiece {
  IGridPiece next;

  //constructor makes a column with no rows
  ColHead() {
    this.next = new Buffer();
  }
  
  //given a piece 'right' that is right to this piece, fix both connections
  public void connectPieceRight(IGridPiece right) {
    throw new IllegalArgumentException("column head cannot point right.");
  }
  
  //EFFECT: given a piece 'up' that is above this piece, fix both connections
  public void connectPieceDown(IGridPiece below) {
    this.next = below;
    this.next.pointUpToThat(this);
  }
  
  //helper for connectPieceRight, points the left field of this piece to the passed 'left'
  //errors since a ColHead does not have a left
  public void pointLeftToThat(IGridPiece left) {
    throw new IllegalArgumentException("column head cannot point left.");
  }
  
  //helper for connectPieceUp, points the down field of this piece to the passed 'down'
  //down already points at next for up field
  public void pointUpToThat(IGridPiece above) {
    throw new IllegalArgumentException("column head cannot point up.");
  }

  //checks the heads one and only connection
  public boolean validSetup() {
    return this.next.sameAbovePiece(this);
  }
  
  //asks the next piece in this col for the minSeam
  public SeamInfo minSeamInCol() {
    return this.next.minSeamInCol();
  }

  //is this header next the same as piece
  public boolean sameBelowPiece(IGridPiece piece) {
    return this.next == piece;
  }
  
  //column headers do not have a next (next only refers to traversing through a row)
  public boolean hasNext() {
    return false;
  }
  
  //calling next on a piece who's hasnext was false should error
  public IGridPiece next() {
    throw new IllegalArgumentException("this piece does not have a next to the right");
  }
  
  //column headers always have a next piece below them
  public boolean hasNextCol() {
    return true;
  }
  
  //return the piece below (for a col header return the next)
  public IGridPiece nextCol() {
    return this.next;
  }
}

//A buffer represents a piece that lines the right and bottom of the image,
//gives pixels something to point towards
class Buffer extends AEdgePiece {
  //given a piece 'right' that is right to this piece, fix both connections
  public void connectPieceRight(IGridPiece right) {
    throw new IllegalArgumentException("buffer cannot point to anything.");
  }
  
  //given a piece 'below' that is below this piece, fix both connections
  public void connectPieceDown(IGridPiece below) {
    throw new IllegalArgumentException("buffer cannot point to anything.");
  }
  
  //helper for connectPieceRight, points the left field of this piece to the passed 'left'
  public void pointLeftToThat(IGridPiece left) {
    return;
  }
  
  //helper for connectPieceUp, points the down field of this piece to the passed 'down'
  //down already points at next for up field
  public void pointUpToThat(IGridPiece up) {
    return;
  }

  //buffers have no pointers and are always valid
  public boolean validSetup() {
    return true;
  }
  
  //buffers do not have a next
  public boolean hasNext() {
    return false;
  }

  //calling next on a piece with no next should error
  public IGridPiece next() {
    throw new IllegalArgumentException("this piece does not have a next to the right");
  }
  
  //buffers do not have a next
  public boolean hasNextCol() {
    return false;
  }
  
  //trying to get the piece below errors
  public IGridPiece nextCol() {
    throw new IllegalArgumentException("this piece does not have a next below it");
  }
}


