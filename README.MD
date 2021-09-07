# KaizoLib
Java API for the Kaizoyu! app.

## Features
- T-XDCC support
- IRC DCC handshake support (Compliant to RFC1459)
- Nibl custom made search API
- Kitsu search API
- Common model shared between modules
- Threadsafe
- Android compatible

## How to use

Use Jitpack.io to include the project as a dependency:

```gradle
    // Include Jitpack.io as a repository
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

    ...

    dependencies {
	        implementation 'com.github.ChameleonIVCR:KaizoLib:main-SNAPSHOT'
	}
```

KaizoLib heavily relies on Event Listeners for Threading operations, an example usage can be found at the `com.chame.kaizolib.Test` package.


## Lifecycle

As the library is targeted towards Android devices, the lifecycle consists on reusing connection objects to avoid socket rate-limiting by the OS. 
HttpRequests are done through a threadsafe `com.chame.kaizolib.common.network.UserHttpClient` connector. Apps are expected to create one at initialization, and reuse it for every connection. At app exit, it should be closed.

`com.chame.kaizolib.nibl.Nibl`, as all UserHttpClient implementing classes, will require a UserHttpClient object. If not present, it will create one. After a search is done, the result should be passed to `com.chame.kaizolib.irc.IrcClient`. At success, it will create a DCC object, ready to be passed to `com.chame.kaizolib.irc.DCCDownloader`.

## TODO

- Download speed limit
- Limit Nibl item process by pagination
- Introduce XDCC, and PDCC support
- *XDCC IPv6 support
- *XDCC resume support
- DCC protocol detection (Currently, only DCC and TXDCC are recognized)

## Dependencies

- HttpClient5
- HttpCore5
- JSoup
- Json-Simple
- Log4j-API
