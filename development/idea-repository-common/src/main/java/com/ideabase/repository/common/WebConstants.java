/* $Id: WebConstants.java 260 2008-03-19 05:38:06Z hasan $ */
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
 * $LastChangedDate: 2008-03-19 11:38:06 +0600 (Wed, 19 Mar 2008) $
 * $LastChangedRevision: 260 $
 ******************************************************************************
*/
package com.ideabase.repository.common;

import javax.security.auth.Subject;

/**
 * Keep all contants which will be used over the web context.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class WebConstants {
  /**
   * This attribute is used to store authenticated user {@see Subject} with in
   * session context.
   */
  public static final String SESSION_ATTR_USER_SUBJECT =
      Subject.class.getName();

  /**
   * {@code load_related_items} set to true or kept empty if user wants to load
   * all related objects.
   */
  public static final String PARAM_LOAD_RELATED_ITEMS = "load_related_items";

  /**
   * It is pretended that related items will be huge. so we need some pagination.
   * the offset says where to start the related items.
   */
  public static final String PARAM_OFFSET = "offset";

  /**
   * Maximum number of related items.
   */
  public static final String PARAM_MAX = "max";

  /**
   * Specify the query encoding format.
   */
  public static final String PARAM_ENCODE = "encode";

  /**
   * Authentication token
   */
  public static final String PARAM_AUTH_TOKEN = "authToken";

  /**
   * Index repository name
   */
  public static final String PARAM_INDEX_REPOSITORY = "index";

  /**
   * Multiple related item relation types can be defiend using comma separator.
   * for example: blog, user
   */
  public static final String PARAM_RELATION_TYPES = "relation_types";

  /**
   * Specify sortby fields, one more fields are defined by separating
   * {@code ,} (comma). ie. &sortby=price,vendor
   */
  public static final String PARAM_SORT_BY = "sortby";

  /**
   * Define sorted result order, by default all results are in ascending ordered.
   */
  public static final String PARAM_ORDER = "order";

  /**
   * Define selectable fields during search result.
   */
  public static final String PARAM_SELECT = "select";

  /**
   * Define except fileds during search asking for search result.
   */
  public static final String PARAM_EXCEPT = "except";

  /**
   * Use user defined seprator
   */
  public static final String PARAM_SEPARATOR = "separator";

  /**
   * Sortable fields separatoar.
   */
  public static final String SORTABLE_FIELDS_SEPARATOR = ",";
}
