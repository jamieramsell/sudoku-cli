package sudoku.cli.exceptions;

/**
 * Thrown when an operation requires unsolved cells, but the Sudoku grid has already been solved.
 */
public class GridSolvedException extends ControllerException {

  /** Constructs a new {@code GridSolvedException}. */
  public GridSolvedException() {
    super("The grid has already been solved.");
  }
}
