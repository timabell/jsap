/*
 * Copyright (c) 2002-2004, Martian Software, Inc.
 * This file is made available under the LGPL as described in the accompanying
 * LICENSE.TXT file.
 */

package com.martiansoftware.jsap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A utility class to allow lookups of parameter IDs by short flag or long flag.
 * This class is used by DefaultSource in order to populate Defaults objects.
 *
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see com.martiansoftware.jsap.Flagged
 * @see com.martiansoftware.jsap.DefaultSource
 * @see com.martiansoftware.jsap.Defaults
 */
public class IDMap {

    /**
     * A list of the unique IDs of all the parameters stored in this IDMap.
     */
    private ArrayList ids = null;

    /**
     * A HashMap associating short flags with parameter IDs.
     */
    private HashMap byShortFlag = null;

    /**
     * A HashMap associating long flags with parameter IDs.
     */
    private HashMap byLongFlag = null;

    /**
     * Creates a new IDMap.
     * @param ids an ArrayList of the unique IDs of all the parameters to store
     * in this IDMap.
     * @param byShortFlag a HashMap with keys equal to the short flags of the
     * parameters (as Character objects),
     * and values equal to the unique IDs of the parameters associated with
     * those short flags.
     * @param byLongFlag a HashMap with keys equal to the long flags of the
     * parameters (as Strings),
     * and values equal to the unique IDs of the parameters associated with
     * those short flags.
     * @see java.util.ArrayList
     * @see java.util.HashMap
     */
    public IDMap(ArrayList ids, HashMap byShortFlag, HashMap byLongFlag) {
        this.ids = (ArrayList) ids.clone();
        this.byShortFlag = (HashMap) byShortFlag.clone();
        this.byLongFlag = (HashMap) byLongFlag.clone();
    }

    /**
     * Returns an Iterator over all parameter IDs stored in this IDMap.
     * @return an Iterator over all parameter IDs stored in this IDMap.
     * @see java.util.Iterator
     */
    public Iterator idIterator() {
        return (ids.iterator());
    }

    /**
     * Returns true if the specified ID is stored in this IDMap, false if not.
     * @param id the id to search for in this IDMap
     * @return true if the specified ID is stored in this IDMap, false if not.
     */
    public boolean idExists(String id) {
        return (ids.contains(id));
    }

    /**
     * Returns the unique ID of the parameter with the specified short flag, or
     * null if the specified short flag is not defined in this IDMap.
     * @param c the short flag to search for in this IDMap.
     * @return the unique ID of the parameter with the specified short flag, or
     * null if the specified short flag is not defined in this IDMap.
     */
    public String getIDByShortFlag(Character c) {
        return ((String) byShortFlag.get(c));
    }

    /**
     * Returns the unique ID of the parameter with the specified short flag, or
     * null if the specified short flag is not defined in this IDMap.
     * @param c the short flag to search for in this IDMap.
     * @return the unique ID of the parameter with the specified short flag, or
     * null if the specified short flag is not defined in this IDMap.
     */
    public String getIDByShortFlag(char c) {
        return (getIDByShortFlag(new Character(c)));
    }

    /**
     * Returns the unique ID of the parameter with the specified long flag, or
     * null if the specified long flag is not defined in this IDMap.
     * @param s the long flag to search for in this IDMap.
     * @return the unique ID of the parameter with the specified long flag, or
     * null if the specified long flag is not defined in this IDMap.
     */
    public String getIDByLongFlag(String s) {
        return ((String) byLongFlag.get(s));
    }
}