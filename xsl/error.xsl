<?xml version="1.0" encoding="windows-1251"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">	
<xsl:output method="html" encoding="windows-1251"/>
<xsl:template match="/request">
	<html>
		<head>
			<title>SPRING - Ошибка</title>						
			<link type="text/css" rel="stylesheet" href="css/main.css"/>
			<link type="text/css" rel="stylesheet" href="css/view.css"/>
			<script>
				<![CDATA[
					function CancelForm(){
			   	  		window.history.back();
					}
					function goToLogin(){
					 	window.location = "Provider?type=static&id=start&autologin=0";					
					}
					function reloadPage(){
					 	window.location.reload()					
					}
					
					function loadXmlPage(){
			   	  		window.location.href = window.location + "&onlyxml=1"
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
							version <xsl:value-of select="error/message/version"/> &#169; Lab of the Future 2012
						</font>
						<br/>
					</td>
					<td width="1%"></td>
					<td style="height:500px" bgcolor="#CD0000" width="1"></td>
					<td width="69%">
						<table style="width:100%;  margin-left:4%">
							<tr>
								<td>
									<xsl:choose>
										<xsl:when test="error/@type = 'INTERNAL'">
											<xsl:choose>
												<xsl:when test="contains (request/error/message/errortext, 'Old password has not match')">
													<font style="font-size:2em;">Некорректно заполнено поле "старый пароль" </font>
												</xsl:when>
												<xsl:otherwise>
													<font style="font-size:2em;">Внутренняя ошибка сервера</font>
													<font style="font-size:1em;"><xsl:value-of select="request/error/message/errortext"/></font>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:when>
										<xsl:when test="error/@type = 'RULENOTFOUND'">
											<font style="font-size:2em;">Правило не найдено</font>
										</xsl:when>
										<xsl:when test="error/@type = 'PROVIDERERROR'">
											<font style="font-size:2em;">Запрос не распознан</font>
										</xsl:when>
										<xsl:when test="error/@type = 'XSLTNOTFOUND'">
											<font style="font-size:2em;">Страница XSLT не найдена</font>
										</xsl:when>
										<xsl:when test="error/@type = 'DATAENGINERROR'">
											<font style="font-size:2em;">Ошибка базы данных</font>
										</xsl:when>
										<xsl:when test="error/@type = 'XSLT_TRANSFORMATOR_ERROR'">
											<font style="font-size:2em;">Ошибка XSLT шаблона </font>
										</xsl:when>
										<xsl:when test="error/@type = 'DOCUMENTEXCEPTION'">
											<font style="font-size:2em;">Ошибка документа</font>
										</xsl:when>
										<xsl:when test="error/@type = 'resourcenotfound'">
											<font style="font-size:2em;">Документ не найден.</font>
										</xsl:when>
										<xsl:when test="error/@type = 'CLASS_NOT_FOUND_EXCEPTION'">
											<font style="font-size:2em;">Класс не найден</font>
										</xsl:when>
										<xsl:otherwise>
											<font style="font-size:2em;">Ошибка</font>
										</xsl:otherwise>
									</xsl:choose>
								</td>
							</tr>
							<tr>
								<td>
									<br/>
										<xsl:choose>
											<xsl:when test="error/@type = 'INTERNAL'">
												<ul style="font-size:0.9em; margin-top:15px">
													<li type="square" style="margin-top:5px">
														<a href="javascript:CancelForm()" style="font-weight:bold;color:#0088CC; font-size:13px">
															<font>Назад...</font>
														</a>
													</li>
													<xsl:if test="contains (request/error/message/errortext, 'Old password has not match')">
														<li type="square" style="margin-top:5px; ">Убедитесь, что пароль вводится на том же языке, что и при регистрации</li>
														<li type="square" style="margin-top:5px">Посмотрите, не нажат ли [Caps Lock]</li>
													</xsl:if>
												</ul>
											</xsl:when>
											<xsl:when test="error/@type = 'PROVIDERERROR'">
												<ul style="font-size:0.9em; margin-top:15px">
													<li type="square" style="margin-top:5px">
														<a href="javascript:reloadPage()">
															<font style="font-weight:bold;color:#0088CC">Перезагрузка</font>
														</a>
													</li>
													<li type="square" style="margin-top:5px">
														<a href="javascript:CancelForm()" style="font-weight:bold;color:#0088CC; font-size:13px">
															<font>Назад...</font>
														</a>
													</li>
												</ul>
											</xsl:when>
											<xsl:when test="error/@type = 'LOGINERROR'">
												<ul style="font-size:0.9em; margin-top:15px">
													<li type="square" style="margin-top:5px">
														<a href="javascript:reloadPage()" style="font-weight:bold;color:#0088CC; font-size:13px">
															<font>Перезагрузка</font>
														</a>
													</li>
													<li type="square" style="margin-top:5px">
														<a href="javascript:CancelForm()" style="font-weight:bold;color:#0088CC; font-size:13px">
															<font>Назад...</font>
														</a>
													</li>
												</ul>
											</xsl:when>
											<xsl:when test="error/@type = 'RULENOTFOUND'">
												<ul style="font-size:0.9em; margin-top:15px">
													<li type="square" style="margin-top:5px">
														<a href="javascript:reloadPage()" style="font-weight:bold;color:#0088CC; font-size:13px">
															<font>Перезагрузка</font>
														</a>
													</li>
													<li type="square" style="margin-top:5px">
														<a href="javascript:CancelForm()" style="font-weight:bold;color:#0088CC; font-size:13px">
															<font>Назад...</font>
														</a>
													</li>
												</ul>
											</xsl:when>
											<xsl:when test="error/@type = 'XSLTNOTFOUND'">
												<ul style="font-size:0.9em; margin-top:15px">
													<li type="square" style="margin-top:5px">
														<a href="javascript:reloadPage()" style="font-weight:bold;color:#0088CC; font-size:13px">
															<font>Перезагрузка</font>
														</a>
													</li>
													<li type="square" style="margin-top:5px">
														<a href="javascript:CancelForm()" style="font-weight:bold;color:#0088CC; font-size:13px">
															<font>Назад...</font>
														</a>
													</li>
													<li type="square" style="margin-top:5px">
														<a href="javascript:loadXmlPage()" style="font-weight:bold;color:#0088CC; font-size:13px">
															<font>Посмотреть исходный xml документ</font>
														</a>
													</li>
												</ul>
											</xsl:when>
											<xsl:when test="error/@type = 'XSLT_TRANSFORMATOR_ERROR'">
												<ul style="font-size:0.9em; margin-top:15px">
													<li type="square" style="margin-top:5px">
														<a href="javascript:reloadPage()" style="font-weight:bold;color:#0088CC; font-size:13px">
															<font>Перезагрузка</font>
														</a>
													</li>
													<li type="square" style="margin-top:5px">
														<a href="javascript:CancelForm()" style="font-weight:bold;color:#0088CC; font-size:13px">
															<font>Назад...</font>
														</a>
													</li>
													<li type="square" style="margin-top:5px">
														<a href="javascript:loadXmlPage()" style="font-weight:bold;color:#0088CC; font-size:13px">
															<font>Посмотреть исходный xml документ</font>
														</a>
													</li>
												</ul>
											</xsl:when>
											<xsl:when test="error/@type = 'DATAENGINERROR'">
												<ul style="font-size:0.9em; margin-top:15px">
													<li type="square" style="margin-top:5px">
														<a href="javascript:reloadPage()" style="font-weight:bold;color:#0088CC; font-size:13px">
															<font>Перезагрузка</font>
														</a>
													</li>
													<li type="square" style="margin-top:5px">
														<a href="javascript:CancelForm()" style="font-weight:bold;color:#0088CC; font-size:13px">
															<font>Назад...</font>
														</a>
													</li>
												</ul>
											</xsl:when>
											<xsl:when test="error/@type = 'DOCUMENTEXCEPTION'">
												<ul style="font-size:0.9em; margin-top:15px">
													<li type="square" style="margin-top:5px">
														<a href="javascript:reloadPage()" style="font-weight:bold;color:#0088CC; font-size:13px">
															<font>Перезагрузка</font>
														</a>
													</li>
													<li type="square" style="margin-top:5px">
														<a href="javascript:CancelForm()" style="font-weight:bold;color:#0088CC; font-size:13px">
															<font>Назад...</font>
														</a>
													</li>
												</ul>
											</xsl:when>
											<xsl:when test="error/@type = 'CLASS_NOT_FOUND_EXCEPTION'">
												<ul style="font-size:0.9em; margin-top:15px">
													<li type="square" style="margin-top:5px"><a href="javascript:reloadPage()">Перезагрузка</a></li>
													<li type="square" style="margin-top:5px"><a href="javascript:CancelForm()">Назад...</a></li>
												</ul>
											</xsl:when>
											<xsl:when test="error/@type = 'resourcenotfound'">
												<ul style="font-size:0.9em; margin-top:15px">
													<li type="square" style="margin-top:5px">Возможно данный документ был удален</li>
													<li type="square" style="margin-top:5px">
														<a href="javascript:reloadPage()" style="font-weight:bold;color:#0088CC; font-size:13px">
															<font>Перезагрузка</font>
														</a>
													</li>
													<li type="square" style="margin-top:5px">
														<a href="javascript:CancelForm()" style="font-weight:bold;color:#0088CC; font-size:13px">
															<font>Назад...</font>
														</a>
													</li>
													<li type="square" style="margin-top:5px">
														<a href="javascript:loadXmlPage()" style="font-weight:bold;color:#0088CC; font-size:13px">
															<font>Посмотреть исходный XML документ</font>
														</a>
													</li>
												</ul>
											</xsl:when>
											<xsl:otherwise>
												<ul style="font-size:0.9em; margin-top:15px">
													<li type="square" style="margin-top:5px">
														<a href="javascript:reloadPage()" style="font-weight:bold;color:#0088CC; font-size:13px">
															<font>Перезагрузка</font>
														</a>
													</li>
													<li type="square" style="margin-top:5px">
														<a href="javascript:CancelForm()" style="font-weight:bold;color:#0088CC; font-size:13px">
															<font>Вернуться на предыдущую страницу</font>
														</a>
													</li>
												</ul>
											</xsl:otherwise>
										</xsl:choose>
									</td>
								</tr>
							</table>
							
						</td>
					</tr>
					<tr>
						<td></td>
						<td></td>
						<td bgcolor="#CD0000" width="1"></td>
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