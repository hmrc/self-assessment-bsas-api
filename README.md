# self-assessment-bsas-api

## Requirements

- Scala 3.5.2
- Java 11
- sbt 1.9.7
- [Service Manager](https://github.com/hmrc/sm2)

# To run locally

`sbt "run 9787"`
or
`sbt run`

Start the service manager profile: `sm2 --start MTDFB_BSAS`

## Running tests

```
sbt test
sbt it/test
```


## To view the OAS

To view documentation locally ensure the Self Assessment BSAS Api is running, and run api-documentation-frontend:
`./run_local_with_dependencies.sh`

Then go to http://localhost:9680/api-documentation/docs/openapi/preview and use this port and version:
`http://localhost:9787/api/conf/7.0/application.yaml`

## Changelog

You can see our changelog [here](https://github.com/hmrc/income-tax-mtd-changelog/wiki)

## Support and Reporting Issues

You can create a GitHub issue [here](https://github.com/hmrc/income-tax-mtd-changelog/issues)

## API Reference / Documentation

Available on
the [HMRC Developer Hub](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/self-assessment-bsas-api/3.0)

# License

This code is open source software licensed under
the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
