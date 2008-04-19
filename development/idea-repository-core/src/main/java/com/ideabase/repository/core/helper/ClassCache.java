/* $Id:ClassCache.java 249 2007-12-02 08:32:47Z hasan $ */
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
package com.ideabase.repository.core.helper;

import com.ideabase.repository.common.exception.ServiceException;

import java.util.Map;
import java.util.HashMap;

/**
 * Cache the field structure of a class. store annotation value as well.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class ClassCache {

  private static final Map<Class, CacheModel> mCacheMap =
      new HashMap<Class, CacheModel>();

  /**
   * Retrieve {@code CacheModel} based on specified {@code Class}.
   * @param pClass which cache model will be returned.
   * @return {@code CacheModel} object instance is returned. if nothing found
   *         null is returned.
   */
  public CacheModel get(final Class pClass) {
    return mCacheMap.get(pClass);
  }

  /**
   * Add {@code CacheModel} for the specified class.
   * @param pClass class which cache model is specified.
   * @param pCacheModel cache model for specified class.
   */
  public void add(final Class pClass,
                                 final CacheModel pCacheModel) {
    mCacheMap.put(pClass,  pCacheModel);
  }

  /**
   * Remove {@code CacheModel} for specified class.
   * @param pClass specified class.
   */
  public void remove(final Class pClass) {
    mCacheMap.remove(pClass);
  }

  /**
   * Internal representation of a class structure.
   */
  public static class CacheModel implements Vistable {
    /**
     * Field name and Annotation name and value is inclded.
     */
    private Map<String, Map<String, String>> mFields =
        new HashMap<String, Map<String, String>>();

    /**
     * Add a new Field with annotated value.
     */
    public CacheModel addField(final String pField,
                               final Map<String, String> pAnnotationMetaInfo) {
      mFields.put(pField, pAnnotationMetaInfo);
      return this;
    }

    /**
     * Remove a field from internal map.
     */
    public CacheModel removeField(final String pField) {
      mFields.remove(pField);
      return this;
    }

    /**
     * Return all enlisted fields.
     * @return all enlisted fields.
     */
    public Map<String, Map<String, String>> getFields() {
      return mFields;
    }

    // ----------- Visitable implementation
    public void acceptVisitor(final Visitor pVisitor) {
      if (pVisitor == null) {
        throw new ServiceException(pVisitor,
                                   "Visitor is null, which is not allowed.");
      }
      for (final Map.Entry<String,Map<String, String>> fieldEntry
          : mFields.entrySet()) {
        pVisitor.visitField(fieldEntry.getKey(), fieldEntry.getValue());
      }
    }
  }

  /**
   * All visitable objects are implementing this interface.
   */
  public static interface Vistable {
    void acceptVisitor(final Visitor pVisitor);
  }

  /**
   * Visitor interface, it allows visitor to visit only the field with
   * annotation meta information.
   */
  public static interface Visitor {
    void visitField(final String pField,
                    final Map<String, String> pAnnotationInfo);
  }
}
