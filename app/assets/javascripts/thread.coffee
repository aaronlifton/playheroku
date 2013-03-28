$(document).ready ->
  $("#messageSubmit").click (e) ->
    e.preventDefault()
    $.post "/messages/"+thread_id,
      body: $("#body").val()
      (data) -> append_message $("#body").val(), data

  return

append_message = (body, id) ->
  link = $("<div class='well'>").text id + " - " + body
  $("#messages").append link
