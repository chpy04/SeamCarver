import java.util.ArrayList;
import tester.*;
import javalib.worldimages.*;
import java.awt.Color;

class ExamplesSqueeze {
  Pixel TL = new Pixel(Color.BLACK);
  Pixel TM = new Pixel(Color.RED);
  Pixel TR = new Pixel(Color.YELLOW);
  Pixel ML = new Pixel(Color.GREEN);
  Pixel MM = new Pixel(Color.BLUE);
  Pixel MR = new Pixel(Color.WHITE);
  Pixel BL = new Pixel(Color.CYAN);
  Pixel BM = new Pixel(Color.ORANGE);
  Pixel BR = new Pixel(Color.MAGENTA);

  Buffer bf = new Buffer();
  ColHead ch = new ColHead();
  RowHead rh = new RowHead();

  ArrayList<Pixel> row1 = new ArrayList<Pixel>();
  ArrayList<Pixel> row2 = new ArrayList<Pixel>();
  ArrayList<Pixel> row3 = new ArrayList<Pixel>();
  ArrayList<ArrayList<Pixel>> grid1 = new ArrayList<ArrayList<Pixel>>();
  PixelGraph PG1;
  PixelGraph PG0;
  PixelGraph balloons = new PixelGraph(new FromFileImage("balloons.jpg"));
  PixelGraph skiing = new PixelGraph(new FromFileImage("skiing.jpg"));
  PixelGraph greenLines = new PixelGraph(new FromFileImage("testImage.jpg"));
  
  void testBigBang(Tester t) {
    ImageWorld g = new ImageWorld("balloons.jpg");
    g.bigBang(1000, 500, 1);
  }
  
  void resetVars() {
    TL = new Pixel(Color.BLACK);
    TM = new Pixel(Color.RED);
    TR = new Pixel(Color.YELLOW);
    ML = new Pixel(Color.GREEN);
    MM = new Pixel(Color.BLUE);
    MR = new Pixel(Color.WHITE);
    BL = new Pixel(Color.CYAN);
    BM = new Pixel(Color.ORANGE);
    BR = new Pixel(Color.MAGENTA);
    Buffer bf = new Buffer();
    ColHead ch = new ColHead();
    RowHead rs = new RowHead();
    row1 = new ArrayList<Pixel>();
    row2 = new ArrayList<Pixel>();
    row3 = new ArrayList<Pixel>();
    grid1 = new ArrayList<ArrayList<Pixel>>();
    row1.add(TL);
    row1.add(TM);
    row1.add(TR);
    row2.add(ML);
    row2.add(MM);
    row2.add(MR);
    row3.add(BL);
    row3.add(BM);
    row3.add(BR);

    grid1.add(row1);
    grid1.add(row2);
    grid1.add(row3);

    PG1 = new PixelGraph(grid1);
    PG0 = new PixelGraph();
  }

  void testPixelGraphConstructor(Tester t) {
    resetVars();
    t.checkExpect(TL.right, TM);
    t.checkExpect(MM.up, TM);
    t.checkExpect(TM.up, PG1.cols.get(1));
    t.checkExpect(BM.down, new Buffer());
    t.checkExpect(MR.right, new Buffer());
    t.checkExpect(PG1.rows.get(2).next, BL);
    t.checkExpect(BL.up, ML);
  }

  void testValidArrangement(Tester t) {
    resetVars();
    t.checkExpect(PG1.validArrangement(), true);
    MM.right = TR.right;
    t.checkExpect(PG1.validArrangement(), false);
    TR.down = TR.right;
    t.checkExpect(PG1.validArrangement(), false);
    BM.right = TR.right;
    t.checkExpect(PG1.validArrangement(), true);
  }
  
  void testSeamInfo(Tester t) {
    resetVars();
    PG1.computeEnergies();

    //vertical removal
    PG1.computeSeams(true);
    t.checkExpect(PG1.minSeam(true).pixel, BL);

    PG1.removeSeam(true);
    t.checkExpect(PG1.validArrangement(), true);
    t.checkExpect(PG1.rows.size(), 3);
    t.checkExpect(PG1.cols.size(), 2);

    //horizontal removal
    PG1.computeSeams(false);
    t.checkExpect(PG1.minSeam(false).pixel, MR);

    PG1.removeSeam(false);
    t.checkExpect(PG1.validArrangement(), true);
    t.checkExpect(PG1.rows.size(), 2);
    t.checkExpect(PG1.cols.size(), 2);
  }

  void testBrightnessUp(Tester t) {
    resetVars();
    //All edge pieces should return 0 for brightness above
    t.checkExpect(ch.brightnessUp(), 0.0);
    t.checkExpect(rh.brightnessUp(), 0.0);
    t.checkExpect(bf.brightnessUp(), 0.0);

    //any pixel with an edge piece above it will return 0
    Pixel colAbove = new Pixel(Color.red);
    colAbove.up = ch;
    Pixel rowAbove = new Pixel(Color.red);
    rowAbove.up = rh;
    Pixel bufAbove = new Pixel(Color.red);
    bufAbove.up = bf;
    t.checkExpect(colAbove.brightnessUp(), 0.0);
    t.checkExpect(rowAbove.brightnessUp(), 0.0);
    t.checkExpect(bufAbove.brightnessUp(), 0.0);

    //Pixel with a red (255, 0, 0) Pixel above it
    Pixel redPix = new Pixel(Color.RED); //(255, 0, 0)
    Pixel belowRed = new Pixel(Color.GREEN);
    belowRed.up = redPix;
    t.checkInexact(belowRed.brightnessUp(), 0.333333333, 0.0001);
  }

  void testBrightnessRight(Tester t) {
    resetVars();
    //All edge pieces should return 0 for brightness above
    t.checkExpect(ch.brightnessRight(), 0.0);
    t.checkExpect(rh.brightnessRight(), 0.0);
    t.checkExpect(bf.brightnessRight(), 0.0);

    //any pixel with an edge piece above it will return 0
    Pixel colRight = new Pixel(Color.red);
    colRight.right = ch;
    Pixel rowRight = new Pixel(Color.red);
    rowRight.right = rh;
    Pixel bufRight = new Pixel(Color.red);
    bufRight.right = bf;
    t.checkExpect(colRight.brightnessRight(), 0.0);
    t.checkExpect(rowRight.brightnessRight(), 0.0);
    t.checkExpect(bufRight.brightnessRight(), 0.0);

    //Pixel with a red (255, 0, 0) Pixel to the right of it
    Pixel redPix = new Pixel(Color.RED); //(255, 0, 0)
    Pixel leftRed = new Pixel(Color.GREEN);
    leftRed.right = redPix;
    t.checkInexact(leftRed.brightnessRight(), 0.333333333, 0.0001);
  }

  void testBrightnessDown(Tester t) {
    resetVars();
    t.checkExpect(TL.brightnessDown(), ML.brightness());
    t.checkExpect(TR.brightnessDown(), MR.brightness());
    t.checkExpect(MM.brightnessDown(), BM.brightness());
    t.checkExpect(BR.brightnessDown(), new Buffer().brightness());
    t.checkExpect(new Buffer().brightnessDown(), 0.0);
    t.checkExpect(TM.up.brightnessDown(), 0.0);
    t.checkExpect(ML.left.brightnessDown(), 0.0);
    t.checkExpect(new RowHead().brightnessDown(), 0.0);
    t.checkExpect(new ColHead().brightnessDown(), 0.0);
  }

  void testBrightnessLeft(Tester t) {
    resetVars();
    t.checkExpect(TM.brightnessLeft(), TL.brightness());
    t.checkExpect(TR.brightnessLeft(), TM.brightness());
    t.checkExpect(MM.brightnessLeft(), ML.brightness());
    t.checkExpect(BL.brightnessLeft(), BL.left.brightness());
    t.checkExpect(new Buffer().brightnessLeft(), 0.0);
    t.checkExpect(TM.up.brightnessLeft(), 0.0);
    t.checkExpect(ML.brightnessLeft(), 0.0);
    t.checkExpect(new RowHead().brightnessLeft(), 0.0);
    t.checkExpect(new ColHead().brightnessLeft(), 0.0);
  }

  void testBrightness(Tester t) {
    resetVars();
    t.checkInexact(TM.brightness(), .33333, .01);
    t.checkInexact(TR.brightness(), .66666, .01);
    t.checkInexact(BM.brightness(), .59477, .01);
    t.checkInexact(MR.brightness(), 1.0, .01);
    t.checkInexact(new Buffer().brightness(), 0.0, .01);
    t.checkInexact(new RowHead().brightness(), 0.0, .01);
    t.checkInexact(new ColHead().brightness(), 0.0, .01);
  }

  void testCalcEnergy(Tester t) {
    resetVars();
    double upDown = (TL.brightness() + (2 * TM.brightness()) + TR.brightness())
        - (BL.brightness() + (2 * BM.brightness()) + BR.brightness());
    double leftRight = (TL.brightness() + (2 * ML.brightness()) + BL.brightness())
        - (TR.brightness() + (2 * MR.brightness()) + BR.brightness());
    MM.calcEnergy();
    ML.calcEnergy();
    BR.calcEnergy();
    //energys of center, edge, and corner pixels
    t.checkInexact(MM.energy, Math.sqrt((leftRight * leftRight) + (upDown * upDown)), .01);
    t.checkInexact(ML.energy, 2.255, .01);
    t.checkInexact(BR.energy, 2.786, .01);
    //changing pixel and updating energy updates energy,
    //diagonals go through left and right pixels (only matters for non-well formed graphs)
    MM.left = ML.left;
    MM.calcEnergy();
    t.checkInexact(MM.energy, 3.539, .01);
  }

  //given a piece 'right' that is right to this piece, fix both connections
  void testConnectPieceRight(Tester t) {
    resetVars();
    //normal connection
    TL.connectPieceRight(BR);
    t.checkExpect(TL.right, BR);
    t.checkExpect(BR.left, TL);
    //row heads connection right
    resetVars();
    ML.left.connectPieceRight(TR);
    t.checkExpect(ML.left.sameRightPiece(TR), true);
    t.checkExpect(TR.left, ML.left);
    //col heads and buffers error
    resetVars();
    t.checkException(new IllegalArgumentException("column head cannot point right."),
        TM.up, "connectPieceRight", BR);
    t.checkException(new IllegalArgumentException("buffer cannot point to anything."),
        BL.down, "connectPieceRight", ML);
  }

  //given a piece 'down' that is below this piece, fix both connections
  void testConnectPieceDown(Tester t) {
    resetVars();
    //normal connection
    TL.connectPieceDown(BR);
    t.checkExpect(TL.down, BR);
    t.checkExpect(BR.up, TL);
    //colHead connection right
    resetVars();
    TM.up.connectPieceDown(MR);
    t.checkExpect(TM.up.sameBelowPiece(MR), true);
    t.checkExpect(MR.up, TM.up);
    //rowHeads and buffers error
    resetVars();
    t.checkException(new IllegalArgumentException("cannot add piece below row head."),
        ML.left, "connectPieceDown", BR);
    t.checkException(new IllegalArgumentException("buffer cannot point to anything."),
        BL.down, "connectPieceDown", ML);
  }

  //helper for connectPieceRight, points the left field of this piece to the passed 'left'
  void testPointLeftToThat(Tester t) {
    resetVars();
    //changes left to paramenter but not other way around
    MM.pointLeftToThat(TR);
    t.checkExpect(MM.left, TR);
    t.checkExpect(TR.right, new Buffer());

    //buffers do nothing and don't error
    BR.right.pointLeftToThat(TL);

    //fails for row/col Heads and buffers
    t.checkException(new IllegalArgumentException("row head cannot point left."),
        ML.left, "pointLeftToThat", BR);
    t.checkException(new IllegalArgumentException("column head cannot point left."),
        TR.up, "pointLeftToThat", BL);
  }

  //helper for connectPieceDown, points the up field of this piece to the passed 'up'
  void testPointUpToThat(Tester t) {
    resetVars();
    //changes left to parameter but not other way around
    MM.pointUpToThat(TR);
    t.checkExpect(MM.up, TR);
    t.checkExpect(TR.right, new Buffer());

    //buffers do nothing and don't error
    BR.right.pointUpToThat(TL);

    //fails for row/col heads and buffers
    t.checkException(new IllegalArgumentException("row head cannot point up."),
        ML.left, "pointUpToThat", BR);
    t.checkException(new IllegalArgumentException("column head cannot point up."),
        TR.up, "pointUpToThat", BL);
  }

  //is this piece consistent with all of its pointers
  void testValidSetup(Tester t) {
    resetVars();
    t.checkExpect(TL.validSetup(), true);
    t.checkExpect(MR.validSetup(), true);
    t.checkExpect(TM.validSetup(), true);
    t.checkExpect(BL.validSetup(), true);
    t.checkExpect(MM.validSetup(), true);

    //row/col heads, buffer
    t.checkExpect(new Buffer().validSetup(), true);
    t.checkExpect(ML.left.validSetup(), true);
    t.checkExpect(TR.up.validSetup(), true);

    //right left failures
    ML.right = MM.right;
    t.checkExpect(ML.validSetup(), false);
    t.checkExpect(MM.validSetup(), false);

    //up down falures
    resetVars();
    BM.up = MM.up;
    t.checkExpect(BM.validSetup(), false);
    t.checkExpect(MM.validSetup(), false);

    //top right error
    resetVars();
    MR.up = TL;
    t.checkExpect(MM.validSetup(), false);
    //bottom left error
    resetVars();
    BM.left = TR;
    t.checkExpect(MM.validSetup(), false);
    //top left error
    resetVars();
    BM.up = TR;
    t.checkExpect(MM.validSetup(), false);
    //bottom right
    resetVars();
    TR.down = MM;
    t.checkExpect(TM.validSetup(), false);
  }

  //is the piece above this piece the same as the given piece
  void testSameAbovePiece(Tester t) {
    resetVars();
    //pixel tests
    t.checkExpect(MM.sameAbovePiece(TM), true);
    t.checkExpect(BR.sameAbovePiece(MR), true);
    t.checkExpect(MM.sameAbovePiece(TL), false);
    t.checkExpect(TM.sameAbovePiece(TM.up), true);

    //boarder pieces always return true for anything
    t.checkExpect(TM.up.sameAbovePiece(null), true);
    t.checkExpect(ML.left.sameAbovePiece(null), true);
    t.checkExpect(BR.right.sameAbovePiece(null), true);
  }

  //is the piece below this piece the same as the given piece
  void testSameBelowPiece(Tester t) {
    resetVars();
    //pixel tests
    t.checkExpect(MM.sameBelowPiece(BM), true);
    t.checkExpect(BR.sameBelowPiece(MR), false);
    t.checkExpect(TR.sameBelowPiece(MR), true);
    t.checkExpect(BL.sameBelowPiece(BL.down), true);

    //non col head boarder pieces always return true for anything
    t.checkExpect(ML.left.sameBelowPiece(null), true);
    t.checkExpect(BR.right.sameBelowPiece(null), true);

    //col head
    t.checkExpect(TM.up.sameBelowPiece(TM), true);
    t.checkExpect(TM.up.sameBelowPiece(BR), false);
  }

  //is the piece right of this piece the same as the given piece
  void testSameRightPiece(Tester t) {
    resetVars();
    //pixel tests
    t.checkExpect(MM.sameRightPiece(MR), true);
    t.checkExpect(BL.sameRightPiece(BR), false);
    t.checkExpect(TL.sameRightPiece(TM), true);
    t.checkExpect(BR.sameRightPiece(BR.right), true);

    //non col head boarder pieces always return true for anything
    t.checkExpect(BR.right.sameRightPiece(null), true);
    t.checkExpect(TM.up.sameRightPiece(null), true);

    //col head
    t.checkExpect(ML.left.sameRightPiece(ML), true);
    t.checkExpect(ML.left.sameRightPiece(TR), false);
  }

  //is the piece left of this piece the same as the given piece
  void testSameLeftPiece(Tester t) {
    resetVars();
    //pixel tests
    t.checkExpect(MM.sameLeftPiece(ML), true);
    t.checkExpect(BL.sameLeftPiece(BM), false);
    t.checkExpect(TL.sameLeftPiece(TL.left), true);
    t.checkExpect(BR.sameLeftPiece(BM), true);

    //boarder pieces always return true for anything
    t.checkExpect(TM.up.sameLeftPiece(null), true);
    t.checkExpect(ML.left.sameLeftPiece(null), true);
    t.checkExpect(BR.right.sameLeftPiece(null), true);
  }

  //is the piece to the right of this piece the same as the piece above the given piece
  void testIsRightOfThisSameAsTopOfThat(Tester t)  {
    resetVars();

    //any edge piece returns true
    t.checkExpect(bf.isRightOfThisSameAsTopOfThat(ch), true);
    t.checkExpect(bf.isRightOfThisSameAsTopOfThat(rh), true);
    t.checkExpect(bf.isRightOfThisSameAsTopOfThat(bf), true);
    t.checkExpect(ch.isRightOfThisSameAsTopOfThat(ch), true);
    t.checkExpect(ch.isRightOfThisSameAsTopOfThat(rh), true);
    t.checkExpect(ch.isRightOfThisSameAsTopOfThat(bf), true);
    t.checkExpect(rh.isRightOfThisSameAsTopOfThat(ch), true);
    t.checkExpect(rh.isRightOfThisSameAsTopOfThat(rh), true);
    t.checkExpect(rh.isRightOfThisSameAsTopOfThat(bf), true);

    //pixels with another pixel southeast to it will return true
    t.checkExpect(TM.isRightOfThisSameAsTopOfThat(MR), true);
    //any pixel with itself will return false
    t.checkExpect(TM.isRightOfThisSameAsTopOfThat(TM), false);
    //pixels will return true to any edge piece passed in
    t.checkExpect(TM.isRightOfThisSameAsTopOfThat(rh), true);
    //pixels with a pixel that is not southeast to it will return false
    t.checkExpect(TM.isRightOfThisSameAsTopOfThat(TL), false);
  }

  //is the piece to the left of this piece the same as the piece above the given piece
  void testIsLeftOfThisSameAsTopOfThat(Tester t) {
    resetVars();

    //any edge piece returns true
    t.checkExpect(bf.isLeftOfThisSameAsTopOfThat(ch), true);
    t.checkExpect(bf.isLeftOfThisSameAsTopOfThat(rh), true);
    t.checkExpect(bf.isLeftOfThisSameAsTopOfThat(bf), true);
    t.checkExpect(ch.isLeftOfThisSameAsTopOfThat(ch), true);
    t.checkExpect(ch.isLeftOfThisSameAsTopOfThat(rh), true);
    t.checkExpect(ch.isLeftOfThisSameAsTopOfThat(bf), true);
    t.checkExpect(rh.isLeftOfThisSameAsTopOfThat(ch), true);
    t.checkExpect(rh.isLeftOfThisSameAsTopOfThat(rh), true);
    t.checkExpect(rh.isLeftOfThisSameAsTopOfThat(bf), true);

    //pixels with another pixel southwest to it will return true
    t.checkExpect(TM.isLeftOfThisSameAsTopOfThat(ML), true);
    //any pixel with itself will return false
    t.checkExpect(TM.isLeftOfThisSameAsTopOfThat(TM), false);
    //pixels will return true to any edge piece passed in
    t.checkExpect(TM.isLeftOfThisSameAsTopOfThat(rh), true);
    //pixels with a pixel that is not southwest to it will return false
    t.checkExpect(TM.isLeftOfThisSameAsTopOfThat(TL), false);
  }

  //is the piece to the right of this piece the same as the piece below the given piece
  void testIsRightOfThisSameAsBottomOfThat(Tester t) {
    resetVars();

    //any edge piece returns true
    t.checkExpect(bf.isRightOfThisSameAsBottomOfThat(ch), true);
    t.checkExpect(bf.isRightOfThisSameAsBottomOfThat(rh), true);
    t.checkExpect(bf.isRightOfThisSameAsBottomOfThat(bf), true);
    t.checkExpect(ch.isRightOfThisSameAsBottomOfThat(ch), true);
    t.checkExpect(ch.isRightOfThisSameAsBottomOfThat(rh), true);
    t.checkExpect(ch.isRightOfThisSameAsBottomOfThat(bf), true);
    t.checkExpect(rh.isRightOfThisSameAsBottomOfThat(ch), true);
    t.checkExpect(rh.isRightOfThisSameAsBottomOfThat(rh), true);
    t.checkExpect(rh.isRightOfThisSameAsBottomOfThat(bf), true);

    //pixels with another pixel southwest to it will return true
    t.checkExpect(BM.isRightOfThisSameAsBottomOfThat(MR), true);
    //any pixel with itself will return false
    t.checkExpect(BM.isRightOfThisSameAsBottomOfThat(BM), false);
    //pixels will return true to any edge piece passed in
    t.checkExpect(BM.isRightOfThisSameAsBottomOfThat(rh), true);
    //pixels with a pixel that is not southwest to it will return false
    t.checkExpect(BM.isRightOfThisSameAsBottomOfThat(TL), false);
  }

  //is the piece to the left of this piece the same as the piece below the given piece
  void testIsLeftOfThisSameAsBottomOfThat(Tester t) {
    resetVars();

    //any edge piece returns true
    t.checkExpect(bf.isLeftOfThisSameAsBottomOfThat(ch), true);
    t.checkExpect(bf.isLeftOfThisSameAsBottomOfThat(rh), true);
    t.checkExpect(bf.isLeftOfThisSameAsBottomOfThat(bf), true);
    t.checkExpect(ch.isLeftOfThisSameAsBottomOfThat(ch), true);
    t.checkExpect(ch.isLeftOfThisSameAsBottomOfThat(rh), true);
    t.checkExpect(ch.isLeftOfThisSameAsBottomOfThat(bf), true);
    t.checkExpect(rh.isLeftOfThisSameAsBottomOfThat(ch), true);
    t.checkExpect(rh.isLeftOfThisSameAsBottomOfThat(rh), true);
    t.checkExpect(rh.isLeftOfThisSameAsBottomOfThat(bf), true);

    //pixels with another pixel southwest to it will return true
    t.checkExpect(BM.isLeftOfThisSameAsBottomOfThat(ML), true);
    //any pixel with itself will return false
    t.checkExpect(BM.isLeftOfThisSameAsBottomOfThat(BM), false);
    //pixels will return true to any edge piece passed in
    t.checkExpect(BM.isLeftOfThisSameAsBottomOfThat(rh), true);
    //pixels with a pixel that is not southwest to it will return false
    t.checkExpect(BM.isLeftOfThisSameAsBottomOfThat(TL), false);
  }

  //returns a seamInfo object with the least total weight of this pixel
  //and its left and right neighbors
  void testMinSeamInfoOfThree(Tester t) {
    resetVars();

    //For Vertical removal
    //All edge pieces will return null
    t.checkExpect(bf.minSeamInfoOfThree(true), null);
    t.checkExpect(ch.minSeamInfoOfThree(true), null);
    t.checkExpect(rh.minSeamInfoOfThree(true), null);

    //For Horizontal removal
    //All edge pieces will return null
    t.checkExpect(bf.minSeamInfoOfThree(false), null);
    t.checkExpect(ch.minSeamInfoOfThree(false), null);
    t.checkExpect(rh.minSeamInfoOfThree(false), null);

    //A pixel with edge pieces on both the left and right of it will return a seamInfo
    Pixel betweenEdges = new Pixel(Color.RED);
    betweenEdges.right = bf;
    betweenEdges.left = rh;
    betweenEdges.seam = new SeamInfo(betweenEdges, 2, null);

    t.checkExpect(betweenEdges.minSeamInfoOfThree(true), betweenEdges.seam);

    //For Horizontal removal
    //A pixel with edge pieces on both the left and right of it will return a seamInfo
    Pixel betweenEdgesVertically = new Pixel(Color.RED);
    betweenEdgesVertically.up = bf;
    betweenEdgesVertically.down = rh;
    betweenEdgesVertically.seam = new SeamInfo(betweenEdges, 2, null);

    t.checkExpect(betweenEdgesVertically.minSeamInfoOfThree(false), betweenEdgesVertically.seam);

    //For vertical removal
    //A pixel with an edge on one side and a Pixel on the other side will compare
    //between itself and just the other pixel
    Pixel edgePixelPixel = new Pixel(Color.CYAN);
    edgePixelPixel.left = bf;
    Pixel redPixel = new Pixel(Color.RED);
    redPixel.seam = new SeamInfo(redPixel, 5, null);
    edgePixelPixel.right = redPixel;
    edgePixelPixel.seam = new SeamInfo(edgePixelPixel, 3, null);
    t.checkExpect(edgePixelPixel.minSeamInfoOfThree(true), edgePixelPixel.seam);

    //For horizontal removal
    //A pixel with an edge on one side and a Pixel on the other side will compare
    //between itself and just the other pixel
    Pixel edgePixelPixelHorz = new Pixel(Color.CYAN);
    edgePixelPixelHorz.up = bf;
    Pixel redPixelHorz = new Pixel(Color.RED);
    redPixelHorz.seam = new SeamInfo(redPixel, 5, null);
    edgePixelPixelHorz.down = redPixelHorz;
    edgePixelPixelHorz.seam = new SeamInfo(edgePixelPixelHorz, 3, null);
    t.checkExpect(edgePixelPixelHorz.minSeamInfoOfThree(false), edgePixelPixelHorz.seam);

    //For vertical removal
    //A pixel between two other pixels will compare between all three
    Pixel threePixels = new Pixel(Color.BLACK);
    Pixel orange = new Pixel(Color.ORANGE);
    threePixels.left = orange;
    Pixel red = new Pixel(Color.RED);
    threePixels.right = red;
    threePixels.energy = 4;
    red.energy = 3;
    orange.energy = 5;
    threePixels.seam = new SeamInfo(threePixels, threePixels.energy, null);
    threePixels.seam = new SeamInfo(red, red.energy, null);
    threePixels.seam = new SeamInfo(orange, orange.energy, null);
    t.checkExpect(threePixels.minSeamInfoOfThree(true), red.seam);

    //For horizontal removal
    //A pixel between two other pixels will compare between all three
    Pixel threePixelsHorz = new Pixel(Color.BLACK);
    Pixel orangeHorz = new Pixel(Color.ORANGE);
    threePixelsHorz.up = orangeHorz;
    Pixel redHorz = new Pixel(Color.RED);
    threePixelsHorz.down = redHorz;
    threePixelsHorz.energy = 4;
    redHorz.energy = 3;
    orangeHorz.energy = 5;
    threePixelsHorz.seam = new SeamInfo(threePixelsHorz, threePixelsHorz.energy, null);
    threePixelsHorz.seam = new SeamInfo(redHorz, redHorz.energy, null);
    threePixelsHorz.seam = new SeamInfo(orangeHorz, orangeHorz.energy, null);
    t.checkExpect(threePixelsHorz.minSeamInfoOfThree(false), redHorz.seam);

  }

  //returns the minimum of the SeamInfo of this SeamInfo and that SeamInfo
  //compared by total weight
  void testMinSeam(Tester t) {
    resetVars();

    SeamInfo lowest = new SeamInfo(TL, 3, null);
    SeamInfo middle = new SeamInfo(TM, 4, null);
    t.checkExpect(lowest.minSeam(middle), lowest);
    t.checkExpect(middle.minSeam(lowest), lowest);

    //in the case of a tie, the seam that is passed into the method call 'wins' and is returned
    SeamInfo equalSeams = new SeamInfo(ML, 5, null);
    SeamInfo equalSeams2 = new SeamInfo(MM, 5, null);
    t.checkExpect(equalSeams.minSeam(equalSeams2), equalSeams2);
  }

  //minimum seamInfo in this row
  void testMinSeamInRowAcc(Tester t) {
    resetVars();

    TL.seam = new SeamInfo(TL, 6, null);
    TM.seam = new SeamInfo(TM, 4, null);
    TR.seam = new SeamInfo(TR, 5.3, null);

    t.checkExpect(TL.minSeamInRowAcc(TL.seam), TM.seam);



    //all edge pieces will just return the accumulated minimum seam
    t.checkExpect(bf.minSeamInRowAcc(TM.seam), TM.seam);
    t.checkExpect(ch.minSeamInRowAcc(TM.seam), TM.seam);
    t.checkExpect(rh.minSeamInRowAcc(TM.seam), TM.seam);
  }

  //minimum seamInfo in this row
  void testMinSeamInRow(Tester t) {
    resetVars();

    //both Buffers and ColHeads throw exceptions since they
    //dont have rows
    t.checkException(new IllegalStateException("row had no seams in it"),
            bf, "minSeamInRow");
    t.checkException(new IllegalStateException("row had no seams in it"),
            ch, "minSeamInRow");

    //Row Heads
    TL.seam = new SeamInfo(TL, 3, null);
    TM.seam = new SeamInfo(TM, 4, null);
    TR.seam = new SeamInfo(TR, 5, null);
    rh.next = TL;
    //if all SeamInfo's in row are different, minimum is returned
    t.checkExpect(rh.minSeamInRow(), TL.seam);
    //On a Pixel, if itself is the minimum in the row it will be returned
    t.checkExpect(TL.minSeamInRow(), TL.seam);
    //Does not factor in seams of Pixels to the left of it
    t.checkExpect(TM.minSeamInRow(), TM.seam);

    TL.seam = new SeamInfo(TL, 4, null);
    TM.seam = new SeamInfo(TM, 3, null);
    TR.seam = new SeamInfo(TR, 3, null);
    //If there are ties for weights, the first one (leftmost/topmost (vertical
    // removal/horizontal removal) in the row)
    //is returned
    t.checkExpect(rh.minSeamInRow(), TM.seam);
    t.checkExpect(TM.minSeamInRow(), TM.seam);
  }

  //EFFECT: given an integer n, where negative is to the left and positive is to the right,
  //accumulate all the |n| pixels to the left or right this pixel and add them to soFar
  void testGetPixelsNToSide(Tester t) {

    resetVars();

    ArrayList<Pixel> pixs = new ArrayList<>();
    pixs.add(TL);
    //All edge pieces return the accumulated list
    //regardless of what n is
    t.checkExpect(bf.getPixelsNToSide(0, pixs), pixs);
    t.checkExpect(bf.getPixelsNToSide(3, pixs), pixs);
    t.checkExpect(bf.getPixelsNToSide(-2, pixs), pixs);

    resetVars();
    ArrayList<Pixel> acc = new ArrayList<>();
    ArrayList<Pixel> revAcc = new ArrayList<>();
    acc.add(TL);
    acc.add(TM);
    revAcc.add(MR);
    revAcc.add(MM);
    //n is less than the amount of pixels to the side
    t.checkExpect(TL.getPixelsNToSide(2, new ArrayList<Pixel>()), acc);
    t.checkExpect(MR.getPixelsNToSide(-2, new ArrayList<Pixel>()), revAcc);

    acc.add(TR);
    revAcc.add(ML);
    //n is larger than amount of pixels to the side, just goes until hits an edge piece
    t.checkExpect(TL.getPixelsNToSide(5, new ArrayList<Pixel>()), acc);
    t.checkExpect(MR.getPixelsNToSide(-5, new ArrayList<Pixel>()), revAcc);

    //getting Pixels with a non-empty accumulator list
    ArrayList<Pixel> botInit = new ArrayList<>();
    ArrayList<Pixel> botAcc = new ArrayList<>();
    botInit.add(BL);
    botAcc.add(BL);
    botAcc.add(BM);
    t.checkExpect(BM.getPixelsNToSide(-1, botInit), botAcc);

    //n = 0 just returns accumulator
    t.checkExpect(BM.getPixelsNToSide(0, botAcc), botAcc);
  }

  //EFFECT: given an integer n, where negative is up and positive is down,
  //accumulate all the |n| pixels above or below this pixel and add them to soFar
  void testGetPixelsNUpDown(Tester t) {

    resetVars();

    ArrayList<Pixel> pixs = new ArrayList<>();
    pixs.add(TL);
    //All edge pieces return the accumulated list
    //regardless of what n is
    t.checkExpect(bf.getPixelsNUpDown(0, pixs), pixs);
    t.checkExpect(bf.getPixelsNUpDown(3, pixs), pixs);
    t.checkExpect(bf.getPixelsNUpDown(-2, pixs), pixs);

    resetVars();
    ArrayList<Pixel> acc = new ArrayList<>();
    ArrayList<Pixel> revAcc = new ArrayList<>();
    acc.add(TL);
    acc.add(ML);
    revAcc.add(BL);
    revAcc.add(ML);
    //n is less than the amount of pixels above or below
    t.checkExpect(TL.getPixelsNUpDown(2, new ArrayList<Pixel>()), acc);
    t.checkExpect(BL.getPixelsNUpDown(-2, new ArrayList<Pixel>()), revAcc);

    //getting Pixels with a non-empty accumulator list
    ArrayList<Pixel> botInit = new ArrayList<>();
    ArrayList<Pixel> botAcc = new ArrayList<>();
    botInit.add(BL);
    botAcc.add(BL);
    botAcc.add(BM);
    t.checkExpect(BM.getPixelsNUpDown(-1, botInit), botAcc);

    //n = 0 just returns accumulator
    t.checkExpect(BM.getPixelsNUpDown(0, botAcc), botAcc);
  }

  void testComputeSeam(Tester t) {
    resetVars();

    //For vertical removal
    TL.computeSeam(true);
    t.checkExpect(TL.seam.pixel, TL);
    t.checkInexact(TL.seam.totalWeight, 1.414, .01);
    t.checkExpect(TL.seam.cameFrom, null);
    TM.computeSeam(true);
    t.checkExpect(TM.seam.pixel, TM);
    t.checkInexact(TM.seam.totalWeight, 2.828, .01);
    t.checkExpect(TM.seam.cameFrom, null);
    TR.computeSeam(true);
    ML.computeSeam(true);
    t.checkExpect(ML.seam.pixel, ML);
    t.checkInexact(ML.seam.totalWeight, 3.669, .01);
    t.checkExpect(ML.seam.cameFrom, TL.seam);
    MM.computeSeam(true);
    t.checkExpect(MM.seam.pixel, MM);
    t.checkInexact(MM.seam.totalWeight, 3.741, .01);
    t.checkExpect(MM.seam.cameFrom, TL.seam);

    //For horizontal removal
    TL.computeSeam(false);
    t.checkExpect(TL.seam.pixel, TL);
    t.checkInexact(TL.seam.totalWeight, 1.414, .01);
    t.checkExpect(TL.seam.cameFrom, null);
    ML.computeSeam(false);
    t.checkExpect(ML.seam.pixel, ML);
    t.checkInexact(ML.seam.totalWeight, 2.255, .01);
    t.checkExpect(ML.seam.cameFrom, null);
    TM.computeSeam(false);
    BL.computeSeam(false);
    t.checkExpect(BL.seam.pixel, BL);
    t.checkInexact(BL.seam.totalWeight, 1.8218, .01);
    t.checkExpect(TM.seam.cameFrom, TL.seam);
    MM.computeSeam(false);
    t.checkExpect(MM.seam.pixel, MM);
    t.checkInexact(MM.seam.totalWeight, 3.741, .01);
    t.checkExpect(MM.seam.cameFrom, TL.seam);
  }

  void testRemove(Tester t) {
    resetVars();
    //removing any seam from bottom to top should maintain well-formedness
    //check stright up and down & left/right diagonal removals
    BR.remove();
    MM.remove();
    TM.remove();
    t.checkExpect(PG1.validArrangement(), true);

    resetVars();
    BL.remove();
    ML.remove();
    TM.remove();
    BM.remove();
    MR.remove();
    TL.remove();
    t.checkExpect(PG1.validArrangement(), true);

    resetVars();
    //removing any seam from right to left should maintain well-formedness
    //check stright up and down & left/right diagonal removals
    BR.remove();
    MM.remove();
    TL.remove();
    t.checkExpect(PG1.validArrangement(), true);
  }

  //does this IGridPiece have a Piece to the right of it
  void testHasNext(Tester t) {
    //For vertical removal
    resetVars();
    //Buffers and ColHeads do not have nexts
    t.checkExpect(bf.hasNext(), false);
    t.checkExpect(ch.hasNext(), false);
    //RowHeads always have a next
    t.checkExpect(rh.hasNext(), true);

    //Pixels always return true, regardless of whether
    //the next Piece is a Pixel or an edge piece
    t.checkExpect(TL.hasNext(), true);
    //TR has a buffer as its next piece, still returns trues
    t.checkExpect(TR.hasNext(), true);

    //For horizontal removal
    resetVars();
    //Buffers and RowHeads do not have nexts
    t.checkExpect(bf.hasNextCol(), false);
    t.checkExpect(rh.hasNextCol(), false);
    //ColHeads always have a next
    t.checkExpect(ch.hasNextCol(), true);

    //Pixels always return true, regardless of whether
    //the next Piece is a Pixel or an edge piece
    t.checkExpect(TL.hasNextCol(), true);
    //BR has an edge piece as its next piece, still returns trues
    t.checkExpect(BR.hasNextCol(), true);
  }

  //the piece to the right of this IGridPiece
  void testNext(Tester t) {
    //For vertical removal
    resetVars();
    //Buffers and ColHeads do not have nexts (since next means next in the row)
    t.checkException(new IllegalArgumentException("this piece does not have a next to the right"),
            bf, "next");
    t.checkException(new IllegalArgumentException("this piece does not have a next to the right"),
            ch, "next");

    //RowHeads return the piece referenced in their next field
    rh.next = new Pixel(Color.RED);
    t.checkExpect(rh.next(), new Pixel(Color.RED));

    //A Pixel with a Pixel to its right will return that
    t.checkExpect(TL.next(), TM);
    //A Pixel with a Buffer to its right will return that
    t.checkExpect(TR.next(), bf);
    //A Pixel with a ColHead to its right will return that
    TR.right = ch;
    t.checkExpect(TR.next(), ch);
    //A Pixel with a RowHead to its right will return that
    TR.right = rh;
    t.checkExpect(TR.next(), rh);

    //For horizontal removal
    resetVars();
    //Buffers and RowHeads do not have nexts (since next means next in the column)
    t.checkException(new IllegalArgumentException("this piece does not have a next below it"),
            bf, "nextCol");
    t.checkException(new IllegalArgumentException("this piece does not have a next below it"),
            rh, "nextCol");

    //ColHeads return the piece referenced in their next field
    ch.next = new Pixel(Color.RED);
    t.checkExpect(ch.nextCol(), new Pixel(Color.RED));

    //A Pixel with a Pixel below it will return that
    t.checkExpect(TL.nextCol(), ML);
    //A Pixel with a buffer below it will return that
    t.checkExpect(BR.nextCol(), bf);
    //A Pixel with a ColHead below it will return that
    BR.down = ch;
    t.checkExpect(BR.nextCol(), ch);
    //A Pixel with a RowHead below it will return that
    TR.down = rh;
    t.checkExpect(TR.nextCol(), rh);
  }

  void testRemoveSeam(Tester t) {
    //For vertical removal
    resetVars();
    PG1.computeSeamAndPaintRed(true);
    t.checkExpect(BL.drawRed, true);
    t.checkExpect(ML.drawRed, true);
    t.checkExpect(TL.drawRed, true);
    t.checkExpect(TR.drawRed, false);

    PG1.removeSeam(true);
    t.checkExpect(PG1.rows.get(0).next, TM);
    t.checkExpect(PG1.rows.get(1).next, MM);
    t.checkExpect(PG1.rows.get(2).next, BM);

    PG1.computeSeamAndPaintRed(true);
    t.checkExpect(BR.drawRed, true);
    t.checkExpect(MR.drawRed, true);
    t.checkExpect(TR.drawRed, true);

    PG1.removeSeam(true);
    t.checkExpect(BM.right, new Buffer());

    //For horizontal removal
    resetVars();
    PG1.computeSeamAndPaintRed(false);
    t.checkExpect(TL.drawRed, true);
    t.checkExpect(MM.drawRed, true);
    t.checkExpect(MR.drawRed, true);
    t.checkExpect(ML.drawRed, false);
    t.checkExpect(BR.drawRed, false);

    PG1.removeSeam(false);
    t.checkExpect(PG1.cols.get(0).next, ML);
    t.checkExpect(PG1.cols.get(1).next, TM);
    t.checkExpect(PG1.cols.get(2).next, TR);

    PG1.computeSeamAndPaintRed(false);
    t.checkExpect(BR.drawRed, true);
    t.checkExpect(BL.drawRed, true);
    t.checkExpect(BM.drawRed, true);
    t.checkExpect(TM.drawRed, false);

    PG1.removeSeam(false);
    t.checkExpect(TM.down, new Buffer());
  }

  void testComputeNextTotalWeight(Tester t) {
    resetVars();
    //The energy passed in is added to the total weight of this SeamInfo
    SeamInfo simpleSeam = new SeamInfo(TL, 3, null);
    t.checkExpect(simpleSeam.computeNextTotalWeight(6.4), 9.4);
    SeamInfo zeroSeam = new SeamInfo(TM, 0, null);
    //When a SeamInfo has a total weight of 0, the energy is returned
    t.checkExpect(zeroSeam.computeNextTotalWeight(3.7), 3.7);
  }

  void testTurnRed(Tester t) {
    resetVars();
    t.checkExpect(TL.drawRed, false);
    TL.turnRed();
    t.checkExpect(TL.drawRed, true);
  }

  void testTurnSeamRed(Tester t) {
    //For vertical removal
    resetVars();
    SeamInfo topSeam = new SeamInfo(TL, 3, null);
    SeamInfo midSeam = new SeamInfo(ML, 5, topSeam);
    SeamInfo botSeam = new SeamInfo(BL, 9, midSeam);

    botSeam.turnSeamRed();
    t.checkExpect(TL.drawRed, true);
    t.checkExpect(ML.drawRed, true);
    t.checkExpect(BL.drawRed, true);

    //For horizontal removal
    resetVars();
    SeamInfo leftSeam = new SeamInfo(TL, 3, null);
    SeamInfo midSeam2 = new SeamInfo(MM, 5, leftSeam);
    SeamInfo rightSeam = new SeamInfo(MR, 9, midSeam2);

    rightSeam.turnSeamRed();
    t.checkExpect(TL.drawRed, true);
    t.checkExpect(MM.drawRed, true);
    t.checkExpect(MR.drawRed, true);
  }

  void testParseImageIntoPixels(Tester t) {
    t.checkExpect(((Pixel)((Pixel)balloons.rows.get(25).next).right).color,
            new FromFileImage("balloons.jpg").getColorAt(1, 25));
    t.checkExpect(((Pixel)((Pixel)balloons.cols.get(134).next).down).color,
            new FromFileImage("balloons.jpg").getColorAt(134, 1));
  }

  void testOnKeyEvent(Tester t) {
    resetVars();

    ImageWorld g = new ImageWorld("balloons.jpg");
    //pause/unpause
    t.checkExpect(g.autoRemove, false);
    g.onKeyEvent(" ");
    t.checkExpect(g.autoRemove, true);

    //removing a vertical seam
    PixelGraph pg = g.pixelImg;
    t.checkExpect(pg.cols.size(), 800);
    g.onKeyEvent("v");
    t.checkExpect(pg.cols.size(), 799);

    //adding back a vertical seam
    t.checkExpect(pg.cols.size(), 799);
    g.onKeyEvent("r");
    t.checkExpect(pg.cols.size(), 800);

    //removing a horizontal seam
    t.checkExpect(pg.rows.size(), 343);
    g.onKeyEvent("h");
    t.checkExpect(pg.rows.size(), 342);

    //adding back a horizontal seam
    t.checkExpect(pg.rows.size(), 342);
    g.onKeyEvent("r");
    t.checkExpect(pg.rows.size(), 343);

    //toggling autoAddBack
    t.checkExpect(g.autoAddBack, false);
    g.onKeyEvent("t");
    t.checkExpect(g.autoAddBack, true);

    //cycling between drawing styles
    t.checkExpect(g.showWeight, false);
    t.checkExpect(g.showEnergy, false);
    t.checkExpect(g.showImage, true);
    g.onKeyEvent("c");
    t.checkExpect(g.showWeight, false);
    t.checkExpect(g.showEnergy, true);
    t.checkExpect(g.showImage, false);
    g.onKeyEvent("c");
    t.checkExpect(g.showWeight, true);
    t.checkExpect(g.showEnergy, false);
    t.checkExpect(g.showImage, false);
    g.onKeyEvent("c");
    t.checkExpect(g.showWeight, false);
    t.checkExpect(g.showEnergy, false);
    t.checkExpect(g.showImage, true);
  }

  //adds a column head to this removed seam if one is not already set
  void testSetTopOfSeam(Tester t) {
    resetVars();
    ArrayList<Pixel> pix = new ArrayList<>();
    SeamInfo bot = new SeamInfo(TL, 4, null);
    RemovedSeam remSeam = new RemovedSeam(bot, true);
    //Setting the top of Seam for a RemovedSeam modifies the topOfSeam
    //field and points it to the passed ColHead
    t.checkExpect(remSeam.topOfSeam, null);
    remSeam.setTopOfSeam(ch);
    t.checkExpect(remSeam.topOfSeam, ch);
    //Attempting to set topOfSeam on a RemovedSeam with a non-null
    //value for topOfSeam results in an exception
    t.checkException(new IllegalStateException("cannnot change the column " +
            "head of a seam after it has been set"), remSeam, "setTopOfSeam",
            new ColHead());
  }

  //adds an AL of pixels that have been effected by the removal of this seam
  //to this removedSeam's effectedPixels
  void testAddToEffectedPixels(Tester t) {
    resetVars();

    SeamInfo top = new SeamInfo(TL, 4, null);
    //vertical/non-vertical does the exact same thing
    RemovedSeam remSeam = new RemovedSeam(top, true);
    t.checkExpect(remSeam.effectedPixels, new ArrayList<Pixel>());
    //EffectedPixels changes from empty to having contents of Pixs
    ArrayList<Pixel> pixs = new ArrayList<Pixel>();
    pixs.add(TM);
    pixs.add(TR);
    remSeam.addToEffectedPixels(pixs);
    t.checkExpect(remSeam.effectedPixels, pixs);

    //Adding duplicate pixels is fine
    ArrayList<Pixel> appendedPixs = new ArrayList<Pixel>();
    appendedPixs.addAll(pixs);
    appendedPixs.addAll(pixs);
    remSeam.addToEffectedPixels(pixs);
    t.checkExpect(remSeam.effectedPixels, appendedPixs);

    //Adding an empty arraylist changes nothing
    remSeam.addToEffectedPixels(new ArrayList<Pixel>());
    t.checkExpect(remSeam.effectedPixels, appendedPixs);
  }

  //fix the energies of all the pixels effected by the removal of this seam
  void testFixAllAffectedPixels(Tester t) {
    resetVars();
    ArrayList<Pixel> pixs = new ArrayList<Pixel>();
    pixs.add(TM);
    pixs.add(MM);
    pixs.add(BM);
    SeamInfo startSeam = new SeamInfo(TM, 3, null);
    SeamInfo midSeam = new SeamInfo(MM, 7, startSeam);
    SeamInfo endSeam = new SeamInfo(BM, 9, midSeam);
    //vertical/non-vertical does the exact same thing
    RemovedSeam remSeam = new RemovedSeam(endSeam, true);
    //arbitrary values for energy to demonstrate that fixAllAffectedPixels
    //recalculates energies
    TM.energy = 3.4;
    MM.energy = 3.6;
    BM.energy = 5.5;
    remSeam.effectedPixels = pixs;
    remSeam.fixAllAffectedPixels();
    t.checkInexact(TM.energy, 2.828, 0.01);
    t.checkInexact(MM.energy, 2.327, 0.01);
    t.checkInexact(BM.energy, 2.108, 0.01);
  }

  void testWeightBiggerThan(Tester t) {
    resetVars();
    SeamInfo smallerSeam = new SeamInfo(new Pixel(Color.RED), 4.0, null);
    t.checkInexact(smallerSeam.weightBiggerThan(8), 8.0, 0.001);
    t.checkInexact(smallerSeam.weightBiggerThan(3), 4.0, 0.001);

    SeamInfo smallerSeam2 = new SeamInfo(new Pixel(Color.RED), 0.0, null);
    t.checkInexact(smallerSeam2.weightBiggerThan(2), 2.0, 0.001);
    t.checkInexact(smallerSeam2.weightBiggerThan(1), 1.0, 0.001);
  }
  
  void testAddLastRemovedSeam(Tester t) {
    resetVars();
    //removing and adding back should not change the graph
    PG1.computeSeamAndPaintRed(false);
    PG1.removeSeam(false);
    PG1.addLastRemovedSeam();
    t.checkExpect(PG1.validArrangement(), true);
    t.checkExpect(PG1.cols.get(1).next, TM);
    t.checkExpect(PG1.rows.get(2).next, BL);
    t.checkExpect(PG1.rows.size(), 3);
    t.checkInexact(((Pixel)PG1.cols.get(1).next).color, Color.RED, .001);
    PG1.computeSeamAndPaintRed(true);
    PG1.removeSeam(true);
    PG1.computeSeamAndPaintRed(true);
    PG1.removeSeam(true);
    PG1.computeSeamAndPaintRed(false);
    PG1.removeSeam(false);
    PG1.addLastRemovedSeam();
    PG1.addLastRemovedSeam();
    PG1.addLastRemovedSeam();
    t.checkExpect(PG1.validArrangement(), true);
    t.checkExpect(PG1.cols.get(1).next, TM);
    t.checkExpect(PG1.rows.get(2).next, BL);
    t.checkExpect(PG1.rows.size(), 3);
    t.checkExpect(((Pixel)PG1.cols.get(1).next).color, Color.RED);
    t.checkExpect(((Pixel)((Pixel)PG1.cols.get(1).next).down).color, Color.BLUE);
  }

  void testAddSeamBack(Tester t) {
    resetVars();

    //removing a vertical seam
    PG1.computeEnergies();
    PG1.computeSeams(true);
    //removing seams decrements number of columns
    t.checkExpect(PG1.cols.size(), 3);
    PG1.removeSeam(true);
    t.checkExpect(PG1.cols.size(), 2);
    //adding last removed seam increments number of columns
    PG1.addLastRemovedSeam();
    t.checkExpect(PG1.cols.size(), 3);

    //attempting to add back a seam when the history is empty
    //changes nothing
    PG1.addLastRemovedSeam();
    t.checkExpect(PG1.cols.size(), 3);

    //removing a horizontal seam
    PG1.computeEnergies();
    PG1.computeSeams(false);
    //removing seams decrements number of rows
    t.checkExpect(PG1.rows.size(), 3);
    PG1.removeSeam(false);
    t.checkExpect(PG1.rows.size(), 2);
    //adding last removed seam increments number of rows
    PG1.addLastRemovedSeam();
    t.checkExpect(PG1.rows.size(), 3);

    //attempting to add back a seam when the history is empty
    //changes nothing
    PG1.addLastRemovedSeam();
    t.checkExpect(PG1.rows.size(), 3);
  }

  void testRemoveTopFromHeaderList(Tester t) {
    resetVars();
    PG1.computeSeamAndPaintRed(true);
    RemovedSeam testRemove = new RemovedSeam(BL.seam, true);
    testRemove.topOfSeam = TL.up;
    testRemove.removeTopFromHeaderList(PG1.cols);
    t.checkExpect(testRemove.topOfSeamPos, 0);
    t.checkExpect(PG1.cols.size(), 2);
    t.checkExpect(PG1.cols.get(0), TM.up);
    
    t.checkException(new IllegalStateException("could not find col/row header to remove"), 
        testRemove, "removeTopFromHeaderList",
        PG1.cols);
    
    PG1.computeSeamAndPaintRed(false);
    RemovedSeam testRemove2 = new RemovedSeam(MR.seam, true);
    testRemove2.topOfSeam = BL.left;
    testRemove2.removeTopFromHeaderList(PG1.rows);
    t.checkExpect(testRemove2.topOfSeamPos, 2);
    t.checkExpect(PG1.rows.size(), 2);
  }

  void testPlaceOnImage(Tester t) {
    resetVars();
    ComputedPixelImage origin = new ComputedPixelImage(50, 50);
    ComputedPixelImage ret = new ComputedPixelImage(50, 50);
    t.checkExpect(origin, ret);
    TL.placeOnImage(origin, 24, 32);
    ret.setPixel(24, 32, TL.color);
    t.checkExpect(origin, ret);
    PG1.computeSeamAndPaintRed(true);
    BL.placeOnImage(origin, 22, 32);
    ret.setPixel(22, 32, Color.RED);
    t.checkExpect(origin, ret);
  }

  void testPlaceOnEnergyImage(Tester t) {
    resetVars();
    ComputedPixelImage origin = new ComputedPixelImage(50, 50);
    ComputedPixelImage ret = new ComputedPixelImage(50, 50);
    t.checkExpect(origin, ret);
    TL.placeOnEnergyImage(origin, 24, 32);
    int greyScale = (int)((TL.energy / Math.sqrt(32)) * 255);
    ret.setPixel(24, 32, new Color(greyScale, greyScale, greyScale));
    t.checkExpect(origin, ret);
    PG1.computeSeamAndPaintRed(true);
    BL.placeOnEnergyImage(origin, 22, 32);
    ret.setPixel(22, 32, Color.RED);
    t.checkExpect(origin, ret);
  }

  void testPlaceOnWeightImage(Tester t) {
    resetVars();
    ComputedPixelImage origin = new ComputedPixelImage(50, 50);
    ComputedPixelImage ret = new ComputedPixelImage(50, 50);
    BL.placeOnWeightImage(origin, 22, 32, 1);
    ret.setPixel(22, 32, new Color(0, 0, 0));
    t.checkExpect(origin, ret);
    PG1.computeSeamAndPaintRed(true);
    t.checkExpect(origin, ret);
    TR.placeOnWeightImage(origin, 24, 32, 6);
    int greyScale = (int)(255 * TR.seam.totalWeight / 6);
    ret.setPixel(24, 32, new Color(greyScale, greyScale, greyScale));
    t.checkExpect(origin, ret);
    ML.placeOnWeightImage(origin, 24, 32, 6);
    ret.setPixel(24, 32, Color.RED);
    t.checkExpect(origin, ret);
  }
  
  void testMaxSeamWeight(Tester t) {
    resetVars();
    PG1.computeSeamAndPaintRed(false);
    t.checkInexact(TM.maxSeamWeight(0), TM.seam.totalWeight, .001);
    t.checkInexact(TR.maxSeamWeight(52.43), 52.43, .001);
    t.checkInexact(ML.maxSeamWeight(2.023), ML.seam.totalWeight, .001);
    t.checkInexact(BM.maxSeamWeight(25.43), 25.43, .001);
  }
}
