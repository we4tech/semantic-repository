/* $Id:RepositoryItemSearchImpl.java 249 2007-12-02 08:32:47Z hasan $ */
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
package com.ideabase.repository.core.search.impl;

import org.springmodules.lucene.search.support.LuceneSearchSupport;
import org.springmodules.lucene.search.core.QueryCreator;
import org.springmodules.lucene.search.factory.LuceneSearcher;
import org.springmodules.lucene.search.factory.LuceneHits;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

import com.ideabase.repository.core.search.RepositoryItemSearch;
import com.ideabase.repository.core.index.NumberSortComparator;
import com.ideabase.repository.common.object.Hit;
import com.ideabase.repository.common.object.ObjectBase;
import com.ideabase.repository.common.object.PaginatedList;
import com.ideabase.repository.common.object.HitTransformer;
import com.ideabase.repository.common.Query;
import com.ideabase.repository.common.CommonConstants;
import com.ideabase.repository.common.exception.ServiceException;

import java.util.*;
import java.io.Reader;
import java.io.IOException;

/**
 * Implementation of {@code RepositoryItemSearch}.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class RepositoryItemSearchImpl extends LuceneSearchSupport
    implements RepositoryItemSearch {

  private final transient Logger LOG =
      LogManager.getLogger(RepositoryItemSearchImpl.class);

  private final HitTransformer mHitTransformer =
      new RepositoryHitTransformerImpl();

  /**
   * Field for relevant sorting.
   */
  private static final String SORT_RELEVANT = "relevant";

  /**
   * Field for index order.
   */
  private static final String SORT_INDEXORDERED = "indexorder";

  /**
   * Support sorting option for number fields.
   */
  private final NumberSortComparator mNumberSortComparator =
      new NumberSortComparator();

  /**
   * {@inheritDoc}
   */
  public PaginatedList<Hit> search(final Query pQuery) {
    LOG.debug("Performing search action by query -  " + pQuery);
    try {
      final QueryCreator queryCreator = new RepositoryQueryCreatorImpl(pQuery);
      // Prepare paginated hit collector.
      final RepositoryHitCollectorImpl hitCollector =
          new RepositoryHitCollectorImpl(
              pQuery.getSkipRows(),
              pQuery.getMaxRows(),
              getSearcherFactory().getSearcher(),
              mHitTransformer);

      final Map<String, Boolean> sortableFields = pQuery.getSortableFields();
      final LuceneSearcher searcher = getSearcherFactory().getSearcher();

      if (sortableFields.isEmpty()) {
        populateHitCollector(hitCollector,
            searcher.search(pQuery.buildQuery(), Sort.RELEVANCE));
      } else {
        // determine sortable field and type
        // by default sort is based on relevance
        applySortingFilter(pQuery, hitCollector, searcher);
      }
      return hitCollector.getPaginatedList();
    } catch (Throwable e) {
      throw new ServiceException(pQuery, "Failed to perform search.", e);
    }
  }

  private void applySortingFilter(final Query pQuery,
      final RepositoryItemSearchImpl.RepositoryHitCollectorImpl pHitCollector,
      final LuceneSearcher pSearcher) throws IOException {
    Sort sort = new Sort();
    final List<SortField> sortFields = new ArrayList<SortField>();
    for (final Map.Entry<String, Boolean> entry :
         pQuery.getSortableFields().entrySet()) {
      // verify special field like relevant and indexoredered
      final String key = entry.getKey();
      final Boolean decending = entry.getValue();
      if (SORT_RELEVANT.equalsIgnoreCase(key)) {
        LOG.debug("Applying relevance sorting.");
        sort = Sort.RELEVANCE;
        break;
      } else if (SORT_INDEXORDERED.equalsIgnoreCase(key)) {
        LOG.debug("Applying Index ordered sorting.");
        sort = Sort.INDEXORDER;
        break;
      } else {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Applying sort field - " + key);
        }
        if (key.startsWith(CommonConstants.FIELD_PREFIX_PRICE)
            || key.endsWith(CommonConstants.FIELD_SUFFIX_ID)
            || key.endsWith(CommonConstants.FIELD_SUFFIX_DATE)) {
          sortFields.add(new SortField(
              key, mNumberSortComparator, decending.booleanValue()));
        } else {
          sortFields.add(new SortField(key, decending.booleanValue()));
        }
      }
    }
    if (!sortFields.isEmpty()) {
      sort.setSort(sortFields.toArray(new SortField[] {}));
    }

    if (LOG.isDebugEnabled()) {
      LOG.debug("Sortable fields - " + sort);
    }

    // perform lucene search and collect the search hits.
    populateHitCollector(pHitCollector,
                         pSearcher.search(pQuery.buildQuery(), sort));
  }

  private void populateHitCollector(
      final RepositoryHitCollectorImpl pHitCollector,
      final LuceneHits pLuceneHits) throws IOException {
    final Iterator<org.apache.lucene.search.Hit> hitIterator =
        pLuceneHits.iterator();
    while (hitIterator.hasNext()) {
      final org.apache.lucene.search.Hit hit = hitIterator.next();
      pHitCollector.collect(hit.getId(), hit.getScore());
    }
  }

  private class RepositoryQueryCreatorImpl implements QueryCreator {
    public final Query mQuery;

    public RepositoryQueryCreatorImpl(final Query pQuery) {
      mQuery = pQuery;
    }

    public org.apache.lucene.search.Query createQuery(Analyzer pAnalyzer)
        throws ParseException {
      return mQuery.buildQuery();
    }
  }

  private class RepositoryHitCollectorImpl extends HitCollector {

    private final PaginatedList<Hit> mPaginatedList;

    public RepositoryHitCollectorImpl(final Integer pSkipRows,
                                      final Integer pMaxRows,
                                      final LuceneSearcher pSearcher,
                                      final HitTransformer pTransformer) {
      super();
      mPaginatedList = new PaginatedList<Hit>(pSkipRows, pMaxRows, pSearcher,
                                              pTransformer);
    }

    public void collect(int i, float v) {
      mPaginatedList.addInteger(i, v);
    }

    public PaginatedList<Hit> getPaginatedList() {
      return mPaginatedList;
    }
  }

  private class RepositoryHitTransformerImpl implements HitTransformer {

    public Hit transform(final Integer pDocId, final Document pDocument) {
      final Hit hit = new Hit();
      hit.setHitId(pDocId);
      hit.setScore(pDocument.getBoost());
      final Integer id = Integer.
          valueOf(pDocument.get(ObjectBase.INDEX_FIELD_ID));
      hit.setId(id);

      // retrive stored fields
      final List<Field> fieldList = pDocument.getFields();
      final Map<String, String> fields = new HashMap<String, String>();
      for (final Field field : fieldList) {
        fields.put(field.name(), pDocument.get(field.name()));
      }
      hit.setFields(fields);
      hit.setTitle(pDocument.get(ObjectBase.INDEX_FIELD_TITLE));

      return hit;
    }

    private Integer findInteger(final Field pField) {
      String value = findString(pField);
      if (value != null) {
        return Integer.valueOf(value);
      }
      return null;
    }

    private String findString(final Field pField) {
      if (pField != null) {
        StringBuilder builder = new StringBuilder();
        Reader reader = pField.readerValue();
        try {
          for (int c = 0; c != -1; c = reader.read()) {
            builder.append(c);
          }
        } catch (IOException e) {
          // INvalid index content.
          LOG.debug("Invalid content found. " + pField, e);
        }
        return builder.toString();
      }
      return null;
    }
  }
}
