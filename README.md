# opengpt-urlshort

### url service written using chat.openai. 98%+ code written ai

api:
 getAllUrls, get: /urls
 
 getOriginalUrl, get, /{shortUrl}
 
 getUrlStatistics, get, /statistics
 
 shortenUrl, post: /shorten?url={originalUrl}
 
 markUrl, post, /mark/{urlId}
 
 deleteUrl, /shorten?url={urlId}
