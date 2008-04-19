/* $Id: DateFormatter.java 250 2008-01-07 10:18:29Z hasan $ */
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
package com.ideabase.repository.common;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Common date formatter for web service layer.<Br>
 * <b>Date format - dd/MM/yyyy HH:mm:ss</b>
 * @author <a href="mailto:hasan@somewherein.net">nhm tanveer hossain khan (hasan)</a>
 */
public class DateFormatter {
  /**
   * Date format for parsing requested date stamp. dd/MM/yyyy HH:mm:ss
   */
  private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
  private static final SimpleDateFormat DATE_FORMATTER =
      new SimpleDateFormat(DATE_FORMAT);

  /**
   * Convert {@see Timestamp} to formatted {@see String}.
   * @param pTimestamp time stamp.
   * @return formatted string date.
   */
  public static String formatDate(final Timestamp pTimestamp) {
    return DATE_FORMATTER.format(pTimestamp);
  }

  /**
   * Parse {@see String} date to formatted {@see Date}.
   * @param pDate String formatted date.
   * @return convert to {@see Date}
   * @throws ParseException if parsing failed.
   */
  public static Date parseDate(final String pDate)
      throws ParseException {
    return DATE_FORMATTER.parse(pDate);
  }

  /**
   * Parse {@see string} date to formatted {@see Timestamp}.
   * @param pDate string date
   * @return {@see Timestamp} date.
   * @throws ParseException if parsing failed.
   */
  public static Timestamp parseTimestamp(final String pDate)
      throws ParseException {
    return new Timestamp(parseDate(pDate).getTime());
  }
}
