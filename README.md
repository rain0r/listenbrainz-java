The ListenBrainz API bindings for java are wrapper for
the [ListenBrainz API](https://listenbrainz.readthedocs.io/en/latest/) and the ListenBrainz
submission service (_scrobbling_).

It provides classes and methods to invoke ListenBrainz API methods from within Java applications.

## Usage

```java
LbService lbService = new LbService();
lbService.getListens("my-username").ifPresent(d -> LOG.info(d.getPayload()));
```

All methods that submit data to the ListenBrainz server need a valid user token:

```java
LbService lbService = new LbService();
lbService.submitPlayingNow("artist", "title");
```


## Maven

```xml

<dependency>
  <groupId>org.hihn</groupId>
  <artifactId>listenbrainz-java</artifactId>
  <version>1.1.1</version>
</dependency>
```
