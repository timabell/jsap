/*
 * Copyright (c) 2002-2004, Martian Software, Inc.
 * This file is made available under the LGPL as described in the accompanying
 * LICENSE.TXT file.
 */
package com.martiansoftware.jsap.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Provides support for loading JSAP configurations at runtime
 * via an xml file.  You don't need to access this class directly;
 * instead, use JSAP's constructors that support xml.
 * 
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 */
class JSAPXStream extends XStream {

	public JSAPXStream() {
		super(new DomDriver());
		alias("JSAP", JSAPConfig.class);
		alias("FlaggedOption", FlaggedOptionConfig.class);
		alias("UnflaggedOption", UnflaggedOptionConfig.class);
		alias("property", Property.class);
		alias("QualifiedSwitch", QualifiedSwitchConfig.class);
		alias("Switch", SwitchConfig.class);
	}

}
