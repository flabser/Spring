
/*Отправляет форму созданную динамический с помощью AJAX*/

function submitDynamicForm(agent, dataArray){
    var result = false;
    createForm(agent, dataArray);
    $('dynaform').request({
	asynchronous:false,
	onComplete: function(req){	
    	
	if ( req.readyState == 4 ) {
		
	    data = req.responseXML;	
	    if (data != null){
	    	
		var status = req.status;
		
		//alert(status+'-- '+req.responseText);
		if (status == 200){	
		    var xmldoc = data.documentElement;  
		    var st = xmldoc.getAttribute('status');	
		    if (st =='ok'){		    	
			var redirect = xmldoc.getElementsByTagName('redirect')[0].text;
			var msg = xmldoc.getElementsByTagName('message'); 
			if (msg[0].text != ''){ 
				alertBox(msg[0].text,'info');
			}	
			if (redirect!=""){
			    window.location = redirect;
			}else{
			    result = true;
			    return;
			}
		    }
		}
	    }
	}
	result = false;
    }
    });
    return result;
}

function createForm(agent, vals){
    var formObj = document.createElement( "form" );

    attr = document.createAttribute("action");
    attr.value = agent;			
    formObj.setAttributeNode(attr);	

    attr = document.createAttribute("name");
    attr.value = "dynaform";			
    formObj.setAttributeNode(attr);	

    attr = document.createAttribute("id");
    attr.value = "dynaform";			
    formObj.setAttributeNode(attr);	

    attr = document.createAttribute("method");
    attr.value = "post";			
    formObj.setAttributeNode(attr);	

    attr = document.createAttribute("enctype");
    attr.value = "application/x-www-form-urlencoded";			
    formObj.setAttributeNode(attr);

    for (var i = 0; i < vals.length; i++) {
	var input = document.createElement("input");
	attr = document.createAttribute("type");
	attr.value = "hidden";			
	input.setAttributeNode(attr);			
	attr = document.createAttribute("name");
	attr.value = vals[i].field;			
	input.setAttributeNode(attr);
	attr = document.createAttribute("id");
	attr.value = vals[i].field;			
	input.setAttributeNode(attr);
	attr = document.createAttribute("value");
	attr.value = vals[i].value;			
	input.setAttributeNode(attr);

	formObj.appendChild(input);  
    }


    document.body.appendChild(formObj);
}

function FormData(field, value){
    this.field = field;
    this.value = value;
}
