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
package de.perdian.apps.tagtiger.fx.panels;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class DirectoryTreeFile {

    private String title = null;
    private File file = null;

    DirectoryTreeFile(File file, String title) {
        this.setFile(file);
        this.setTitle(title);
    }

    static List<DirectoryTreeFile> listRoots() {
        return Arrays.stream(File.listRoots()).map(file -> new DirectoryTreeFile(file, file.getAbsolutePath())).collect(Collectors.toList());
    }

    List<DirectoryTreeFile> listChildren() {
        File[] childrenArray = this.getFile().listFiles(new DirectoryFileFilter());
        if (childrenArray == null) {
            return Collections.emptyList();
        } else {
            return Arrays.stream(childrenArray).map(file -> new DirectoryTreeFile(file, file.getName())).collect(Collectors.toList());
        }
    }

    @Override
    public String toString() {
        return this.getTitle();
    }

    // -------------------------------------------------------------------------
    // --- Inner classes -------------------------------------------------------
    // -------------------------------------------------------------------------

    static class DirectoryFileFilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            return file.isDirectory() && !file.isHidden();
        }

    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    String getTitle() {
        return this.title;
    }
    private void setTitle(String title) {
        this.title = title;
    }

    File getFile() {
        return this.file;
    }
    private void setFile(File file) {
        this.file = file;
    }

}