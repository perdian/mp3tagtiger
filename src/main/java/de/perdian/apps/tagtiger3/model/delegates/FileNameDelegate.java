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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.jaudiotagger.audio.AudioFile;

import de.perdian.apps.tagtiger3.model.SongPropertyDelegate;

public class FileNameDelegate implements SongPropertyDelegate<String> {

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
        Files.copy(existingFile.toPath(), newFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
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
