var prevEntry1 = "";

console.log(document.getElementById("playerTeamSearch"));

function suggestions() {
	var spot1 = $("#playerTeamSearch")[0].value;
		$.post("/dashboard/autocomplete", {spot : spot1}, function(responseJSON){
			var res = JSON.parse(responseJSON);

			var sug1 = document.getElementById("suggestions1");
			var sug1Html = "";				
			if (prevEntry1 != spot1) {
				for (var i=0; i<5; i++) {
					if (res.res[i] != null)
					sug1Html = sug1Html + "<option>" + res.res[i] + "</option>";
				}
				prevEntry1 = spot1;
				sug1.innerHTML = sug1Html;
			}
		}); 

}