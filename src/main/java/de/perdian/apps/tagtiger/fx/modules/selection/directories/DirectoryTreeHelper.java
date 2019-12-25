/*
 * Copyright 2014-2019 Christian Seifert
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

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javafx.scene.control.TreeItem;

class DirectoryTreeHelper {

    static List<Path> computeParents(Path directory) {
        List<Path> resultList = new LinkedList<>();
        for (Path currentDirectory = directory; currentDirectory != null; currentDirectory = currentDirectory.getParent()) {
            resultList.add(0, currentDirectory);
        }
        return Collections.unmodifiableList(resultList);
    }

    static Map<Path, DirectoryTreeItem> computeRootItems() {
        Map<Path, DirectoryTreeItem> rootItems = new HashMap<>();
        for (Path rootPath : FileSystems.getDefault().getRootDirectories()) {
            rootItems.put(rootPath, new DirectoryTreeItem(rootPath, 1));
        }
        return Collections.unmodifiableMap(rootItems);
    }

    public static List<TreeItem<DirectoryPathWrapper>> computeItemsFromRoot(DirectoryTreeItem item) {
        List<TreeItem<DirectoryPathWrapper>> resultList = new LinkedList<>();
        for (TreeItem<DirectoryPathWrapper> currentItem = item; currentItem != null; currentItem = currentItem.getParent()) {
            resultList.add(0, currentItem);
        }
        return resultList;
    }

}
