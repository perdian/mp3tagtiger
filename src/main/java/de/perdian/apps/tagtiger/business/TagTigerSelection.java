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

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger.business.model.MpFileWrapper;
import de.perdian.apps.tagtiger.business.tagging.MpFileWrapperFactory;

/**
 * Wrapper around a list of selected files
 *
 * @author Christian Robert
 */

public class TagTigerSelection {

    private static final Logger log = LoggerFactory.getLogger(TagTigerSelection.class);

    private AtomicLong fileLoadingIndex = null;
    private ObservableList<MpFileWrapper> availableFiles = null;
    private ObservableList<MpFileWrapper> selectedFiles = null;
    private List<SelectionProgressListener> progressListeners = null;

    TagTigerSelection() {
        this.setFileLoadingIndex(new AtomicLong());
        this.setAvailableFiles(FXCollections.observableArrayList());
        this.setSelectedFiles(FXCollections.observableArrayList());
        this.setProgressListeners(new CopyOnWriteArrayList<>());
    }

    public void loadFilesFromDirectory(Path directory) {
        new Thread(() -> this.loadFilesFromDirectoryInternal(directory)).start();
    }

    public void loadFilesFromDirectoryInternal(Path directory) {
        try {

            this.getProgressListeners().forEach(listener -> listener.startProcessing(directory));
            List<Path> sourceFiles = Files.walk(directory, FileVisitOption.FOLLOW_LINKS)
                                            .filter(path -> !Files.isDirectory(path))
                                            .filter(path -> Files.isRegularFile(path))
                                            .collect(Collectors.toList());

            log.debug("Collected {} files from directory: {}", sourceFiles.size(), directory);
            this.getProgressListeners().forEach(listener -> listener.startProcessingFiles(sourceFiles));

            MpFileWrapperFactory fileWrapperFactory = new MpFileWrapperFactory();
            List<MpFileWrapper> targetWrappers = new ArrayList<>(sourceFiles.size());
            long referenceFileLoadingIndex = this.getFileLoadingIndex().incrementAndGet();
            for (int i = 0; i < sourceFiles.size() && this.checkFileLoadingValid(referenceFileLoadingIndex); i++) {

                final int currentFileIndex = i;
                Path sourceFile = sourceFiles.get(i);
                this.getProgressListeners().forEach(listener -> listener.startProcessingFile(sourceFile, currentFileIndex, sourceFiles.size()));

                MpFileWrapper targetWrapper = fileWrapperFactory.createFileWrapper(sourceFile);
                this.getProgressListeners().forEach(listener -> listener.finishedProcessingFile(sourceFile, targetWrapper, currentFileIndex, sourceFiles.size()));
                targetWrappers.add(targetWrapper);

            }

            if(this.checkFileLoadingValid(referenceFileLoadingIndex)) {

                log.debug("Completed processing of {} source files", sourceFiles.size());
                this.getProgressListeners().forEach(listener -> listener.finishedProcessing(sourceFiles, targetWrappers));

                this.getAvailableFiles().setAll(targetWrappers);
                this.getSelectedFiles().clear();

            }

        } catch (IOException e) {
            log.warn("Cannot walk directory: {}", directory, e);
        }
    }

    private boolean checkFileLoadingValid(long referenceIndex) {
        return this.getFileLoadingIndex().get() == referenceIndex;
    }

    public void cancelFileLoading() {
        this.getFileLoadingIndex().incrementAndGet();
        this.getProgressListeners().forEach(listener -> listener.cancelProcessing());
    }

    // -------------------------------------------------------------------------
    // --- Inner classes -------------------------------------------------------
    // -------------------------------------------------------------------------

    public static interface SelectionProgressListener {

        default void startProcessing(Path directory) {
        }

        default void startProcessingFiles(List<Path> originalFiles) {
        }

        default void startProcessingFile(Path path, int index, int totalFiles) {
        }

        default void finishedProcessingFile(Path path, MpFileWrapper fileWrapper, int index, int totalFiles) {
        }

        default void finishedProcessing(List<Path> originalFiles, List<MpFileWrapper> wrappedFiles) {
        }

        default void cancelProcessing() {
        }

    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public ObservableList<MpFileWrapper> getAvailableFiles() {
        return this.availableFiles;
    }
    private void setAvailableFiles(ObservableList<MpFileWrapper> availableFiles) {
        this.availableFiles = availableFiles;
    }

    public ObservableList<MpFileWrapper> getSelectedFiles() {
        return this.selectedFiles;
    }
    private void setSelectedFiles(ObservableList<MpFileWrapper> selectedFiles) {
        this.selectedFiles = selectedFiles;
    }

    public void addProgressListener(SelectionProgressListener progressListener) {
        this.getProgressListeners().add(progressListener);
    }
    public List<SelectionProgressListener> getProgressListeners() {
        return this.progressListeners;
    }
    private void setProgressListeners(List<SelectionProgressListener> progressListeners) {
        this.progressListeners = progressListeners;
    }

    AtomicLong getFileLoadingIndex() {
        return this.fileLoadingIndex;
    }
    private void setFileLoadingIndex(AtomicLong fileLoadingIndex) {
        this.fileLoadingIndex = fileLoadingIndex;
    }

}