var eventTarget = document.evaluate("$#objectXPath#$", document, null,
		XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
var evObj = document.createEvent("MouseEvents");
evObj.initEvent("focus", true, false);
eventTarget.dispatchEvent(evObj);