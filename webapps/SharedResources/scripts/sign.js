/* edsApp
 * �������� � ������ � �������� eds.jar
 */
var edsApp = {
	sign: function(id){
		/* ����� 'sign' - ��������� �� �� �������� ����� ��������� �� ��������� �����
		 * id - id �����
		 * ���������� �� ��� ������� ������������, ����� false
		 */
		try {
			var msg = "";
			var signedfields = this.getSignedFields(id);
			var sf = (signedfields.length>0) ? signedfields.split(",") : "";
			var field;
			var sgn;
			if(sf.length > 0){
				for(i=0; i<sf.length; i++){
					field = $("[name='"+sf[i]+"']");
					if( field.length>0 ) msg += field.val();
				}
				sgn = this.getSign(msg);
				if(sgn && (sgn!="null")){
					$("#signedfields").val(signedfields);
					$("#sign").val(sgn);
				} else {
					$("#signedfields").val("");
					$("#sign").val("");
				}
				return sgn;
			}
			return false;
		} catch(e) {
			window.status = e;
			return false;
		}
	},
	getSign: function(msg){
		/* ����� 'getSign' - ���������� ���������� �� ������ msg �������� �������*/
		try {
			/*alert(document.applets.edsApplet.getSign(msg).length)*/
			return document.applets.edsApplet.getSign(msg);
		} catch (e){ window.status = e; }
		return false;
	},
	verifySign: function(msg, sgn){
		/* ����� 'verifySign' - �������� ��
		 * msg - ������ ��������
		 * sgn - �������� �� ���� 'sign'
		 */
		return document.applets.edsApplet.verifySign(msg, sgn);
	},
	getSignedFields: function(id){
		// ���������� ������������ ����� ��� ������� ����������� ��������
		var signedfields = "";
		$.ajax({
			async: false,
			type: "get",
			url: "Provider?type=service&operation=fields_to_sign&id=" + id,
			success: function(data){
				$(data).find("signedfields").each(function(){
					signedfields = $(this).text();
				});
				window.status = signedfields;
			}
		});
		return signedfields;
	}
}