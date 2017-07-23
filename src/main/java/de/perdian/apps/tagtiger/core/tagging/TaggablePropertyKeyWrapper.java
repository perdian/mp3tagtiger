/*
 * Copyright 2014-2017 Christian Robert
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
package de.perdian.apps.tagtiger.core.tagging;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.perdian.apps.tagtiger.fx.localization.Localization;

public class TaggablePropertyKeyWrapper {

    private TaggablePropertyKey key = null;
    private String title = null;

    public static List<TaggablePropertyKeyWrapper> of(Collection<TaggablePropertyKey> keys, Localization localization) {
        return keys.stream()
            .map(key -> new TaggablePropertyKeyWrapper(key, key.getTitleKey().apply(localization)))
            .collect(Collectors.toList());
    }

    private TaggablePropertyKeyWrapper(TaggablePropertyKey key, String title) {
        this.setKey(key);
        this.setTitle(title);
    }

    @Override
    public String toString() {
        return this.getTitle();
    }

    public TaggablePropertyKey getKey() {
        return this.key;
    }
    private void setKey(TaggablePropertyKey key) {
        this.key = key;
    }

    public String getTitle() {
        return this.title;
    }
    private void setTitle(String title) {
        this.title = title;
    }

}
