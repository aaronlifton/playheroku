$(document).ready ->
  $.get "/threads", (data) ->
    $.each data, (index, item) ->
      append_thread item.name, item.id

  $("#threadSubmit").click (e) ->
    e.preventDefault()
    $.post "/threads",
      name: $("#name").val()
      (data) -> append_thread $("#name").val(), data.id

  return

append_thread = (name, id) ->
  link = $("<a>").text(name).attr("href","/threads/"+id.id)
  $("#threads").append $("<li>").append link