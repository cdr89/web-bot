var eventTarget = getElementByXPath("$#objectXPath#$");
var evObj = document.createEvent("MouseEvents");
evObj.initEvent("focus", true, false);
eventTarget.dispatchEvent(evObj);