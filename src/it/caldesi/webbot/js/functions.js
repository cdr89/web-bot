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

function highlight(element) {
	var div = highlight.div; // only highlight one element per page

	if (element === null) { // remove highlight via `highlight(null)`
		if (div.parentNode)
			div.parentNode.removeChild(div);
		return;
	}

	var width = element.offsetWidth, height = element.offsetHeight;

	div.style.width = width + 'px';
	div.style.height = height + 'px';

	element.offsetParent.appendChild(div);

	div.style.left = element.offsetLeft + (width - div.offsetWidth) / 2 + 'px';
	div.style.top = element.offsetTop + (height - div.offsetHeight) / 2 + 'px';
}

highlight.div = document.createElement('div');

// set highlight styles
with (highlight.div.style) {
	position = 'absolute';
	border = '5px solid yellow';
}

function getElementByXPath(xPath) {
	var element = document.evaluate(xPath, document, null,
			XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
	
	return element;
}
