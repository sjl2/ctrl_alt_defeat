<#assign content>

	<h1>Bacon: ${movieDB}</h1>
	<div id="wrapper">
		<div id="query">
			<form method="POST" action="/path">
				 Start <input id="suggest" type="text" name="source" rows="1" cols="85"></input> 
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
		<div id="path">
			<h2>Path: </h2>
			<#list path as edge>
				<a href=${"/actor" + edge.getSource().getID()}>${edge.getSource().getName()}</a>
				 <#if edge.getDistance() != -1.0>
				 -> 
				 <#else>
				 -/-
				 </#if>
				 <a href=${"/actor" + edge.getTarget().getID()}>${edge.getTarget().getName()}</a>
				 <#if edge.getDistance() != -1.0>
						:
					 <a href=${"/movie" + edge.getValue().getID()}>${edge.getValue().getName()}</a>
				 </#if>
				 <br>
			</#list>
			<#if path?size == 0>
				<p>NO PATH</p>
			</#if>
		</div>		
	</div>
</#assign>
<#include "main.ftl">
