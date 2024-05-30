/*
 * Copyright 2014-2021 Christian Seifert
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
package de.perdian.apps.tagtiger.fx.components.tools.filenames;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

import java.util.Objects;

public class FilenamesToolPatternPane extends TitledPane {

    private StringProperty pattern = new SimpleStringProperty();
    private ObjectProperty<Exception> error = new SimpleObjectProperty<>(null);
    private EventHandler<ActionEvent> onActionEvent = event -> {};

    public FilenamesToolPatternPane() {

        TextField patternField = new TextField();
        this.patternProperty().addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(newValue, patternField.getText())) {
                patternField.setText(newValue);
            }
        });
        patternField.textProperty().addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(newValue, this.patternProperty().getValue())) {
                this.patternProperty().setValue(newValue);
            }
        });

        Label errorLabel = new Label();
        errorLabel.textFillProperty().setValue(Color.RED);
        this.errorProperty().addListener((o, oldValue, newValue) -> {
            if (newValue == null) {
                errorLabel.textProperty().setValue("");
            } else {
                StringBuilder cleanValue = new StringBuilder();
                for (char newValueChar : newValue.toString().toCharArray()) {
                    if (Character.isWhitespace(newValueChar)) {
                        cleanValue.append(" ");
                    } else {
                        cleanValue.append(newValueChar);
                    }
                }
                errorLabel.textProperty().setValue(cleanValue.toString());
            }
        });

        GridPane.setHgrow(patternField, Priority.ALWAYS);
        GridPane.setFillHeight(patternField, Boolean.TRUE);
        Button executeButton = new Button("Execute", new FontAwesomeIconView(FontAwesomeIcon.PLAY));
        executeButton.setOnAction(event -> this.getOnActionEvent().handle(event));
        executeButton.disableProperty().bind(patternField.textProperty().isEmpty().or(errorProperty().isNotNull()));
        GridPane.setFillHeight(executeButton, Boolean.TRUE);

        GridPane filenamePatternPane = new GridPane();
        filenamePatternPane.add(patternField, 0, 0, 1, 1);
        filenamePatternPane.add(errorLabel, 0, 1, 1, 1);
        filenamePatternPane.add(executeButton, 1, 0, 1, 1);
        filenamePatternPane.setHgap(5);
        filenamePatternPane.setPadding(new Insets(10, 10, 10, 10));
        this.setContent(filenamePatternPane);

        this.setText("File name pattern");
        this.setCollapsible(false);

    }

    public StringProperty patternProperty() {
        return this.pattern;
    }
    public ObjectProperty<Exception> errorProperty() {
        return this.error;
    }

    public EventHandler<ActionEvent> getOnActionEvent() {
        return this.onActionEvent;
    }
    public void setOnActionEvent(EventHandler<ActionEvent> onActionEvent) {
        this.onActionEvent = onActionEvent;
    }

}
