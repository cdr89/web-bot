function getElementByAttribute(attr, value, root) {
	root = root || document.body;
	if (root.hasAttribute(attr) && root.getAttribute(attr) == value) {
		return root;
	}
	var children = root.children;
	var element;
	for (var i = children.length; i--;) {
		element = getElementByAttribute(attr, value, children[i]);
		if (element) {
			return element;
		}
	}
	return null;
}

function getXPath(element) {
	var val = element.value;
	var xpath = '';
	for (; element && element.nodeType == 1; element = element.parentNode) {
		var id = $(element.parentNode).children(element.tagName).index(element) + 1;
		id > 1 ? (id = '[' + id + ']') : (id = '');
		xpath = '/' + element.tagName.toLowerCase() + id + xpath;
	}
	return xpath;
}

function webbot_highlight(element) {
	var div = webbot_highlight.div; // only highlight one element per page

	if (element === null) { // remove highlight via `highlight(null)`
		if (div.parentNode)
			div.parentNode.removeChild(div);
		return;
	}

	var width = element.offsetWidth, height = element.offsetHeight;
	var rect = element.getBoundingClientRect();

	div.style.width = width + 'px';
	div.style.height = height + 'px';

	// element.offsetParent.appendChild(div);
	var bodyElement = document.getElementsByTagName("BODY")[0];
	bodyElement.appendChild(div);

	// div.style.left = element.offsetLeft + (width - div.offsetWidth) / 2 +
	// 'px';
	// div.style.top = element.offsetTop + (height - div.offsetHeight) / 2 +
	// 'px';

	div.style.left = rect.left + (width - div.offsetWidth) / 2 + 'px';
	div.style.top = rect.top + (height - div.offsetHeight) / 2 + 'px';
}

webbot_highlight.div = document.createElement('div');

// set highlight styles
with (webbot_highlight.div.style) {
	position = 'absolute';
	border = '5px solid yellow';
}

function getElementByXPath(xPath) {
	var element = document.evaluate(xPath, document, null,
			XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;

	return element;
}
