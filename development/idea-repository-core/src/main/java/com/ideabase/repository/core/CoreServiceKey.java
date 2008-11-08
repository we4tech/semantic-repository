/* $Id:CoreServiceKey.java 249 2007-12-02 08:32:47Z hasan $ */
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
 * $LastChangedBy:hasan $
 * $LastChangedDate:2007-12-02 14:32:47 +0600 (Sun, 02 Dec 2007) $
 * $LastChangedRevision:249 $
 ******************************************************************************
*/
package com.ideabase.repository.core;

/**
 * Constants for Core service.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class CoreServiceKey {

  // ---------- DAO RELATED OBJECTS ---------------------
  public static final String ITEM_DAO = "itemDAO";
  public static final String ITEM_MAPPING_DAO = "itemMappingDAO";
  public static final String TERM_DAO = "termDAO";

  // ---------- CACHE RELATED OBJECTS ---------------------
  public static final String CLASS_CACHE = "classCache";
  public static final String CACHE_ADMINISTRATOR = "cacheAdministrator";
  
  // ---------- CACHE KEY RELATED CONSTANTS ---------------
  public static final String ANN_FIELD_INDEX = "index";
  public static final String ANN_FIELD_STORE = "store";
  public static final String ANN_FIELD_TERM_VECTOR = "termVector";

  public static final String ANN_INDEXABLE_INDEX = "index";
  public static final String ANN_INDEXABLE_STORE = "store";
  public static final String ANN_INDEXABLE_TERM_VECTOR = "termVector";
  public static final String REPOSITORY_ITEM_INDEX = "itemIndex1";
  public static final String REPOSITORY_ITEM_SEARCH = "itemSearch1";
  public static final String EVENT_MANAGER = "eventManager";

  // ---------- JMX Beans --------------------------------
  public static final String INDEX_ADMINISTRATION_JMX_BEAN =
                             "indexAdministrationJMXBean";

  // ---------- server beans ----------------------------
  public static final String REPOSITORY_SERVER_IMPL =
      "repositoryServerImplementation";

  private CoreServiceKey() {
    // Prevent from instantiating this class.
  }
}
