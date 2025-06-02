import java.util.*;
import javalib.worldimages.*;
import java.awt.Color;

//A Pixel represents a pixel in the image that is being compressed,
//it has access to its neighbors and its corresponding seamIfo.
class Pixel implements IGridPiece {
  IGridPiece up;
  IGridPiece right;
  IGridPiece left;
  IGridPiece down;
  Color color;
  double energy;
  SeamInfo seam;
  boolean drawRed;
  
  //pixels start off with 0 energy and all pointers null, these pointers are 
  //then fixed as they are linked up and it is given a seamInfo
  Pixel(Color color) {
    this.up = null;
    this.right = null;
    this.left = null;
    this.down = null;
    this.energy = 0;
    this.color = color;
    this.seam = null;
    this.drawRed = false;
  }
  
  //EFFECT: given a piece 'right' that is right to this piece, fix both connections
  public void connectPieceRight(IGridPiece right) {
    this.right = right;
    right.pointLeftToThat(this);
  }

  //EFFECT: given a piece 'below' that is below this piece, fix both connections
  public void connectPieceDown(IGridPiece below) {
    this.down = below;
    below.pointUpToThat(this);
  }

  //EFFECT: helper for connectPieceRight, points the left field of this piece to the passed 'left'
  public void pointLeftToThat(IGridPiece left) {
    this.left = left;
  }

  //EFFECT: helper for connectPieceUp, points the Up field of this piece to the passed 'above'
  //down already points at next for up field
  public void pointUpToThat(IGridPiece above) {
    this.up = above;
  }
  
  //EFFECT: calculate the energy of this pixel using the brightness of its 8 neighbors
  public void calcEnergy() {
    double horizEnergy = (this.left.brightnessUp() + (2 * this.left.brightness())
            + this.left.brightnessDown())
        - (this.right.brightnessUp() + (2 * this.right.brightness())
            + this.right.brightnessDown());
    double vertEnergy = (this.up.brightnessLeft() + (2 * this.up.brightness())
            + this.up.brightnessRight())
        - (this.down.brightnessLeft() + (2 * this.down.brightness())
            + this.down.brightnessRight());
    this.energy = Math.sqrt(Math.pow(horizEnergy, 2) + Math.pow(vertEnergy, 2));
  }
  
  //the brightness of the grid piece above this pixel
  public double brightnessUp() {
    return this.up.brightness();
  }

  //the brightness of the grid piece to the right of this pixel
  public double brightnessRight() {
    return this.right.brightness();
  }

  //the brightness of the grid piece below this pixel
  public double brightnessDown() {
    return this.down.brightness();
  }

  //the brightness of the grid piece to the left of this pixel
  public double brightnessLeft() {
    return this.left.brightness();
  }

  //the brightness of this pixel given by the average of its RGB values / 255
  public double brightness() {
    return ((double)(this.color.getBlue() + this.color.getGreen() + this.color.getRed())) 
        / (255.0 * 3.0);
  }

  //does this pixel's links with its neighbors constitute a valid setup. Valid setup means:
  // - going in any direction and then following that grid piece in the other direction
  //comes back to this pixel (if the igridpiece has pointers in that direction)
  // - for each corner, it is the same in either direction. For example top-left
  //is the same grid piece is going left-top (again barring running into an edge piece)
  public boolean validSetup() {
    return this.left.sameRightPiece(this)
        && this.right.sameLeftPiece(this)
        && this.up.sameBelowPiece(this)
        && this.down.sameAbovePiece(this)
        && this.up.isRightOfThisSameAsTopOfThat(this.right)
        && this.up.isLeftOfThisSameAsTopOfThat(this.left)
        && this.down.isRightOfThisSameAsBottomOfThat(this.right)
        && this.down.isLeftOfThisSameAsBottomOfThat(this.left);
  }

  //is the given piece the same as the grid piece above this pixel
  public boolean sameAbovePiece(IGridPiece piece) {
    return this.up == piece;
  }

  //is the given piece the same as the grid piece below this pixel
  public boolean sameBelowPiece(IGridPiece piece) {
    return this.down == piece;
  }

  //is the given piece the same as the grid piece to the right of this pixel
  public boolean sameRightPiece(IGridPiece piece) {
    return this.right == piece;
  }

  //is the give piece the same as the grid piece to the left of this pixel
  public boolean sameLeftPiece(IGridPiece piece) {
    return this.left == piece;
  }

  //is the grid piece right of this the same as the grid piece above the given piece
  public boolean isRightOfThisSameAsTopOfThat(IGridPiece piece) {
    return piece.sameAbovePiece(this.right);
  }

  //is the grid piece left of this the same as the grid piece above the given piece
  public boolean isLeftOfThisSameAsTopOfThat(IGridPiece piece) {
    return piece.sameAbovePiece(this.left);
  }

  //is the grid piece right of this the same as the grid piece below the given piece
  public boolean isRightOfThisSameAsBottomOfThat(IGridPiece piece) {
    return piece.sameBelowPiece(this.right);
  }

  //is the grid piece left of this the same as the grid piece below the given piece
  public boolean isLeftOfThisSameAsBottomOfThat(IGridPiece piece) {
    return piece.sameBelowPiece(this.left);
  }
  
  //EFFECT: compute the seamInfo corresponding to this pixel
  //relys on the invarient that every pixel above this one/next to this one
  // has already computed their seaminfo
  public void computeSeam(boolean vertical) {
    //find the seamInfo above that has the least energy
    SeamInfo minPrev;
    if (vertical) {
      minPrev = this.up.minSeamInfoOfThree(vertical);
    } else {
      minPrev = this.left.minSeamInfoOfThree(vertical);
    }
    double nextTotalWeight = this.energy;
    
    //if there is a seam above (this is not the top row), calculate this seams weight
    if (minPrev != null) {
      nextTotalWeight = minPrev.computeNextTotalWeight(this.energy);
    }

    //create the new seam, which links it with this pixel in both directions
    this.seam = new SeamInfo(this, nextTotalWeight, minPrev);
  }

  //finds the seam with the least weight of this seam and the left and right seams,
  //asks the left and right to return the minSeam of their seam and this seam to 
  //account for boarder pieces
  public SeamInfo minSeamInfoOfThree(boolean vertical) {
    if (vertical) {
      return this.right.minSeam(this.left.minSeam(this.seam));
    } else {
      return this.up.minSeam(this.down.minSeam(this.seam));
    }
  }
  
  //returns the seam with the least weight of this seaminfo and that seaminfo
  public SeamInfo minSeam(SeamInfo that) {
    return this.seam.minSeam(that);
  }
  
  //Helper for minSeamInRow, find the seam in this row with the least totalWeight
  //while tracking the least so far. checks if this seam is less than the 
  //minSoFar and calls recursively to the right
  public SeamInfo minSeamInRowAcc(SeamInfo minSoFar) {
    return this.right.minSeamInRowAcc(this.seam.minSeam(minSoFar));
  }
  
  //find the seam in this row with the least totalWieght, no seams have been found
  //yet so call the helper with this seam as the new lowest.
  public SeamInfo minSeamInRow() {
    return this.right.minSeamInRowAcc(this.seam);
  }
  
  //Helper for minSeamInCol, find the seam in this col with the least totalWeight
  //while tracking the least so far. checks if this seam is less than the 
  //minSoFar and calls recursively to the right
  public SeamInfo minSeamInColAcc(SeamInfo minSoFar) {
    return this.down.minSeamInColAcc(this.seam.minSeam(minSoFar));
  }
  
  //find the seam in this col with the least totalWieght, no seams have been found
  //yet so call the helper with this seam as the new lowest.
  public SeamInfo minSeamInCol() {
    return this.down.minSeamInColAcc(this.seam);
  }
  
  //EFFECT: adds pixels |n| to either the left or right (left if n is negative and 
  //right if n is positive) to a given arrayList
  public ArrayList<Pixel> getPixelsNToSide(int n, ArrayList<Pixel> soFar) {
    //base case
    if (n == 0) {
      return soFar;
    } else if (n > 0) {
      //add this pixel and recur right
      soFar.add(this);
      return this.right.getPixelsNToSide(n - 1, soFar);
    } else {
      soFar.add(this);
      //add this pixel and recur left
      return this.left.getPixelsNToSide(n + 1, soFar);
    }
  }
  
  //EFFECT: adds pixels |n| either above or below (above if n is negative and 
  //below if n is positive) to a given arrayList
  public ArrayList<Pixel> getPixelsNUpDown(int n, ArrayList<Pixel> soFar) {
    //base case
    if (n == 0) {
      return soFar;
    } else if (n > 0) {
      //add this pixel and recur right
      soFar.add(this);
      return this.down.getPixelsNToSide(n - 1, soFar);
    } else {
      soFar.add(this);
      //add this pixel and recur left
      return this.up.getPixelsNToSide(n + 1, soFar);
    }
  }
  
  //EFFECT: Removes this pixel and fixes all the connections on this row and below.
  //releys on the invarient that every row below has already had their pixels 
  //removed and links adjusted properly. 
  //returns an arraylist of pixels on this row whose energies could have 
  //been effected by removing a seam including this pixel.
  ArrayList<Pixel> remove() {
    //pixel energies use use all 8 neighbors. Because this pixel doesn't know which 
    //of the pixels below it was removed, up to 2 pixels on each side of it could
    //have been effected in the removal of this seam. Adds 2 pixels to the left and 
    //right to an arraylist that will be returned after fixing connections.
    ArrayList<Pixel> effectedPixels = new ArrayList<Pixel>();
    effectedPixels.addAll(this.getPixelsNToSide(2, new ArrayList<Pixel>()));
    effectedPixels.addAll(this.getPixelsNToSide(-2, new ArrayList<Pixel>()));
    
    //regardless of which pixel below was removed, the left and right of this
    //must be connected to be side by side
    this.left.connectPieceRight(this.right);
    
    //if down left is not the same as left down, the left down pixel was removed,
    //meaning the left must be connected up down with the pixel below this
    if (!(this.down.isLeftOfThisSameAsBottomOfThat(this.left))) {
      this.left.connectPieceDown(this.down);
    }
    
    //if down right is not the same as right down, the right down pixel was removed,
    //meaning the left must be connected up down with the pixel below this
    if (!(this.down.isRightOfThisSameAsBottomOfThat(this.right))) {
      this.right.connectPieceDown(this.down);
    }
   
    return effectedPixels;
  }
  
  ArrayList<Pixel> removeCol() {
    //pixel energies use use all 8 neighbors. Because this pixel doesn't know which 
    //of the pixels below it was removed, up to 2 pixels on each side of it could
    //have been effected in the removal of this seam. Adds 2 pixels to the left and 
    //right to an arraylist that will be returned after fixing connections.
    ArrayList<Pixel> effectedPixels = new ArrayList<Pixel>();
    effectedPixels.addAll(this.getPixelsNUpDown(2, new ArrayList<Pixel>()));
    effectedPixels.addAll(this.getPixelsNUpDown(-2, new ArrayList<Pixel>()));
    
    //this.left.connectPieceRight(this.right);
    this.up.connectPieceDown(this.down);
    
    /*if (!(this.down.isLeftOfThisSameAsBottomOfThat(this.left))) {
      this.left.connectPieceDown(this.down);
    }*/
    if (!(this.down.isRightOfThisSameAsBottomOfThat(this.right))) {
      this.down.connectPieceRight(this.right);
    }
    

    /*if (!(this.down.isRightOfThisSameAsBottomOfThat(this.right))) {
      this.right.connectPieceDown(this.down);
    }*/
    if (!(this.up.isRightOfThisSameAsTopOfThat(this.right))) {
      this.up.connectPieceRight(this.right);
    }
   
    return effectedPixels;
  }
  
  //EFFECT: place this pixel on a given image at the x y coordinate using this pixels color
  public void placeOnImage(ComputedPixelImage bg, int x, int y) {
    //if this pixel is part of a computed seam it will be drawn red
    if (this.drawRed) {
      bg.setPixel(x, y, Color.RED);
    } else {
      bg.setPixel(x, y, this.color);
    }
  }
  
  //EFFECT: place this pixel on a given image at the x y coordinate using this pixels brightness
  public void placeOnEnergyImage(ComputedPixelImage bg, int x, int y) {
    //if this pixel is part of a computed seam it will be drawn red
    if (this.drawRed) {
      bg.setPixel(x, y, Color.RED);
    } else {
      int greyScale = (int)((this.energy / Math.sqrt(32)) * 255);
      bg.setPixel(x, y, new Color(greyScale, greyScale, greyScale));
    }
  }

  //EFFECT: place this pixel on a given image at the x y coordinate using this
  // pixel's seam's totalWeight
  public void placeOnWeightImage(ComputedPixelImage bg, int x, int y, double maxWeight) {
    int weightVal = 0;
    if (this.seam != null) {
      weightVal = (int)(255 * this.seam.totalWeight / maxWeight);
    }
    //if this pixel is part of a computed seam it will be drawn red
    if (this.drawRed) {
      bg.setPixel(x, y, Color.RED);
    } else {
      bg.setPixel(x, y, new Color(weightVal, weightVal, weightVal));
    }
  }
  
  //EFFECT: from now on this pixel will be drawn red
  public void turnRed() {
    this.drawRed = true;
  }
  
  //does this pixel have a grid piece to the right, always true
  public boolean hasNext() {
    return true;
  }
  
  //next is always the piece to the right
  public IGridPiece next() {
    return this.right;
  }
  
  //Pixels always have a next piece below them
  public boolean hasNextCol() {
    return true;
  }
  
  //nextCol is always the piece below
  public IGridPiece nextCol() {
    return this.down;
  }
  
  //EFFECT: given a grid piece, connects it above the right of this grid piece
  public void connectRightOfThistoDownOfThat(IGridPiece that) {
    that.connectPieceDown(this.right);
  }
  
  //EFFECT: given a grid piece, connects it above the left of this grid piece
  public void connectLeftOfThistoDownOfThat(IGridPiece that) {
    that.connectPieceDown(this.left);
  }
  
  //EFFECT: given a grid piece, connects it to the left of 
  //the piece above of this grid piece
  public void connectUpOfThistoRightOfThat(IGridPiece that) {
    that.connectPieceRight(this.up);
  }
  
  //EFFECT: given a grid piece, connects it to the left of 
  //the piece below of this grid piece
  public void connectDownOfThistoRightOfThat(IGridPiece that) {
    that.connectPieceRight(this.down);
  }
  
  //add this pixel back into the network, either from a vertical or horizontal seam
  //fixes all the connections below and to the side for vertical seams and 
  //to the right and up/down for horizontal seams
  void addSeamBack(boolean vertical) {
    if (vertical) {
      //vertical seam adding back
      
      //fix all of this pixels connections to insert it back in (excluding up)
      this.left.connectPieceRight(this);
      this.connectPieceRight(this.right);
      this.connectPieceDown(this.down);
      
      //if left-down is not the same as down left, the left of this and 
      //the down-left of this need to be set as up/down neighbors
      if (!(this.down.isLeftOfThisSameAsBottomOfThat(this.left))) {
        this.down.connectLeftOfThistoDownOfThat(this.left);
      }
      
      //if right-down is not the same as down right, the right of this and 
      //the down-right of this need to be set as up/down neighbors
      if (!(this.down.isRightOfThisSameAsBottomOfThat(this.right))) {
        this.down.connectRightOfThistoDownOfThat(this.right);
      }
    } else {
      //horizontal seam adding back
      
      //fix all of this pixels connections to insert it back in (excluding left)
      this.up.connectPieceDown(this);
      this.connectPieceDown(this.down);
      this.connectPieceRight(this.right);
      
      //if down-right is not the same as right down, the down of this and 
      //the right-down of this need to be set as left/right neighbors
      if (!(this.down.isRightOfThisSameAsBottomOfThat(this.right))) {
        this.right.connectDownOfThistoRightOfThat(this.down);
      }
      
      //if up-right is not the same as right up, the up of this and 
      //the right-up of this need to be set as left/right neighbors
      if (!(this.up.isRightOfThisSameAsTopOfThat(this.right))) {
        this.right.connectUpOfThistoRightOfThat(this.up);
      }
    }
    
    //change the drawred so the pixel is draw normally again
    this.drawRed = false;
  }
  
  //the maximum seamWeight between this pixels seam and the prevMax weight
  //asks this seam for the miximum of the two
  public double maxSeamWeight(double prevMax) {
    return this.seam.weightBiggerThan(prevMax);
  }
}

