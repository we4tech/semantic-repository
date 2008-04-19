/* $Id$ */
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
 * $LastChangedBy$
 * $LastChangedDate$
 * $LastChangedRevision$
 ******************************************************************************
*/
package com.ideabase.repository.webservice.handler;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.HashMap;

import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.beans.BeansException;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

/**
 * Support restful url.
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class RESTUrlHandlerMapping extends AbstractUrlHandlerMapping {

  private final Logger LOG = LogManager.getLogger(getClass());
  private Map<String, Object> mPatternMap = new HashMap<String, Object>();

  public void setPatternMap(final Map<String, Object> pPatternMap) {
    mPatternMap = pPatternMap;
  }
  
  public Map<String, Object> getPatternMap() {
    return mPatternMap;
  }
  
  @Override
  protected Object lookupHandler(String pUri, HttpServletRequest pRequest)
      throws Exception {
    if (LOG.isDebugEnabled()) {
      LOG.debug("lookup handler - " + pUri + ", " + pRequest);
    }
    final Object controller = lookupHandlerInternal(pUri);
    if (controller != null) {
      return controller;
    } else {
      LOG.debug("No pattern matches.");
    }
    return super.lookupHandler(pUri, pRequest);
  }

  private Object lookupHandlerInternal(final String pUri) {
    for (final Map.Entry<String, Object> map : mPatternMap.entrySet()) {
      final String pattern = map.getKey();
      final Object controller = map.getValue();
      if (pUri.matches(pattern)) {
        LOG.debug("Pattern - " + pattern + " matches uri - " + pUri);
        return controller;
      }
    }
    return null;
  }
}
