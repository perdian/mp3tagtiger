/*
 * Copyright 2014-2018 Christian Seifert
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
package de.perdian.apps.tagtiger3.fx.jobs.listeners;

import de.perdian.apps.tagtiger3.fx.jobs.Job;
import de.perdian.apps.tagtiger3.fx.jobs.JobListener;
import javafx.application.Platform;
import javafx.beans.property.Property;

public class DisableWhileJobRunningJobListener implements JobListener {

    private Property<Boolean> disableProperty = null;

    public DisableWhileJobRunningJobListener(Property<Boolean> disableProperty) {
        this.setDisableProperty(disableProperty);
    }

    @Override
    public void jobStarted(Job job) {
        Platform.runLater(() -> this.getDisableProperty().setValue(Boolean.TRUE));
    }

    @Override
    public void jobCompleted(Job job, boolean otherJobsActive) {
        if (!otherJobsActive) {
            Platform.runLater(() -> this.getDisableProperty().setValue(Boolean.FALSE));
        }
    }

    private Property<Boolean> getDisableProperty() {
        return this.disableProperty;
    }
    private void setDisableProperty(Property<Boolean> disableProperty) {
        this.disableProperty = disableProperty;
    }

}
