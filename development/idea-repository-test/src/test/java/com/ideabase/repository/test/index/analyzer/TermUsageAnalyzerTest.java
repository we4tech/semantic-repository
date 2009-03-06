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

package com.ideabase.repository.test.index.analyzer;

import com.ideabase.repository.test.BaseTestCase;
import com.ideabase.repository.core.index.analyzer.TermUsageAnalyzer;
import com.ideabase.repository.core.index.service.TermUsageService;
import junit.framework.TestCase;
import org.jmock.core.MockObjectSupportTestCase;
import org.jmock.Mock;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Token;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

import java.io.StringReader;
import java.io.IOException;

/**
 * Test {@see TermUsageAnalyzer}
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class TermUsageAnalyzerTest extends MockObjectSupportTestCase {

  private final Logger mLogger = LogManager.getLogger(getClass());
  private final TermUsageService mTermUsageService;

  public TermUsageAnalyzerTest() {
    super();
    mTermUsageService = mockTermUsageService();
  }

  private TermUsageService mockTermUsageService() {
    final Mock mock = new Mock(TermUsageService.class);
    return (TermUsageService) mock.proxy();
  }

  public void testAnalysis() throws IOException {
    final TermUsageAnalyzer analyzer =
        new TermUsageAnalyzer(new StandardAnalyzer(), mTermUsageService);
    final String tokens = "hello world how u doing doing this 2008 years";
    final TokenStream tokenStream =
        analyzer.tokenStream("test", new StringReader(tokens));
    assertNotNull(tokenStream);

    Token token = null;
    while ((token = tokenStream.next()) != null) {
      mLogger.debug("Token - " + token);
      mLogger.debug(String.valueOf(token.termBuffer()));
    }
  }
}
