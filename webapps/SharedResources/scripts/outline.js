var currentView;
var direction;
var page
var n;

function checkAll(allChbox) {
	allChbox.checked ? $("input[name=chbox]").attr("checked","true") : $("input[name=chbox]").removeAttr("checked");
}

function refresher() {
	setInterval("refreshAction()", 600000);

}

function refreshAction(view) {

	if (n != undefined) {
		direction = n;
	}

	if (currentView == undefined) {
		currentView = 'notes.' + view; // ��� ����������� � NotesOutline
	}

	updateView(currentView, direction, page);
	// updateAllCount();
}

function updateCount(query, entry) {

	var url = 'Provider';
	var pars = 'type=count&id=' + query + '&' + Math.random();
	var myAjax = new Ajax.Updater(entry, url, {
		method : 'get',
		parameters : pars
	});
	// alert(pars)
}

function updateView(view, datedirection, pagenum) {
	page = pagenum;
	direction = datedirection;
	if (page == undefined) {
		page = 1;
	}

	showProgress()
	n = direction;
	var pars = 'type=view&id=' + view + '&direction=' + direction + '&page='
			+ page;
	// var myAjax = new Ajax.Updater('view', url, {method: 'get', asynchronous:
	// false, parameters: pars});*/
	var myAjax = new Ajax.Request('Provider', {
		method : 'GET',
		parameters : pars,
		onComplete : showResponse,
		onFailure : showError,
		asynchronous : true
	});

}

function showResponse(req) {
	hideProgress()
	var ready = req.readyState;
	var status = req.status;

	if (ready == 4) {
		// 	alert(status+'-- '+req.responseText);     	   
		if (status == 200) {
			$('view').innerHTML = req.responseText;
		} else {
			//document.write(req.responseText);
		}
	} else {

		//alert('�� ������� �������� ����� �� �������');
	}
}

function showError(req) {

	//alert('�������� ������ ��� ������� ������ � �������');	
	$('view').innerHTML = "<div style='margin-left:26%; margin-top:25%'><font style='font-size:25px; font-family:arial;color: #6790b3; text-align:center; '>�� ������� �������� ����� �� �������</font><br/><br/></div>";
	$('view').innerHTML += "<img id='noconnect' src='sdimg/noserver.gif' style='border:none;margin-left:42%; margin-top:10%'></img>";
	// $('disconnect').style.display="block"
}

function hideProgress() {
	$('blockWindow').style.cursor = "default";
	if ($("prgdiv")) {
		$("prgdiv").style.display = "none";
	}
	var delBlockWindow = $('blockWindow').parentNode;
	var parentBlockWindow = document.body;
	parentBlockWindow.removeChild(delBlockWindow);
}

function showProgress() {
	if ($("prgdiv")) {
		$("prgdiv").style.display = "block";
	}
	block = "<div  style='display:block;cursor:wait; position:absolute; z-index:10; color:red; background=#FFFFFF; top:0px; left:0px;' id='blockWindow'></div>";
	var MyBlockWindow = document.createElement("DIV");
	MyBlockWindow.innerHTML = block;
	document.body.appendChild(MyBlockWindow);
	$('blockWindow').style.height = document.body.clientHeight;
	$('blockWindow').style.width = document.body.clientWidth;
	$('blockWindow').style.filter = "progid:DXImageTransform.Microsoft.Alpha(Opacity=10, Style=0)";
}

function toglleSection(tableid, imgid) {
	if ($(tableid).style.display == "block") {
		$(tableid).style.display = "none";
		$(imgid).src = "sdimg/plus.gif";
	} else {
		$(tableid).style.display = "block";
		$(imgid).src = "sdimg/minus.gif";
	}
}

function subentry(id) {
	if ($("subentry" + id).style.display == "none") {
		$('subentry' + id).style.display = "block"
	} else {
		$('subentry' + id).style.display = "block"
	}
}

/*function Numeric(event,id,maxpage,view,direction) {
 if (event.keyCode == 13){
 gotoPage(maxpage,id,view,direction);
 }
 if ((event.keyCode < 48) || (event.keyCode > 57)) {
 event.returnValue = false;
 }
 }

 function gotoPage(maxpage,id,view,direction){
 newpage=$(id).value;
 if (newpage > maxpage || newpage<1){
 alert();
 }else{
 updateView(view,direction,newpage)
 }

 }*/

