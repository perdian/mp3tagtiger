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
package de.perdian.apps.tagtiger.fx.components.tools.filenames.impl;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.perdian.apps.tagtiger.fx.components.tools.filenames.*;
import de.perdian.apps.tagtiger.model.SongAttribute;
import de.perdian.apps.tagtiger.model.SongFile;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ExtractTagsFromFilenamesActionEventHandler extends FilenamesToolActionEventHandler {

    public ExtractTagsFromFilenamesActionEventHandler(ObservableList<SongFile> files) {
        super("Extract tags from file names", files);
    }

    @Override
    protected Pane createDialogPane(ObservableList<FilenamesToolItem> items) {

        Map<String, String> legendVariables = Arrays.stream(FilenamesToolAttribute.values())
            .filter(attribute -> attribute.getFilenameResolver() != null)
            .collect(Collectors.toMap(variable -> variable.name().toLowerCase(), variable -> variable.getAttribute().getTitle(), (e1, e2) -> e1, LinkedHashMap::new));

        FilenamesToolLegendPane legendPane = new FilenamesToolLegendPane("Destination regex groups", legendVariables, "?<", ">");

        FilenamesToolPatternPane patternPane = new FilenamesToolPatternPane();
        patternPane.patternProperty().addListener((o, oldValue, newValue) -> {
            try {
                this.updateToItems(newValue, items);
                patternPane.errorProperty().setValue(null);
            } catch (Exception e) {
                patternPane.errorProperty().setValue(e);
            }
        });
        patternPane.setOnActionEvent(event -> {
            this.updateToSongFiles(items);
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        });

        TableView<FilenamesToolItem> tableView = this.createTableView(items);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        VBox dialogPane = new VBox();
        dialogPane.getChildren().add(legendPane);
        dialogPane.getChildren().add(patternPane);
        dialogPane.getChildren().add(tableView);
        dialogPane.setPadding(new Insets(10, 10, 10, 10));
        dialogPane.setSpacing(10);
        return dialogPane;

    }

    private TableView<FilenamesToolItem> createTableView(ObservableList<FilenamesToolItem> items) {

        TableColumn<FilenamesToolItem, Boolean> matchingColumn = new TableColumn<>("");
        matchingColumn.setCellValueFactory(callback -> callback.getValue().getMatching());
        matchingColumn.setCellFactory(FilenamesToolHelpers.createIconCellCallback(FontAwesomeIcon.CHECK, FontAwesomeIcon.BAN));
        matchingColumn.setEditable(false);
        matchingColumn.setSortable(false);
        matchingColumn.setReorderable(false);
        matchingColumn.setMinWidth(25);
        matchingColumn.setMaxWidth(25);

        TableColumn<FilenamesToolItem, String> filenameColumn = new TableColumn<>("File name");
        filenameColumn.setCellValueFactory(features -> features.getValue().originalValueProperty(SongAttribute.FILENAME));
        filenameColumn.setSortable(false);
        filenameColumn.setReorderable(false);

        TableColumn<FilenamesToolItem, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(features -> features.getValue().newValueProperty(SongAttribute.TITLE));
        titleColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        titleColumn.setEditable(true);
        titleColumn.setSortable(false);
        titleColumn.setReorderable(false);

        TableColumn<FilenamesToolItem, String> artistColumn = new TableColumn<>("Artist");
        artistColumn.setCellValueFactory(features -> features.getValue().newValueProperty(SongAttribute.ARTIST));
        artistColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        artistColumn.setEditable(true);
        artistColumn.setSortable(false);
        artistColumn.setReorderable(false);

        TableColumn<FilenamesToolItem, String> albumColumn = new TableColumn<>("Album");
        albumColumn.setCellValueFactory(features -> features.getValue().newValueProperty(SongAttribute.ALBUM));
        albumColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        albumColumn.setEditable(true);
        albumColumn.setSortable(false);
        albumColumn.setReorderable(false);

        TableView<FilenamesToolItem> itemsTableView = new TableView<>(items);
        itemsTableView.setEditable(true);
        itemsTableView.getColumns().addAll(List.of(matchingColumn, filenameColumn, titleColumn, artistColumn, albumColumn));
        itemsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return itemsTableView;

    }

    private void updateToSongFiles(ObservableList<FilenamesToolItem> items) {
        for (FilenamesToolItem item : items) {
            for (Map.Entry<SongAttribute, FilenamesToolItemValue> itemValue : item.getValues().entrySet()) {
                item.getSongFile().getAttributeValueProperty(itemValue.getKey(), String.class).setValue(itemValue.getValue().getNewValue().getValue());
            }
        }
    }

    private void updateToItems(String regexPatternString, ObservableList<FilenamesToolItem> items) {

        Pattern regexPattern = Pattern.compile(regexPatternString.strip());
        Map<Integer, SongAttribute> songAttributesByIndex = new HashMap<>();
        for (Map.Entry<String, Integer> namedGroup : regexPattern.namedGroups().entrySet()) {
            SongAttribute songAttribute = Stream.of(SongAttribute.values())
                .filter(value -> value.name().equalsIgnoreCase(namedGroup.getKey()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find attribute for name '" + namedGroup.getKey() + "'"));
            songAttributesByIndex.put(namedGroup.getValue(), songAttribute);
        }

        for (FilenamesToolItem item : items) {
            String itemFilename = item.getValues().get(SongAttribute.FILENAME).getNewValue().getValue().strip();
            Matcher itemFilenameMatcher = regexPattern.matcher(itemFilename);
            boolean itemMatcherValid = itemFilenameMatcher.matches();
            item.getMatching().setValue(itemMatcherValid);
            if (itemMatcherValid) {
                for (Map.Entry<String, Integer> namedGroup : itemFilenameMatcher.namedGroups().entrySet()) {
                    SongAttribute songAttribute = songAttributesByIndex.get(namedGroup.getValue());
                    String itemMatcherValue = itemFilenameMatcher.group(namedGroup.getValue());
                    item.newValueProperty(songAttribute).setValue(itemMatcherValue);
                }
            }
        }

    }

}
