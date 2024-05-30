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
package de.perdian.apps.tagtiger.fx.components.tools.filenames;

import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class FilenamesToolLegendPane extends TitledPane {

    public FilenamesToolLegendPane(String title, Map<String, String> variables, String prefix, String postfix) {

        GridPane innerPane = new GridPane();
        int columnCount = 3;
        int currentRow = 0;
        int currentColumn = 0;
        for (Map.Entry<String, String> variableEntry : variables.entrySet()) {

            StringBuilder variableText = new StringBuilder();
            variableText.append(prefix).append(variableEntry.getKey()).append(postfix);
            variableText.append(": ");
            variableText.append(variableEntry.getValue());

            Label variableLabel = new Label(variableText.toString());
            variableLabel.setMaxWidth(Double.MAX_VALUE);
            variableLabel.setPadding(new Insets(3, 3, 3, 3));
            variableLabel.setAlignment(Pos.TOP_LEFT);
            GridPane.setFillWidth(variableLabel, Boolean.TRUE);
            GridPane.setHgrow(variableLabel, Priority.ALWAYS);
            innerPane.add(variableLabel, currentColumn, currentRow);

            currentColumn++;
            if (currentColumn >= columnCount) {
                currentRow++;
                currentColumn = 0;
            }

        }

        innerPane.setHgap(2.5);
        innerPane.setVgap(2.5);
        innerPane.setPadding(new Insets(10, 10, 10, 10));

        this.setText(title);
        this.setContent(innerPane);
        this.setCollapsible(false);

    }

}
