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
package de.perdian.apps.tagtiger.fx.panels.tools.updatefilenames;

import de.perdian.apps.tagtiger.fx.localization.Localization;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

class UpdateFileNamesLegendPane extends BorderPane {

    UpdateFileNamesLegendPane(Localization localization) {

        GridPane legendPane = new GridPane();
        legendPane.setPadding(new Insets(5, 5, 5, 5));
        int columnCount = 3;
        int currentRow = 0;
        int currentColumn = 0;
        for (UpdateFileNamesPlaceholder placeholder : UpdateFileNamesPlaceholder.values()) {

            StringBuilder placeholderText = new StringBuilder();
            placeholderText.append("${").append(placeholder.getPlaceholder()).append("}: ");
            placeholderText.append(placeholder.resolveLocalization(localization));

            Label placeholderLabel = new Label(placeholderText.toString());
            placeholderLabel.setMaxWidth(Double.MAX_VALUE);
            placeholderLabel.setPadding(new Insets(3, 3, 3, 3));
            placeholderLabel.setAlignment(Pos.TOP_LEFT);
            legendPane.add(placeholderLabel, currentColumn, currentRow);
            GridPane.setFillWidth(placeholderLabel, Boolean.TRUE);
            GridPane.setHgrow(placeholderLabel, Priority.ALWAYS);

            currentColumn++;
            if (currentColumn >= columnCount) {
                currentRow++;
                currentColumn = 0;
            }

        }

        TitledPane titlePane = new TitledPane(localization.legend(), legendPane);
        titlePane.setCollapsible(false);
        this.setCenter(titlePane);

    }

}
