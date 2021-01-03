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
package de.perdian.apps.tagtiger3.fx.components.actions.batchactions;

import de.perdian.apps.tagtiger3.model.SongProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

class BatchActionLegendPane extends TitledPane {

    BatchActionLegendPane() {

        GridPane innerPane = new GridPane();
        int columnCount = 3;
        int currentRow = 0;
        int currentColumn = 0;
        for (SongProperty songProperty : SongProperty.values()) {
            if (songProperty.isReplaceable()) {

                StringBuilder placeholderText = new StringBuilder();
                placeholderText.append("${").append(songProperty.name().toLowerCase()).append("}: ");
                placeholderText.append(songProperty.getTitle());

                Label placeholderLabel = new Label(placeholderText.toString());
                placeholderLabel.setMaxWidth(Double.MAX_VALUE);
                placeholderLabel.setPadding(new Insets(3, 3, 3, 3));
                placeholderLabel.setAlignment(Pos.TOP_LEFT);
                GridPane.setFillWidth(placeholderLabel, Boolean.TRUE);
                GridPane.setHgrow(placeholderLabel, Priority.ALWAYS);
                innerPane.add(placeholderLabel, currentColumn, currentRow);

                currentColumn++;
                if (currentColumn >= columnCount) {
                    currentRow++;
                    currentColumn = 0;
                }

            }
        }

        innerPane.setHgap(2.5);
        innerPane.setVgap(2.5);
        innerPane.setPadding(new Insets(10, 10, 10, 10));

        this.setText("Legend");
        this.setContent(innerPane);
        this.setCollapsible(false);

    }

}
