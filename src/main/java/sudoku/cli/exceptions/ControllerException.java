package sudoku.cli.exceptions;

/**
 * Base type for checked exceptions thrown by {@code sudoku.cli.IController} operations,
 * representing recoverable error conditions that arise from invalid user actions.
 */
public abstract class ControllerException extends Exception {

  /**
   * Constructs a new {@code ControllerException} with the given detail message.
   *
   * @param message The detail message describing the error condition.
   */
  protected ControllerException(String message) {
    super(message);
  }
}
