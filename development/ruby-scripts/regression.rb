require "rubygems"
require 'ruby-growl'

class NotificationService
  NOTIFICATION_TYPE = "repository-regration-test-server"
  APP_NAME = "repository-regression-test-server"
  @@connected = false
  
  def self.connect(p_host = "localhost", p_password = nil)
    @growl = Growl.new(p_host, APP_NAME, [NOTIFICATION_TYPE], nil, p_password)
    if @growl
      @@connected = true
    end
  end
  
  def self.notify(p_title, p_message)
    if !@@connected
      connect()
    end
    if @@connected
      @growl.notify(NOTIFICATION_TYPE, p_title, p_message)
    end
  end
end
NotificationService.notify("Hello", "World")
# execute ping service
fork do
  file_name = Time.now.to_i
  system(%{
       echo file name - #{file_name}
       ping www.yahoo.com > /tmp/#{file_name}
       rm /tmp/#{file_name}
       })
end

