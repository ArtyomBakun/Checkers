package com.arba.checkers.rule;

import java.util.List;

import com.arba.checkers.data.GameState;
import com.arba.checkers.data.Point;

public interface GameRules {
    
    GameState getInitialGameState();

    List<Point> possibleManMovesInDirection(GameState state, Point from, boolean left, boolean up);

    List<Point> possibleKingMovesInDirection(GameState state, Point from, boolean left, boolean up);

    List<Point> possibleManJumpsInDirection(GameState state, Point from, boolean left, boolean up);

    List<Point> possibleKingJumpsInDirection(GameState state, Point from, boolean left, boolean up);

    Point getBattleFieldSize();

    boolean isMultiJumpAllowed();
}
