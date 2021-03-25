package com.cs360.chess.ui;

import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.scene.control.skin.ButtonSkin;
import javafx.util.Duration;

public class HoverSkin extends ButtonSkin {

    public HoverSkin(Button control) {
        super(control);

        ScaleTransition scale = new ScaleTransition(Duration.millis(100));
        scale.setNode(control);
        scale.setToX(1.02f);
        scale.setToY(1.02f);
        scale.setCycleCount(1);
        scale.setAutoReverse(true);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100));
        scaleDown.setNode(control);
        scaleDown.setToX(1);
        scaleDown.setToY(1);
        scaleDown.setCycleCount(1);
        scaleDown.setAutoReverse(true);

        control.setOnMouseEntered(e -> scale.play());
        control.setOnMouseExited(e -> scaleDown.play());

    }
}
