package com.arba.checkers.data;

import java.util.List;

import com.arba.checkers.data.dict.ColorType;

public class GameState {

    private List<List<Cell>> battleField;

    private ColorType currentPlayer;

    private boolean forwardIsUp;

    public GameState(List<List<Cell>> battleField, ColorType currentPlayer, boolean forwardIsUp) {
        this.battleField = battleField;
        this.currentPlayer = currentPlayer;
        this.forwardIsUp = forwardIsUp;
    }

    public List<List<Cell>> getBattleField() {
        return battleField;
    }

    public void setBattleField(List<List<Cell>> battleField) {
        this.battleField = battleField;
    }

    public ColorType getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(ColorType currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public boolean isForwardIsUp() {
        return forwardIsUp;
    }

    public void setForwardIsUp(boolean forwardIsUp) {
        this.forwardIsUp = forwardIsUp;
    }
}
