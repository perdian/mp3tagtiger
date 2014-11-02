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
package de.perdian.apps.tagtiger.fx.handlers.batchupdate;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import javafx.beans.property.Property;
import de.perdian.apps.tagtiger.core.localization.Localization;
import de.perdian.apps.tagtiger.core.tagging.TaggableFile;

enum UpdateFileNamesPlaceholder {

    TITLE("title", TaggableFile::titleProperty, Localization::title, UpdateFileNamesPlaceholder::resolveDefault),
    TRACK_NUMBER("track", TaggableFile::trackNumberProperty, Localization::trackNumber, UpdateFileNamesPlaceholder::resolveTrackNumber),
    TRACKS("tracks", TaggableFile::tracksTotalProperty, Localization::tracks, UpdateFileNamesPlaceholder::resolveDefault),

    ALBUM("album", TaggableFile::albumProperty, Localization::album, UpdateFileNamesPlaceholder::resolveDefault),
    ARTIST("artist", TaggableFile::artistProperty, Localization::artist, UpdateFileNamesPlaceholder::resolveDefault),
    YEAR("year", TaggableFile::yearProperty, Localization::year, UpdateFileNamesPlaceholder::resolveDefault),

    GENRE("genre", TaggableFile::genreProperty, Localization::genre, UpdateFileNamesPlaceholder::resolveDefault),
    DISC_NUMBER("disc", TaggableFile::discNumberProperty, Localization::discNumber, UpdateFileNamesPlaceholder::resolveDefault),
    DISCS("discs", TaggableFile::discsTotalProperty, Localization::discs, UpdateFileNamesPlaceholder::resolveDefault),

    COMMENT("comment", TaggableFile::commentProperty, Localization::comment, UpdateFileNamesPlaceholder::resolveDefault),
    COMPOSER("composer", TaggableFile::composerProperty, Localization::composer, UpdateFileNamesPlaceholder::resolveDefault);

    private String placeholder = null;
    private Function<TaggableFile, Property<String>> propertyFunction = null;
    private Function<Localization, String> localizationFunction = null;
    private BiFunction<String, TaggableFile, String> resolverFunction = null;

    private UpdateFileNamesPlaceholder(String placeholder, Function<TaggableFile, Property<String>> propertyFunction, Function<Localization, String> localizationFunction, BiFunction<String, TaggableFile, String> resolverFunction) {
        this.setPlaceholder(placeholder);
        this.setPropertyFunction(propertyFunction);
        this.setLocalizationFunction(localizationFunction);
        this.setResolverFunction(resolverFunction);
    }

    String resolveLocalization(Localization localization) {
        return this.getLocalizationFunction().apply(localization);
    }

    String resolveValue(TaggableFile file) {
        return this.getResolverFunction().apply(this.getPropertyFunction().apply(file).getValue(), file);
    }

    // -------------------------------------------------------------------------
    // --- Helper functions ----------------------------------------------------
    // -------------------------------------------------------------------------

    private static String resolveDefault(String value, TaggableFile file) {
        return value;
    }

    private static String resolveTrackNumber(String value, TaggableFile file) {
        String tracksTotel = Optional.ofNullable(file.tracksTotalProperty().get()).orElse("");
        StringBuilder resultValue = new StringBuilder(value);
        while (resultValue.length() < tracksTotel.length()) {
            resultValue.insert(0, "0");
        }
        return resultValue.toString();
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    String getPlaceholder() {
        return this.placeholder;
    }
    private void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    private Function<TaggableFile, Property<String>> getPropertyFunction() {
        return this.propertyFunction;
    }
    private void setPropertyFunction(Function<TaggableFile, Property<String>> propertyFunction) {
        this.propertyFunction = propertyFunction;
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