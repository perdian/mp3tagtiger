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
import de.perdian.apps.tagtiger.business.framework.selection.Selection;
import de.perdian.apps.tagtiger.business.impl.DirectorySelectJob;

/**
 * Central accessor class for all non-ui specific functions. This call is
 * designed to be used as facade/delegate for all business related functions
 * that need to be called from the UI.
 *
 * @author Christian Robert
 */

public class TagTiger {

    private Localization localization = new Localization() {};
    private Selection selection = new Selection();
    private JobExecutor jobExecutor = new JobExecutor();
    private MessageDistributor messageDistributor = new MessageDistributor();

    public TagTiger() {
        this.getSelection().getSelectedDirectory().addListener((observable, oldValue, newValue) -> this.getJobExecutor().executeJob(new DirectorySelectJob(newValue, this.getSelection(), this.getLocalization(), this.getMessageDistributor())));
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

}