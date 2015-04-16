<#assign content>
  <h1>Welcome to the Wonderously Useful New Team Screen! (written by the project czar himself)</h1>
	<form method="POST" action="/dashboard/newTeam/results">
		Team name:<br>
		<input type="text" name="name">
		<br>
		Primary Color:<br>
		<input type="text" name="color1">
		<br>
		Secondary Color:<br>
		<input type="text" name="color2">
	</form>
  <link rel="stylesheet" href="css/newTeam.css">
</#assign>
<#include "main.ftl">