var webbot_evObj = document.createEvent("MouseEvents");
webbot_evObj.initEvent("blur", true, false);
webbot_target.dispatchEvent(webbot_evObj);