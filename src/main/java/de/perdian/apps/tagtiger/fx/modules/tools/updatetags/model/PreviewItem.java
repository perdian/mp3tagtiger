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
package de.perdian.apps.tagtiger.fx.modules.tools.updatetags.model;

import java.util.LinkedHashMap;
import java.util.Map;

import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.core.tagging.TaggablePropertyKey;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PreviewItem {

    private static final Image MATCH_YES_IMAGE = new Image(PreviewItem.class.getClassLoader().getResourceAsStream("icons/16/match-yes.png"));
    private static final Image MATCH_NO_IMAGE = new Image(PreviewItem.class.getClassLoader().getResourceAsStream("icons/16/match-no.png"));

    private TaggableFile sourceFile = null;
    private Property<String> fileName = null;
    private Map<TaggablePropertyKey, Property<String>> newValueMap = null;
    private Property<Boolean> matches = null;
    private Property<ImageView> matchesImageView = null;

    public PreviewItem(TaggableFile sourceFile) {

        Property<ImageView> matchesImageViewProperty = new SimpleObjectProperty<>();
        Property<Boolean> matchesProperty = new SimpleObjectProperty<>(null);
        matchesProperty.addListener((o, oldValue, newValue) -> matchesImageViewProperty.setValue(new ImageView(newValue.booleanValue() ? MATCH_YES_IMAGE : MATCH_NO_IMAGE)));

        this.setSourceFile(sourceFile);
        this.setFileName(sourceFile.fileNameProperty());
        this.setNewValueMap(new LinkedHashMap<>());
        this.setMatches(matchesProperty);
        this.setMatchesImageView(matchesImageViewProperty);

    }

    public Property<String> property(TaggablePropertyKey propertyKey) {
        return this.getNewValueMap().compute(propertyKey, (key, value) -> value == null ? new SimpleStringProperty() : value);
    }

    public TaggableFile getSourceFile() {
        return this.sourceFile;
    }
    private void setSourceFile(TaggableFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    public Property<String> getFileName() {
        return this.fileName;
    }
    private void setFileName(Property<String> fileName) {
        this.fileName = fileName;
    }

    public Map<TaggablePropertyKey, Property<String>> getNewValueMap() {
        return this.newValueMap;
    }
    private void setNewValueMap(Map<TaggablePropertyKey, Property<String>> newValueMap) {
        this.newValueMap = newValueMap;
    }

    public Property<Boolean> getMatches() {
        return this.matches;
    }
    private void setMatches(Property<Boolean> matches) {
        this.matches = matches;
    }

    public Property<ImageView> getMatchesImageView() {
        return this.matchesImageView;
    }
    private void setMatchesImageView(Property<ImageView> matchesImageView) {
        this.matchesImageView = matchesImageView;
    }

}
