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
package de.perdian.apps.tagtiger.fx.handlers.batchupdate;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import de.perdian.apps.tagtiger.core.localization.Localization;

class BatchUpdateLegendPane extends GridPane {

    BatchUpdateLegendPane(Localization localization) {

        this.setPadding(new Insets(5, 5, 5, 5));

        int columnCount = 3;
        int currentRow = 0;
        int currentColumn = 0;
        for (BatchUpdatePlaceholder placeholder : BatchUpdatePlaceholder.values()) {

            StringBuilder placeholderText = new StringBuilder();
            placeholderText.append("${").append(placeholder.getPlaceholder()).append("}: ");
            placeholderText.append(placeholder.resolveLocalization(localization));

            Label placeholderLabel = new Label(placeholderText.toString());
            placeholderLabel.setMaxWidth(Double.MAX_VALUE);
            placeholderLabel.setPadding(new Insets(3, 3, 3, 3));
            placeholderLabel.setAlignment(Pos.TOP_LEFT);
            this.add(placeholderLabel, currentColumn, currentRow);
            GridPane.setFillWidth(placeholderLabel, Boolean.TRUE);
            GridPane.setHgrow(placeholderLabel, Priority.ALWAYS);

            currentColumn++;
            if (currentColumn >= columnCount) {
                currentRow++;
                currentColumn = 0;
            }

        }

    }

}