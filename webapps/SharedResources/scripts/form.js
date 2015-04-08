/** author Kairat* */
function CancelForm() {
	window.history.back();
}

function fieldOnFocus(field) {
	field.style.backgroundColor = '#FFFFE4';
}

function fieldOnBlur(field) {
	field.style.backgroundColor = '#FFFFFF';
}

function Numeric() {
	if ((event.keyCode < 48) || (event.keyCode > 57)) {
		event.returnValue = false;
	}
}

function deleteDoc() {
	var i; var result = false;
	var chBoxes = document.getElementsByName('chbox');
	for (i = 0; i < chBoxes.length; i++) {	
		if (chBoxes[i].checked){			
			docid=chBoxes[i].id;
			$.get('Provider?type=delmaindoc&key='+docid, {},function (){});
			/*	var url = 'Provider';
				var pars = 'type=delmaindoc&key='+docid;
				var myAjax = new Ajax.Request( url, {method: 'get', parameters: pars,onComplete : window.location.reload(),onFailure : showError});			
			*/
		}
	}
	window.location.reload()
}	

function SaveFormJquery(typeForm, formName) {
	var formData = $("form").serialize();
	$.ajax({
		   type: "POST",
		   url: 'Provider',
		   data: $("form").serialize(),
		   success:function (xml){
				$(xml).find('response').each(function(){
					var st=$(this).attr('status');
						if (st=="error"){
							var myDiv = document.createElement("DIV");
							divhtml ="<div id='dialog-message' title='�����������'  >";
							divhtml+="<p><span class='ui-icon ui-icon-circle-check' style='float:left; '></span>"+"<font style='color:#4297D7; top:30px; left: 20px ; font-size:13px'>�������� ������� ��������</font"+"</p>";
							divhtml += "</div>";
							myDiv.innerHTML = divhtml;
							document.body.appendChild(myDiv);
							$("#dialog").dialog("destroy");
							$("#dialog-message").dialog({
								modal: true,
								height:150,
								buttons: {
								Ok: function() {
								$(this).dialog('close');
								window.history.back();
							}
							}
							});
						}else{
							msgtext=$(xml).find('message').text();
							redirect=$(xml).find('redirect').text();
							
							var myDiv = document.createElement("DIV");
							divhtml ="<div id='dialog-message' title='�����������'  >";
							if (msgtext.length==0){
								divhtml+="<br/><p><span style='height:50px; text-align:center'>"+"<font style='font-size:13px; margin-left:16%'>�������� ������� ��������</font>"+"</p></span>";
							}else{
								divhtml+="<br/><p><span style='height:40px'><font style='font-size:13px'>"+msgtext+"</font></p></span>";
							}
							divhtml += "</div>";
							myDiv.innerHTML = divhtml;
							document.body.appendChild(myDiv);
							$("#dialog").dialog("destroy");
							$("#dialog-message").dialog({
								modal: true,
								height:150,
								buttons: {
								Ok: function() {
								$(this).dialog('close');
								if (redirect==''){
									window.history.back();
								}else{
								window.location = redirect;
								}
								}
							}
							});
						}	
				});
	}});
}

function SaveForm(typeForm, formName) {
	var formData = Form.serialize(formName);
	var myAjax = new Ajax.Request('Provider', {
		method : 'POST',
		postBody : formData,
		onComplete : showResponse,
		onFailure : showError
	});
}

function showResponse(data) {
	alert(data.TextStatus)
	var ready = req.readyState;
	var status = req.status;
	var doc = null;
	if (ready == 4) {
		data = req.responseXML;
		if (data != null) {
				    //alert(status+'-- '+req.responseText);     	   
			if (status == 200) {
				var xmldoc = data.documentElement;
				//var test=xmldoc.getElementsByTagName("request")[0];
				var st=xmldoc.getElementsByTagName("response")[0].getAttribute("status");
				//alert(st)
				//var st = xmldoc.getAttribute('status');
				//var msg = xmldoc.getElementsByTagName('message');
				if (st == 'ok') {
					/*if (msg[0].text != '') {
						msg = msg[0].text*/
						//redirect = xmldoc.getElementsByTagName('redirect')[0].text;

						/*notification(msg, 'info', redirect);
					} else {*/
						
						
						alert("�������� ��������")
						window.history.back();
						redirect = xmldoc.getElementsByTagName('redirect')[0].text;
						notification(msg, 'info', redirect);
					//}
					//window.location = xmldoc.getElementsByTagName('redirect')[0].text;
				} else if (st == 'validationerror') {
					msgText = '';
					for (i = 0; i < msg.length; i++) {
						msgText = msgText + msg[i].firstChild.data + "\n";
					}
					alertBox(msgText, 'warning');
				} else if (st == 'error') {
					alertBox(msg[0].firstChild.data, 'error');
				} else {
					alert('������ ������ ����������� ������: ' + st);
				}
			} else {
				msg = '������ ������ : ' + status;
				alertBox(msg, 'warning');
				document.write(req.responseText);
			}
		} else {
			msg = '�� ������� �������� ����� � ���������� �������� ����������';
			alertBox(msg, 'warning');
		}
	}

}

function showError(req) {
	msg = '�������� ������ ��� ����������!';
	alertBox(msg, 'warning');
}

/*set of upload function*/

function submitFile(form, tableID, fieldName) {
	if ($('#'+fieldName).attr('value') == '') {
		alert('������� ��� ����� ��� ��������');
	} else {
		form = $('#'+form);
		var frame = createIFrame();
		frame.onSendComplete = function() {
			uploadComplete(tableID, getIFrameXML(frame));
		};
		form.attr('target', frame.id);
		form.submit();
		$("#upload")[0].reset();
		//form.reset();
	}
}

var cnt = 0;

function uploadComplete(tableID, doc) {
	if (!doc)
		return;
	// if (typeof(element)=="string") element=document.getElementById(element);

	var xmldoc = doc.documentElement;
	var st = xmldoc.getAttribute('status');
	var msg = xmldoc.getElementsByTagName('BODY');
       d=$("BODY", doc).text();
	if (st = 'ok') {
		tableid='#'+tableID;
		var table = $(tableid);
		sesid=$(doc).find("message").attr('formsesid');
		var range = 200 - 1 + 1;
		fieldid=Math.floor(Math.random()*range) + 1;;
		 $(table).append("<tr id='"+ fieldid + "'><td ><div style='display:inline;margin-left:97px; border:1px solid gray; width:99%'>"+ d +"'</div></td><td><a href='Provider?type=getattach&formsesid="+ sesid+"&field=rtfcontent&file="+ d +"'>�������</a>&#xA0;&#xA0;<a href='javascript:deleterow("+sesid +",&quot;"+ d +"&quot;,"+ fieldid +")'>�������</a></td></tr>");
		 
		
		
		//html=table.attr('innerHTML');
		//html+='<tr>123</tr>'
		//var newrow = table.insertRow(table.row);
		//newrow.insertCell().innerHTML = msg[0].firstChild.data;
	} else {
		alert('��������� ������ �� ������� ��� �������� �����');
	}

	//element.innerHTML=msg[0].text;
}
function deleterow(sesid,filename, fieldid){
	$("#"+fieldid).remove()
	$("#frm").append("<input type='hidden' name='deletertfcontentsesid' value='"+ sesid +"'></input>")
	$("#frm").append("<input type='hidden' name='deletertfcontentname' value='"+ filename +"'></input>")
	$("#frm").append("<input type='hidden' name='deletertfcontentfield' value='rtfcontent'></input>")
}
function createIFrame() {
	var id = 'f' + Math.floor(Math.random() * 99999);
	var div = document.createElement('div');
	var divHTML = '<iframe style="display:none" src="about:blank" id="' + id
			+ '" name="' + id + '" onload="sendComplete(\'' + id
			+ '\')"></iframe>';
	div.innerHTML = divHTML;
	document.body.appendChild(div);
	return document.getElementById(id);
}

function sendComplete(id) {
	var iframe = document.getElementById(id);
	if (iframe.onSendComplete && typeof (iframe.onSendComplete) == 'function')
		iframe.onSendComplete();
}

function getIFrameXML(iframe) {
	var doc = iframe.contentDocument;
	if (!doc && iframe.contentWindow)
		doc = iframe.contentWindow.document;
	if (!doc)
		doc = window.frames[iframe.id].document;
	if (!doc)
		return null;
	if (doc.location == "about:blank")
		return null;
	if (doc.XMLDocument)
		doc = doc.XMLDocument;
	return doc;
}
function iconposition() {
	var table = document.getElementById('table');
	var tr = table.getElementsByTagName('tr');
	var newElem = document.createElement('table');
	newElem.style.top = "5px";
	newElem.style.marginLeft = "120px";
	newElem.style.width = "140px";
	newElem.style.position = "absolute";
	newElem.align = "right";
	document.body.appendChild(newElem);
	for ( var i = 0; i < tr.length; i++) {
		if (tr.length > 5) {
			var newRow = newElem.insertRow(0);
			var newCell = newRow.insertCell(0);
			newCell.style.textAlign = "center";
			newCell.style.height = "135px";
			newCell.innerHTML = tr[i].innerHTML;
			table.deleteRow(i);
		}
	}

}

function transparency() {
	var arVersion = navigator.appVersion.split("MSIE")
	var version = parseFloat(arVersion[1])

	if ((version >= 5.5) && (document.body.filters)) {
		for ( var i = 0; i < document.images.length; i++) {
			var img = document.images[i]
			var imgName = img.src.toUpperCase()
			if (imgName.substring(imgName.length - 3, imgName.length) == "PNG") {
				var imgID = (img.id) ? "id='" + img.id + "' " : ""
				var imgClass = (img.className) ? "class='" + img.className
						+ "' " : ""
				var imgTitle = (img.title) ? "title='" + img.title + "' "
						: "title='" + img.alt + "' "
				var imgStyle = "display:inline-block;" + img.style.cssText
				if (img.align == "left")
					imgStyle = "float:left;" + imgStyle
				if (img.align == "right")
					imgStyle = "float:right;" + imgStyle
				if (img.parentElement.href)
					imgStyle = "cursor:hand;" + imgStyle
				var strNewHTML = "<span "
						+ imgID
						+ imgClass
						+ imgTitle
						+ " style=\""
						+ "width:"
						+ img.width
						+ "px; height:"
						+ img.height
						+ "px;"
						+ imgStyle
						+ ";"
						+ "filter:progid:DXImageTransform.Microsoft.AlphaImageLoader"
						+ "(src=\'" + img.src
						+ "\', sizingMethod='scale');\"></span>"
				img.outerHTML = strNewHTML
				i = i - 1
			}
		}
	}
}

function alertBox(msg, status, redirect) {
	if ($('box')) {
		return false;
	}
	var message = msg;
	divhtml = "<div class='box' id='box'>";
	divhtml += "<div  class='headerBox' onmousedown='move(box)' ><font style='font-size:0.8em'>SmartDoc</font>";
	divhtml += "<div class='closeButton'  onclick='closeAlertBox(); '><img src='sdimg/smallcancel.gif' style='border:0;'/>";
	divhtml += "</div></div>";
	divhtml += "<div id='contentpane'  style='width:400px; height:70px; ' ></div>";
	if (redirect) {
		divhtml += "<input type='button' id='button' onClick='closeAlertBox(redirect)' value='ok' style='width:75px; margin-top:15px;margin-left:45px'></input>"
	} else {
		divhtml += "<input type='button' id='button' onClick='closeAlertBox()' value='ok' style='width:75px; margin-top:15px;margin-left:45px'></input>"
	}
	divhtml += "</div>";
	var block = "<div  class='blockWindow' id='blockWindow'></div>";
	var myDiv = document.createElement("DIV");
	var MyBlockWindow = document.createElement("DIV");
	myDiv.innerHTML = divhtml;
	MyBlockWindow.innerHTML = block;
	document.body.appendChild(myDiv);
	document.body.appendChild(MyBlockWindow);
	$('blockWindow').style.height = document.body.clientHeight;
	$('blockWindow').style.width = document.body.clientWidth;
	$('blockWindow').style.display = "block";
	var w = document.body.clientWidth;
	var h = document.body.clientHeight;
	var winH = 80;
	var winW = 400;
	var scrollA = document.body.scrollTop;
	var scrollB = document.body.scrollLeft;
	$('box').style.top = scrollA + ((h / 2) - (winH / 2));
	$('box').style.left = scrollB + ((w / 2) - (winW / 2));
	$('box').style.display = 'block';
	document.body.className = "noselect";

	if (status == 'warning') {
		$('contentpane').innerHTML = "<img onLoad='transparency ()' src='sdimg/messagebox_warning.png' style='border:none;margin-right:350px; margin-top:20px;'/>";
	} else {
		$('contentpane').innerHTML = "<img onLoad='transparency ()' src='sdimg/messagebox_info.png' style='border:none;margin-right:350px; margin-top:20px;'/>";
	}
	if (status == 'error') {
		$('contentpane').innerHTML = "<img onLoad='transparency ()' src='sdimg/messagebox_critical.png' style='border:none;margin-right:350px; margin-top:20px;'/>";
	}
	$('contentpane').innerHTML += "<div style='margin-top:-45px; margin-left:50px; width:350px; font:13px'><font1 >"
			+ message + "</font1></div>";

}

function closeAlertBox(redirect) {
	var delAlertBox = $('box').parentNode;
	var parentAlertBox = document.body;
	parentAlertBox.removeChild(delAlertBox);
	document.body.className = "";
	var delBlockWindow = $('blockWindow').parentNode;
	var parentBlockWindow = document.body;
	parentBlockWindow.removeChild(delBlockWindow);
	if (redirect) {
		window.location = redirect;
	}
}

function validationDate(id) {
	input = $(id).value;
	if (input.length != 10) {
		if (input.length != 2 && input.length != 5) {
			if ((event.keyCode != 8) && (event.keyCode != 46)) {
				if ((event.keyCode < 48) || (event.keyCode > 57)) {
					if ((event.keyCode < 96) || (event.keyCode > 105)) {
						event.returnValue = false;
						msg = "������ ����� ������." + "\n";
						msg += "������ ����: ��-��-����";
						alertBox(msg, 'warning');

					}
				}
			}
		} else {

			if ((event.keyCode != 189) && (event.keyCode != 109)) {
				if ((event.keyCode != 8) && (event.keyCode != 46)) {
					event.returnValue = false;
					msg = "������ ����� ������." + "\n";
					msg += "������ ����: ��-��-����";
					alertBox(msg, 'warning');

				}
			}

		}

	}
}

function endValidationDate(id,daterez) {
	input = $(id).value;
	if (input.length == 10) {
		dayRez=daterez.substring(0, 2);
		monthRez=daterez.substring(3,5);
		yearRez=daterez.substring (6,10);
		day = input.substring(0, 2);
		month = input.substring(3, 5);
		year = input.substring(6, 10);
		if ( month<monthRez && year==yearRez || year<yearRez || day<dayRez && month==monthRez && year==yearRez){
			msg="���� ����� ���������� �� ����� ���� ������ ���� �������� ���������"
			alertBox(msg, 'warning');
			$(id).value='';
		}
		
		if (day > 31 && month == 1 || day > 31 && month == 3 || day > 31
				&& month == 5 || day > 31 && month == 7 || day > 31
				&& month == 8 || day > 31 && month == 10 || day > 31
				&& month == 12) {
			msg = "������ ����� ������." + "\n";
			msg += "���� ������ ������ ���� ������ ��� ����� 31";
			alertBox(msg, 'warning');
			$(id).value='';

		}
		if (day > 30 && month == 4 || day > 31 && month == 6 || day > 31
				&& month == 9 || day > 31 && month == 11) {
			msg = "������ ����� ������." + "\n";
			msg += "���� ������ ������ ���� ������ ��� ����� 30";
			alertBox(msg, 'warning');
			$(id).value='';
		}
		if (day > 28 && month == 2) {
			if (year == 2004 || year == 2008 || year == 2012 || year == 2016
					|| year == 2020 || year == 2024 || year == 2028
					|| year == 2032 || year == 2036 || year == 2040
					|| year == 2044 || year == 2048 || year == 2052) {
				if (day > 29) {
					msg = "������ ����� ������." + "\n";
					msg += "���� ������ ������ ���� ������ ��� ����� 29";
					alertBox(msg, 'warning');
					$(id).value='';
				}
			} else {
				msg = "������ ����� ������." + "\n";
				msg += "���� ������ ������ ���� ������ ��� ����� 28";
				alertBox(msg, 'warning')
				$(id).value='';
			}
		}
		if (month > 12) {
			msg = "������ ����� ������." + "\n";
			msg += "����� ������ ������ ���� ������ ��� ����� 12";
			alertBox(msg, 'warning');
			$(id).value='';
		}
		if (year < 2000 || year > 2100) {
			msg = "������ ����� ������." + "\n";
			msg += "���  ������ ���� ������ 2000 � ������ 2100";
			alertBox(msg, 'warning');
			$(id).value='';
		}

	}
}

function notification(message, type , redirect) {
	
	var divhtml ="<div style='top:695px; left:-300px;width:300px;height:150px;position: absolute; border: 1px solid gray; background: white; padding: 20px 20px 20px 20px; text-align: center;display:block;z-index:4;' id='notification'>";
    divhtml +="<div id='body' style='border-bottom:1px solid gray;width:298px;height:21px;position:absolute;top:0;left:0;cursor: move;background-color:#dde0ec;<font style='font-size:0.8em; font-family:verdana'>SmartDoc </font> ";
    divhtml +="</div><div id='img' style='margin-top:50px; margin-left:-175px; '><img onLoad='transparency ()'; src='sdimg/messagebox_warning.png' style='border:none;margin-right:40px; '></img></div><div style='margin-top:-30px;margin-left:45px '><font id='text' style='margin-top:-10px; font-size:0.8em; font-family:verdana;'>�������� ��������</font></div></div>";  
    var myDiv = document.createElement("DIV");
    myDiv.innerHTML=divhtml;
    document.body.appendChild(myDiv);
    if (type == 'warning') {
		$('img').innerHTML = "<img onLoad='transparency ()' src='sdimg/messagebox_warning.png' style='border:none;margin-right:30px; '/>";
	} else {
		$('img').innerHTML = "<img onLoad='transparency ()' src='sdimg/messagebox_info.png' style='border:none;margin-right:30px; '/>";
	}
	if (type == 'error') {
		$('img').innerHTML = "<img onLoad='transparency ()' src='sdimg/messagebox_critical.png' style='border:none;margin-right:30px; '/>";
	}
		$('text').innerHTML = "<font style='font-size:0.8em; font-family:verdana'>"+ message +"</font>"
			
		showHidenotification(redirect);
    
}

function showHidenotification(redirect){
	
$('notification').morph("left:10px",{duration:  4}); 
window.setTimeout(function(){$('notification').morph('left:-300px',{duration:  3});},5000); 
window.setTimeout(function(){$('notification').morph( "opacity:0%",{duration:  3.5});},5000); 
if (redirect){
window.setTimeout(function(){window.location = redirect;},8000);
}	
}


function Numeric(e) {
	var event = e || window.event; 
	
	//ie
	if ((e.keyCode < 48) || (e.keyCode > 57)) {
		if ((e.keyCode < 96) || (e.keyCode > 105)){
			if ((e.keyCode == 110) || (e.keyCode == 190)|| (e.keyCode == 8)||  (e.keyCode == 46)){
			}else
			{
				e.returnValue = false;
				}
			}
		}
	
	//firefox
	if ((e.which < 48) || (e.which > 57)) {
		if ((e.keyCode < 96) || (e.keyCode > 105 )){
			if ((e.keyCode != 110) || (e.keyCode != 190 )){
				if ((e.keyCode == 110) || (e.keyCode == 190)|| (e.keyCode == 8)||  (e.keyCode == 46)){
				}else{
						event.preventDefault();
						return false;
						}
				}
			}
		}
	}