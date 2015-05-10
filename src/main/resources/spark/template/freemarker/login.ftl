<#assign content>
  <script src="js/login.js"></script>

  <link rel="stylesheet" href="css/login.css">

  <div class="container">
    <div class="jumbotron">
      <h1>Welcome to<br><kbd>ctrl + alt + defeat</kbd></h1>
    </div>

    <form method="POST" action="/login/login" class="form-signin" id="loginForm">
      <label for="username" class="sr-only">Username</label>
      <input type="text" name="username" id="username" class="form-control" placeholder="Username" required autofocus>
      <label for="password" class="sr-only">Password</label>
      <input type="password" name="password" id="password" class="form-control" placeholder="Password" required>
      <button class="btn btn-lg btn-primary btn-block" type="submit" id="submit">Sign in</button>
    </form>
    <div id="message"></div>
  </div>

</#assign>
<#include "main.ftl">
