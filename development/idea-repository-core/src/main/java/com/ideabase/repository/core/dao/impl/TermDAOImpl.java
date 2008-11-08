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

package com.ideabase.repository.core.dao.impl;

import com.ideabase.repository.core.dao.TermDAO;
import com.ideabase.repository.core.dao.Term;
import com.ideabase.repository.common.exception.ServiceException;

import java.util.List;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

/**
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class TermDAOImpl extends SqlMapClientDaoSupport implements TermDAO {

  private static final String QUERY_INSERT = "insertTerm";
  private static final String QUERY_UPDATE = "updateTerm";
  private static final String QUERY_FIND_TERMS = "findTerms";
  private static final String QUERY_FIND_TERMS_BY_TERMS = "findTermsByTerms";
  private static final String QUERY_DELETE = "deleteTerm";

  public Integer createTerm(final Term pTerm) {
    return (Integer) getSqlMapClientTemplate().insert(QUERY_INSERT, pTerm);
  }

  public void updateTerm(final Term pTerm) {
    int rowAffected = getSqlMapClientTemplate().update(QUERY_UPDATE, pTerm);
    if (rowAffected < 0) {
      throw new ServiceException(pTerm,
          "Failed to update a term on repository.");
    }
  }

  public List<Term> findTerms(final Term pTerm,
                              final Integer pOffset,
                              final Integer pMax) {
    return getSqlMapClientTemplate().
        queryForList(QUERY_FIND_TERMS, pTerm, pOffset, pMax);
  }

  public List<Term> findTermsByCollection(final List<String> pTerms,
                                          final Integer pOffset,
                                          final Integer pMax) {
    return getSqlMapClientTemplate().queryForList(
        QUERY_FIND_TERMS_BY_TERMS, pTerms, pOffset, pMax);
  }

  public void deleteTerm(final String pTerm) {
    int rowAffected = getSqlMapClientTemplate().delete(QUERY_DELETE, pTerm);
    if (rowAffected < 0) {
      throw new ServiceException(pTerm,
          "Failed to delete a term on repository.");
    }
  }
}
