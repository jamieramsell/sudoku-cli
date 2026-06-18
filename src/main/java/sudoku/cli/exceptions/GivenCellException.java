package sudoku.cli.exceptions;

/** Thrown when an operation attempts to modify a cell that stores a given/hinted value. */
public class GivenCellException extends ControllerException {

  /**
   * Constructs a new {@code GivenCellException} for the given cell.
   *
   * @param row The row (or y-coordinate) of the given cell.
   * @param col The column (or x-coordinate) of the given cell.
   */
  public GivenCellException(int row, int col) {
    super(String.format("Cell (%d, %d) stores a given value and cannot be modified.", row, col));
  }
}
