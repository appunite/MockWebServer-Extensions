# MockWebServer Extensions

The MockWebServer Extensions, provides a convenient solution for testing HTTP clients by extending MockWebServer functionalities. With easy integration into your testing environment, it allows for seamless mocking of server responses, facilitating comprehensive testing of HTTP client behaviors.

## Download

Add it in your root build.gradle at the end of repositories or in settings.gradle:
```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```
Add the dependency:
```kotlin
dependencies {
        implementation 'com.github.appunite:MockWebServer-Extensions:0.1.0'
}
```

## Usage

### Add a Network Security Configuration file

#### Add `app/src/debug/res/xml/network_security_config.xml` file.

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">localhost</domain>
    </domain-config>
</network-security-config>
```

#### Add `network_security_config.xml` to the debug AndroidManifest.

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application android:networkSecurityConfig="@xml/network_security_config">
    </application>
</manifest>
```

### Add TestInterceptor to your HTTP Client.

```kotlin
// Ktor
val client = HttpClient(OkHttp) {
    expectSuccess = true
    engine {
        addInterceptor(TestInterceptor)
    }
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
            }
        )
    }
}

// Retrofit
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(TestInterceptor)
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()
```

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
