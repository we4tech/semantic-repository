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

package com.ideabase.repository.core.index.factory;

import org.springmodules.lucene.index.factory.LuceneIndexWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.store.Directory;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Similarity;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

import com.ideabase.repository.core.index.service.TermUsageService;
import static com.ideabase.repository.core.helper.IndexHelper.isAcceptableFieldNameForTokenCollection;

/**
 * To store terms when new item is indexed.
 * Decoreated {@code LuceneIndexWriter}
 *
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class TermUsageAwareLuceneIndexWriter implements LuceneIndexWriter {

  private static final Logger LOG =
    LogManager.getLogger(TermUsageAwareLuceneIndexWriter.class);

  private final LuceneIndexWriter mBaseLuceneIndexWriter;
  private final TermUsageService mTermUsageService;

  public TermUsageAwareLuceneIndexWriter(
    final LuceneIndexWriter pBaseLuceneIndexWriter,
    final TermUsageService pTermUsageService) {
    mBaseLuceneIndexWriter = pBaseLuceneIndexWriter;
    mTermUsageService = pTermUsageService;
  }

  public void addDocument(final Document pDocument) throws IOException {
    mBaseLuceneIndexWriter.addDocument(pDocument);
    parseTermsFromDocument(pDocument, getAnalyzer());
  }

  public void addDocument(final Document pDocument,
                          final Analyzer pAnalyzer) throws IOException {
    mBaseLuceneIndexWriter.addDocument(pDocument, pAnalyzer);
    parseTermsFromDocument(pDocument, pAnalyzer);
  }

  /**
   * Parse terms from the specified document and store their hit count
   * through {@code TermUsageService}
   *
   * @param pDocument lucene index document
   * @param pAnalyzer used for analyzing tokens document
   */
  private void parseTermsFromDocument(final Document pDocument,
                                      final Analyzer pAnalyzer) {
    for (final Object fieldObject : pDocument.getFields()) {
      final Field field = (Field) fieldObject;
      try {
        if (LOG.isDebugEnabled()) {
          LOG.debug("PTFD: caculating tokens for - " + field.name() + " - " + isAcceptableFieldNameForTokenCollection(field.name()));
        }
        if (isAcceptableFieldNameForTokenCollection(field.name()) && field.isTokenized()) {
          final TokenStream tokenStream = pAnalyzer.
              tokenStream(field.name(), new StringReader(field.stringValue()));
          if (tokenStream != null) {
            Token token = null;
            while ((token = tokenStream.next()) != null) {
              final String tokenString = String.valueOf(token.termBuffer()).trim();
              mTermUsageService.incrementTermCount(tokenString, field.name());
            }
          }
        }
      } catch(Exception e) {
        LOG.warn(e);
      }
    }
  }

  public void addIndexes(final Directory[] pDirectories) throws IOException {
    mBaseLuceneIndexWriter.addIndexes(pDirectories);
  }

  public void addIndexes(final IndexReader[] pIndexReaders) throws IOException {
    mBaseLuceneIndexWriter.addIndexes(pIndexReaders);
  }

  public void close() throws IOException {
    mBaseLuceneIndexWriter.close();
  }

  public int docCount() {
    return mBaseLuceneIndexWriter.docCount();
  }

  public Analyzer getAnalyzer() {
    return mBaseLuceneIndexWriter.getAnalyzer();
  }

  public long getCommitLockTimeout() {
    return mBaseLuceneIndexWriter.getCommitLockTimeout();
  }

  public Directory getDirectory() {
    return mBaseLuceneIndexWriter.getDirectory();
  }

  public PrintStream getInfoStream() {
    return mBaseLuceneIndexWriter.getInfoStream();
  }

  public int getMaxBufferedDocs() {
    return mBaseLuceneIndexWriter.getMaxBufferedDocs();
  }

  public int getMaxFieldLength() {
    return mBaseLuceneIndexWriter.getMaxFieldLength();
  }

  public int getMaxMergeDocs() {
    return mBaseLuceneIndexWriter.getMaxMergeDocs();
  }

  public int getMergeFactor() {
    return mBaseLuceneIndexWriter.getMergeFactor();
  }

  public Similarity getSimilarity() {
    return mBaseLuceneIndexWriter.getSimilarity();
  }

  public int getTermIndexInterval() {
    return mBaseLuceneIndexWriter.getTermIndexInterval();
  }

  public boolean getUseCompoundFile() {
    return mBaseLuceneIndexWriter.getUseCompoundFile();
  }

  public long getWriteLockTimeout() {
    return mBaseLuceneIndexWriter.getWriteLockTimeout();
  }

  public void optimize() throws IOException {
    mBaseLuceneIndexWriter.optimize();
  }

  public void setCommitLockTimeout(final long l) {
    mBaseLuceneIndexWriter.setCommitLockTimeout(l);
  }

  public void setInfoStream(final PrintStream pPrintStream) {
    mBaseLuceneIndexWriter.setInfoStream(pPrintStream);
  }

  public void setMaxBufferedDocs(final int i) {
    mBaseLuceneIndexWriter.setMaxBufferedDocs(i);
  }

  public void setMaxFieldLength(final int i) {
    mBaseLuceneIndexWriter.setMaxFieldLength(i);
  }

  public void setMaxMergeDocs(final int i) {
    mBaseLuceneIndexWriter.setMaxMergeDocs(i);
  }

  public void setMergeFactor(final int i) {
    mBaseLuceneIndexWriter.setMergeFactor(i);
  }

  public void setSimilarity(final Similarity pSimilarity) {
    mBaseLuceneIndexWriter.setSimilarity(pSimilarity);
  }

  public void setTermIndexInterval(final int i) {
    mBaseLuceneIndexWriter.setTermIndexInterval(i);
  }

  public void setUseCompoundFile(final boolean b) {
    mBaseLuceneIndexWriter.setUseCompoundFile(b);
  }

  public void setWriteLockTimeout(final long l) {
    mBaseLuceneIndexWriter.setWriteLockTimeout(l);
  }
}
