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
package de.perdian.apps.tagtiger.core.jobs;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobExecutor {

    private static final Logger log = LoggerFactory.getLogger(JobExecutor.class);
    private List<JobListener> listeners = new CopyOnWriteArrayList<>();
    private Executor executor = Executors.newCachedThreadPool();
    private AtomicLong jobCounter = new AtomicLong();
    private JobContext currentJobContext = null;

    /**
     * Executes the given job with a separate thread. All registered listeners
     * will be notified upon the start of the job and changes within the jobs
     * state
     *
     * @param job
     *     the job to be executed
     */
    public synchronized void executeJob(Job job) {

        // If we already have a job running, we need to make sure that it gets
        // cancelled first
        this.cancelCurrentJob();

        long jobIndex = this.getJobCounter().incrementAndGet();
        JobContext jobContext = new JobContext(job, jobIndex, this.getJobCounter(), this.getListeners());
        this.setCurrentJobContext(jobContext);

        log.trace("Executing job: {}", job);
        this.getExecutor().execute(() -> {

            this.getListeners().forEach(listener -> listener.jobStarted(job));
            job.execute(jobContext);
            this.getListeners().forEach(listener -> listener.jobCompleted(job, !jobContext.isActive()));

            synchronized (this) {
                if (this.getCurrentJobContext() == jobContext) {
                    this.setCurrentJobContext(null);
                }
            }

        });

    }

    public synchronized void cancelCurrentJob() {
        Optional.ofNullable(this.getCurrentJobContext()).ifPresent(jobContext -> jobContext.setCancelled(true));
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public void addListener(JobListener listener) {
        this.getListeners().add(listener);
    }
    List<JobListener> getListeners() {
        return this.listeners;
    }
    void setListeners(List<JobListener> listeners) {
        this.listeners = listeners;
    }

    Executor getExecutor() {
        return this.executor;
    }
    void setExecutor(Executor executor) {
        this.executor = executor;
    }

    AtomicLong getJobCounter() {
        return this.jobCounter;
    }
    void setJobCounter(AtomicLong jobCounter) {
        this.jobCounter = jobCounter;
    }

    JobContext getCurrentJobContext() {
        return this.currentJobContext;
    }
    void setCurrentJobContext(JobContext currentJobContext) {
        this.currentJobContext = currentJobContext;
    }

}