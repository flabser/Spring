<?xml version="1.0" encoding="windows-1251"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">	
	<xsl:output method="html" encoding="windows-1251"/>
	<xsl:template match="/request">
		<html>
			<head>
				<title>SPRING - Ошибка авторизации</title>						
				<link type="text/css" rel="stylesheet" href="css/main.css"/>
				<link type="text/css" rel="stylesheet" href="css/view.css"/>		
				<script>
					<![CDATA[
						function CancelForm(){
			   	  			window.history.back();
						}
						function goToLogin(){
					 		window.location = "Logout";					
						}
					]]>
				</script>
				<style>
					A:hover{
						text-decoration:underline;
						color:#00AAFF !important;
					}
				</style>
			</head>
			<body style="background:#F1F8FF; margin:0px">
				<div style="background:#686662; height:50px;  padding:10px 0px 0px 20px; width:auto">
					<font style="color:#ffffff; font-family:corbel; font-size:28px;">Администратор</font>
				</div>
				<table  border="0" style="margin-top:140px">
				<tr>
					<td width="30%" align="right" style="font-size:9pt;">
						<font style="font-size:1.9em;">NextBase</font>
						<div style="clear:both; height:10px"/>
						<font style="font-size:9pt; color:gray">
							version <xsl:value-of select="error/version"/> &#169; Lab of the Future 2012
						</font>
						<br/>
					</td>
					<td width="1%"></td>
					<td style="height:500px" bgcolor="#FFCC00" width="1"></td>
					<td width="69%">
						<table style="width:100%;  margin-left:4%">
								<tr>
									<td>
										<font style="font-size:2em;">Ошибка авторизации</font>
									</td>
								</tr>
								<tr>
									<td>
										<ul style="font-size:0.9em; margin-top:15px">
											<li type="square">Проверьте правильность написания имени пользователя</li>
											<li type="square" style="margin-top:5px; ">Убедитесь, что пароль вводится на том же языке, что и при регистрации</li>
											<li type="square" style="margin-top:5px">Убедитесь, не нажат ли [Caps Lock]</li>
											<li type="square" style="margin-top:5px">
												<a href="javascript:goToLogin()" style="font-weight:bold;color:#0088CC; font-size:13px">
													<font>Повторить попытку авторизации</font>
												</a>
											</li>
										</ul>
									</td>
								</tr>
							</table>
							
						</td>
					</tr>
					<tr>
						<td></td>
						<td></td>
						<td bgcolor="#FFCC00" width="1"></td>
						<td>
							<div style="font-family:arial; font-size:0.75em; ">
								&#xA0;
								<a href="http://www.flabs.kz" target="_blank" style="font-weight:bold;color:#0088CC;">
									<font>Lab of the Future</font>
								</a>&#xA0; &#8226; &#xA0;
								<a href="http://www.smartdoc.kz" target="_blank" style="font-weight:bold;color:#0088CC;">
									<font>Feedback</font>
								</a>
							</div>
						</td>
					</tr>
				</table>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>