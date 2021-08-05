# Simpler NyaaSi-API
Simpler Java API implementation for https://nyaa.si/ and https://sukebei.nyaa.si/

# Installation
Go ahead and build it, or just include it through Jitpack.io.

# What can it do?
It can only search the page, and fetch torrents from ids. All the login and user functions have been removed for the sake of simplicity.

# Usage
The entry point of this API is the `NyaaSiApi(boolean isSukebei)` constructor.

Alternate constructor is `NyaaSiApi(boolean isSukebei, UserHttpClient client)`, which allows you to set a custom UserHttpClient class, which is a tiny convenience class around HttpClient5, its purpose in life is to ease the recycling of a single HttpClient application wide, thus saving resources. Aditionally, the UserHttpClient class can be built using a CloseableHttpResponse to save time.

It should be Android compatible, too.

For more example usages you may have a look at the `de.kaysubs.tracker.nyaasi.examples` package.
