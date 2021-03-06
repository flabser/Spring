<?xml version="1.0" encoding="windows-1251"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
		
	<xsl:template name="viewstat">	
		<table style="margin-top:5px;width:99%;font-size:0.7em">
			<tr>
				<td width="33%">
					<span style="font-size: 12px">
						��������:&#xA0;<xsl:value-of select="query/@currentpage"/> �� <xsl:value-of select="query/@maxpage"/>
					</span>
				</td>
				<td width="33%">
					<xsl:call-template name="prepagelist"/>
				</td>
				<td width="33%" style="text-align:right; font-size: 12px">
					����: <xsl:value-of select="query/@count"/>
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="pagenavigator">
		<xsl:param name="currentpage" select="query/@currentpage"/>
	  	<xsl:param name="maxpage" select="query/@maxpage"/>
	  	<xsl:param name="nextpage" select="$currentpage + 1"/>
	  	<xsl:param name="nextpage10" select="$nextpage + 9"/>
	  	<xsl:param name="prevpage" select="$currentpage - 1"/>
	  	<xsl:param name="prevpage10" select="$prevpage - 9"/>	
	  	<xsl:param name="maxpage10" select="$maxpage - 9"/>
    	<xsl:choose>
 			<xsl:when test="$prevpage10&lt;10">
 				&lt;x10 					
 			</xsl:when>
 			<xsl:otherwise> 				
      			<a>      
      				<xsl:attribute name="title">jump to <xsl:value-of select="$prevpage10"/></xsl:attribute>				
      				<xsl:attribute name="href">Provider?type=get_outline&amp;subtype=<xsl:value-of select="$type"/>&amp;id=<xsl:value-of select="$doctype"/>&amp;page=<xsl:value-of select="$prevpage10"/>&amp;dbid=<xsl:value-of select="query/@dbid" /></xsl:attribute>
      				&lt;x10
      			</a>
      		</xsl:otherwise>
      	</xsl:choose>
      	&#xA0;
      	<xsl:choose>
 			<xsl:when test="$prevpage=0"> 				
 				&lt;
 			</xsl:when>
 			<xsl:otherwise> 			
      			<a>
      				<xsl:attribute name="href">Provider?type=get_outline&amp;subtype=<xsl:value-of select="$type"/>&amp;id=<xsl:value-of select="$doctype"/>&amp;page=<xsl:value-of select="$prevpage"/>&amp;dbid=<xsl:value-of select="query/@dbid" /></xsl:attribute>
      				&lt;
      			</a>
   			</xsl:otherwise>
   		</xsl:choose>
      	&#xA0;&#xA0;
   		 <xsl:choose>
			<xsl:when test="$currentpage=$maxpage">
				&gt;
			</xsl:when>
			<xsl:otherwise>
 				<a>
      				<xsl:attribute name="href">Provider?type=get_outline&amp;subtype=<xsl:value-of select="$type"/>&amp;id=<xsl:value-of select="$doctype"/>&amp;page=<xsl:value-of select="$nextpage"/>&amp;dbid=<xsl:value-of select="query/@dbid" /></xsl:attribute>
      				&gt;
				</a>
      		</xsl:otherwise>
      	</xsl:choose>
      	&#xA0;
   		<xsl:choose>
 			<xsl:when test="$nextpage10 > $maxpage10">
 				<a>
      				<xsl:attribute name="href">Provider?type=get_outline&amp;subtype=<xsl:value-of select="$type"/>&amp;id=<xsl:value-of select="$doctype"/>&amp;page=<xsl:value-of select="$nextpage10"/>&amp;dbid=<xsl:value-of select="query/@dbid" /></xsl:attribute>
      				<xsl:attribute name="title">jump to <xsl:value-of select="$nextpage10"/><xsl:value-of select="$maxpage10"/></xsl:attribute>
      				x10&gt;
				</a> 					 					
 			</xsl:when>
 			<xsl:otherwise>
 				x10&gt;
      		</xsl:otherwise>
      	</xsl:choose>
  </xsl:template>
  
  <xsl:template name="prepagelist">
	<xsl:if test="query/@maxpage !=1">
		<table style="margin-top:10px; margin:0;">
			<xsl:variable name="curpage" select="query/@currentpage"/>
			<xsl:variable name="prevpage" select="$curpage -1 "/>
			<xsl:variable name="beforecurview" select="substring-before(@id,'.')"/> 
           	<xsl:variable name="aftercurview" select="substring-after(@id,'.')"/> 
			<tr>
				<xsl:if test="query/@currentpage>1">
					<td>
						<a href="">
							<xsl:attribute name="href">javascript:updateView('<xsl:value-of select="@type"/>','<xsl:value-of select="@id"/>',1,'<xsl:value-of select="@dbid"/>','<xsl:value-of select="@dbid"/>')</xsl:attribute>
							<font style="font-size:12px">&lt;&lt;</font>
						</a>&#xA0;
					</td>
					<td>
						<a href="">
							<xsl:attribute name="href">javascript:updateView('<xsl:value-of select="@type"/>','<xsl:value-of select="@id"/>',<xsl:value-of select="$prevpage"/>,'<xsl:value-of select="@dbid"/>','<xsl:value-of select="@dbid"/>')</xsl:attribute>
							<font style="font-size:12px">&lt;</font>
						</a>&#xA0;
					</td>
				</xsl:if>
				<xsl:call-template name="pagenavig"/>
					<xsl:if test="query/@maxpage > 15">
						<xsl:variable name="beforecurview" select="substring-before(@id,'.')"/> 
                		<xsl:variable name="aftercurview" select="substring-after(@id,'.')"/> 
						<td>
							<select style="margin-left:5px">
								<xsl:attribute name="onChange">javascript:updateView(&quot;<xsl:value-of select="@type"/>&quot;,&quot;<xsl:value-of select="@id"/>&quot;,this.value,&quot;<xsl:value-of select="@dbid" />&quot;,&quot;<xsl:value-of select="@dbid" />&quot;)</xsl:attribute>
			 					<xsl:call-template name="combobox"/>
			 				</select>
			 			</td>
					</xsl:if>
				</tr>
			</table>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="pagenavig">
 		<xsl:param name="i" select="1"/>  <!-- ������� ���������� ������� ������������ � ����������  -->
 		<xsl:param name="n" select="15"/> <!-- ���������� ������� ������������ � ���������� -->
  		<xsl:param name="z" select="query/@maxpage -14"/>
  		<xsl:param name="f" select="15"/>
 		<xsl:param name="c" select="query/@currentpage"/> <!-- ������� �������� � ���� -->
 		<xsl:param name="startnum" select="query/@currentpage - 7"/> 
  		<xsl:param name="d" select="query/@maxpage - 14"/>	<!-- ���������� ��� ���������� ���������� ������ �������� � ����������  -->
  		<xsl:param name="currentpage" select="query/@currentpage"/>
  		<xsl:param name="maxpage" select="query/@maxpage"/>
  		<xsl:param name="nextpage" select="$currentpage + 1"/>
  		<xsl:param name="prevpage" select="$currentpage - 1"/>
  		<xsl:param name="curview" select="@id"/> 
  		<xsl:param name="direction" select="query/@direction"/> 
  		<xsl:param name="app" select="@dbid"/> 
  		<xsl:param name="dbid" select="@dbid"/> 
  		<xsl:param name="type" select="@type"/> 
		<xsl:choose>
			<xsl:when test="$maxpage>15">
				<xsl:choose>
					<xsl:when test="$maxpage - $currentpage &lt; 7">
						<xsl:if test="$i != $n+1">
							<xsl:if test="$z &lt; $maxpage + 1">
								<td>
									<a href="">
   										<xsl:attribute name="href">javascript:updateView('<xsl:value-of select="@type"/>','<xsl:value-of select="@id"/>',<xsl:value-of select="$z"/>,'<xsl:value-of select="@dbid"/>','<xsl:value-of select="@dbid"/>')</xsl:attribute>
   			 								<font style="font-size:12px">
    											<xsl:if test="$z=$currentpage">
    												<xsl:attribute name="style">font-weight:bold;font-size:18px</xsl:attribute>
    											</xsl:if>
    											<xsl:value-of select="$z"/>
    										</font>
   									</a>&#xA0;
								</td>
							</xsl:if>
      						<xsl:call-template name="pagenavig">
	        					<xsl:with-param name="i" select="$i + 1"/>
	        					<xsl:with-param name="n" select="$n"/>
	        					<xsl:with-param name="z" select="$z+1"/>
      						</xsl:call-template>
						</xsl:if>
						<xsl:if test="$currentpage != $maxpage">
							<xsl:if test="$i = $n+1">
		 						<td>
     								<a href="">
      									<xsl:attribute name="href">javascript:updateView('<xsl:value-of select="@type"/>','<xsl:value-of select="@id"/>',<xsl:value-of select="$nextpage"/>,'<xsl:value-of select="@dbid"/>','<xsl:value-of select="@dbid"/>')</xsl:attribute>
      									<font style="font-size:12px"> > </font>
      								</a>&#xA0;
      							</td>
       							<td>
       								<a href="">
       									<xsl:attribute name="href">javascript:updateView('<xsl:value-of select="@type"/>','<xsl:value-of select="@id"/>',<xsl:value-of select="$maxpage"/>,'<xsl:value-of select="@dbid"/>','<xsl:value-of select="@dbid"/>')</xsl:attribute>
       									<font style="font-size:12px"> >> </font>
       								</a> &#xA0;
						        </td>
							</xsl:if>
   						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="$currentpage &lt; 7">
								<xsl:if test="$i=1">
									<xsl:if test="$currentpage = 1">
										<td>
											&#xA0;&#xA0;&#xA0;&#xA0;	
										</td>
										<td>
											&#xA0;&#xA0;&#xA0;
										</td>
									</xsl:if>
								</xsl:if>
								<xsl:if test="$i != $n+1">
									<xsl:if test="$i &lt; $maxpage + 1">
										<td>
											<a href="">
   				 								<xsl:attribute name="href">javascript:updateView('<xsl:value-of select="@type"/>','<xsl:value-of select="@id"/>',<xsl:value-of select="$i"/>,'<xsl:value-of select="@dbid"/>','<xsl:value-of select="@dbid"/>')</xsl:attribute>
												<font style="font-size:12px">
    												<xsl:if test="$i=$currentpage">
    													<xsl:attribute name="style">font-weight:bold;font-size:18px</xsl:attribute>
    												</xsl:if>
    												<xsl:value-of select="$i"/>
    											</font>
						   					</a>&#xA0;
										</td>
									</xsl:if>
      								<xsl:call-template name="pagenavig">
	        							<xsl:with-param name="i" select="$i + 1"/>
	        							<xsl:with-param name="n" select="$n"/>
	        							<xsl:with-param name="c" select="$c+1"/>
      								</xsl:call-template>
      							</xsl:if>
							</xsl:when>
							<xsl:otherwise>
								<xsl:if test="$i != $n+1">
									<xsl:if test="$i &lt; $maxpage + 1">
										<xsl:if test="$startnum !=0">
											<td>
												<a href="">
   				 									<xsl:attribute name="href">javascript:updateView('<xsl:value-of select="@type"/>','<xsl:value-of select="@id"/>',<xsl:value-of select="$startnum"/>,'<xsl:value-of select="@dbid"/>','<xsl:value-of select="@dbid"/>')</xsl:attribute>
													<font style="font-size:12px">
    													<xsl:if test="$startnum=$currentpage">
    														<xsl:attribute name="style">font-weight:bold;font-size:18px</xsl:attribute>
    													</xsl:if>
    													<xsl:value-of select="$startnum"/>
    												</font>
						   						</a>&#xA0;
											</td>
										</xsl:if>
									</xsl:if>
									<xsl:if test="$startnum != 0">
      									<xsl:call-template name="pagenavig">
											<xsl:with-param name="i" select="$i + 1"/>
	        								<xsl:with-param name="n" select="$n"/>
	        								<xsl:with-param name="c" select="$c+1"/>
	        								<xsl:with-param name="startnum" select="$c - 6"/>
      									</xsl:call-template>
      								</xsl:if>
									<xsl:if test="$startnum = 0">
      									<xsl:call-template name="pagenavig">
										<xsl:with-param name="i" select="$i"/>
	        							<xsl:with-param name="n" select="$n"/>
	        							<xsl:with-param name="c" select="$c+1"/>
	        							<xsl:with-param name="startnum" select="$c - 6"/>
      								</xsl:call-template>
      							</xsl:if>
      						</xsl:if>
						</xsl:otherwise>
					</xsl:choose>
						<xsl:if test="$currentpage != $maxpage">
							<xsl:if test="$i = $n+1">
		 						<td>
      								<a href="">
     									<xsl:attribute name="href">javascript:updateView('<xsl:value-of select="@type"/>','<xsl:value-of select="@id"/>',<xsl:value-of select="$nextpage"/>,'<xsl:value-of select="@dbid"/>','<xsl:value-of select="@dbid"/>')</xsl:attribute>
     									<font style="font-size:12px"> ></font>
     								</a>&#xA0;
							    </td>
       							<td>
       								<a href="">
       									<xsl:attribute name="href">javascript:updateView('<xsl:value-of select="@type"/>','<xsl:value-of select="@id"/>',<xsl:value-of select="$maxpage"/>,'<xsl:value-of select="@dbid"/>','<xsl:value-of select="@dbid"/>')</xsl:attribute>
       									<font style="font-size:12px"> >></font>
       								</a> 
							    </td>
							</xsl:if>
  						</xsl:if>
						
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:if test="$i=1">
					<xsl:if test="$currentpage = 1">
						<td>
							&#xA0;&#xA0;&#xA0;&#xA0;	
						</td>
						<td>
							&#xA0;&#xA0;&#xA0;
						</td>
					</xsl:if>
				</xsl:if>
				<xsl:if test="$i != $n+1">
					<xsl:if test="$i &lt; $maxpage + 1">
						<td>
							<a href="">
   								<xsl:attribute name="href">javascript:updateView('<xsl:value-of select="@type"/>','<xsl:value-of select="@id"/>',<xsl:value-of select="$i"/>,'<xsl:value-of select="@dbid"/>','<xsl:value-of select="@dbid"/>')</xsl:attribute>
   			 					<font style="font-size:12px">
    								<xsl:if test="$i=$currentpage">
    									<xsl:attribute name="style">font-weight:bold;font-size:18px</xsl:attribute>
    								</xsl:if>
    								<xsl:value-of select="$i"/>
    							</font>
						    </a>&#xA0;
						</td>
					</xsl:if>
      				<xsl:call-template name="pagenavig">
	        			<xsl:with-param name="i" select="$i + 1"/>
	        			<xsl:with-param name="n" select="$n"/>
	        			<xsl:with-param name="c" select="$c+1"/>
      				</xsl:call-template>
				</xsl:if>
				<xsl:if test="$currentpage != $maxpage">
					<xsl:if test="$i = $n+1">
						<td>
      						<a href="">
      							<xsl:attribute name="href">javascript:updateView('<xsl:value-of select="@type"/>','<xsl:value-of select="@id"/>',<xsl:value-of select="$nextpage"/>,'<xsl:value-of select="@dbid"/>','<xsl:value-of select="@dbid"/>')</xsl:attribute>
      							<font style="font-size:12px"> > </font>
      						</a>&#xA0;
					    </td>
       					<td>
       						<a href="">
       							<xsl:attribute name="href">javascript:updateView('<xsl:value-of select="@type"/>','<xsl:value-of select="@id"/>',<xsl:value-of select="$maxpage"/>,'<xsl:value-of select="@dbid"/>','<xsl:value-of select="@dbid"/>')</xsl:attribute>
       							<font style="font-size:12px"> >> </font>
       						</a> &#xA0;
						</td>
					</xsl:if>
   				</xsl:if>
   			</xsl:otherwise>
  		 </xsl:choose>
 	 </xsl:template>
 	 
 	 <xsl:template name="combobox">
		<xsl:param name="i" select="1"/>
		<xsl:param name="k" select="query/@currentpage"/>
 		<xsl:param name="n" select="query/@maxpage + 1"/>
		<xsl:choose>
			<xsl:when test="$n > $i">
				<option>
 					<xsl:attribute name="value"><xsl:value-of select="$i"/></xsl:attribute>
 					<xsl:if test="$k=$i">
 						<xsl:attribute name="selected">true</xsl:attribute>
 					</xsl:if>
 					<xsl:value-of select="$i"/>
 				</option>
				<xsl:call-template name="combobox">
	        		<xsl:with-param name="i" select="$i + 1"/>
	        		<xsl:with-param name="n" select="$n"/>
	        		<xsl:with-param name="k" select="query/@currentpage"/>
	        	</xsl:call-template>
		 	</xsl:when>
 		</xsl:choose>
	 </xsl:template>
</xsl:stylesheet>