/*
 * Copyright 2014-2020 Christian Seifert
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
package de.perdian.apps.tagtiger3.fx.components.editor;

import de.perdian.apps.tagtiger3.model.SongProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

class EditorFilePane extends GridPane {

    EditorFilePane(EditorComponentBuilder componentBuilder) {

        this.add(componentBuilder.createLabel("File name", 0, 5), 0, 0, 1, 1);
        this.add(componentBuilder.createTextField(SongProperty.FILENAME), 1, 0, 1, 1);

        this.getColumnConstraints().add(new ColumnConstraints(75));
        this.getColumnConstraints().add(new ColumnConstraints());
        this.setPadding(new Insets(10, 10, 10, 10));
        this.setHgap(5);
        this.setVgap(5);

    }

}
