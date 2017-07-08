/*
 * Copyright 2014-2017 Christian Robert
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
package de.perdian.apps.tagtiger.fx.jobs;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger.core.jobs.Job;
import de.perdian.apps.tagtiger.core.jobs.JobContext;
import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.localization.Localization;
import javafx.application.Platform;

public class ChangeDirectoryJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(ChangeDirectoryJob.class);
    private File selectedDirectory = null;
    private Selection targetSelection = null;
    private Localization localization = null;

    public ChangeDirectoryJob(File selectedDirectory, Selection targetSelection, Localization localization) {
        this.setSelectedDirectory(selectedDirectory);
        this.setTargetSelection(targetSelection);
        this.setLocalization(localization);
    }

    @Override
    public void execute(JobContext context) {
        if (this.getSelectedDirectory() != null) {

            log.info("Changing current directory to: {}", this.getSelectedDirectory().getAbsolutePath());

            if (this.getSelectedDirectory() == null) {

                Platform.runLater(() -> this.getTargetSelection().availableFilesProperty().clear());

            } else {

                context.updateProgress(this.getLocalization().analyzingFilesFromDirectory(this.getSelectedDirectory().getName()), -1, -1);
                Platform.runLater(() -> this.getTargetSelection().availableFilesProperty().clear());

                List<File> sourceFiles = this.resolveSourceFiles(context);
                log.debug("Collected {} files from directory: {}", sourceFiles.size(), this.getSelectedDirectory().getName());
                context.updateProgress(this.getLocalization().startProcessingOfFiles(sourceFiles.size()), -1, -1);

                List<TaggableFile> taggableFiles = new ArrayList<>(sourceFiles.size());
                for (int i = 0; i < sourceFiles.size() && context.isActive() && !context.isCancelled(); i++) {
                    File sourceFile = sourceFiles.get(i);
                    context.updateProgress(this.getLocalization().processingFile(sourceFile.getName()), i + 1, sourceFiles.size());
                    try {
                        TaggableFile taggableFile = new TaggableFile(sourceFile);
                        taggableFile.dirtyProperty().addListener((o, oldValue, newValue) -> {
                            List<TaggableFile> targetList = this.getTargetSelection().changedFilesProperty();
                            Platform.runLater(() -> {
                                if (newValue != null && newValue.booleanValue()) {
                                    targetList.add(taggableFile);
                                } else {
                                    targetList.remove(taggableFile);
                                }
                            });
                        });
                        taggableFiles.add(taggableFile);
                    } catch (Exception e) {
                        log.warn("Cannot read file: {}", sourceFile.getAbsolutePath(), e);
                    }
                }
                log.debug("Processed {} files from directory: {}", taggableFiles.size(), this.getSelectedDirectory().getName());

                if (context.isActive() && !context.isCancelled()) {
                    Platform.runLater(() -> this.getTargetSelection().availableFilesProperty().setAll(taggableFiles));
                }

            }
        }
    }

    private List<File> resolveSourceFiles(JobContext context) {
        List<File> targetList = new ArrayList<>();
        this.appendSourceFilesFromDirectory(this.getSelectedDirectory(), targetList, true, context);
        return targetList;
    }

    private void appendSourceFilesFromDirectory(File sourceDirectory, List<File> targetFiles, boolean recursive, JobContext context) {
        File[] sourceFiles = sourceDirectory == null ? null : sourceDirectory.listFiles(new Mp3FileFilter());
        if (sourceFiles != null && context.isActive()) {
            Arrays.stream(sourceFiles).filter(file -> file.isFile()).forEach(file -> targetFiles.add(file));
            Arrays.stream(sourceFiles).filter(file -> file.isDirectory()).forEach(file -> this.appendSourceFilesFromDirectory(file, targetFiles, recursive, context));
        }
    }

    static class Mp3FileFilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            } else {
                String fileName = file.getName().toLowerCase();
                return fileName.endsWith(".mp3") || fileName.endsWith(".m4a");
            }
        }

    }

    private File getSelectedDirectory() {
        return this.selectedDirectory;
    }
    private void setSelectedDirectory(File selectedDirectory) {
        this.selectedDirectory = selectedDirectory;
    }

    private Selection getTargetSelection() {
        return this.targetSelection;
    }
    private void setTargetSelection(Selection targetSelection) {
        this.targetSelection = targetSelection;
    }

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

}
