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
package de.perdian.apps.tagtiger.business.framework.tagging;

import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.beans.property.Property;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

public enum Tag {

    TITLE(TaggableFile::getTagTitle, ID3v1::getTitle, ID3v1::setTitle, ID3v2::getTitle, ID3v2::setTitle),
    ARTIST(TaggableFile::getTagArtist, ID3v1::getArtist, ID3v1::setArtist, ID3v2::getArtist, ID3v2::setArtist),
    ALBUM(TaggableFile::getTagAlbum, ID3v1::getAlbum, ID3v1::setAlbum, ID3v2::getAlbum, ID3v2::setAlbum),
    CD(TaggableFile::getTagCd, null, null, ID3v2::getPartOfSet, ID3v2::setPartOfSet),
    YEAR(TaggableFile::getTagYear, ID3v1::getYear, ID3v1::setYear, ID3v2::getYear, ID3v2::setYear),
    TRACK_NUMBER(TaggableFile::getTagTrackNumber, ID3v1::getTrack, ID3v1::setTrack, ID3v2::getTrack, ID3v2::setTrack);

    private Function<TaggableFile, Property<String>> propertyMethod = null;
    private Function<ID3v1, String> v1GetMethod = null;
    private BiConsumer<ID3v1, String> v1SetMethod = null;
    private Function<ID3v2, String> v2GetMethod = null;
    private BiConsumer<ID3v2, String> v2SetMethod = null;

    private Tag(Function<TaggableFile, Property<String>> propertyMethod, Function<ID3v1, String> v1GetMethod, BiConsumer<ID3v1, String> v1SetMethod, Function<ID3v2, String> v2GetMethod, BiConsumer<ID3v2, String> v2SetMethod) {
        this.setPropertyMethod(propertyMethod);
        this.setV1GetMethod(v1GetMethod);
        this.setV1SetMethod(v1SetMethod);
        this.setV2GetMethod(v2GetMethod);
        this.setV2SetMethod(v2SetMethod);
    }

    public void updateWrapper(TaggableFile fileWrapper, Mp3File mp3File) {
        String v2Value = this.getV2GetMethod() == null || mp3File.getId3v2Tag() == null ? null : this.getV2GetMethod().apply(mp3File.getId3v2Tag());
        String v1Value = this.getV1GetMethod() == null || mp3File.getId3v1Tag() == null ? null : this.getV1GetMethod().apply(mp3File.getId3v1Tag());
        this.getPropertyMethod().apply(fileWrapper).setValue(v2Value == null ? v1Value : v2Value);
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    Function<TaggableFile, Property<String>> getPropertyMethod() {
        return this.propertyMethod;
    }
    private void setPropertyMethod(Function<TaggableFile, Property<String>> propertyMethod) {
        this.propertyMethod = propertyMethod;
    }

    Function<ID3v1, String> getV1GetMethod() {
        return this.v1GetMethod;
    }
    private void setV1GetMethod(Function<ID3v1, String> v1GetMethod) {
        this.v1GetMethod = v1GetMethod;
    }

    BiConsumer<ID3v1, String> getV1SetMethod() {
        return this.v1SetMethod;
    }
    private void setV1SetMethod(BiConsumer<ID3v1, String> v1SetMethod) {
        this.v1SetMethod = v1SetMethod;
    }

    Function<ID3v2, String> getV2GetMethod() {
        return this.v2GetMethod;
    }
    private void setV2GetMethod(Function<ID3v2, String> v2GetMethod) {
        this.v2GetMethod = v2GetMethod;
    }

    BiConsumer<ID3v2, String> getV2SetMethod() {
        return this.v2SetMethod;
    }
    private void setV2SetMethod(BiConsumer<ID3v2, String> v2SetMethod) {
        this.v2SetMethod = v2SetMethod;
    }

}