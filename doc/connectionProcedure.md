# How to connect to ALFEN ACE0272306

> Note : This procedure is for Debian-based OS such as Ubuntu and Mint.

You will need to go to "Parameters" -> "Network" -> Click on the gear next to "Wired".

In the new window "Wired", go to the tab "IPv4" -> Check, in the "IPv4 method", the "manual" option.

Now, below, in adresses, write these infos to the according tabs.

```
Address          Mask            Bridge        
"IPv4 address"   255.255.255.0    
```

> Note : The machine should have been defined an IP address to listen to. In our case, it is 192.168.0.5.

Then, apply and the connection to the machine should be working.

> Note : If you change the network, don't forget to reset the network parameter on your computer.

