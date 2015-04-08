/* Календарь v1.0 author-John */

/* массивы дней недели */
var day_of_week = new Array('Пн','Вт','Ср','Чт','Пт','Сб','Вс');
var day_of_week_kz = new Array ('Д','С','С','Б','Ж','С','Ж');
var day_of_week_en = new Array ('M','T','W','T','F','S','S');

/* массивы названия месяцев */
var month_of_year = new Array('Январь','Февраль','Март','Апрель','Май','Июнь','Июль','Август','Сентябрь','Октябрь','Ноябрь','Декабрь');
var month_of_year_kz= new Array ('Кантар','Акпан','Наурыз','Сауiр','Мамыр','Маусым','Шiлде','Тамыз','Кыркуйек','Казан','Караша','Желтоксан');
var month_of_year_en= new Array ('January','February','March','April','May','June','Jule','August','Sentember','October','November','December');

/* переменные для определения текущей даты */
var Calendar = new Date();
var year = Calendar.getFullYear(); 
var currentmonth = Calendar.getMonth(); 
var currentyear = Calendar.getFullYear();
var today = Calendar.getDate(); 
weekday=Calendar.getDay();
weekday=weekday-1;

/* количество дней в неделе и максимальное количество дней в месяце */
var DAYS_OF_WEEK = 7; 
var DAYS_OF_MONTH = 31; 

var cal; 
var newmonth;
var newyear;
var mycalendar;

/* переменная в которую записываеться id поля в которое будет записываться выбранная дата */
var field;

/* переменные для позиционнирования календаря */
var x = 0;
var y = 0;
var show=false;

function createcalendar(element,input){
	
	/* вычисление будущей позиции календаря */
	var left = element.offsetLeft;
	var top = element.offsetTop;
	for (var parent = element.offsetParent; parent; parent = parent.offsetParent)
	{
		left += parent.offsetLeft; //- parent.scrollLeft;
		top += parent.offsetTop + 6; //- parent.scrollTop+5;
	}
	y = left;
	x = top; 
    
	/* создание div в документе для отображения календаря */
	mycalendar = document.createElement("DIV");
	document.body.appendChild(mycalendar);
	mycalendar.id="calendar";
	mycalendar.className="calendar";
	
	constuctcalendar(currentmonth,currentyear);
	field=input;
	
}

function constuctcalendar(nm,ny){
/* построение даты календаря */	
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
	
/* построение календаря */
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

/* позиционирование календаря*/
function poscalendar(){
	idElem = document.getElementById("calendar");
	idElem.style.top=x;
	idElem.style.left=y;			
}

/* переключение месяца назад*/
function prevmonth(month,year){
	var previosmonth=month;
	var newmonth=previosmonth-1;
	if (newmonth<0){
	newmonth=11;
	newyear=year-1;
	}
	constuctcalendar(newmonth,newyear);
	
}

/* переключение месяца вперед */
function nextmonth(month){
	var previosmonth=month;
	var newmonth=previosmonth+1;
	if (newmonth>11){
	newmonth=0;
	newyear=year+1;
	
	}
	
	constuctcalendar(newmonth,newyear);
}

/* вывод выбранной даты в поле */
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

/* закрытие календаря */
function hidecalendar(){
	$('calendar').style.display="none";
	idElem = document.getElementById("calendar");
	idElem.parentNode.removeChild(idElem);
	show = false;
}


/* функции для событий onmouseover, onmouseout */
function eover(cell){
	cell.style.backgroundColor="#dde0ec";	
}

function eout(cell){
	cell.style.backgroundColor="#ffffff";	
}


/* закрытие календаря по клику вне области календаря */
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