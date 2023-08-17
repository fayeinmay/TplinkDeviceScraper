# TplinkDeviceScraper AX6000

## What this is

TpLink does not provide any API to interact with their webinterface. 

But we wanted to include a list of all connected devices in our home assistant instance. 

So that's why I wrote this little tool which grabs all devices every 2 minutes from tplink router. 

It is tested with the AX6000 router running in english and with local login instead of TP-Link ID login. 

Feel free to reach out if you have any questions. 

## How to use

Change yourpassword in TplinkDeviceScraperService 

Run mvn clean package -B 

Start the jar 
