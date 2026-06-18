package sudoku.cli.exceptions;

/** Thrown when an operation attempts to clear a cell that is already empty. */
public class EmptyCellException extends ControllerException {

  /**
   * Constructs a new {@code EmptyCellException} for the given cell.
   *
   * @param row The row (or y-coordinate) of the empty cell.
   * @param col The column (or x-coordinate) of the empty cell.
   */
  public EmptyCellException(int row, int col) {
    super(String.format("Cell (%d, %d) is already empty.", row, col));
  }
}
