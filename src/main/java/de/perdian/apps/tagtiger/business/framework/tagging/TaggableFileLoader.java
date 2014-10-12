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
package de.perdian.apps.tagtiger.business.framework.tagging;

import java.io.File;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import com.mpatric.mp3agic.Mp3File;

import de.perdian.apps.tagtiger.business.framework.selection.Selection;

public class TaggableFileLoader {

    private Selection targetSelection = null;

    public TaggableFileLoader(Selection targetSelection) {
        this.setTargetSelection(targetSelection);
    }

    public TaggableFile loadFile(File file) throws Exception {

        int extensionSeparator = file.getName().lastIndexOf(".");

        TaggableFile fileWrapper = new TaggableFile(file);
        fileWrapper.getFileName().set(extensionSeparator < 0 ? file.getName() : file.getName().substring(0, extensionSeparator));
        fileWrapper.getFileExtension().set(extensionSeparator < 0 || extensionSeparator >= file.getName().length() - 1 ? null : file.getName().substring(extensionSeparator + 1));

        // Load the actual MP3 tags
        Mp3File mp3File = new Mp3File(file.getAbsolutePath());
        for(Tag tag : Tag.values()) {
            tag.updateWrapper(fileWrapper, mp3File);
        }

        // Add listener to get notified when something has changed
        fileWrapper.getChanged().addListener(new UpdateChangedFilesInTargetSelectionListener(fileWrapper));

        UpdateTargetPropertyWithNewValueListener<String> updateListener = new UpdateTargetPropertyWithNewValueListener<>(fileWrapper.getChanged());
        fileWrapper.getFileExtension().addListener(updateListener);
        fileWrapper.getFileName().addListener(updateListener);
        fileWrapper.getTagAlbum().addListener(updateListener);
        fileWrapper.getTagArtist().addListener(updateListener);
        fileWrapper.getTagCd().addListener(updateListener);
        fileWrapper.getTagCoder().addListener(updateListener);
        fileWrapper.getTagComment().addListener(updateListener);
        fileWrapper.getTagComposer().addListener(updateListener);
        fileWrapper.getTagCopyright().addListener(updateListener);
        fileWrapper.getTagGenre().addListener(updateListener);
        fileWrapper.getTagOriginalArtist().addListener(updateListener);
        fileWrapper.getTagTitle().addListener(updateListener);
        fileWrapper.getTagTrackNumber().addListener(updateListener);
        fileWrapper.getTagTracksTotal().addListener(updateListener);
        fileWrapper.getTagUrl().addListener(updateListener);
        fileWrapper.getTagYear().addListener(updateListener);

        return fileWrapper;

    }

    // -------------------------------------------------------------------------
    // --- Inner classes -------------------------------------------------------
    // -------------------------------------------------------------------------

    class UpdateChangedFilesInTargetSelectionListener implements ChangeListener<Boolean> {

        private TaggableFile file = null;

        UpdateChangedFilesInTargetSelectionListener(TaggableFile file) {
            this.setFile(file);
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue.booleanValue()) {
                TaggableFileLoader.this.getTargetSelection().getChangedFiles().add(this.getFile());
            } else {
                TaggableFileLoader.this.getTargetSelection().getChangedFiles().remove(this.getFile());
            }
        }

        // ---------------------------------------------------------------------
        // --- Property access methods -----------------------------------------
        // ---------------------------------------------------------------------

        private TaggableFile getFile() {
            return this.file;
        }
        private void setFile(TaggableFile file) {
            this.file = file;
        }

    }

    class UpdateTargetPropertyWithNewValueListener<T> implements ChangeListener<T> {

        private BooleanProperty targetProperty = null;

        UpdateTargetPropertyWithNewValueListener(BooleanProperty targetProperty) {
            this.setTargetProperty(targetProperty);
        }

        @Override
        public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
            if (oldValue == null && newValue == null) {
                // Do nothing - no change
            } else if (oldValue == newValue) {
                // Do nothing - no change
            } else if (oldValue == null || !oldValue.equals(newValue)) {
                this.getTargetProperty().set(true);
            }
        }

        // -------------------------------------------------------------------------
        // --- Property access methods ---------------------------------------------
        // -------------------------------------------------------------------------

        private BooleanProperty getTargetProperty() {
            return this.targetProperty;
        }
        private void setTargetProperty(BooleanProperty targetProperty) {
            this.targetProperty = targetProperty;
        }

    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    Selection getTargetSelection() {
        return this.targetSelection;
    }
    private void setTargetSelection(Selection targetSelection) {
        this.targetSelection = targetSelection;
    }

}