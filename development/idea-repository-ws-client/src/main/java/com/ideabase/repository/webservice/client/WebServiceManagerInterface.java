/* $Id: WebServiceManagerInterface.java 250 2008-01-07 10:18:29Z hasan $ */
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
package com.ideabase.repository.webservice.client;

import com.ideabase.repository.common.Query;
import com.ideabase.repository.common.object.User;
import com.ideabase.repository.common.object.GenericItem;
import com.ideabase.repository.common.object.ObjectBase;

/**
 * Inteface defined for web service manager.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public interface WebServiceManagerInterface extends WebServiceController {
  /**
   * Perform find search query, the retruned object will be parsed with the
   * specified mime type. for example: text/xml mime typed response will be
   * processed with xml response processor.<br>
   * @param pMime content mime type.
   * @param pQuery the query for finding out the object.
   * @return if invalid response format found it throws an unchecked exception
   *         otherwise this object is kept null.
   */
  SearchResult findContent(final String pMime, final Query pQuery);

  /**
   * Prepare web serice request for single item object.
   * @param pMime type of response content.
   * @param pRequest web servcie request.
   * @return the item object.
   */
  <T extends ObjectBase> T getItem(final String pMime,
                                   final WebServiceRequest pRequest);

  /**
   * Find item by item id.
   * @param pMime web sercie response type.
   * @param pItemId item id.
   * @return item object.
   */
  <T extends ObjectBase> T getItemById(final String pMime,
                                       final Integer pItemId);

  /**
   * Store item through the web service.
   * @param pItem the item object.
   * @param pMime response mime type.
   * @return return execution message and state.
   */
  <T extends GenericItem> ExecutionResponse saveItem(final String pMime,
                                                     final T pItem);

  /**
   * Add a list of new related ietms.
   * @param pMime response and request format.
   * @param pItem the host object, which holds the related item list.
   * @return the response is returned in a {@see ExecutionResponse} format.
   */
  ExecutionResponse addRelatedItems(final String pMime, final ObjectBase pItem);

  /**
   * Delete a specific item by specified item id.
   * @param pMime xml content type.
   * @param pItemId item id.
   * @return execution message or state is returned.
   */
  ExecutionResponse deleteItem(final String pMime, final Integer pItemId);

  /**
   * Delete a list of related items or a group of related items from an item.
   * @param pMime response and request format.
   * @param pItem item id.
   * @return web service response.
   */
  ExecutionResponse deleteRelatedItems(final String pMime,
                                       final GenericItem pItem);

  /**
   * Authentication using user name and password.
   * @param pUser user name.
   * @param pPassword user password.
   * @param pMime request format .
   * @return web service response returned.
   */
  ExecutionResponse login(final String pMime,
                          final String pUser,
                          final String pPassword);

  /**
   * Register new user object.
   * @param pMime request and response type.
   * @param pUser user object that needs to be registered.
   * @return the response which holds the list of objects.
   */
  ExecutionResponse register(final String pMime, final User pUser);

  /**
   * Return the list of related items form a specified item.
   * @param pMime response and request format.
   * @param pItemId item id.
   * @param pSkip skip number of rows.
   * @param pMaxRows maximum number of rows.
   * @return the list of related items are set to {@see ExectuionResponse}
   * @param pRelationType relation type.
   */
  SearchResult getRelatedItems(final String pMime,
                               final String pRelationType,
                               final Integer pItemId,
                               final Integer pSkip,
                               final Integer pMaxRows);
  /**
   * Retrieve {@see ObjectBase} with related item references.
   * @param pMime request format.
   * @param pItemId item id.
   * @param pSkip skip number of rows.
   * @param pMaxValue retrieve number of rows.
   * @param pRelationType relation type, here defining relation type is
   *        optional, if it is set to null. all related items will be retrieved,
   *        otherwise the specific groups, multiple types are defined by
   *        separating comma. for example -
   *        (blog, related-post, avatar, author...)
   * @return {@see ObjectBase} object instance.
   */
  <T extends ObjectBase> T getItemByIdAndRelatedItems(
      final String pMime, final Integer pItemId, final String pRelationType,
      final Integer pSkip, final Integer pMaxValue);

  /**
   * Return the active cookies.
   * @return active cookies. null will be returned
             {@see WebServiceManager#login()} wasn't invoked before.
   */
  String getCookies();

  /**
   * Return the base url of the remote web service.
   * @return the base url of the remote web service.
   */
  String getBaseUrl();
}
