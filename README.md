# GallyShuttle

This is an Android project to get schedules of Gallaudet University shuttles and to notify users of
arrival/departure times. Gallaudet students, feel free to fork and contribute to this project! View
the change log for this project [here] (CHANGELOG.md).

[![Google Play link](https://developer.android.com/images/brand/en_generic_rgb_wo_60.png)]
(https://play.google.com/store/apps/details?id=com.ephemeraldreams.gallyshuttle&referrer=utm_source%3Dgithub%26utm_medium%3Dcpc)

## Building

Fork, clone or download this project and import this project using [Android Studio] (http://developer.android.com/tools/studio/index.html).
Rebuild your project (via "Build → Rebuild Project") and you're ready to get developing. There are
still configurations to be modified for backend, mobile, and wear modules.

<!--
[![Build Status](https://travis-ci.org/tjyu1040/GallyShuttle.svg?branch=master)](https://travis-ci.org/tjyu1040/GallyShuttle)
-->

### Building backend module

The backend is built using Google App Engine. Follow the instructions provided in the following links:

1. [Debugging the backend locally] (https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints#11-debugging-the-backend-locally)
2. [Obtaining Google Cloud Messaging API key] (https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints#21-obtaining-google-cloud-messaging-api-key)
3. [Deploying the backend live to App Engine] (https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints#25-deploying-the-backend-live-to-app-engine)

### Building mobile and wear modules

The mobile app uses Fabric for crash reporting, Google Analytics for analytics, and Google Cloud
Messaging for notifications, which will need to be configured.

#### Fabric Setup

1. Sign up at [fabric.io](https://get.fabric.io/).
2. Install Fabric IDE plugin.
3. Rename `mobile/fabric.properties.sample` to `mobile/fabric.properties`.
4. Fill in your API key and secret in `mobile/fabric.properties`.

#### Google Analytics, App Invites, & Google Cloud Messaging Setup

1. Go to this link: [Enable Google services for your app](https://developers.google.com/mobile/add?platform=android)
2. Create a new project for your own app.
3. Add "Analytics", "App Invites", & "Cloud Messaging" services and generate your configuration file.
4. Download `google-services.json` and move it to `mobile/` directory.

##### Integrating Google Analytics & Google Cloud Messaging

This section is still under construction, as Google Analytics and Google Cloud Messaging has not been 
fully integrated and tested yet.

## Contributing to This Project

You can contribute to this project by reporting bugs or suggesting new features [here] (https://github.com/tjyu1040/GallyShuttle/issues).

Alternatively, you can fork this project, fix the bug/add new feature, and submit a pull request for
me to review and approve it for release.

## Acknowledgements

This project currently makes use of the following open-source libraries:

- [Dagger 2] (http://google.github.io/dagger/) - Dependency injection framework library for Android and Java
- [Butterknife] (http://jakewharton.github.io/butterknife/) - View injection library for Android
- [OkHttp] (http://square.github.io/okhttp/) - Networking library for Android and Java
- [Retrofit] (http://square.github.io/retrofit/) - REST client for Android and Java
- [Otto] (http://square.github.io/otto/) - Event bus library for Android
- [DiskLruCache](https://github.com/JakeWharton/DiskLruCache) - Android-compatible disk-based LRU cache
- [Gson] (https://code.google.com/p/google-gson/) - Java library to convert Java Objects into JSON representations
- [joda-time-android] (https://github.com/dlew/joda-time-android) - Java date and time Android library
- [Timber] (https://github.com/JakeWharton/timber) - Utility logger for Android
- [LeakCanary](https://github.com/square/leakcanary) - Memory leak detection library for Android and Java

## License

    Copyright 2014 Timothy Yu

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.