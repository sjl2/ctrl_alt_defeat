<#assign content>
  <div class="container">
    <h1>Thank you for using Ctrl-Alt-Defeat!</h1>
    <h3>To deliver our top quality service, we need a little information from you.</h3>
    <form method="POST" action="/dashboard/new">
      Team name:<br>
      <input type="text" name="name" required>
      <br>
      Coach's name:<br>
      <input type="text" name="coach" required>
      <br>
      Primary Color:<br>
      <input type="color" name="color1">
      <br>
      Secondary Color:<br>
      <input type="color" name="color2">
      <br><br>
      <input type="submit" class="submit" value="Submit"> <br>
    </form>
    <link rel="stylesheet" href="/css/dashboard_setup.css">
  </div>
</#assign>
<#include "main.ftl">
