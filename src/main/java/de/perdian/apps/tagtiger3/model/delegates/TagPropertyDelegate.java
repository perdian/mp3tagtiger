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
package de.perdian.apps.tagtiger3.model.delegates;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;

import de.perdian.apps.tagtiger3.model.SongPropertyDelegate;

public class TagPropertyDelegate implements SongPropertyDelegate<String> {

    private FieldKey fieldKey = null;

    public TagPropertyDelegate(FieldKey fieldKey) {
        this.setFieldKey(fieldKey);
    }

    @Override
    public String readValue(AudioFile audioFile) throws IOException {
        return audioFile.getTag().getFirst(this.getFieldKey());
    }

    @Override
    public void writeValue(AudioFile audioFile, String newValue) throws IOException {
        try {
            if (StringUtils.isEmpty(newValue)) {
                audioFile.getTag().deleteField(this.getFieldKey());
            } else {
                audioFile.getTag().setField(this.getFieldKey(), newValue);
            }
        } catch (FieldDataInvalidException e) {
            throw new IOException("Cannot write MP3 tag '" + this.getFieldKey().name() + "': " + e.getMessage(), e);
        }
    }

    private FieldKey getFieldKey() {
        return this.fieldKey;
    }
    private void setFieldKey(FieldKey fieldKey) {
        this.fieldKey = fieldKey;
    }

}
