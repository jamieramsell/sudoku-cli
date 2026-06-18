package sudoku.cli.exceptions;

/** Thrown when a requested value to place in a cell exceeds the size of the Sudoku grid. */
public class InvalidValueException extends ControllerException {

  /**
   * Constructs a new {@code InvalidValueException} for the given out-of-range value.
   *
   * @param value The value which was rejected.
   * @param gridSize The size of the Sudoku grid, which bounds the set of valid values.
   */
  public InvalidValueException(int value, int gridSize) {
    super(String.format("Value %d exceeds the grid size of %d.", value, gridSize));
  }
}
