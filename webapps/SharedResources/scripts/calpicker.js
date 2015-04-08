var	fixedX = -1;
var	fixedY = -1;
var startAt = 1;
var	crossobj, monthSelected, yearSelected, dateSelected, omonthSelected, oyearSelected, odateSelected, monthConstructed, yearConstructed, ctlToPlaceValue, ctlNow, dateFormat, nStartingYear
var	bPageLoaded=false;
var	today = new	Date();
var	dateNow  = today.getDate();
var	monthNow = today.getMonth();
var	yearNow  = today.getYear();
var bShow = false;
var colorOver='#803fb9';
var colorOut='#dde0ec';
var field;
var field2;
/*** For language packs, month/day names should be changed here  ***/
var	monthName =	new Array("������", "�������", "����", "������", "���", "����", "����", "������", "��������", "�������","������", "�������")
var dayName = new Array("��","��","��","��","��","��","��");
function fieldfocus(){
	$(field).focus();	
}

function readId(id,id2){
	field=id;
	field2='';	
	if (id2 && id2=='ctrldate'){
		field2=id2;	
	}
}

function readIdKR(id){
	field=id;
	field2='';
}
	if (dom)
{
	
	document.write ("<div onclick='bShow=true' id='calendar' class='div-style'>\n");
	document.write ("<table width='140' class='table-style' cellSpacing='1px'>\n");
	document.write ("<tr class='title-background-style' >\n");
	document.write ("	<td width='100%'>\n");
	document.write ("	<table width='100%'>\n");
	document.write ("		<tr>\n");
	document.write ("			<td class='title-style'>\n");
	document.write ("				<span id='caption'></span>\n");
	document.write ("			</td>\n");
	document.write ("		</tr>\n");
	document.write ("		</table>\n");
	document.write ("	</td>\n");
	document.write ("</tr>\n");
	document.write ("<tr>\n");
	document.write ("	<td width='100%' class='body-style'>\n");
	document.write ("		<span id='content'></span>\n");
	document.write ("	</td>\n");
	document.write ("</tr>");
	document.write ("</table>");
	document.write ("</div>");
}

function hideCalendar() {
	crossobj.visibility="hidden";
	showElement( 'SELECT' );
	showElement( 'APPLET' );
}

function padZero(num) {
	return (num	< 10)? '0' + num : num ;
}

function constructDate(d,m,y){
	sTmp = dateFormat;
	sTmp = sTmp.replace	("dd","<e>");
	sTmp = sTmp.replace	("d","<d>");
	sTmp = sTmp.replace	("<e>",padZero(d));
	sTmp = sTmp.replace	("<d>",d);
	sTmp = sTmp.replace	("mmm","<o>");
	sTmp = sTmp.replace	("mm","<n>");
	sTmp = sTmp.replace	("m","<m>");
	sTmp = sTmp.replace	("<m>",m+1);
	sTmp = sTmp.replace	("<n>",padZero(m+1));
	sTmp = sTmp.replace	("<o>",monthName[m]);
	return sTmp.replace ("yyyy",y);
}

function closeCalendar() {
	var	sTmp;

	hideCalendar();
	var date=constructDate(dateSelected,monthSelected,yearSelected);
	var day=date.split(':')[0];
	var month=date.split(':')[1];
	var year=date.split(':')[2];
	date=day+'-'+month+'-'+year;
	$(field).value =date ;
	if (field2!=''){
		$(field2).value =date ;
	}
	fieldfocus()
}

function incMonth () {
	monthSelected++;
	if (monthSelected>11) {
		monthSelected=0;
		yearSelected++;
	}
	constructCalendar()
}

function decMonth () {
	monthSelected--;
	if (monthSelected<0) {
		monthSelected=11;
		yearSelected--;
	}
	constructCalendar();
}


/*** calendar ****/
function constructCalendar () {
	var	fullDate=$(field).value;
	var	sDay=fullDate.split('-')[0]-0;
	var	sMonth=fullDate.split('-')[1]-1;
	var	sYear=fullDate.split('-')[2];	
	var dateMessage;
	var	startDate =	new	Date (yearSelected,monthSelected,1);
	var	endDate = new Date (yearSelected,monthSelected+1,1);
	endDate = new Date (endDate	- (24*60*60*1000));
	numDaysInMonth = endDate.getDate();
	datePointer	= 0;
	dayPointer = startDate.getDay() - startAt;
	if (dayPointer < 0){
		dayPointer = 6;
	}

	sHTML = "<table id='table' width='100%' border='0' cellpadding='1' cellspacing='1' class='body-style'><tr>"
	for	(i=0; i<7; i++) {
		sHTML += "<td width='2500px' align='center'><B>"+ dayName[i]+"</B></td>"
	}
	sHTML +="</tr><tr>";
	for	( var i=1; i<=dayPointer;i++ ){
		sHTML += "<td>&nbsp;</td>";
	}

	for	( datePointer=1; datePointer<=numDaysInMonth; datePointer++ ){
		dayPointer++;
		sHTML += "<td style='cursor:pointer; align:center' onMouseOver='javascript:this.style.background=colorOver'  onMouseOut='javascript:this.style.background=colorOut' width='15px'  align='center'>"

		var sStyle="normal-day-style"; //regular day
		if (dayPointer==6){sStyle = "holiday-day-style";}
		if (dayPointer==7){sStyle = "holiday-day-style";}
		if (dayPointer==13){sStyle = "holiday-day-style";}
		if (dayPointer==14){sStyle = "holiday-day-style";}
		if (dayPointer==20){sStyle = "holiday-day-style";}
		if (dayPointer==21){sStyle = "holiday-day-style";}
		if (dayPointer==27){sStyle = "holiday-day-style";}
		if (dayPointer==28){sStyle = "holiday-day-style";}
		if (dayPointer==34){sStyle = "holiday-day-style";}
		if (dayPointer==35){sStyle = "holiday-day-style";}
		if (dayPointer==41){sStyle = "holiday-day-style";}
		if (dayPointer==42){sStyle = "holiday-day-style";}
		if ($('CtrlDate').value!=''){
			if ((sDay==datePointer) &&	(sMonth==monthSelected) && (sYear==yearSelected)){ 
				sStyle += " selected-day-style";
				}
			}
		if ((datePointer==dateNow)&&(monthSelected==monthNow)&&(yearSelected==yearNow)){ 
			sStyle = "current-day-style"; 
			}
		
		sHint = "";
		var regexp= /\"/g;
		sHint=sHint.replace(regexp,"&quot;");
		sHTML += "<a  class='"+sStyle+"' title=\"" + sHint + "\" href='javascript:dateSelected="+datePointer+";closeCalendar();'>" + datePointer + "</a>"
		if ((dayPointer+startAt) % 7 == startAt) {
			sHTML += "</tr><tr>";
		}
	}
	document.getElementById("content").innerHTML   = sHTML
	document.getElementById("spanMonth").innerHTML = monthName[monthSelected]
	document.getElementById("spanYear").innerHTML = yearSelected
	
}

function popUpCalendar(ctl,	ctl2, format) {
	var	leftpos=0;
	var	toppos=0;
	DocumentRegisterEvents();
	if (bPageLoaded){
		if ( crossobj.visibility ==	"hidden" ) {
			ctlToPlaceValue = ctl2;
			dateFormat=format;
			formatChar = " ";
			aFormat = dateFormat.split(formatChar);
			if (aFormat.length<3)
			{
				formatChar = "/";
				aFormat = dateFormat.split(formatChar);
				if (aFormat.length<3)
				{
					formatChar = ".";
					aFormat = dateFormat.split(formatChar);
					if (aFormat.length<3)
					{
						formatChar = "-";
						aFormat = dateFormat.split(formatChar);
						if (aFormat.length<3)
						{
							// invalid date format
							formatChar="";
						}
					}
				}
			}

			tokensChanged = 0;
			if ( formatChar != "" )
			{
				// use user's date
				aData = ctl2.value.split(formatChar);

				for	(i=0;i<3;i++)
				{
					if ((aFormat[i]=="d") || (aFormat[i]=="dd"))
					{
						dateSelected = parseInt(aData[i], 10);
						tokensChanged ++;
					}
					else if ((aFormat[i]=="m") || (aFormat[i]=="mm"))
					{
						monthSelected = parseInt(aData[i], 10) - 1;
						tokensChanged ++;
					}
					else if (aFormat[i]=="yyyy")
					{
						yearSelected = parseInt(aData[i], 10);
						tokensChanged ++;
					}
					else if (aFormat[i]=="mmm")
					{
						for	(j=0; j<12;	j++)
						{
							if (aData[i]==monthName[j])
							{
								monthSelected=j;
								tokensChanged ++;
							}
						}
					}
				}
			}

			if ((tokensChanged!=3)||isNaN(dateSelected)||isNaN(monthSelected)||isNaN(yearSelected))
			{
				dateSelected = dateNow;
				monthSelected = monthNow;
				yearSelected = yearNow;
			}

			odateSelected=dateSelected;
			omonthSelected=monthSelected;
			oyearSelected=yearSelected;

			aTag = ctl;
			do {
				aTag = aTag.offsetParent;
				leftpos += aTag.offsetLeft;
				toppos += aTag.offsetTop;
			} while(aTag.tagName!="BODY");

			crossobj.left = fixedX==-1 ? ctl.offsetLeft	+ leftpos :	fixedX;
			crossobj.top = fixedY==-1 ?	ctl.offsetTop + toppos + ctl.offsetHeight +	2 :	fixedY;
			constructCalendar (1, monthSelected, yearSelected);
			crossobj.visibility=(dom||ie)? "visible" : "show";
			
			hideElement( 'SELECT', document.getElementById("calendar") );
			hideElement( 'APPLET', document.getElementById("calendar") );			

			bShow = true;
		}
	}
	else
	{
		DateSelectorInit();
		popUpCalendar(ctl, ctl2, format);
	}
}

function DateSelectorInit()	{
	if (!ns4)
	{
		if (!ie) { yearNow += 1900;	}

		crossobj=(dom)?document.getElementById("calendar").style : ie? document.all.calendar : document.calendar;
		hideCalendar();

		monthConstructed=false;
		yearConstructed=false;

		sHTML1 = "<table width='100%' border='0' cellpadding='0' cellspacing='0'>\n";
		sHTML1 += "<tr>\n";
		sHTML1 += "	<td width='5'><span id='spanLeft' class='title-control-normal-style' onclick='javascript:decMonth()'>&lt;</span></td>\n";
		sHTML1 += "	<td width='100%' align='center'><span id='spanMonth' class='title-control-normal-style'></span>&nbsp;<span id='spanYear' class='title-control-normal-style'></span></td>\n";
		sHTML1 += "	<td width='5'><span id='spanRight' class='title-control-normal-style' onclick='incMonth()'>&gt;</span></td>\n";
		sHTML1 += "</tr>\n";
		sHTML1 += "</table>\n";

		document.getElementById("caption").innerHTML  = sHTML1;

		bPageLoaded=true;
	}
}

function DocumentRegisterEvents()
{
  document.onkeypress = function hideCalender_Trap1 () 
  {
	  if (event.keyCode == 27)
	  {
      hideCalendar();
	  }
  }

  document.onclick = function hideCalender_Trap2()
  {
	  if (!bShow)
	  {
      hideCalendar();
	  }
	  bShow = false;
  }
}
var	ie = document.all;
var	dom = document.getElementById;
var	ns4 = document.layers;

/* hides <select> and <applet> objects (for IE only) */
function hideElement( elmID, overDiv )
{
  	if (ie)
	{
        for( i = 0; i < document.all.tags( elmID ).length; i++ )
        {
			obj = document.all.tags( elmID )[i];
			if( !obj || !obj.offsetParent )
			{
				continue;
			}

			// Find the element's offsetTop and offsetLeft relative to the BODY tag.
			objLeft   = obj.offsetLeft;
			objTop    = obj.offsetTop;
			objParent = obj.offsetParent;

			while( objParent.tagName.toUpperCase() != "BODY" )
			{
				objLeft  += objParent.offsetLeft;
				objTop   += objParent.offsetTop;
				objParent = objParent.offsetParent;
			}

			objHeight = obj.offsetHeight;
			objWidth = obj.offsetWidth;

			if(( overDiv.offsetLeft + overDiv.offsetWidth ) <= objLeft );
			else if(( overDiv.offsetTop + overDiv.offsetHeight ) <= objTop );
			else if( overDiv.offsetTop >= ( objTop + objHeight ));
			else if( overDiv.offsetLeft >= ( objLeft + objWidth ));
			else
			{
				obj.style.visibility = "hidden";
			}
		}
	}
}

/*
 * unhides <select> and <applet> objects (for IE only)
 */
function showElement( elmID )
{
	if (ie)
	{
		for( i = 0; i < document.all.tags( elmID ).length; i++ )
		{
			obj = document.all.tags( elmID )[i];

			if( !obj || !obj.offsetParent )
			{
				continue;
			}

			obj.style.visibility = "";
		}
	}
}