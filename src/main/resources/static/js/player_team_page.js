$(function () {

	$("#season").change(function () {

		console.log("Current Season changed.");

		postParameters = {
			year: $("#season").val(), 
			playerID: $("#id").val()
		}
		console.log("Year: "+ postParameters.year);

		$.post("/player/get/year", postParameters, function (responseHTML) {

		})
	})
	
})

