$(document).ready ->
  $.get "/threads", (data) ->
    $.each data, (index, item) ->
      $("#threads").append $("<li>").text item.name

  $("#threadSubmit").click (e) ->
    e.preventDefault()
    $.post "/threads",
      name: $("#name").val()
      (data) -> $("#threads").append $("<li>").text $("#name").val()
  return
  