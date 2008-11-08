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

package com.ideabase.repository.test.dao;

import com.ideabase.repository.test.BaseTestCase;
import com.ideabase.repository.core.CoreServiceKey;
import com.ideabase.repository.core.dao.TermDAO;
import com.ideabase.repository.core.dao.Term;

import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class TermDAOTest extends BaseTestCase {

  private TermDAO mTermDAO;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    System.out.println("Context - " + mContext);
    mTermDAO = (TermDAO) mContext.getBean(CoreServiceKey.TERM_DAO);
  }

  public Term testCreateTerm() {
    System.out.println(mTermDAO);
    final Term term = new Term.Builder().term("test1").count(1).build();
    final int id = mTermDAO.createTerm(term);
    assertNotNull(id);
    term.setId(id);
    return term;
  }

  public void testUpdateTerm() {
    final Term term = testCreateTerm();
    final String oldTerm = term.getTerm();
    final int oldCount = term.getCount();

    // update term with new value
    final String newTerm = "newTerm";
    term.setCount(term.getCount() + 1);
    term.setTerm(newTerm);
    mTermDAO.updateTerm(term);

    // reload term object
    final Term reloadedTerm = mTermDAO.findTerms(
        new Term.Builder().id(term.getId()).build(), 0, 1).get(0);
    assertNotNull(reloadedTerm);
    assertEquals(reloadedTerm.getTerm(), term.getTerm());
    assertEquals(reloadedTerm.getCount(), term.getCount());
  }

  public List<Term> findTerms() {
    return null;
  }

  public void testFindTermsByCollection() {
    final List<String> terms = new ArrayList<String>();
    for (int i = 0; i < 5; i++) {
      final String termString = "term" + i;
      terms.add(termString);
      final Term term = new Term();
      term.setTerm(termString);
      term.setCount(0);
      assertNotNull(mTermDAO.createTerm(term));
    }

    // find all terms by the specified name
    final List<Term> termObjects =
        mTermDAO.findTermsByCollection(terms, 0, terms.size());
    assertNotNull(termObjects);
    assertFalse(termObjects.isEmpty());
    System.out.println("TERMOBJECT - " + termObjects);
  }

  public void deleteTerm() {

  }

}
