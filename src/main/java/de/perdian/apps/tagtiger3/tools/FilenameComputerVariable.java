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
package de.perdian.apps.tagtiger3.tools;

import de.perdian.apps.tagtiger3.model.SongAttribute;
import de.perdian.apps.tagtiger3.tools.FilenameComputerVariableResolver.NumericAttributeResolver;
import de.perdian.apps.tagtiger3.tools.FilenameComputerVariableResolver.SimpleAttributeResolver;

public enum FilenameComputerVariable {

    TITLE("Title", new SimpleAttributeResolver(SongAttribute.TITLE)),
    ARTIST("Artist", new SimpleAttributeResolver(SongAttribute.ARTIST)),
    ALBUM("Album", new SimpleAttributeResolver(SongAttribute.ALBUM)),
    ALBUM_ARTIST("Album artist", new SimpleAttributeResolver(SongAttribute.ALBUM_ARTIST)),
    YEAR("Year", new SimpleAttributeResolver(SongAttribute.YEAR)),
    TRACK_NUMBER("Track number", new NumericAttributeResolver(SongAttribute.TRACK_NUMBER, SongAttribute.TRACKS_TOTAL, "0")),
    TRACKS_TOTAL("Tracks total", new SimpleAttributeResolver(SongAttribute.TRACKS_TOTAL)),
    DISC_NUMBER("Disc number", new NumericAttributeResolver(SongAttribute.DISC_NUMBER, SongAttribute.DISCS_TOTAL, "0")),
    DISCS_TOTAL("Discs total", new SimpleAttributeResolver(SongAttribute.DISCS_TOTAL)),
    GENRE("Genre", new SimpleAttributeResolver(SongAttribute.GENRE)),
    COMMENT("Comment", new SimpleAttributeResolver(SongAttribute.COMMENT)),
    COMPOSER("Composer", new SimpleAttributeResolver(SongAttribute.COMPOSER));

    private String title = null;
    private FilenameComputerVariableResolver variableResolver = null;

    private FilenameComputerVariable(String title, FilenameComputerVariableResolver variableResolver) {
        this.setTitle(title);
        this.setVariableResolver(variableResolver);
    }

    public String getTitle() {
        return this.title;
    }
    private void setTitle(String title) {
        this.title = title;
    }

    FilenameComputerVariableResolver getVariableResolver() {
        return this.variableResolver;
    }
    private void setVariableResolver(FilenameComputerVariableResolver variableResolver) {
        this.variableResolver = variableResolver;
    }

}
