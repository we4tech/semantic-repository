/* $Id: PaginatedList.java 265 2008-04-18 09:10:48Z hasan $ */
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
 * $LastChangedBy: hasan $
 * $LastChangedDate: 2008-04-18 15:10:48 +0600 (Fri, 18 Apr 2008) $
 * $LastChangedRevision: 265 $
 ******************************************************************************
*/
package com.ideabase.repository.common.object;

import org.springmodules.lucene.search.factory.LuceneSearcher;
import org.apache.lucene.document.Document;

import java.util.*;
import java.io.IOException;

import com.ideabase.repository.common.exception.ServiceException;

/**
 * Implementation of a typical list, but it supports pagination.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class PaginatedList<E> extends AbstractList<E> {

  /**
   * Maximum number of rows.
   */
  private final Integer mMaxRowPerPage;

  /**
   * Number of skippable rows.
   */
  private final Integer mSkipRows;

  /**
   * Retrieve document by the original lucene searcher.
   */
  private final LuceneSearcher mSearcher;

  /**
   * Store all integer value here.
   */
  private final List<Integer> mInternalList = new ArrayList<Integer>();

  /**
   * Score and index document id map
   */
  private final Map<Integer, Float> mScoreMap = new HashMap<Integer, Float>();

  /**
   * Trasform search document id to {@see Hit} object
   */
  private final HitTransformer mHitTransformer;


  /**
   * Default constructor, it accepts required maximum number of rows per page
   * and number of skippable rows and also it requires the {@see Searcher}
   * which enables to retrieve doucument on demand. instead of loading them
   * all together.
   *
   * @param pSkipRows the first maximum number of skippable rows.
   * @param pMaxRowPerPage maximum number of rows per page.
   * @param pSearcher the searcher which performs search operation.
   * @param pHitTransformer trasform search document id to {@see Hit} object.
   */
  public PaginatedList(final Integer pSkipRows,
                       final Integer pMaxRowPerPage,
                       final LuceneSearcher pSearcher,
                       final HitTransformer pHitTransformer) {
    mSkipRows = pSkipRows;
    mMaxRowPerPage = pMaxRowPerPage;
    mSearcher = pSearcher;
    mHitTransformer = pHitTransformer;
  }

  public Integer getPageSize() {
    return mMaxRowPerPage;
  }

  public void addInteger(final Integer pId, final float pScore) {
    mInternalList.add(pId);
    mScoreMap.put(pId, pScore);
  }

  /**
   * Divide {@see size / mMaxRowPerPage}.<br>
   * if list size is '0' return '0'.
   * @return Divide {@see size / mMaxRowPerPage}.
   */
  public int getPageCount() {
    final int listSize = size();
    if (listSize == 0) {
      return 0;
    } else {
      int pageCount = (int) Math.round((double) listSize / (double) mMaxRowPerPage);
      if (pageCount == 0 && listSize > 0) {
        pageCount = 1;
      }
      return pageCount;
    }
  }

  // ------------- AbstractList implementation

  public E get(final int pIndex) {
    try {
      return (E) getHit(pIndex);
    } catch (IOException e) {
      throw new ServiceException(pIndex, "Failed to document from index.", e);
    }
  }

  private Hit getHit(final int pIndex) throws IOException {
    final Integer docId = mInternalList.get(pIndex);
    final Document document = mSearcher.doc(docId);
    final float score = mScoreMap.get(docId);
    document.setBoost(score);
    return mHitTransformer.transform(docId, document);
  }

  public int size() {
    return mInternalList.size();
  }

  public Iterator<E> iterator() {
    if (mSkipRows > size()) {
      throw new IllegalStateException("List size(" + size() + ") is less " +
          "than the skippable rows(" + mSkipRows + ")." );
    }
    return new IteratorImpl<E>();
  }

  private class IteratorImpl<E> implements Iterator<E> {

    private int mIndex = mSkipRows;
    private int mRowCount = 0;
    private final int mSize;

    public IteratorImpl() {
      mSize = size();
    }

    public boolean hasNext() {
      final boolean hasNext = mIndex != mSize;
      return hasNext && mRowCount != mMaxRowPerPage;
    }

    public E next() {
      try {
        // Create hit object
        return (E) getHit(mIndex);
      } catch (IOException e) {
        throw new ServiceException(mSearcher,
            "Searcher threw an IO exception.", e);
      } finally {
        mIndex++;
        mRowCount++;
      }
    }

    public void remove() {
      mInternalList.remove(mIndex);
    }
  }
}
