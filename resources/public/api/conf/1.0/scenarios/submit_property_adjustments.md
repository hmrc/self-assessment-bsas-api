<p>Scenario simulations using Gov-Test-Scenario headers is only available in the sandbox environment.</p>
<table>
    <thead>
        <tr>
            <th>Header Value (Gov-Test-Scenario)</th>
            <th>Scenario</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><p>N/A - DEFAULT</p></td>
            <td><p>Simulates the scenario where no data could be found</p></td>
        </tr>
        <tr>
            <td><p>UK_PROPERTY_FHL</p></td>
            <td><p>Simulates a successful response for a FHL UK Property</p></td>
        </tr>
        <tr>
            <td><p>UK_PROPERTY_NON_FHL</p></td>
            <td><p>Simulates a successful response for a Non-FHL UK Property</p></td>
        </tr>
        <tr>
            <td><p>NOT_UK_PROPERTY</p></td>
            <td><p>Simulates the error response that may occur if a self-employment BSAS ID is used</p></td>
        </tr>
        <tr>
            <td><p>SUMMARY_STATUS_INVALID</p></td>
            <td><p>Simulates the error response where the summary is invalid and cannot be adjusted</p></td>
        </tr>
        <tr>
            <td><p>SUMMARY_STATUS_SUPERSEDED</p></td>
            <td><p>Simulates the error response where the summary is superseded and cannot be adjusted</p></td>
        </tr>
        <tr>
            <td><p>BSAS_ALREADY_ADJUSTED</p></td>
            <td><p>Simulates the error response where the summary has already been adjusted</p></td>
        </tr>
        <tr>
            <td><p>PROPERTY_OVER_CONSOLIDATED_EXPENSES_THRESHOLD</p></td>
            <td><p>Simulates the error response where the cumulative turnover exceeds the threshold for consolidated expenses</p></td>
        </tr>
        <tr>
            <td><p>PROPERTY_INCOME_ALLOWANCE_CLAIMED</p></td>
            <td><p>Simulates the error response where property income allowance has been claimed and therefore no further expenses can be claimed</p></td>
        </tr>
        <tr>
            <td><p>PROPERTY_TYPE_OF_BUSINESS_INCORRECT</p></td>
            <td><p>Simulates the error response where either the fields submitted or the BSAS ID are for an incorrect type of business</p></td>
        </tr>
        <tr>
            <td><p>RESULTING_VALUE_NOT_PERMITTED</p></td>
            <td><p>Simulates the error response that may occur if one of more adjustments submitted would result in a negative value</p></td>
        </tr>
    </tbody>
</table>
