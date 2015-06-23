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

returns:

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
      "orders": 0,
      "itemPurchased": {
          "overall": 0,
          "recommended": 0,
          "regular": 0
      },
      "conversionRate": 0,
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

