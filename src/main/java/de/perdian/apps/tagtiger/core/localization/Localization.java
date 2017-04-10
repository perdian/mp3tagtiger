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
package de.perdian.apps.tagtiger.core.localization;

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
        return "ID3-TagHandler";
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

    default String fileName() {
        return "Filename";
    }

    default String changed() {
        return "Changed";
    }

    default String noFilesChanged() {
        return "No files changed";
    }

    default String nFilesChanged(Number numberOfFiles) {
        return numberOfFiles + " file(s) changed";
    }

    default String saveChangedFiles() {
        return "Saved changed file(s)";
    }

    default String savingNFiles(Number numberOfFiles) {
        return "Saving " + numberOfFiles + " file(s)";
    }

    default String savingFile(String fileName) {
        return "Saving file: " + fileName;
    }

    default String title() {
        return "Title";
    }

    default String artist() {
        return "Artist";
    }

    default String album() {
        return "Album";
    }

    default String albumArtist() {
        return "Album artist";
    }

    default String year() {
        return "Year";
    }

    default String copyToAllOtherSelectedFiles() {
        return "Copy to all other selected files";
    }

    default String comment() {
        return "Comment";
    }

    default String composer() {
        return "Composer";
    }

    default String originalArtist() {
        return "Original artist";
    }

    default String copyright() {
        return "Copyright";
    }

    default String url() {
        return "URL";
    }

    default String coder() {
        return "Coder";
    }

    default String disc() {
        return "Disc";
    }

    default String discNumber() {
        return "Disc number";
    }

    default String discs() {
        return "Discs";
    }

    default String track() {
        return "Track";
    }

    default String trackNumber() {
        return "Track number";
    }

    default String tracks() {
        return "Tracks";
    }

    default String enumerateTracks() {
        return "Enumerate tracks";
    }

    default String countTracks() {
        return "Count tracks";
    }

    default String reload() {
        return "Reload";
    }

    default String fileExtension() {
        return "Extension";
    }

    default String tags() {
        return "Tags";
    }

    default String common() {
        return "Common";
    }

    default String images() {
        return "Images";
    }

    default String genre() {
        return "Genre";
    }

    default String description() {
        return "Description";
    }

    default String mimeType() {
        return "Mime Type";
    }

    default String pictureType() {
        return "Picture type";
    }

    default String removeImage() {
        return "Remove image";
    }

    default String addImage() {
        return "Add image";
    }

    default String clearImages() {
        return "Clear images";
    }

    default String openImage() {
        return "Open image";
    }

    default String file() {
        return "File";
    }

    default String exit() {
        return "Exit";
    }

    default String firstFile() {
        return "First file";
    }

    default String previousFile() {
        return "Previous file";
    }

    default String nextFile() {
        return "Next file";
    }

    default String lastFile() {
        return "Last file";
    }

    default String actions() {
        return "Actions";
    }

    default String updateFileNames() {
        return "Update file names";
    }

    default String legend() {
        return "Legend";
    }

    default String fileNamePattern() {
        return "File name pattern";
    }

    default String newFileNames() {
        return "New file names";
    }

    default String currentFileName() {
        return "Current file name";
    }

    default String newFileName() {
        return "New file name";
    }

    default String executeRename() {
        return "Execute rename";
    }

    default String copyImages() {
        return "Copy images";
    }

    default String copyImagesToOtherFiles() {
        return "Copy images to other selected files";
    }

    default String removeAllImages() {
        return "Remove all images";
    }

    default String clearAllValues() {
        return "Clear all value";
    }

}