<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:import href="../templates/view.xsl"/>
	<xsl:variable name="type">get_doc_list</xsl:variable>
	<xsl:variable name="doctype">log</xsl:variable>
	<xsl:output method="html" encoding="utf-8" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
	 doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" indent="yes"/>
	
	<xsl:template match="/request">
		<html>
			<head>
				<title>
					Spring Администратор - Список Записей 
				</title>
				<link type="text/css" rel="stylesheet" href="css/main.css"/>
				<link type="text/css" rel="stylesheet" href="css/dialogs.css"/>
				<link type="text/css" rel="stylesheet" href="css/view.css"/>
				<link type="text/css" rel="stylesheet" href="css/outline.css"/>			
				<script type="text/javascript" src="/SharedResources/jquery/js/jquery-1.4.2.js"/>
				<script type="text/javascript" src="/SharedResources/jquery/js/ui/jquery.ui.core.js"/>
				<script type="text/javascript" src="/SharedResources/jquery/js/ui/jquery.ui.widget.js"/>
				<script type="text/javascript" src="/SharedResources/jquery/js/ui/jquery.ui.mouse.js"/>				
				<script type="text/javascript" src="/SharedResources/jquery/js/ui/jquery.ui.position.js"/>
				<script type="text/javascript" src="/SharedResources/jquery/js/ui/jquery.ui.dialog.js"/>
				<script type="text/javascript" src="/SharedResources/jquery/js/ui/jquery.ui.draggable.js"/>
				<script type="text/javascript" src="/SharedResources/jquery/js/ui/jquery.ui.dialog.js"/>
				<script type="text/javascript" src="/SharedResources/jquery/js/ui/jquery.ui.button.js"/>
				<script type="text/javascript" src="scripts/outline.js"/>
				<script type="text/javascript" src="scripts/dialogs.js"/>
				<script type="text/javascript" src="scripts/service.js"/>
				<script type="text/javascript" src="scripts/view.js"/>
				<link type="text/css" rel="stylesheet" href="/SharedResources/jquery/css/smoothness/jquery-ui-1.8.20.custom.css"/>
				<script>
					function onLoadActions(){
					service = '<xsl:value-of select="currentview/@service"/>';
					id = '<xsl:value-of select="currentview"/>';
					app = '<xsl:value-of select="currentview/@app"/>';
					dbid = '<xsl:value-of select="currentview/@dbid"/>';
					curPage = '<xsl:value-of select="currentview/@page"/>';
					refreshAction();
					refresher();
							lw = $("#loadingpage").width();
							lh = $("#loadingpage").height();
							lt = ($(window).height() - lh )/2;
							ll = ($(window).width() - lw )/2;
							$("#loadingpage").css("top",lt);
							$("#loadingpage").css("left",ll);
							$("#loadingpage").css("z-index",1);
					}
				</script>
			</head>
			<body>
				<div id="wrapper" style="background:#F1F8FF">
					<div style="background:#686662; height:50px;  padding:10px 0px 0px 20px">
						<font style="color:#ffffff; font-family:corbel; font-size:28px;">Администратор</font>
					</div>
					<div style="height:20px; padding-top:15px; padding-left:230px">
						<a class="btn">
							<xsl:attribute name="href">javascript:window.location.reload();</xsl:attribute>
							<font style="font-size:13px; text-decoration:none">Обновить</font>
						</a>
					</div>
					<div id='loadingpage' style='position:absolute; display:none'>
						<img src='/SharedResources/img/classic/4(4).gif'/>
					</div>	
					<div id="outline">
						<div id="outline-header">
							<font style="font-size:1.1em; font-weight:bold">
							</font>
						</div>
						<div id="outline-container">
						<table border="0" style=" border-collapse: collapse; width:245px; margin-top:20px">
							<xsl:for-each select="outline/entry"> 
								<tr>
									<td>
										<a href="{@url}" style="color:white;">
											<div class="menuentry" style="width:77%;">
												<xsl:if test="/request/@id = @id">
													<xsl:attribute name="class">menuentrycurrent</xsl:attribute>
												</xsl:if>
												<xsl:value-of select="@caption"/>
												
											</div>
										</a>
									</td>
								</tr>
							</xsl:for-each>
							</table>
						</div>
					</div>
					<span id="view" class="viewframe{outline/category[entry[@current=1]]/@id}">
						<div class="actionbar">	
							<span class="action">
							</span>	
							<xsl:call-template name="viewstat"/>	
						</div>	
											
						<table class="viewtable">
							<tr class="th">
								<td width="3%" class="thcell">
									<input type="checkbox" id="allchbox" onClick="checkAll(this);"/>							
								</td>
								<td width="20%" class="thcell">Название</td>	
								<td width="15%" class="thcell">Размер</td>					
							</tr>
							<xsl:for-each select="view/entry">
								<tr onmouseover="javascript:elemBackground(this,'EEEEEE')" onmouseout="elemBackground(this,'FFFFFF')">
									<xsl:variable name="num" select="position()" />
									<xsl:attribute name="bgcolor">#ffffff</xsl:attribute>
									<td width="3%" style="border:1px solid #ccc; text-align:center">
										<input type="checkbox" name="chbox" id="{@key}"/>
									</td>
									<td style="border: 1px solid #ccc; padding-left:5px">		
										<a class="doclink" title="{name}">
											<xsl:attribute name="href">Provider?type=edit&amp;element=log&amp;id=<xsl:value-of select="name"/></xsl:attribute>
											<xsl:value-of select="name"/>								
										</a>															
									</td>	
									<td class="title" style="border: 1px solid #ccc; padding-left:5px">							
										<xsl:value-of select="length"/>															
									</td>						
								</tr>
							</xsl:for-each>
						</table>
					</span>
				</div>
				<div id="footer">
			        <div style=" padding:2px 10px 0px 10px; color: #444444; width:600px; margin-top:3px; float:left">
						<a target="_parent"  href="Logout" title="{outline/fields/logout/@caption}">
							<img src="img/logout.gif" style="width:15px; height:15px" alt=""/>						
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
		</html>
	</xsl:template>
</xsl:stylesheet>