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

package com.ideabase.repository.core.index.service;

import java.util.Map;
import java.util.List;

/**
 * Store term usage count
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public interface TermUsageService {
  /**
   * Increment count for the specific term {@code pTerm}
   * @param pTerm string
   */
  void incrementTermCount(final String pTerm);

  /**
   * Increment count for the specific term (@code pTerm} also set the
   * lastly updated from field
   * @param pTerm string
   * @param pField field name
   */
  void incrementTermCount(final String pTerm, final String pField);


  /**
   * store new term reference
   * @param pTerm term content
   * @param pField found term on the field
   * @param pItemId found term on the item
   */
  void storeTerm(final String pTerm, final String pField, final int pItemId);

  /**
   * Decrement count for the specific term {@code pTerm}
   * @param pTerm string
   */
  void decrementTermCount(final String pTerm);

  /**
   * Decrement count for the spepcific term {@code pTerm}
   * @param pTerm string
   * @param pField field name
   */
  void decrementTermCount(final String pTerm, final String pField);

  /**
   * Total number of terms stored
   * @return integer value of terms
   */
  int length();

  /**
   * {@code Map} of terms and their usage count.
   * @return {@code Map} of terms and their usage count.
   */
  Map<String, Integer> map();

  /**
   * Get the usage count of the specified {@code pTerm}
   * @param pTerm term string
   * @return usage count
   */
  int getUsageCountOf(final String pTerm);

  /**
   * Retrieve tags from the given list of item ids.
   * @param pItemIds list of items ids
   * @param pFields list of fields from where tags will be generated
   * @param pMax maximum number of tags need to be retrieved.  @return map of tag cloud data
   * @return {@code Map} of tags and their respective hit count
   */
  Map<String, String> getTags(final List<Integer> pItemIds, 
                              final List<String> pFields,
                              final int pMax);

  /**
   * Get maximum number of allowed words
   * @return max number of allowed words
   */
  int getMaxWordLength();

  /**
   * Get minimum number of allowed words
   * @return min number of allowed words
   */
  int getMinWordLength();

}
