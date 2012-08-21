/*
 *
 * Copyright (C) 2005-2008 Yves Zoundi
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
package com.googlecode.vfsjfilechooser2.plaf.basic;

import java.beans.PropertyChangeListener;

import javax.swing.Action;


/**
 * That class is a clone of UIAction in some Sun's code
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @version 0.0.1
 */
public abstract class AbstractVFSUIAction implements Action
{
    private String name;

    /**
     *
     * @param name
     */
    public AbstractVFSUIAction(String name)
    {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public final String getName()
    {
        return name;
    }

    public Object getValue(String key)
    {
        if (key.equals(NAME))
        {
            return name;
        }

        return null;
    }

    // UIAction is not mutable, this does nothing.
    public void putValue(String key, Object value)
    {
    }

    // UIAction is not mutable, this does nothing.
    public void setEnabled(boolean b)
    {
    }

    /**
     * Cover method for <code>isEnabled(null)</code>.
     */
    public final boolean isEnabled()
    {
        return isEnabled(null);
    }

    /**
     * Subclasses that need to conditionalize the enabled state should
     * override this. Be aware that <code>sender</code> may be null.
     *
     * @param sender Widget enabled state is being asked for, may be null.
     * @return
     */
    public boolean isEnabled(Object sender)
    {
        return true;
    }

    // UIAction is not mutable, this does nothing.
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
    }

    // UIAction is not mutable, this does nothing.
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
    }
}
