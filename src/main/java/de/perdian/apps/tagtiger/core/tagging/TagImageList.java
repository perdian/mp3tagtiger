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
package de.perdian.apps.tagtiger.core.tagging;

import java.util.Collection;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

public class TagImageList {

    private ObservableList<TagImage> tagImages = null;
    private final BooleanProperty changed = new SimpleBooleanProperty();

    public TagImageList(List<TagImage> tagImages, ChangeListener<Object> changeListener) {

        ObservableList<TagImage> observableTagImages = FXCollections.observableArrayList();
        observableTagImages.addListener((Change<? extends TagImage> change) -> change.getList().forEach(tagImage -> tagImage.changedProperty().addListener((o, oldValue, newValue) -> this.changedProperty().set(true))));
        observableTagImages.setAll(tagImages);
        observableTagImages.addListener((Change<? extends TagImage> change) -> this.changedProperty().set(true));

        this.changedProperty().addListener(changeListener);
        this.setTagImages(observableTagImages);

    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.getClass().getSimpleName());
        result.append("[tagImages=").append(this.getTagImages());
        result.append(",changed=").append(this.changedProperty().get());
        return result.append("]").toString();
    }

    public void updateTagImages(Collection<TagImage> newImages) {
        this.getTagImages().setAll(newImages);
        this.changedProperty().set(true);
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public ObservableList<TagImage> getTagImages() {
        return this.tagImages;
    }
    private void setTagImages(ObservableList<TagImage> tagImages) {
        this.tagImages = tagImages;
    }

    public BooleanProperty changedProperty() {
        return this.changed;
    }

}