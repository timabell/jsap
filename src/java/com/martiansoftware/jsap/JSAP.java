/*
 * Copyright (c) 2002-2004, Martian Software, Inc.
 * This file is made available under the LGPL as described in the accompanying
 * LICENSE.TXT file.
 */

package com.martiansoftware.jsap;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import com.martiansoftware.util.StringUtils;

/**
 * The core class of the JSAP (Java Simple Argument Parser) API.
 *
 * <p>A JSAP is responsible for converting an array of Strings, typically
 * received from a  command line in the main class' main() method, into a
 * collection of Objects that are retrievable by a unique ID assigned by the
 * developer.</p>
 *
 * <p>Before a JSAP parses a command line, it is configured with the Switches,
 * FlaggedOptions, and UnflaggedOptions it will accept.  As a result, the
 * developer can rest assured that if no Exceptions are thrown by the JSAP's
 * parse() method, the entire command line was parsed successfully.</p>
 *
 * <p>For example, to parse a command line with the syntax "[--verbose]
 * {-n|--number} Mynumber", the following code could be used:</p.
 *
 * <code><pre>
 * JSAP myJSAP = new JSAP();
 * myJSAP.registerParameter( new Switch( "verboseSwitch", JSAP.NO_SHORTFLAG,
 * "verbose" ) );
 * myJSAP.registerParameter( new FlaggedOption( "numberOption", new
 * IntegerStringParser(), JSAP.NO_DEFAULT,
 * JSAP.NOT_REQUIRED, 'n', "number" ) );
 * JSAPResult result = myJSAP.parse(args);
 * </pre></code>
 *
 * <p>The results of the parse could then be obtained with:</p>
 *
 * <code><pre>
 * int n = result.getInt("numberOption");
 * boolean isVerbose = result.getBoolean("verboseSwitch");
 * </pre></code>
 *
 * <h3>Generating a JSAP from ANT</h3>
 * <p>If you don't want to register all your parameters manually as shown
 * above, the JSAP API provides a custom ANT task that will generate a
 * custom JSAP subclass to suit your needs.  See
 * com.martiansoftware.jsap.ant.JSAPAntTask for details.</p>
 * <p>See the accompanying documentation for examples and further information.
 *
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see com.martiansoftware.jsap.ant.JSAPAntTask
 */
public class JSAP {

    /**
     * Map of this JSAP's AbstractParameters keyed on their unique ID.
     */
    private HashMap paramsByID = null;

    /**
     * Map of this JSAP's AbstractParameters keyed on their short flag.
     */
    private HashMap paramsByShortFlag = null;

    /**
     * Map of this JSAP's AbstractParameters keyed on their long flag.
     */
    private HashMap paramsByLongFlag = null;

    /**
     * ArrayList of this JSAP's UnflaggedOptions, in order of declaration.
     */
    private ArrayList unflaggedOptions = null;

    /**
     * ArrayList of all of this JSAP's AbstractParameters, in order of
     * declaration.
     */
    private ArrayList paramsByDeclarationOrder = null;

    /**
     * ArrayList of all of this JSAP's DefaultSources, in order of declaration.
     */
    private ArrayList defaultSources = null;

    /**
     * If not null, overrides the automatic usage info.
     */
    private String usage = null;

    /**
     * If not null, overrides the automatic help info.
     */
    private String help = null;

    /**
     * Does not have a short flag.
     *
     * @see com.martiansoftware.jsap.FlaggedOption
     * @see com.martiansoftware.jsap.UnflaggedOption
     */
    public static final char NO_SHORTFLAG = '\0';

    /**
     * Does not have a long flag.
     *
     * @see com.martiansoftware.jsap.FlaggedOption
     * @see com.martiansoftware.jsap.UnflaggedOption
     */
    public static final String NO_LONGFLAG = null;

    /**
     * The default separator for list parameters (equivalent to
     * java.io.File.pathSeparatorChar)
     *
     * @see com.martiansoftware.jsap.Option#setListSeparator(char)
     */
    public static final char DEFAULT_LISTSEPARATOR =
        java.io.File.pathSeparatorChar;

    /**
     * The parameter is required.
     *
     * @see com.martiansoftware.jsap.Option#setRequired(boolean)
     */
    public static final boolean REQUIRED = true;

    /**
     * The parameter is not required.
     *
     * @see com.martiansoftware.jsap.Option#setRequired(boolean)
     */
    public static final boolean NOT_REQUIRED = false;

    /**
     * The parameter is a list.
     *
     * @see com.martiansoftware.jsap.Option#setIsList(boolean)
     */
    public static final boolean LIST = true;

    /**
     * The parameter is not a list.
     *
     * @see com.martiansoftware.jsap.Option#setIsList(boolean)
     */
    public static final boolean NOT_LIST = false;

    /**
     * The parameter allows multiple declarations.
     *
     * @see FlaggedOption#setAllowMultipleDeclarations(boolean)
     */
    public static final boolean MULTIPLEDECLARATIONS = true;

    /**
     * The parameter does not allow multiple declarations.
     *
     * @see FlaggedOption#setAllowMultipleDeclarations(boolean)
     */
    public static final boolean NO_MULTIPLEDECLARATIONS = false;

    /**
     * The parameter consumes the command line.
     *
     * @see com.martiansoftware.jsap.UnflaggedOption#setGreedy(boolean)
     */
    public static final boolean GREEDY = true;

    /**
     * The parameter does not consume the command line.
     *
     * @see com.martiansoftware.jsap.UnflaggedOption#setGreedy(boolean)
     */
    public static final boolean NOT_GREEDY = false;

    /**
     * The parameter has no default value.
     *
     * @see com.martiansoftware.jsap.AbstractParameter#setDefault(String)
     */
    public static final String NO_DEFAULT = null;

    /**
     * The default screen width used for formatting help.
     */
    private static final int DEFAULT_SCREENWIDTH = 80;

    /**
     * Creates a new JSAP with an empty configuration.  It must be configured
     * with registerParameter() before its parse() methods may be called.
     */
    public JSAP() {
        paramsByID = new HashMap();
        paramsByShortFlag = new HashMap();
        paramsByLongFlag = new HashMap();
        unflaggedOptions = new ArrayList();
        paramsByDeclarationOrder = new ArrayList();
        defaultSources = new ArrayList();
    }

    /**
     * Sets the usage string manually, overriding the automatically-
     * generated String.  To remove the override, call setUsage(null).
     * @param usage the manually-set usage string.
     */
    public void setUsage(String usage) {
        this.usage = usage;
    }

    /**
     * Sets the help string manually, overriding the automatically-
     * generated String.  To remove the override, call setHelp(null).
     * @param help the manualy-set help string.
     */
    public void setHelp(String help) {
        this.help = help;
    }

    /**
     * A shortcut method for calling getHelp(80).
     * @see #getHelp(int)
     * @return the same as gethelp(80)
     */
    public String getHelp() {
        return (getHelp(DEFAULT_SCREENWIDTH));
    }

    /**
     * If the help text has been manually set, this method simply
     * returns it, ignoring the screenWidth parameter.  Otherwise,
     * an automatically-formatted help message is returned, tailored
     * to the specified screen width.
     * @param screenWidth the screen width (in characters) for which
     * the help text will be formatted.
     * @return complete help text for this JSAP.
     */
    public String getHelp(int screenWidth) {
        String result = help;
        if (result == null) {
            StringBuffer buf = new StringBuffer();

            // first, determine the maximum width of the usage info
            int maxUsageLength = 0;
            for (Iterator i = paramsByDeclarationOrder.iterator();
                i.hasNext();) {
                maxUsageLength =
                    Math.max(
                        maxUsageLength,
                        ((AbstractParameter) i.next()).getSyntax().length());
            }
            // now determine with width we'll wrap the help text to.
            // assume 2 leading spaces, 4 spaces after the longest usage info,
            // and 2 trailing spaces.  so we'll wrap at screenWidth - 8
            int wrapWidth = screenWidth - 8 - maxUsageLength;

            // now loop through all the params again and display their help info
            for (Iterator i = paramsByDeclarationOrder.iterator();
                i.hasNext();) {
                AbstractParameter param = (AbstractParameter) i.next();
                Iterator helpInfo =
                    StringUtils
                        .wrapToList(param.getHelp(), wrapWidth)
                        .iterator();

                buf.append("  "); // the two leading spaces
                buf.append(
                    StringUtils.padRightToWidth(
                        param.getSyntax(),
                        maxUsageLength));
                buf.append("    ");
                buf.append(
                    StringUtils.padRightToWidth(
                        (String) helpInfo.next(),
                        wrapWidth));
                buf.append("\n");

                while (helpInfo.hasNext()) {
                    buf.append(
                        StringUtils.padRightToWidth("", maxUsageLength + 6));
                    buf.append(
                        StringUtils.padRightToWidth(
                            (String) helpInfo.next(),
                            wrapWidth));
                    buf.append("\n");
                }
                if (i.hasNext()) {
                    buf.append("\n");
                }
            }
            result = buf.toString();
        }
        return (result);
    }

    /**
     * Returns an automatically generated usage description based upon this
     * JSAP's  current configuration.
     *
     * @return an automatically generated usage description based upon this
     * JSAP's current configuration.
     */
    public String getUsage() {
        String result = usage;
        if (result == null) {
            StringBuffer buf = new StringBuffer();
            for (Iterator i = paramsByDeclarationOrder.iterator();
                i.hasNext();) {
                AbstractParameter param = (AbstractParameter) i.next();
                if (buf.length() > 0) {
                    buf.append(" ");
                }
                buf.append(param.getSyntax());
            }
            result = buf.toString();
        }
        return (result);
    }

    /**
     * Returns an automatically generated usage description based upon this
     * JSAP's  current configuration.  This returns exactly the same result
     * as getUsage().
     *
     * @return an automatically generated usage description based upon this
     * JSAP's current configuration.
     */
    public String toString() {
        return (getUsage());
    }

    /**
     * Returns an IDMap associating long and short flags with their associated
     * parameters' IDs, and allowing the listing of IDs.  This is probably only
     * useful for developers creating their own DefaultSource classes.
     * @return an IDMap based upon this JSAP's current configuration.
     */
    public IDMap getIDMap() {
        ArrayList ids = new ArrayList();
        for (Iterator i = paramsByDeclarationOrder.iterator(); i.hasNext();) {
            AbstractParameter param = (AbstractParameter) i.next();
            ids.add(param.getID());
        }

        HashMap byShortFlag = new HashMap();
        for (Iterator i = paramsByShortFlag.keySet().iterator();
            i.hasNext();) {
            Character c = (Character) i.next();
            byShortFlag.put(
                c,
                ((AbstractParameter) paramsByShortFlag.get(c)).getID());
        }

        HashMap byLongFlag = new HashMap();
        for (Iterator i = paramsByLongFlag.keySet().iterator(); i.hasNext();) {
            String s = (String) i.next();
            byLongFlag.put(
                s,
                ((AbstractParameter) paramsByLongFlag.get(s)).getID());
        }

        return (new IDMap(ids, byShortFlag, byLongFlag));
    }

    /**
     * Returns the requested Switch, FlaggedOption, or UnflaggedOption with the
     * specified ID.  Depending upon what you intend to do with the result, it
     * may be necessary to re-cast the result as a Switch, FlaggedOption, or
     * UnflaggedOption as appropriate.
     *
     * @param id the ID of the requested Switch, FlaggedOption, or
     * UnflaggedOption.
     * @return the requested Switch, FlaggedOption, or UnflaggedOption, or null
     * if no AbstractParameter with the specified ID is defined in this JSAP.
     */
    public AbstractParameter getByID(String id) {
        return ((AbstractParameter) paramsByID.get(id));
    }

    /**
     * Returns the requested Switch or FlaggedOption with the specified long
     * flag. Depending upon what you intend to do with the result, it may be
     * necessary to re-cast the result as a Switch or FlaggedOption as
     * appropriate.
     *
     * @param longFlag the long flag of the requested Switch or FlaggedOption.
     * @return the requested Switch or FlaggedOption, or null if no Flagged
     * object with the specified long flag is defined in this JSAP.
     */
    public Flagged getByLongFlag(String longFlag) {
        return ((Flagged) paramsByLongFlag.get(longFlag));
    }

    /**
     * Returns the requested Switch or FlaggedOption with the specified short
     * flag. Depending upon what you intend to do with the result, it may be
     * necessary to re-cast the result as a Switch or FlaggedOption as
     * appropriate.
     *
     * @param shortFlag the short flag of the requested Switch or FlaggedOption.
     * @return the requested Switch or FlaggedOption, or null if no Flagged
     * object with the specified short flag is defined in this JSAP.
     */
    public Flagged getByShortFlag(Character shortFlag) {
        return ((Flagged) paramsByShortFlag.get(shortFlag));
    }

    /**
     * Returns the requested Switch or FlaggedOption with the specified short
     * flag. Depending upon what you intend to do with the result, it may be
     * necessary to re-cast the result as a Switch or FlaggedOption as
     * appropriate.
     *
     * @param shortFlag the short flag of the requested Switch or FlaggedOption.
     * @return the requested Switch or FlaggedOption, or null if no Flagged
     * object with the specified short flag is defined in this JSAP.
     */
    public Flagged getByShortFlag(char shortFlag) {
        return (getByShortFlag(new Character(shortFlag)));
    }

    /**
     * Returns an Iterator over all UnflaggedOptions currently registered with
     * this JSAP.
     *
     * @returns an Iterator over all UnflaggedOptions currently registered with
     * this JSAP.
     * @see java.util.Iterator
     */
    public Iterator getUnflaggedOptionsIterator() {
        return (unflaggedOptions.iterator());
    }

    /**
     * Registers a new DefaultSource with this JSAP, at the end of the current
     * DefaultSource chain, but before the defaults defined within the
     * AbstractParameters themselves.
     *
     * @param ds the DefaultSource to append to the DefaultSource chain.
     * @see com.martiansoftware.jsap.DefaultSource
     */
    public void registerDefaultSource(DefaultSource ds) {
        defaultSources.add(ds);
    }

    /**
     * Removes the specified DefaultSource from this JSAP's DefaultSource chain.
     * If this specified DefaultSource is not currently in this JSAP's
     * DefaultSource chain, this method does nothing.
     *
     * @param ds the DefaultSource to remove from the DefaultSource chain.
     */
    public void unregisterDefaultSource(DefaultSource ds) {
        defaultSources.remove(ds);
    }

    /**
     * Returns a Defaults object representing the default values defined within
     * this JSAP's AbstractParameters themselves.
     *
     * @return a Defaults object representing the default values defined within
     * this JSAP's AbstractParameters themselves.
     */
    private Defaults getSystemDefaults() {
        Defaults defaults = new Defaults();
        for (Iterator i = paramsByDeclarationOrder.iterator(); i.hasNext();) {
            AbstractParameter param = (AbstractParameter) i.next();
            defaults.setDefault(param.getID(), param.getDefault());
        }
        return (defaults);
    }

    /**
     * Merges the specified Defaults objects, only copying Default values from
     * the source to the destination if they are NOT currently defined in the
     * destination.
     *
     * @param dest the destination Defaults object into which the source should
     * be merged.
     * @param src the source Defaults object.
     */
    private void combineDefaults(Defaults dest, Defaults src) {
        if (src != null) {
            for (Iterator i = src.idIterator(); i.hasNext();) {
                String paramID = (String) i.next();
                dest.setDefaultIfNeeded(paramID, src.getDefault(paramID));
            }
        }
    }

    /**
     * Returns a Defaults object representing the merged Defaults of every
     * DefaultSource in the DefaultSource chain and the default values specified
     * in the AbstractParameters themselves.
     *
     * @param exceptionMap the ExceptionMap object within which any encountered
     * exceptions will be returned.
     * @return a Defaults object representing the Defaults of the entire JSAP.
     * @see com.martiansoftware.jsap.DefaultSource#getDefaults(IDMap)
     */
    protected Defaults getDefaults(ExceptionMap exceptionMap) {
        Defaults defaults = new Defaults();
        IDMap idMap = getIDMap();
        for (Iterator dsi = defaultSources.iterator(); dsi.hasNext();) {
            DefaultSource ds = (DefaultSource) dsi.next();
            combineDefaults(defaults, ds.getDefaults(idMap, exceptionMap));
        }
        combineDefaults(defaults, getSystemDefaults());
        return (defaults);
    }

    /**
     * Registers the specified AbstractParameter (i.e., Switch, FlaggedOption,
     * or UnflaggedOption) with this JSAP.
     *
     * <p>Registering an AbstractParameter <b>locks</b> the parameter.
     * Attempting to change its properties (ID, flags, etc.) while it is locked
     * will result in a JSAPException.  To unlock an AbstractParameter, it must
     * be unregistered from the JSAP.
     *
     * @param param the AbstractParameter to register.
     * @throws JSAPException if this AbstractParameter cannot be added. Possible
     * reasons include:
     * <ul>
     *     <li>Another AbstractParameter with the same ID has already been
     *      registered.</li>
     *  <li>You are attempting to register a Switch or FlaggedOption with
     *      neither a short nor long flag.</li>
     *  <li>You are attempting to register a Switch or FlaggedOption with a long
     *      or short flag that is already
     *  defined in this JSAP.</li>
     *  <li>You are attempting to register a second greedy UnflaggedOption</li>
     * </ul>
     */
    public void registerParameter(AbstractParameter param)
        throws JSAPException {
        String paramID = param.getID();

        if (paramsByID.containsKey(paramID)) {
            throw (
                new JSAPException(
                    "A parameter with ID '"
                        + paramID
                        + "' has already been registered."));
        }

        if (param instanceof Flagged) {
            Flagged f = (Flagged) param;
            if ((f.getShortFlagCharacter() == null)
                && (f.getLongFlag() == null)) {
                throw (
                    new JSAPException(
                        "FlaggedOption '"
                            + paramID
                            + "' has no flags defined."));
            }
            if (paramsByShortFlag.containsKey(f.getShortFlagCharacter())) {
                throw (
                    new JSAPException(
                        "A parameter with short flag '"
                            + f.getShortFlag()
                            + "' has already been registered."));
            }
            if (paramsByLongFlag.containsKey(f.getLongFlag())) {
                throw (
                    new JSAPException(
                        "A parameter with long flag '"
                            + f.getLongFlag()
                            + "' has already been registered."));
            }
        } else {
            if ((unflaggedOptions.size() > 0)
                && (((UnflaggedOption) unflaggedOptions
                    .get(unflaggedOptions.size() - 1))
                    .isGreedy())) {
                throw (
                    new JSAPException(
                        "A greedy unflagged option has already been registered;"
                            + " option '"
                            + paramID
                            + "' will never be reached."));
            }
        }

        if (param instanceof Option) {
            ((Option) param).register();
        }

        // if we got this far, it's safe to insert it.
        param.setLocked(true);
        paramsByID.put(paramID, param);
        paramsByDeclarationOrder.add(param);
        if (param instanceof Flagged) {
            Flagged f = (Flagged) param;
            if (f.getShortFlagCharacter() != null) {
                paramsByShortFlag.put(f.getShortFlagCharacter(), param);
            }
            if (f.getLongFlag() != null) {
                paramsByLongFlag.put(f.getLongFlag(), param);
            }
        } else if (param instanceof Option) {
            unflaggedOptions.add(param);
        }
    }

    /**
     * Unregisters the specified AbstractParameter (i.e., Switch, FlaggedOption,
     * or UnflaggedOption) from this JSAP.  Unregistering an AbstractParameter
     * also unlocks it, allowing changes to its properties (ID, flags, etc.).
     *
     * @param param the AbstractParameter to unregister from this JSAP.
     */
    public void unregisterParameter(AbstractParameter param) {
        if (paramsByID.containsKey(param.getID())) {

            if (param instanceof Option) {
                ((Option) param).unregister();
            }

            paramsByID.remove(param.getID());
            paramsByDeclarationOrder.remove(param);
            if (param instanceof FlaggedOption) {
                FlaggedOption fo = (FlaggedOption) param;
                paramsByShortFlag.remove(fo.getShortFlagCharacter());
                paramsByLongFlag.remove(fo.getLongFlag());
            } else if (param instanceof UnflaggedOption) {
                unflaggedOptions.remove(param);
            }
            param.setLocked(false);
        }
    }

    /**
     * Parses the specified command line array.  If no Exception is thrown, the
     * entire command line has been parsed successfully, and its results have
     * been successfully instantiated.
     *
     * @param args An array of command line arguments to parse.  This array is
     * typically provided in the application's main class' main() method.
     * @return a JSAPResult containing the resulting Objects.
     */
    public JSAPResult parse(String[] args) {
        Parser p = new Parser(this, args);
        return (p.parse());
    }

    /**
     * Parses the specified command line.  The specified command line is first
     * parsed into an array, much like the operating system does for the JVM
     * prior to calling your application's main class' main() method.  If no
     * Exception is thrown, the entire command line has been parsed
     * successfully, and its results have been successfully instantiated.
     *
     * @param cmdLine An array of command line arguments to parse.  This array
     * is typically provided in the application's main class' main() method.
     * @return a JSAPResult containing the resulting Objects.
     */
    public JSAPResult parse(String cmdLine) {
        String[] args = CommandLineTokenizer.tokenize(cmdLine);
        return (parse(args));
    }

    /**
     * Unregisters all registered AbstractParameters, allowing them to perform
     * their cleanup.
     */
    public void finalize() {
        AbstractParameter[] params =
            (AbstractParameter[]) paramsByDeclarationOrder.toArray(
                new AbstractParameter[0]);
        int paramCount = params.length;
        for (int i = 0; i < paramCount; ++i) {
            unregisterParameter(params[i]);
        }
    }

}