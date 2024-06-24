package com.epsilon777.maven.mvndecryptui.framemvndecryptui;
import java.io.File;
import java.util.EventObject;

public interface ConfigurationChangeListener  {

	public void onsettingschanged( File settingsfile);
	public void onsettingssecuritychanged( File settingssecurityfile);
	public void onconfigurationchanged( );

}