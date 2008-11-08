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

package com.ideabase.repository.core.index.filter;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Token;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ideabase.repository.core.index.service.TermUsageService;

/**
 * To find seprate token and store them in seperate storage and calculate their usages.
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class TermUsageFilter extends TokenFilter {

  private static final Logger LOG = LogManager.getLogger(TermUsageFilter.class);
  private static final String TYPE_ALPHANUM = "<ALPHANUM>";
  private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
  public static TermUsageService mTermUsageService;

  /**
   * Default constructor
   * @param pInputTokenStream token input stream
   */
  public TermUsageFilter(final TokenStream pInputTokenStream,
                         final TermUsageService pTermUsageService) {
    super(pInputTokenStream);
    mTermUsageService = pTermUsageService;
  }

  public void setExecutorService(final ExecutorService pExecutorService) {
    mExecutorService = pExecutorService;
  }

  public ExecutorService getExecutorService() {
    return mExecutorService;
  }

  @Override
  public Token next() throws IOException {
    final Token token = input.next();
    if (token != null) {
      if (token.type().equals(TYPE_ALPHANUM)) {
        storeAndIncrementCount(String.valueOf(token.termBuffer()));
      }
      return token;
    } else {
      return null;
    }
  }

  /**
   * To store the token usages count, if this token is completely new
   * store it otherwise increment usages count by 1
   *
   * this process might slow down the general filtering thats why we will
   * use task service to execute this job
   *
   * @param pToken token string
   */
  private void storeAndIncrementCount(final String pToken) {
    mExecutorService.submit(new Runnable() {
      public void run() {
        final String token = pToken.trim();
        if (LOG.isDebugEnabled()) {
          LOG.debug("Store and increment count for token - " + token);
        }
        mTermUsageService.incrementTermCount(token);
      }
    });
  }
}
