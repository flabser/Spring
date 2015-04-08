var service;
var xslt;
var curPage;
var view;

function loadOutline(){
	alert('load outline');
	var myAjax = new Ajax.Updater('outline', 'Provider', {method: 'get', parameters:'type=xmlfile&id=outline'});
}

function loadPage(page){
	var myAjax = new Ajax.Updater('view', page, {method: 'get', parameters:''});
}

function update(service,id){
	var pars = 'type='+ service+'&cacheid='+id;
	var myAjax = new Ajax.Request('Provider',{method:'GET',parameters:pars,onComplete:message(service),onFailure:showError,asynchronous: true});
}

function elemBackground(el,color){
	$(el).css("background","#"+color)
}

function message(service){
	service!="refresh_cache" ? alert('Cache cleared') : alert('Cache updated');
	window.location.reload();
}

function refresher(){
    setInterval("refreshAction()", 600000);	
}
					
function refreshAction(){
    updateView(service, id, curPage,app,dbid);
}

function updateView(service, id, curPage, app, dbid){
	if ( id == "users" || id == "groups" || id== "activity" ){
		var URL='Provider?type=' + service + '&id=' + id +'&element='+id+ '&page=' + curPage+'&app='+app + '&dbid=' + dbid;
	}else{
		var URL='Provider?type=' + service + '&id=' + id + '&app=' + app + '&dbid=' + dbid + '&page=' + curPage
	}
	$.ajax({
		url: URL,
		dataType:'HTML',
		async:'true',
		beforeSend: function(){
			loadingViewInOutline()
		},
		success: function(data) {
			$('#view').html(data);
			endLoadingViewInOutline()
		},
		error:function (xhr, ajaxOptions, thrownError){
			if (xhr.status == 400){
				$("body").children().wrapAll("<div id='doerrorcontent' style='display:none'/>")
				$("body").append("<div id='errordata'>"+xhr.responseText+"</div>")
				$("li[type='square'] > a").attr("href","javascript:backtocontent()")
			}
		}   
	});	
}

function loadingViewInOutline(){
	$('#blockWindow, #loadingpage').css("display","block");
	$("body").css("cursor","wait")
}

function endLoadingViewInOutline(){
	$('#loadingpage, #blockWindow').css("display","none");
	$("body").css("cursor","default")
}

function colapseOutlineCategory(num){
	$(".outlineEntry"+num).css("display","none");
	$("#acategory"+num).attr("href","javascript:expandOutlineCategory('"+num+"')");
	$("#imgcategory"+num).attr("src","/SharedResources/img/classic/plus.gif");
}

function expandOutlineCategory(num){
	$(".outlineEntry"+num).css("display","block");
	$("#acategory"+num).attr("href","javascript:colapseOutlineCategory('"+num+"')");
	$("#imgcategory"+num).attr("src","/SharedResources/img/classic/minus.gif");
}

function showError(req){ 	
    alert('�������� ������ ��� ������� ������ � �������');	
}