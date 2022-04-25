# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased](https://github.com/internetofwater/nldi-services/compare/nldi-services-1.10.0...master)
### Changed
* Adjusted measure estimation to avoid null result

## [1.10.0](https://github.com/internetofwater/nldi-services/compare/nldi-services-1.9.0...nldi-services-1.10.0)
### Changed
* Fixed pygeoapi response handling

## [1.9.0](https://github.com/internetofwater/nldi-services/compare/nldi-services-1.8.0...nldi-services-1.9.0)
### Changed
* Fixed navigation URL in hydrolocation result

## [1.8.0](https://github.com/internetofwater/nldi-services/compare/nldi-services-1.7.0...nldi-services-1.8.0)
### Added
* Endpoint to query hydrologic locations

## [1.7.0](https://github.com/internetofwater/nldi-services/compare/nldi-services-1.6.0...nldi-services-1.7.0)

### Changed
* Fixed calls to pygeoapi endpoint

## [1.6.0](https://github.com/internetofwater/nldi-services/compare/nldi-services-1.5.0...nldi-services-1.6.0)
### Added
* Parameters to allow trimming a flowline navigation up to the location of a feature

### Changed
* Updated integration tests to use new Docker image

## [1.5.0](https://github.com/internetofwater/nldi-services/compare/nldi-services-1.4.0...nldi-services-1.5.0)
### Added
* getBasin has a new splitCatchment=<true|false> parameter to retrieve a catchment from the basin that 
is associated with the specified feature
* Feature properties now includes a "type" field to indicate catchment association

## [1.4.0](https://github.com/internetofwater/nldi-services/compare/nldi-services-1.3.0...nldi-services-1.4.0)
### Changed
* Missing parameter error now returns an informative message
* Updated flowline navigation to ignore coastal features

### Added
* Add `?simplified=false` parameter to retrieve full resolution basin boundary

## [1.3.0](https://github.com/internetofwater/nldi-services/compare/nldi-services-1.2.0...nldi-services-1.3.0)
### Changed
*   /basin request now returns a 404 instead of 500 error when basin is not found (WDFN-334)
*   Updated README for consistency and clearer dev setup
*   Updated getCharacteristicData (/{featureSource}/{featureID}/{characteristicType}) to return 404s when a comid is not found
*   Update validation of distance to accept an empty value by switching to a regex pattern
*   Add features collection API /linked-data/{featureSource}
*   Updated Swagger to show distance is in kilometers
*   Add code coverage
*   Migrate from springfox to springdoc
*   Added swagger annotations
*   Implement HTML media type for NLDI resources
*   Add new API to query for a feature by latitude and longitude
*   Add new navigation endpoint and deprecate navigate endpoint
*   Add new API for /lookups
*   Add redirect for /lookups/{characteristicType} to /lookups/{characteristicType}/characteristics
*   Make distance a required parameter for the new flowlines APIs.
  
## [1.2.0](https://github.com/internetofwater/nldi-services/compare/nldi-services-1.1.0...nldi-services-1.2.0)
### Changed
*   REST endpoint mappings
*   Updated POM dependencies
*   Updated URL paths used in deployment
*   CORS

## [1.1.0](https://github.com/internetofwater/nldi-services/compare/nldi-services-1.0.0...nldi-services-1.1.0)
### Changed
*   Updated POM dependencies

## [1.0.0](https://github.com/internetofwater/nldi-services/compare/nldi-services-0.9...nldi-services-1.0.0)
### Added
*   CSRF protection
### Changed
*   New CI database
*   Updated POM dependencies

## [0.9.0](https://github.com/internetofwater/nldi-services/compare/nldi-services-0.8...nldi-services-0.9)
### Changed
*   Updated to use the usgswma/openjdk image and build the project in the Dockerfile
*   Updated POM dependencies

## [0.8.0](https://github.com/internetofwater/nldi-services/compare/nldi-services-0.7.0...nldi-services-0.8)
### Changed
*   Using Spring Boot
*   Docker container created
*   Load environment variables via entrypoint script
*   Setup Jenkinsfiles for build and deploy jobs
*   Set "nldi" as the context path for the service
*   Retrofit to build via Jenkins shared libraries
