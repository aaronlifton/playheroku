$(document).ready ->
  $.get "/threads", (data) ->
    $.each data, (index, item) ->
      append_thread item.name, item.id.id

  $("#threadSubmit").click (e) ->
    e.preventDefault()
    $.post "/threads",
      name: $("#name").val()
      (data) -> append_thread $("#name").val(), data

  return

append_thread = (name, id) ->
  link = $("<a>").text(name).attr("href","/threads/"+id)
  trel = $('<tr>
	      <td><input type="checkbox"> <a href="#"><i class="icon-star"></i></a></td>
	      <td><strong>John Doe</strong></td>
	      <td><span class="label pull-right"></span></td>
	      <td class="thread-name"><strong><a href="' + "/threads/"+id + '">' + name + '</a></strong></td>
	      <td><strong>Sept3</strong></td>
	    </tr>');
  $("#threads").append $(trel)
