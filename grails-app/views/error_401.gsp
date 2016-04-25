<!DOCTYPE html>
<html>
	<head>
		<title>Prisma</title>
    <meta name="layout" content="error" />
	</head>
	<body>
    <g:form url="[controller:'index',action:'index']" method="post" name="indexForm" id="indexForm">
      <input type="submit" style="display:none" />
    </g:form>
    <script type="text/javascript">
      document.getElementById('indexForm').submit();
    </script>
	</body>
</html>