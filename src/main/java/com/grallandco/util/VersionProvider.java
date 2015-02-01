/*
 * Copyright (c) 2003-2015 Tugdual Grall
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grallandco.util;


import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Used to load project properties
 */
public class VersionProvider {

    private static final VersionProvider versionProvider = new VersionProvider();
    private String version;
    private String name;
    private String description;

    private VersionProvider() {
        ResourceBundle rb;
        try {
            rb = ResourceBundle.getBundle("mongodb-cb-replicator");
            version = rb.getString("project.version");
            name = rb.getString("project.name");
            description = rb.getString("project.description");

        } catch (MissingResourceException e) {
        }
    }

    public static String getVersion() {
        return versionProvider.version;
    }

    public static String getName() {
        return versionProvider.name;
    }

    public static String getDescription() {
        return versionProvider.description;
    }
}
