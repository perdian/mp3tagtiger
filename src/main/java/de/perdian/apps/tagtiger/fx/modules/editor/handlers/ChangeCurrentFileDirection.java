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
package de.perdian.apps.tagtiger.fx.modules.editor.handlers;

import java.util.List;

public enum ChangeCurrentFileDirection {

    FIRST {

        @Override
        <T> T resolveRecord(T currentRecord, List<T> availableRecords) {
            return availableRecords.isEmpty() ? null : availableRecords.get(0);
        }

    },

    PREVIOUS {

        @Override
        <T> T resolveRecord(T currentRecord, List<T> availableRecords) {
            return availableRecords.isEmpty() ? null : availableRecords.get(Math.max(0, availableRecords.indexOf(currentRecord) - 1));
        }

    },

    NEXT {

        @Override
        <T> T resolveRecord(T currentRecord, List<T> availableRecords) {
            return availableRecords.isEmpty() ? null : availableRecords.get(Math.min(availableRecords.size() - 1, availableRecords.indexOf(currentRecord) + 1));
        }

    },

    LAST {

        @Override
        <T> T resolveRecord(T currentRecord, List<T> availableRecords) {
            return availableRecords.isEmpty() ? null : availableRecords.get(availableRecords.size() - 1);
        }

    };

    abstract <T> T resolveRecord(T currentRecord, List<T> availableRecords);

}