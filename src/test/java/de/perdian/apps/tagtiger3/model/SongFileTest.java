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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SongFileTest {

    @Test
    public void constructorWithValidFile() throws IOException {
        SongFile songFile = new SongFile(new File("src/test/resources/META-INF/mp3/Hold on a Sec.mp3"));
        Assertions.assertEquals("Hold on a Sec", songFile.getProperties().getValue(SongProperty.TITLE, String.class).getPersistedValue().getValue());
        Assertions.assertEquals("Hold on a Sec", songFile.getProperties().getValue(SongProperty.TITLE, String.class).getValue().getValue());
        Assertions.assertEquals("Free PD", songFile.getProperties().getValue(SongProperty.ALBUM, String.class).getPersistedValue().getValue());
        Assertions.assertEquals("Free PD", songFile.getProperties().getValue(SongProperty.ALBUM, String.class).getValue().getValue());
    }

    @Test
    public void dirtyFlagSetAndReset() throws IOException {
        SongFile originalSongFile = new SongFile(new File("src/test/resources/META-INF/mp3/Hold on a Sec.mp3"));
        Assertions.assertFalse(originalSongFile.getProperties().getDirty().getValue());

        originalSongFile.getProperties().getValue(SongProperty.TITLE, String.class).getValue().setValue("A new title");
        Assertions.assertEquals("Hold on a Sec", originalSongFile.getProperties().getValue(SongProperty.TITLE, String.class).getPersistedValue().getValue());
        Assertions.assertEquals("A new title", originalSongFile.getProperties().getValue(SongProperty.TITLE, String.class).getValue().getValue());
        Assertions.assertTrue(originalSongFile.getProperties().getDirty().getValue());
        Assertions.assertTrue(originalSongFile.getProperties().getValue(SongProperty.TITLE, String.class).getDirty().getValue());

        originalSongFile.getProperties().getValue(SongProperty.TITLE, String.class).getValue().setValue("Hold on a Sec");
        Assertions.assertEquals("Hold on a Sec", originalSongFile.getProperties().getValue(SongProperty.TITLE, String.class).getPersistedValue().getValue());
        Assertions.assertEquals("Hold on a Sec", originalSongFile.getProperties().getValue(SongProperty.TITLE, String.class).getValue().getValue());
        Assertions.assertFalse(originalSongFile.getProperties().getValue(SongProperty.TITLE, String.class).getDirty().getValue());
        Assertions.assertFalse(originalSongFile.getProperties().getDirty().getValue());
    }

    @Test
    public void writeNewValue() throws IOException {
        File tempSongOsFile = this.createTempSongFile();
        SongFile songFile = new SongFile(tempSongOsFile);
        Assertions.assertEquals("Hold on a Sec", songFile.getProperties().getValue(SongProperty.TITLE, String.class).getPersistedValue().getValue());
        Assertions.assertEquals("Hold on a Sec", songFile.getProperties().getValue(SongProperty.TITLE, String.class).getValue().getValue());

        songFile.getProperties().getValue(SongProperty.TITLE, String.class).getValue().setValue("This is a new title");

        Assertions.assertTrue(songFile.persistChanges());

        Assertions.assertEquals("This is a new title", songFile.getProperties().getValue(SongProperty.TITLE, String.class).getPersistedValue().getValue());
        Assertions.assertEquals("This is a new title", songFile.getProperties().getValue(SongProperty.TITLE, String.class).getValue().getValue());

        SongFile reloadedSongFile = new SongFile(tempSongOsFile);
        Assertions.assertEquals("This is a new title", reloadedSongFile.getProperties().getValue(SongProperty.TITLE, String.class).getPersistedValue().getValue());
        Assertions.assertEquals("This is a new title", reloadedSongFile.getProperties().getValue(SongProperty.TITLE, String.class).getValue().getValue());
    }

    @Test
    public void writeNewValueWithFilenameChanged() throws IOException {
        File originalOsFile = this.createTempSongFile();
        SongFile originalSongFile = new SongFile(originalOsFile);
        Assertions.assertFalse(originalSongFile.getProperties().getDirty().getValue());

        originalSongFile.getProperties().getValue(SongProperty.FILENAME, String.class).getValue().setValue("Hold on a Sec - NEW");
        originalSongFile.getProperties().getValue(SongProperty.TITLE, String.class).getValue().setValue("foo");
        Assertions.assertEquals("Hold on a Sec - TEMP", originalSongFile.getProperties().getValue(SongProperty.FILENAME, String.class).getPersistedValue().getValue());
        Assertions.assertEquals("Hold on a Sec - NEW", originalSongFile.getProperties().getValue(SongProperty.FILENAME, String.class).getValue().getValue());
        Assertions.assertEquals("Hold on a Sec", originalSongFile.getProperties().getValue(SongProperty.TITLE, String.class).getPersistedValue().getValue());
        Assertions.assertEquals("foo", originalSongFile.getProperties().getValue(SongProperty.TITLE, String.class).getValue().getValue());
        Assertions.assertTrue(originalSongFile.getProperties().getDirty().getValue());
        Assertions.assertTrue(originalSongFile.getProperties().getValue(SongProperty.FILENAME, String.class).getDirty().getValue());

        Assertions.assertTrue(originalSongFile.persistChanges());

        Assertions.assertFalse(originalOsFile.exists());
        Assertions.assertEquals("Hold on a Sec - NEW.mp3", originalSongFile.getAudioFile().getFile().getName());
        Assertions.assertEquals("Hold on a Sec - NEW", originalSongFile.getProperties().getValue(SongProperty.FILENAME, String.class).getPersistedValue().getValue());
        Assertions.assertEquals("Hold on a Sec - NEW", originalSongFile.getProperties().getValue(SongProperty.FILENAME, String.class).getValue().getValue());
        Assertions.assertFalse(originalSongFile.getProperties().getDirty().getValue());

        // Make sure that the old file hasn't been changed. It should still have its old properties
        SongFile originalSongFileReloaded = new SongFile(new File("src/test/resources/META-INF/mp3/Hold on a Sec.mp3"));
        Assertions.assertEquals("Hold on a Sec", originalSongFileReloaded.getProperties().getValue(SongProperty.TITLE, String.class).getPersistedValue().getValue());
        Assertions.assertEquals("Hold on a Sec", originalSongFileReloaded.getProperties().getValue(SongProperty.TITLE, String.class).getValue().getValue());
        Assertions.assertEquals("Hold on a Sec", originalSongFileReloaded.getProperties().getValue(SongProperty.FILENAME, String.class).getPersistedValue().getValue());
        Assertions.assertEquals("Hold on a Sec", originalSongFileReloaded.getProperties().getValue(SongProperty.FILENAME, String.class).getValue().getValue());

        // The new file however should have been changed to contain the new values
        SongFile newSongFileReloaded = new SongFile(new File("src/test/resources/META-INF/mp3/Hold on a Sec - NEW.mp3"));
        Assertions.assertEquals("foo", newSongFileReloaded.getProperties().getValue(SongProperty.TITLE, String.class).getPersistedValue().getValue());
        Assertions.assertEquals("foo", newSongFileReloaded.getProperties().getValue(SongProperty.TITLE, String.class).getValue().getValue());
        Assertions.assertEquals("Hold on a Sec - NEW", newSongFileReloaded.getProperties().getValue(SongProperty.FILENAME, String.class).getPersistedValue().getValue());
        Assertions.assertEquals("Hold on a Sec - NEW", newSongFileReloaded.getProperties().getValue(SongProperty.FILENAME, String.class).getValue().getValue());

    }

    @Test
    public void resetPropertyValues() throws IOException {
        SongFile songFile = new SongFile(this.createTempSongFile());
        Assertions.assertFalse(songFile.getProperties().getDirty().getValue());
        Assertions.assertFalse(songFile.persistChanges());

        songFile.getProperties().getValue(SongProperty.TITLE, String.class).getValue().setValue("A new title");
        Assertions.assertEquals("Hold on a Sec", songFile.getProperties().getValue(SongProperty.TITLE, String.class).getPersistedValue().getValue());
        Assertions.assertEquals("A new title", songFile.getProperties().getValue(SongProperty.TITLE, String.class).getValue().getValue());
        Assertions.assertTrue(songFile.getProperties().getDirty().getValue());
        Assertions.assertTrue(songFile.getProperties().getValue(SongProperty.TITLE, String.class).getDirty().getValue());

        songFile.getProperties().resetValues();
        Assertions.assertEquals("Hold on a Sec", songFile.getProperties().getValue(SongProperty.TITLE, String.class).getPersistedValue().getValue());
        Assertions.assertEquals("Hold on a Sec", songFile.getProperties().getValue(SongProperty.TITLE, String.class).getValue().getValue());
        Assertions.assertFalse(songFile.getProperties().getDirty().getValue());
        Assertions.assertFalse(songFile.getProperties().getValue(SongProperty.TITLE, String.class).getDirty().getValue());
        Assertions.assertFalse(songFile.persistChanges());
    }

    private File createTempSongFile() throws IOException {
        File originalSongOsFile = new File("src/test/resources/META-INF/mp3/Hold on a Sec.mp3");
        File tempSongOsFile = new File("src/test/resources/META-INF/mp3/Hold on a Sec - TEMP.mp3");
        Files.copy(originalSongOsFile.toPath(), tempSongOsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return tempSongOsFile;
    }

}
