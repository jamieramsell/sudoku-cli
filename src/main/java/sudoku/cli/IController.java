package sudoku.cli;

import sudoku.ISudokuGrid;

import sudoku.cli.exceptions.EmptyCellException;
import sudoku.cli.exceptions.EmptyGridException;
import sudoku.cli.exceptions.GivenCellException;
import sudoku.cli.exceptions.GridSolvedException;
import sudoku.cli.exceptions.InvalidCellException;
import sudoku.cli.exceptions.InvalidValueException;

import sudoku.generation.ISudokuGenerator.PuzzleDifficulty;

/**
 * Outlines the controller layer of the game, which wraps the sudoku package, facilitating all
 * communication between the model and view layers of the MVC stack of the game. Stores the current
 * state of the Sudoku game, which is mutated as the game progresses.
 */
public interface IController {
  
  /**
   * Generates a new sudoku grid, starting with the given difficulty.
   * 
   * @param difficulty The difficulty, which determines the number of givens in the grid.
   * 
   * @see sudoku.ISudokuGrid
   * @see sudoku.generation.ISudokuGenerator.PuzzleDifficulty
   */
  void newGame(PuzzleDifficulty difficulty);

  /**
   * Attempts to place a given value in the target cell.
   * 
   * <p> The value to be placed within the cell must fall within the bounds of the size of the
   * {@link sudoku.ISudokuGrid}. For example, a typical grid has a size of 9, meaning the values 1-9
   * inclusive must be placed exactly once in each row, column, and box.
   * 
   * @param row The row (or y-coordinate) of the target cell.
   * @param col The column (or x-coordinate) of the target cell.
   * @param value The value to attempt to place within the given cell.
   * 
   * @throws InvalidCellException if the given cell coordinates do not exist within the Sudoku
   * grid.
   * @throws InvalidValueException if the given value exceeds the size of the Sudoku grid.
   * @throws GivenCellException if the given cell stores a given value: these cannot be
   * overwritten.
   *
   * @see sudoku.ISudokuGrid
   */
  void placeValue(int row, int col, int value) throws InvalidCellException, InvalidValueException,
      GivenCellException;

  /**
   * Clears the target cell of the value which it currently stores.
   * 
   * @param row The row (or y-coordinate) of the target cell.
   * @param col The column (or x-coordinate) of the target cell.
   * 
   * @throws InvalidCellException if the given cell coordinates do not exist within the Sudoku
   * grid.
   * @throws EmptyCellException if the given cell is already empty.
   * @throws GivenCellException if trying to clear a given cell.
   */
  void clearCell(int row, int col) throws InvalidCellException, EmptyCellException,
      GivenCellException;

  /**
   * Generates a new given cell in a random place on the Sudoku grid.
   * 
   * <p> If all cells are filled, but the grid has not yet been solved, the algorithm will replace a
   * cell with an existing, but incorrect, entry with a given value.
   * 
   * <p> The algorithm will never place a hint in a cell in which the player has placed a value
   * which is already correct.
   * 
   * @throws GridSolvedException if the grid has already been solved: there are no more cells
   * left to hint.
   */
  void hint() throws GridSolvedException;

  /**
   * Completes the grid by filling in any blank cells, and replacing any that are incorrect.
   *
   * @throws GridSolvedException if the grid has already been solved.
   */
  void solve() throws GridSolvedException;

  /**
   * Resets the grid to an empty state.
   *
   * <p> Note that given/hint cells are not cleared.
   *
   * @throws EmptyGridException if the grid is already empty.
   */
  void reset() throws EmptyGridException;

  /**
   * Returns the current state of the Sudoku grid.
   *
   * @return the current state of the Sudoku grid.
   *
   * @throws IllegalStateException if the instance of {@code IController} does not have an ongoing
   * Sudoku game.
   *
   * @see sudoku.ISudokuGrid
   */
  ISudokuGrid getGridState();

}
