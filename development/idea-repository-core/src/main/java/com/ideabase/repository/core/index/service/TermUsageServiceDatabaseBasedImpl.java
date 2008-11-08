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

import com.ideabase.repository.core.dao.TermDAO;
import com.ideabase.repository.core.dao.Term;

import java.util.*;

/**
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class TermUsageServiceDatabaseBasedImpl implements TermUsageService {

  private final TermDAO mTermDAO;
  private static final String DEFAULT_FIELD = "default";

  public TermDAO getTermDAO() {
    return mTermDAO;
  }

  public TermUsageServiceDatabaseBasedImpl(final TermDAO pTermDAO) {
    mTermDAO = pTermDAO;
  }

  public void incrementTermCount(final String pTerm) {
    incrementTermCount(pTerm, DEFAULT_FIELD);
  }

  public void incrementTermCount(final String pTerm, final String pField) {
    final String trimmedTerm = pTerm.trim();
    final Term term = getExistingTerm(trimmedTerm);

    // if term exists increment the value
    if (term != null) {
      term.setCount(term.getCount() + 1);
      term.setField(pField);
      mTermDAO.updateTerm(term);
    } else {
      // otherwise create a new term with count 1
      mTermDAO.createTerm(
          new Term.Builder()
          .term(trimmedTerm)
          .count(1)
          .build());
    }
  }

  private Term getExistingTerm(final String pTerm) {
    // find existing term
    final List<Term> terms = mTermDAO.
        findTerms(new Term.Builder().term(pTerm).build(), 0, 1);
    if (terms != null && !terms.isEmpty()) {
      return terms.get(0);
    }
    return null;
  }

  public void decrementTermCount(final String pTerm) {
    final Term term = getExistingTerm(pTerm.trim());
    if (term != null && term.getCount() > 0) {
      term.setCount(term.getCount() - 1);
      mTermDAO.updateTerm(term);
    }
  }

  public int length() {
    throw new NoSuchMethodError("this method is not yet implemented");
  }

  public Map<String, Integer> map() {
    return new TermMap<String, Integer>();
  }

  public int getUsageCountOf(final String pTerm) {
    final List<Term> terms = mTermDAO.
        findTerms(new Term.Builder().term(pTerm).build(), 0, 1);
    if (terms != null && !terms.isEmpty()) {
      return terms.get(0).getCount();
    }
    return 0;
  }

  /**
   * This class is only for testing purpose, don't use it in real code.
   */
  public class TermMap<K, V> extends HashMap<K, V> {

    @Override
    public V get(final Object pTerm) {
      return (V) Integer.valueOf(getExistingTerm((String) pTerm).getCount());
    }

    @Override
    public boolean isEmpty() {
      return false;
    }
  }
}
