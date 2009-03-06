/* $Id: RepositoryItemIndexTest.java 253 2008-03-10 18:04:01Z hasan $ */
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
 * $LastChangedDate: 2008-03-10 23:34:01 +0530 (Mon, 10 Mar 2008) $
 * $LastChangedRevision: 253 $
 ******************************************************************************
*/
package com.ideabase.repository.test.index;

import java.io.IOException;

import com.ideabase.repository.test.BaseTestCase;
import com.ideabase.repository.core.index.RepositoryItemIndex;
import com.ideabase.repository.core.CoreServiceKey;
import com.ideabase.repository.api.RepositoryService;
import com.ideabase.repository.api.APIServiceKey;
import com.ideabase.repository.common.object.Hit;
import com.ideabase.repository.common.object.PaginatedList;
import com.ideabase.repository.common.Query;
import org.springmodules.lucene.index.support.SimpleIndexFactoryBean;
import org.springmodules.lucene.index.factory.SimpleIndexFactory;

/**
 * Test {@code RepositoryIndex}
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class RepositoryItemIndexTest extends BaseTestCase {

  private RepositoryItemIndex mRepositoryItemIndex;
  private RepositoryService mRepositoryService;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mRepositoryItemIndex = (RepositoryItemIndex) mContext.
        getBean(CoreServiceKey.REPOSITORY_ITEM_INDEX);
    mRepositoryService = (RepositoryService) mContext.
        getBean(APIServiceKey.REPOSITORY_SERVICE);
  }

  public void testIndexSearch() {
    final Query q = new Query() {{
      and("title", "hossain");
      and("password", "123456");
    }};
    final PaginatedList<Hit> hits = mRepositoryService.getItemsByQuery(q);
    assertFalse("No hits found", hits == null || hits.isEmpty());
    LOG.debug("Hits - " + hits);
  }

  public void testDestroyIndex() throws IOException {
    LOG.debug("Destroy index.");
    mRepositoryService.destroyIndex(true);
  }

  public void testFindItemsFromSpecificIndex() {
    final Query query = new Query() {{
      and("title", "test");
    }};
    final PaginatedList list = mRepositoryService.getItemsByQuery(query);
  }
}