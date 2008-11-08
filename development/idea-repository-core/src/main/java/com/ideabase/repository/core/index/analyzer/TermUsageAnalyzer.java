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

package com.ideabase.repository.core.index.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

import java.io.Reader;

import com.ideabase.repository.core.index.filter.TermUsageFilter;
import com.ideabase.repository.core.index.service.TermUsageService;

/**
 * to store term in different data source and to keep the usages 
 * count for each term.
 *  
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class TermUsageAnalyzer extends Analyzer {
  
  private final Analyzer mBaseAnalyzer;
  private final TermUsageService mTermUsageService;

  /**
   * Default constructor which accepts {@code Analyzer} instance
   * @param pBaseAnalyzer base analyzer
   */
  public TermUsageAnalyzer(final Analyzer pBaseAnalyzer,
                           final TermUsageService pTermUsageService) {
    mBaseAnalyzer = pBaseAnalyzer;
    mTermUsageService = pTermUsageService;
  }

  public TokenStream tokenStream(final String fieldName, final Reader reader) {
    TokenStream result = mBaseAnalyzer.tokenStream(fieldName, reader);
    result = new TermUsageFilter(result, mTermUsageService);
    return result;
  }
}
