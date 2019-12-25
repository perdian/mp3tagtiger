/*
 * Copyright 2014-2018 Christian Seifert
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
package de.perdian.apps.tagtiger.fx.modules.tools.updatetags.components;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.perdian.apps.tagtiger.fx.localization.Localization;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

public class RegularExpressionInputPane extends GridPane {

    public RegularExpressionInputPane(Property<Pattern> patternProperty, Localization localization) {

        Property<Pattern> internalPatternProperty = new SimpleObjectProperty<>();
        patternProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(internalPatternProperty.getValue(), newValue)) {
                internalPatternProperty.setValue(newValue);
            }
        });

        Button regexApplyButton = new Button(localization.apply());
        regexApplyButton.setOnAction(event -> patternProperty.setValue(internalPatternProperty.getValue()));
        regexApplyButton.setDisable(true);
        regexApplyButton.setMaxHeight(Double.MAX_VALUE);

        TextField regexPatternField = new TextField();
        regexPatternField.setMaxWidth(Double.MAX_VALUE);
        regexPatternField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                regexApplyButton.fire();
            }
        });
        GridPane.setHgrow(regexPatternField, Priority.ALWAYS);
        internalPatternProperty.addListener((o, oldValue, newValue) -> {
            if (newValue != null) {
                regexPatternField.setText(newValue.toString());
            }
        });

        TextArea regexResultArea = new TextArea(localization.noRegularExpressionSetYet());
        regexResultArea.setEditable(false);
        regexResultArea.setFont(Font.font("Monospaced"));
        regexResultArea.setPrefHeight(60d);
        regexResultArea.minHeightProperty().bind(regexResultArea.prefHeightProperty());
        regexResultArea.setFocusTraversable(false);

        this.setHgap(5);
        this.setVgap(5);
        this.add(new Label(localization.regularExpressionPattern()), 0, 0, 2, 1);
        this.add(regexPatternField, 0, 1, 1, 1);
        this.add(regexApplyButton, 1, 1, 1, 1);
        this.add(new Label(localization.compilationResult()), 0, 2, 2, 1);
        this.add(regexResultArea, 0, 3, 2, 1);

        regexPatternField.textProperty().addListener((o, oldValue, newValue) -> {
            try {
                if (newValue == null || newValue.isEmpty()) {
                    throw new PatternSyntaxException(localization.noRegularExpressionSetYet(), newValue, -1);
                } else {
                    Pattern regexPattern = Pattern.compile(newValue);
                    int numberOfGroups = regexPattern.matcher("").groupCount();
                    if (numberOfGroups < 1) {
                        throw new PatternSyntaxException(localization.regularExpressionMustContainAtLeastOneGroup(), newValue, -1);
                    } else {
                        internalPatternProperty.setValue(regexPattern);
                        regexApplyButton.setDisable(false);
                        regexResultArea.setText(localization.regularExpressionValid());
                        regexResultArea.setStyle("-fx-control-inner-background: #00ff00");
                    }
                }
            } catch (PatternSyntaxException e) {
                internalPatternProperty.setValue(null);
                regexApplyButton.setDisable(true);
                regexResultArea.setText(e.getMessage());
                regexResultArea.setStyle("-fx-control-inner-background: #ffaaaa");
            }
        });

    }

}
