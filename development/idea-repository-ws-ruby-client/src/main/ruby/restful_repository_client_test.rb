# restful_repository_client_test.rb
# August 30, 2007
#

$:.unshift File.join(File.dirname(__FILE__),'..','lib')

require 'test/unit'
require 'restful_repository.rb'
require "date"

class TestRestful_repository_client_test < Test::Unit::TestCase
  
  def setup
    @m_client = RESTfulRepository::Client.new
  end

  def teardown
  end

  # invoke - RESTfulRepository::Client.login
  # required - true
  def _test_login
    login_state = authenticate_user
    assert_equal true, login_state, "Failed to authenticate user"
  end

  def authenticate_user
    user = "we4tech3"
    password = "hasankhan"
    @m_client.login(user, password)
  end

  # invoke - RESTfulRepository::Client.get
  # required - Not null object of RESTfulRepository::Item
  # required - Not null object.id
  # required - Not null object.title
  # required - Not null object.created_on
  # required - Not null object.last_updated_on
  def _test_get
    authenticate_user
    item_id = 2253
    item = @m_client.get(item_id)

    puts item

    assert_not_nil item, "Item object is not found"
    assert_not_nil item.id, "Item id is not defined"
    assert_not_nil item.title, "Item title is not defined"
    assert_not_nil item.created_on, "Item created on is not defined"
    assert_not_nil item.last_updated_on, "Item last updated on is not defined"
  end

  # invoke - RESTfulRepository::Client.save
  # required - true
  # required - object.id
  # required - object.uri
  def _test_save
    authenticate_user
    item = RESTfulRepository::Item.new
    item.title = "hasan's profile"
    item[:fav_color] = "red"
    item[:fav_book] = "science fiction"
    item[:full_name] = "nhm tanveer hossain"
    save_state = @m_client.save(item)

    assert_equal true, save_state, "Failed to store a new object"
    assert_not_nil item.id, "Item id is not defined"
    assert_not_nil item.uri, "Item uri is not defined"
  end

  # invoke - RESTfulRepository::Client.save
  # required - true
  # required - object.id
  # required - object.uri
  # required - object.related_items
  def _test_save_with_relation
    authenticate_user
    item = RESTfulRepository::Item.new
    item.title = "hasan's profile"
    item[:fav_color] = "red"
    item[:fav_book] = "science fiction"
    item[:full_name] = "nhm tanveer hossain"
    item.add_related_items({:user => [2262, 2265],
                            :blog => [2255, 2249, 2248]})
    item.add_related_items({:user2 => [2262, 2265],
                            :blog2 => [2255, 2249, 2248]})
    puts item
    save_state = @m_client.save(item)

    assert_equal true, save_state, "Failed to store a new object"
    assert_not_nil item.id, "Item id is not defined"
    assert_not_nil item.uri, "Item uri is not defined"
  end

  # invoke - RESTfulRepository::Client.get
  # required - Not null object of RESTfulRepository::Item
  # required - Not null object.id
  # required - Not null object.title
  # required - Not null object.created_on
  # required - Not null object.last_updated_on
  # required - Not null object.get_related_items 
  def _test_get_with_relation
    authenticate_user
    item_id = 2273
    item = @m_client.get(item_id, {:related_items => true,
                                   :offset => 0,
                                   :max => 1,
                                   :relation_types => ["user", "blog"]})

    puts item

    assert_not_nil item, "Item object is not found"
    assert_not_nil item.id, "Item id is not defined"
    assert_not_nil item.title, "Item title is not defined"
    assert_not_nil item.created_on, "Item created on is not defined"
    assert_not_nil item.last_updated_on, "Item last updated on is not defined"
    assert_equal false, item.get_related_items.empty?, "Item doesn't hold the related items"
  end

  # invoke - RESTfulRepository::Client.find
  # required - Not null object
  # required - Not null object.page_count
  # required - Not null object.max_rows
  def _test_find
    authenticate_user
    result = @m_client.find(:query => "full_name: tanveer", :max => 1, :offset => 0)

    puts result
    
    assert_not_nil result, "NO search result found"
    assert_not_nil result.page_count, "NO page count set"
    assert_not_nil result.max_rows, "NO max rows set"
    assert_equal 1, result.get_items.size, "Search result size doesn't match"
  end

  # invoke - RESTfulRepository::Client.delete
  # required - true
  def _test_delete
    authenticate_user
    item_id = 2259
    state = @m_client.delete(item_id)

    assert_equal true, state, "Failed to remove an item"
  end

  # invoke - RESTfulRepository::Client.register
  # required - true,
  # required - user id
  def _test_register_user
    @item = RESTfulRepository::Item.new
    @item.title = "no"
    @item[:user] = "abc"
    @item[:password] = "abc"
    @item[:role_admin] = true
    @item[:role_read] = true
    @item[:role_write] = true
    @item.target_class = "user"

    status, user_id = @m_client.register(@item)

    puts "Status - #{status}, user_id = #{user_id}"
    assert_equal true, status, "Failed to register an user"
    assert_not_nil user_id, "NO user id defined"
  end

  # invoke - RESTfulRepository::Client.find
  # required - result
  def _test_find_related_items
    authenticate_user
    @result = @m_client.find_related_items(:relation_type => "blog",
                                           :item_id => 2273,
                                           :offset => 0,
                                           :max => 2)

    puts @result
    
    assert_not_nil @result, "NO result found"
    assert_equal 2, @result.get_items.size, "Item size doesn match the defined length"
  end

  # invoke - RESTfulRepository::Client.add_related_items
  # required - true
  def _test_add_related_items
    authenticate_user
    item = RESTfulRepository::Item.new
    item.id = 2266
    item.add_related_items(:blog => [2277])
    status = @m_client.add_related_items(item)

    assert_equal true, status, "Couldn't add new related items"
  end

  # invoke - RESTfulRepository::Client.delete_related_items
  # required - true
  def test_delete_related_items
    authenticate_user
    item = RESTfulRepository::Item.new
    item.id = 2266
    item.add_related_items(:blog => [2277])
    status = @m_client.delete_related_items(item)

    assert_equal true, status, "Couldn't remove an existing related items."
  end


end
