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
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TaggableFile {

    private File file = null;
    private final StringProperty fileName = new SimpleStringProperty();
    private final StringProperty fileExtension = new SimpleStringProperty();
    private final BooleanProperty changed = new SimpleBooleanProperty(false);
    private final StringProperty tagArtist = new SimpleStringProperty();
    private final StringProperty tagTitle = new SimpleStringProperty();
    private final StringProperty tagAlbum = new SimpleStringProperty();
    private final StringProperty tagCd = new SimpleStringProperty();
    private final StringProperty tagYear = new SimpleStringProperty();
    private final StringProperty tagTrackNumber = new SimpleStringProperty();
    private final StringProperty tagTracksTotal = new SimpleStringProperty();
    private final StringProperty tagGenre = new SimpleStringProperty();
    private final StringProperty tagComment = new SimpleStringProperty();
    private final StringProperty tagComposer = new SimpleStringProperty();
    private final StringProperty tagOriginalArtist = new SimpleStringProperty();
    private final StringProperty tagCopyright = new SimpleStringProperty();
    private final StringProperty tagUrl = new SimpleStringProperty();
    private final StringProperty tagCoder = new SimpleStringProperty();

    TaggableFile(File file) {
        this.setFile(file);
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

    public StringProperty getFileName() {
        return this.fileName;
    }

    public StringProperty getFileExtension() {
        return this.fileExtension;
    }

    public BooleanProperty getChanged() {
        return this.changed;
    }

    public StringProperty getTagTitle() {
        return this.tagTitle;
    }

    public StringProperty getTagArtist() {
        return this.tagArtist;
    }

    public StringProperty getTagAlbum() {
        return this.tagAlbum;
    }

    public StringProperty getTagCd() {
        return this.tagCd;
    }

    public StringProperty getTagYear() {
        return this.tagYear;
    }

    public StringProperty getTagTrackNumber() {
        return this.tagTrackNumber;
    }

    public StringProperty getTagTracksTotal() {
        return this.tagTracksTotal;
    }

    public StringProperty getTagGenre() {
        return this.tagGenre;
    }

    public StringProperty getTagComment() {
        return this.tagComment;
    }

    public StringProperty getTagComposer() {
        return this.tagComposer;
    }

    public StringProperty getTagOriginalArtist() {
        return this.tagOriginalArtist;
    }

    public StringProperty getTagCopyright() {
        return this.tagCopyright;
    }

    public StringProperty getTagUrl() {
        return this.tagUrl;
    }

    public StringProperty getTagCoder() {
        return this.tagCoder;
    }

}