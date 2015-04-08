var tableField;
var hiddenField;
var formName;
var entryCollections = new Array();
var field;
var isMultiValue;
var table;
var form;

function closePicklist(){ 
	$("#picklist").empty().remove();
	$('#blockWindow').remove();
	$('#picklist').enableSelection();
}

function search(){
	var value=$('search').value;
    var table = document.getElementById('table');
    var trList= table.getElementsByTagName('tr');
    var e = document.getElementsByName('chbox');
    var y=0;
    var x=0;
    var z=0;
    for (var i=0; i<trList.length;i++){ 
		tdList = trList[i].getElementsByTagName('td'); 
		trList[i].style.display="block";
		if (value.length>0){
			if(tdList[0].innerHTML!=""){
				y++;
			}
			if ((trList[i].innerText).substring(0,value.length).toLowerCase() != value.toLowerCase()){
				trList[i].style.display='none';
			}else{
				if(tdList[0].innerHTML!=""){
					x++;
					z=y;
				}
				if (tdList[0].innerHTML==""){
					trList[i].style.display='none';
				}
			}
		}
	    if (x==1){
	    	obj=e[z-1];
	    	if( e[z-1].checked==false){
	    		e[z-1].checked=true;
	    		obj.focus();
	    		$('search').focus();
	    	}
	    }
    }
}

function keyDown(el){
	if(event.keyCode==27){
		$("#"+el).css("display","none");
		$("#"+el).empty().remove();
		$("#blockWindow").remove();
	}
}

function pickListSingleOk(docid){ 
	text=$("#"+docid).attr("value");
	$("input[name="+field+"]").remove();
	if (field == "signer"){
		$("#coordBlockSign").remove();
		$("#frm").append("<input type='hidden' name='coordBlock'  id='coordBlockSign' value='new`tosign`0`"+docid+"'>")
	}
	if (field=='executor'){
		$("#intexectable tr:gt(0)").remove();
		$("input [name=executor]").remove();
		num=$("#intexectable tr").length;
		addExecutorField(num,docid);
		$('#intexectable').append("<tr>" +
			"<td style='text-align:center'>"+num+"</td>" +
				"<td>"+text+"<input  type='hidden' id='idContrExec"+num+"' value='"+docid+"'/></td>" +
				"<td id='controlOffDate"+num+"'></td>" +
				"<td id='idCorrControlOff"+num+"'></td>" +
				"<td id='switchControl"+num+"'><a href='javascript:controlOff("+num+")'><img  title='����� � ��������' src='/SharedResources/img/classic/exec_control.gif'/><a/></td>" +
			"</tr>");
		}else{
			$("#frm").append("<input type='hidden' name='"+field+"'  id='"+field+"' value='"+docid+"'>")
		}
	newTable="<table id="+ table +" width='380px' style='border:1px solid #ccc; '><tr><td>"+ text +"</td></tr></table>"
	$("#"+ table).replaceWith(newTable)
	pickListClose(); 
}

function pickListBtnOk(table,form,field){
	var k=0;
	var chBoxes = $('input[name=chbox]'); 
	for( var i = 0; i < chBoxes.length; i ++ ){
		if (chBoxes[i].checked){ 
			if (k==0){
				if (field=='executor'){
					$("#intexectable tr:gt(0)").remove();
				}
				newTable="<table id="+ table +" width='380px' ></table>"
				$("#"+ table).replaceWith(newTable)
				$("input[name="+field+"]").remove();
			}
			k=k+1;
			$("#"+ table).append("<tr><td style='border:1px solid #ccc'>"+chBoxes[i].value+"</td></tr>");
				if (field == "signer"){
					$("#coordBlockSign").remove();
					$("#frm").append("<input type='hidden' name='coordBlock'  id='coordBlockSign' value='new`tosign`0`"+chBoxes[i].id+"`367"+"'>")
				}
				if (field=='executor'){
					num=$("#intexectable tr").length;
					addExecutorField(num,chBoxes[i].id);
					$('#intexectable').append("<tr>" +
						"<td style='text-align:center'>"+num+"</td>" +
						"<td>"+chBoxes[i].value+"<input  type='hidden' id='idContrExec"+num+"' value='"+chBoxes[i].id+"'/></td>" +
						"<td id='controlOffDate"+num+"'></td>" +
						"<td id='idCorrControlOff"+num+"'></td>" +
						"<td id='switchControl"+num+"'><a href='javascript:controlOff("+num+")'><img  title='����� � ��������' src='/SharedResources/img/classic/exec_control.gif'/><a/></td>" +
					"</tr>");
				}else{
					$("#"+ form).append("<input type='hidden' name='"+field+"' id='"+field+"' value='"+chBoxes[i].id+"'>")
				}
		}
	}
	if (k>0){
		pickListClose();  
	}else{
		alert('�������� ��������');
	}
}

function pickListClose(){ 
	$("#picklist").empty().remove();
	$('#blockWindow').remove();
	$('#picklist').enableSelection();
}

function pickListSingleCoordOk(docid){ 
	text=$("#"+docid).attr("value");
	$("input[name=coorder]").remove();
	$("#frm").append("<input type='hidden' name='coorder' id='coorder' value='"+docid+"'/>")
	newTable="<table id='coordertbl' width='100%'><tr><td>"+ text +"</td></tr></table>"
	$("#coordertbl").replaceWith(newTable);
	closePicklistCoord();  
}

function closePicklistCoord(){
	$("#picklist").remove();
	$("#blockWindow").css('z-index','2');
}

/* ����� ���� ���������� ������  */
function centring(id,wh,ww){
	var w=document.body.clientWidth; 
	var h=document.body.clientHeight;
	var winH=wh; 
	var winW=ww;
	var scrollA=$("body").scrollTop(); 
	var scrollB=$("body").scrollLeft();
	htop=scrollA+((h/2)-(winH/2))
	hleft=scrollB+((w/2)-(winW/2))
	$('#'+id).css('top',htop) ;
	$('#'+id).css('left',hleft) ;
}

function entryOver(cell){
	cell.style.backgroundColor='#FFF1AF';
}

function entryOut(cell){
	cell.style.backgroundColor='#FFFFFF';
}

function picklistCoordinators(){
	$.ajax({
		type: "get",
		url: 'Provider?type=view&id=bossandemp',
		success:function (data){
			if(data.match("html")){
				window.location="Provider?type=static&id=start&autologin=0"
			}
			$("#contentpane").attr("innerText",'');
			$("#contentdiv").append(data);
			$('#searchCor').focus();
		}
	});
}

var elementCoord;

function addCoordinator(docid,el){
	//docid - userID  ���������� ��������������
	// el - ������ ������� � ��������� ���������������
	cwb=$(".coordinatorsWithBlock")
	
	signer=$("#signer").val(); 
	recipient=$("#recipient").val()
	if (signer == docid){
		text="��������� ���� ��������� �������� ������������� ��������� �������"
		infoDialog(text)
	}else{
		if (recipient == docid){
			text="��������� ���� ��������� �������� ����������� ��������� �������"
			infoDialog(text)
		}else{
			if ($("."+docid).val()!= null){
				text="������ ������������� ��� ������ ��� ������������"
				infoDialog(text)
			}else{
				text=$("#"+docid).val();
				$(el).replaceWith("");
				tr="<div style='display:block; width:100%; text-align:left; cursor:pointer' name='itemStruct' onClick='selectItem(this)' ondblclick='removeCoordinator(&quot;"+docid+"&quot;,this)' style='cursor:pointer'><input class='chbox' type='hidden' name='chbox'   id='"+ docid+"' value='"+ text +"'></input><font style='font-size:12px'>"+text+"</font></div>";
				$("#coorderToBlock").append(tr);
			}
		}
	}
}

/*�������� �� ������� ��������������� ������� ������� ���� */
function removeCoordinator(docid,el){
	text=$("#"+docid).attr("value");
	$(el).remove();
	tr="<div style='display:block; width:100%; text-align:left; cursor:pointer' name='itemStruct' onClick='selectItem(this)' ondblclick='addCoordinator(&quot;"+docid+"&quot;,this)' ><input class='chbox' type='hidden' name='chbox'   id='"+ docid+"' value='"+ text +"'></input><font style='font-size:12px'>"+ text+"</font></div>";
	$("#picklistCoorder").append(tr);
}

/*��������� � ������ ��������� �������������� � ������� ��������� ������� ���� */
var prevSelectItem=null

function selectItem (el){
	elementCoord=el;
	if (prevSelectItem != null){
		$(prevSelectItem).attr("class","")
	}
	$(el).attr("class","selectedItem");
	prevSelectItem=el
}

function plusCoordinator(){
	isWithBlock="false";
	userID=$(".selectedItem  input").attr("id");
	if(!userID){
		infoDialog("Вы не выбрали участника согласования для добавления");
	}
	if($("."+userID).val() != null){
		infoDialog(alreadychosen)
		isWithBlock="true"
	}
	if (isWithBlock=="false"){
		signer=$("#signer").val(); 
		recipient=$("#recipient").val()
		if (userID == signer){
			infoDialog(issignerofsz);
		}else{
			if (userID == recipient){
				infoDialog(isrecieverofsz);
			}else{
				$("#coorderToBlock").append(elementCoord);
				$(elementCoord).removeClass();
				$("#picklistCoorder .selectedItem").replaceWith("");
				elementCoord=null;
			} 
		}
	}
}

/* удаление корреспондента из таблицы нажатием кнопки "<--" */
function minusCoordinator(){
	if($("#coorderToBlock").children(".selectedItem").length !=0){
		$("#coorderToBlock").children(".selectedItem").remove();
		$("#picklistCoorder").append(elementCoord);
		$(elementCoord).removeClass();
		elementCoord=null
	}else{
		infoDialog("Вы не выбрали участника согласования для удаления");
	}
}

function closeDialog(el){
	$("#"+el).empty().remove();
	$("#blockWindow").remove();
}

function fastCloseDialog(){
	$("#picklist").css("display","none");
}

function dialogBoxStructure(query,isMultiValue, nfield, nform, ntable) {
	field=nfield;
	form=nform;
	table=ntable;
	el='picklist'
	divhtml ="<div class='picklist' id='picklist' onkeyUp='keyDown(el);'>";
	divhtml +="<div class='header'><font id='headertext' style='font-size:0.9em'></font>";
	divhtml +="<div class='closeButton'><img src='/SharedResources/img/classic/smallcancel.gif' onclick='pickListClose();' style='border:0;margin-left:3px'/>";
	divhtml +="</div></div><div id='divChangeView' style='margin-top:7%; margin-left:81%'><a id='btnChangeView' href='javascript:changeViewStructure(1,"+"&quot;"+isMultiValue+"&quot;,"+"&quot;"+table+"&quot;,"+"&quot;"+form+"&quot;,"+"&quot;"+field+"&quot;,"+"&quot;"+query+"&quot;"+")' style='font-size:11px'>�������� ���</a></div>";
	divhtml +="<div id='divSearch' display='block'></div><div id='contentpane' style='overflow:auto; margin-top:20px; border:1px solid  #a6c9e2; padding-top:10px; height:390px;' >Пожалуйста ждите...</div>";  
	divhtml += "<div  id = 'btnpane' style='margin-top:8%; text-align:right;'>";
	divhtml += "<a href='javascript:pickListBtnOk(&quot;"+table+"&quot;,"+"&quot;"+form+"&quot;,"+"&quot;"+field+"&quot;)'><font class='button'>Ok</font></a>&#xA0;&#xA0;";    
	divhtml += "<a href='javascript:pickListClose()'><font class='button'>Отмена</font></a>";    
	divhtml += "</div></div>";
	$("body").append(divhtml);
	$("#picklist").draggable({handle:"div.header"});
	centring('picklist',500,500);
	blockWindow = "<div class='ui-widget-overlay' id='blockWindow'/>"; 
	$("body").append(blockWindow);
	$('#blockWindow').css('width',$(document).width()).css('height',$(document).height()).css('display',"block"); 
	$('#picklist').css('display', "none");
	$("#headertext").attr('innerText',"����� ��������������");
	$("body").css("cursor","wait")
	$.ajax({
		   type: "get",
		   url: 'Provider?type=get_doc_list&id=structure&dbid=SmartDocWebKMG',
		   success:function (data){
			   if (isMultiValue=="false"){
					  while(data.match("checkbox")){
						   data=data.replace("checkbox", "radio");
					   }
				   }
			   $("#contentpane").attr("innerText",'');
			   $("#contentpane").append(data);
			   searchTbl ="<table id='searchTable' style='margin-top:5px; margin-left:-35%'>" +
			   		"<tr>" +
			   			"<td><font style='vertical-align:5px;'><b>�����:</b></font> <input type='text' id='searchCor' size='34' onKeyUp='findCorStructure()'/></td>" +
						"<td></td>" +
					"</tr>" +
				"</table>"
					$("#divSearch").append(searchTbl);
			   $('#btnChangeView').attr("href","javascript:changeViewStructure(2,"+"'"+isMultiValue+"'"+",'"+table+"'"+",'"+form+"'"+",'"+field+"'"+",'"+query+"'"+")");
			   $("body").css("cursor","default")
			   
			   if ($("#coordTableView tr").length > 1 &&  nfield == 'signer'){
					textConfirm="��� ��������� ���� '��� ����� ��������' ������������ ����� ������������ ����� �������"
					dialogConfirm (textConfirm, "picklist","trblockCoord")
					}else{
						$('#picklist').css('display', "inline-block");
					}
			   $('#searchCor').focus()
		   }
	});
}

/* �������  ������� � ���������� ��������� ������   */
jQuery.fn.extend({
    disableSelection : function() {
    	this.each(function() {
    		this.onselectstart = function() { return false; };
    		this.unselectable = "on";
    		jQuery(this).css('-moz-user-select', 'none');
    	});
    },
    enableSelection : function() {
    	this.each(function() {
    		this.onselectstart = function() {};
    		this.unselectable = "off";
    		jQuery(this).css('-moz-user-select', 'auto');
    	});
    }
});

function findCorStructure(){
	var value=$('#searchCor').val();
	var len=value.length
	if (len > 0){
		$("div[name=itemStruct]").css("display","none");
		$("#contentpane").find("div[name=itemStruct]").each(function(){
			if ($(this).text().substring(0,len).toLowerCase()== value.toLowerCase()){
				$(this).css("display","block")
			}
		});
	}else{
		$("div[name=itemStruct]").css("display","block");
	}
}

function changeViewStructure (viewType,isMultiValue,table,form,field,query){
	if (viewType==1){
	$.ajax({
		   type: "get",
		   url: 'Provider?type=view&id=bossandemppicklist',
		   success:function (data){
			   if (isMultiValue=="false"){
					  while(data.match("checkbox")){
						   data=data.replace("checkbox", "radio");
					   }
				   }
			   $("#contentpane").attr("innerText",'');
			   $("#contentpane").append(data);
			   
			  searchTbl ="<table id='searchTable' style='margin-top:5px; margin-left:-35%'>" +
			   		"<tr>" +
			   			"<td><font style='vertical-align:5px;'><b>�����:</b></font> <input type='text' id='searchCor' size='34' onKeyUp='findCorStructure()'/></td>" +
						"<td></td>" +
					"</tr>" +
				"</table>"
			$("#divSearch").append(searchTbl);
			  $('#btnChangeView').attr("href","javascript:changeViewStructure(2,"+"'"+isMultiValue+"'"+",'"+table+"'"+",'"+form+"'"+",'"+field+"'"+",'"+query+"'"+")");
			  $('#searchCor').focus()
		   }
	});
	}else{
		$('#btnChangeView').attr("href","javascript:changeViewStructure(1,"+"'"+isMultiValue+"'"+",'"+table+"'"+",'"+form+"'"+",'"+field+"'"+",'"+query+"'"+")");
		$.ajax({
			   type: "get",
			   url: 'Provider?type=view&id='+query,
			   success:function (data){
				  if(data.match("html")){
					 window.location="Provider?type=static&id=start"
				  }
				   if (isMultiValue=="false"){
					  while(data.match("checkbox")){
						   data=data.replace("checkbox", "radio");
					   }
				   }
				   $("#contentpane").attr("innerText",'');
				   $("#contentpane").append(data);
				   $("#searchTable").remove();
				   if (isMultiValue=="false"){
					   $("#contentpane").append("<input type='hidden' id='radio' value='true'/>");
				   }else{
					   $("#contentpane").append("<input type='hidden' id='radio' value='false'/>");
				   }
				   $(document).ready(function(){
					   $('#picklist').disableSelection();
				   });
				   $('#picklist').focus()
				   if ($("#coordTableView tr").length > 1 &&  nfield == 'signer'){
					   textConfirm="��� ��������� ���� '��� ����� ��������' ������������ ����� ������������ ����� �������"
						   dialogConfirm (textConfirm, "picklist","trblockCoord")
				   }else{
					   $('#blockWindow').css('display',"block")
					   $('#picklist').css('display', "inline-block");
				   }
			   }
		});
	}
}