# MockWebServer Extensions

The MockWebServer Extensions, provides a convenient solution for testing HTTP clients by extending MockWebServer functionalities. With easy integration into your testing environment, it allows for seamless mocking of server responses, facilitating comprehensive testing of HTTP client behaviors.

## Usage

### Create a variable with MockWebServerRule in the test class.

```kotlin
@get:Rule
var mockWebServer: MockWebServerRule = MockWebServerRule()
```

### To mock a request, use `register` method.

```kotlin
mockWebServer.register {
    expectThat(it).url.path.isEqualTo("/fact")
    jsonResponse("""{"fact": "Example fact about your cat."}""")
}
```

## Requirements

### Download

Add it in your root build.gradle at the end of repositories or in settings.gradle:
```kotlin
repositories {
    maven { url 'https://jitpack.io' }
}
```

Add the dependency:
```kotlin
dependencies {
    androidTestImplementation 'com.github.appunite.MockWebServer-Extensions:mockwebserver-extensions:0.3.0'
    androidTestImplementation 'com.github.appunite.MockWebServer-Extensions:mockwebserver-request:0.3.0'
    implementation 'com.github.appunite.MockWebServer-Extensions:mockwebserver-interceptor:0.3.0'
}
```

### Add a Network Security Configuration

```kotlin
dependencies {
    debugImplementation 'com.github.appunite.MockWebServer-Extensions:mockwebserver-allow-mocking:0.3.0'
}
```

### Add TestInterceptor to your HTTP Client.

```kotlin
// Ktor
val client = HttpClient(OkHttp) {
    engine {
        addInterceptor(TestInterceptor)
    }
}

// Retrofit
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(TestInterceptor)
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(okHttpClient)
    .build()
```

### Optional Assertions using okhttp

```kotlin
dependencies {
    androidTestImplementation 'com.github.appunite.MockWebServer-Extensions:mockwebserver-assertions:0.3.0'
}
```


You can check full example in the [app module](https://github.com/appunite/MockWebServer/tree/main/app/src).
And more examples in the [Loudius - Android playground](https://github.com/appunite/Loudius) in the [app-shared-tests module](https://github.com/appunite/Loudius/tree/develop/app-shared-tests/src/main/java/com/appunite/loudius).

## License
```
Copyright 2024 Appunite

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
