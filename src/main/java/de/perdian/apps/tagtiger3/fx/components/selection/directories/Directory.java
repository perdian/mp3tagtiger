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
package de.perdian.apps.tagtiger3.fx.components.selection.directories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Directory {

    private String title = null;
    private Path path = null;

    public Directory(String title, Path path) {
        this.setTitle(title);
        this.setPath(path);
    }

    @Override
    public String toString() {
        return this.getTitle();
    }

    public List<Directory> loadChildren() throws IOException {
        return Files.walk(this.getPath(), 1)
            .filter(file -> Files.isDirectory(file))
            .filter(file -> { try { return !Files.isHidden(file); } catch (Exception e) { return false; } })
            .filter(file -> !Files.isSymbolicLink(file))
            .filter(file -> file.getFileName() == null || !file.getFileName().toString().startsWith("."))
            .filter(file -> !Objects.equals(file, this.getPath()))
            .map(file -> new Directory(file.getFileName().toString(), file))
            .sorted(Directory::compareByTitle)
            .collect(Collectors.toList());
    }

    private static int compareByTitle(Directory d1, Directory d2) {
        return String.CASE_INSENSITIVE_ORDER.compare(d1.getTitle(), d2.getTitle());
    }

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
