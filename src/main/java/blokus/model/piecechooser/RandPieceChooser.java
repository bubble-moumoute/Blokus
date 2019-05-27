package blokus.model.piecechooser;

import java.util.List;
import java.util.Random;

import blokus.model.Move;
import blokus.model.Node;
import blokus.model.Piece;

public class RandPieceChooser implements PieceChooser {
  Random r = new Random();

  @Override
  public Piece pickPiece(List<Piece> availablePieces) {
    return availablePieces.get(r.nextInt(availablePieces.size()));
  }

  @Override
  public Move pickMove(List<Move> moves) {
    return moves.get(r.nextInt(moves.size()));
  }

  @Override
  public Node pickNode(List<Node> nodes) {
    return nodes.get(r.nextInt(nodes.size()));
  }

  @Override
  public List<Node> selectNodes(List<Node> nodes) {
    throw new IllegalStateException("RandPieceChooser's selectNodes method not applicable");
  }

  @Override
  public List<Move> selectMoves(List<Move> moves) {
    throw new IllegalStateException("RandPieceChooser's selectMoves method not applicable");
  }

}