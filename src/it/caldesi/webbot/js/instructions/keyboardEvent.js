var string = $#value#$; 

for (var i = 0, len = string.length; i < len; i++) {
	// alert(string.charCodeAt(i));
	// alert(string.charAt(i));
	var keyboardEvent = document.createEvent("KeyboardEvent");
	var initMethod = typeof keyboardEvent.initKeyboardEvent !== 'undefined' ? "initKeyboardEvent" : "initKeyEvent";
	keyboardEvent[initMethod](
                   	$#eventName#$, // event type : keydown, keyup, keypress
                    true, // bubbles
                    true, // cancelable
                    window, // viewArg: should be window
                    false, // ctrlKeyArg
                    false, // altKeyArg
                    false, // shiftKeyArg
                    false, // metaKeyArg
                    string.charCodeAt(i), // keyCodeArg : unsigned long the
											// virtual key code, else
						// 0
                    string.charCodeAt(i) // charCodeArgs : unsigned long the
											// Unicode character
						// associated with the depressed key, else 0
    );
	
	// TODO document if null web_target
	document.dispatchEvent(keyboardEvent);
}