/*
 * Copyright 2014 Christian Seifert
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

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jaudiotagger.tag.Tag;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class TagImageList {

    private ObservableList<TagImage> tagImages = null;
    private List<ChangeListener<Object>> changeListeners = new CopyOnWriteArrayList<>();
    private boolean dirty = false;

    public TagImageList(TagImageList sourceList) {
        ChangeListener<Object> distributionChangeListener = new DelegatingChangeListener();
        ObservableList<TagImage> newImageList = FXCollections.observableArrayList();
        for (TagImage sourceImage : sourceList.getTagImages()) {
            TagImage newImage = new TagImage(sourceImage);
            newImage.addChangeListener(distributionChangeListener);
            newImageList.add(newImage);
        }
        this.setTagImages(newImageList);
        this.setDirty(true);
    }

    TagImageList(Tag sourceTag) {

        ChangeListener<Object> distributionChangeListener = new DelegatingChangeListener();

        List<TagImage> tagImages = TagImageHelper.loadTagImages(sourceTag);
        tagImages.forEach(tagImage -> tagImage.addChangeListener(distributionChangeListener));

        ListChangeListener<TagImage> tagImagesChangeListener = change -> {
            while (change.next()) {
                distributionChangeListener.changed(null, null, change.getList());
                change.getAddedSubList().forEach(image -> image.addChangeListener(distributionChangeListener));
                change.getRemoved().forEach(image -> image.removeChangeListener(distributionChangeListener));
            }
        };
        ObservableList<TagImage> observableTagImages = FXCollections.observableArrayList(tagImages);
        observableTagImages.addListener(tagImagesChangeListener);
        this.setTagImages(observableTagImages);

    }

    class DelegatingChangeListener implements ChangeListener<Object> {

        @Override
        public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
            if (!Objects.equals(oldValue, newValue)) {
                TagImageList.this.getChangeListeners().forEach(listener -> listener.changed(observable, oldValue, newValue));
                TagImageList.this.setDirty(true);
            }
        }

    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.getClass().getSimpleName());
        result.append("[tagImages=").append(this.getTagImages());
        return result.append("]").toString();
    }

    @Override
    public int hashCode() {
        return this.getTagImages().hashCode();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        } else if (that instanceof TagImageList) {
            TagImageList thatList = (TagImageList)that;
            if (this.getTagImages().size() != thatList.getTagImages().size()) {
                return false;
            } else {
                for (int i=0; i < this.getTagImages().size(); i++) {
                    if (!this.getTagImages().get(i).equals(thatList.getTagImages().get(i))) {
                        return false;
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }

    public ObservableList<TagImage> getTagImages() {
        return this.tagImages;
    }
    private void setTagImages(ObservableList<TagImage> tagImages) {
        this.tagImages = tagImages;
    }

    public void addChangeListener(ChangeListener<Object> changeListener) {
        this.getChangeListeners().add(changeListener);
    }
    public void removeChangeListener(ChangeListener<Object> changeListener) {
        this.getChangeListeners().remove(changeListener);
    }
    List<ChangeListener<Object>> getChangeListeners() {
        return this.changeListeners;
    }
    void setChangeListeners(List<ChangeListener<Object>> changeListeners) {
        this.changeListeners = changeListeners;
    }

    boolean isDirty() {
        return this.dirty;
    }
    void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

}