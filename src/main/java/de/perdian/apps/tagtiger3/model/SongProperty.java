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

    FILENAME("File name", false, String.class, new FileNameDelegate()),
    TITLE("Title", true, String.class, new TagPropertyDelegate(FieldKey.TITLE)),
    ARTIST("Artist", true, String.class, new TagPropertyDelegate(FieldKey.ARTIST)),
    ALBUM("Album", true, String.class, new TagPropertyDelegate(FieldKey.ALBUM)),
    ALBUM_ARTIST("Album artist", true, String.class, new TagPropertyDelegate(FieldKey.ALBUM_ARTIST)),
    YEAR("Year", true, String.class, new TagPropertyDelegate(FieldKey.YEAR)),
    TRACK_NUMBER("Track number", true, String.class, new TagPropertyDelegate(FieldKey.TRACK)),
    TRACKS_TOTAL("Tracks total", true, String.class, new TagPropertyDelegate(FieldKey.TRACK_TOTAL)),
    DISC_NUMBER("Disc number", true, String.class, new TagPropertyDelegate(FieldKey.DISC_NO)),
    DISCS_TOTAL("Discs total", true, String.class, new TagPropertyDelegate(FieldKey.DISC_TOTAL)),
    GENRE("Genre", true, String.class, new TagPropertyDelegate(FieldKey.GENRE)),
    COMMENT("Comment", true, String.class, new TagPropertyDelegate(FieldKey.COMMENT)),
    COMPOSER("Composer", true, String.class, new TagPropertyDelegate(FieldKey.COMPOSER)),
    IMAGES("Images", false, SongImages.class, new ArtworkDelegate());

    private String title = null;
    private SongPropertyDelegate<?> delegate = null;
    private boolean replaceable = false;
    private Class<?> type = null;

    private SongProperty(String title, boolean replaceable, Class<?> type, SongPropertyDelegate<?> delegate) {
        this.setTitle(title);
        this.setReplaceable(replaceable);
        this.setType(type);
        this.setDelegate(delegate);
    }

    public String getTitle() {
        return this.title;
    }
    private void setTitle(String title) {
        this.title = title;
    }

    public boolean isReplaceable() {
        return this.replaceable;
    }
    private void setReplaceable(boolean replaceable) {
        this.replaceable = replaceable;
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
