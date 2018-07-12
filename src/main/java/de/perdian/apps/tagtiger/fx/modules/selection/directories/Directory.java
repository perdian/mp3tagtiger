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
package de.perdian.apps.tagtiger.fx.modules.selection.directories;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

class Directory {

    private File file = null;
    private String title = null;

    Directory(File file, String title) {
        this.setFile(file);
        this.setTitle(title);
    }

    public List<Directory> loadChildren(FileFilter childrenFilter) {

        FileFilter listFileFilter = file -> file.isDirectory() && childrenFilter.accept(file);
        File[] listArray = this.getFile() == null ? null : this.getFile().listFiles(listFileFilter);

        return Arrays
            .stream(Optional.ofNullable(listArray).orElse(new File[0]))
            .sorted((f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()))
            .map(file -> new Directory(file, file.getName()))
            .collect(Collectors.toList());

    }

    @Override
    public String toString() {
        return this.getTitle();
    }

    @Override
    public boolean equals(Object that) {
        if(this == that) {
            return true;
        } else {
            return (that instanceof Directory) && Objects.equals(this.getFile(), ((Directory)that).getFile());
        }
    }

    @Override
    public int hashCode() {
        return this.getFile() == null ? 0 : this.getFile().hashCode();
    }

    File getFile() {
        return this.file;
    }
    private void setFile(File file) {
        this.file = file;
    }

    String getTitle() {
        return this.title;
    }
    private void setTitle(String title) {
        this.title = title;
    }

}