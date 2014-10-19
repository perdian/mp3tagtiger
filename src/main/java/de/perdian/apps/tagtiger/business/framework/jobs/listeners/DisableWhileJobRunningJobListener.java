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
package de.perdian.apps.tagtiger.business.framework.jobs.listeners;

import java.util.function.Consumer;

import javafx.application.Platform;
import de.perdian.apps.tagtiger.business.framework.jobs.Job;
import de.perdian.apps.tagtiger.business.framework.jobs.JobListener;

public class DisableWhileJobRunningJobListener implements JobListener {

    private Consumer<Boolean> consumer = null;

    public DisableWhileJobRunningJobListener(Consumer<Boolean> consumer) {
        this.setConsumer(consumer);
    }

    @Override
    public void jobStarted(Job job) {
        Platform.runLater(() -> this.getConsumer().accept(Boolean.TRUE));
    }

    @Override
    public void jobCompleted(Job job, boolean otherJobsActive) {
        if (!otherJobsActive) {
            Platform.runLater(() -> this.getConsumer().accept(Boolean.FALSE));
        }
    }

    // ---------------------------------------------------------------------
    // --- Property access methods -----------------------------------------
    // ---------------------------------------------------------------------

    private Consumer<Boolean> getConsumer() {
        return this.consumer;
    }
    private void setConsumer(Consumer<Boolean> consumer) {
        this.consumer = consumer;
    }

}