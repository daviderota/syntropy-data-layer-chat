# Syntropy Data Layer - Chat
Hello everyone,<br/>
I created this App to demonstrate how powerful and amazing is the "Syntropy Data Layer" service.<br/>
The App then creates a room in which all users can talk to each other.<br/>
It sends and receives data from the Syntropy Data Layer in the form of chat message strings.<br/> 
The room is created based on the configuration parameters used to connect App to the Syntropy Data Layer.<br/>
The app retrieves the Syntropy Data Layer connection parameters through the Firebase Remote Config service with the following form:<br/>
```json
{
  "accessToken": "xyz123456789....",
  "url": "nats://xyz",
  "stream": "streamName" 
}
```
If you want to use this repo tu build your own app version, you must create a firebase account [Firebase](https://console.firebase.google.com/) , create a project, activate the Remote Config service, and add the google-services.json to the source code ().<br/>

If you don't have an Access Token or one of the Syntropy Data Layer params, please follow this link: [Developer Portal](https://developer-portal.syntropynet.com/) <br/>

Apk generated from this repository is available [Here](https://github.com/daviderota/syntropy-data-layer-chat/blob/main/app-release.apk) with an access token generated for this demo and may no longer be functional in the future. In that case please write to me.
