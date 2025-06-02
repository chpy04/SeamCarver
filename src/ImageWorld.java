import javalib.impworld.*;
import javalib.worldimages.*;

/*
INSTRUCTIONS FOR USE:
  - Press spacebar to toggle on and off removal
  - Press 'c' to cycle between displaying the image, energies, and sums of weights
  - Press 't' to toggle on and off reinsertion
  - Press 'v' to remove a single vertical seam
  - Press 'h' to remove a single horizontal seam
  - Press 'r' to reinsert a single seam
 */

//an ImageWorld handles the UI for a Pixelgraph, dealing with the user input
class ImageWorld extends World {
  //the PixelGraph for this world
  PixelGraph pixelImg;
  
  //the starting height and width of the image (does not change)
  int width;
  int height;
  
  //if true, on the next tick, calculate a seam and paint it red,
  //if false, a seam has been calculated and can be removed
  boolean paintRed;
  
  //whether or not the world will automatically remove seams in the ontick
  boolean autoRemove;
  
  //whether or not the world will automatically add back seams in the ontick
  boolean autoAddBack;
  
  //whether the seam being removed is horizontal or vertical
  boolean verticalSeamRemoval;
  
  //true if the draw method will display normal colors
  boolean showImage;
  
  //true if the draw method will display the energy of the pixels in greyscale
  boolean showEnergy;

  //true if the draw method will display the totalWeight of the seam corresponding
  //to each pixel
  boolean showWeight;
  
  //constructor for an imageWorld takes in a filename for the image to be worked on
  ImageWorld(String fileName) {
    FromFileImage img = new FromFileImage(fileName);
    this.pixelImg = new PixelGraph(img);
    this.height = (int)img.getHeight();
    this.width = (int)img.getWidth();
    this.paintRed = true;
    this.autoRemove = false;
    this.autoAddBack = false;
    this.showImage = true;
    this.showEnergy = false;
    this.showWeight = false;
  }

  //makes a WorldScene to draw this image
  public WorldScene makeScene() {
    WorldScene canvas = new WorldScene(this.width, this.height);
    if (this.showImage) {
      //normal draw case
      canvas.placeImageXY(
          this.pixelImg.draw(this.width, this.height),
              this.width / 2, this.height / 2);  
    } else if (this.showEnergy) {
      //draw energies case
      canvas.placeImageXY(
          this.pixelImg.drawEnergy(this.width, this.height),
              this.width / 2, this.height / 2);
    } else if (this.showWeight) {
      //draw totalWeight case
      canvas.placeImageXY(
          this.pixelImg.drawWeight(this.width, this.height),
              this.width / 2, this.height / 2);
    }
    return canvas;
  }

  //method that is continuously called on the imageWorld
  public void onTick() {
    //if auto-removal is on, automatically paint red/remove seams
    if (this.autoRemove) {
      if (!paintRed) {
        //if not paintRed, remove the prev calculated seam, and pick randomly
        //between horizontal and vertical for the next seam
        this.pixelImg.removeSeam(this.verticalSeamRemoval);
        this.verticalSeamRemoval = (Math.random() > .5);
      } else {
        //if paintRed, compute the next seam
        this.pixelImg.computeSeamAndPaintRed(this.verticalSeamRemoval);
      }
      //change the paintedRed
      this.paintRed = !this.paintRed;
      return;
    } else {
      //if the autoRemove is turned off, but a seam has already been calculated, 
      //remove it
      if (!this.paintRed) {
        this.pixelImg.removeSeam(this.verticalSeamRemoval);
        this.paintRed = !this.paintRed;
      }
      //if autoAddBack is on, add back a seam
      if (this.autoAddBack) {
        this.pixelImg.addLastRemovedSeam();
      }
    }
  }

  //In the event that space is pressed
  //autoRemove of this ImageWorld is negated
  public void onKeyEvent(String key) {
    //if any key is pressed, before acting on it, remove any pre-computed seams
    //to not end up with red seams in the middle of the image that never get 
    //removed
    if (!this.paintRed) {
      this.pixelImg.removeSeam(this.verticalSeamRemoval);
      this.paintRed = !this.paintRed;
    }
    
    //space bar toggles autoremoval
    if (key.equals(" ")) {
      this.autoAddBack = false;
      this.autoRemove = !this.autoRemove;
    }
    
    //r adds the last removed seam back into the image
    if (key.equals("r")) {
      this.pixelImg.addLastRemovedSeam();
    }
    
    //t toggles auto reinsertion
    if (key.equals("t")) {
      this.autoRemove = false;
      this.autoAddBack = !this.autoAddBack;
    }
    
    //v computes and removes a horizontal seam
    if (key.equals("v")) {
      this.pixelImg.computeSeamAndPaintRed(true);
      this.pixelImg.removeSeam(true);
    }
    
    //h computes and removes a vertical seam
    if (key.equals("h")) {
      this.pixelImg.computeSeamAndPaintRed(false);
      this.pixelImg.removeSeam(false);
    }
    
    //c toggles between drawing the normal image, energies, and weights
    if (key.equals("c")) {
      if (this.showImage) {
        this.showImage = false;
        this.showEnergy = true;
        this.showWeight = false;
      }
      else if (this.showEnergy) {
        this.showImage = false;
        this.showEnergy = false;
        this.showWeight = true;
      }
      else {
        this.showImage = true;
        this.showEnergy = false;
        this.showWeight = false;
      }
    }
  }
}
