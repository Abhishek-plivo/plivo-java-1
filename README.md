# Java Helper SDK

**Note**: `Bleeding edge: Use with caution`

## Installation

Add as a dependency using your dependency management tool:

Maven:
```xml
<dependency>
    <groupId>com.plivo</groupId>
    <artifactId>plivo-java</artifactId>
    <version>4.0.0</version>
</dependency>
```

Gradle:
```groovy
compile group: 'com.plivo', name: 'plivo-java', version: '4.0.0'
```

### Authentication:

To use the Java SDK with a single client, call `Plivo.init()`, and all API calls will use this global client by default.
We recommend that you store your credentials in the `PLIVO_AUTH_ID` and the `PLIVO_AUTH_TOKEN` environment variables, so as to avoid the possibility of accidentally committing them to source control. If you do this, you can initialise the client with no arguments and it will automatically fetch them from the environment variables:

```java
class Example {
  public static void main(String [] args) {
    Plivo.init();
  }
}
```

Alternatively, you can provide these to `Plivo.init()`'s constructor yourself:

```java
class Example {
  public static void main(String [] args) {
    Plivo.init("<insert your authentication ID here>", "<insert your authentication token here>");
  }
}
```

To use multiple clients, you can create a `PlivoClient` instance yourself and set it on a request:

```java
class Example {
  public static void main(String [] args) {
    PlivoClient client = new PlivoClient("<insert your authentication ID here>", "<insert your authentication token here>");
    Message.creator("14153336666", Collections.singletonList("14156667777"), "Test Message")
                    .client(client)
                    .create();
  }
}
```

If you are making several requests to Plivo's API, please re-use the same client instance for maximum efficiency.

## The Basics

To send a message:

```java
class Example {
  public static void main(String [] args) {
    Plivo.init();
    Message.creator("14153336666", Collections.singletonList("14156667777"), "Test Message")
                    .create();
  }
}
```

To make a call

```java
class Example {
  public static void main(String [] args) {
    Plivo.init();
    Call.creator("1231231231", Collections.singletonList("3213213213"), "http://s3.amazonaws.com/static.plivo.com/answer.xml")
                    .answerMethod("GET")
                    .create();
  }
}
```

To list all objects of any resource, simply use the request object itself as an iterable:

```java
class Example {
  public static void main(String [] args) {
    Plivo.init();
    for (Message message : Message.lister()) {
      System.out.println(message.getMessageUuid());
    }
  }
}
```

Please note that this makes several requests to the Plivo API.

To generate PlivoXML:

```java
class Example {
  public static void main(String [] args) {
    System.out.println(new Response()
                             .children(
                               new Speak("Hello World, from Plivo!")
                             ).toXmlString());
  }
}
```
