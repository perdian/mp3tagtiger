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
package de.perdian.apps.tagtiger.business;

/**
 * Interface to provide methods for accessing localized values
 *
 * @author Christian Robert
 */

public interface TagTigerLocalization {

    default String applicationTitle() {
        return "MP3 TagTiger";
    }

    default String id3Tag() {
        return "ID3-Tag";
    }

    default String mp3File() {
        return "MP3-File";
    }

    default String selectFiles() {
        return "Select files";
    }

    default String noFilesSelectedYet() {
        return "No files selected yet";
    }

}