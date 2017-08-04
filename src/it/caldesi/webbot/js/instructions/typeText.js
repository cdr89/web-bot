(function writer(i){
	var string = $#value#$; 
    if(string.length <= i++){
      webbot_target.value = string;
      return;
    }
    
    webbot_target.value = string.substring(0,i);
    if( webbot_target.value[webbot_target.value.length - 1] != " " )
    	webbot_target.focus();
    
    var rand = Math.floor(Math.random() * (100)) + 100;
    setTimeout(function(){writer(i);}, rand);
})(0);