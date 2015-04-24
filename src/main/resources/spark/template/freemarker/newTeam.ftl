<#assign coachContent>
    <div class="container">
    </div>
  <h1>Make a Team</h1>
  <form method="POST" action="/dashboard/new/team/results">
    Team name:<br>
    <input type="text" name="name">
    <br>
    Coach's name:<br>
    <input type="text" name="coach">
    <br>
    Primary Color:<br>
    <input type="color" name="color1">
    <br>
    Secondary Color:<br>
    <input type="color" name="color2">
    <br><br>
    <input type="submit" class="submit" value="Submit"> <br>
  </form>
  <link rel="stylesheet" href="/css/coach.css">
</#assign>
<#include "coach.ftl">
