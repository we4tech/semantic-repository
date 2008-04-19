/**
 * $Id$
 * *****************************************************************************
 *    Copyright (C) 2005 - 2007 somewhere in .Net ltd.
 *    All Rights Reserved.  No use, copying or distribution of this
 *    work may be made except in accordance with a valid license
 *    agreement from somewhere in .Net LTD.  This notice must be included on
 *    all copies, modifications and derivatives of this work.
 * *****************************************************************************
 * $LastChangedBy$
 * $LastChangedDate$
 * $LastChangedRevision$
 * *****************************************************************************
 */

package com.ideabase.repository.core.protocols.extfile;

import java.net.URLStreamHandler;
import java.net.URLConnection;
import java.net.URL;
import java.io.IOException;
import java.io.File;

/**
 * Extended file uri handler. <br>
 * previously file:///path/to/somewhere was only supported to locate some
 * native file system resource.<br>
 * now it supports -
 * ext-file:///./relative/path/to/somewhere<br>
 *
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class Handler extends URLStreamHandler {
  protected URLConnection openConnection(final URL pURL) throws IOException {
    final String path = pURL.getPath();
    final URL resourceUrl;
    if (path.startsWith("/./")) {
      // relative path
      resourceUrl = new File(path.substring(1, path.length())).toURL();
    } else {
      // absolute path
      throw new RuntimeException("Unsupported url schema. currently you can " +
          "use only extfile:///./path/to/somewhere/in");
    }

    // build url connection
    return resourceUrl.openConnection();
  }
}
