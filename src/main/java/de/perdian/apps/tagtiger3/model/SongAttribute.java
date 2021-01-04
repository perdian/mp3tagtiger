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

import de.perdian.apps.tagtiger3.model.SongAttributeConverter.ArtworkAttributeConverter;
import de.perdian.apps.tagtiger3.model.SongAttributeConverter.FieldKeyAttributeConverter;
import de.perdian.apps.tagtiger3.model.SongAttributeConverter.FileNameAttributeConverter;

public enum SongAttribute {

    FILENAME("File name", new FileNameAttributeConverter()),
    TITLE("Title", new FieldKeyAttributeConverter(FieldKey.TITLE)),
    ARTIST("Artist", new FieldKeyAttributeConverter(FieldKey.ARTIST)),
    ALBUM("Album", new FieldKeyAttributeConverter(FieldKey.ALBUM)),
    ALBUM_ARTIST("Album artist", new FieldKeyAttributeConverter(FieldKey.ALBUM_ARTIST)),
    YEAR("Year", new FieldKeyAttributeConverter(FieldKey.YEAR)),
    TRACK_NUMBER("Track number", new FieldKeyAttributeConverter(FieldKey.TRACK)),
    TRACKS_TOTAL("Tracks total", new FieldKeyAttributeConverter(FieldKey.TRACK_TOTAL)),
    DISC_NUMBER("Disc number", new FieldKeyAttributeConverter(FieldKey.DISC_NO)),
    DISCS_TOTAL("Discs total", new FieldKeyAttributeConverter(FieldKey.DISC_TOTAL)),
    GENRE("Genre", new FieldKeyAttributeConverter(FieldKey.GENRE)),
    COMMENT("Comment", new FieldKeyAttributeConverter(FieldKey.COMMENT)),
    COMPOSER("Composer", new FieldKeyAttributeConverter(FieldKey.COMPOSER)),
    IMAGES("Images", new ArtworkAttributeConverter());

    private String title = null;
    private SongAttributeConverter<?> converter = null;

    private <T> SongAttribute(String title, SongAttributeConverter<T> converter) {
        this.setTitle(title);
        this.setConverter(converter);
    }

    public String getTitle() {
        return this.title;
    }
    private void setTitle(String title) {
        this.title = title;
    }

    SongAttributeConverter<?> getConverter() {
        return this.converter;
    }
    private void setConverter(SongAttributeConverter<?> converter) {
        this.converter = converter;
    }

}
