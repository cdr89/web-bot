package it.caldesi.webbot.script;

public enum Mode {

	BROWSING("scene.record.statusBar.mode.browsing"), //
	RECORDING("scene.record.statusBar.mode.recording"), //
	EXECUTING("scene.record.statusBar.mode.executing");

	String resourceId;

	Mode(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceId() {
		return resourceId;
	}

}
