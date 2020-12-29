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
package de.perdian.apps.tagtiger3.model;

import org.jaudiotagger.tag.FieldKey;

import de.perdian.apps.tagtiger3.model.SongPropertyDelegate.ArtworkDelegate;
import de.perdian.apps.tagtiger3.model.SongPropertyDelegate.FileNameDelegate;
import de.perdian.apps.tagtiger3.model.SongPropertyDelegate.TagPropertyDelegate;

public enum SongProperty {

    FILENAME(String.class, new FileNameDelegate()),
    TITLE(String.class, new TagPropertyDelegate(FieldKey.TITLE)),
    ARTIST(String.class, new TagPropertyDelegate(FieldKey.ARTIST)),
    ALBUM(String.class, new TagPropertyDelegate(FieldKey.ALBUM)),
    ALBUM_ARTIST(String.class, new TagPropertyDelegate(FieldKey.ALBUM_ARTIST)),
    YEAR(String.class, new TagPropertyDelegate(FieldKey.YEAR)),
    TRACK_NUMBER(String.class, new TagPropertyDelegate(FieldKey.TRACK)),
    TRACKS_TOTAL(String.class, new TagPropertyDelegate(FieldKey.TRACK_TOTAL)),
    DISC_NUMBER(String.class, new TagPropertyDelegate(FieldKey.DISC_NO)),
    DISCS_TOTAL(String.class, new TagPropertyDelegate(FieldKey.DISC_TOTAL)),
    GENRE(String.class, new TagPropertyDelegate(FieldKey.GENRE)),
    COMMENT(String.class, new TagPropertyDelegate(FieldKey.COMMENT)),
    COMPOSER(String.class, new TagPropertyDelegate(FieldKey.COMPOSER)),
    IMAGES(SongImages.class, new ArtworkDelegate());

    private SongPropertyDelegate<?> delegate = null;
    private Class<?> type = null;

    private SongProperty(Class<?> type, SongPropertyDelegate<?> delegate) {
        this.setType(type);
        this.setDelegate(delegate);
    }

    Class<?> getType() {
        return this.type;
    }
    private void setType(Class<?> type) {
        this.type = type;
    }

    SongPropertyDelegate<?> getDelegate() {
        return this.delegate;
    }
    private void setDelegate(SongPropertyDelegate<?> delegate) {
        this.delegate = delegate;
    }

}
