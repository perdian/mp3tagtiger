/*
 * Copyright 2014-2021 Christian Seifert
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
package de.perdian.apps.tagtiger3.fx.components.tools.filenames;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

import de.perdian.apps.tagtiger3.model.SongAttribute;
import de.perdian.apps.tagtiger3.model.SongFile;

interface FilenamesToolAttributeFilenameResolver {

    Optional<String> resolveAttributeValue(Matcher filenameMatcher, SongAttribute attribute, SongFile currentFile, List<SongFile> allFiles);

    static class RegexGroupResolver implements FilenamesToolAttributeFilenameResolver {

        @Override
        public Optional<String> resolveAttributeValue(Matcher filenameMatcher, SongAttribute attribute, SongFile currentFile, List<SongFile> allFiles) {
            try {
                return Optional.of(filenameMatcher.group(attribute.name().toLowerCase()));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }

    }

}
