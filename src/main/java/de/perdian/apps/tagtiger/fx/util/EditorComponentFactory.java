/*
 * Copyright 2014 Christian Robert
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
package de.perdian.apps.tagtiger.fx.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Abstraction for creating components that are used to edit the content of
 * a bean
 *
 * @author Christian Robert
 */

public class EditorComponentFactory<T> {

    private ObjectProperty<T> beanProperty = null;
    private List<EditorComponentWrapper<T, ?>> componentWrappers = new ArrayList<>();
    private List<Consumer<Control>> controlCustomizers = new ArrayList<>();
    private List<Control> createdControls = new ArrayList<>();

    public EditorComponentFactory(ObjectProperty<T> beanProperty) {

        this.setBeanProperty(beanProperty);
        this.setComponentWrappers(new ArrayList<>());
        this.setCreatedControls(new ArrayList<>());
        this.getControlCustomizers().add(this::customizeControlForUpAndDown);

        beanProperty.addListener((o, oldValue, newValue) -> this.handleCurrentBeanUpdate(oldValue, newValue));

    }

    public TextField createNumericTextField(Function<T, Property<String>> propertyFunction) {
        TextField textField = this.createTextField(propertyFunction);
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("\\d+")) {
                event.consume();
            }
        });
        return textField;
    }

    public TextField createTextField(Function<T, Property<String>> propertyFunction) {

        TextField textField = new TextField();
        textField.focusedProperty().addListener((o, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> textField.selectAll());
            }
        });
        textField.setPrefWidth(0);
        textField.textProperty().addListener((o, oldValue, newValue) -> Optional.ofNullable(this.getBeanProperty().get()).ifPresent(bean -> propertyFunction.apply(bean).setValue(newValue)));
        this.getControlCustomizers().forEach(consumer -> consumer.accept(textField));

        EditorComponentWrapper<T, String> componentWrapper = new EditorComponentWrapper<>();
        componentWrapper.setBeanPropertySupplier(propertyFunction);
        componentWrapper.setBeanPropertyChangeListener((o, oldValue, newValue) -> {
            textField.setText(newValue == null ? null : newValue.toString());
            if (textField.focusedProperty().get()) {
                textField.selectAll();
            }
        });
        this.getComponentWrappers().add(componentWrapper);

        this.getCreatedControls().add(textField);
        return textField;

    }

    public ComboBox<String> createSelectBox(Function<T, Property<String>> propertyFunction, List<String> values) {

        ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList(values));
        comboBox.setPrefWidth(0);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        comboBox.setEditable(true);
        comboBox.valueProperty().addListener((o, oldValue, newValue) -> Optional.ofNullable(this.getBeanProperty().get()).ifPresent(bean -> propertyFunction.apply(bean).setValue(newValue)));
        comboBox.focusedProperty().addListener((o, oldValue, newValue) -> {
            Platform.runLater(() -> comboBox.getEditor().selectAll());
        });
        this.getControlCustomizers().forEach(consumer -> consumer.accept(comboBox));

        EditorComponentWrapper<T, String> componentWrapper = new EditorComponentWrapper<>();
        componentWrapper.setBeanPropertySupplier(propertyFunction);
        componentWrapper.setBeanPropertyChangeListener((o, oldValue, newValue) -> {
            comboBox.setValue(newValue == null ? null : newValue.toString());
        });
        this.getComponentWrappers().add(componentWrapper);

        this.getCreatedControls().add(comboBox);
        return comboBox;

    }

    @SuppressWarnings("unchecked")
    private void handleCurrentBeanUpdate(T oldValue, T newValue) {
        for (EditorComponentWrapper<T, ?> wrapper : this.getComponentWrappers()) {
            if (oldValue != null) {
                ((Property<Object>)wrapper.getBeanPropertySupplier().apply(oldValue)).removeListener((ChangeListener<Object>)wrapper.getBeanPropertyChangeListener());
            }
            if (newValue != null) {
                ((Property<Object>)wrapper.getBeanPropertySupplier().apply(newValue)).addListener((ChangeListener<Object>)wrapper.getBeanPropertyChangeListener());
                ((ChangeListener<Object>)wrapper.getBeanPropertyChangeListener()).changed(null, null, wrapper.getBeanPropertySupplier().apply(newValue).getValue());
            } else {
                ((ChangeListener<Object>)wrapper.getBeanPropertyChangeListener()).changed(null, null, null);
            }
        }
    }

    private void customizeControlForUpAndDown(Control control) {
        control.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.UP) {
                this.handleControlUpOrDown(control, -1, event.isShiftDown());
            } else if (event.getCode() == KeyCode.DOWN) {
                this.handleControlUpOrDown(control, 1, event.isShiftDown());
            }
        });
    }

    private void handleControlUpOrDown(Control control, int direction, boolean absolute) {
        if (absolute) {
            this.getCreatedControls().get(direction < 0 ? 0 : this.getCreatedControls().size() - 1).requestFocus();
        } else {
            int currentIndex = this.getCreatedControls().indexOf(control);
            if (currentIndex > -1) {
                if (direction < 0) {
                    this.getCreatedControls().get(Math.max(0, currentIndex - 1)).requestFocus();
                } else {
                    this.getCreatedControls().get(Math.min(this.getCreatedControls().size() - 1, currentIndex + 1)).requestFocus();
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    private ObjectProperty<T> getBeanProperty() {
        return this.beanProperty;
    }
    private void setBeanProperty(ObjectProperty<T> beanProperty) {
        this.beanProperty = beanProperty;
    }

    private List<EditorComponentWrapper<T, ?>> getComponentWrappers() {
        return this.componentWrappers;
    }
    private void setComponentWrappers(List<EditorComponentWrapper<T, ?>> componentWrappers) {
        this.componentWrappers = componentWrappers;
    }

    public void addControlCustomizer(Consumer<Control> customizer) {
        this.getControlCustomizers().add(customizer);
    }
    public List<Consumer<Control>> getControlCustomizers() {
        return this.controlCustomizers;
    }
    public void setControlCustomizers(List<Consumer<Control>> controlCustomizers) {
        this.controlCustomizers = controlCustomizers;
    }

    private List<Control> getCreatedControls() {
        return this.createdControls;
    }
    private void setCreatedControls(List<Control> createdControls) {
        this.createdControls = createdControls;
    }

}