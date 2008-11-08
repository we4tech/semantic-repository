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

package com.ideabase.repository.core.index;

import org.apache.lucene.search.RangeFilter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermEnum;

import java.io.IOException;
import java.util.BitSet;

import com.ideabase.repository.common.CommonConstants;

/**
 * Extended range filter implementation.
 *
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class ExtendedRangeFilter extends RangeFilter {

  private final String mFieldName;
  private final String mLowerTerm;
  private final String mUpperTerm;
  private final  boolean mIncludeLower;
  private final boolean mIncludeUpper;
  private final boolean mNumberField;
  private Long mLowerTermNumber;
  private Long mUpperTermNumber;

  public ExtendedRangeFilter(final String pFieldName,
                             final String pLowerTerm, final String pUpperTerm,
                             final boolean pIncludeLower,
                             final boolean pIncludeUpper) {
    super(pFieldName, pLowerTerm, pUpperTerm, pIncludeLower, pIncludeUpper);

    mFieldName = pFieldName;
    mLowerTerm = pLowerTerm;
    mUpperTerm = pUpperTerm;
    mIncludeLower = pIncludeLower;
    mIncludeUpper = pIncludeUpper;
    mNumberField = false;
    if (mNumberField) {
      mLowerTermNumber = Long.valueOf(mLowerTerm);
      mUpperTermNumber = Long.valueOf(mUpperTerm);
    }

  }

  @Override
  public BitSet bits(IndexReader reader) throws IOException {
    final BitSet bits = new BitSet(reader.maxDoc());
    final TermEnum enumerator = (mIncludeLower
            ? reader.terms(new Term(mFieldName, mLowerTerm))
            : reader.terms(new Term(mFieldName, "")));
    try {
      if (enumerator.term() == null) {
        return bits;
      }

      boolean checkLower = mIncludeLower;
      final TermDocs termDocs = reader.termDocs();
      try {
        do {
          final Term currentTerm = enumerator.term();
          final boolean sameField = (currentTerm != null && currentTerm.field().equals(mFieldName));
          if (sameField) {
            Long valueNumber = 0L;
            String valueText = currentTerm.text();
            final boolean noLowerBound = !checkLower || mLowerTerm == null;
            final int compareResult;
            if (mNumberField) {
              valueNumber = Long.valueOf(valueText);
              compareResult = valueNumber.compareTo(mLowerTermNumber);
            } else {
              compareResult = valueText.compareTo(mLowerTerm);
            }
            if (noLowerBound || compareResult > 0) {
              checkLower = false;
              if (mUpperTerm != null) {
                final int compare;
                if (mNumberField) {
                  compare = mUpperTermNumber.compareTo(valueNumber);
                } else {
                  compare = mUpperTerm.compareTo(valueText);
                }
                /*
                 * if beyond the upper term, or is exclusive and
                 * this is equal to the upper term, break out
                 */
                if ((compare < 0) ||
                    (!mIncludeUpper && compare == 0)) {
                  break;
                }
              }
              /* we have a good term, find the docs */
              termDocs.seek(enumerator.term());
              while (termDocs.next()) {
                bits.set(termDocs.doc());
              }
            }
          } else {
            break;
          }
        }
        while (enumerator.next());

      } finally {
        termDocs.close();
      }
    } finally {
      enumerator.close();
    }

    return bits;
  }

}
