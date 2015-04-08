/* ��������� v1.0 author-John */

/* ������� ���� ������ */
var day_of_week = new Array('��','��','��','��','��','��','��');
var day_of_week_kz = new Array ('�','�','�','�','�','�','�');
var day_of_week_en = new Array ('M','T','W','T','F','S','S');

/* ������� �������� ������� */
var month_of_year = new Array('������','�������','����','������','���','����','����','������','��������','�������','������','�������');
var month_of_year_kz= new Array ('������','�����','������','���i�','�����','������','�i���','�����','��������','�����','������','���������');
var month_of_year_en= new Array ('January','February','March','April','May','June','Jule','August','Sentember','October','November','December');

/* ���������� ��� ����������� ������� ���� */
var Calendar = new Date();
var year = Calendar.getFullYear(); 
var currentmonth = Calendar.getMonth(); 
var currentyear = Calendar.getFullYear();
var today = Calendar.getDate(); 
weekday=Calendar.getDay();
weekday=weekday-1;

/* ���������� ���� � ������ � ������������ ���������� ���� � ������ */
var DAYS_OF_WEEK = 7; 
var DAYS_OF_MONTH = 31; 

var cal; 
var newmonth;
var newyear;
var mycalendar;

/* ���������� � ������� ������������� id ���� � ������� ����� ������������ ��������� ���� */
var field;

/* ���������� ��� ����������������� ��������� */
var x = 0;
var y = 0;
var show=false;

function createcalendar(element,input){
	
	/* ���������� ������� ������� ��������� */
	var left = element.offsetLeft;
	var top = element.offsetTop;
	for (var parent = element.offsetParent; parent; parent = parent.offsetParent)
	{
		left += parent.offsetLeft; //- parent.scrollLeft;
		top += parent.offsetTop + 6; //- parent.scrollTop+5;
	}
	y = left;
	x = top; 
    
	/* �������� div � ��������� ��� ����������� ��������� */
	mycalendar = document.createElement("DIV");
	document.body.appendChild(mycalendar);
	mycalendar.id="calendar";
	mycalendar.className="calendar";
	
	constuctcalendar(currentmonth,currentyear);
	field=input;
	
}

function constuctcalendar(nm,ny){
/* ���������� ���� ��������� */	
	if (nm==undefined){
	nm=Calendar.getMonth();
	}
	if(ny==undefined){
	ny=Calendar.getFullYear();
	}
	month=nm;
	year=ny;
	Calendar.setMonth(nm);
	Calendar.setDate(1);
	
/* ���������� ��������� */
	cal="";	
	cal += '<TABLE  width="200px" border=1 cellspacing=0 cellpadding=0  bordercolor="#BBBBBB"><TR><TD>';
	cal += '<TABLE id="caltable" height=180 border=0 cellspacing=0 cellpadding=2>' + '<TR height=30>';
	cal += '<TD style="cursor:pointer" onclick="prevmonth(month,year)" bgcolor="#EFEFEF"> '+'<<'+ '</TD>';
	cal += '<TD colspan="' + 5 + '" bgcolor="#EFEFEF"><CENTER><B>';
	cal += month_of_year[month] + ' ' + year + '</B>' + '</CENTER></TD>' +'<TD style="cursor:pointer" onclick="nextmonth(month,year)" bgcolor="#EFEFEF">>></TD>' + '</TR>';
	cal += '<TR>';
	for(i=0; i < DAYS_OF_WEEK; i++)
	{
		week_day =new Date(year,month,i).getDay();
		if(i==6)
			cal += '<TD bgcolor="#ffffff" style="color:red" width="30"><CENTER>' + '<B>' + day_of_week[i] + '</B>' + '</CENTER></TD>';
		else
			cal += '<TD bgcolor="#ffffff" width="30"><CENTER>' + day_of_week[i] + '</CENTER></TD>';
	}
	cal += '</CENTER></TD>' + '</TR>';
	cal += '<TR>';
	var firstdaymonth=new Date(year,month,1).getDay()-1;
	if(firstdaymonth==-1){
		firstdaymonth=6;
	}
	for(i=0; i < firstdaymonth; i++)
		cal += '<TD width="30" bgcolor="#ffffff"><CENTER>' + ' ' + '</CENTER></TD>';
	for(i=0; i < DAYS_OF_MONTH; i++)
	{
		if( Calendar.getDate() > i )
		{
			week_day =new Date(year,month,i).getDay();
			if(week_day == 0)
				cal += '<TR>';
			if(week_day != DAYS_OF_WEEK)
			{
				var day = Calendar.getDate();
				if( currentmonth==month && currentyear==year && today==day )
					cal += '<TD WIDTH="30" bgcolor="#ffffff"><TABLE cellspacing=0 border=1  bordercolor=CCCCCC><TR ><TD  onmouseover="javascript:eover(this)" onmouseout="javascript:eout(this)" style="cursor:pointer" width=20  onclick="datecol('+day+','+month+','+year+')"><B><CENTER>' + day + '</CENTER></TD></TR></TABLE></B>' + '</CENTER></TD>';
				else
					if(week_day==DAYS_OF_WEEK-1){
						cal += '<TD bgcolor="#ffffff" style="color:red; cursor:pointer" onmouseover="javascript:eover(this)" onmouseout="javascript:eout(this)" onclick="datecol('+day+','+month+','+year+')" width="30"><CENTER>' + day  + '</CENTER></TD>';
					}else{
						cal += '<TD  bgcolor="#ffffff" style="cursor:pointer" onmouseover="javascript:eover(this)" onmouseout="javascript:eout(this)" onclick="datecol('+day+','+month+','+year+')" width="30"><CENTER>' + day + '</CENTER></TD>';
					}
			}
			if(week_day == DAYS_OF_WEEK)
				cal += '</TR>';
		}
		Calendar.setDate(Calendar.getDate()+1);
	}
	cal += '</TD></TR></TABLE></TABLE>';
	
	
	$('calendar').innerHTML=cal;
	poscalendar();
	DocumentRegisterEvents();
	}

/* ���������������� ���������*/
function poscalendar(){
	idElem = document.getElementById("calendar");
	idElem.style.top=x;
	idElem.style.left=y;			
}

/* ������������ ������ �����*/
function prevmonth(month,year){
	var previosmonth=month;
	var newmonth=previosmonth-1;
	if (newmonth<0){
	newmonth=11;
	newyear=year-1;
	}
	constuctcalendar(newmonth,newyear);
	
}

/* ������������ ������ ������ */
function nextmonth(month){
	var previosmonth=month;
	var newmonth=previosmonth+1;
	if (newmonth>11){
	newmonth=0;
	newyear=year+1;
	
	}
	
	constuctcalendar(newmonth,newyear);
}

/* ����� ��������� ���� � ���� */
function datecol(d,m,y){
	dayres=d; 
    monthres=m+1;
    if (monthres<10){
    monthres='0'+monthres;
    }
    if (dayres<10){
    	dayres='0'+dayres;
        }
    yearres=y;
    $(field).value=dayres+'-'+monthres+'-'+yearres;
    hidecalendar();
    document.onclick =""
}

/* �������� ��������� */
function hidecalendar(){
	$('calendar').style.display="none";
	idElem = document.getElementById("calendar");
	idElem.parentNode.removeChild(idElem);
	show = false;
}


/* ������� ��� ������� onmouseover, onmouseout */
function eover(cell){
	cell.style.backgroundColor="#dde0ec";	
}

function eout(cell){
	cell.style.backgroundColor="#ffffff";	
}


/* �������� ��������� �� ����� ��� ������� ��������� */
function DocumentRegisterEvents(){
	document.onclick = function hideCalender_Trap(){
		
	if (show)
	  {
		hidecalendar();
		show = false;
		document.onclick="";
	  }else{
	  show = true;
	  }
	}
	}