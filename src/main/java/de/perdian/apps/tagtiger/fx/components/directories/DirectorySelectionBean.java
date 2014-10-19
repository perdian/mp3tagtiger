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
package de.perdian.apps.tagtiger.fx.components.directories;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a single file that is to be displayed in the
 * {@link DirectorySelectionPane}
 *
 * @author Christian Robert
 */

class DirectorySelectionBean {

    private String title = null;
    private File file = null;

    DirectorySelectionBean(File file) {
        this(file, file.getName());
    }

    DirectorySelectionBean(File file, String title) {
        this.setFile(file);
        this.setTitle(title);
    }

    /**
     * List all the root directories that are available in the current file
     * system
     */
    static List<DirectorySelectionBean> listRoots() {
        return Arrays.stream(File.listRoots()).map(file -> new DirectorySelectionBean(file, file.getAbsolutePath())).collect(Collectors.toList());
    }

    /**
     * List the direct children of the current directory
     */
    List<DirectorySelectionBean> listChildren() {
        File[] childrenArray = this.getFile() == null ? null : this.getFile().listFiles(file -> file.isDirectory() && !file.isHidden());
        if (childrenArray == null) {
            return Collections.emptyList();
        } else {
            return Arrays.stream(childrenArray).map(DirectorySelectionBean::new).collect(Collectors.toList());
        }
    }

    @Override
    public String toString() {
        return this.getTitle();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        } else {
            try {
                return (that instanceof DirectorySelectionBean) && Objects.equals(this.getFile().getCanonicalFile(), ((DirectorySelectionBean)that).getFile().getCanonicalFile());
            } catch (Exception e) {
                return (that instanceof DirectorySelectionBean) && Objects.equals(this.getFile(), ((DirectorySelectionBean)that).getFile());
            }
        }
    }

    @Override
    public int hashCode() {
        return this.getFile() == null ? 0 : this.getFile().hashCode();
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    /**
     * Gets the underlying file that is represented by this bean
     */
    public File getFile() {
        return this.file;
    }
    private void setFile(File file) {
        this.file = file;
    }

    /**
     * Gets the title under which the bean should be displayed
     */
    String getTitle() {
        return this.title;
    }
    private void setTitle(String title) {
        this.title = title;
    }

}