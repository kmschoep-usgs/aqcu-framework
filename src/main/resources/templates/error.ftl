<html>
	<body>
		<h1>Error Page</h1>
		<div id='created'>${timestamp?datetime}</div>
		<div>There was an unexpected error (type=${error}, status=${status}).</div>
		<div>
			<ul>
			<#list errors as err>
				<#if err.field??>
				<li>Field: ${err.field}; Value: ${err.rejectedValue!"[null]"}; Error: ${err.defaultMessage}</li>
				<#else>
				<li>Error: ${err.defaultMessage}</li>
				</#if>
			</#list>
			</ul>
		</div>
	</body>
</html>