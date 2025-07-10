# MockWebServer Extensions - Development Guidelines

This document provides essential information for developers working on the MockWebServer Extensions project.

## Build/Configuration Instructions

### Prerequisites
- JDK 11 or higher
- Android SDK with API level 34 (compileSdk)
- Gradle 8.x

### Project Setup
1. Clone the repository
2. Open the project in Android Studio or IntelliJ IDEA
3. Sync the project with Gradle files

### Building the Project
- To build the entire project: `./gradlew build`
- To build a specific module: `./gradlew :moduleName:build`
  - Example: `./gradlew :mockwebserver-extensions:build`

### Publishing
The library is configured for Maven publishing. To publish locally:
```
./gradlew publishToMavenLocal
```

## Testing Information

### Running Tests
- Run all tests: `./gradlew test`
- Run tests for a specific module: `./gradlew :moduleName:test`
  - Example: `./gradlew :mockwebserver-extensions:test`

### Writing Tests
The project uses JUnit 4 for testing, along with Strikt for assertions and Mockk for mocking.

#### Basic Test Structure
```kotlin
class YourTest {
    @get:Rule
    val mockWebServerRule: MockWebServerRule = MockWebServerRule()

    @Test
    fun testSomething() {
        // Register a mock response
        mockWebServerRule.register { request ->
            // You can inspect the request here
            MockResponse().setBody("Your response")
        }

        // Execute your code that makes HTTP requests
        val result = yourCodeThatMakesHttpRequests()

        // Assert the results
        expectThat(result).isEqualTo(expectedValue)
    }
}
```

#### Key Testing Components
1. **MockWebServerRule**: JUnit rule that sets up and manages a MockWebServer instance
   - Automatically installs and uninstalls the TestInterceptor
   - Provides methods to register mock responses
   - Reports errors that occurred during tests

2. **TestInterceptor**: Intercepts HTTP requests and redirects them to the MockWebServer
   - Automatically installed by MockWebServerRule
   - Can be customized with your own interceptor

3. **Assertions**: Custom assertions for HTTP responses
   - `code`: Assert response status code
   - `bodyString`: Assert response body as string
   - `headers`: Assert response headers
   - And many more in the mockwebserver-assertions module

### Example Test
Here's a simple test that demonstrates how to use the MockWebServerRule:

```kotlin
@Test
fun testSimpleRequest() {
    // Register a mock response for any request
    mockWebServerRule.register { _ ->
        MockResponse().setBody("Hello, MockWebServer!")
    }

    // Execute a request
    val client = OkHttpClient.Builder()
        .addInterceptor(TestInterceptor)
        .build()
    val request = okhttp3.Request.Builder()
        .url("https://example.com/test")
        .build()
    val response = client.newCall(request).execute()

    // Verify the response
    expectThat(response).and {
        code.isEqualTo(200)
        bodyString.isEqualTo("Hello, MockWebServer!")
    }
}
```

## Additional Development Information

### Project Structure
The project is organized into several modules:
- **app**: Sample Android application
- **mockwebserver-extensions**: Core library with MockWebServerRule and related components
- **mockwebserver-interceptor**: Contains the TestInterceptor for redirecting HTTP requests
- **mockwebserver-assertions**: Custom assertions for HTTP responses
- **mockwebserver-allow-mocking**: Android configuration for allowing HTTP traffic to MockWebServer
- **mockwebserver-request**: Request model and utilities

### Dependency Management
The project uses Gradle version catalogs (libs.versions.toml) for dependency management.

### Code Style
- The project follows Kotlin coding conventions
- Use extension functions for adding functionality to existing classes
- Write comprehensive tests for all new features

### Common Patterns
1. **Response Generation**: Use the `ResponseGenerator` functional interface to create mock responses
2. **Request Assertions**: Use the assertion extensions from mockwebserver-assertions to verify request properties
3. **Network Simulation**: Use `simulateNetworkDown()` and `simulateNetworkUp()` methods to test network failure scenarios