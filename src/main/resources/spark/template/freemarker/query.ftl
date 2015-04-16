<#assign content>

	<div id="wrapper">
		<div id="query">
			<form method="POST" action="/path">
				Start   <input id="suggest" type="text" name="source" rows="1" cols="85"></input> 
				<div class="suggestions">
					<select id="list"></select>
				</div>
				Finish <input id="suggest2" type="text" name="target" rows="1" cols="85"></input> 
				<div class="suggestions">
					<select id="list2"></select>
				</div>
				<br>
			  <input type="submit">
			</form>
		</div>
	</div>
</#assign>
<#include "main.ftl">
