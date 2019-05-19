package blokus.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import blokus.utils.Utils;
import javafx.scene.paint.Color;

/**
 * Class Board
 */
public class Board {
  public static final Coord SIZE = new Coord(14, 14);

  private static final ArrayList<Color> colors = new ArrayList<>();
  private static final ArrayList<String> colorsName = new ArrayList<>();
  //
  // Fields
  //

  /**
   * y line of x colors
   */
  private byte[] boardColor = new byte[(SIZE.y * SIZE.x)];

  private HashMap<Color, ArrayList<Piece>> pieces = new HashMap<>();
  private HashMap<Color, HashSet<Coord>> accCorners = new HashMap<>();

  //
  // Constructors
  //
  public Board() {
    addColor(null, "NULL");
    addColor(Color.BLUE, "Blue");
    addColor(Color.YELLOW, "Yellow");
    addColor(Color.RED, "Red");
    addColor(Color.GREEN, "Green");
  }

  public Board(Board b) {
    System.arraycopy(b.boardColor, 0, boardColor, 0, boardColor.length);

    pieces.putAll(b.pieces);
    accCorners.putAll(b.accCorners);
  }

  //
  // Methods
  //

  /**
   * @param piece
   * @param pos
   * @param color
   */
  public void add(Piece piece, Coord pos, Color color) {
    byte colorId = getColorId(color);
    if (canAdd(piece, pos, color)) {
      piece.translate(pos);
      for (Coord c : piece.getShape()) {
        set(c, colorId);
      }
      pieces.computeIfAbsent(color, (_c) -> {
        return new ArrayList<>();
      }).add(piece);

    } else {
      throw new IllegalArgumentException("can't place " + Utils.getAnsi(color) + getColorName(color) + Utils.ANSI_RESET
          + " piece at " + pos + "\n------------pieces------------:\n" + pieces + "\n------------piece------------:\n"
          + piece + "\n------------Board------------:\n" + this);
    }
  }

  public void remove(Piece piece, Color color) {
    pieces.get(color).remove(piece);
    for (Coord c : piece.getShape()) {
      set(c, (byte) 0);
    }
  }

  /**
   * @return HashMap<Color, Integer>
   */
  public HashMap<Color, Integer> numOfEachColor() {
    HashMap<Color, Integer> map = new HashMap<>();

    for (int i = 0; i < boardColor.length; ++i) {
      Color c = get(i);
      if (c != null) {
        map.put(c, map.getOrDefault(c, 0) + 1);
      }
    }

    return map;
  }

  /**
   * @param piece
   * @param pos
   */
  public boolean canAdd(Piece piece, Coord pos, Color color) {
    boolean ret = true;
    byte colorId = getColorId(color);
    try {
      ArrayList<Coord> shape = piece.getShape();
      Coord tmp;
      for (int i = 0; ret && i < shape.size(); ++i) {
        tmp = pos.add(shape.get(i));
        ret = ret && canAdd(tmp.x, tmp.y, colorId);
      }
      boolean cornerCheck = false;
      if (isFirst(color)) {
        for (int i = 0; !cornerCheck && i < shape.size(); ++i) {
          tmp = pos.add(shape.get(i));
          cornerCheck = cornerCheck || isCorner(tmp);
        }
      } else {
        ArrayList<Coord> corners = piece.getCorners();
        for (int i = 0; !cornerCheck && i < corners.size(); ++i) {
          Coord corner = pos.add(corners.get(i));
          cornerCheck = cornerCheck || (isIn(corner.x, corner.y) && getId(corner) == colorId);
        }
      }
      ret = ret && cornerCheck;
    } catch (ArrayIndexOutOfBoundsException e) {
      ret = false;
    }
    return ret;
  }

  private boolean canAdd(int x, int y, byte color) {
    boolean ret = isIn(x, y) && getId(x, y) == 0;
    for (int i = -1; i < 2; i += 2) {
      int tmpX = x + i;
      int tmpY = y + i;
      ret = ret && (!isIn(tmpX, y) || getId(tmpX, y) != color);
      ret = ret && (!isIn(x, tmpY) || getId(x, tmpY) != color);
    }
    return ret;
  }

  public Set<Coord> getAccCorners(Color color) {
    if (!accCorners.containsKey(color)) {
      HashSet<Coord> res = new HashSet<>();
      byte colorId = getColorId(color);
      if (!isFirst(color)) {
        for (Piece p : pieces.get(color)) {
          for (Coord c : p.getCorners()) {
            if (canAdd(c.x, c.y, colorId)) {
              res.add(c);
            }
          }
        }
      } else {
        res.add(new Coord(0, 0));
        res.add(new Coord(SIZE.x - 1, 0));
        res.add(new Coord(0, SIZE.y - 1));
        res.add(new Coord(SIZE.x - 1, SIZE.y - 1));
        Iterator<Coord> it = res.iterator();
        for (Coord c = it.next(); it.hasNext(); c = it.next()) {
          if (getId(c) != 0) {
            it.remove();
          }
        }
      }
      accCorners.put(color, res);
    }
    return Collections.unmodifiableSet(accCorners.get(color));
  }

  //
  // Accessor methods
  //

  /**
   * check bounds of the board
   */
  public boolean isIn(int x, int y) {
    return x >= 0 && y >= 0 && x < SIZE.x && y < SIZE.y;
  }

  public Color get(int x, int y) {
    return get(toI(x, y));
  }

  public Color get(Coord pos) {
    return get(pos.x, pos.y);
  }

  private Color get(int i) {
    return getColor(getId(i));
  }

  /**
   * @param i
   * @return 0 no color 1..4: nth color
   */
  private byte getId(int i) {
    return (byte) (boardColor[i]);
  }

  private byte getId(int x, int y) {
    return getId(toI(x, y));
  }

  private byte getId(Coord c) {
    return getId(c.x, c.y);
  }

  private void set(int x, int y, byte c) {
    set(toI(x, y), c);
  }

  private void set(Coord pos, byte c) {
    set(pos.x, pos.y, c);
  }

  private void set(int i, byte c) {
    boardColor[i] = c;
    change();
  }

  private int toI(int x, int y) {
    return x + y * SIZE.x;
  }

  /**
   * return true if c is at a corner of the board</br>
   *
   * *---*</br>
   * -----</br>
   * -----</br>
   * *---*</br>
   */
  private boolean isCorner(Coord c) {
    return (c.x == 0 || c.x == SIZE.x - 1) && (c.y == 0 || c.y == SIZE.y - 1);
  }

  /**
   *
   * @param color
   * @return true if color hasn't played before in the board
   */
  private boolean isFirst(Color color) {
    return !pieces.containsKey(color) || pieces.get(color).isEmpty();
  }

  public ArrayList<Piece> getPlayed(Color c) {
    return pieces.get(c);
  }

  public void addColor(Color color, String colorName) {
    colors.add(color);
    colorsName.add(colorName);
  }

  /**
   * @param c
   * @return
   */
  public static String getColorName(Color c) {
    return colorsName.get(getColorId(c));
  }

  /**
   * 0 is null
   * 
   * begins at 1
   */
  public static byte getColorId(Color c) {
    return (byte) colors.indexOf(c);
  }

  /**
   * begins at 1
   * 
   * 0 is null
   */
  public static Color getColor(byte val) {
    return colors.get(val);
  }

  private void change() {
    accCorners.clear();
  }

  //
  // Other methods
  //

  @Override
  public String toString() {
    String ret = "\n";
    for (int i = 0; i < SIZE.y; ++i) {
      for (int j = 0; j < SIZE.x; j++) {
        Color c = get(j, i);
        if (c == null) {
          ret += "_ ";
        } else {
          ret += Utils.getAnsi(c) + Integer.toString(getColorId(c)) + Utils.ANSI_RESET + " ";
        }
      }
      ret += "\n";
    }
    return ret;
  }
}
