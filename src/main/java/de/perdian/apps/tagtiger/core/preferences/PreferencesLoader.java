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
package de.perdian.apps.tagtiger.core.preferences;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PreferencesLoader {

    private static final Logger log = LoggerFactory.getLogger(PreferencesLoader.class);

    static Properties loadProperties() {
        Properties properties = new Properties();
        try {
            File preferencesFile = PreferencesLoader.resolvePreferencesFile(false);
            if (preferencesFile.exists()) {
                log.debug("Loading preferences from file: {}", preferencesFile.getAbsolutePath());
                try (InputStream preferencesStream = new BufferedInputStream(new FileInputStream(preferencesFile))) {
                    properties.load(preferencesStream);
                }
            } else {
                log.trace("No preferences file found at: {}", preferencesFile.getAbsolutePath());
            }
        } catch (Exception e) {
            log.warn("Cannot load preferences", e);
        }
        return properties;
    }

    static void writeProperties(Properties properties) {
        try {
            File preferencesFile = PreferencesLoader.resolvePreferencesFile(true);
            log.trace("Writing preferences into file: {}", preferencesFile.getAbsolutePath());
            try (OutputStream preferencesStream = new BufferedOutputStream(new FileOutputStream(preferencesFile))) {
                properties.store(preferencesStream, null);
                preferencesStream.flush();
            }
        } catch (Exception e) {
            log.warn("Cannot write preferences", e);
        }
    }

    private static File resolvePreferencesFile(boolean createIfNotExisting) throws IOException {
        File userDirectory = new File(System.getProperty("user.home"));
        File tagtigerDirectory = new File(userDirectory, ".tagtiger");
        File preferencesFile = new File(tagtigerDirectory, "preferences").getCanonicalFile();
        if (!preferencesFile.exists() && createIfNotExisting) {
            if (!preferencesFile.getParentFile().exists()) {
                preferencesFile.getParentFile().mkdirs();
            }
            preferencesFile.createNewFile();
        }
        return preferencesFile;
    }

}
