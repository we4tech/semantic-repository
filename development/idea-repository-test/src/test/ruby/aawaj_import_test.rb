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

require 'rubygems'
require "test/unit"
require 'mysql'
require "benchmark"

# Set repository URL
REPOSITORY_URL = "http://localhost:1990/rest/service/"

# repository vendor configuraton
REPOSITORY_VENDOR = "swiad"

# repository user configuration
REPOSITORY_USER = "mac1"
REPOSITORY_PASSWORD = "mac1"

require '/Users/nhmtanveerhossainkhanhasan/projects/ideabase-products/semantic-repository/development/idea-repository-ws-ruby-client/src/main/ruby/restful_repository.rb'

class AwaajImportTest < Test::Unit::TestCase

  @connection = nil
  @repository_client = nil
  @active_auth_token = nil
  def setup
    @connection = Mysql::new("localhost", "root", "", "aawaj")
    @repository_client = RESTfulRepository::Client.new
    state, @active_auth_token = @repository_client.login(REPOSITORY_USER, REPOSITORY_PASSWORD)
  end

  def test_import_aawaj_database
    result = @connection.query("select * from sme_user_profile ORDER BY id ASC LIMIT 24000 ");
    benchmark("first 100 items ") do
      result.each do |row|
        benchmark("each row indexing is ") do
          item = RESTfulRepository::Item.new
          id = row[0]
          user_type = row[1]
          user_name = row[2]
          first_name = row[3]
          last_name = row[4]
          gender = row[5]
          location = row[6]
          date_of_birth = row[9]
          age = row[10]
          aboutme = row[11]
          profession = row[12]
          phone = row[13]
          status = row[14]
          email = row[18]
          address = row[19]
          title = row[20]
          post_code = row[22]
          created = row[24]
          reg_by = row[25]
          port = row[26]
          has_profile = row[29]

          item.title = user_name
          item[:database_id] = id
          item[:user_name] = user_name
          item[:user_type] = user_type
          item[:first_name] = first_name
          item[:last_name] = last_name
          item[:gender] = gender
          item[:location] = location
          item[:date_of_birth] = date_of_birth
          item[:age_number] = age
          item[:aboutme] = aboutme
          item[:profession] = profession
          item[:phone] = phone
          item[:status] = status
          item[:email] = email
          item[:address] = address
          item[:job_title] = title
          item[:post_code] = post_code
          item[:created] = created
          item[:reg_by] = reg_by
          item[:port] = port
          item[:has_profile] = has_profile

          @repository_client.save(@active_auth_token, item)
        end
      end
    end
  end

  def benchmark(p_msg = nil)
    started_time = Time.now.to_f
    yield
    puts "#{p_msg} ended in - #{(Time.now.to_f - started_time).to_s[0..5]} seconds."
  end

  def teardown
    @connection.close
  end
end