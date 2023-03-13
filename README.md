# <img src="https://github.com/lwdovico/GPSrecorder/raw/main/app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="icon" height="35"> GPS recorder for Android


A small android app to record gps data and send them to a database. 

## Install
It is meant to work with a [Firestore Database](https://firebase.google.com/), considering it is meant primarlily for personal use, in order to build the app and use it on a device it is necessary to set up the database on [firebase.google.com](https://firebase.google.com/), activating writing operations and downloading the google-services.json file from the firebase console and putting it in the "app" folder so that a connection to your own db can be established.

## Usage
First open the app:

<p align="center">
<img src="https://github.com/lwdovico/GPSrecorder/raw/main/examples/icon_home.png" alt="IconHome" height="500">
</p>

For the app to work it is necessary to have the GPS active on the device and to grant the necessary permissions on start up. It is possible to send data in two ways:
1.  Batch: if multiple locations are being recorded by the service in the main app activity as seen in the image. It is better to send the records in batches so that there are fewer writing operations to perform on the database (google's db is free up to 10.000 operations). In this case the field stored and sent are datetime, device, ip, latitude and longitude. As of now locations are stored every ten seconds (it can be easily changed but it was set up to be fixed to keep the density of trajectories identical). Data is stored locally until it is sent to the db with the upload button, then it will be deleted from the device.
<p align="center">
<img src="https://github.com/lwdovico/GPSrecorder/raw/main/examples/app_main_active.png" alt="LocationListenerActive" height="500">
</p>

2.  Labeled: It is also possible to send labeled data with a short description of the activity. In addition to the previous fields in this case is also possible to specify manually the usage of a VPN and a description of the activity (it may be necessary if inference of the activity by the location only is not possible), an example can be seen in the image.
<p align="center">
<img src="https://github.com/lwdovico/GPSrecorder/raw/main/examples/app_labeled.png" alt="SendingLabeledData" height="500">
</p>

Finally the data can be retrieved through either the API or it can be queried through the website, it can be exported to a json and it can be easily made into tabular data. As an example this is one timestamp stored in the db:
<p align="center">
<img src="https://github.com/lwdovico/GPSrecorder/raw/main/examples/data_stored.png" alt="dbData" height="500">
</p>


## Disclaimer
This app was created as a research tool, the intent was to collect and retrieve easily gps locations from multiple devices in order to build a dataset to work with. I am not responsible for any misuse or violations as the app itself wasn't developed with a general user in mind but it was designed only for my personal use and that of my collaborators.