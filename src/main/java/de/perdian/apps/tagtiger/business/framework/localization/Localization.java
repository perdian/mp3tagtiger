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
package de.perdian.apps.tagtiger.business.framework.localization;

/**
 * Interface to provide methods for accessing localized values
 *
 * @author Christian Robert
 */

public interface Localization {

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

    default String startProcessingOfFiles(int size) {
        return "Start processing of " + size + " files";
    }

    default String processingFile(String fileName) {
        return "Processing file " + fileName;
    }

    default String analyzingFilesFromDirectory(String directory) {
        return "Analyzing files from directory: " + directory;
    }

    default String cancel() {
        return "Cancel";
    }

    default String loadingDirectory(String directory) {
        return "Loading directory: " + directory;
    }

    default String directoryNotFound() {
        return "Directory not found!";
    }

    default String cannotFindDirectory(String directory) {
        return "Cannot find directory: " + directory;
    }

}