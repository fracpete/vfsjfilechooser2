/*
 *
 * Copyright (C) 2008-2009 Yves Zoundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * under the License.
 */
package com.googlecode.vfsjfilechooser2.utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * i18n messages
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @version 0.0.3
 */
public final class VFSResources
{
    private static final String BUNDLE_BASENAME = "com.googlecode.vfsjfilechooser2.i18n.resources";
    private static ResourceBundle resourceBundle;
    private static final Logger LOG = Logger.getLogger(VFSResources.class.getName());

    static
    {
        try
        {
            loadResources();
            LOG.log(Level.INFO, "Loaded i18n resources");
        }
        catch (Exception ex)
        {
            LOG.log(Level.SEVERE, "Unable to load i18n resources", ex);
        }
    }

    // no public constructor
    private VFSResources()
    {
        throw new AssertionError("Cannot create a VFSResources object");
    }

    // load the default message translations
    private static void loadResources() throws MissingResourceException
    {
        resourceBundle = ResourceBundle.getBundle(BUNDLE_BASENAME);
    }

    /**
     * Sets the resource bundle to use
     * @param rb A resource bundle
     */
    public static synchronized void setResourceBundle(ResourceBundle rb)
    {
        if (rb == null)
        {
            throw new IllegalArgumentException(
                "ResourceBundle object musn't be null!");
        }

        resourceBundle = rb;
    }

    /**
     * Returns a translated message
     * @param messageKey The key to translate
     * @return a translated message
     */
    public static String getMessage(String messageKey)
    {
        if (messageKey == null)
        {
            return null;
        }

        synchronized (resourceBundle)
        {
            String msg = null;

            try
            {
                msg = resourceBundle.getString(messageKey);
            }
            catch (MissingResourceException ex)
            {
                msg = messageKey + " Untranslated";
                LOG.log(Level.SEVERE, "Unable to retrieve i18n key", ex);
            }

            return msg;
        }
    }
}
