class ApplicationController < ActionController::Base
  protect_from_forgery
end

require 'date'
class Date
  def dayname
    DAYNAMES[self.wday]
  end
end

