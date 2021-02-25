package com.arba.checkers.data;

import java.util.Objects;

import com.arba.checkers.data.dict.ColorType;
import com.arba.checkers.data.dict.PieceType;

public class Piece {
    private PieceType type;
    private ColorType color;

    public Piece() {
    }

    public Piece(ColorType color) {
        this.color = color;
        this.type = PieceType.MAN;
    }

    public Piece(PieceType type, ColorType color) {
        this.type = type;
        this.color = color;
    }

    public PieceType getType() {
        return type;
    }

    public void setType(PieceType type) {
        this.type = type;
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
        Piece piece = (Piece) o;
        return type == piece.type && color == piece.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color);
    }
}
