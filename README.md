# Crawler

This project consists in two modules, the webcrawler for crawling data from news websites, and noah for crawling data from Twitter.


## Build

Prerequisites: scala, sbt, [AsterixDB](http://asterixdb.apache.org), [TextGeoLocator](https://github.com/MoniMoledo/text-geo-locator)

### Prepare the AsterixDB cluster
Follow the official [documentation](https://ci.apache.org/projects/asterixdb/install.html) to setup a fully functional cluster.

### Run Webcrawler

Webcrawler is an application that integrates with [Webhose.io API](https://webhose.io/) to crawl data from news websites, geotag and ingest it on AsterixDB, shows the Brazilian map with the mentions of Zika Virus, Dengue, Yellow Fever and Chikungunya diseases on twitter and news websites.

Parameters description:

* `-tk` or `--api-key` : API key to use Webhose.io
* `-kw` or `--keywords` : Keywords to search for in the news
* `-co` or `--country-code` : Thread country code
* `-ds` or `--days-ago` : Crawl since the given number of days ago
* `-tglurl` or `--textgeolocatorurl` : Url of the TextGeoLocator API, default: "http://localhost:9000/location"
* `-u` or `--url` : Url of the feed adapter
* `-p` or `--port` : Port of the feed socket
* `-w` or `--wait` : Waiting milliseconds per record, default 500
* `-b` or `--batch` : Batchsize per waiting periods, default 50
* `-c` or `--count` : Maximum number to feed, default unlimited
* `-fo`or `--file-only` : Only store in a file, do not geotag nor ingest


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