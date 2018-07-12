/*
 * Copyright 2014-2018 Christian Robert
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
package de.perdian.apps.tagtiger.fx.support.jobs;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.tagtiger.core.jobs.Job;
import de.perdian.apps.tagtiger.core.jobs.JobContext;
import de.perdian.apps.tagtiger.core.selection.Selection;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.fx.localization.Localization;

public class SaveChangedFilesJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(SaveChangedFilesJob.class);
    private Selection selection;
    private Localization localization;

    public SaveChangedFilesJob(Selection selection, Localization localization) {
        this.setSelection(selection);
        this.setLocalization(localization);
    }

    @Override
    public void execute(JobContext context) {
        List<TaggableFile> changedFiles = this.getSelection().changedFilesProperty();
        if (!changedFiles.isEmpty()) {

            context.updateProgress(this.getLocalization().savingNFiles(changedFiles.size()));

            // Copy files into separate list, since they will be removed from
            // the original list by the listeners after the changed property is
            // updated
            List<TaggableFile> changedFilesCopy = this.getSelection().availableFilesProperty().stream()
                .filter(file -> changedFiles.contains(file))
                .collect(Collectors.toList());

            // Finally save the changed files
            this.executeSaveFiles(changedFilesCopy, context);

        }
    }

    private void executeSaveFiles(List<TaggableFile> changedFiles, JobContext context) {
        for (int i = 0; i < changedFiles.size() && !context.isCancelled(); i++) {

            TaggableFile file = changedFiles.get(i);
            context.updateProgress(this.getLocalization().savingFile(file.toString()), i, changedFiles.size());

            log.debug("Saving MP3 file: {}", file.toExtendedString());
            try {
                if (file.dirtyProperty().getValue()) {
                    file.writeIntoFile();
                }
            } catch (Exception e) {
                log.error("Cannot save file {}", file.toExtendedString(), e);
            }

        }
    }

    private Selection getSelection() {
        return this.selection;
    }
    private void setSelection(Selection selection) {
        this.selection = selection;
    }

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

}
