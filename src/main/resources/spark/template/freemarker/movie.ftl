<#assign content>

	<h1>Bacon: ${movieDB}</h1>
	<div id="wrapper">
		<div id="query">
			<form method="POST" action="/path">
				<input id="suggest" type="text" name="source" rows="1" cols="85"></input>
				<div class="suggestions">
					<select id="list"></select>
				</div>
				<input id="suggest2" type="text" name="target" rows="1" cols="85"></input>
				<div class="suggestions">
					<select id="list2"></select>
				</div>
				<br>
			  <input type="submit">
			</form>
		</div>
		<div id="info">
			<h2>Movie: ${movie.getName()}</h2>
			<ul>
				<#list movie.getActors() as actor>
					<li><a href=${"/actor" + actor.getID()}>${actor.getName()}</a></li>
				</#list>
			</ul>
		</div>		
	</div>
</#assign>
<#include "main.ftl">