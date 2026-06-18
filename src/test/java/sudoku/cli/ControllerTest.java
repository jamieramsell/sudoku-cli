package sudoku.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import sudoku.ISudokuGrid;
import sudoku.SudokuSolver;
import sudoku.cli.exceptions.EmptyCellException;
import sudoku.cli.exceptions.EmptyGridException;
import sudoku.cli.exceptions.GivenCellException;
import sudoku.cli.exceptions.GridSolvedException;
import sudoku.cli.exceptions.InvalidCellException;
import sudoku.cli.exceptions.InvalidValueException;
import sudoku.generation.ISudokuGenerator.PuzzleDifficulty;

/**
 * Unit and integration tests for {@link Controller}.
 *
 * <p>The {@code Controller} wires itself directly to the real engine generator and solver, so there
 * is no seam to inject test doubles; these tests therefore drive the genuine engine and assert
 * against the actual generated puzzle. To stay deterministic despite the random generator, each
 * test inspects the grid it was handed (which cells are givens, which are empty) and computes the
 * puzzle's unique solution up front, then asserts relative to those facts rather than hard-coded
 * values.
 */
public class ControllerTest {

  private Controller controller;
  private ISudokuGrid grid;
  private ISudokuGrid solution;
  private int size;

  /** Starts a fresh EASY game and computes its unique solution before each test. */
  @Before
  public void setUp() {
    controller = new Controller(PuzzleDifficulty.EASY);
    grid = controller.getGridState();
    size = grid.getSize();
    // Solve a clone so the controller's live grid is left untouched. The puzzle is generated with a
    // unique solution, so this matches the solution the controller computed internally.
    solution = new SudokuSolver(grid.clone()).solveGrid().iterator().next();
  }

  // --- Construction / newGame ----------------------------------------------------------------- //

  @Test
  public void constructorStartsValidUnsolvedGame() {
    assertNotNull(grid);
    assertEquals(ISudokuGrid.DEFAULT_SIZE, size);
    assertTrue("a freshly generated puzzle should be valid", grid.isValid());
    assertFalse("a freshly generated puzzle should not be solved", grid.isSolved());
    assertNotNull("puzzle should contain at least one given", firstGivenCell());
    assertNotNull("puzzle should contain at least one empty cell", firstEmptyCell());
  }

  @Test
  public void newGameReplacesGridWithFreshUnsolvedPuzzle() throws GridSolvedException {
    controller.solve();
    assertTrue(controller.getGridState().isSolved());

    controller.newGame(PuzzleDifficulty.EASY);
    grid = controller.getGridState();

    assertTrue(grid.isValid());
    assertFalse("a new game must hand back an unsolved puzzle", grid.isSolved());
  }

  @Test
  public void newGameProtectsEveryGivenCell() {
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        if (grid.getValue(row, col) != -1) {
          assertCellProtected(row, col);
        }
      }
    }
  }

  // --- placeValue ----------------------------------------------------------------------------- //

  @Test
  public void placeValuePopulatesAnEmptyCell()
      throws InvalidCellException, InvalidValueException, GivenCellException {
    int[] cell = firstEmptyCell();
    int value = solution.getValue(cell[0], cell[1]);

    controller.placeValue(cell[0], cell[1], value);

    assertEquals(value, grid.getValue(cell[0], cell[1]));
  }

  @Test
  public void placeValueCanOverwritePlayerEntry()
      throws InvalidCellException, InvalidValueException, GivenCellException {
    int[] cell = firstEmptyCell();

    controller.placeValue(cell[0], cell[1], 1);
    controller.placeValue(cell[0], cell[1], 2);

    assertEquals(2, grid.getValue(cell[0], cell[1]));
  }

  @Test
  public void placeValueOnGivenCellThrowsAndLeavesItUnchanged()
      throws InvalidCellException, InvalidValueException {
    int[] cell = firstGivenCell();
    int before = grid.getValue(cell[0], cell[1]);

    try {
      controller.placeValue(cell[0], cell[1], 1);
      fail("expected GivenCellException");
    } catch (GivenCellException expected) {
      // expected
    }

    assertEquals(before, grid.getValue(cell[0], cell[1]));
  }

  @Test(expected = InvalidValueException.class)
  public void placeValueRejectsValueAboveGridSize()
      throws InvalidCellException, InvalidValueException, GivenCellException {
    int[] cell = firstEmptyCell();
    controller.placeValue(cell[0], cell[1], size + 1);
  }

  @Test(expected = InvalidValueException.class)
  public void placeValueRejectsValueBelowOne()
      throws InvalidCellException, InvalidValueException, GivenCellException {
    int[] cell = firstEmptyCell();
    controller.placeValue(cell[0], cell[1], 0);
  }

  @Test(expected = InvalidCellException.class)
  public void placeValueRejectsAnOutOfBoundsCell()
      throws InvalidCellException, InvalidValueException, GivenCellException {
    controller.placeValue(size, 0, 1);
  }

  @Test(expected = InvalidCellException.class)
  public void placeValueRejectsNegativeCell()
      throws InvalidCellException, InvalidValueException, GivenCellException {
    controller.placeValue(-1, 0, 1);
  }

  @Test
  public void placeValueChecksGivenStatusBeforeValueRange()
      throws InvalidCellException, InvalidValueException {
    // A given cell combined with an out-of-range value must still report the given-cell problem.
    int[] cell = firstGivenCell();
    try {
      controller.placeValue(cell[0], cell[1], size + 1);
      fail("expected GivenCellException");
    } catch (GivenCellException expected) {
      // expected
    }
  }

  @Test
  public void placeValueChecksValueRangeBeforeCellBounds()
      throws InvalidCellException, GivenCellException {
    // An out-of-bounds cell combined with an out-of-range value must report the value problem,
    // because the value is validated before the grid is asked to place it.
    try {
      controller.placeValue(size, 0, size + 1);
      fail("expected InvalidValueException");
    } catch (InvalidValueException expected) {
      // expected
    }
  }

  // --- clearCell ------------------------------------------------------------------------------ //

  @Test
  public void clearCellEmptiesPlayerEntry() throws InvalidCellException, InvalidValueException,
      GivenCellException, EmptyCellException {
    int[] cell = firstEmptyCell();
    controller.placeValue(cell[0], cell[1], 1);

    controller.clearCell(cell[0], cell[1]);

    assertEquals(-1, grid.getValue(cell[0], cell[1]));
  }

  @Test(expected = EmptyCellException.class)
  public void clearCellOnAnAlreadyEmptyCellThrows()
      throws InvalidCellException, EmptyCellException, GivenCellException {
    int[] cell = firstEmptyCell();
    controller.clearCell(cell[0], cell[1]);
  }

  @Test
  public void clearCellOnGivenCellThrowsAndLeavesItUnchanged()
      throws InvalidCellException, EmptyCellException {
    int[] cell = firstGivenCell();
    int before = grid.getValue(cell[0], cell[1]);

    try {
      controller.clearCell(cell[0], cell[1]);
      fail("expected GivenCellException");
    } catch (GivenCellException expected) {
      // expected
    }

    assertEquals(before, grid.getValue(cell[0], cell[1]));
  }

  @Test(expected = InvalidCellException.class)
  public void clearCellRejectsAnOutOfBoundsCell()
      throws InvalidCellException, EmptyCellException, GivenCellException {
    controller.clearCell(size, 0);
  }

  // --- hint ----------------------------------------------------------------------------------- //

  @Test
  public void hintFillsCellWithSolvedValueAndProtectsIt() throws GridSolvedException {
    int[][] before = snapshot();
    controller.hint();
    int[] hinted = singleChangedCell(before, snapshot());

    assertEquals("hint must place the solved value",
        solution.getValue(hinted[0], hinted[1]), grid.getValue(hinted[0], hinted[1]));
    assertTrue("the grid must remain valid after a hint", grid.isValid());
    assertCellProtected(hinted[0], hinted[1]);
  }

  @Test
  public void hintOnlyTargetsReplaceableCellNeverCorrectEntry()
      throws InvalidCellException, InvalidValueException, GivenCellException, GridSolvedException {
    // Fill every empty cell correctly except one, so the single remaining replaceable cell is that
    // last empty cell. A correct hint must complete exactly that cell, proving it never overwrites
    // the entries the player already got right.
    List<int[]> empties = emptyCells();
    for (int i = 0; i < empties.size() - 1; i++) {
      int[] cell = empties.get(i);
      controller.placeValue(cell[0], cell[1], solution.getValue(cell[0], cell[1]));
    }
    assertFalse(grid.isSolved());

    controller.hint();

    int[] last = empties.get(empties.size() - 1);
    assertEquals(solution.getValue(last[0], last[1]), grid.getValue(last[0], last[1]));
    assertTrue("filling the final cell correctly should solve the grid", grid.isSolved());
  }

  @Test(expected = GridSolvedException.class)
  public void hintOnSolvedGridThrows() throws GridSolvedException {
    controller.solve();
    controller.hint();
  }

  // --- solve ---------------------------------------------------------------------------------- //

  @Test
  public void solveFillsTheGridWithTheSolution() throws GridSolvedException {
    controller.solve();

    assertTrue(grid.isSolved());
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        assertEquals(solution.getValue(row, col), grid.getValue(row, col));
      }
    }
  }

  @Test
  public void solveOverwritesAnIncorrectPlayerEntry()
      throws InvalidCellException, InvalidValueException, GivenCellException, GridSolvedException {
    int[] cell = firstEmptyCell();
    int correct = solution.getValue(cell[0], cell[1]);
    int wrong = (correct % size) + 1;
    controller.placeValue(cell[0], cell[1], wrong);

    controller.solve();

    assertEquals(correct, grid.getValue(cell[0], cell[1]));
  }

  @Test(expected = GridSolvedException.class)
  public void solveOnAnAlreadySolvedGridThrows() throws GridSolvedException {
    controller.solve();
    controller.solve();
  }

  // --- reset ---------------------------------------------------------------------------------- //

  @Test
  public void resetClearsPlayerEntriesButKeepsGivens() throws InvalidCellException,
      InvalidValueException, GivenCellException, EmptyGridException {
    List<int[]> empties = emptyCells();
    List<int[]> touched = empties.subList(0, Math.min(3, empties.size()));
    for (int[] cell : touched) {
      controller.placeValue(cell[0], cell[1], 1);
    }

    controller.reset();

    for (int[] cell : touched) {
      assertEquals(-1, grid.getValue(cell[0], cell[1]));
    }
    assertNotNull("givens must survive a reset", firstGivenCell());
    assertEquals(solution.getValue(firstGivenCell()[0], firstGivenCell()[1]),
        grid.getValue(firstGivenCell()[0], firstGivenCell()[1]));
  }

  @Test(expected = EmptyGridException.class)
  public void resetOnAnUntouchedPuzzleThrows() throws EmptyGridException {
    controller.reset();
  }

  @Test(expected = EmptyGridException.class)
  public void resetTwiceThrowsTheSecondTime() throws InvalidCellException, InvalidValueException,
      GivenCellException, EmptyGridException {
    int[] cell = firstEmptyCell();
    controller.placeValue(cell[0], cell[1], 1);
    controller.reset();
    controller.reset();
  }

  @Test
  public void resetDoesNotClearHintCells() throws GridSolvedException, InvalidCellException,
      InvalidValueException, GivenCellException, EmptyGridException {
    int[][] before = snapshot();
    controller.hint();
    int[] hinted = singleChangedCell(before, snapshot());
    int hintedValue = grid.getValue(hinted[0], hinted[1]);

    // Make a player entry somewhere else so reset has something to clear.
    int[] player = firstEmptyCell();
    controller.placeValue(player[0], player[1], 1);

    controller.reset();

    assertEquals("a hinted cell behaves like a given and survives reset",
        hintedValue, grid.getValue(hinted[0], hinted[1]));
    assertCellProtected(hinted[0], hinted[1]);
    assertEquals(-1, grid.getValue(player[0], player[1]));
  }

  // --- getGridState --------------------------------------------------------------------------- //

  @Test
  public void getGridStateReturnsTheSameLiveInstance()
      throws InvalidCellException, InvalidValueException, GivenCellException {
    assertSame(controller.getGridState(), controller.getGridState());

    int[] cell = firstEmptyCell();
    controller.placeValue(cell[0], cell[1], 1);
    assertEquals("getGridState should reflect live mutations", 1,
        controller.getGridState().getValue(cell[0], cell[1]));
  }

  // --- Integration ---------------------------------------------------------------------------- //

  @Test
  public void playingEveryCellCorrectlySolvesThePuzzle()
      throws InvalidCellException, InvalidValueException, GivenCellException {
    for (int[] cell : emptyCells()) {
      controller.placeValue(cell[0], cell[1], solution.getValue(cell[0], cell[1]));
      assertTrue("the grid must stay valid as correct values are entered", grid.isValid());
    }
    assertTrue(grid.isSolved());
  }

  // --- Helpers -------------------------------------------------------------------------------- //

  /** Asserts the given cell rejects further placement because it is protected. */
  private void assertCellProtected(int row, int col) {
    try {
      controller.placeValue(row, col, 1);
      fail("expected GivenCellException for protected cell (" + row + ", " + col + ")");
    } catch (GivenCellException expected) {
      // expected
    } catch (InvalidCellException | InvalidValueException e) {
      fail("unexpected exception for cell (" + row + ", " + col + "): " + e);
    }
  }

  private int[] firstEmptyCell() {
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        if (grid.getValue(row, col) == -1) {
          return new int[] {row, col};
        }
      }
    }
    return null;
  }

  private int[] firstGivenCell() {
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        if (grid.getValue(row, col) != -1) {
          return new int[] {row, col};
        }
      }
    }
    return null;
  }

  private List<int[]> emptyCells() {
    List<int[]> cells = new ArrayList<>();
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        if (grid.getValue(row, col) == -1) {
          cells.add(new int[] {row, col});
        }
      }
    }
    return cells;
  }

  private int[][] snapshot() {
    int[][] cells = new int[size][size];
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        cells[row][col] = grid.getValue(row, col);
      }
    }
    return cells;
  }

  /** Returns the only cell differing between two snapshots, failing unless exactly one does. */
  private int[] singleChangedCell(int[][] before, int[][] after) {
    int[] changed = null;
    int count = 0;
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        if (before[row][col] != after[row][col]) {
          changed = new int[] {row, col};
          count++;
        }
      }
    }
    assertEquals("expected exactly one cell to change", 1, count);
    return changed;
  }
}
