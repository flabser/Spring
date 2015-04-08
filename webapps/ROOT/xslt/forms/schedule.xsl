<?xml version="1.0" encoding="windows-1251"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:import href="../templates/action.xsl" />
	<xsl:output method="html" encoding="utf-8" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
	 doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" indent="yes"/>
	<xsl:template match="/request">
		<head>
			<title>
				Расписание&#xA0;-<xsl:value-of select="document/userid"/>
			</title>
			<link type="text/css" rel="stylesheet" href="css/main.css"/>
			<link type="text/css" rel="stylesheet" href="css/dialogs.css"/>
			<link type="text/css" rel="stylesheet" href="css/view.css"/>
			<link type="text/css" rel="stylesheet" href="css/form.css"/>
			<link type="text/css" rel="stylesheet" href="css/outline.css"/>	
			<script type="text/javascript" src="scripts/jquery/js/jquery-1.4.2.js"/>
			<script type="text/javascript" src="scripts/outline.js"/>
			<script type="text/javascript" src="scripts/service.js"/>
			<script type="text/javascript" src="scripts/dialogs.js"/>
			<script type="text/javascript" src="scripts/form.js"/>
		</head>
		<body>
			<div id="wrapper" style="background:#F1F8FF">
				<div style="background:#686662; height:50px;  padding:10px 0px 0px 20px">
						<font style="color:#ffffff; font-family:corbel; font-size:28px;">Администратор</font>
					</div>
					<div style="height:20px; padding-top:15px; padding-left:5px">
						<!-- <a class="btn">
							<xsl:attribute name="href">javascript:window.location = window.location + '&amp;onlyxml=1'</xsl:attribute>
							<font style="font-size:13px; text-decoration:none">Show as XML</font>
						</a> -->
						<!-- <a class="btn">
							<xsl:attribute name="href">javascript:saveUser()</xsl:attribute>
							<font style="font-size:13px;  text-decoration:none">Save &amp; Close</font>
						</a> -->
						<a class="btn" style="float:right">
							<xsl:attribute name="href">javascript:window.history.back();</xsl:attribute>
							<font style="font-size:13px; text-decoration:none">Закрыть</font>
						</a>
					</div>
			<br/>
			<br/>
			<font style="font-family:corbel; font-size:25px; margin-left:15px"> Расписание:&#xA0;</font>
			<xsl:value-of select="document/userid"/>
			<hr/>
			<form action="Provider" method="post" id="scriptest" enctype="application/x-www-form-urlencoded">
				<table border="0" style="margin-top:30px;width:100%">
					<tr>
						<td class="fc" style="color:#686662; font-size:12px; vertical-align:middle">Расписание :&#xA0;</td>
						<td>
							<input type="text" name="userid" style="width:300px; padding:5px" value="{document/userid}"/>
						</td>
					</tr>
					
				</table>
				<!-- Скрытые поля -->
				<input type="hidden" name="key" id="key" value="{document/@docid}"/>
			</form>
			</div>
			<div id="footer">
			        <div style=" padding:2px 10px 0px 10px; color: #444444; width:600px; margin-top:3px; float:left">
						<a target="_parent"  href="Logout" title="{outline/fields/logout/@caption}">
							<img src="img/logout.gif" style="width:15px; height:15px"/>						
							<font style="margin-left:5px; font-size:11px; vertical-align:3px">Выйти</font> 
						</a>&#xA0;
					</div>
					<div style=" padding:5px 20px 0px 10px;  width:300px; float:right">
						<div id="langview" style="font-size:12px; float:right">
							<font style="vertical-align:2px; color: #444444 !important;">NextBase © 2012</font>
						</div>
					</div>
			     </div>
		</body>
	</xsl:template>
</xsl:stylesheet>