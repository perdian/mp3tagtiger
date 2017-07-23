/*
 * Copyright 2014-2017 Christian Robert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.perdian.apps.tagtiger.fx.panels.tools.updatetags;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

class UpdateTagsEditorPane extends VBox {

    UpdateTagsEditorPane(Selection selection, Localization localization) {

        Property<Pattern> regexPatternProperty = new SimpleObjectProperty<>();
        Button regexApplyButton = new Button(localization.apply());
        regexApplyButton.setDisable(true);
        regexApplyButton.setMaxHeight(Double.MAX_VALUE);
        TextField regexField = new TextField();
        GridPane.setHgrow(regexField, Priority.ALWAYS);
        TextArea regexResultArea = new TextArea(localization.noRegularExpressionSetYet());
        regexResultArea.setEditable(false);
        regexResultArea.setFont(Font.font("Monospaced"));
        regexResultArea.setPrefHeight(60d);
        regexResultArea.minHeightProperty().bind(regexResultArea.prefHeightProperty());
        regexResultArea.setFocusTraversable(false);
        GridPane topPane = new GridPane();
        topPane.setHgap(5);
        topPane.setVgap(5);
        topPane.add(new Label(localization.regularExpression()), 0, 0, 2, 1);
        topPane.add(regexField, 0, 1, 1, 1);
        topPane.add(regexApplyButton, 1, 1, 1, 1);
        topPane.add(new Label(localization.compilationResult()), 0, 2, 2, 1);
        topPane.add(regexResultArea, 0, 3, 2, 1);
        TitledPane topTitledPane = new TitledPane(localization.regularExpression(), topPane);
        topTitledPane.setCollapsible(false);

        BorderPane detailsWrapperPane = new BorderPane(new Label(localization.noRegularExpressionSetYet()));
        detailsWrapperPane.setPadding(new Insets(5, 5, 5, 5));
        TitledPane detailsTitledPane = new TitledPane(localization.details(), detailsWrapperPane);
        detailsTitledPane.setCollapsible(false);

        this.getChildren().addAll(topTitledPane, detailsTitledPane);
        this.setSpacing(5);
        this.setPadding(new Insets(5, 5, 5, 5));

        regexField.textProperty().addListener((o, oldValue, newValue) -> {
            try {
                if (newValue == null || newValue.isEmpty()) {
                    throw new PatternSyntaxException(localization.noRegularExpressionSetYet(), newValue, -1);
                } else {
                    Pattern regexPattern = Pattern.compile(newValue);
                    int numberOfGroups = regexPattern.matcher("").groupCount();
                    if (numberOfGroups < 1) {
                        throw new PatternSyntaxException(localization.regularExpressionMustContainAtLeastOneGroup(), newValue, -1);
                    } else {
                        regexPatternProperty.setValue(regexPattern);
                        regexApplyButton.setDisable(false);
                        regexResultArea.setText(localization.regularExpressionValid());
                        regexResultArea.setStyle("-fx-control-inner-background: #00ff00");
                    }
                }
            } catch (PatternSyntaxException e) {
                regexPatternProperty.setValue(null);
                regexApplyButton.setDisable(true);
                regexResultArea.setText(e.getMessage());
                regexResultArea.setStyle("-fx-control-inner-background: #ffaaaa");
            }
        });

        regexPatternProperty.addListener((o, oldValue, newValue) -> {
            if (newValue == null) {
                Platform.runLater(() -> {
                    detailsWrapperPane.getChildren().clear();
                    detailsWrapperPane.setCenter(new Label(localization.noRegularExpressionSetYet()));
                });
            } else {
                UpdateTagsPatternPane patternPane = new UpdateTagsPatternPane(newValue, selection, localization);
                Platform.runLater(() -> {
                    detailsWrapperPane.getChildren().clear();
                    detailsWrapperPane.setCenter(patternPane);
                });
            }
        });

    }

}
