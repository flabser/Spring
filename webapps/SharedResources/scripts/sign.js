/* edsApp
 * работает в связке с апплетом eds.jar
 */
var edsApp = {
	sign: function(id){
		/* метод 'sign' - формирует ЦП из значений полей документа по указанной форме
		 * id - id формы
		 * возвращает ЦП при удачном формировании, иначе false
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
		/* метод 'getSign' - возвращает полученную из строки msg цифровую подпись*/
		try {
			/*alert(document.applets.edsApplet.getSign(msg).length)*/
			return document.applets.edsApplet.getSign(msg);
		} catch (e){ window.status = e; }
		return false;
	},
	verifySign: function(msg, sgn){
		/* метод 'verifySign' - проверка ЦП
		 * msg - строка проверки
		 * sgn - значение из поля 'sign'
		 */
		return document.applets.edsApplet.verifySign(msg, sgn);
	},
	getSignedFields: function(id){
		// возвращает наименования полей для подписи разделенные запятыми
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