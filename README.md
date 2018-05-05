# Nearby Chat

[![Build Status](https://travis-ci.com/EzraLopez/nearbychat.svg?branch=master)](https://travis-ci.com/EzraLopez/nearbychat)

Experimental Android app demonstrating the use of [Nearby Messages API](https://developers.google.com/nearby/messages/overview) for communicating between devices within close proximity of each other (within about 100 feet). The devices don't have to be on the same network, but they do have to be connected to the Internet.

<a href='https://lh3.googleusercontent.com/STKvfQlGMDOAj56sNvArmlEv1FjTGMwaeMjuC80NCUm1IBTRNQyqmpCAK_dtCrQgJEMwwqxqL3Tj_S2cfXfx4xGTba5vrL1tIfRstPOGocdwK95s1YGB2KUwp4yOwZmRIFSVKlCJRkmYCSpDU6TWWVnbAUfuKXgofaK4Z9oht1rbnPj44MHtDCgjXH2ao5PGQuN2r-GaAvPEHAd53g47T20s0xSafalllOSnSJb2sJ68sFXH-RvF-RxRJGRvWJnkZF1U2ftz6qyX0ZyxFK0Du6M67qf9XA1vSO4M3dDoae1a0MrRrQNKHLyPchuywyAqfE4CCoeUiFPPkaa8l1dTaLpQ5eIgG-VMgm4yVOrDhHp5M19vzxyZ3SbTnzg5sRR6rAc3tSaFwW7HbrODdKis6wi1j4cEGBTgPqGQ-3k98LlcFTpTVRebyMdocVmkegG-LzRfVnQI8dTkA56uRezUK_cjplmh2RYSeTulDsJzkFCFlQK-8JKMBN2hebb1Gj1pieyMu8Pv0Uie_Cn1vImkUTCo3YB4KQQ7vjfHhx8C3hUJdqNPy6D2fA7BA4pYM20-b98w35ZYCPVX2MXfzzlZq9NkfBElqxNCpBcPdGG5LScUMRLKqabNfoXB6rr5QNEPeUALajWoxc2ttp1d1XXt9a2tV5SYF8cyhA=w1139-h703-no'><img width='100%' height='100%' alt='Screenshot 1' src='https://lh3.googleusercontent.com/STKvfQlGMDOAj56sNvArmlEv1FjTGMwaeMjuC80NCUm1IBTRNQyqmpCAK_dtCrQgJEMwwqxqL3Tj_S2cfXfx4xGTba5vrL1tIfRstPOGocdwK95s1YGB2KUwp4yOwZmRIFSVKlCJRkmYCSpDU6TWWVnbAUfuKXgofaK4Z9oht1rbnPj44MHtDCgjXH2ao5PGQuN2r-GaAvPEHAd53g47T20s0xSafalllOSnSJb2sJ68sFXH-RvF-RxRJGRvWJnkZF1U2ftz6qyX0ZyxFK0Du6M67qf9XA1vSO4M3dDoae1a0MrRrQNKHLyPchuywyAqfE4CCoeUiFPPkaa8l1dTaLpQ5eIgG-VMgm4yVOrDhHp5M19vzxyZ3SbTnzg5sRR6rAc3tSaFwW7HbrODdKis6wi1j4cEGBTgPqGQ-3k98LlcFTpTVRebyMdocVmkegG-LzRfVnQI8dTkA56uRezUK_cjplmh2RYSeTulDsJzkFCFlQK-8JKMBN2hebb1Gj1pieyMu8Pv0Uie_Cn1vImkUTCo3YB4KQQ7vjfHhx8C3hUJdqNPy6D2fA7BA4pYM20-b98w35ZYCPVX2MXfzzlZq9NkfBElqxNCpBcPdGG5LScUMRLKqabNfoXB6rr5QNEPeUALajWoxc2ttp1d1XXt9a2tV5SYF8cyhA=w1139-h703-no'/></a>

<p align="center"><a href='https://play.google.com/store/apps/details?id=com.esdraslopez.android.nearbychat&pcampaignid=github'><img width='250px' alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a></p>

* No need to sign up for anything
* Usernames are optional
* Only text-based chat is currently supported - URLs, emails, phone numbers, and addresses are clickable and will be opened with any installed app that supports them

Nearby uses a combination of Bluetooth, Bluetooth Low Energy, Wi-Fi and near-ultrasonic audio to communicate between devices.

Push notifications are not currently supported due to API limitations. You must have the app in the foreground to guarantee you will receive messages from devices around you. 

The app is not meant to be used for extended periods of time. Nearby's use of radios and sensors will consume battery at a higher rate than usual. (Battery consumption will decrease with earshot mode - not yet implemented, but you could help, remember, it is fully open source 😀 )

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
Beta. Issues with [label "planned"](https://github.com/EzraLopez/nearbychat/labels/planned) are currently in development or will be soon.
