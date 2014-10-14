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
package de.perdian.apps.tagtiger.fx.panels.editor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import de.perdian.apps.tagtiger.business.framework.localization.Localization;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.components.EditorComponentFactory;

public class EditorPane extends VBox {

    private final ObjectProperty<TaggableFile> currentFileProperty = new SimpleObjectProperty<>();

    public EditorPane(Localization localization) {

        EditorComponentFactory<TaggableFile> componentFactory = new EditorComponentFactory<>(this.currentFileProperty());

        EditorInformationPane informationPane = new EditorInformationPane(componentFactory);
        informationPane.setPadding(new Insets(5, 5, 5, 5));
        TitledPane informationWrapperPane = new TitledPane(localization.mp3File(), informationPane);
        informationWrapperPane.setExpanded(true);

        this.setSpacing(5);
        this.setDisable(true);
        this.getChildren().addAll(informationWrapperPane);

        this.currentFileProperty().addListener((c, oldValue, newValue) -> this.setDisable(newValue == null));

    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public ObjectProperty<TaggableFile> currentFileProperty() {
        return this.currentFileProperty;
    }
    public TaggableFile getCurrentFile() {
        return this.currentFileProperty.get();
    }
    public void setCurrentFile(TaggableFile file) {
        this.currentFileProperty.set(file);
    }

}