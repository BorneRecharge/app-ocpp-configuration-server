# How to develop and test web sockets on this project.

> Note : This procedure is for development purposes only, and could be changed at any time.
> You will need to launch the Java Application beforehand and write down the given password on launch.
> After that, launch the front with ```npm run start``` in the front folder.

The given password should look like this :

```
Using generated security password: x-x-x-x-x
This generated password is for development use only. 
Your security configuration must be updated before running your application in production.
```

After which, you will connect to http://localhost:8080.

On this link, you will find a login form, you will need to type 'user' or 'admin" on the username field
and put the given password on the password field.

This step lets you be authenticated on the application.

On the front web application (http://localhost:3000), you should be able to see the websocket connection
in the web browser console.
