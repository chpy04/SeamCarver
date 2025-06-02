import java.util.ArrayList;

//A SeamInfo stores information about a given pixel and links
//with other seam infos to form a seam to remove
class SeamInfo {
  //the pixel this SeamInfo Represents
  Pixel pixel;
  //the total sum of the path with the lowest cumulative energy up to this seam
  double totalWeight;
  //the seam that this seam is below
  SeamInfo cameFrom;

  //constructor points this pixels seam back to itself
  SeamInfo(Pixel pix, double totalWeight, SeamInfo cameFrom) {
    this.pixel = pix;
    this.totalWeight = totalWeight;
    this.cameFrom = cameFrom;
    pix.seam = this;
  }

  //the minimum seam between that seam and this seam by totalWeight
  SeamInfo minSeam(SeamInfo that) {
    if (this.totalWeight < that.totalWeight) {
      return this;
    } else {
      return that;
    }
  }

  //given the energy of a pixel, compute the total weight for
  //the that pixel's seam given that this seam is the least-weight path
  double computeNextTotalWeight(double energy) {
    return energy + this.totalWeight;
  }
  
  //given a removed seam soFar, remove this seams pixel, add the 
  //pixels who's energies might have changed by removing this seam to
  //the removedSeam, and recursively call to remove the seams above it
  RemovedSeam removeFullSeam(RemovedSeam removedSeamSoFar, boolean vertical) {
    ArrayList<Pixel> effectedPixels;
    if (vertical) {
      effectedPixels = this.pixel.remove();
    } else {
      effectedPixels = this.pixel.removeCol();
    }
    
    removedSeamSoFar.addToEffectedPixels(effectedPixels);
    //if the this.cameFrom is null, this seam is the top seam in the removal, 
    //which means that the peice above it must be a column head,
    //which is then sent to the seamRemoval. The piece above the last piece
    //in a seam being a colHead is an invarient of the data, which is why
    //we are casting here.
    if (this.cameFrom == null) {
      if (vertical) {
        removedSeamSoFar.setTopOfSeam(this.pixel.up);
      } else {
        removedSeamSoFar.setTopOfSeam(this.pixel.left);
      }
      return removedSeamSoFar;
    }
    return this.cameFrom.removeFullSeam(removedSeamSoFar, vertical);

  }

  //turns all the pixels coresponding to this seam red
  void turnSeamRed() {
    this.pixel.turnRed();
    if (this.cameFrom != null) {
      this.cameFrom.turnSeamRed();
    }
  }
  
  //add this seam's pixel back into the grid, and recursively call on the next seam
  void addSeamBack(boolean vertical) {
    this.pixel.addSeamBack(vertical);
    if (this.cameFrom != null) {
      this.cameFrom.addSeamBack(vertical);
    }
  }
  
  //return the greater weight between a given totalWeight and this seams totalWeight
  double weightBiggerThan(double prevMax) {
    return Math.max(prevMax, this.totalWeight);
  }
}
