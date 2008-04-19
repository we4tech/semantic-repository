/*
 * $Id:RepositoryItemIndex.java 249 2007-12-02 08:32:47Z hasan $
 ******************************************************************************
 * Copyright (C) 2007 somewhere in .Net ltd.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from somewhere in .Net LTD.  This notice must be included on
 * all copies, modifications and derivatives of this work.
 ******************************************************************************
 * $LastChangedBy:hasan $
 * $LastChangedDate:2007-12-02 14:32:47 +0600 (Sun, 02 Dec 2007) $
 * $LastChangedRevision:249 $
 ******************************************************************************
*/
package com.ideabase.repository.core.index;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.springmodules.lucene.index.core.DocumentModifier;

/**
 * Provide accessor and mutator for Lucene based indexing service.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public interface RepositoryItemIndex {

  /**
   * Index new document. all indexable fields are defined over {@code Document}
   * @param pDocument all indexable fields are defined here.
   */
  void addDocument(final Document pDocument);

  /**
   * Update index for an existing object.
   * @param pTerm which felds need to update.
   * @param pDocument index new modified document
   */
  void updateDocument(final Term pTerm,
                      final Document pDocument);

  /**
   * Delete an index from indexer.
   * @param pIdTerm find deletable document
   */
  void deleteDocument(final Term pIdTerm);

  /**
   * Delete all index files. <b>This is very harmful task.</b> it will remove
   * all index files. that mean you will loss all previouse index.<br>
   * though you can find your old index files under a new directoy with date
   * stamp. <br>
   * @param pConfirmation intentially given argument, to double ensure about
   *        the user intention.
   * @throws IOException when failed to delete any file.
   */
  public void deleteIndexFiles(final boolean pConfirmation) throws IOException;

  /**
   * Perform Lucene index optimization task.
   */
  void optimize();

}
