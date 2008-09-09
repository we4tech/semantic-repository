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

package com.ideabase.repository.core.index.function.impl;

import com.ideabase.repository.core.index.function.TermValueEmbedFunction;
import com.ideabase.dictionary.core.LanguagePhoneticConverter;

/**
 * Convert string into phonetically encoded form. for example - হবার -> hober
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class PhoneticEncodeEmbedFunctionImpl implements TermValueEmbedFunction {

  private final LanguagePhoneticConverter mLanguagePhoneticConverter;

  public PhoneticEncodeEmbedFunctionImpl(
      final LanguagePhoneticConverter pLanguagePhoneticConverter) {
    mLanguagePhoneticConverter = pLanguagePhoneticConverter;
  }

  public String call(final String pValue) {
    return mLanguagePhoneticConverter.convert(pValue);
  }
}
