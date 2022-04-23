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
package de.perdian.apps.tagtiger.model;

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
        Assertions.assertEquals("Hold on a Sec", songFile.getAttributeValue(SongAttribute.TITLE, Object.class).getPersistedValue().getValue());
        Assertions.assertEquals("Hold on a Sec", songFile.getAttributeValue(SongAttribute.TITLE, Object.class).getValue().getValue());
        Assertions.assertEquals("Free PD", songFile.getAttributeValue(SongAttribute.ALBUM, Object.class).getPersistedValue().getValue());
        Assertions.assertEquals("Free PD", songFile.getAttributeValue(SongAttribute.ALBUM, Object.class).getValue().getValue());
    }

    @Test
    public void dirtyFlagSetAndReset() throws IOException {
        SongFile originalSongFile = new SongFile(new File("src/test/resources/META-INF/mp3/Hold on a Sec.mp3"));
        Assertions.assertFalse(originalSongFile.getDirty().getValue());

        originalSongFile.getAttributeValueProperty(SongAttribute.TITLE, String.class).setValue("A new title");
        Assertions.assertEquals("Hold on a Sec", originalSongFile.getAttributeValue(SongAttribute.TITLE, Object.class).getPersistedValue().getValue());
        Assertions.assertEquals("A new title", originalSongFile.getAttributeValue(SongAttribute.TITLE, Object.class).getValue().getValue());
        Assertions.assertTrue(originalSongFile.getDirty().getValue());
        Assertions.assertTrue(originalSongFile.getAttributeValue(SongAttribute.TITLE, Object.class).getDirty().getValue());

        originalSongFile.getAttributeValueProperty(SongAttribute.TITLE, String.class).setValue("Hold on a Sec");
        Assertions.assertEquals("Hold on a Sec", originalSongFile.getAttributeValue(SongAttribute.TITLE, Object.class).getPersistedValue().getValue());
        Assertions.assertEquals("Hold on a Sec", originalSongFile.getAttributeValue(SongAttribute.TITLE, Object.class).getValue().getValue());
        Assertions.assertFalse(originalSongFile.getAttributeValue(SongAttribute.TITLE, Object.class).getDirty().getValue());
        Assertions.assertFalse(originalSongFile.getDirty().getValue());
    }

    @Test
    public void persistChanges() throws IOException {
        File tempSongOsFile = this.createTempSongFile();
        SongFile songFile = new SongFile(tempSongOsFile);
        Assertions.assertEquals("Hold on a Sec", songFile.getAttributeValue(SongAttribute.TITLE, Object.class).getPersistedValue().getValue());
        Assertions.assertEquals("Hold on a Sec", songFile.getAttributeValue(SongAttribute.TITLE, Object.class).getValue().getValue());

        songFile.getAttributeValueProperty(SongAttribute.TITLE, String.class).setValue("This is a new title");

        Assertions.assertTrue(songFile.persistChanges());

        Assertions.assertEquals("This is a new title", songFile.getAttributeValue(SongAttribute.TITLE, Object.class).getPersistedValue().getValue());
        Assertions.assertEquals("This is a new title", songFile.getAttributeValue(SongAttribute.TITLE, Object.class).getValue().getValue());

        SongFile reloadedSongFile = new SongFile(tempSongOsFile);
        Assertions.assertEquals("This is a new title", reloadedSongFile.getAttributeValue(SongAttribute.TITLE, Object.class).getPersistedValue().getValue());
        Assertions.assertEquals("This is a new title", reloadedSongFile.getAttributeValue(SongAttribute.TITLE, Object.class).getValue().getValue());
    }

    @Test
    public void persistChangesWithFilenameChanged() throws IOException {
        File originalOsFile = this.createTempSongFile();
        SongFile originalSongFile = new SongFile(originalOsFile);
        Assertions.assertFalse(originalSongFile.getDirty().getValue());

        originalSongFile.getAttributeValueProperty(SongAttribute.FILENAME, String.class).setValue("Hold on a Sec - NEW");
        originalSongFile.getAttributeValueProperty(SongAttribute.TITLE, String.class).setValue("foo");
        Assertions.assertEquals("Hold on a Sec - TEMP", originalSongFile.getAttributeValue(SongAttribute.FILENAME, Object.class).getPersistedValue().getValue());
        Assertions.assertEquals("Hold on a Sec - NEW", originalSongFile.getAttributeValue(SongAttribute.FILENAME, Object.class).getValue().getValue());
        Assertions.assertEquals("Hold on a Sec", originalSongFile.getAttributeValue(SongAttribute.TITLE, Object.class).getPersistedValue().getValue());
        Assertions.assertEquals("foo", originalSongFile.getAttributeValue(SongAttribute.TITLE, Object.class).getValue().getValue());
        Assertions.assertTrue(originalSongFile.getDirty().getValue());
        Assertions.assertTrue(originalSongFile.getAttributeValue(SongAttribute.FILENAME, Object.class).getDirty().getValue());

        Assertions.assertTrue(originalSongFile.persistChanges());

        Assertions.assertFalse(originalOsFile.exists());
        Assertions.assertEquals("Hold on a Sec - NEW.mp3", originalSongFile.getAudioFile().getFile().getName());
        Assertions.assertEquals("Hold on a Sec - NEW", originalSongFile.getAttributeValue(SongAttribute.FILENAME, Object.class).getPersistedValue().getValue());
        Assertions.assertEquals("Hold on a Sec - NEW", originalSongFile.getAttributeValue(SongAttribute.FILENAME, Object.class).getValue().getValue());
        Assertions.assertFalse(originalSongFile.getDirty().getValue());

        // Make sure that the old file hasn't been changed. It should still have its old properties
        SongFile originalSongFileReloaded = new SongFile(new File("src/test/resources/META-INF/mp3/Hold on a Sec.mp3"));
        Assertions.assertEquals("Hold on a Sec", originalSongFileReloaded.getAttributeValue(SongAttribute.TITLE, Object.class).getPersistedValue().getValue());
        Assertions.assertEquals("Hold on a Sec", originalSongFileReloaded.getAttributeValue(SongAttribute.TITLE, Object.class).getValue().getValue());
        Assertions.assertEquals("Hold on a Sec", originalSongFileReloaded.getAttributeValue(SongAttribute.FILENAME, Object.class).getPersistedValue().getValue());
        Assertions.assertEquals("Hold on a Sec", originalSongFileReloaded.getAttributeValue(SongAttribute.FILENAME, Object.class).getValue().getValue());

        // The new file however should have been changed to contain the new values
        SongFile newSongFileReloaded = new SongFile(new File("src/test/resources/META-INF/mp3/Hold on a Sec - NEW.mp3"));
        Assertions.assertEquals("foo", newSongFileReloaded.getAttributeValue(SongAttribute.TITLE, Object.class).getPersistedValue().getValue());
        Assertions.assertEquals("foo", newSongFileReloaded.getAttributeValue(SongAttribute.TITLE, Object.class).getValue().getValue());
        Assertions.assertEquals("Hold on a Sec - NEW", newSongFileReloaded.getAttributeValue(SongAttribute.FILENAME, Object.class).getPersistedValue().getValue());
        Assertions.assertEquals("Hold on a Sec - NEW", newSongFileReloaded.getAttributeValue(SongAttribute.FILENAME, Object.class).getValue().getValue());

    }

    @Test
    public void resetChanges() throws IOException {
        SongFile songFile = new SongFile(this.createTempSongFile());
        Assertions.assertFalse(songFile.getDirty().getValue());
        Assertions.assertFalse(songFile.persistChanges());

        songFile.getAttributeValueProperty(SongAttribute.TITLE, String.class).setValue("A new title");
        Assertions.assertEquals("Hold on a Sec", songFile.getAttributeValue(SongAttribute.TITLE, Object.class).getPersistedValue().getValue());
        Assertions.assertEquals("A new title", songFile.getAttributeValue(SongAttribute.TITLE, Object.class).getValue().getValue());
        Assertions.assertTrue(songFile.getDirty().getValue());
        Assertions.assertTrue(songFile.getAttributeValue(SongAttribute.TITLE, Object.class).getDirty().getValue());

        songFile.resetChanges();;
        Assertions.assertEquals("Hold on a Sec", songFile.getAttributeValue(SongAttribute.TITLE, Object.class).getPersistedValue().getValue());
        Assertions.assertEquals("Hold on a Sec", songFile.getAttributeValue(SongAttribute.TITLE, Object.class).getValue().getValue());
        Assertions.assertFalse(songFile.getDirty().getValue());
        Assertions.assertFalse(songFile.getAttributeValue(SongAttribute.TITLE, Object.class).getDirty().getValue());
        Assertions.assertFalse(songFile.persistChanges());
    }

    private File createTempSongFile() throws IOException {
        File originalSongOsFile = new File("src/test/resources/META-INF/mp3/Hold on a Sec.mp3");
        File tempSongOsFile = new File("src/test/resources/META-INF/mp3/Hold on a Sec - TEMP.mp3");
        Files.copy(originalSongOsFile.toPath(), tempSongOsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return tempSongOsFile;
    }

}
