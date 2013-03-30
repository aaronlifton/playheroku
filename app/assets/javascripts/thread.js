var append_message, pusher, channel;

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
  channel = pusher.subscribe('messages');
  channel.bind('new-message', function(data) {
    alert(data);
  });
  
});

append_message = function(body, id) {
  var link;
  link = $("<div class='well'>").text(id + " - " + body);
  channel.trigger('new-message', { 'body': body });
  return $("#messages").append(link);
};