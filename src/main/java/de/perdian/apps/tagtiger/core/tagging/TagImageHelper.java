/*
 * Copyright 2014-2017 Christian Seifert
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TagImageHelper {

    private static final Logger log = LoggerFactory.getLogger(TagImageHelper.class);

    static List<TagImage> loadTagImages(Tag sourceTag) {
        List<Artwork> artworkList = sourceTag.getArtworkList();
        if (artworkList == null || artworkList.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<TagImage> tagImageList = new ArrayList<>(artworkList.size());
            for (Artwork artwork : artworkList) {
                if (artwork != null && artwork.getBinaryData() != null) {
                    try {
                        tagImageList.add(new TagImage(artwork));
                    } catch (Exception e) {
                        log.debug("Cannot load tag image from artwork", e);
                    }
                }
            }
            return Collections.unmodifiableList(tagImageList);
        }
    }

}
