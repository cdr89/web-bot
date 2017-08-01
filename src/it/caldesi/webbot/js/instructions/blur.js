var eventTarget = getElementByXPath("$#objectXPath#$");
var evObj = document.createEvent("MouseEvents");
evObj.initEvent("blur", true, false);
eventTarget.dispatchEvent(evObj);