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

package com.ideabase.repository.core.dao;

import java.util.List;

/**
 * Term data access object
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public interface TermDAO {

  Integer createTerm(final Term pTerm);
  void updateTerm(final Term pTerm);

  List<Term> findTerms(final Term pTerm,
                       final Integer pOffset,
                       final Integer pMax);

  List<Term> findTermsByCollection(final List<String> pTerms,
                                   final Integer pOffset, final Integer pMax);

  void deleteTerm(final String pTerm);
}
