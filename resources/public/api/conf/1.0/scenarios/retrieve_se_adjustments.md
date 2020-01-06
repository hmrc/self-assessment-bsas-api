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
            <td><p>Simulates the scenario where no data was found</p></td>
        </tr>
        <tr>
            <td><p>SELF_EMPLOYMENT_ADJUSTED</p></td>
            <td><p>Simulates a successful response where adjustments are present for a self-employment</p></td>
        </tr>        
        <tr>
            <td><p>SELF_EMPLOYMENT_CONSOLIDATED</p></td>
            <td><p>Simulates a successful response where adjustments are present for a consolidated self-employment</p></td>
        </tr>
        <tr>
            <td><p>SELF_EMPLOYMENT_UNADJUSTED</p></td>
            <td><p>Simulates an unsuccessful response where adjustments are not present for a self-employment</p></td>
        </tr>
        <tr>
            <td><p>NOT_SELF_EMPLOYMENT</p></td>
            <td><p>Simulates an unsuccessful response where returned adjustments do not belong to a self-employment</p></td>
        </tr>
    </tbody>
</table>