package sudoku.cli.exceptions;

/** Thrown when an operation requires a non-empty grid, but the Sudoku grid is already empty. */
public class EmptyGridException extends ControllerException {

  /** Constructs a new {@code EmptyGridException}. */
  public EmptyGridException() {
    super("The grid is already empty.");
  }
}
