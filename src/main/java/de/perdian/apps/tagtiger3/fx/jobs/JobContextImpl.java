/*
 * Copyright 2014-2020 Christian Seifert
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
package de.perdian.apps.tagtiger3.fx.jobs;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

class JobContextImpl implements JobContext {

    private boolean cancelled = false;
    private Job job = null;
    private List<JobListener> listeners = null;
    private long jobIndex = 0;
    private AtomicLong jobCounter = null;

    JobContextImpl(Job job, long jobIndex, AtomicLong jobCounter, List<JobListener> listeners) {
        this.setJob(job);
        this.setJobIndex(jobIndex);
        this.setJobCounter(jobCounter);
        this.setListeners(listeners);
    }

    @Override
    public void updateProgress(String message) {
        this.updateProgress(message, null, null);
    }

    @Override
    public void updateProgress(String message, Integer step, Integer totalSteps) {
        if(!this.isCancelled() && this.isActive()) {
            this.getListeners().forEach(listener -> listener.jobProgress(this.getJob(), message, step, totalSteps));
        }
    }

    @Override
    public boolean isActive() {
        return this.getJobIndex() == this.getJobCounter().get();
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
    void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    Job getJob() {
        return this.job;
    }
    void setJob(Job job) {
        this.job = job;
    }

    List<JobListener> getListeners() {
        return this.listeners;
    }
    void setListeners(List<JobListener> listeners) {
        this.listeners = listeners;
    }

    AtomicLong getJobCounter() {
        return this.jobCounter;
    }
    void setJobCounter(AtomicLong jobCounter) {
        this.jobCounter = jobCounter;
    }

    long getJobIndex() {
        return this.jobIndex;
    }
    void setJobIndex(long jobIndex) {
        this.jobIndex = jobIndex;
    }

}
