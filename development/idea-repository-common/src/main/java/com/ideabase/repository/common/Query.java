/*
 * $Id: Query.java 258 2008-03-10 19:02:20Z hasan $
 ******************************************************************************
 * Copyright (C) 2007 somewhere in .Net ltd.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from somewhere in .Net LTD.  This notice must be included on
 * all copies, modifications and derivatives of this work.
 ******************************************************************************
 * $LastChangedBy: hasan $
 * $LastChangedDate: 2008-03-11 00:32:20 +0530 (Tue, 11 Mar 2008) $
 * $LastChangedRevision: 258 $
 ******************************************************************************
*/
package com.ideabase.repository.common;

import org.apache.lucene.search.*;
import org.apache.lucene.index.Term;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.ideabase.repository.common.exception.ServiceException;

/**
 * Query constructing utility class.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class Query {

  private static final String INDEX_DEFAULT = "default";

  private List<org.apache.lucene.search.Query> mAndQuries =
      new ArrayList<org.apache.lucene.search.Query>();
  private List<org.apache.lucene.search.Query> mOrQuries =
      new ArrayList<org.apache.lucene.search.Query>();
  private Map<String, Boolean> mSortBy = new HashMap<String, Boolean>();
  private Integer mMaxRows = Integer.MAX_VALUE;
  private Integer mSkipRows = 0;
  private float mScore = 0.0f;
  private BooleanQuery mBuiltQuery;
  private String mIndex = INDEX_DEFAULT;

  /**
   * Base query, the default value is null, if this value is not empty.
   * other conditions will be added with this query object.
   */
  private final org.apache.lucene.search.Query mBaseQuery;

  /**
   * Default constructor, it doesn't accept any argument.
   */
  public Query() {
    mBaseQuery = null;
  }

  /**
   * Constructor with {@see org.apache.lucene.search.Query} object.
   * @param pQuery pre parsed query.
   */
  public Query(final org.apache.lucene.search.Query pQuery) {
    mBaseQuery = pQuery;
  }

  /**
   * And add another new range query.
   * @param pField index field
   * @param pStart start value
   * @param pEnd end value.
   * @return self instance.
   */
  public Query andRange(final String pField,
                        final String pStart, final String pEnd) {
    mAndQuries.add(new RangeQuery(
        new Term(pField, pStart),
        new Term(pField, pEnd), true));
    return this;
  }

  /**
   * Or add a new range query.
   * @param pField index field
   * @param pStart start value
   * @param pEnd end value.
   * @return self instance.
   */
  public Query orRange(final String pField,
                       final String pStart, final String pEnd) {
    mOrQuries.add(new RangeQuery(
        new Term(pField, pStart),
        new Term(pField, pEnd), false ));
    return this;
  }

  /**
   * Add required query expression.
   * @param pField query field name
   * @param pValue query field value.
   * @return the current instance of {@see Query} object, because it helps on
   *         method chaining.
   */
  public Query and(final String pField, final String pValue) {
    mAndQuries.add(new TermQuery(new Term(pField, pValue)));
    return this;
  }

  /**
   * Add optional query expression.
   * @param pField query field name.
   * @param pValue query field value.
   * @return the current instance of {@see Query} object, because it helps on
   *         method chaining.
   */
  public Query or(final String pField, final String pValue) {
    mOrQuries.add(new TermQuery(new Term(pField, pValue)));
    return this;
  }

  /**
   * Add option query expression which is matched for prefix only.
   * @param pField field name
   * @param pValue field value.
   * @return the current instance of {@see Query} object, because it helps on
   *         method chaining.
   */
  public Query orPrefix(final String pField, final String pValue) {
    mOrQuries.add(new PrefixQuery(new Term(pField, pValue)));
    return this;
  }

  /**
   * Required query expression which is matched for prefix only.
   * @param pField field name.
   * @param pValue field value.
   * @return the current instance of {@see Query} object, because it helps on
   *         method chaining.
   */
  public Query andPrefix(final String pField, final String pValue) {
    mOrQuries.add(new PrefixQuery(new Term(pField, pValue)));
    return this;
  }

  /**
   * Sort search result by the specified field in directed order.
   * @param pField field of indexed content
   * @param pDecending {@code true} if search result should be in decending ordered.
   * @return the current instance of (@see Query) object.
   */
  public Query sortBy(final String pField, final Boolean pDecending) {
    mSortBy.put(pField, pDecending);
    return this;
  }

  /**
   * Getter for sortable fields.
   * @return map of sortable fields.
   */
  public Map<String, Boolean> getSortableFields() {
    return mSortBy;
  }

  /**
   * set score for boosting search result.
   * @param pScore
   * @return self instance.
   */
  public Query score(final float pScore) {
    mScore = pScore;
    return this;
  }

  /**
   * Return suggested score boosting.
   * @return score
   */
  public float getScore() {
    return mScore;
  }

  /**
   * Set the maximum number of rows per search result.
   * @param pMaxRows maximum number of rows.
   * @return the current instance of {@see Query} object, because it helps on
   *         method chaining.
   */
  public Query maxRows(final Integer pMaxRows) {
    mMaxRows = pMaxRows;
    return this;
  }

  public Integer getMaxRows() {
    return mMaxRows;
  }

  /**
   * Set searchable index
   * @param pIndex searchable index name
   * @return self instance to keep the chain continuing.
   */
  public Query index(final String pIndex) {
    mIndex = pIndex;
    return this;
  }

  public String getIndex() {
    return mIndex.toLowerCase();
  }

  /**
   * Set the skipRows of the active record set.
   * @param pSkipRows the skipRows of the active record set.
   * @return the current instance of {@see Query} object, because it helps on
   *         method chaining.
   */
  public Query skipRows(final Integer pSkipRows) {
    mSkipRows = pSkipRows;
    return this;
  }

  public Integer getSkipRows() {
    return mSkipRows;
  }

  public List<org.apache.lucene.search.Query> getOrQueries() {
    return mOrQuries;
  }

  public List<org.apache.lucene.search.Query> getAndQueries() {
    return mAndQuries;
  }

  /**
   * Convert this query expression to {@see org.apache.lucene.search.Query}
   * object.<br> if base query object is defined, the current conditions
   * will be added with the base query.
   * @return lucene query object is returned.
   */
  public org.apache.lucene.search.Query buildQuery() {
    if (mBuiltQuery == null) {
      if (mAndQuries.isEmpty() && mOrQuries.isEmpty() && mBaseQuery == null) {
        throw new ServiceException(null, "No Query (AND, OR, PREFIX or " +
                                         "BaseQuery) is defined.");
      }
      // all quries are merged here.
      final BooleanQuery query = new BooleanQuery();

      // All required queries.
      if (!mAndQuries.isEmpty()) {
        for (final org.apache.lucene.search.Query andQuery : mAndQuries) {
          query.add(andQuery, BooleanClause.Occur.MUST);
        }
      }

      // All optional quries.
      if (!mOrQuries.isEmpty()) {
        for (final org.apache.lucene.search.Query orQuery : mOrQuries) {
          query.add(orQuery, BooleanClause.Occur.SHOULD);
        }
      }

      // set score for boosting search result
      if (mScore != 0.0f) {
        query.setBoost(mScore);
      }

      if (mBaseQuery != null) {
        // TODO: it set to required.
        // Add defined based query
        query.add(mBaseQuery, BooleanClause.Occur.MUST);
      }
      mBuiltQuery = query;
    }

    return mBuiltQuery;
  }


  @Override
  public String toString() {
    return ObjectToString.toString(this);
  }
}
