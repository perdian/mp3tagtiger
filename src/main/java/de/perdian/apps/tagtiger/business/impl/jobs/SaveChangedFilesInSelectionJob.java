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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger.business.framework.jobs.Job;
import de.perdian.apps.tagtiger.business.framework.jobs.JobContext;
import de.perdian.apps.tagtiger.business.framework.localization.Localization;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFile;
import de.perdian.apps.tagtiger.business.framework.tagging.TaggableFileWriter;

public class SaveChangedFilesInSelectionJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(SaveChangedFilesInSelectionJob.class);
    private List<TaggableFile> availableFiles = null;
    private List<TaggableFile> changedFiles = null;
    private Localization localization = null;

    public SaveChangedFilesInSelectionJob(List<TaggableFile> availableFiles, List<TaggableFile> changedFiles, Localization localization) {
        this.setAvailableFiles(availableFiles);
        this.setChangedFiles(changedFiles);
        this.setLocalization(localization);
    }

    @Override
    public void execute(JobContext context) {
        List<TaggableFile> changedFiles = this.getChangedFiles();
        if (!changedFiles.isEmpty()) {

            context.updateProgress(this.getLocalization().savingNFiles(changedFiles.size()));

            // Copy files into separate list, since they will be removed from
            // the original list by the listeners after the changed property is
            // updated
            List<TaggableFile> changedFilesCopy = this.getAvailableFiles().stream()
                .filter(file -> changedFiles.contains(file))
                .collect(Collectors.toList());

            // Finally save the changed files
            this.executeSaveFiles(changedFilesCopy, context);

        }
    }

    private void executeSaveFiles(List<TaggableFile> changedFiles, JobContext context) {
        for (int i = 0; i < changedFiles.size() && !context.isCancelled(); i++) {

            TaggableFile file = changedFiles.get(i);
            context.updateProgress(this.getLocalization().savingFile(file.getFile().getName()), i, changedFiles.size());

            log.debug("Saving MP3 file: {}", file.getFile().getAbsolutePath());
            Map<TaggableFile, Exception> errorsDuringSave = new LinkedHashMap<>();
            try {
                if (file.getChanged().get()) {
                    this.executeSaveFile(file, context);
                }
            } catch (Exception e) {
                log.error("Cannot save file {}", file.getFile(), e);
                errorsDuringSave.put(file, e);
            }

        }
    }

    private void executeSaveFile(TaggableFile file, JobContext context) throws Exception {

        File currentSystemFile = file.getFile().getCanonicalFile();
        File newSystemFile = this.createNewSystemFile(currentSystemFile, file).getCanonicalFile();
        if (!newSystemFile.equals(currentSystemFile)) {
            currentSystemFile.renameTo(newSystemFile);
        }

        // Now write the MP3 tags back into the file
        TaggableFileWriter fileWriter = new TaggableFileWriter();
        fileWriter.writeFile(file, newSystemFile);

        // We're done, so mark the file as clean
        file.getChanged().set(false);

    }

    private File createNewSystemFile(File currentSystemFile, TaggableFile file) {

        StringBuilder newFileName = new StringBuilder();
        newFileName.append(file.getFileName().get());
        newFileName.append(".").append(file.getFileExtension().get());

        return new File(currentSystemFile.getParentFile(), newFileName.toString());

    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    private List<TaggableFile> getAvailableFiles() {
        return this.availableFiles;
    }
    private void setAvailableFiles(List<TaggableFile> availableFiles) {
        this.availableFiles = availableFiles;
    }

    private List<TaggableFile> getChangedFiles() {
        return this.changedFiles;
    }
    private void setChangedFiles(List<TaggableFile> changedFiles) {
        this.changedFiles = changedFiles;
    }

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

}
