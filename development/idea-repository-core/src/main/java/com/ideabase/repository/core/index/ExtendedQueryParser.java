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

import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.*;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.document.NumberTools;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

import static com.ideabase.repository.common.CommonConstants.*;

import java.io.IOException;

/**
 * Extended query parser supports Number to pad
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class ExtendedQueryParser extends QueryParser {

  private final Logger mLog = LogManager.getLogger(getClass());

  public ExtendedQueryParser(final String pField, final Analyzer pAnalyzer) {
    super(pField, pAnalyzer);
  }

  @Override
  protected Query getRangeQuery(final String pField,
                                final String pLowerTerm,
                                final String pUpperTerm,
                                final boolean pInclusive)
      throws ParseException {

    final boolean includeLower = pLowerTerm != null && !"*".equals(pLowerTerm);
    final boolean includeUpper = pUpperTerm != null && !"*".equals(pUpperTerm);
    return new ConstantScoreRangeQuery(
        pField,
        includeLower ? NumberTools.longToString(Integer.parseInt(pLowerTerm)) : "",
        includeUpper ? NumberTools.longToString(Integer.parseInt(pUpperTerm)) : "*",
        includeLower, includeUpper) {
    };
  }
}
