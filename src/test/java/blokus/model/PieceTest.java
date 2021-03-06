package blokus.model;

import static org.testng.Assert.assertTrue;

import java.io.InputStream;
import java.util.ArrayList;

import org.testng.annotations.Test;

/**
 * PieceTest
 */
public class PieceTest {

  void showAllTrans(Piece p) {
    int noTrans = 1;
    System.out.println("\n~~~~~~~~~~~~~~~Transformations~~~~~~~~~~~~~~~");
    for (PieceTransform pt : p.getTransforms()) {
      System.out.println("transformation no " + noTrans + ":");
      p.apply(pt);
      System.out.println(p);
      ++noTrans;
    }
  }

  @Test
  public void piece_reader() {
    InputStream is = Config.loadRsc("pieces");
    PieceReader pr = new PieceReader(is);

    ArrayList<Piece> ps = new ArrayList<>();
    Piece p;
    while ((p = pr.nextPiece()) != null) {
      ps.add(p);
      System.out.println(p);
    }
    p = ps.get(12);
    Piece pRef = new Piece(p);

    // Test t1 = new Test("Piece manip");

    System.out.println(p);
    assertTrue(p.getDirection() == Direction.UP, "piece should be UP by default");
    assertTrue(!p.isReverted(), "piece should not be reverted by default");

    p.left();
    Piece pRefLeft = new Piece(p);
    System.out.println(p);
    assertTrue(p.getDirection() == Direction.LEFT, "piece should be LEFT after one left()");
    assertTrue(!p.isReverted(), "piece should not be reverted after one left()");

    p.left();
    System.out.println(p);
    assertTrue(p.getDirection() == Direction.DOWN, "piece should be DOWN after two left()");
    assertTrue(!p.isReverted(), "piece should not be reverted after two left()");

    p.left();
    System.out.println(p);
    assertTrue(p.getDirection() == Direction.RIGHT, "piece should be RIGHT after three left()");
    assertTrue(!p.isReverted(), "piece should not be reverted after three left()");

    p.left();
    System.out.println(p);
    assertTrue(p.equals(pRef), "piece should be equals to refence piece taken before any modif");
    assertTrue(p.getDirection() == Direction.UP, "piece should be UP after four left()");
    assertTrue(!p.isReverted(), "piece should not be reverted after four left()");

    p.right();
    // p should be right
    System.out.println(p);
    assertTrue(p.getDirection() == Direction.RIGHT, "piece should be RIGHT after four left() then one right()");

    p.revertX();
    // p should still be right but reverted
    System.out.println(p);
    assertTrue(p.getDirection() == Direction.RIGHT, "piece should STILL be RIGHT after one right() then revertX()");
    assertTrue(p.isReverted(), "piece should be reverted after one revert_() eg: revertX()");

    p.revertY();
    // p should LEFT
    System.out.println(p);
    assertTrue(p.equals(pRefLeft), "piece should be equals to refence piece taken on left()");
    assertTrue(p.getDirection() == Direction.LEFT, "piece should be LEFT after one right() then revertY()");
    assertTrue(!p.isReverted(), "piece should NOT be reverted after two revert_() eg: revertX() then revertY()");

    // t1.end();

    // Test t2 = new Test("Piece Transform");

    p = ps.get(0);
    assertTrue(p.getTransforms().size() == 1, "piece 0 should have one transformation");
    showAllTrans(p);

    p = ps.get(15);
    assertTrue(p.getTransforms().size() == 8, "piece 0 should have one transformation");
    showAllTrans(p);

    p = ps.get(1);
    assertTrue(p.getTransforms().size() == 2, "piece 0 should have one transformation");
    showAllTrans(p);

    // t2.end();

    // number of corners of pieces
    int nbAllCorners = 0;
    int nbAllTrans = 0;

    for (Piece pie : ps) {
      nbAllCorners += pie.getCorners().size();
      nbAllTrans += pie.getTransforms().size();
    }

    System.out.println("All pieces corners: " + nbAllCorners);
    System.out.println("All pieces transforms: " + nbAllTrans);
  }
}
