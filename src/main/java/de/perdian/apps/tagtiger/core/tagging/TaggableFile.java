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

import java.io.File;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class TaggableFile {

    private static final Logger log = LoggerFactory.getLogger(TaggableFile.class);

    private File file = null;
    private AudioFile audioFile = null;

    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty artist = new SimpleStringProperty();
    private final StringProperty album = new SimpleStringProperty();
    private final StringProperty discNumber = new SimpleStringProperty();
    private final StringProperty discsTotal = new SimpleStringProperty();
    private final StringProperty year = new SimpleStringProperty();
    private final StringProperty trackNumber = new SimpleStringProperty();
    private final StringProperty tracksTotal = new SimpleStringProperty();
    private final StringProperty genre = new SimpleStringProperty();
    private final StringProperty comment = new SimpleStringProperty();
    private final StringProperty composer = new SimpleStringProperty();
    private final ObjectProperty<TagImageList> images = new SimpleObjectProperty<>();

    private final StringProperty fileName = new SimpleStringProperty();
    private final StringProperty fileExtension = new SimpleStringProperty();
    private final BooleanProperty dirty = new SimpleBooleanProperty(false);

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    public TaggableFile(File file) throws Exception {

        ChangeListener<Object> markAsDirtyChangeListener = TaggableFileHelper.createMarkDirtyChangeListener(this.dirtyProperty());

        log.trace("Reading tag information from file: {}", file.getAbsolutePath());
        AudioFile audioFile = AudioFileIO.read(file);
        Tag tag = audioFile.getTagOrCreateDefault();
        this.setFile(file);
        this.setAudioFile(audioFile);

        // Update all the internal tag objects with the values read from the tag
        // available in the file that we just read
        TaggableFileHelper.initializePropertyFromStringTag(this.titleProperty(), tag, FieldKey.TITLE, markAsDirtyChangeListener);
        TaggableFileHelper.initializePropertyFromStringTag(this.artistProperty(), tag, FieldKey.ARTIST, markAsDirtyChangeListener);
        TaggableFileHelper.initializePropertyFromStringTag(this.albumProperty(), tag, FieldKey.ALBUM, markAsDirtyChangeListener);
        TaggableFileHelper.initializePropertyFromStringTag(this.yearProperty(), tag, FieldKey.YEAR, markAsDirtyChangeListener);
        TaggableFileHelper.initializePropertyFromStringTag(this.trackNumberProperty(), tag, FieldKey.TRACK, markAsDirtyChangeListener);
        TaggableFileHelper.initializePropertyFromStringTag(this.tracksTotalProperty(), tag, FieldKey.TRACK_TOTAL, markAsDirtyChangeListener);
        TaggableFileHelper.initializePropertyFromStringTag(this.discNumberProperty(), tag, FieldKey.DISC_NO, markAsDirtyChangeListener);
        TaggableFileHelper.initializePropertyFromStringTag(this.discsTotalProperty(), tag, FieldKey.DISC_TOTAL, markAsDirtyChangeListener);
        TaggableFileHelper.initializePropertyFromGenreTag(this.genreProperty(), tag, FieldKey.GENRE, markAsDirtyChangeListener);
        TaggableFileHelper.initializePropertyFromStringTag(this.commentProperty(), tag, FieldKey.COMMENT, markAsDirtyChangeListener);
        TaggableFileHelper.initializePropertyFromStringTag(this.composerProperty(), tag, FieldKey.COMPOSER, markAsDirtyChangeListener);
        TaggableFileHelper.initializePropertyFromImagesTag(this.imagesProperty(), tag, markAsDirtyChangeListener);

        // Finally we extract the file name and extension information from the
        // file so that the user may change this as well
        int extensionSeparator = file.getName().lastIndexOf(".");
        TaggableFileHelper.initializePropertyFromStringValue(this.fileNameProperty(), extensionSeparator < 0 ? file.getName() : file.getName().substring(0, extensionSeparator), markAsDirtyChangeListener);
        TaggableFileHelper.initializePropertyFromStringValue(this.fileExtensionProperty(), extensionSeparator < 0 || extensionSeparator >= file.getName().length() - 1 ? null : file.getName().substring(extensionSeparator + 1), markAsDirtyChangeListener);

    }

    public void writeIntoFile() throws Exception {

        StringBuilder newFileName = new StringBuilder();
        newFileName.append(this.fileNameProperty().get());
        newFileName.append(".").append(this.fileExtensionProperty().get());

        File currentSystemFile = this.getFile().getCanonicalFile();
        File newSystemFile = new File(currentSystemFile.getParentFile(), newFileName.toString());
        if (!newSystemFile.equals(currentSystemFile)) {
            currentSystemFile.renameTo(newSystemFile);
        }

        AudioFile audioFile = this.getAudioFile();
        Tag targetTag = audioFile.getTagOrCreateAndSetDefault();
        TaggableFileHelper.updateTagFromStringProperty(targetTag, FieldKey.TITLE, this.titleProperty());
        TaggableFileHelper.updateTagFromStringProperty(targetTag, FieldKey.ARTIST, this.artistProperty());
        TaggableFileHelper.updateTagFromStringProperty(targetTag, FieldKey.ALBUM, this.albumProperty());
        TaggableFileHelper.updateTagFromStringProperty(targetTag, FieldKey.YEAR, this.yearProperty());
        TaggableFileHelper.updateTagFromStringProperty(targetTag, FieldKey.TRACK, this.trackNumberProperty());
        TaggableFileHelper.updateTagFromStringProperty(targetTag, FieldKey.TRACK_TOTAL, this.tracksTotalProperty());
        TaggableFileHelper.updateTagFromStringProperty(targetTag, FieldKey.DISC_NO, this.discNumberProperty());
        TaggableFileHelper.updateTagFromStringProperty(targetTag, FieldKey.DISC_TOTAL, this.discsTotalProperty());
        TaggableFileHelper.updateTagFromGenreProperty(targetTag, this.genreProperty());
        TaggableFileHelper.updateTagFromStringProperty(targetTag, FieldKey.COMMENT, this.commentProperty());
        TaggableFileHelper.updateTagFromStringProperty(targetTag, FieldKey.COMPOSER, this.composerProperty());
        TaggableFileHelper.updateTagFromTagImageListProperty(targetTag, this.imagesProperty());

        audioFile.setFile(newSystemFile);
        audioFile.setTag(targetTag);
        AudioFileIO.write(audioFile);
        this.dirtyProperty().setValue(false);

    }

    @Override
    public String toString() {
        return this.getFile().getName();
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public File getFile() {
        return this.file;
    }
    private void setFile(File file) {
        this.file = file;
    }

    private AudioFile getAudioFile() {
        return this.audioFile;
    }
    private void setAudioFile(AudioFile audioFile) {
        this.audioFile = audioFile;
    }

    public StringProperty fileNameProperty() {
        return this.fileName;
    }
    public StringProperty fileExtensionProperty() {
        return this.fileExtension;
    }
    public BooleanProperty dirtyProperty() {
        return this.dirty;
    }

    public StringProperty titleProperty() {
        return this.title;
    }
    public StringProperty artistProperty() {
        return this.artist;
    }
    public StringProperty albumProperty() {
        return this.album;
    }
    public StringProperty discNumberProperty() {
        return this.discNumber;
    }
    public StringProperty discsTotalProperty() {
        return this.discsTotal;
    }
    public StringProperty yearProperty() {
        return this.year;
    }
    public StringProperty trackNumberProperty() {
        return this.trackNumber;
    }
    public StringProperty tracksTotalProperty() {
        return this.tracksTotal;
    }
    public StringProperty genreProperty() {
        return this.genre;
    }
    public StringProperty commentProperty() {
        return this.comment;
    }
    public StringProperty composerProperty() {
        return this.composer;
    }
    public ObjectProperty<TagImageList> imagesProperty() {
        return this.images;
    }

}