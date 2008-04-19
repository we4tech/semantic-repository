/* $Id$ */
/*
 ******************************************************************************
 *   Copyright (C) 2007 IDEASense, (hasin & hasan) 
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************
 * $LastChangedBy$
 * $LastChangedDate$
 * $LastChangedRevision$
 ******************************************************************************
*/
package com.ideabase.repository.webservice.helper;

import com.ideabase.repository.common.exception.ServiceException;
import com.ideabase.repository.common.formatter.XmlFormatter;
import com.ideabase.repository.common.formatter.JsonFormatter;
import com.ideabase.repository.common.formatter.ResponseFormatter;
import com.ideabase.repository.common.formatter.PHPFormatter;

import java.util.List;
import java.util.ArrayList;

/**
 * Build response based on differnet response type.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class ResponseBuilder {

  private static final ResponseFormatter FORMATTER_XML = new XmlFormatter();
  private static final ResponseFormatter FORMATTER_JSON = new JsonFormatter();
  private static final ResponseFormatter FORMATTER_PHP = new PHPFormatter();

  private static final String FORMAT_XML = "xml";
  private static final String FORMAT_JSON = "json";
  private static final String FORMAT_HTML = "html";
  private static final String FORMAT_TEXT = "txt";
  private static final String FORMAT_PHP = "php";

  private final String mResponseFormat;
  private final List<Object> mResponseObjects = new ArrayList<Object>();
  private final boolean mState;

  /**
   * Default constructor, it accepts required parameter {@code mResponseFormat}.
   * @param pResponseFormat required parameter response format.
   */
  public ResponseBuilder(final String pResponseFormat, final boolean pState) {
    if (pResponseFormat == null) {
      throw new ServiceException(null, "Response format is required, null " +
                                       "value is not acceptable.");
    }
    mResponseFormat = pResponseFormat.toLowerCase();
    mState = pState;
  }

  public String getResponseFormat() {
    return mResponseFormat;
  }

  public ResponseBuilder addObject(final Object pObject) {
    mResponseObjects.add(pObject);
    return this;
  }

  public ResponseBuilder removeObject(final Object pObject) {
    mResponseObjects.remove(pObject);
    return this;
  }

  /**
   * Invoke specific formatter for spcecific response formatting type.
   * @return the formatted string is returned.
   */
  public String buildResponse() {
    // XML format;
    if (FORMAT_XML.equals(mResponseFormat)) {
      return FORMATTER_XML.format(mState, mResponseObjects);
    }
    // php format
    else if (FORMAT_PHP.equals(mResponseFormat)) {
      return FORMATTER_PHP.format(mState, mResponseObjects);
    }
    // JSON format
    else if (FORMAT_JSON.equals(mResponseFormat)) {
      return FORMATTER_JSON.format(mState, mResponseObjects);
    }
    // Raise exception for other format.
    throw new ServiceException(mResponseFormat, "No such formatter supported.");
  }
}
