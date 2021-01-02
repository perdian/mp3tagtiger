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
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.images.Artwork;

public interface SongPropertyDelegate<T> {

    T getClearValue();

    T readValue(AudioFile audioFile) throws IOException;

    void writeValue(AudioFile audioFile, T value) throws IOException;

    static class FileNameDelegate implements SongPropertyDelegate<String> {

        @Override
        public String getClearValue() {
            return "";
        }

        @Override
        public String readValue(AudioFile audioFile) throws IOException {
            String fileName = audioFile.getFile().getName();
            int fileTypeSeparatorIndex = fileName.lastIndexOf(".");
            return fileTypeSeparatorIndex < 0 ? fileName.strip() : fileName.substring(0, fileTypeSeparatorIndex).strip();
        }

        @Override
        public void writeValue(AudioFile audioFile, String newFileName) throws IOException {
            File existingFile = audioFile.getFile();
            int existingFileTypeSeparatorIndex = existingFile.getName().lastIndexOf(".");
            String existingFileExtension = existingFileTypeSeparatorIndex < 0 ? ".mp3" : existingFile.getName().substring(existingFileTypeSeparatorIndex).strip();
            String newFileNameWithExtension = cleanupFileName(newFileName.strip()) + existingFileExtension;
            File newFile = new File(existingFile.getParentFile(), newFileNameWithExtension);
            Files.move(existingFile.toPath(), newFile.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            audioFile.setFile(newFile);
        }

        private String cleanupFileName(String newFileName) {
            StringBuilder cleanFileName = new StringBuilder();
            for (char c : newFileName.toCharArray()) {
                if (c == '/') {
                    cleanFileName.append("_");
                } else {
                    cleanFileName.append(c);
                }
            }
            return cleanFileName.toString();
        }

    }

    static class TagPropertyDelegate implements SongPropertyDelegate<String> {

        private FieldKey fieldKey = null;

        public TagPropertyDelegate(FieldKey fieldKey) {
            this.setFieldKey(fieldKey);
        }

        @Override
        public String getClearValue() {
            return "";
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

    static class ArtworkDelegate implements SongPropertyDelegate<SongImages> {

        @Override
        public SongImages getClearValue() {
            return new SongImages(Collections.emptyList());
        }

        @Override
        public SongImages readValue(AudioFile audioFile) throws IOException {
            return new SongImages(audioFile);
        }

        @Override
        public void writeValue(AudioFile audioFile, SongImages value) throws IOException {
            List<Artwork> artworkList = audioFile.getTagOrCreateDefault().getArtworkList();
            artworkList.clear();
            artworkList.addAll(value.toArtworkList());
        }

    }


}
