# Crawler

This project consists in two modules, the webcrawler for crawling data from news websites, and noah for crawling data from Twitter.


## Build

Prerequisites: scala, sbt, [AsterixDB](http://asterixdb.apache.org), [TextGeoLocator](https://github.com/MoniMoledo/text-geo-locator)

### Prepare the AsterixDB cluster
Follow the official [documentation](https://ci.apache.org/projects/asterixdb/install.html) to setup a fully functional cluster.

### Run Webcrawler

Webcrawler is an application that integrates with [Webhose.io API](https://webhose.io/) to crawl data from news websites, geotag and ingest it on AsterixDB.

Parameters description:

* `-tk` or `--api-key` : [Webhose.io](https://webhose.io/auth/signup) API key
* `-kw` or `--keywords` : Keywords to search for in the news
* `-co` or `--country-code` : Thread country code
* `-ds` or `--days-ago` : Crawl since the given number of days ago, default 1
* `-tglurl` or `--textgeolocatorurl` : Url of the TextGeoLocator API, default: "http://localhost:9000/location"
* `-u` or `--url` : Url of the feed adapter
* `-p` or `--port` : Port of the feed socket
* `-w` or `--wait` : Waiting milliseconds per record, default 500
* `-b` or `--batch` : Batchsize per waiting periods, default 50
* `-c` or `--count` : Maximum number to feed, default unlimited
* `-fo` or `--file-only` : Only store in a file, do not geotag nor ingest, default false


You can run the following example command in a separate command line window:
```
>cd crawler
>sbt "project webcrawler" "run-main Crawler \
>-tk "Your Webhose.io token" \
>-kw "dengue", "zika", "zikavirus", "microcefalia", "febreamarela", "chikungunya" \
>-co "BR" \
>-ds 1 \
>-tglurl "http://localhost:9000/location" \
>-u 127.0.0.1 \
>-p 10010 \
>-w 0 \
>-b 50"
```

### Run Noah

Noah is a module that continuously crawls new tweets that mentions a specified keyword, geotag and ingests it on AsterixDB.

Parameters description:

* `-ck` or `--consumer-key` : ConsumerKey for [Twitter API](https://apps.twitter.com/) OAuth
* `-cs` or `--consumer-secret` : Consumer Secret for [Twitter API](https://apps.twitter.com/) OAuth
* `-tk` or `--token` : Token for [Twitter API](https://apps.twitter.com/) OAuth
* `-ts` or `--token-secret` : Token secret for [Twitter API](https://apps.twitter.com/)  OAuth
* `-tr` or `--tracker` : Tracked terms
* `-u` or `--url` : Url of the feed adapter
* `-p` or `--port` : Port of the feed socket
* `-w` or `--wait` : Waiting milliseconds per record, default 500
* `-b` or `--batch` : Batchsize per waiting periods, default 50
* `-c` or `--count` : Maximum number to feed, default unlimited
* `-fo` or `--file-only` : Only store in a file, do not geotag nor ingest, default false

You can run the following example command in a separate command line window:
```
> cd crawler
> sbt "project noah" "run-main edu.uci.ics.cloudberry.noah.feed.TwitterFeedStreamDriver \
> -ck Your consumer key \
> -cs Your consumer secret \
> -tk Your token \
> -ts Your token secret \
> -tr dengue zikavirus microcefalia febreamarela chikungunya\
> -u 127.0.0.1 \
> -p 10001 \
> -w 0 \
> -b 50"

```

### Acknowledgments

* The Noah and Gnosis modules were adapted from [TwitterMap](https://github.com/ISG-ICS/cloudberry/tree/master/examples/twittermap).
* Currently, geotagging works only for Brazil.
* Users and developers are welcome to contact me through moniquemoledo@id.uff.br