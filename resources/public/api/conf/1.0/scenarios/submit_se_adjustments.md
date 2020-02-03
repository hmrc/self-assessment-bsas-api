<p>Scenario simulations using Gov-Test-Scenario headers are only available in the sandbox environment.</p>
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
            <td><p>SELF_EMPLOYMENT</p></td>
            <td><p>Simulates a successful response for a self-employment</p></td>
        </tr>
        <tr>
            <td><p>NOT_SELF_EMPLOYMENT</p></td>
            <td><p>Simulates the error response where the BSAS ID is for an incorrect type of business</p></td>
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
            <td><p>SELF_EMPLOYMENT_OVER_CONSOLIDATED_EXPENSES_THRESHOLD</p></td>
            <td><p>Simulates the error response where the cumulative turnover amount exceeds the consolidated expenses threshold</p></td>
        </tr>
        <tr>
            <td><p>RULE_TRADING_INCOME_ALLOWANCE_CLAIMED</p></td>
            <td><p>Simulates the error response where a claim for trading income allowance was made - cannot also have expenses</p></td>
        </tr>
        <tr>
            <td><p>UK_PROPERTY_ADJUSTED</p></td>
            <td><p>Simulates the error response that may occur if a UK property BSAS ID is used</p></td>
        </tr>
        <tr>
            <td><p>RESULTING_VALUE_NOT_PERMITTED</p></td>
            <td><p>Simulates the error response that may occur if one or more adjustments submitted would result in a negative value</p></td>
        </tr>
    </tbody>
</table>
