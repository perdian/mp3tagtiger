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
import java.io.IOException;
import java.util.Objects;

import javafx.beans.property.StringProperty;

import org.apache.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

public class TaggableFileWriter {

    static final Logger log = Logger.getLogger(TaggableFileWriter.class);

    public void writeFile(TaggableFile file, File newSystemFile) throws IOException {
        try {

            AudioFile audioFile = file.getAudioFile();
            audioFile.setFile(newSystemFile);
            audioFile.setTag(this.populateTag(audioFile.getTagOrCreateDefault(), file));

            AudioFileIO.write(audioFile);

        } catch (CannotWriteException e) {
            throw new IOException("Cannot write file: " + newSystemFile.getAbsolutePath(), e);
        } catch (TagException e) {
            throw new IOException("Cannot write file: " + newSystemFile.getAbsolutePath(), e);
        }
    }

    private Tag populateTag(Tag tag, TaggableFile file) throws TagException {
        for (FieldKey fieldKey : FieldKey.values()) {

            StringProperty fieldKeyProperty = file.getTag(fieldKey);
            String fieldKeyValue = fieldKeyProperty == null ? null : fieldKeyProperty.get();
            String existingValue = tag.getFirst(fieldKey);
            if (!Objects.equals(fieldKeyValue, existingValue)) {
                try {
                    tag.setField(fieldKey, fieldKeyValue);
                } catch (UnsupportedOperationException e) {
                    // The value cannot be saved the way we'd like it to
                } catch (Exception e) {
                    log.debug("Cannot update tag " + fieldKey + " with value: " + fieldKeyValue, e);
                }
            }

        }
        return tag;
    }

}