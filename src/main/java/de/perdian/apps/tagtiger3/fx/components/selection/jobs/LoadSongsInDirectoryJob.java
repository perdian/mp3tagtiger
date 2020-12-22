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
package de.perdian.apps.tagtiger3.fx.components.selection.jobs;

import de.perdian.apps.tagtiger3.fx.components.selection.directories.Directory;
import de.perdian.apps.tagtiger3.fx.jobs.Job;
import de.perdian.apps.tagtiger3.fx.jobs.JobContext;
import de.perdian.apps.tagtiger3.model.SongFile;
import javafx.collections.ObservableList;

public class LoadSongsInDirectoryJob implements Job {

    private Directory directory = null;
    private ObservableList<SongFile> targetSongs = null;

    public LoadSongsInDirectoryJob(Directory directory, ObservableList<SongFile> targetSongs) {
        this.setDirectory(directory);
        this.setTargetSongs(targetSongs);
    }

    @Override
    public void execute(JobContext context) {
        context.updateProgress("Loading available songs in directory: " + this.getDirectory().getPath());
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
        }
        throw new UnsupportedOperationException();
    }

    private Directory getDirectory() {
        return this.directory;
    }
    private void setDirectory(Directory directory) {
        this.directory = directory;
    }

    private ObservableList<SongFile> getTargetSongs() {
        return this.targetSongs;
    }
    private void setTargetSongs(ObservableList<SongFile> targetSongs) {
        this.targetSongs = targetSongs;
    }

}
