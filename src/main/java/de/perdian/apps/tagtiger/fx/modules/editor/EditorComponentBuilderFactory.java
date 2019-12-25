/*
 * Copyright 2014-2017 Christian Seifert
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
package de.perdian.apps.tagtiger.fx.modules.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Control;

public class EditorComponentBuilderFactory {

    private Property<TaggableFile> mainObjectProperty = null;
    private Property<Boolean> disabledProperty = null;
    private List<Consumer<Control>> controlCustomizers = null;
    private Selection selection = null;
    private Localization localization = null;

    public EditorComponentBuilderFactory(Property<TaggableFile> mainObjectProperty, Selection selection, Localization localization) {

        Property<Boolean> disabledProperty = new SimpleBooleanProperty(true);
        mainObjectProperty.addListener((o, oldValue, newValue) -> disabledProperty.setValue(newValue == null));

        this.setMainObjectProperty(mainObjectProperty);
        this.setSelection(selection);
        this.setLocalization(localization);
        this.setDisabledProperty(disabledProperty);
        this.setControlCustomizers(new ArrayList<>());

    }

    public EditorComponentBuilder componentBuilder(Function<TaggableFile, Property<String>> propertyResolver) {
        EditorComponentBuilder componentBuilder = new EditorComponentBuilder(this.createEditProperty(propertyResolver));
        componentBuilder.setPropertyResolver(propertyResolver);
        componentBuilder.setDisabledProperty(this.getDisabledProperty());
        componentBuilder.setControlCustomizers(this.getControlCustomizers());
        componentBuilder.setSelection(this.getSelection());
        componentBuilder.setLocalization(this.getLocalization());
        return componentBuilder;
    }

    private Property<String> createEditProperty(Function<TaggableFile, Property<String>> propertyResolver) {

        Property<String> editProperty = new SimpleStringProperty();
        editProperty.addListener((o, oldValue, newValue) -> {
            TaggableFile mainObject = this.getMainObjectProperty().getValue();
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

    private Property<TaggableFile> getMainObjectProperty() {
        return this.mainObjectProperty;
    }
    private void setMainObjectProperty(Property<TaggableFile> mainObjectProperty) {
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
    private void setDisabledProperty(Property<Boolean> disabledProperty) {
        this.disabledProperty = disabledProperty;
    }

    private Selection getSelection() {
        return this.selection;
    }
    private void setSelection(Selection selection) {
        this.selection = selection;
    }

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

}
