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
  private int mMinWordLength = 3;
  private int mMaxWordLength = 12;

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
    if (isAcceptableLength(trimmedTerm)) {
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
  }

  public void storeTerm(final String pTerm,
                        final String pField,
                        final int pItemId) {
    final String trimmedTerm = pTerm.trim();
    if (isAcceptableLength(trimmedTerm)) {
      final Term term = new Term
          .Builder()
          .term(pTerm)
          .field(pField)
          .itemId(pItemId).build();
      mTermDAO.createTerm(term);
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
    final String termString = pTerm.trim();
    if (isAcceptableLength(termString)) {
      final Term term = getExistingTerm(termString);
      if (term != null && term.getCount() > 0) {
        term.setCount(term.getCount() - 1);
        mTermDAO.updateTerm(term);
      }
    }
  }

  private boolean isAcceptableLength(final String pTermString) {
    return (pTermString != null
            && pTermString.length() >= mMinWordLength
            && pTermString.length() <= mMaxWordLength);
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

  public Map<String, String> getTags(final List<Integer> pItemIds,
                                     final List<String> pFields,
                                     final int pMax) {
    final Map<String, String> map = new HashMap<String, String>();
    final List<Term> terms = mTermDAO.
        findTermsByItemIds(pItemIds, pFields, 0, pMax);
    for (final Term term : terms) {
      map.put(term.getTerm(), String.valueOf(term.getCount()));
    }
    return map;
  }

  public void decrementTermCount(final String pTerm, final String pField) {
    throw new NoSuchMethodError("this method is not yet implemented");
  }

  public void setMaxWordLength(final int pMaxWordLength) {
    mMaxWordLength = pMaxWordLength;
  }

  public int getMaxWordLength() {
    return mMaxWordLength;
  }

  public void setMinWordLength(final int pMinWordLength) {
    mMinWordLength = pMinWordLength;
  }

  public int getMinWordLength() {
    return mMinWordLength;
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
