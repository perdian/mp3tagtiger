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
package de.perdian.apps.tagtiger.fx.components.tools.filenames;

import de.perdian.apps.tagtiger.fx.components.tools.filenames.FilenamesToolAttributeFilenameResolver.RegexGroupResolver;
import de.perdian.apps.tagtiger.fx.components.tools.filenames.FilenamesToolAttributeSongFileResolver.NumericResolver;
import de.perdian.apps.tagtiger.fx.components.tools.filenames.FilenamesToolAttributeSongFileResolver.SimpleResolver;
import de.perdian.apps.tagtiger.model.SongAttribute;

public enum FilenamesToolAttribute {

    TITLE(SongAttribute.TITLE, new SimpleResolver(), new RegexGroupResolver()),
    ARTIST(SongAttribute.ARTIST, new SimpleResolver(), new RegexGroupResolver()),
    ALBUM(SongAttribute.ALBUM, new SimpleResolver(), new RegexGroupResolver()),
    ALBUMARTIST(SongAttribute.ALBUM_ARTIST, new SimpleResolver(), new RegexGroupResolver()),
    YEAR(SongAttribute.YEAR, new SimpleResolver(), new RegexGroupResolver()),
    TRACKNUMBER(SongAttribute.TRACK_NUMBER, new NumericResolver(SongAttribute.TRACKS_TOTAL, "0"), new RegexGroupResolver()),
    TRACKSTOTAL(SongAttribute.TRACKS_TOTAL, new SimpleResolver(), new RegexGroupResolver()),
    DISCNUMBER(SongAttribute.DISC_NUMBER, new NumericResolver(SongAttribute.DISCS_TOTAL, "0"), new RegexGroupResolver()),
    DISCSTOTAL(SongAttribute.DISCS_TOTAL, new SimpleResolver(), new RegexGroupResolver()),
    GENRE(SongAttribute.GENRE, new SimpleResolver(), new RegexGroupResolver()),
    COMMENT(SongAttribute.COMMENT, new SimpleResolver(), new RegexGroupResolver()),
    COMPOSER(SongAttribute.COMPOSER, new SimpleResolver(), new RegexGroupResolver()),
    FILENAME(SongAttribute.FILENAME, new SimpleResolver(), null);

    private SongAttribute attribute = null;
    private FilenamesToolAttributeSongFileResolver songFileResolver = null;
    private FilenamesToolAttributeFilenameResolver filenameResolver = null;

    private FilenamesToolAttribute(SongAttribute attribute, FilenamesToolAttributeSongFileResolver songFileResolver, FilenamesToolAttributeFilenameResolver filenameResolver) {
        this.setAttribute(attribute);
        this.setSongFileResolver(songFileResolver);
        this.setFilenameResolver(filenameResolver);
    }

    public SongAttribute getAttribute() {
        return this.attribute;
    }
    private void setAttribute(SongAttribute attribute) {
        this.attribute = attribute;
    }

    public FilenamesToolAttributeSongFileResolver getSongFileResolver() {
        return this.songFileResolver;
    }
    private void setSongFileResolver(FilenamesToolAttributeSongFileResolver songFileResolver) {
        this.songFileResolver = songFileResolver;
    }

    public FilenamesToolAttributeFilenameResolver getFilenameResolver() {
        return this.filenameResolver;
    }
    private void setFilenameResolver(FilenamesToolAttributeFilenameResolver filenameResolver) {
        this.filenameResolver = filenameResolver;
    }

}
