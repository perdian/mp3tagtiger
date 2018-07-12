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
package de.perdian.apps.tagtiger.fx.modules.tools.updatetags;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.core.tagging.TaggablePropertyKey;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import de.perdian.apps.tagtiger.fx.modules.tools.updatetags.components.ExtractionRulesPane;
import de.perdian.apps.tagtiger.fx.modules.tools.updatetags.components.PreviewItemsPane;
import de.perdian.apps.tagtiger.fx.modules.tools.updatetags.components.RegularExpressionInputPane;
import de.perdian.apps.tagtiger.fx.modules.tools.updatetags.components.TemplatesPane;
import de.perdian.apps.tagtiger.fx.modules.tools.updatetags.model.ExtractionRule;
import de.perdian.apps.tagtiger.fx.modules.tools.updatetags.model.PreviewItem;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

class ExtractTagsPane extends GridPane {

    ExtractTagsPane(Selection selection, Localization localization) {

        ObservableList<ExtractionRule> extractionRules = FXCollections.observableArrayList();
        ObservableList<PreviewItem> previewItems = FXCollections.observableArrayList(selection.selectedFilesProperty().stream().map(PreviewItem::new).collect(Collectors.toList()));

        Property<Pattern> regexPatternProperty = new SimpleObjectProperty<>();
        regexPatternProperty.addListener((o, oldValue, newValue) -> this.recomputeExtractionRules(extractionRules, newValue));

        ChangeListener<Object> recomputeOnChangeListener = (o, oldValue, newValue) -> this.recomputePreviewItems(previewItems, regexPatternProperty.getValue(), extractionRules);
        extractionRules.addListener((ListChangeListener.Change<? extends ExtractionRule> change) -> {
            while (change.next()) {
                change.getAddedSubList().forEach(newExtractionRule -> newExtractionRule.getTargetPropertyKey().addListener(recomputeOnChangeListener));
                change.getRemoved().forEach(obsoleteExtractionRule -> obsoleteExtractionRule.getTargetPropertyKey().removeListener(recomputeOnChangeListener));
            }
            recomputeOnChangeListener.changed(null, null, null);
        });

        TemplatesPane templatesPane = new TemplatesPane(extractionRules, regexPatternProperty, localization);
        templatesPane.setPadding(new Insets(10, 10, 10, 10));
        TitledPane templatesTitledPane = new TitledPane(localization.templates(), templatesPane);
        templatesTitledPane.setCollapsible(false);
        templatesTitledPane.setPrefWidth(300);

        RegularExpressionInputPane regexInputPane = new RegularExpressionInputPane(regexPatternProperty, localization);
        regexInputPane.setPadding(new Insets(10, 10, 10, 10));
        TitledPane regexInputTitledPane = new TitledPane(localization.regularExpression(), regexInputPane);
        regexInputTitledPane.setCollapsible(false);
        GridPane.setHgrow(regexInputTitledPane, Priority.ALWAYS);

        ExtractionRulesPane extractionRulesPane = new ExtractionRulesPane(extractionRules, localization);
        extractionRulesPane.setPrefWidth(250);
        extractionRulesPane.setPadding(new Insets(10, 10, 10, 10));
        TitledPane extractionRulesTitledPane = new TitledPane(localization.extractionRules(), extractionRulesPane);
        extractionRulesTitledPane.setCollapsible(false);
        extractionRulesTitledPane.setMaxHeight(Double.MAX_VALUE);
        extractionRulesTitledPane.setPrefWidth(300);

        PreviewItemsPane previewPane = new PreviewItemsPane(previewItems, localization);
        previewPane.setPadding(new Insets(5, 5, 5, 5));
        TitledPane previewTitledPane = new TitledPane(localization.preview(), previewPane);
        previewTitledPane.setCollapsible(false);
        previewTitledPane.setMaxHeight(Double.MAX_VALUE);
        GridPane.setHgrow(previewTitledPane, Priority.ALWAYS);

        Button executeButton = new Button(localization.executeExtraction(), new ImageView(new Image(PreviewItemsPane.class.getClassLoader().getResourceAsStream("icons/16/save.png"))));
        executeButton.setOnAction(event -> this.rename(previewItems, event));
        HBox buttonPane = new HBox(executeButton);
        buttonPane.setPadding(new Insets(5, 5, 5, 5));
        buttonPane.setAlignment(Pos.CENTER);

        this.setHgap(5);
        this.setVgap(5);
        this.add(templatesTitledPane, 0, 0, 2, 1);
        this.add(regexInputTitledPane, 0, 1, 1, 1);
        this.add(extractionRulesTitledPane, 1, 1, 1, 1);
        this.add(previewTitledPane, 0, 2, 2, 1);
        this.add(buttonPane, 0, 3, 2, 1);
        this.setPadding(new Insets(5, 5, 5, 5));

    }

    private void rename(ObservableList<PreviewItem> previewItems, ActionEvent event) {

        for (PreviewItem previewItem : previewItems) {
            for (Map.Entry<TaggablePropertyKey, Property<String>> newValueEntry : previewItem.getNewValueMap().entrySet()) {
                if (!StringUtils.isEmpty(newValueEntry.getValue().getValue())) {
                    previewItem.getSourceFile().property(newValueEntry.getKey()).setValue(newValueEntry.getValue().getValue());
                }
            }
        }

        ((Stage)((Control)event.getSource()).getScene().getWindow()).close();

    }

    private void recomputePreviewItems(ObservableList<PreviewItem> previewItems, Pattern pattern, ObservableList<ExtractionRule> extractionRules) {
        for (PreviewItem previewItem : previewItems) {
            Matcher matcher = pattern.matcher(previewItem.getFileName().getValue());
            for (TaggablePropertyKey propertyKey : TaggablePropertyKey.values()) {
                Property<String> targetProperty = previewItem.property(propertyKey);
                previewItem.getMatches().setValue(matcher.matches());
                if (matcher.matches()) {
                    ExtractionRule extractionRule = extractionRules.stream().filter(rule -> propertyKey.equals(rule.getTargetPropertyKey().getValue())).findFirst().orElse(null);
                    targetProperty.setValue(extractionRule == null ? "" : matcher.group(extractionRule.getPatternGroupIndex()));
                } else {
                    targetProperty.setValue("");
                }
            }
        }
    }

    private void recomputeExtractionRules(ObservableList<ExtractionRule> extractionRules, Pattern pattern) {
        int newGroupCount = pattern.matcher("").groupCount();
        if (newGroupCount < extractionRules.size()) {
            while (extractionRules.size() > newGroupCount) {
                extractionRules.remove(extractionRules.size() - 1);
            }
        } else if (newGroupCount > extractionRules.size()) {
            while (extractionRules.size() < newGroupCount) {
                extractionRules.add(new ExtractionRule(extractionRules.size(), null));
            }
        }
    }

}
