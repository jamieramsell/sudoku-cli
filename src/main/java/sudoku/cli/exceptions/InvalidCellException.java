package sudoku.cli.exceptions;

/** Thrown when a requested cell coordinate does not exist within the bounds of the Sudoku grid. */
public class InvalidCellException extends ControllerException {

  /**
   * Constructs a new {@code InvalidCellException} for the given out-of-bounds cell.
   *
   * @param row The row (or y-coordinate) of the requested cell.
   * @param col The column (or x-coordinate) of the requested cell.
   */
  public InvalidCellException(int row, int col) {
    super(String.format("Cell (%d, %d) does not exist within the grid.", row, col));
  }
}
