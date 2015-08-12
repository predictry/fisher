# fisher
This application is part of Predictry.  It read logs from Tapirus, performs aggregations and provides the results
in REST APIs.  This application also stores items in its database including mapping between item's id and their name.

## Development

To run integration testing for this application in development system, you must setup Elasticsearch with default clustername (`elasticsearch`). 
Fisher expects Elasticsearch with clustername `fisher` available in production.  See 
`com.predictry.fisher.config.RootConfig` for more information.

This application can be build by using gradle.  While it provides project file for Eclipse STS, the use of it is not 
necessary.

After downloading source code and setup Elasticsearch, use the following command to build this application:

    gradle build

To run all test cases, use the following command:

    gradle test

To run only unit test cases, use the following command:

    gradle testDomainClass

To generate executable WAR file for this application, use the following command:

    gradle war

To deploy this application to deployment server (**Don't run this command if not necessary!**):

    gradle deployToServer

## API

To returns summary of statistics for a period, hits `/stat/overview` resource.  For example:

    http://119.81.208.244:8090/fisher/stat/overview?tenantId=targettenantid&startDate=2015010100&endDate=2015010102

returns value such as:

    {
      "pageView": {
          "overall": 265,
          "recommended": 0,
          "regular": 0
      },
      "uniqueVisitor": {
          "overall": 247,
          "recommended": 0,
          "regular": 0
      },
      "salesAmount": {
          "overall": 0,
          "recommended": 0,
          "regular": 0
      },
      "orders": {
          "overall": 0,
          "recommended": 0,
          "regular": 0
      },
      "itemPurchased": {
          "overall": 0,
          "recommended": 0,
          "regular": 0
      },
      "conversionRate": {
          "overall": 0,
          "recommended": 0,
          "regular": 0
      },
      "salesPerCart": {
          "overall": 0,
          "recommended": 0,
          "regular": 0
      },
      "itemPerCart": {
          "overall": 0,
          "recommended": 0,
          "regular": 0
      }
    }

To return histogram (grouped by specific interval), use resource `/stat`.  For example:

    http://119.81.208.244:8090/fisher/stat?tenantId=FAMILYNARA2014&startDate=2015010100&endDate=2015010323&metric=VIEWS&interval=day
    
returns value such as:

    
    [
        {
            "date": "2015-01-01T00:00:00",
            "value": 53
        },
        {
            "date": "2015-01-02T00:00:00",
            "value": 68
        },
        {
            "date": "2015-01-03T00:00:00",
            "value": 36
        }
    ]

By default, this API will return the overall value.  To change to another type of value, add a `valueType` parameter which accepts the following values: `OVERALL`, `RECOMMENDED` and `REGULAR`.
    
Valid values for `metric` are `VIEWS`, `SALES_AMOUNT`, `ITEM_PER_CART`, `ITEM_PURCHASED`, `UNIQUE_VISITOR`, `ORDERS`, `UNIQUE_ITEM_PURCHASED`.

`interval` can be predefined value such as `year`, `quarter`, `month`, `week`, `day`, and `hour`.  It can also be an expression such as `1.5h` (every `1.5` hours), `3M` (every `3` months), `2y` (every `2` year), etc.

To return top ten most viewed items for a period, use resource `/top/hits`.  For example:

    http://119.81.208.244:8090/fisher/top/hits?tenantId=FAMILYNARA2014&startDate=2015010100&endDate=2015020100
    
To return top ten most purchased items for a period, use resource `/top/sales`.  For example:

    http://119.81.208.244:8090/fisher/top/sales?tenantId=FAMILYNARA2014&startDate=2015010100&endDate=2015020100
    
Whenever error in encountered (in application logic), fisher will return JSON such as:

    {
       "error": "This is the error message"
    }
    
APIs that has time value accepts a `timeZone` parameter which determined which time zone there are in.  For example: `timeZone=Asia/Jakarta` or `timeZone=Asia/Kuala_Lumpur`.
    
## Items

You find information about item by using resource `/items`.  For example, to find information about item with id `11068` owned by tenant id `FAMILYNARA2014`, use the following URL:

    http://119.81.208.244:8090/fisher/items/FAMILYNARA2014/11068
    
To return number of stored items for a tenant, use the following URL:

    http://119.81.208.244:8090/fisher/items/{tenantId}/count 

To find related items for certain item, use the following URL:

    http://119.81.208.244:8090/fisher/items/{tenantId}/related/{id}
    
For example, if you want to find related items for item with id `11068` of tenant `tenant1`, you can use:

    http://119.81.208.244:8090/fisher/items/tenant1/related/11068

To search based on a string on multiple fields, you can send a POST request to:

    http://119.81.208.244:8090/fisher/items/{tenantId}/related
    
It accepts a JSON that contains `fields` and `value`, for example:

    {
       "fields": ["name", "category"],
       "value": "this is a string to search for"
    }

To upload CSV file that contains item, use the following URL:

    http://119.81.208.244:8090/fisher/items/{tenantId}/upload
    
The first line of CSV file must contains field names.

## Configuration

To retrieve information about current config, use the following resource:

    http://119.81.208.244:8090/fisher/config
    
To set new value for blacklisted tenant ids, send the `PUT` request to `/fisher/config/blacklist_tenants`.  For example:

    http://119.81.208.244:8090/fisher/config/blacklist_tenants/tenantid1,tenantid2,tenantid3
    
To clear blacklist value, send `DELETE` request to `/fisher/config/blacklist_tenants`.

To enable or disable pulling operations from Tapirus, send `PUT` request to `/fisher/config/pull_enabled`.  For example:

    http://119.81.208.244:8090/fisher/config/pull_enabled/false
    
or

    http://119.81.208.244:8090/fisher/config/pull_enabled/false
   
To set a different time to pull from Tapirus than default one, send `PUT` request to `/fisher/config/execution_time`.  Don't forget to disable pull operation before changing time to pull. For example:

    http://119.81.208.244:8090/fisher/config/execution_time/2015-01-01T00:00
   
To point to another Tapirus' deployment, set the value for `FISHER_TAPIRUS_URL` environment variable before running Fisher.  For example:

    export FISHER_TAPIRUS_URL=http://12.23.44.55:9999
    
If Tapirus returns another bucket, set the value for `FISHER_S3_BUCKET_NAME` environment variable before running Fisher.  For example:

    export FISHER_S3_BUCKET_NAME=newbuckets
    

## Database Schema

Fisher stores aggregations into Elasticsearch in periodical indices.  Statistics are stored in indices with name starting with `stat_` and followed by four digit year.  For example: `stat_2011`, `stat_2012`, `stat_2013`, `stat_2014`.  Inside each indices, there will be a tenant id as type.

The following Elasticsearch query can be used to retrieve an entry:

    GET stat_2015/SUPERBUY/2015-02-01T10:00:00
    
Information about most viewed items is stored in periodical indices like `top_hits_2014`, `top_hits_2015`, etc.  The following Elasticsearch query can be used to retrieve an entry:

    GET top_hits_2015/BUKALAPAK/2015-02-01T10:00
    
Information about most purchased items is stored in periodical indices like `top_views_2014`, `top_views_2015`, etc.  The following Elasticsearch query can be used to retrieve an entry:

    GET top_sales_2015/grouponid/2015-01-01T04:00
    
Fisher also track items information for every tenant id.  For example, items for tenant id `bukalapak` is stored in `item_bukalapak`.  You can find information about an item of a tenant by using query like:

    GET item_superbuymy/item/276995
    
To delete old items, use:

    DELETE stat_2014/BUKALAPAK
    
or

    DELETE stat_2014