import java.util.*;
import javalib.worldimages.*;

//A PixelGraph represents an image that is being compressed. It consists of two AL,
//each of which contain heads that point into a network of connected pixels. The
//rows AL contains heads that point towards the first pixel in each row, and the
//cols AL contains heads that point towards the first pixel in each column. Pixels
//point to either other pixels, rowHeads, colHeads, or buffer pieces. Each pixel
//knows all 4 of its neighbors, but this approach allows a PixelGraph to easily access
//a given row or column.
class PixelGraph {
  ArrayList<RowHead> rows;
  ArrayList<ColHead> cols;
  Stack<RemovedSeam> history;
  
  //empty constructor makes a PixelGraph with no rows or columns
  PixelGraph() {
    this.rows = new ArrayList<RowHead>();
    this.cols = new ArrayList<ColHead>();
    this.history = new Stack<RemovedSeam>();
  }
  
  //main constructor, takes in a 2 dimensional AL of Pixels,
  //and constructs a network using them. AL input must be rectangular and must
  //have at least 1 row in it
  PixelGraph(ArrayList<ArrayList<Pixel>> pixels) {
    this.rows = new ArrayList<RowHead>();
    this.cols = new ArrayList<ColHead>();
    this.history = new Stack<RemovedSeam>();
    
    //if it is an empty ArrayList, create an empty rows and cols
    if (pixels.size() == 0) {
      this.rows = new ArrayList<RowHead>();
      this.cols = new ArrayList<ColHead>();
      return;
    }
    
    //if any of the rows are different sizes, throw an error
    int rowSize = pixels.get(0).size();
    for (ArrayList<Pixel> row : pixels) {
      if (row.size() != rowSize) {
        throw new IllegalArgumentException("pixelGraphs can only represent rectangular images");
      }
    }
    
    
    //for every row of pixels in the AL, add a row Head to this rows
    for (ArrayList<Pixel> row : pixels) {
      this.rows.add(new RowHead());
    }
    
    //for every pixel in the first row, add a col Head to this cols
    for (Pixel col : pixels.get(0)) {
      this.cols.add(new ColHead());
    }
    
    //two for loops iterate through every pixel in the array with an xpos and ypos
    for (int ypos = 0; ypos < pixels.size(); ypos += 1) {
      for (int xpos = 0; xpos < pixels.get(ypos).size(); xpos += 1) {
        
        if (xpos == 0) {
          //if the xpos is 0, the pixel is the first in a row. Connect that pixel to its rowHead
          this.rows.get(ypos).connectPieceRight(pixels.get(ypos).get(xpos));
        } else {
          //otherwise there is a piece to the left that can be connected left and right
          pixels.get(ypos).get(xpos - 1).connectPieceRight(pixels.get(ypos).get(xpos));
        }
        
        if (ypos == 0) {
          //if the ypos is 0, the pixel is the first in a column. Connect it to its colHead
          this.cols.get(xpos).connectPieceDown(pixels.get(ypos).get(xpos));
        } else {
          //otherwise connect it to the pixel above
          pixels.get(ypos - 1).get(xpos).connectPieceDown(pixels.get(ypos).get(xpos));
        }
        
        //if pixel is the last in a row connect its right to a buffer
        if (xpos == pixels.get(ypos).size() - 1) {
          pixels.get(ypos).get(xpos).connectPieceRight(new Buffer());
        }
        
        //if pixel is the last in a column connect its down to a buffer
        if (ypos == pixels.size() - 1) {
          pixels.get(ypos).get(xpos).connectPieceDown(new Buffer());
        }
      }
    }
    
    //ensure that this arrangement is valid (should always be true)
    if (!this.validArrangement()) {
      throw new IllegalStateException("Pixel links are not consistent");
    }
    
    //compute the energies for all the pixels in this graph
    this.computeEnergies();
  }
  
  //convenience constructor for taking in an image, parses to AL of AL of pixels and calls this
  PixelGraph(FromFileImage img) {
    this(new Util().parseImageIntoPixels(img));
  }
  
  //draw this pixelgraph as a computed pixel image
  ComputedPixelImage draw(int width, int height) {
    ComputedPixelImage ret = new ComputedPixelImage(width, height);
    
    //for each row, draw the row
    for (int row = 0; row < this.rows.size(); row += 1) {
      int xpos = 0;
      //using an xpos accumulator, iterate through row and draw each pixel on bg
      IGridPiece curPiece = this.rows.get(row);
      while (curPiece.hasNext()) {
        curPiece = curPiece.next();
        curPiece.placeOnImage(ret, xpos, row);
        xpos += 1;
      }
    }
    return ret;
  }
  
  //draw this pixelgraph using the energy of each pixel
  ComputedPixelImage drawEnergy(int width, int height) {
    //same as draw but have pixels draw their energies
    ComputedPixelImage ret = new ComputedPixelImage(width, height);
    for (int row = 0; row < this.rows.size(); row += 1) {
      int xpos = 0;
      IGridPiece curPiece = this.rows.get(row);
      while (curPiece.hasNext()) {
        curPiece = curPiece.next();
        curPiece.placeOnEnergyImage(ret, xpos, row);
        xpos += 1;
      }
    }
    return ret;
  }
  
  //draw this pixelgraph using the totalWeight of each pixel
  ComputedPixelImage drawWeight(int width, int height) {
    //to scale according to the totalWeight of each pixel, find the largest
    //totalWeight across the entire image:
    double maxWeight = 0;
    for (RowHead row : this.rows) {
      IGridPiece curPiece = row;
      while (curPiece.hasNext()) {
        curPiece = curPiece.next();
        maxWeight = curPiece.maxSeamWeight(maxWeight);
      }
    }
    
    //same as draw but have pixels draw their energies. Pass in the MaxWeight for scaling
    ComputedPixelImage ret = new ComputedPixelImage(width, height);
    for (int row = 0; row < this.rows.size(); row += 1) {
      int xpos = 0;
      IGridPiece curPiece = this.rows.get(row);
      while (curPiece.hasNext()) {
        curPiece = curPiece.next();
        curPiece.placeOnWeightImage(ret, xpos, row, maxWeight);
        xpos += 1;
      }
    }
    return ret;
  }
  
  //are all of the pixels in this pixel graph well formed
  boolean validArrangement() {
    for (RowHead row : this.rows) {
      IGridPiece curPiece = row;
      if (!curPiece.validSetup()) {
        return false;
      }
      while (curPiece.hasNext()) {
        curPiece = curPiece.next();
        if (!curPiece.validSetup()) {
          return false;
        }
      }
    }
    return true;
  } 
  
  //EFFECT: calculate the energies of all the pixels in this grid
  void computeEnergies() {
    for (RowHead row : this.rows) {
      IGridPiece curPiece = row;
      curPiece.calcEnergy();
      while (curPiece.hasNext()) {
        curPiece = curPiece.next();
        curPiece.calcEnergy();
      }
    }
  }

  //EFFECT: compute the seams for every pixel in this pixelgraph,
  //computes vertical/horizontal seams based on the parameter
  void computeSeams(boolean vertical) {
    if (vertical) {
      //for each row starting from the top, compute the seams
      for (RowHead row : this.rows) {
        // iterate through each piece and have it compute its seams
        IGridPiece curPiece = row;
        curPiece.computeSeam(vertical);
        while (curPiece.hasNext()) {
          curPiece = curPiece.next();
          curPiece.computeSeam(vertical);
        }
      }
    } else {
      //for each column starting from the left, compute the seams
      for (ColHead col : this.cols) {
        // iterate through each piece and have it compute its seams
        IGridPiece curPiece = col;
        curPiece.computeSeam(vertical);
        while (curPiece.hasNextCol()) {
          curPiece = curPiece.nextCol();
          curPiece.computeSeam(vertical);
        }
      }
    }
  }
  
  //get the seam from the bottom or right row with the least totalweight
  SeamInfo minSeam(boolean vertical) {
    //ask the last row to iterate through the pixels and return the 
    //seam with the loweest total weight
    if (vertical) {
      RowHead lastRow = this.rows.get(this.rows.size() - 1);
      return lastRow.minSeamInRow();
    } else {
      ColHead lastCol = this.cols.get(this.cols.size() - 1);
      return lastCol.minSeamInCol();
    }
  }
  
  //EFFECT: compute all the seams in this graph, find the minimum seam,
  //and have it turn all the pixels in that seam red
  void computeSeamAndPaintRed(boolean vertical) {
    //if there are no more seams to remove, do nothing
    if ((this.cols.size() == 0) || (this.rows.size() == 0)) {
      return;
    }
    
    //compute the seams, find the minimum, and turn it red
    this.computeSeams(vertical);
    SeamInfo minSeam = this.minSeam(vertical);
    minSeam.turnSeamRed();
  }

  
  //EFFECT: remove the minweight seam from the bottom row / right column
  //assuming all seamInfos have been calculated
  void removeSeam(boolean vertical) {
    //if there is no row to remove do nothing
    if ((this.cols.size() == 0) || (this.rows.size() == 0)) {
      return;
    }
    
    //find the minSeam and create a removedSeam for it
    SeamInfo minSeam = this.minSeam(vertical);
    RemovedSeam removal = minSeam.removeFullSeam(new RemovedSeam(minSeam, vertical), vertical);

    if (vertical) {
      //loop through the rowHeads until the one at the top of the seam is found and remove it
      removal.removeTopFromHeaderList(this.cols);
    } else {
      //loop through the colHeads until the one at the top of the seam is found and remove it
      removal.removeTopFromHeaderList(this.rows);
    }
    
    //re-calculate all the energies for the affected pixels through the RemovedSeam
    removal.fixAllAffectedPixels();
    this.history.add(removal);
    
    //Optional check for valid arrangement, after testing could be removed for speed
    if (!this.validArrangement()) {
      throw new IllegalStateException("Pixel links are not consistent");
    }
    
  }
  
  //EFFECT: adds the last removed seam from history back into the image
  void addLastRemovedSeam() {
    //if there is no seams in the history to remove, do nothing
    if (this.history.size() == 0) {
      return;
    }
    
    //get the seam to add back
    RemovedSeam seamToAddBack = this.history.pop();
    //add the pixels back
    seamToAddBack.addBackToGrid();
    //add the associated header back
    if (seamToAddBack.verticalSeam) {
      this.cols.add(seamToAddBack.topOfSeamPos, (ColHead)seamToAddBack.topOfSeam);
    } else {
      this.rows.add(seamToAddBack.topOfSeamPos, (RowHead)seamToAddBack.topOfSeam);
    }
    //fix the energies of the effected pixels
    seamToAddBack.fixAllAffectedPixels();
    //recompute the seams so drawing totalWeights is still correct
    this.computeSeams(true);
    
    /*if (!this.validArrangement()) {
      throw new IllegalStateException("broken state");
    }*/
  }
}

//Util class for parsing an image into a 2D AL of Pixels
class Util {
  ArrayList<ArrayList<Pixel>> parseImageIntoPixels(FromFileImage img) {
    ArrayList<ArrayList<Pixel>> pixels = new ArrayList<ArrayList<Pixel>>();
    
    //for each row add an arraylist to the result and add the pixels for that row
    for (int y = 0; y < img.getHeight(); y += 1) {
      pixels.add(new ArrayList<Pixel>());
      for (int x = 0; x < img.getWidth(); x += 1) {
        //add a pixel for each x, y with the color at that position
        pixels.get(y).add(new Pixel(img.getColorAt(x, y)));
      }
    }
    return pixels;
  }
}
