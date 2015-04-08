var tableField;
var hiddenField;
var formName;
var entryCollections = new Array();
var field;
var isMultiValue;
var table;

//* поиск корреспондента //
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
					}
				if(tdList[0].innerHTML!=""){
					z=y;
					}
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



function giveChoosedDataToForm(entries){
	resetHiddenInput(hiddenField);    
	form = $(formName);

	if (isMultiValue){
		var tableObj = $(tableField);
		resetTable(tableObj);	
	}

	for (var i = 0; i < entries.length; i++) {		
		if (isMultiValue){
			var tr = document.createElement("tr");                                
			var td = document.createElement("td");    
			tableObj.appendChild(tr);
			tr.appendChild(td);    
			td.appendChild(document.createTextNode(entries[i].value));
		}else{
			$(field).value = entries[i].value;
		}
		var input = document.createElement("input");
		var attr = document.createAttribute("type");
		attr.value = "hidden";            
		input.setAttributeNode(attr);            
		attr = document.createAttribute("name");
		attr.value = hiddenField;    
		input.setAttributeNode(attr);
		attr = document.createAttribute("id");
		attr.value =hiddenField;            
		input.setAttributeNode(attr);
		attr = document.createAttribute("value");
		attr.value = entries[i].id;            
		input.setAttributeNode(attr);
		form.appendChild(input);
	}   
	entries.length=0;
}




function checkCheckBox(obj){    
	if (obj.checked){        
		$('chval'+obj.value).innerHTML = addToEntryCollection(obj.value) + 1;        
	}
}

function addToEntryCollection(id){
	lastElement = entryCollections.length++ ;  
	entry = new PickListData(id);    
	entryCollections[lastElement] = entry;
	return lastElement;    
}

function addToEntryCollectionClick(id){
	lastElement = entryCollections.length++ ;  
	entry = new PickListData(id);    
	entryCollections[lastElement] = entry;
	return lastElement; 
	
	
}




function PickListData(id){    
	if    (IE='\v'=='v'){
		this.value = $(id).innerText;  //IE 
	}else{
		this.value = $(id).textContent; //Firefox
	}
	this.id = $(id).readAttribute('id');  
	
}

function entryChoosed(id){    
	var entries= new Array;
	var entry = new PickListData(id);    
	entries[0] = entry;
	giveChoosedDataToForm(entries);
	pickListClose(); 
}     




/* picklist interface */

/*ОБРАБОТКА НАЖАТИЯ КЛАВИШ ESC И ENTER*/
function keyDown(){
	if (event.keyCode==9){
		$('search').focus();
	}
	if(event.keyCode==13){
		pickListOk();
	}else if(event.keyCode==27){
		pickListClose();
	}
}
function dialogBox(query,isMultiValue, field, form, table) {
	
	divhtml ="<div class='picklist' id='picklist'   onkeyUp='keyDown();'>";
	divhtml +="<div  class='header'><font id='headertext' style='font-size:0.8em'></font>";
	divhtml +="<div class='closeButton'  onclick='pickListClose(); '><img src='img/smallcancel.gif' style='border:0;'/>";
	divhtml +="</div></div><br/>";
	divhtml +="<div id='contentpane'   style='overflow:auto; margin-top:30px; padding-top:10px; height:370px;' >Загрузка данных...</div>";  
	divhtml += "<div  id = 'btnpane' style='margin-top:10%; text-align:right;'>";
	divhtml += "<a href='javascript:pickListBtnOk(&quot;"+table+"&quot;,"+"&quot;"+form+"&quot;,"+"&quot;"+field+"&quot;)'><font class='button'>ОК</font></a>&#xA0;&#xA0;";    
	divhtml += "<a href='javascript:pickListClose()'><font class='button'>Отмена</font></a>";    
	divhtml += "</div></div>";
	
	$("body").append(divhtml);
	$("#picklist").draggable({handle:"div.header"});
	centring('picklist',500,500);
	 
	blockWindow = "<div  class = 'blockWindow' id = 'blockWindow'></div>"; 
	$("body").append(blockWindow);

	$('#blockWindow').css('height',document.body.clientHeight); 
	$('#blockWindow').css('width',document.body.clientWidth);
	$('#blockWindow').css('display',"block")
	$('#picklist').css('display', "inline-block");
	$.ajax({
		   type: "get",
		   url: 'Provider?type=query&id='+query,
		   success:function (xml){
				$("#contentpane").attr("innerText",'');
				$("#contentpane").append("<table id='contenttable' cellspacing='0'></table>");
				htext=$(xml).find('header:first').text();
				$("#headertext").attr('innerText',htext)
					$(xml).find('entry').each(function(){
						text=$(this).find('name').text() ; 
						code=$(this).find('code').text() ;
						tr="<tr onmouseover='entryOver(this)' onmouseout='entryOut(this)' style='cursor:pointer'><td width='10%'><input class='chbox' type='radio' name='chbox' id='"+ code+"' value='"+ text +"'></input></td><td ondblclick='pickListSingleOk(&quot;"+ field + "&quot;,&quot;" + code + "&quot;,&quot;" + table+"&quot;)' width='90%'>"+text+"<td></tr>"
						$("#contenttable").append(tr);
						if (isMultiValue=='true'){
						$("input[type=radio]").replaceWith("<input class='chbox' type='checkbox' name='chbox' id='"+ code+"' value='"+ text +"'></input>");
						}
					});
	}
	});
}



function pickListSingleOk(field,code,table){ 
	text=$("#"+code).attr("value");
	$("#"+field).attr("value",code);
	newTable="<table id="+ table +" width='380px' style='border:1px solid #ccc'><tr><td>"+ text +"</td></tr></table>"
	$("#"+ table).replaceWith(newTable)
	pickListClose();  
}

function pickListBtnOk(table,form,field){ 
	var k=0;
	var chBoxes = $('input[name=chbox]'); 
	for( var i = 0; i < chBoxes.length; i ++ ){
		if (chBoxes[i].checked){ 
			if (k==0){
				newTable="<table id="+ table +" width='380px' ></table>"
				$("#"+ table).replaceWith(newTable)
				$("input[name="+field+"]").remove();
			}
			k=k+1;
			$("#"+ table).append("<tr><td style='border:1px solid #ccc'>"+chBoxes[i].value+"</td></tr>");
			$("#"+ form).append("<input type='hidden' name='"+field+"' value='"+chBoxes[i].id+"'>")
		}
		}
	if (k>0){
		pickListClose();  
	}else{
		alert('Выберите значение');
	}
	}
		


function pickListClose(){ 
	 $('#picklist').remove();
	 $('#blockWindow').remove();
}

/* вывод окна посередине экрана  */
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


/*функция для обеспечения rollover*/
function entryOver(cell){
	cell.style.backgroundColor='#FFF1AF';
	//cell.style.textDecoration = 'underline';    
}

/*функция для обеспечения rollover*/
function entryOut(cell){
	cell.style.backgroundColor='#FFFFFF';
	//cell.style.textDecoration = 'none';
}

function test(){
alert('testsdsd');
}