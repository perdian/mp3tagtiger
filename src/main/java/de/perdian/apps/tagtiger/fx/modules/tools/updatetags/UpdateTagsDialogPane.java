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
package de.perdian.apps.tagtiger.fx.modules.tools.updatetags;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.core.tagging.TaggablePropertyKey;
import de.perdian.apps.tagtiger.core.tagging.TaggablePropertyKeyWrapper;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

class UpdateTagsDialogPane extends GridPane {

    UpdateTagsDialogPane(Selection selection, Localization localization) {

        Property<Pattern> regexPatternProperty = new SimpleObjectProperty<>();
        BooleanProperty executeEnabledProperty = new SimpleBooleanProperty(false);
        ObservableList<ExtractionRule> extractionRules = FXCollections.observableArrayList();
        ObservableList<UpdateTagsPreviewItem> previewItems = FXCollections.observableArrayList(selection.selectedFilesProperty().stream().map(UpdateTagsPreviewItem::new).collect(Collectors.toList()));
        ChangeListener<Object> recomputeOnChangeListener = (o, oldValue, newValue) -> this.recomputePreviewItems(previewItems, regexPatternProperty.getValue(), extractionRules, executeEnabledProperty);

        regexPatternProperty.addListener((o, oldValue, newValue) -> this.recomputeExtractionRules(newValue, extractionRules));
        extractionRules.addListener((Change<? extends ExtractionRule> change) -> {
            while (change.next()) {
                change.getAddedSubList().forEach(newExtractionRule -> newExtractionRule.getTargetPropertyKey().addListener(recomputeOnChangeListener));
                change.getRemoved().forEach(obsoleteExtractionRule -> obsoleteExtractionRule.getTargetPropertyKey().removeListener(recomputeOnChangeListener));
            }
            recomputeOnChangeListener.changed(null, null, null);
        });

        RegexPane regexPane = new RegexPane(regexPatternProperty, localization);
        regexPane.setPadding(new Insets(10, 10, 10, 10));
        TitledPane regexTitledPane = new TitledPane(localization.regularExpression(), regexPane);
        regexTitledPane.setCollapsible(false);
        GridPane.setHgrow(regexTitledPane, Priority.ALWAYS);

        ExtractionPane extractionPane = new ExtractionPane(extractionRules, localization);
        extractionPane.setPrefWidth(250);
        extractionPane.setPadding(new Insets(10, 10, 10, 10));
        TitledPane extractionTitledPane = new TitledPane(localization.extractionRules(), extractionPane);
        extractionTitledPane.setCollapsible(false);
        extractionTitledPane.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(extractionTitledPane, Priority.ALWAYS);

        PreviewPane previewPane = new PreviewPane(previewItems, localization, executeEnabledProperty);
        previewPane.setPadding(new Insets(5, 5, 5, 5));
        TitledPane previewTitledPane = new TitledPane(localization.preview(), previewPane);
        previewTitledPane.setCollapsible(false);
        previewTitledPane.setMaxHeight(Double.MAX_VALUE);
        GridPane.setHgrow(previewTitledPane, Priority.ALWAYS);
        GridPane.setVgrow(previewTitledPane, Priority.ALWAYS);

        this.add(regexTitledPane, 0, 0, 2, 1);
        this.add(extractionTitledPane, 0, 1, 1, 1);
        this.add(previewTitledPane, 1, 1, 1, 1);
        this.setHgap(5);
        this.setVgap(5);
        this.setPadding(new Insets(5, 5, 5, 5));

    }

    private void recomputeExtractionRules(Pattern newPattern, ObservableList<ExtractionRule> extractionRules) {
        int newGroupCount = newPattern.matcher("").groupCount();
        if (newGroupCount < extractionRules.size()) {
            while (extractionRules.size() > newGroupCount) {
                extractionRules.remove(extractionRules.size() - 1);
            }
        } else if (newGroupCount > extractionRules.size()) {
            while (extractionRules.size() < newGroupCount) {
                extractionRules.add(new ExtractionRule(extractionRules.size()));
            }
        }
    }

    private void recomputePreviewItems(ObservableList<UpdateTagsPreviewItem> previewItems, Pattern pattern, ObservableList<ExtractionRule> extractionRules, BooleanProperty executeEnabledProperty) {

        for (UpdateTagsPreviewItem previewItem : previewItems) {
            Matcher matcher = pattern.matcher(previewItem.getFileName().getValue());
            for (TaggablePropertyKey propertyKey : TaggablePropertyKey.values()) {
                Property<String> targetProperty = previewItem.property(propertyKey);
                previewItem.getMatches().setValue(matcher.matches());
                if (matcher.matches()) {
                    ExtractionRule extractionRule = ExtractionRule.find(propertyKey, extractionRules);
                    targetProperty.setValue(extractionRule == null ? "" : matcher.group(extractionRule.getPatternGroupIndex() + 1));
                } else {
                    targetProperty.setValue("");
                }
            }
        }

        executeEnabledProperty.setValue(
            extractionRules.stream()
                .filter(rule -> rule.getTargetPropertyKey().getValue() != null)
                .findAny()
                .isPresent()
        );

    }

    static class RegexPane extends GridPane {

        RegexPane(Property<Pattern> externalPatternProperty, Localization localization) {

            Property<Pattern> internalPatternProperty = new SimpleObjectProperty<>();

            Button regexApplyButton = new Button(localization.apply());
            regexApplyButton.setOnAction(event -> externalPatternProperty.setValue(internalPatternProperty.getValue()));
            regexApplyButton.setDisable(true);
            regexApplyButton.setMaxHeight(Double.MAX_VALUE);

            ObservableList<String> regexPatternDefaults = FXCollections.observableArrayList(
                "(.*?) - (.*?)",
                "(.*?) \\((.*?)\\)"
            );
            ComboBox<String> regexPatternBox = new ComboBox<>(FXCollections.observableArrayList(regexPatternDefaults));
            regexPatternBox.setEditable(true);
            regexPatternBox.setMaxWidth(Double.MAX_VALUE);
            regexPatternBox.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    regexApplyButton.fire();
                }
            });
            GridPane.setHgrow(regexPatternBox, Priority.ALWAYS);

            TextArea regexResultArea = new TextArea(localization.noRegularExpressionSetYet());
            regexResultArea.setEditable(false);
            regexResultArea.setFont(Font.font("Monospaced"));
            regexResultArea.setPrefHeight(60d);
            regexResultArea.minHeightProperty().bind(regexResultArea.prefHeightProperty());
            regexResultArea.setFocusTraversable(false);

            this.setHgap(5);
            this.setVgap(5);
            this.add(new Label(localization.regularExpression()), 0, 0, 2, 1);
            this.add(regexPatternBox, 0, 1, 1, 1);
            this.add(regexApplyButton, 1, 1, 1, 1);
            this.add(new Label(localization.compilationResult()), 0, 2, 2, 1);
            this.add(regexResultArea, 0, 3, 2, 1);

            regexPatternBox.editorProperty().get().textProperty().addListener((o, oldValue, newValue) -> {
                try {
                    if (newValue == null || newValue.isEmpty()) {
                        throw new PatternSyntaxException(localization.noRegularExpressionSetYet(), newValue, -1);
                    } else {
                        Pattern regexPattern = Pattern.compile(newValue);
                        int numberOfGroups = regexPattern.matcher("").groupCount();
                        if (numberOfGroups < 1) {
                            throw new PatternSyntaxException(localization.regularExpressionMustContainAtLeastOneGroup(), newValue, -1);
                        } else {
                            internalPatternProperty.setValue(regexPattern);
                            regexApplyButton.setDisable(false);
                            regexResultArea.setText(localization.regularExpressionValid());
                            regexResultArea.setStyle("-fx-control-inner-background: #00ff00");
                        }
                    }
                } catch (PatternSyntaxException e) {
                    internalPatternProperty.setValue(null);
                    regexApplyButton.setDisable(true);
                    regexResultArea.setText(e.getMessage());
                    regexResultArea.setStyle("-fx-control-inner-background: #ffaaaa");
                }
            });

        }

    }

    static class ExtractionRule {

        private int patternGroupIndex = 0;
        private Property<TaggablePropertyKey> targetPropertyKey = null;

        ExtractionRule(int patternGroupIndex) {
            this.setPatternGroupIndex(patternGroupIndex);
            this.setTargetPropertyKey(new SimpleObjectProperty<>());
        }

        static ExtractionRule find(TaggablePropertyKey propertyKey, Collection<ExtractionRule> extractionRules) {
            return extractionRules.stream()
                .filter(extractionRule -> propertyKey.equals(extractionRule.getTargetPropertyKey().getValue()))
                .findFirst()
                .orElse(null);
        }

        int getPatternGroupIndex() {
            return this.patternGroupIndex;
        }
        void setPatternGroupIndex(int patternGroupIndex) {
            this.patternGroupIndex = patternGroupIndex;
        }

        Property<TaggablePropertyKey> getTargetPropertyKey() {
            return this.targetPropertyKey;
        }
        void setTargetPropertyKey(Property<TaggablePropertyKey> targetPropertyKey) {
            this.targetPropertyKey = targetPropertyKey;
        }

    }

    static class ExtractionPane extends BorderPane {

        ExtractionPane(ObservableList<ExtractionRule> extractionRules, Localization localization) {

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

                    List<TaggablePropertyKey> propertyKeys = Arrays.asList(TaggablePropertyKey.values());
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

    static class PreviewPane extends BorderPane {

        PreviewPane(ObservableList<UpdateTagsPreviewItem> previewItems, Localization localization, BooleanProperty executeEnabledProperty) {

            TableColumn<UpdateTagsPreviewItem, ImageView> matchColumn = new TableColumn<>(localization.match());
            matchColumn.setSortable(false);
            matchColumn.setCellValueFactory(p -> p.getValue().getMatchesImageView());
            matchColumn.setMaxWidth(50);
            matchColumn.setMinWidth(50);

            TableColumn<UpdateTagsPreviewItem, String> fileNameColumn = new TableColumn<>(localization.fileName());
            fileNameColumn.setSortable(false);
            fileNameColumn.setCellValueFactory(p -> p.getValue().getFileName());

            TableColumn<UpdateTagsPreviewItem, String> newArtistColumn = new TableColumn<>(localization.artist());
            newArtistColumn.setSortable(false);
            newArtistColumn.setCellValueFactory(p -> p.getValue().property(TaggablePropertyKey.ARTIST));
            TableColumn<UpdateTagsPreviewItem, String> newTitleColumn = new TableColumn<>(localization.title());
            newTitleColumn.setSortable(false);
            newTitleColumn.setCellValueFactory(p -> p.getValue().property(TaggablePropertyKey.TITLE));

            TableView<UpdateTagsPreviewItem> tableView = new TableView<>(previewItems);
            tableView.getColumns().addAll(Arrays.asList(matchColumn, fileNameColumn, newArtistColumn, newTitleColumn));
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            this.setCenter(tableView);

            Button executeButton = new Button(localization.executeExtraction(), new ImageView(new Image(UpdateTagsDialogPane.class.getClassLoader().getResourceAsStream("icons/16/save.png"))));
            executeButton.setOnAction(new UpdateTagsExecuteEventHandler(previewItems));
            executeButton.setDisable(true);
            executeButton.disableProperty().bind(executeEnabledProperty.not());
            HBox buttonPane = new HBox(executeButton);
            buttonPane.setPadding(new Insets(5, 5, 5, 5));
            buttonPane.setAlignment(Pos.CENTER);
            this.setBottom(buttonPane);

        }

    }

}
