Date.prototype.format = function(format) {
       var date = {
              "M+": this.getMonth() + 1,
              "d+": this.getDate(),
              "h+": this.getHours(),
              "m+": this.getMinutes(),
              "s+": this.getSeconds(),
              "q+": Math.floor((this.getMonth() + 3) / 3),
              "S+": this.getMilliseconds()
       };
       if (/(y+)/i.test(format)) {
              format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
       }
       for (var k in date) {
              if (new RegExp("(" + k + ")").test(format)) {
                     format = format.replace(RegExp.$1, RegExp.$1.length == 1
                            ? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
              }
       }
       return format;
}

Array.prototype.in_array = function(e)  
{  
for(i=0;i<this.length;i++)  
{  
if(this[i] == e)  
return true;  
}  
return false;  
} 

function parseDate(dateStr){
	var leng=dateStr.length;
	var resultDate;
	if(leng==6){
		resultDate=dateStr.substr(0,4)+'/'+dateStr.substr(4,2)+'/01';
	}else if(leng==8){
		resultDate=dateStr.substr(0,4)+'/'+dateStr.substr(4,2)+'/'+dateStr.substr(6,2);
	}else if(leng==10){
		resultDate=dateStr.substr(0,4)+'/'+dateStr.substr(4,2)+'/'+dateStr.substr(6,2)+' '+dateStr.substr(8,2)+':00:00';
	}else if(leng==12){
		resultDate=dateStr.substr(0,4)+'/'+dateStr.substr(4,2)+'/'+dateStr.substr(6,2)+' '+dateStr.substr(8,2)+':'+dateStr.substr(10,2)+':00';
	}else if(leng==14){
		resultDate=dateStr.substr(0,4)+'/'+dateStr.substr(4,2)+'/'+dateStr.substr(6,2)+' '+dateStr.substr(8,2)+':'+dateStr.substr(10,2)+':'+dateStr.substr(12,2);
	}
	return new Date(resultDate);
}
