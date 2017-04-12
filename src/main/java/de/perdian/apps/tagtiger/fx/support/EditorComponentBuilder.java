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
package de.perdian.apps.tagtiger.fx.support;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class EditorComponentBuilder<T> {

    private Control control = null;
    private Property<String> editProperty = null;
    private Property<Boolean> disabledProperty = null;
    private List<Consumer<Control>> controlCustomizers = null;

    EditorComponentBuilder(Property<String> editProperty) {
        this.setEditProperty(editProperty);
    }

    public EditorComponentBuilder<T> useTextField() {
        return this.useTextField(null);
    }

    public EditorComponentBuilder<T> useTextField(Consumer<TextField> textFieldConsumer) {
        TextField textField = new TextField(this.getEditProperty().getValue());
        textField.setMinWidth(50);
        textField.setPrefWidth(0);
        textField.focusedProperty().addListener((o, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> textField.selectAll());
            }
        });
        textField.disableProperty().bind(this.getDisabledProperty());
        textField.textProperty().addListener((o, oldValue, newValue) -> this.getEditProperty().setValue(newValue));
        this.getEditProperty().addListener((o, oldValue, newValue) -> {
            textField.setText(newValue == null ? null : newValue.toString());
            if (textField.focusedProperty().get()) {
                textField.selectAll();
            }
        });
        this.setControl(textField);
        Optional.ofNullable(textFieldConsumer).ifPresent(consumer -> consumer.accept(textField));
        return this;
    }

    public Parent build() {
        if (this.getControl() == null) {
            throw new IllegalArgumentException("Control has not been initialized for this component builder");
        } else {
            this.getControlCustomizers().forEach(customer -> customer.accept(this.getControl()));
            HBox.setHgrow(this.getControl(), Priority.SOMETIMES);
            HBox componentWrapper = new HBox();
            componentWrapper.getChildren().add(this.getControl());
            return componentWrapper;
        }
    }

    private Control getControl() {
        return this.control;
    }
    private void setControl(Control control) {
        this.control = control;
    }

    private Property<String> getEditProperty() {
        return this.editProperty;
    }
    private void setEditProperty(Property<String> editProperty) {
        this.editProperty = editProperty;
    }

    Property<Boolean> getDisabledProperty() {
        return this.disabledProperty;
    }
    void setDisabledProperty(Property<Boolean> disabledProperty) {
        this.disabledProperty = disabledProperty;
    }

    List<Consumer<Control>> getControlCustomizers() {
        return this.controlCustomizers;
    }
    void setControlCustomizers(List<Consumer<Control>> controlCustomizers) {
        this.controlCustomizers = controlCustomizers;
    }

}
