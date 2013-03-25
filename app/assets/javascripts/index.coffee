members = []
Pusher.channel_auth_transport = "ajax"
pusher = undefined
channel = undefined
  
$(document).ready ->
  pusher = new Pusher("55b5e3dcfcf7f9ac7bb9")
  channel = pusher.subscribe("thread-channel-999")
  
  channel.bind "thread_create", (thread) ->
    append_thread thread.name
    # plummet()

  $.get "/threads", (data) ->
    $.each data, (index, item) ->
      append item.name

  $("#threadSubmit").click (e) ->
    e.preventDefault()
    $.post "/threads",
      name: $("#name").val()
      (data) -> append_thread $("#name").val()

  return

append_thread = (text) ->
	$("#threads").append $("<li>").text $("#threadSubmit").val()
  