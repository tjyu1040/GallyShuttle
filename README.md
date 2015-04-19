GallyShuttle
============
This is an Android project to get schedules of Gallaudet University shuttles and to notify users of
arrival/departure times. Gallaudet students, feel free to fork and contribute to this project! View
the change log for this project [here] (CHANGELOG.md).

[![Google Play link](https://developer.android.com/images/brand/en_generic_rgb_wo_60.png)] (https://play.google.com/store/apps/details?id=com.ephemeraldreams.gallyshuttle)

Importing This Project
----------------------
Clone this project or download this project in a .zip file and unzip it. Import this project using
[Android Studio] (http://developer.android.com/tools/studio/index.html).

Contributing to This Project
----------------------------
You can contribute to this project by reporting bugs or suggesting new features [here] (https://github.com/tjyu1040/GallyShuttle/issues).

Alternatively, you can clone this project, fix the bug/add new feature, and submit a pull request for
me to review and approve it for release.

You may notice during development that there are some snippets of code commented out. These snippets
are related to [Crashlytics] (https://try.crashlytics.com/), a crash reporting service that I use to
monitor any crashes for the application at any given time. Since Crashlytics gives out a secret API
key for each individual application, it's better that I withhold the API keys to prevent any tampering
with the crash reporting service. These commented out snippets should not affect you when building this project.

Acknowledgements
----------------
This project currently makes use of the following open-source libraries:
- [Gson] (https://code.google.com/p/google-gson/) - Java library to convert Java Objects into JSON representations
- [Dagger 2] (http://google.github.io/dagger/) - Dependency injection framework library for Android and Java
- [Otto] (http://square.github.io/otto/) - Event bus library for Android
- [OkHttp] (http://square.github.io/okhttp/) - Networking library for Android and Java
- [Retrofit] (http://square.github.io/retrofit/) - REST client for Android and Java
- [RxJava] (https://github.com/ReactiveX/RxJava) - Reactive Extensions Java implementation.
- [RxAndroid] (https://github.com/ReactiveX/RxAndroid) - Android-specific bindings for RxJava
- [Butterknife] (http://jakewharton.github.io/butterknife/) - View injection library for Android
- [Timber] (https://github.com/JakeWharton/timber) - Utility logger for Android
- [joda-time-android] (https://github.com/dlew/joda-time-android) - Java date and time Android library

License
-------

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