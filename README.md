This is ja java wrapper for the the [ListenBrainz API](https://listenbrainz.readthedocs.io/en/latest/).

It supports retrieving and submitting data (_scrobbling_).

## Usage

```java
LbService lbService = new LbService();
lbService.getListens("my-username").ifPresent(d -> LOG.info(d.getPayload()));
```

All methods that submit data to the ListenBrainz server need a valid user token:

```java
LbService lbService = new LbService("auth-token");
lbService.submitPlayingNow("artist", "title");
```


## Maven

```xml

<dependency>
  <groupId>org.hihn</groupId>
  <artifactId>listenbrainz-java</artifactId>
  <version>1.1.3</version>
</dependency>
```
