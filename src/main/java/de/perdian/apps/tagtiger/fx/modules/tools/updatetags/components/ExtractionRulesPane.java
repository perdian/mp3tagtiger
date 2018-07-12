/*
 * Copyright 2014-2018 Christian Robert
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
package de.perdian.apps.tagtiger.fx.modules.tools.updatetags.components;

import java.util.List;

import de.perdian.apps.tagtiger.core.tagging.TaggablePropertyKey;
import de.perdian.apps.tagtiger.core.tagging.TaggablePropertyKeyWrapper;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import de.perdian.apps.tagtiger.fx.modules.tools.updatetags.model.ExtractionRule;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;

public class ExtractionRulesPane extends BorderPane {

    public ExtractionRulesPane(ObservableList<ExtractionRule> extractionRules, Localization localization) {

        Label initialTitleLabel = new Label(localization.regularExpressionNotEvaluatedYet());
        initialTitleLabel.setTextAlignment(TextAlignment.CENTER);
        initialTitleLabel.setWrapText(true);
        initialTitleLabel.setAlignment(Pos.CENTER);
        this.setCenter(initialTitleLabel);

        extractionRules.addListener((Change<? extends ExtractionRule> change) -> {
            List<? extends ExtractionRule> changeItems = change.getList();
            if (changeItems.isEmpty()) {
                this.getChildren().clear();
                this.setCenter(new Label(localization.regularExpressionMustContainAtLeastOneGroup()));
            } else {

                List<TaggablePropertyKey> propertyKeys = List.of(TaggablePropertyKey.values());
                ObservableList<TaggablePropertyKeyWrapper> propertyKeyWrappers = FXCollections.observableArrayList(TaggablePropertyKeyWrapper.of(propertyKeys, localization));

                GridPane gridPane = new GridPane();
                gridPane.setHgap(10);
                gridPane.setVgap(5);
                for (int i=0; i < changeItems.size(); i++) {

                    ExtractionRule extractionRule = changeItems.get(i);
                    Label titleLabel = new Label(localization.group() + " " + (i+1));
                    ComboBox<TaggablePropertyKeyWrapper> propertyKeyBox = new ComboBox<>(propertyKeyWrappers);
                    propertyKeyBox.getSelectionModel().select(propertyKeys.indexOf(extractionRule.getTargetPropertyKey().getValue()) + 1);
                    propertyKeyBox.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> extractionRule.getTargetPropertyKey().setValue(newValue.getKey()));
                    propertyKeyBox.setMaxWidth(Double.MAX_VALUE);
                    GridPane.setHgrow(propertyKeyBox, Priority.ALWAYS);

                    gridPane.add(titleLabel, 0, i, 1, 1);
                    gridPane.add(propertyKeyBox, 1, i, 1, 1);

                }
                this.getChildren().clear();
                this.setCenter(gridPane);

            }
        });

    }

}
