var append_thread, pusher, channel;

// Enable pusher logging - don't include this in production

Pusher.log = function(message) {
  if (window.console && window.console.log) window.console.log(message);
};

// Flash fallback logging - don't include this in production
WEB_SOCKET_DEBUG = true;

$(document).ready(function() {
	
  $.get("/threads", function(data) {
    return $.each(data, function(index, item) {
      return append_thread(item.name, item.id.id);
    });
  });
  
  $("#threadSubmit").click(function(e) {
    e.preventDefault();
    return $.post("/threads", {
      name: $("#name").val()
    }, function(data) {
      return append_thread($("#name").val(), data);
    });
  });

  pusher = new Pusher('1a90d7d1e1ce1c909125');
  channel = pusher.subscribe('messages');
  channel.bind('new-thread', function(data) {
    alert(data);
  });
  
});

append_thread = function(name, id) {
  var link, trel;
  link = $("<a>").text(name).attr("href", "/threads/" + id);
  trel = $('<tr>\
	      <td><input type="checkbox"> <a href="#"><i class="icon-star"></i></a></td>\
	      <td><strong>John Doe</strong></td>\
	      <td><span class="label pull-right"></span></td>\
	      <td class="thread-name"><strong><a href="' + "/threads/" + id + '">' + name + '</a></strong></td>\
	      <td><strong>Sept3</strong></td>\
	    </tr>');
  // channel.trigger('new-thread', { 'name': name });
  return $("#threads").append($(trel));
};
