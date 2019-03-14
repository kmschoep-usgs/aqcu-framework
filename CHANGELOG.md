# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html). (Patch version X.Y.0 is implied if not specified.)

## [unreleased]
### Added
- spring boot starter parent to pom for AspectJ usage
- Logging of execution times of AQ retrievals

### Changed
- This changelog with all changes since 0.0.1
 
##[0.0.5] - 2019-02-20
### Added
- additional attributes to field visit readings
- configurable timeouts to Aquarius retrievals

### Changed
- Update to AQ SDK 18.8.1
- controlConditions are now list of strings instead of enum.
- TimeSeriesDataService to pull an "extra" day of daily values timeseries data to accommodate the "2400" handling of daily value dates.
- Populate field visit readings values with expected filler values when crest stage readings and min/max indicator readings are null.

## [0.0.4] - 2018-11-02
### Added
- NwisRaClient
- WaterLevelRecordDeserializer
- WaterLevelRecordsDeserializer
- WaterQualitySampleRecordDeserializer
- FieldVisitMeasurement model
- InstantRange model
- MeasurementGrade model
- MinMaxData model
- MinMaxPoint model
- RatingModelErrorVector model
- ReportTimeSeriesPoint model
- CorrectionListService
- FieldVisitDataService
- FieldVisitDescriptionService
- GradeLookupService
- NwisRaService
- ParameterListService
- RatingCurveListService
- RatingModelInputValuesService 	
- TimeSeriesDataService
- TimeSeriesDescriptionListService
- TimeSeriesUniqueIdListService
- BigDecimalSummaryStatistics
- DoubleWithDisplayUtil
- AqcuReportUtils

## [0.0.3] - 2018-08-03
### Changed
- Update to AQ SDK 18.6.2

## [0.0.2] - 2018-06-15
### Added
- DownchainProcessorListService
- LocationDescriptionListService
- UpchainProcessorListService
- OffsetDateTimeGsonSerializer
- AquariusRetrievalUtils
- TimeSeriesUtils
- test coverage

### Changed
- Split RequestParameters into DateRangeRequestParameters and ReportRequestParameters
- Updated the Aquarius SDK to 18.5.2 to fix USGSMultiPoint deserialization issues.

### Removed
- LocationDescriptionService

## [0.0.1] - 2018-04-18
### Added
- AquariusRetrievalService
- LocationDescriptionService
- QualifierLookupService
- DataGapListBuilderService
- ReportPeriodPresent validation
- ReportPeriodPresentValidator
- StartDateBeforeEndDate validation
- StartDateBeforeEndDateValidator validation
- JavaToRClient
- DataGap model
- DataGapExtent model
- ExtendedCorrection model
- ExtendedCorrectionType model
- ReportMetadata model
- AqcuGsonBuilderFactory
- AqcuTimeUtils
- RequestParameters
- LocalDateGsonSerializer

[Unreleased]: https://github.com/USGS-CIDA/aqcu-framework/compare/aqcu-framework-0.0.5...master
[0.0.5]: https://github.com/USGS-CIDA/aqcu-framework/compare/aqcu-framework-0.0.4...aqcu-framework-0.0.5
[0.0.4]: https://github.com/USGS-CIDA/aqcu-framework/compare/aqcu-framework-0.0.3...aqcu-framework-0.0.4
[0.0.3]: https://github.com/USGS-CIDA/aqcu-framework/compare/aqcu-framework-0.0.2...aqcu-framework-0.0.3
[0.0.2]: https://github.com/USGS-CIDA/aqcu-framework/compare/aqcu-framework-0.0.1...aqcu-framework-0.0.2