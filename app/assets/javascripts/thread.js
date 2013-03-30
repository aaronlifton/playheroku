var append_message, pusher, channel;

// Enable pusher logging - don't include this in production

Pusher.log = function(message) {
  if (window.console && window.console.log) window.console.log(message);
};

// Flash fallback logging - don't include this in production
WEB_SOCKET_DEBUG = true;

$(document).ready(function() {
	
  $("#messageSubmit").click(function(e) {
    e.preventDefault();
    return $.post("/messages/" + thread_id, {
      body: $("#body").val()
    }, function(data) {
      return append_message($("#body").val(), data);
    });
  });
  
  pusher = new Pusher('1a90d7d1e1ce1c909125');
  channel = pusher.subscribe('private-messages');
  channel.bind('client-new-message', function(data) {
    alert(data);
  });
  
});

append_message = function(body, id) {
  var link;
  link = $("<div class='well'>").text(id + " - " + body);
  // channel.trigger('client-new-message', { body: body });
  return $("#messages").append(link);
};