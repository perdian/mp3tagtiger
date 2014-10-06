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
package de.perdian.apps.tagtiger.business.framework.preferences;

import java.util.Properties;

public class PreferencesLookup {

    private Properties properties = null;

    public PreferencesLookup() {
        this.setProperties(PreferencesLoader.loadProperties());
    }

    public void setString(String key, String value) {
        if (value == null) {
            if (this.getProperties().remove(key) != null) {
                PreferencesLoader.writeProperties(this.getProperties());
            }
        } else {
            String existingValue = this.getProperties().getProperty(key);
            if (!value.equals(existingValue)) {
                this.getProperties().setProperty(key, value);
                PreferencesLoader.writeProperties(this.getProperties());
            }
        }
    }

    public String getString(String key, String defaultValue) {
        return this.getProperties().getProperty(key, defaultValue);
    }

    // -------------------------------------------------------------------------
    // --- Property access methods ---------------------------------------------
    // -------------------------------------------------------------------------

    Properties getProperties() {
        return this.properties;
    }
    void setProperties(Properties properties) {
        this.properties = properties;
    }

}
