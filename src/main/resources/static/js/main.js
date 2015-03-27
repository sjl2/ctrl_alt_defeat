$('#suggest').change(function(event) {

	var postParameters = { rawText: $('#suggest').val() };

	$.post("/suggestions", postParameters, function(responseJSON) {
		$("#list").find('option').remove().end();
		var suggestions = JSON.parse(responseJSON);
		for (i in suggestions) {
			$('#list').append(
				$('<option>', {id: "remove", value : suggestions[i]}).text(suggestions[i]));
		}
	})
})

$('#suggest2').change(function(event) {

	var postParameters = { rawText: $('#suggest2').val() };

	$.post("/suggestions", postParameters, function(responseJSON) {
		$("#list2").find('option').remove().end();
		var suggestions = JSON.parse(responseJSON);
		for (i in suggestions) {
			$('#list2').append(
				$('<option>', {id: "remove 2", value : suggestions[i]}).text(suggestions[i]));
		}
	})
})

$("#list").change(function(event) {
	$('#suggest').val($("#list option:selected").val());
})

$("#list2").change(function(event) {
	$('#suggest2').val($("#list2 option:selected").val());
})