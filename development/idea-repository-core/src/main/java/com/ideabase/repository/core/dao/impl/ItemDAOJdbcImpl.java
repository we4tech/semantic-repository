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

package com.ideabase.repository.core.dao.impl;

import com.ideabase.repository.core.dao.ItemDAO;
import com.ideabase.repository.core.dao.Item;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
 * {@see ItemDAO} plain Jdbc based implementation.
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class ItemDAOJdbcImpl extends JdbcTemplate implements ItemDAO{

  private final String mTableName;

  public ItemDAOJdbcImpl(final String pTableName,
                         final DataSource pDataSource) {
    super(pDataSource);
    mTableName = pTableName;
  }

  public Integer createItem(final Item pItem) {
    final StringBuilder queryBuilder = new StringBuilder();
    /*
    INSERT INTO item(
      title,
      document,
      createdOn,
      lastUpdatedOn,
      indexRepository
    ) VALUES (
      #title#,
      #document#,
      #createdOn#,
      #lastUpdatedOn#,
      #indexRepository#
    )
    */
    queryBuilder.append("INSERT INTO ").append(mTableName)
      .append("(title, document, createdOn, lastUpdatedOn, indexRepository)")
      .append("VALUES(?, ?, ?, ?, ?)");
    final int changeCount = update(queryBuilder.toString(),
      new Object[] {
        pItem.getTitle(),
        pItem.getDocument(),
        pItem.getCreatedOn(),
        pItem.getLastUpdatedOn(),
        pItem.getIndexRepository()});
    return changeCount;
  }

  public void updateItem(final Item pItem) {
    throw new NoSuchMethodError("this method is not yet implemented");
  }

  public void deleteItem(final Item pItem) {
    throw new NoSuchMethodError("this method is not yet implemented");
  }

  public void deleteItemById(final Integer pId) {
    throw new NoSuchMethodError("this method is not yet implemented");
  }

  public List<Item> findItems(final Item pItem, final Integer pSkip, final Integer pMax) {
    throw new NoSuchMethodError("this method is not yet implemented");
  }

  public Item findItem(final int pItemId) {
    throw new NoSuchMethodError("this method is not yet implemented");
  }

  public int countItems(final Item pItem) {
    throw new NoSuchMethodError("this method is not yet implemented");
  }

  public void startTransaction() {
    throw new NoSuchMethodError("this method is not yet implemented");
  }

  public void endTransaction() {
    throw new NoSuchMethodError("this method is not yet implemented");
  }

  public void commitTransaction() {
    throw new NoSuchMethodError("this method is not yet implemented");
  }
}
