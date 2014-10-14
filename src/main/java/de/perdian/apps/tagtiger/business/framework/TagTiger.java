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
package de.perdian.apps.tagtiger.business.framework;

import de.perdian.apps.tagtiger.business.framework.jobs.JobExecutor;
import de.perdian.apps.tagtiger.business.framework.localization.Localization;
import de.perdian.apps.tagtiger.business.framework.messages.MessageDistributor;
import de.perdian.apps.tagtiger.business.framework.preferences.PreferencesKey;
import de.perdian.apps.tagtiger.business.framework.preferences.PreferencesLookup;
import de.perdian.apps.tagtiger.business.framework.selection.Selection;
import de.perdian.apps.tagtiger.business.impl.jobs.DirectorySelectJob;

/**
 * Central accessor class for all non-ui specific functions. This call is
 * designed to be used as facade/delegate for all business related functions
 * that need to be called from the UI.
 *
 * @author Christian Robert
 */

public class TagTiger {

    private Localization localization = null;
    private Selection selection = null;
    private JobExecutor jobExecutor = null;
    private MessageDistributor messageDistributor = null;
    private PreferencesLookup preferences = null;

    public TagTiger() {

        JobExecutor jobExecutor = new JobExecutor();
        MessageDistributor messageDistributor = new MessageDistributor();
        PreferencesLookup preferences = new PreferencesLookup();
        Localization localization = new Localization() {};
        Selection selection = new Selection();

        this.setJobExecutor(jobExecutor);
        this.setMessageDistributor(messageDistributor);
        this.setPreferences(preferences);
        this.setLocalization(localization);
        this.setSelection(selection);

        // Add listeners
        selection.availableFilesProperty().addListener((o, oldValue, newValue) -> selection.changedFilesProperty().clear());
        selection.selectedDirectoryProperty().addListener((o, oldValue, newValue) -> jobExecutor.executeJob(new DirectorySelectJob(newValue, selection, localization, messageDistributor)));
        selection.selectedDirectoryProperty().addListener((o, oldValue, newValue) -> preferences.setString(PreferencesKey.CURRENT_DIRECTORY, newValue == null ? null : newValue.getAbsolutePath()));

    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public Localization getLocalization() {
        return this.localization;
    }
    void setLocalization(Localization localization) {
        this.localization = localization;
    }

    public Selection getSelection() {
        return this.selection;
    }
    void setSelection(Selection selection) {
        this.selection = selection;
    }

    public JobExecutor getJobExecutor() {
        return this.jobExecutor;
    }
    void setJobExecutor(JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
    }

    public MessageDistributor getMessageDistributor() {
        return this.messageDistributor;
    }
    void setMessageDistributor(MessageDistributor messageDistributor) {
        this.messageDistributor = messageDistributor;
    }

    public PreferencesLookup getPreferences() {
        return this.preferences;
    }
    void setPreferences(PreferencesLookup preferences) {
        this.preferences = preferences;
    }

}