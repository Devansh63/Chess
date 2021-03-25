package com.cs360.chess.ui;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 * Represents a clickable transparent tile for the click grid. When opacity is set to 1, a white border shows around the given tile
 */
public class ClickableTile extends Rectangle {

    private final int column;
    private final int row;

    public ClickableTile(int column, int row) {
        super();
        this.column = column;
        this.row = row;

        setFill(Color.TRANSPARENT);
        setStroke(Color.WHITE);
        setStrokeType(StrokeType.INSIDE);
        setStrokeWidth(4);
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }
}
