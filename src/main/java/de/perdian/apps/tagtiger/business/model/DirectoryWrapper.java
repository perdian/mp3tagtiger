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
package de.perdian.apps.tagtiger.business.model;

import java.nio.file.Path;

public class DirectoryWrapper {

    private String title = null;
    private Path path = null;

    public DirectoryWrapper(Path path, String title) {
        this.setPath(path);
        this.setTitle(title);
    }

    @Override
    public String toString() {
        return this.getTitle();
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    public String getTitle() {
        return this.title;
    }
    private void setTitle(String title) {
        this.title = title;
    }

    public Path getPath() {
        return this.path;
    }
    private void setPath(Path path) {
        this.path = path;
    }

}