# $Id$
# *****************************************************************************
# Copyright (C) 2005 - 2007 somewhere in .Net ltd.
# All Rights Reserved.  No use, copying or distribution of this
# work may be made except in accordance with a valid license
# agreement from somewhere in .Net LTD.  This notice must be included on
# all copies, modifications and derivatives of this work.
# *****************************************************************************
# $LastChangedBy$
# $LastChangedDate$
# $LastChangedRevision$
# *****************************************************************************

require "test/unit"

class  BaseTest < Test::Unit::TestCase

  def setup
    @m_context = org.springframework.context.support.ClassPathXmlApplicationContext.new
    raise @m_context
  end

  def teardown
  end


end