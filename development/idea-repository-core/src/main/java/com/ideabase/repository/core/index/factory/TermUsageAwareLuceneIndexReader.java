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

import org.springmodules.lucene.index.factory.LuceneIndexReader;
import org.springmodules.lucene.search.factory.LuceneSearcher;
import org.springmodules.lucene.search.factory.LuceneHits;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

import java.io.IOException;
import java.util.Collection;

import com.ideabase.repository.core.index.service.TermUsageService;
import static com.ideabase.repository.core.helper.IndexHelper.isAcceptableFieldNameForTokenCollection;

/**
 * To trigger action while removing or creating new index.
 * this decorator suppose to store term usages information when new
 * index is created.
 * otherwise when new index is removed it will reduce the term
 * usage information.
 *
 * decorate {@code
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class TermUsageAwareLuceneIndexReader implements LuceneIndexReader {

  private static final Logger LOG =
    LogManager.getLogger(TermUsageAwareLuceneIndexReader.class);
  private final LuceneIndexReader mBaseLuceneIndexReader;
  private final TermUsageService mTermUsageService;

  public TermUsageAwareLuceneIndexReader(
    final LuceneIndexReader pLuceneIndexReader,
    final TermUsageService pTermUsageService) {
    mBaseLuceneIndexReader = pLuceneIndexReader;
    mTermUsageService = pTermUsageService;
  }

  public void close() throws IOException {
    mBaseLuceneIndexReader.close();
  }

  public void deleteDocument(final int i) throws IOException {
    mBaseLuceneIndexReader.deleteDocument(i);
  }

  public int deleteDocuments(final Term pTerm) throws IOException {
    // create new searcher to find the document
    if (LOG.isDebugEnabled()) {
      LOG.debug("DD: caculating tokens for - " + pTerm.field());
    }
    if (isAcceptableFieldNameForTokenCollection(pTerm.field())) {
      final LuceneSearcher searcher = createSearcher();
      try {
        // find the document body
        final LuceneHits hits = searcher.search(new TermQuery(pTerm));
        // remove the token count
        if (hits != null && hits.length() > 0) {
          for (int i = 0; i < hits.length(); i++) {
            final TermFreqVector[] termFreqVectors =
              mBaseLuceneIndexReader.getTermFreqVectors(hits.id(i));
            if (termFreqVectors != null && termFreqVectors.length > 0) {
              for (final TermFreqVector freqVector : termFreqVectors) {
                for (final String pTermString : freqVector.getTerms()) {
                  mTermUsageService.decrementTermCount(pTermString.trim());
                }
              }
            }
          }
        }
      } finally {
        // close the searcher
        searcher.close();
      }
    }
    // delete the document
    return mBaseLuceneIndexReader.deleteDocuments(pTerm);
  }

  public Directory directory() {
    return mBaseLuceneIndexReader.directory();
  }

  public int docFreq(final Term pTerm) throws IOException {
    return mBaseLuceneIndexReader.docFreq(pTerm);
  }

  public Document document(final int i) throws IOException {
    return mBaseLuceneIndexReader.document(i);
  }

  public Collection getFieldNames(final IndexReader.FieldOption pFieldOption) {
    return mBaseLuceneIndexReader.getFieldNames(pFieldOption);
  }

  public TermFreqVector getTermFreqVector(final int i, final String s) throws IOException {
    return mBaseLuceneIndexReader.getTermFreqVector(i, s);
  }

  public TermFreqVector[] getTermFreqVectors(final int i) throws IOException {
    return mBaseLuceneIndexReader.getTermFreqVectors(i);
  }

  public long getVersion() {
    return mBaseLuceneIndexReader.getVersion();
  }

  public boolean hasDeletions() {
    return mBaseLuceneIndexReader.hasDeletions();
  }

  public boolean hasNorms(final String s) throws IOException {
    return mBaseLuceneIndexReader.hasNorms(s);
  }

  public boolean isCurrent() throws IOException {
    return mBaseLuceneIndexReader.isCurrent();
  }

  public boolean isDeleted(final int i) {
    return mBaseLuceneIndexReader.isDeleted(i);
  }

  public int maxDoc() {
    return mBaseLuceneIndexReader.maxDoc();
  }

  public byte[] norms(final String s) throws IOException {
    return mBaseLuceneIndexReader.norms(s);
  }

  public void norms(final String s, final byte[] pBytes, final int i) throws IOException {
    mBaseLuceneIndexReader.norms(s, pBytes, i);
  }

  public int numDocs() {
    return mBaseLuceneIndexReader.numDocs();
  }

  public void setNorm(final int i, final String s, final byte b) throws IOException {
    mBaseLuceneIndexReader.setNorm(i, s, b);
  }

  public void setNorm(final int i, final String s, final float v) throws IOException {
    mBaseLuceneIndexReader.setNorm(i, s, v);
  }

  public TermDocs termDocs() throws IOException {
    return mBaseLuceneIndexReader.termDocs();
  }

  public TermDocs termDocs(final Term pTerm) throws IOException {
    return mBaseLuceneIndexReader.termDocs(pTerm);
  }

  public TermPositions termPositions() throws IOException {
    return mBaseLuceneIndexReader.termPositions();
  }

  public TermPositions termPositions(final Term pTerm) throws IOException {
    return mBaseLuceneIndexReader.termPositions(pTerm);
  }

  public TermEnum terms() throws IOException {
    return mBaseLuceneIndexReader.terms();
  }

  public TermEnum terms(final Term pTerm) throws IOException {
    return mBaseLuceneIndexReader.terms(pTerm);
  }

  public void undeleteAll() throws IOException {
    mBaseLuceneIndexReader.undeleteAll();
  }

  public LuceneSearcher createSearcher() {
    return mBaseLuceneIndexReader.createSearcher();
  }

  public Searcher createNativeSearcher() {
    return mBaseLuceneIndexReader.createNativeSearcher();
  }
}
