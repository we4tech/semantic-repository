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

package com.ideabase.repository.api;

/**
 * Object access related statistics
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public interface ServiceAccessStatistics {

  /**
   * Return the number of total hits for the whole service.
   * @return total hits for the whole service
   */
  int getTotalHits();

  /**
   * Return the number of total misses for the whole service.
   * @return total misses for the whole service.
   */
  int getTotalMisses();

  /**
   * Return the number of total hits for the specified service.
   * @param pKey service key
   * @return total hits for the specified service.
   */
  int getHits(final String pKey);

  /**
   * Return the number of total misses for the specified service.
   * @param pKey service key
   * @return total misses for the specified service.
   */
  int getMisses(final String pKey);

}
