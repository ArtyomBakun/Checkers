package com.arba.checkers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.arba.checkers.data.Cell;
import com.arba.checkers.data.GameState;
import com.arba.checkers.data.Piece;
import com.arba.checkers.data.Point;
import com.arba.checkers.data.dict.MoveResult;
import com.arba.checkers.data.dict.MoveType;
import com.arba.checkers.rule.GameRules;

import static com.arba.checkers.data.dict.ColorType.DARK;
import static com.arba.checkers.data.dict.ColorType.LIGHT;
import static com.arba.checkers.data.dict.MoveResult.MUST_BE_CONTINUED;
import static com.arba.checkers.data.dict.MoveResult.OPPONENT_WON;
import static com.arba.checkers.data.dict.MoveResult.TRANSFERRED;
import static com.arba.checkers.data.dict.MoveType.JUMP;
import static com.arba.checkers.data.dict.MoveType.MOVE;
import static com.arba.checkers.data.dict.PieceType.KING;
import static com.arba.checkers.data.dict.PieceType.MAN;
import static com.arba.checkers.util.BattleFieldUtil.*;

public class Game {
    private GameRules rules;
    
    private GameState state;

    public Game(GameRules rule) {
        this.rules = rule;
        this.state = rule.getInitialGameState();
    }

    public Map<Point, MoveType> getPossibleMoves(Point from) {
        Piece piece = getCell(state.getBattleField(), from).getPiece();
        Map<Point, MoveType> availableMoves = new HashMap<>();
        if (piece != null && piece.getColor() == state.getCurrentPlayer()) {
            Stream.of(0, 1, 2, 3).forEach(i -> {
                boolean left = i / 2 == 0;
                boolean up = i % 2 == 0;
                if (piece.getType() == MAN) {
                    rules.possibleManMovesInDirection(state, from, left, up).forEach(move -> availableMoves.put(move, MOVE));
                    rules.possibleManJumpsInDirection(state, from, left, up).forEach(move -> availableMoves.put(move, JUMP));
                } else {
                    rules.possibleKingMovesInDirection(state, from, left, up).forEach(move -> availableMoves.put(move, MOVE));
                    rules.possibleKingJumpsInDirection(state, from, left, up).forEach(move -> availableMoves.put(move, JUMP));
                }
            });
        }
        return availableMoves;
    }

    public boolean canMove(Point from, Point to) {
        if (getCell(state.getBattleField(), from).getPiece() == null
                || getCell(state.getBattleField(), from).getPiece().getColor() != state.getCurrentPlayer()) {
            return false;
//            throw new IllegalArgumentException("Your piece is not found in current cell!");
        }
        Map<Point, MoveType> possibleMoves = getPossibleMoves(from);
        if (!possibleMoves.containsKey(to)) {
            return false;
//            throw new IllegalArgumentException("Couldn't go to selected point!");
        }
        if (possibleMoves.get(to) == MOVE && hasJumps()) {
            return false;
//            throw new IllegalArgumentException("You have other mandatory move!");
        }
        return true;
    }

    public MoveResult move(Point from, Point to) {
        if (!canMove(from, to)) {
            throw new IllegalArgumentException("Couldn't go to selected point!");
        }
        Map<Point, MoveType> possibleMoves = getPossibleMoves(from);
        boolean isEnemyPieceEaten = possibleMoves.getOrDefault(to, MOVE) == JUMP;

        doMove(from, to, isEnemyPieceEaten);

        if (rules.isMultiJumpAllowed() && isEnemyPieceEaten && getPossibleMoves(to).containsValue(MoveType.JUMP)) {
            return MUST_BE_CONTINUED;
        }
        changePlayer();
        if (hasAnyValidMove()) {
            return TRANSFERRED;
        } else {
            return OPPONENT_WON;
        }
    }

    private void doMove(Point from, Point to, boolean isEnemyPieceEaten) {
        if (isEnemyPieceEaten) {//delete eaten piece
            for (Cell cell : getCellsByDiagonal(state.getBattleField(), from, from.getY() > to.getY(), from.getX() > to.getX())) {
                if (cell.getLocation().equals(to)) {
                    break;
                }
                cell.setPiece(null);
            }
        }
        getCell(state.getBattleField(), to).setPiece(getCell(state.getBattleField(), from).getPiece());
        getCell(state.getBattleField(), from).setPiece(null);
        //convert to king if needed
        if (to.getX() == (state.isForwardIsUp() ? 0 : (rules.getBattleFieldSize().getX() - 1))) {
            getCell(state.getBattleField(), to).getPiece().setType(KING);
        }
    }

    private boolean hasAnyValidMove() {
        return state.getBattleField().stream().flatMap(List::stream)
                .filter(cell -> cell.getPiece() != null && cell.getPiece().getColor() == state.getCurrentPlayer())
                .anyMatch(cell -> !getPossibleMoves(cell.getLocation()).isEmpty());
    }

    private boolean hasJumps() {
        return state.getBattleField().stream().flatMap(List::stream)
                .filter(cell -> cell.getPiece() != null && cell.getPiece().getColor() == state.getCurrentPlayer())
                .anyMatch(cell -> getPossibleMoves(cell.getLocation()).containsValue(MoveType.JUMP));
    }

    private void changePlayer() {
        state.setCurrentPlayer(state.getCurrentPlayer() == LIGHT ? DARK : LIGHT);
        state.setForwardIsUp(!state.isForwardIsUp());
    }

    public GameState getState() {
        return state;
    }

    public GameRules getRule() {
        return rules;
    }
}
