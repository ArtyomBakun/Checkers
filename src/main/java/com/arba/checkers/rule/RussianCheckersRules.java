package com.arba.checkers.rule;

import java.util.ArrayList;
import java.util.List;

import com.arba.checkers.data.Cell;
import com.arba.checkers.data.GameState;
import com.arba.checkers.data.Piece;
import com.arba.checkers.data.Point;

import static com.arba.checkers.data.dict.ColorType.DARK;
import static com.arba.checkers.data.dict.ColorType.LIGHT;
import static com.arba.checkers.util.BattleFieldUtil.couldJumpOver;
import static com.arba.checkers.util.BattleFieldUtil.getCellsByDiagonal;

public class RussianCheckersRules implements GameRules {

    private static final Point battleFieldSize = new Point(8, 8);

    @Override
    public GameState getInitialGameState() {
        return new GameState(generateStartupBattleField(), LIGHT, true);
    }

    private List<List<Cell>> generateStartupBattleField() {
        List<List<Cell>> board = new ArrayList<>();
        //generate empty board
        for (int i = 0; i < getBattleFieldSize().getX(); i++) {
            List<Cell> row = new ArrayList<>();
            for (int j = 0; j < getBattleFieldSize().getY(); j++) {
                row.add(new Cell(new Point(i, j), null, (i + j) % 2 != 0 ? LIGHT : DARK));
            }
            board.add(row);
        }
        //fill board with pieces
        for (int i = 0; i < getBattleFieldSize().getX(); i++) {
            for (int j = 0; j < getBattleFieldSize().getY(); j++) {
                Cell cell = board.get(i).get(j);
                if (cell.getColor() == DARK) {
                    cell.setPiece(i < 3 ? new Piece(DARK) : i > 4 ? new Piece(LIGHT) : null);
                }
            }
        }
        return board;
    }

    @Override
    public List<Point> possibleManMovesInDirection(GameState state, Point from, boolean left, boolean up) {
        List<Cell> diagonal = getCellsByDiagonal(state.getBattleField(), from, left, up);
        if (up != state.isForwardIsUp() || diagonal.isEmpty() || diagonal.get(0).getPiece() != null) {
            return List.of();
        } else {
            return List.of(diagonal.get(0).getLocation());
        }
    }

    @Override
    public List<Point> possibleKingMovesInDirection(GameState state, Point from, boolean left, boolean up) {
        List<Cell> diagonal = getCellsByDiagonal(state.getBattleField(), from, left, up);
        List<Point> possibleMoves = new ArrayList<>();
        for (Cell cell : diagonal) {
            if (cell.getPiece() != null) {
                break;
            }
            possibleMoves.add(cell.getLocation());
        }
        return possibleMoves;
    }

    @Override
    public List<Point> possibleManJumpsInDirection(GameState state, Point from, boolean left, boolean up) {
        List<Cell> diagonal = getCellsByDiagonal(state.getBattleField(), from, left, up);
        if (couldJumpOver(diagonal, 0, state.getCurrentPlayer())) {
            return List.of(diagonal.get(1).getLocation());
        } else {
            return List.of();
        }
    }

    @Override
    public List<Point> possibleKingJumpsInDirection(GameState state, Point from, boolean left, boolean up) {
        List<Cell> diagonal = getCellsByDiagonal(state.getBattleField(), from, left, up);
        List<Point> possibleJumps = new ArrayList<>();
        for (int i = 0; i < diagonal.size(); i++) {
            if (diagonal.get(i).getPiece() == null) {
                continue;
            }
            if (couldJumpOver(diagonal, i, state.getCurrentPlayer())) {
                for (int j = i + 1; j < diagonal.size(); j++) {
                    Cell cell = diagonal.get(j);
                    if (cell.getPiece() != null) {
                        break;
                    }
                    possibleJumps.add(cell.getLocation());
                }
            }
            break;
        }
        return possibleJumps;
    }

    @Override
    public Point getBattleFieldSize() {
        return battleFieldSize;
    }

    @Override
    public boolean isMultiJumpAllowed() {
        return true;
    }
}
