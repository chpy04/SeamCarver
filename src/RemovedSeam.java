import java.util.ArrayList;

//A removed seam represents a seam that has already been removed
//from an image. It consists of a linked list of seamInfos from
//bottom seam, a column Head on the top of the seam, and an
//AL of pixels whose energies were effected by the seams removal.
class RemovedSeam {
  ArrayList<Pixel> effectedPixels;
  SeamInfo bottomSeam;
  IGridPiece topOfSeam;
  int topOfSeamPos;
  boolean verticalSeam;
  
  //constructor starts with a bottomSeam, the effected pixels and
  //column head are then accumlated later when the seam has been
  //fully removed
  RemovedSeam(SeamInfo bottomSeam, boolean verticalSeam) {
    this.effectedPixels = new ArrayList<Pixel>();
    this.bottomSeam = bottomSeam;
    this.topOfSeam = null;
    this.topOfSeamPos = -1;
    this.verticalSeam = verticalSeam;
  }
  
  //EFFECT: adds a column head to this removed seam if one is not already set
  void setTopOfSeam(IGridPiece head) {
    if (this.topOfSeam == null) {
      this.topOfSeam = head;
    } else {
      throw new IllegalStateException("cannnot change " +
              "the column head of a seam after it has been set");
    }
  }
  
  //EFFECT: adds an AL of pixels that have been effected by the removal of this seam
  //to this removedSeam's effectedPixels
  void addToEffectedPixels(ArrayList<Pixel> pixelsToAdd) {
    this.effectedPixels.addAll(pixelsToAdd);
  }
  
  //EFFECT: fix the energies of all the pixels effected by the removal of this seam
  void fixAllAffectedPixels() {
    //for each pixel, recalculate it's energy
    for (Pixel pix : this.effectedPixels) {
      pix.calcEnergy();
    }
  }
  
  //EFFECT: add this seams pixels back into the grid
  void addBackToGrid() {
    this.bottomSeam.addSeamBack(this.verticalSeam);
  }

  //given a header arrayList of either column or row headers, remove the 
  //top of seam of this removed seam from the arrayList and store the 
  //index in this removed seam
  <T> void removeTopFromHeaderList(ArrayList<T> headerList) {
    //loop through the given row/col Heads until the one at the top of this seam 
    //is found and remove it. Also keep track of the position for adding back later.
    for (int index = 0; index < headerList.size(); index += 1) {
      if (headerList.get(index) == this.topOfSeam) {
        headerList.remove(index);
        this.topOfSeamPos = index;
        break;
      } else if (index == headerList.size() - 1) {
        //there will always be a col/row header at the end of the seam, so if it is not found error
        throw new IllegalStateException("could not find col/row header to remove");
      }
    }
  }
}
