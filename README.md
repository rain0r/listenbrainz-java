# listenbrainz-java

This is a java wrapper for the the [ListenBrainz API](https://listenbrainz.readthedocs.io/en/latest/).

It supports retrieving and submitting data (_scrobbling_).

## Usage

To get the listens of a user and log them:

```java
LbService lbService = new LbService();
lbService.getListens("my-username").ifPresent(d -> LOG.info(d.getListens()));
```

All methods that submit data to the ListenBrainz server need a valid user token.
This can be passed to the constructor:


```java
LbService lbService = new LbService("auth-token");
lbService.submitPlayingNow("artist", "title");
```


## Maven

```xml

<dependency>
  <groupId>org.hihn</groupId>
  <artifactId>listenbrainz-java</artifactId>
  <version>1.1.6</version>
</dependency>
```
