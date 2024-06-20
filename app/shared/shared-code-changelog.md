# Shared Code Changelog

This file contains a log of changes made to the MTD shared code
(that is, the 'shared' folder that is propagated across to each MTD API).

For the Shared Code update steps, see: https://confluence.tools.tax.service.gov.uk/display/MTE/Shared+Code

Place new items at the top, and auto-format the file...

## June 20 2024:  Shared code updates

- Added methods and parameters that were required self-employment-business-api in 
  BaseDownstreamConnector, RequestHandlerBuilder, TaxYear and CommonMtdErrors.

## June 14 2024:  Shared test code

- Updated the shared test code to work "as-is" for all APIs rather than just BSAS;
e.g. removed assumptions of which API versions are available, enabled etc.

## June 10 2024:  New changelog

- Added the shared-code-changelog.md file.
