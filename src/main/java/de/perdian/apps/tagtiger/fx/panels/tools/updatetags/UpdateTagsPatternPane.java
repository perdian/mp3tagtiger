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
package de.perdian.apps.tagtiger.fx.panels.tools.updatetags;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.core.tagging.TaggablePropertyKey;
import de.perdian.apps.tagtiger.core.tagging.TaggablePropertyKeyWrapper;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

class UpdateTagsPatternPane extends BorderPane {

    private ObservableList<TagComputeResult> resultList = null;
    private ObservableList<TagItem> tagItemList = null;
    private Property<Pattern> pattern = null;

    UpdateTagsPatternPane(Pattern pattern, Selection selection, Localization localization) {

        this.setPattern(new SimpleObjectProperty<>(pattern));
        this.setTagItemList(FXCollections.observableArrayList(IntStream.range(0, pattern.matcher("").groupCount()).mapToObj(TagItem::new).collect(Collectors.toList())));
        this.setResultList(FXCollections.observableArrayList(selection.selectedFilesProperty().stream().map(TagComputeResult::new).collect(Collectors.toList())));

        Label patternTitleLabel = new Label(localization.regularExpression() + ": " + pattern.toString());
        patternTitleLabel.setPadding(new Insets(0, 0, 10, 0));
        this.setTop(patternTitleLabel);

        ObservableList<TaggablePropertyKeyWrapper> propertyKeyWrappers = FXCollections.observableArrayList(TaggablePropertyKeyWrapper.of(Arrays.asList(TaggablePropertyKey.values()), localization));
        GridPane groupPane = new GridPane();
        groupPane.setHgap(10);
        groupPane.setVgap(2);
        groupPane.setPadding(new Insets(0, 10, 0, 0));
        for (TagItem tagItem : this.getTagItemList()) {

            ChoiceBox<TaggablePropertyKeyWrapper> propertyKeyBox = new ChoiceBox<>(propertyKeyWrappers);
            propertyKeyBox.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
                tagItem.getPropertyKey().setValue(newValue.getKey());
                this.recomputeResults();
            });

            groupPane.add(new Label(localization.group() + " " + (tagItem.getIndex()+1)), 0, tagItem.getIndex(), 1, 1);
            groupPane.add(propertyKeyBox, 1, tagItem.getIndex(), 1, 1);

        }
        this.setLeft(groupPane);

        TableColumn<TagComputeResult, Boolean> matchColumn = new TableColumn<>(localization.match());
        matchColumn.setSortable(false);
        matchColumn.setCellValueFactory(p -> p.getValue().getMatches());
        matchColumn.setMaxWidth(50);
        matchColumn.setMinWidth(50);

        TableColumn<TagComputeResult, String> fileNameColumn = new TableColumn<>(localization.fileName());
        fileNameColumn.setSortable(false);
        fileNameColumn.setCellValueFactory(p -> p.getValue().getTaggableFile().fileNameProperty());

        TableColumn<TagComputeResult, String> newArtistColumn = new TableColumn<>(localization.artist());
        newArtistColumn.setSortable(false);
        newArtistColumn.setCellValueFactory(p -> p.getValue().property(TaggablePropertyKey.ARTIST));
        TableColumn<TagComputeResult, String> newTitleColumn = new TableColumn<>(localization.title());
        newTitleColumn.setSortable(false);
        newTitleColumn.setCellValueFactory(p -> p.getValue().property(TaggablePropertyKey.TITLE));

        TableView<TagComputeResult> tableView = new TableView<>(FXCollections.observableArrayList(this.getResultList()));
        tableView.getColumns().addAll(Arrays.asList(matchColumn, fileNameColumn, newArtistColumn, newTitleColumn));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.setCenter(tableView);

        this.getPattern().addListener((o, oldValue, newValue) -> {
            patternTitleLabel.setText(localization.regularExpression() + ": " + newValue.toString());
            this.recomputeResults();
        });

    }

    void recomputeResults() {
        for (TagComputeResult resultListItem : this.getResultList()) {

            String fileName = resultListItem.getTaggableFile().fileNameProperty().getValue();
            Matcher fileNameMatcher = this.getPattern().getValue().matcher(fileName);
            resultListItem.getMatches().setValue(fileNameMatcher.matches());

            for (TaggablePropertyKey propertyKey : TaggablePropertyKey.values()) {
                String newPropertyValue = "";
                for (TagItem tagItem : this.getTagItemList())  {
                    if (propertyKey.equals(tagItem.getPropertyKey().getValue())) {
                        if (fileNameMatcher.matches()) {
                            newPropertyValue = fileNameMatcher.group(tagItem.getIndex() + 1);
                        }
                    }
                }
                resultListItem.property(propertyKey).setValue(newPropertyValue);
            }

        }
    }

    static class TagItem {

        private int index = 0;
        private Property<TaggablePropertyKey> propertyKey = null;

        TagItem(int index) {
            this.setIndex(index);
            this.setPropertyKey(new SimpleObjectProperty<>());
        }

        int getIndex() {
            return this.index;
        }
        void setIndex(int index) {
            this.index = index;
        }

        Property<TaggablePropertyKey> getPropertyKey() {
            return this.propertyKey;
        }
        void setPropertyKey(Property<TaggablePropertyKey> propertyKey) {
            this.propertyKey = propertyKey;
        }

    }

    static class TagComputeResult {

        private TaggableFile taggableFile = null;
        private Map<TaggablePropertyKey, Property<String>> newValueMap = null;
        private Property<Boolean> matches = null;

        TagComputeResult(TaggableFile taggableFile) {

            Map<TaggablePropertyKey, Property<String>> newValueMap = new LinkedHashMap<>();
            for (TaggablePropertyKey propertyKey : TaggablePropertyKey.values()) {
                newValueMap.put(propertyKey, new SimpleStringProperty(taggableFile.property(propertyKey).getValue()));
            }

            this.setTaggableFile(taggableFile);
            this.setNewValueMap(newValueMap);
            this.setMatches(new SimpleBooleanProperty());

        }

        Property<String> property(TaggablePropertyKey propertyKey) {
            return this.getNewValueMap().compute(propertyKey, (key, value) -> value == null ? new SimpleStringProperty() : value);
        }

        TaggableFile getTaggableFile() {
            return this.taggableFile;
        }
        void setTaggableFile(TaggableFile taggableFile) {
            this.taggableFile = taggableFile;
        }

        Map<TaggablePropertyKey, Property<String>> getNewValueMap() {
            return this.newValueMap;
        }
        void setNewValueMap(Map<TaggablePropertyKey, Property<String>> newValueMap) {
            this.newValueMap = newValueMap;
        }

        Property<Boolean> getMatches() {
            return this.matches;
        }
        void setMatches(Property<Boolean> matches) {
            this.matches = matches;
        }

    }

    ObservableList<TagComputeResult> getResultList() {
        return this.resultList;
    }
    void setResultList(ObservableList<TagComputeResult> resultList) {
        this.resultList = resultList;
    }

    ObservableList<TagItem> getTagItemList() {
        return this.tagItemList;
    }
    void setTagItemList(ObservableList<TagItem> tagItemList) {
        this.tagItemList = tagItemList;
    }

    Property<Pattern> getPattern() {
        return this.pattern;
    }
    void setPattern(Property<Pattern> pattern) {
        this.pattern = pattern;
    }

}
