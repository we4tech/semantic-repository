/* $Id: RepositoryItemSearchTest.java 250 2008-01-07 10:18:29Z hasan $ */
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
 * $LastChangedDate: 2008-01-07 16:18:29 +0600 (Mon, 07 Jan 2008) $
 * $LastChangedRevision: 250 $
 ******************************************************************************
*/
package com.ideabase.repository.test.search;

import com.ideabase.repository.test.BaseTestCase;
import com.ideabase.repository.core.search.RepositoryItemSearch;
import com.ideabase.repository.core.CoreServiceKey;
import com.ideabase.repository.common.object.ObjectBase;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.Term;

/**
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class RepositoryItemSearchTest extends BaseTestCase {

  private RepositoryItemSearch mRepositoryItemSearch;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mRepositoryItemSearch = (RepositoryItemSearch) mContext.
        getBean(CoreServiceKey.REPOSITORY_ITEM_SEARCH);
  }

  public void testSearch() {
    Query query = new TermQuery(new Term(ObjectBase.INDEX_FIELD_ID, "1112"));
    /*List<Hit> result = mRepositoryItemSearch.search(query);
    assertNotNull("No search result returned.", result);
    LOG.debug(result);*/
  }
}
