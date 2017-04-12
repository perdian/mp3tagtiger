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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Control;

public class EditorComponentBuilderFactory<T> {

    private Property<T> mainObjectProperty = null;
    private Property<Boolean> disabledProperty = null;
    private List<Consumer<Control>> controlCustomizers = null;

    public EditorComponentBuilderFactory(Property<T> mainObjectProperty) {
        this.setMainObjectProperty(mainObjectProperty);
        this.setDisabledProperty(new SimpleBooleanProperty());
        this.setControlCustomizers(new ArrayList<>());
    }

    public EditorComponentBuilder<T> componentBuilder(Function<T, Property<String>> propertyResolver) {
        EditorComponentBuilder<T> componentBuilder = new EditorComponentBuilder<>(this.createEditProperty(propertyResolver));
        componentBuilder.setDisabledProperty(this.getDisabledProperty());
        componentBuilder.setControlCustomizers(this.getControlCustomizers());
        return componentBuilder;
    }

    private Property<String> createEditProperty(Function<T, Property<String>> propertyResolver) {

        Property<String> editProperty = new SimpleStringProperty();
        editProperty.addListener((o, oldValue, newValue) -> {
            T mainObject = this.getMainObjectProperty().getValue();
            if (!Objects.equals(oldValue, newValue) && mainObject != null) {
                propertyResolver.apply(mainObject).setValue(newValue);
            }
        });

        ChangeListener<String> updateEditPropertyChangeListener = (o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                editProperty.setValue(newValue);
            }
        };
        this.getMainObjectProperty().addListener((o, oldMainObject, newMainObject) -> {
           if (!Objects.equals(oldMainObject, newMainObject)) {
               if (oldMainObject != null) {
                   propertyResolver.apply(oldMainObject).removeListener(updateEditPropertyChangeListener);
               }
               if (newMainObject != null) {
                   Property<String> newMainObjectProperty = propertyResolver.apply(newMainObject);
                   editProperty.setValue(newMainObjectProperty.getValue());
                   newMainObjectProperty.addListener(updateEditPropertyChangeListener);
               } else {
                   editProperty.setValue(null);
               }
           }
        });
        return editProperty;

    }

    private Property<T> getMainObjectProperty() {
        return this.mainObjectProperty;
    }
    private void setMainObjectProperty(Property<T> mainObjectProperty) {
        this.mainObjectProperty = mainObjectProperty;
    }

    public void addControlCustomizer(Consumer<Control> controlCustomizer) {
        this.getControlCustomizers().add(controlCustomizer);
    }
    private List<Consumer<Control>> getControlCustomizers() {
        return this.controlCustomizers;
    }
    private void setControlCustomizers(List<Consumer<Control>> controlCustomizers) {
        this.controlCustomizers = controlCustomizers;
    }

    public Property<Boolean> getDisabledProperty() {
        return this.disabledProperty;
    }
    public void setDisabledProperty(Property<Boolean> disabledProperty) {
        this.disabledProperty = disabledProperty;
    }

}
