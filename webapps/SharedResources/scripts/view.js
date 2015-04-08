/*SmartDoc Copyright F-labs's (c) Burlay Eugene */
function search() {
	val="1";
	var formName='myfrm';
	var formData = Form.serialize('frm');
	var myAjax = new Ajax.Request('Provider?type=view&id=ftquery&key=', {
		method : 'POST',
		postBody : formData,
		onComplete : showResult,
		onFailure : showError
	});
}
	
function showResult(req) {
	var ready = req.readyState;
	var status = req.status;
	if (ready == 4) {
		document.write(req.responseText);
	}else{
		alert('�� ������� �������� ����� �� �������');
	}
}