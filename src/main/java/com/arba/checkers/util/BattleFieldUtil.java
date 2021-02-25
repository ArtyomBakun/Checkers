package com.arba.checkers.util;

import java.util.ArrayList;
import java.util.List;

import com.arba.checkers.data.Cell;
import com.arba.checkers.data.Point;
import com.arba.checkers.data.dict.ColorType;

public class BattleFieldUtil {

    /**
     * Trace routes in four directions starting from provided point<p/>
     * \    up   /
     *  \       /
     *   \     /
     *    \   /
     *     \ /
     *left  x  right
     *     / \
     *    /   \
     *   /     \
     *  /       \
     * /   down  \
     */
    public static List<Cell> getCellsByDiagonal(List<List<Cell>> battleField, Point from, boolean left, boolean up) {
        List<Cell> result = new ArrayList<>();
        Point next = nextDiagonalPoint(from, left, up);
        while (hasCell(battleField, next)) {
            result.add(getCell(battleField, next));
            next = nextDiagonalPoint(next, left, up);
        }
        return result;
    }

    private static Point nextDiagonalPoint(Point from, boolean left, boolean up) {
        return new Point(
                from.getX() + (up ? -1 : 1),
                from.getY() + (left ? -1 : 1)
        );
    }

    private static boolean hasCell(List<List<Cell>> battleField, Point location) {
        return location.getX() > -1 && location.getY() > -1
                && location.getX() < battleField.size()
                && location.getY() < battleField.get(location.getX()).size();
    }

    public static Cell getCell(List<List<Cell>> battleField, Point location) {
        return battleField.get(location.getX()).get(location.getY());
    }

    public static boolean couldJumpOver(List<Cell> diagonal, int location, ColorType currentPlayer) {
        return diagonal.size() > (location + 1)
                && diagonal.get(location).getPiece() != null
                && diagonal.get(location).getPiece().getColor() != currentPlayer
                && diagonal.get(location + 1).getPiece() == null;
    }
}
