require "net/http"
require "uri"
require "rexml/document"

module RESTfulRepository

  # Base repository item.
  class Item
    attr_reader :id, :title, :created_on, :last_updated_on, :fields, :uri, :target_class

    def initialize
      @fields = {}
      @related_items = {}
    end

    def id=(p_id)
      unless p_id.nil?
        @id = p_id
      end
    end

    def created_on=(p_created_on)
      unless p_created_on.nil?
        @created_on = p_created_on
      end
    end

    def last_updated_on=(p_last_updated_on)
      unless p_last_updated_on.nil?
        @last_updated_on = p_last_updated_on
      end
    end

    def title=(p_title)
      unless p_title.nil?
        @title = p_title
      end
    end

    def uri=(p_uri)
      unless p_uri.nil?
        @uri = p_uri
      end
    end

    def target_class=(p_target_class)
      unless p_target_class.nil?
        @target_class = p_target_class
      end
    end

    def add_related_items(p_related_items)
      unless p_related_items.nil?
        p_related_items.each do |key, value|
          group_name = key
          @related_items[key] = value
        end
      end
    end

    def delete_related_items(p_key)
      unless p_key.nil?
        @related_items[p_key]
      end
    end

    def get_related_items
      @related_items
    end

    def get_fields
      @fields
    end

    def <<(p_arg)
      key = p_arg.keys[0]
      value = p_arg[key]
      unless key.is_a?(String)
        key = key.id2name
      end
      @fields[key] = value
    end

    def [](p_key)
      key = p_key.to_s
      puts key.to_s
      if @fields.has_key?(key)
        @fields[key]
      else
        return nil
      end
    end

    def []=(p_key, p_value)
      @fields[p_key.to_s] = p_value
    end

    def size
      @fields.size
    end

    def each
      @fields.each do |key, value|
        yield(key, value)
      end
    end

    public
    def to_s
      s = "{\r\n\tid:#{id},\r\n\ttitle: #{title},\r\n\tcreated_on: #{created_on},\r\n\tlast_updated_on: #{last_updated_on},\r\n\tfields: \r\n"
      unless @fields.nil?
        @fields.each do |key, value|
          s += "\t\t#{key} = #{value}\r\n"
        end
      end
      s += "\trelated_items : #{@related_items}\r\n"
      s += "\ttarget_class : #{@target_class}\r\n"
      s += "}"
    end
  end

  class SearchResult
    attr_reader :max_rows, :page_count

    def initialize
      @items = []
      @max_rows = 0
      @page_count = 0
    end

    def max_rows=(p_max_rows)
      @max_rows = p_max_rows
    end

    def page_count=(p_page_count)
      @page_count = p_page_count
    end

    def <<(p_item_id)
      @items.push(p_item_id)
    end

    def each
      @items.each do |item|
        yield(item)
      end
    end

    def get_items
      @items
    end

    def to_s
      s = "{items: "
      each {|item|
        s += item +", "
      }
      s += "page-count: #{@page_count}, max-rows: #{@max_rows}"
      s += "}"
      return s
    end
  end

  class Client

    # constants for header and parameter name
    HEADER_SET_COOKIE = "set-cookie"
    HEADER_COOKIE = "cookie"

    # XML elements and attributes
    ATTR_STATE = "state"
    ELE_ITEM = "item"
    ELE_ITEMS = "items"
    ELE_MAX_ROWS = "max-rows"
    ELE_PAGE_COUNT = "page-count"
    ELE_ID = "id"
    ELE_TITLE = "title"
    ELE_CREATED_ON = "createdOn"
    ELE_LAST_UPDATED_ON = "lastUpdatedOn"
    ELE_FIELDS = "fields"
    ELE_FIELD = "field"
    ATTR_NAME = "name"
    ELE_RELATED_ITEMS = "relatedItems"
    ELE_REQUEST = "request"
    ELE_TARGET_CLASS = "target-class"

    # date format which is used to convert a date and parsing a string to date
    DATE_FORMAT = "%d/%m/%Y %H:%M:%S"

    # indicate all related items must be loaded
    PARAM_LOAD_RELATED_ITEMS = "load_related_items"

    # repository restful web service uri
    @@m_service_url = URI.parse("http://localhost:8080/repository/rest/service/")
    # request and response preferred format.
    @@m_content_format = "xml"

    # authentication service uri
    @@m_service_auth = "login/user&password.#{@@m_content_format}"
    # content retrieval service uri
    @@m_service_get = "get/"
    # content retrieval related find service uri
    @@m_service_find = "find/query"
    # object update service uri
    @@m_service_update = "update/item"
    # object save service uri
    @@m_service_save = "save/item"

    # object delete service uri
    @@m_service_delete = "delete/"

    # object registration service uri
    @@m_service_register = "register/user"

    # object get related items service uri
    @@m_service_find_related_items = "find-related-items/"

    # object add related items service uri
    @@m_service_add_related_items = "add-related-items/item"

    # object delete related items service uri
    @@m_service_delete_related_items = "delete-related-items/item"

    # set recently created cookies
    @@m_cookies = nil

    private
    def build_item(p_xml_content)
      puts p_xml_content
      document = REXML::Document.new(p_xml_content)
      root_element = document.root
      response_state = root_element.attributes[ATTR_STATE]
      # successfull request
      if response_state
        puts("Item retrieved from repository")
        root_element.each_element do |item|
          if item.name == ELE_ITEM
            g_item = Item.new
            item.each_element do |node|
              case node.name
              when ELE_ID
                g_item.id = node.text
              when ELE_TITLE
                g_item.title = node.text
              when ELE_CREATED_ON
                g_item. created_on = DateTime.strptime(node.text, DATE_FORMAT)
              when ELE_LAST_UPDATED_ON
                g_item.last_updated_on = DateTime.strptime(node.text, DATE_FORMAT)
              when ELE_FIELDS
                node.each_element do |field|
                  name = field.attributes[ATTR_NAME]
                  value = field.text
                  g_item << {name => value}
                end
              when ELE_TARGET_CLASS
                g_item.target_class = node.text
              when ELE_RELATED_ITEMS
                related_items = {}
                node.each_element {|group|
                  group_name = group.name
                  group_items = []
                  group.each_element {|item_id|
                    group_items.push(item_id.text.to_i)
                  }
                  related_items[group_name] = group_items
                }
                g_item.add_related_items(related_items)
              end
            end
            return g_item
          end
        end
      end
    end

    def state_true?(p_item, p_response)
      puts "Respose - #{p_response}"
      document = REXML::Document.new(p_response)
      root = document.root
      state = root.attributes[ATTR_STATE]
      unless p_item.nil?
        root.each_element do |item|
          p_item.uri = item.text
          # parse item id
          p_item.id = p_item.uri[/\d+/]
        end
      end
      response_state = state == "true"
      if response_state and block_given?
        yield(root)
      end
      return response_state
    end

    def post_request(p_service_uri, p_request, *p_header_exclude)
      puts "URI - #{p_service_uri}, request - #{p_request}"
      @response = Net::HTTP.start(@@m_service_url.hosts,
                                  @@m_service_url.port) {|http|
        headers = {}
        if p_header_exclude.nil? or p_header_exclude == false
          unless @@m_cookies.nil?
            headers[HEADER_COOKIE] = @@m_cookies
          end
        end
        http.post(p_service_uri, p_request, headers)
      }
    end

    def delete_request(p_service_uri)
      puts "URI - #{p_service_uri}"
      raise "No cookies exists" if @@m_cookies.nil?
      @response = Net::HTTP.start(@@m_service_url.hosts,
                                  @@m_service_url.port) {|http|
        http.delete(p_service_uri, {HEADER_COOKIE => @@m_cookies})
      }
    end

    def get_request(p_service_uri)
      puts "URI - #{p_service_uri}"
      raise "No cookies exists" if @@m_cookies.nil?
      @response = Net::HTTP.start(@@m_service_url.hosts,
                                  @@m_service_url.port) {|http|
        http.get(p_service_uri, {HEADER_COOKIE => @@m_cookies})
      }
    end

    # build xml from the item object
    def build_xml(p_item)
      xml = "<#{ELE_ITEM}>\n"
      unless p_item.id.nil?
        xml += "\t<#{ELE_ID}>#{p_item.id}</#{ELE_ID}>\n"
      end
      xml += "\t<#{ELE_TITLE}>#{p_item.title}</#{ELE_TITLE}>\n"
      xml += "\t<#{ELE_CREATED_ON}><![CDATA[#{format_date(p_item.created_on)}]]></#{ELE_CREATED_ON}>\n"
      xml += "\t<#{ELE_LAST_UPDATED_ON}><![CDATA[#{format_date(p_item.last_updated_on)}]]></#{ELE_LAST_UPDATED_ON}>\n"
      xml += "\t<#{ELE_FIELDS}>\n"
      p_item.get_fields.each do |name, value|
        xml += "\t\t<#{ELE_FIELD} #{ATTR_NAME}='#{name}'><![CDATA[#{value}]]></#{ELE_FIELD}>\n"
      end
      xml += "\t</#{ELE_FIELDS}>\n"

      # add related items
      related_items = p_item.get_related_items
      unless related_items.nil? and related_items.empty?
        xml += "<#{ELE_RELATED_ITEMS}>\n"
        related_items.each do |key, item_ids|
          xml += "<#{key}>\n"
          item_ids.each do |item_id|
            xml += "<#{ELE_ITEM}>"
            xml += item_id.to_s
            xml += "</#{ELE_ITEM}>\n"
          end
          xml += "</#{key.to_s.downcase}>\n"
        end
        xml += "</#{ELE_RELATED_ITEMS}>\n"
      end

      # set target class
      unless p_item.target_class.nil?
        xml += "<#{ELE_TARGET_CLASS}>"
        xml += p_item.target_class
        xml += "</#{ELE_TARGET_CLASS}>"
      end

      xml += "</#{ELE_ITEM}>\n"
    end

    def format_date(p_date_time)
      if p_date_time.nil?
        DateTime.now.strftime(DATE_FORMAT)
      else
        p_date_time.strftime(DATE_FORMAT)
      end
    end

    def build_search_result(p_search_result, p_root)
      p_root.each_element {|node|
        case node.name
          when ELE_ITEMS
            node.each_element {|item|
              p_search_result << item.text[/\d+/]
            }
          when ELE_PAGE_COUNT
            p_search_result.page_count = node.text.to_i
          when ELE_MAX_ROWS
            p_search_result.max_rows = node.text.to_i
        end
      }
      p_search_result
    end

    # create http request to the repository web service.
    # and pass user id and password, if authentication is successful
    # return *true* and keep the cookies in global variable
    # otherwise return false.
    public
    def login(p_user, p_password)
      params = "?user=#{p_user}&password=#{p_password}"
      service_uri = @@m_service_url.path + @@m_service_auth + params
      @response = Net::HTTP.start(@@m_service_url.hosts,
          @@m_service_url.port) {|http|
        http.get(service_uri)
      }
      @@m_cookies = @response.header[HEADER_SET_COOKIE]
      state_true?(nil, @response.body)
    end

    # retrieve a specific item by the given item id from the reposotry.
    def get(p_item_id, p_args)
      service_uri = "#{@@m_service_url.path}#{@@m_service_get}#{p_item_id}.#{@@m_content_format}"

      # verify user intention about related items, if :related_items flag is set
      # load all related items
      if p_args.nil? == false and p_args[:related_items] == true
        service_uri += "?#{PARAM_LOAD_RELATED_ITEMS}=true"

        # verify whether user has set any offset
        offset = p_args[:offset]
        unless offset.nil?
          service_uri += "&offset=#{offset.to_s}"
        end

        # verify whether user has set any maximum number of related items
        max = p_args[:max]
        unless max.nil?
          service_uri += "&max=#{max}"
        end

        # verify whether user has set the accepted relation types
        relation_types_array = p_args[:relation_types]
        unless relation_types_array.nil? and relation_types_array.empty?
          relation_types = ""
          relation_types_array.each do |relation_type|
            if relation_types.size > 0
              relation_types += "," + relation_type
            else
              relation_types += relation_type
            end
          end
          service_uri += "&relation_types=#{relation_types}"
        end
      end
      puts service_uri
      raise "No cookies exists" if @@m_cookies.nil?
      @response = Net::HTTP.start(@@m_service_url.hosts,
          @@m_service_url.port) {|http|
        http.get(service_uri, {HEADER_COOKIE => @@m_cookies})
      }
      begin
        xml_content = @response.body
        return build_item(xml_content)
      end
    end

    # perform search on the repository
    def find(p_args)
      query = p_args[:query]
      max = p_args[:max]
      offset = p_args[:offset]

      # verify the arguments
      raise "NO query defined" if query.nil?
      query = URI.escape(query)
      if max.nil?
        max = 100020
      end
      if offset.nil?
        offset = 0
      end

      # build service uri
      params = "query=#{query}&offset=#{offset}&max=#{max}"
      service_uri = "#{@@m_service_url.path}#{@@m_service_find}.#{@@m_content_format}?" + params
      search_result = SearchResult.new
      @response = get_request(service_uri)
      state_true?(nil, @response.body) do |root_element|
        build_search_result(search_result, root_element)
      end
      search_result
    end

    # store an item to the repository
    # return true if service request is successful
    def save(p_item)
      # build request string
      request = "<#{ELE_REQUEST}>\n"

      # set target class if it is defined
      unless p_item.target_class.nil?
        request += "<#{ELE_TARGET_CLASS}>"
        request += p_item.target_class
        request += "</#{ELE_TARGET_CLASS}>"
      end

      request += build_xml(p_item)
      request += "</#{ELE_REQUEST}>\n"

      # build service uri
      service_uri = "#{@@m_service_url.path}#{@@m_service_update}.#{@@m_content_format}"

      # if no user id is defined treat it as a new object.
      if p_item.id.nil?
        service_uri = "#{@@m_service_url.path}#{@@m_service_save}.#{@@m_content_format}"
      end

      # send http request
      state_true?(p_item, post_request(service_uri, "item=#{request}").body)
    end

    # remove an existing repository item
    def delete(p_item_id)
      service_uri = "#{@@m_service_url.path}#{@@m_service_delete}#{p_item_id}.#{@@m_content_format}"
      state_true?(nil, delete_request(service_uri).body)
    end

    # add related items
    def add_related_items(p_item)
      # build request string
      request = "<#{ELE_REQUEST}>\n"
      request += build_xml(p_item)
      request += "</#{ELE_REQUEST}>\n"

      # build service uri
      service_uri = "#{@@m_service_url.path}#{@@m_service_add_related_items}.#{@@m_content_format}"

      # send http request
      state_true?(p_item, post_request(service_uri, "item=#{request}").body)
    end

    # add related items
    def delete_related_items(p_item)
      # build request string
      request = "<#{ELE_REQUEST}>\n"
      request += build_xml(p_item)
      request += "</#{ELE_REQUEST}>\n"

      # build service uri
      service_uri = "#{@@m_service_url.path}#{@@m_service_delete_related_items}.#{@@m_content_format}"

      # send http request
      state_true?(p_item, post_request(service_uri, "item=#{request}").body)
    end

    # find related items from a specific item
    def find_related_items(p_args)
      relation_type = p_args[:relation_type]
      item_id = p_args[:item_id]
      offset = p_args[:offset]
      max = p_args[:max]

      unless relation_type.nil?
        service_uri = "#{@@m_service_url.path}#{@@m_service_find_related_items}#{relation_type}&#{item_id}.#{@@m_content_format}"
        params = nil
        unless offset.nil?
          params = "?offset=#{offset}"
        end
        unless max.nil?
          if params.nil?
            params = "?max=#{max}"
          else
            params += "&max=#{max}"
          end
        end
        service_uri += params
      end
      search_result = SearchResult.new
      state_true?(nil, post_request(service_uri, nil).body) {|root|
        build_search_result(search_result, root)
      }
      search_result
    end

    # register a new user
    def register(p_item)
      # build request string
      request = "<#{ELE_REQUEST}>\n"
      request += build_xml(p_item)
      request += "</#{ELE_REQUEST}>\n"

      # build service uri
      service_uri = "#{@@m_service_url.path}#{@@m_service_register}.#{@@m_content_format}"

      # send http request
      state_true?(p_item, post_request(service_uri, "user=#{request}", true).body) {|root|
        root.each_element do |node|
          if node.name == ELE_ITEM
            return true, node.text[/\d+/]
          end
        end
      }
    end
  end
end