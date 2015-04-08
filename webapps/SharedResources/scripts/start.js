/*Author Rustam!!!*/
function makeCookie(){
	username="";
	password="";
	expireAt= new Date;
	expireAt.setMonth(expireAt.getMonth() + 1);
	username=document.form.login.value
	password=document.form.pwd.value
	//document.cookie= "smart"+ "+" + username + "+" + password  + "+" + ";expires=" + expireAt.toGMTString();
	document.cookie="smart="+username+"$"+password+"; path=/; expires="+expireAt.toGMTString();
}
function key(event){
	if (event.keyCode==13){ 
		ourSubmit();
	}
	}

function getCookie(name) {
    var dc = document.cookie;
    var prefix = name + "=";
    var begin = dc.indexOf("; " + prefix);
    if (begin == -1) {
        begin = dc.indexOf(prefix);
        if (begin != 0) return null;
    } else {
        begin += 2;
    }
    var end = document.cookie.indexOf(";", begin);
    if (end == -1) {
        end = dc.length;
    }
    text=unescape(dc.substring(begin + prefix.length, end));
    document.form.login.value=text.split("$")[0];
    document.form.pwd.value=text.split("$")[1];

}

/*function makeCookie(){
	username="";
	password="";
	expireAt= new Date;
	expireAt.setMonth(expireAt.getMonth() + 1);
	username=document.form.login.value
	password=document.form.pwd.value
	document.cookie= username + "+" + password  + "+" + ";expires=" + expireAt.toGMTString();
}*/
function key(event){
	if (event.keyCode==13){ 
		ourSubmit();
	}
	}
/*function fillIn(){
		document.form.pwd.value= document.cookie.split("+")[1];
		document.form.login.value= document.cookie.split ("+") [0];
	}*/

function ourSubmit(){
	if(document.form.login.value==""){
		alert ('¬ведите им€ пользовател€');
		document.frm.login.focus();
		return;
	}else{
		if(document.form.pwd.value==""){
			alert ('¬ведите пароль');
			document.frm.pwd.focus();
			return;
		}else{
			if (document.getElementById("cbx").checked ){
	 		makeCookie();
		}
	}


		form.submit();	}
}