<#assign content>

	<h1> Path of Bacon </h1>
	<div id="wrapper">
		<div id="query">
			<form method="POST" action="/path">
				<input type="text" name="source" rows="1" cols="50"></input>
				<input type="text" name="target" rows="1" cols="50"></input>
				<br>
			  <input type="submit">
			</form>
		</div>
		<div id="info">
			<h2>${type}: ${</h2>
		</div>		
	</div>
</#assign>
<#include "main.ftl">