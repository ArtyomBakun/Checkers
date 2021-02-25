package com.arba.checkers.data;

import java.util.Objects;

import com.arba.checkers.data.dict.ColorType;

public class Cell {
    private Point location;
    private Piece piece;
    private ColorType color;

    public Cell() {
    }

    public Cell(Point location, Piece piece, ColorType color) {
        this.location = location;
        this.piece = piece;
        this.color = color;
    }

    public Point getLocation() {
        return location;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public ColorType getColor() {
        return color;
    }

    public void setColor(ColorType color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return Objects.equals(piece, cell.piece) && color == cell.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece, color);
    }
}
