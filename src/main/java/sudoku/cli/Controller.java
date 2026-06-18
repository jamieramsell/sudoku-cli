package sudoku.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import sudoku.ISudokuGrid;
import sudoku.ISudokuSolver;
import sudoku.SudokuSolver;
import sudoku.Tuple2;
import sudoku.cli.exceptions.EmptyCellException;
import sudoku.cli.exceptions.EmptyGridException;
import sudoku.cli.exceptions.GivenCellException;
import sudoku.cli.exceptions.GridSolvedException;
import sudoku.cli.exceptions.InvalidCellException;
import sudoku.cli.exceptions.InvalidValueException;
import sudoku.generation.ISudokuGenerator;
import sudoku.generation.ISudokuGenerator.PuzzleDifficulty;
import sudoku.generation.SudokuGenerator;
import sudoku.generation.symmetry.NoSymmetry;

/**
 * Concrete implementation of {@code IController}, facilitating communication between the Model and
 * View layers of the stack.
 */
public class Controller implements IController {
  
  // Constants //

  private static final int DEFAULT_SIZE = ISudokuGrid.DEFAULT_SIZE;

  // Attributes //

  private ISudokuGenerator generator;
  private ISudokuGrid grid;
  private ISudokuSolver solver;
  private ISudokuGrid solvedGrid;
  private List<Tuple2<Integer, Integer>> protectedCells;

  // Constructor Methods //

  /**
   * Constructs a controller and immediately starts a new game at the given difficulty.
   *
   * @param difficulty The difficulty, which determines the number of givens in the generated grid.
   */
  public Controller(PuzzleDifficulty difficulty) {
    newGame(difficulty);
  }

  @Override
  public void newGame(PuzzleDifficulty difficulty) {
    this.generator = new SudokuGenerator(difficulty, new NoSymmetry(DEFAULT_SIZE));
    this.grid = generator.generatePuzzle(true);

    // The cells the generator leaves filled are the puzzle's givens. Record them up front (before
    // solving, while the grid is still the pristine puzzle) so they cannot later be overwritten or
    // cleared by the player. Hints append to this same list.
    this.protectedCells = new ArrayList<>();
    int gridSize = grid.getSize();
    for (int row = 0; row < gridSize; row++) {
      for (int col = 0; col < gridSize; col++) {
        if (grid.getValue(row, col) != -1) {
          protectedCells.add(new Tuple2<>(row, col));
        }
      }
    }

    this.solver = new SudokuSolver(grid);

    Optional<ISudokuGrid> solvedGridOptional =
        solver.solveGrid()
        .stream()
        .findFirst();

    if (solvedGridOptional.isPresent()) {
      this.solvedGrid = solvedGridOptional.get();
    } else {
      throw new IllegalStateException("ISudokuGenerator generated a puzzle with no solution");
    }

  }

  // Implementation //

  @Override
  public void placeValue(int row, int col, int value) throws InvalidCellException,
      InvalidValueException, GivenCellException {

    if (isCellProtected(row, col)) {
      throw new GivenCellException(row, col);
    } else if (value < 1 || value > grid.getSize()) {
      throw new InvalidValueException(value, grid.getSize());
    }

    try {
      grid.setValue(row, col, value);
    } catch (IndexOutOfBoundsException e) {
      throw new InvalidCellException(row, col);
    }

  }

  @Override
  public void clearCell(int row, int col) throws InvalidCellException, EmptyCellException,
      GivenCellException {
    
    if (isCellProtected(row, col)) {
      throw new GivenCellException(row, col);
    }

    try {
      if (grid.getValue(row, col) == -1) {
        throw new EmptyCellException(row, col);
      } else {
        grid.setValue(row, col, -1);
      }
    } catch (IndexOutOfBoundsException e) {
      throw new InvalidCellException(row, col);
    }

  }

  @Override
  public void hint() throws GridSolvedException {
    
    if (grid.isSolved()) {
      throw new GridSolvedException();
    }

    List<Tuple2<Integer, Integer>> replaceableCells = new ArrayList<>();

    // Replace either an empty cell, or one which is incorrectly placed

    // Find all replaceable_cells
    int gridSize = grid.getSize();

    for (int row = 0; row < gridSize; row++) {
      for (int col = 0; col < gridSize; col++) {

        int cellValue = grid.getValue(row, col);
        if (cellValue == -1 || !ISudokuSolver.isPlacementValid(grid, row, col, cellValue)) {
          replaceableCells.add(new Tuple2<>(row, col));
        } 

      }
    }

    // Select one of the replaceable cells at random. Because the grid is not solved, there is
    // always at least one empty or incorrect cell, so this list is never empty here. Choosing only
    // from this list guarantees a hint never lands on a cell the player has already filled in
    // correctly.
    var random = new Random();
    var randomCell = replaceableCells.get(random.nextInt(replaceableCells.size()));

    // Add the cell to protectedCells and replace its value in the grid with the solved value
    protectedCells.add(randomCell);

    int row = randomCell.first();
    int col = randomCell.second();
    int solvedValue = solvedGrid.getValue(row, col);
    grid.setValue(row, col, solvedValue);

  }

  @Override
  public void solve() throws GridSolvedException {
    if (grid.isSolved()) {
      throw new GridSolvedException();
    } 

    int gridSize = grid.getSize();

    for (int row = 0; row < gridSize; row++) {
      for (int col = 0; col < gridSize; col++) {
        grid.setValue(row, col, solvedGrid.getValue(row, col));
      }
    }
  }

  @Override
  public void reset() throws EmptyGridException {
    int emptiedCells = 0;
    int gridSize = grid.getSize();

    for (int row = 0; row < gridSize; row++) {
      for (int col = 0; col < gridSize; col++) {
        // Only player-entered cells (not given/hint cells) that actually hold a value are cleared.
        if (!isCellProtected(row, col) && grid.getValue(row, col) != -1) {
          grid.setValue(row, col, -1);
          emptiedCells++;
        }
      }
    }

    if (emptiedCells == 0) {
      throw new EmptyGridException();
    }
  }

  @Override
  public ISudokuGrid getGridState() {
    return grid;
  }

  // Convenience Methods //

  /** Convenience method to check whether a given cell is protected. */
  private boolean isCellProtected(int row, int col) {
    Tuple2<Integer, Integer> targetCell = new Tuple2<>(row, col);
    return protectedCells.contains(targetCell);
  }

}
