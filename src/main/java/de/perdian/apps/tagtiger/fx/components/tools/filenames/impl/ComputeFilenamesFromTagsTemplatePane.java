package de.perdian.apps.tagtiger.fx.components.tools.filenames.impl;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;

class ComputeFilenamesFromTagsTemplatePane extends TitledPane {

    static final List<String> TEMPLATE_PATTERNS = List.of(
        "${tracknumber} ${title}",
        "${discnumber}.${tracknumber} ${title}"
    );

    ComputeFilenamesFromTagsTemplatePane(StringProperty patternProperty) {

        GridPane patternPane = new GridPane(10, 5);
        for (int rowIndex=0; rowIndex < TEMPLATE_PATTERNS.size(); rowIndex++) {
            String pattern = TEMPLATE_PATTERNS.get(rowIndex);
            Label patternLabel = new Label(pattern);
            Button selectPatternButton = new Button("Use pattern");
            selectPatternButton.setOnAction(event -> patternProperty.setValue(pattern));
            patternPane.add(selectPatternButton, 0, rowIndex, 1, 1);
            patternPane.add(patternLabel, 1, rowIndex, 1, 1);
        }

        this.setContent(patternPane);
        this.setText("Templates");
        this.setExpanded(true);
        this.setCollapsible(false);
        this.setMaxHeight(Double.MAX_VALUE);

    }

}
