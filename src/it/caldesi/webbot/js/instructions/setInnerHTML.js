var element = document.evaluate("$#objectXPath#$", document, null,
		XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
var value = "$#value#$";
element.innerHTML = value;