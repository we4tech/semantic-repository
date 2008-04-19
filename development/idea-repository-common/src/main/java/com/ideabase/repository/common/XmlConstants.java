/* $Id: XmlConstants.java 265 2008-04-18 09:10:48Z hasan $ */
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
 * $LastChangedBy: hasan $
 * $LastChangedDate: 2008-04-18 15:10:48 +0600 (Fri, 18 Apr 2008) $
 * $LastChangedRevision: 265 $
 ******************************************************************************
*/
package com.ideabase.repository.common;

/**
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class XmlConstants {

  public static final String ELEMENT_RESPONSE = "response";
  public static final String ELEMENT_START = "<";
  public static final String ELEMENT_END = ">";
  public static final String ELEMENT_CLOSE = "/>";
  public static final String ATTR_STATE = "state";
  public static final String ELEMENT_AUTH_TOKEN = "authToken";
  public static final String ELEMENT_MESSAGE = "message";
  public static final String BLOCK_CDATA_START = "<![CDATA[";
  public static final String BLOCK_CDATA_END = "]]>";
  public static final String ELEMENT_ID = "id";
  public static final String ELEMENT_CREATED_ON = "createdOn";
  public static final String ELEMENT_LAST_UPDATED_ON = "lastUpdatedOn";
  public static final String ELEMENT_RELATED_ITEMS = "relatedItems";
  public static final String ELEMENT_ITEM = "item";
  public static final String ELEMENT_TITLE = "title";
  public static final String ELEMENT_ITEMS = "items";
  public static final String ELEMENT_REQUEST = "request";
  public static final String ELEMENT_TARGET_CLASS = "target-class";
  public static final String TARGET_CLASS_USER = "user";
  public static final String ELEMENT_MAX_ROWS = "max-rows";
  public static final String ELEMENT_PAGES_COUNT = "page-count";
  public static final String ELEMENT_INDEX_REPOSITORY = "indexRepository";

  public static final String ELEMENT_FIELD = "field";
  public static final String TAG_META_INFO = "metaInfo";
  public static final String TAG_CONTENT = "content";
  public static final String ATTR_NAME = "name";
  public static final String GETTER_METHOD_PREFIX = "get";
  public static final String QUOTE_START = "=\"";
  public static final String QUOTE_END = "\"";
  public static final String ATTR_INDEX = "index";
  public static final String ATTR_SCORE = "score";
  public static final String ATTR_STORE = "store";
  public static final String ATTR_TERM_VECTOR = "termVector";
  public static final String ELEMENT_FIELDS = "fields";
  public static final String ATTR_TYPE = "type";
}
