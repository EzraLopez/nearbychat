# Nearby Chat

An Android app demonstrating the use of [Nearby Messages API](https://developers.google.com/nearby/messages/overview) for communicating between devices within close proximity of each other (withing about 100 feet). The devices don't have to be on the same network, but they do have to be connected to the Internet.

<a href='https://play.google.com/store/apps/details?id=com.esdraslopez.android.nearbychat&pcampaignid=github'><img width='25%' height='25%' alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a>

## Run Locally
* Get an API Key. You may reuse an existing Android Key, but to create a new one follow the steps bellow: 
  1. Go to the [Google Developers Console](https://console.developers.google.com/flows/enableapi?apiid=copresence&keyType=CLIENT_SIDE_ANDROID&reusekey=true).
  2. Create or select a project.
  3. Name the API Key. E.g. ```nearbychat-debug```
  4. Optionally apply Application and API restrictions. I highly ecourage you to do it. Use ```com.esdraslopez.android.nearbychat``` as the Application restriction and ```Nearby Messages API``` as the API restriction.
  5. Click on create.
* Place the key in your ```gradle.properties (Global Properties)``` file like this:
  ``` 
  NearbyChat_NearbyMessagesAPIKeyDebug="YOUR_API_KEY" 
  ```
 ## Project Status
Beta. [Issues with label "planned"](https://github.com/EzraLopez/nearbychat-private/labels/planned) are currently in development or will be soon.
