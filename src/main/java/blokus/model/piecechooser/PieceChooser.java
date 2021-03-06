package blokus.model.piecechooser;

import java.io.Serializable;
import java.util.List;

import blokus.model.Move;
import blokus.model.Node;
import blokus.model.Piece;

public interface PieceChooser extends Serializable {
  Piece pickPiece(List<Piece> availablePieces);

  Move pickMove(List<Move> moves);

  Node pickNode(List<Node> nodes);

}