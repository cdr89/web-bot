var keyboardEvent = document.createEvent("KeyboardEvent");
var initMethod = typeof keyboardEvent.initKeyboardEvent !== 'undefined' ? "initKeyboardEvent" : "initKeyEvent";
keyboardEvent[initMethod](
               	$#eventName#$, // event type : keydown, keyup, keypress
            	$#bubbles#$, // bubbles
            	$#cancelable#$, // cancelable
                window, // viewArg: should be window
            	$#ctrlKey#$, // ctrlKeyArg
            	$#altKey#$, // altKeyArg
            	$#shiftKey#$, // shiftKeyArg
            	$#metaKey#$, // metaKeyArg
            	$#keyCode#$, // keyCodeArg : unsigned long the virtual
								// key code, else 0
            	$#charCode#$ // charCodeArgs : unsigned long the Unicode
								// character associated with the depressed
								// key, else 0
);

if(webbot_target)
	webbot_target.dispatchEvent(keyboardEvent);
else
	document.dispatchEvent(keyboardEvent);