/*
 * Copyright 2014-2020 Christian Seifert
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
package de.perdian.apps.tagtiger3.model;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class SongFile {

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    private SongProperties properties = null;
    private AudioFile audioFile = null;
    private BooleanProperty marker = null;

    public SongFile(File osFile) throws IOException {
        try {
            AudioFile audioFile = AudioFileIO.read(Objects.requireNonNull(osFile, "File must not be null"));
            this.setAudioFile(audioFile);
            this.setProperties(new SongProperties(audioFile));
            this.setMarker(new SimpleBooleanProperty());
        } catch (InvalidAudioFrameException | ReadOnlyFileException | TagException | CannotReadException e) {
            throw new IOException("Invalid MP3 file: " + osFile.getAbsolutePath(), e);
        }
    }

    public boolean persistChanges() throws IOException {
        if (this.getProperties().writeValues(this.getAudioFile())) {
            try {
                AudioFileIO.write(this.getAudioFile());
            } catch (CannotWriteException e) {
                throw new IOException("Cannot write MP3 tags into target file: " + this.getAudioFile().getFile().getAbsolutePath(), e);
            }
            this.getProperties().resetValuesToNewValues();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.getAudioFile().getFile().getAbsolutePath();
    }

    AudioFile getAudioFile() {
        return this.audioFile;
    }
    private void setAudioFile(AudioFile audioFile) {
        this.audioFile = audioFile;
    }

    public SongProperties getProperties() {
        return this.properties;
    }
    private void setProperties(SongProperties properties) {
        this.properties = properties;
    }

    public BooleanProperty getMarker() {
        return this.marker;
    }
    private void setMarker(BooleanProperty marker) {
        this.marker = marker;
    }

}
