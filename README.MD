# Karaoke Party Song/Data Snatcher

___Gotta catch em' all!___
Even though they're "not available in your country", the android app (and it's "interface") still work.
I intercepted the data and found that the content is still available.

# Instructions

1. Download and compile (dependent on [JSoup](https://jsoup.org/download) and [org.json](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.codeartisans%22%20AND%20a%3A%22org.json%22) libraries)
2. Run (java -jar KaraokePartySnatcher.jar)
3. Choose Folder to save data to
4. Let it run. Estimated download size ~12.6 GB

# Issues

- Karaoke Data files (abin) are empty for premium (star) songs. Somehow need to get a premium account to do this, but login db is down...
- TODO: parse karaoke files into friendly format
