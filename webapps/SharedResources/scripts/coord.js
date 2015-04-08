function savePrjAsDraft(){
    var tableObj = $('revtable');  
    var table = tableObj.firstChild;        
    blockNum = table.rows.length;    
    ct = ""; cr = "";
    for (i = 1; i <= blockNum; i++){	
	var bl =  new Block(i);
	ct += getRevType(i)+"#";	
	revObj = document.getElementsByName(bl.hiddenFieldName);	
	for (var ni = 0; ni < revObj.length; ni++){
	    cr += revObj[ni].value+",";
	}
	cr += "#";
    }
    $('coordtypes').value = ct;
    $('coordreviewers').value = cr;
    var formData = Form.serialize('frm');
    var myAjax = new Ajax.Request('Provider',{
	method: 'POST', 			
	postBody: formData,				
	onComplete: showResponse,
	onFailure: showError});			
}

function saveAndSend(){
    var tableObj = $('revtable');  
    var table = tableObj.firstChild;        
    blockNum = table.rows.length;    
    ct = ""; cr = "";
    for (i = 1; i <= blockNum; i++){	
	var bl =  new Block(i);
	ct += getRevType(i)+"#";	
	revObj = document.getElementsByName(bl.hiddenFieldName);	
	for (var ni = 0; ni < revObj.length; ni++){
	    cr += revObj[ni].value+",";
	}
	cr += "#";
    }
    $('coordtypes').value = ct;

    $('coordreviewers').value = cr;

    $('coordstatus').value = 'nocoordination';
    $('action').value = 'send';
    var formData = Form.serialize('frm');
    var myAjax = new Ajax.Request('Provider',{
	method: 'POST', 			
	postBody: formData,				
	onComplete: showResponse,
	onFailure: showError});			
}

function saveAndCoord(){
    var tableObj = $('revtable');  
    var table = tableObj.firstChild;        
    blockNum = table.rows.length;    
    ct = ""; cr = "";
    for (i = 1; i <= blockNum; i++){	
	var bl =  new Block(i);
	ct += getRevType(i)+"#";	
	revObj = document.getElementsByName(bl.hiddenFieldName);	
	for (var ni = 0; ni < revObj.length; ni++){
	    cr += revObj[ni].value+",";
	}
	cr += "#";
    }
    $('coordtypes').value = ct;

    $('coordreviewers').value = cr;

    $('coordstatus').value = 'coordinating';
    $('action').value = 'coordinate';
    // must reviewers validate
    var formData = Form.serialize('frm');
    var myAjax = new Ajax.Request('Provider',{
	method: 'POST', 			
	postBody: formData,				
	onComplete: showResponse,
	onFailure: showError});			
}

function Block(blockNum){  
    this.revTableName = 'blockrevtable'+blockNum;  
    this.revTypeRadioName = 'block_revtype_'+blockNum;
    this.hiddenFieldName = 'block_reviewers_'+blockNum;

}

function getRevType(blockNum){
    var form = $("rtfrm"+blockNum);
    var radioObj =  form.elements("radio"+blockNum);
    if (radioObj[1].checked){
	return radioObj[1].value;
    }else{
	return radioObj[0].value;
    }
}
var dataArray=new Array;

/* обработка действий пользователя при согласовании и подписании. Кнопки "Согласен" и "Не согласен" */
function decision(yesno, key, action){
	form="<form action='Provider' name='dynamicform' method='post' id='dynamicform' enctype='application/x-www-form-urlencoded'/>"
	$("body").append(form);
	actionTime= moment().format('DD.MM.YYYY HH:mm:ss');
	new FormData('actionDate',actionTime);
	new FormData('type', 'page'); 
    new FormData('id', action); 
    new FormData('key', key);
    if (yesno == "no"){
    	addComment(action)
    }else{
    	var dialog_title = "Оставить комментарий ответа?";
    	if ($.cookie("lang")=="KAZ")
    		dialog_title = "Жауаптың түсiнiктемесін қалдырасыз ма?";
        else if ($.cookie("lang")=="ENG")
        	dialog_title = "To leave the answer comment?";
        	
       dialogConfirmComment(dialog_title,action)
    }
}

function submit(){
	
    table=document.getElementById('extraCoordTable');
    tr=table.getElementsByTagName('tr');
    if (dataArray[6]){
	for (var i=0;i<tr.length;i++){
	    notesTable.innerText+=tr[i].innerText;
	}
	dataArray[6].value=$('notesTable').innerText;

    }
    if ($('comment').value!=''){
	dataArray[3].value=$('comment').value;
	submitDynamicForm ("Provider",dataArray);
    }
    else{
    var	msg='��������� �����������';
	alertBox(msg,'info');
	$('comment').focus();
    }
}