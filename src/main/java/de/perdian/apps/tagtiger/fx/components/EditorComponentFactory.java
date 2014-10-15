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
package de.perdian.apps.tagtiger.fx.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

/**
 * Abstraction for creating components that are used to edit the content of
 * a bean
 *
 * @author Christian Robert
 */

public class EditorComponentFactory<T> {

    private ObjectProperty<T> beanProperty = null;
    private List<EditorComponentWrapper<T>> componentWrappers = new ArrayList<>();
    private List<Consumer<Control>> controlCustomizers = new ArrayList<>();

    public EditorComponentFactory(ObjectProperty<T> beanProperty) {

        this.setBeanProperty(beanProperty);
        this.setComponentWrappers(new ArrayList<>());

        beanProperty.addListener((o, oldValue, newValue) -> this.handleCurrentBeanUpdate(oldValue, newValue));

    }

    public TextField createTextField(Function<T, StringProperty> propertyFunction) {

        TextField textField = new TextField();
        textField.focusedProperty().addListener((o, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> textField.selectAll());
            }
        });
        this.getControlCustomizers().forEach(consumer -> consumer.accept(textField));

        EditorComponentWrapper<T> componentWrapper = new EditorComponentWrapper<>();
        componentWrapper.setBeanPropertySupplier(propertyFunction);
        componentWrapper.setBeanPropertyChangeListener((o, oldValue, newValue) -> textField.setText(newValue));
        componentWrapper.setControlValueSupplier(() -> textField.textProperty());
        componentWrapper.setControlValueChangeListener((o, oldValue, newValue) -> Optional.ofNullable(this.getBeanProperty().get()).ifPresent(bean -> propertyFunction.apply(bean).set(newValue)));
        this.getComponentWrappers().add(componentWrapper);

        return textField;

    }

    private void handleCurrentBeanUpdate(T oldValue, T newValue) {
        for (EditorComponentWrapper<T> componentWrapper : this.getComponentWrappers()) {
            if (oldValue != null) {
                componentWrapper.getBeanPropertySupplier().apply(oldValue).removeListener(componentWrapper.getBeanPropertyChangeListener());
                componentWrapper.getControlValueSupplier().get().removeListener(componentWrapper.getControlValueChangeListener());
            }
            if (newValue != null) {
                componentWrapper.getBeanPropertySupplier().apply(newValue).addListener(componentWrapper.getBeanPropertyChangeListener());
                componentWrapper.getControlValueSupplier().get().addListener(componentWrapper.getControlValueChangeListener());
                componentWrapper.getBeanPropertyChangeListener().changed(null, null, componentWrapper.getBeanPropertySupplier().apply(newValue).get());
            } else {
                componentWrapper.getBeanPropertyChangeListener().changed(null, null, "");
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

    private List<EditorComponentWrapper<T>> getComponentWrappers() {
        return this.componentWrappers;
    }
    private void setComponentWrappers(List<EditorComponentWrapper<T>> componentWrappers) {
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

}