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
package de.perdian.apps.tagtiger.business.impl.jobs;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger.business.framework.jobs.Job;
import de.perdian.apps.tagtiger.business.framework.jobs.JobContext;
import de.perdian.apps.tagtiger.business.framework.localization.Localization;
import de.perdian.apps.tagtiger.business.framework.messages.Message;
import de.perdian.apps.tagtiger.business.framework.messages.MessageDistributor;
import de.perdian.apps.tagtiger.business.framework.selection.Selection;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFileLoader;

/**
 * The job that is responsible for handling a selection that has been made upon
 * the the tree of available directories
 *
 * @author Christian Robert
 */

public class DirectorySelectJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(DirectorySelectJob.class);

    private Selection targetSelection = null;
    private File selectedDirectory = null;
    private Localization localization = null;
    private MessageDistributor messageDistributor = null;

    public DirectorySelectJob(File selectedDirectory, Selection targetSelection, Localization localization, MessageDistributor messageDistributor) {
        this.setSelectedDirectory(selectedDirectory);
        this.setTargetSelection(targetSelection);
        this.setLocalization(localization);
        this.setMessageDistributor(messageDistributor);
    }

    @Override
    public void execute(JobContext context) {
        if (!this.getSelectedDirectory().exists() || !this.getSelectedDirectory().isDirectory()) {
            Message message = new Message();
            message.setTitle(this.getLocalization().directoryNotFound());
            message.setHeaderText(this.getLocalization().directoryNotFound());
            message.setContentText(this.getLocalization().cannotFindDirectory(this.getSelectedDirectory().getAbsolutePath()));
            this.getMessageDistributor().distributeMessage(message);
        } else {
            this.executeInternal(context);
        }
    }

    private void executeInternal(JobContext context) {

        context.updateProgress(this.getLocalization().analyzingFilesFromDirectory(this.getSelectedDirectory().getName()), -1, -1);

        List<File> sourceFiles = this.resolveSourceFiles(context);

        log.debug("Collected {} files from directory: {}", sourceFiles.size(), this.getSelectedDirectory().getName());
        context.updateProgress(this.getLocalization().startProcessingOfFiles(sourceFiles.size()), -1, -1);

        TaggableFileLoader taggableFileLoader = new TaggableFileLoader(this.getTargetSelection());
        List<TaggableFile> taggableFiles = new ArrayList<>(sourceFiles.size());
        for (int i = 0; i < sourceFiles.size() && context.isActive() && !context.isCancelled(); i++) {
            File sourceFile = sourceFiles.get(i);
            context.updateProgress(this.getLocalization().processingFile(sourceFile.getName()), i + 1, sourceFiles.size());
            try {
                taggableFiles.add(taggableFileLoader.loadFile(sourceFile));
            } catch(Exception e) {
                log.warn("Cannot read file: {}", sourceFile.getAbsolutePath(), e);
            }
        }
        log.debug("Processed {} files from directory: {}", taggableFiles.size(), this.getSelectedDirectory().getName());

        if (context.isActive() && !context.isCancelled()) {
            this.getTargetSelection().updateAvailableFiles(taggableFiles);
        }

    }

    private List<File> resolveSourceFiles(JobContext context) {
        List<File> targetList = new ArrayList<>();
        this.appendSourceFilesFromDirectory(this.getSelectedDirectory(), targetList, true, context);
        return targetList;
    }

    private void appendSourceFilesFromDirectory(File sourceDirectory, List<File> targetFiles, boolean recursive, JobContext context) {
        File[] sourceFiles = sourceDirectory.listFiles(new Mp3FileFilter());
        if (sourceFiles != null && context.isActive()) {
            Arrays.stream(sourceFiles).filter(file -> file.isFile()).forEach(file -> targetFiles.add(file));
            Arrays.stream(sourceFiles).filter(file -> file.isDirectory()).forEach(file -> this.appendSourceFilesFromDirectory(file, targetFiles, recursive, context));
        }
    }

    // -------------------------------------------------------------------------
    // --- Inner classes -------------------------------------------------------
    // -------------------------------------------------------------------------

    static class Mp3FileFilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            } else {
                String fileName = file.getName().toLowerCase();
                return fileName.endsWith(".mp3");
            }
        }

    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

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

    private MessageDistributor getMessageDistributor() {
        return this.messageDistributor;
    }
    private void setMessageDistributor(MessageDistributor messageDistributor) {
        this.messageDistributor = messageDistributor;
    }

}