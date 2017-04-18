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
package de.perdian.apps.tagtiger.fx.panels.tools.updatefilenames;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import de.perdian.apps.tagtiger.core.tagging.TaggableFile;
import de.perdian.apps.tagtiger.core.tagging.TaggablePropertyKey;
import de.perdian.apps.tagtiger.fx.localization.Localization;

enum UpdateFileNamesPlaceholder {

    TITLE("title", TaggablePropertyKey.TITLE, Localization::title, UpdateFileNamesPlaceholder::resolveDefault),
    TRACK_NUMBER("track", TaggablePropertyKey.TRACK_NUMBER, Localization::trackNumber, UpdateFileNamesPlaceholder::resolveTrackNumber),
    TRACKS("tracks", TaggablePropertyKey.TRACKS_TOTAL, Localization::tracks, UpdateFileNamesPlaceholder::resolveDefault),

    ALBUM("album", TaggablePropertyKey.ALBUM, Localization::album, UpdateFileNamesPlaceholder::resolveDefault),
    ARTIST("artist", TaggablePropertyKey.ARTIST, Localization::artist, UpdateFileNamesPlaceholder::resolveDefault),
    ALBUM_ARTIST("albumArtist", TaggablePropertyKey.ALBUM_ARTIST, Localization::albumArtist, UpdateFileNamesPlaceholder::resolveDefault),
    YEAR("year", TaggablePropertyKey.YEAR, Localization::year, UpdateFileNamesPlaceholder::resolveDefault),

    GENRE("genre", TaggablePropertyKey.GENRE, Localization::genre, UpdateFileNamesPlaceholder::resolveDefault),
    DISC_NUMBER("disc", TaggablePropertyKey.DISC_NUMBER, Localization::discNumber, UpdateFileNamesPlaceholder::resolveDefault),
    DISCS("discs", TaggablePropertyKey.DISCS_TOTAL, Localization::discs, UpdateFileNamesPlaceholder::resolveDefault),

    COMMENT("comment", TaggablePropertyKey.COMMENT, Localization::comment, UpdateFileNamesPlaceholder::resolveDefault),
    COMPOSER("composer", TaggablePropertyKey.COMPOSER, Localization::composer, UpdateFileNamesPlaceholder::resolveDefault);

    private String placeholder = null;
    private TaggablePropertyKey propertyKey = null;
    private Function<Localization, String> localizationFunction = null;
    private BiFunction<String, TaggableFile, String> resolverFunction = null;

    private UpdateFileNamesPlaceholder(String placeholder, TaggablePropertyKey propertyKey, Function<Localization, String> localizationFunction, BiFunction<String, TaggableFile, String> resolverFunction) {
        this.setPlaceholder(placeholder);
        this.setPropertyKey(propertyKey);
        this.setLocalizationFunction(localizationFunction);
        this.setResolverFunction(resolverFunction);
    }

    String resolveLocalization(Localization localization) {
        return this.getLocalizationFunction().apply(localization);
    }

    String resolveValue(TaggableFile file) {
        return Optional.ofNullable(this.getResolverFunction().apply(file.property(this.getPropertyKey()).getValue(), file)).orElse("");
    }

    private static String resolveDefault(String value, TaggableFile file) {
        return value;
    }

    private static String resolveTrackNumber(String value, TaggableFile file) {
        String tracksTotel = Optional.ofNullable(file.property(TaggablePropertyKey.TRACKS_TOTAL).getValue()).orElse("");
        StringBuilder resultValue = new StringBuilder(value);
        while (resultValue.length() < tracksTotel.length()) {
            resultValue.insert(0, "0");
        }
        return resultValue.toString();
    }

    String getPlaceholder() {
        return this.placeholder;
    }
    private void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    private TaggablePropertyKey getPropertyKey() {
        return this.propertyKey;
    }
    private void setPropertyKey(TaggablePropertyKey propertyKey) {
        this.propertyKey = propertyKey;
    }

    private Function<Localization, String> getLocalizationFunction() {
        return this.localizationFunction;
    }
    private void setLocalizationFunction(Function<Localization, String> localizationFunction) {
        this.localizationFunction = localizationFunction;
    }

    private BiFunction<String, TaggableFile, String> getResolverFunction() {
        return this.resolverFunction;
    }
    private void setResolverFunction(BiFunction<String, TaggableFile, String> resolverFunction) {
        this.resolverFunction = resolverFunction;
    }

}