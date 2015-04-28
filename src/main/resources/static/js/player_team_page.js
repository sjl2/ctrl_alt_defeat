$("#current-season").on('change', function () {

	postParameters = {
		year: $("#current-season").val(), 
		
	}
	$.post("/player/get/year", postParameters, function (responseHTML) {

	})
})