/*Author john*/

function smartReq(value){	
var pars = 'type='+value+'&id=smart';   
var myAjax = new Ajax.Request('Provider',{
method: 'GET',	
parameters: pars,
onComplete: showResult,
onFailure: showError});
}

function showResult(req){ 	
    var ready = req.readyState;
    var status = req.status;
    var doc = null;

    if ( ready == 4 ) { 	  
	if (status == 200){   
	    $('resultField').innerHTML = req.responseText;
	    entryCollections = new Array;
	}else{         		
	    alert('Сервер вернул неизвестный статус: '+status);         		
	}
    }else{      		
	document.write(req.responseText);
    } 	
}
