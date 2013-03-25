$(document).ready ->
  $.get "/threads", (data) ->
    $.each data, (index, item) ->
      append_thread item.name

  $("#threadSubmit").click (e) ->
    e.preventDefault()
    $.post "/threads",
      name: $("#name").val()
      (data) -> append_thread $("#name").val()

  return

append_thread = (name) ->
	$("#threads").append $("<li>").text name
  