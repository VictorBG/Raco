# [WIP] Raco

[![Build Status](https://travis-ci.com/VictorBG/RacoFib.svg?token=xio7T67Yoyke3FpGmCAb&branch=master)](https://travis-ci.com/VictorBG/RacoFib)
[![codecov](https://codecov.io/gh/VictorBG/RacoFib/branch/master/graph/badge.svg?token=n4vV3gqOXM)](https://codecov.io/gh/VictorBG/RacoFib)

<div style="text-align:center; margin-bottom: 16px;"><img src=".github/assets/ic_launcher.png"
            alt="Download from Google Play"
            height="150">

**An unofficial Android client for [El Racó](http://raco.fib.upc.edu)**.

###### The app is not yet on the play store neither through direct download

[<img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png"
      alt="Download from Google Play"
      height="80">](https://victorblancogarcia.com/)
[<img src=".github/assets/direct-apk-download.png"
      alt="Direct apk download"
      height="80">](https://victorblancogarcia.com/)

The app is currently in development and the things might not work correctly until the final deploy. The test are not done yet.

### Features

- Publications
- Exams information
- Today classes schedule
- Week schedule
- Subjects information

### Specs/Open-source libraries

The app has been developed using the [official API](https://api.fib.upc.edu/)

- [**MVVM architecture**](https://developer.android.com/jetpack/docs/guide)
- [**Material components**](https://github.com/material-components/material-components-android) for the UI
- [**Android Architecture Components**](https://developer.android.com/topic/libraries/architecture/) for building the architecture
- [**RxJava2**](https://github.com/ReactiveX/RxJava) & [**RxAndroid**](https://github.com/ReactiveX/RxAndroid) for Retrofit & background threads
- [**Retrofit**](https://github.com/square/retrofit) for constructing the REST API
- [**Dagger**](https://github.com/google/dagger) for dependency injection
- [**ButterKnife**](https://github.com/JakeWharton/butterknife) for view binding
- [**Glide**](https://github.com/bumptech/glide) for loading images
- [**Lottie**](https://github.com/airbnb/lottie-android) for animations

### Contribution

I'm open for any contribution you would like to do either by [_creating a PR_](https://github.com/VictorBG/RacoFib/compare), [_submitting an issue_](https://github.com/VictorBG/RacoFib/issues/new) on Github or proposing ideas to make the app better.

You can see the [current features](https://github.com/VictorBG/RacoFib/projects/1) that are being developed at the time and contribute to one. Note that the _In Progress_ features are actively being developed and won't accept any PR until it is finished. 

### FAQ

**Why I have to login every day?**

The current system that is being used is the _implicit_ token grant, which doesn't return a refreshToken, that's why when the token has expired it cannot be automatically refreshed and needs the user to log in again. The [_authorization code_](https://github.com/VictorBG/RacoFib/projects/1#card-17730295) grant is scheduled but not actively in development.

**I have classes on weekend and the schedule view doesn't show them**

I was not aware of classes on weekend, that's why it is designed in a 5 column grid. Anyway, please tell me and I will modify the application in order to show the 7 days (you can do it yourself if you want, it's the _visibleDays_ variable of the class [_CalendarWeekScheduleView_](https://github.com/VictorBG/RacoFib/blob/01a40a108d8b57f0273cd89d29064438805bcc83/app/src/main/java/com/victorbg/racofib/view/widgets/calendar/CalendarWeekScheduleView.java#L96).

**I don't want the subjects to have colors**

You can edit them or remove them in the settings, but they always will have a color, at least the red color of the app.

**The information of the app is not the same than the information on the Racó**

The app caches the information in local databases in order to use it offline, you can refresh or delete this data in the settings.

**Is there any dark theme?**

In development.

### Developed By

- Víctor Blanco
- [victorblancogarcia.com](https://victorblancogarcia.com)


### License
    Copyright 2019 VictorBG

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
