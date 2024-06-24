package com.epsilon777.maven.mvndecryptui;

import lombok.extern.slf4j.Slf4j;
import  com.epsilon777.maven.mvndecryptui.config.PROP;
import  com.epsilon777.maven.mvndecryptui.config.I18N;
import com.formdev.flatlaf.FlatDarkLaf;



/**
 * Application class.
 *
 * @author Nafaa Friaa (nafaa.friaa@isetjb.rnu.tn)
 */
@Slf4j
public class Application
{

	public static void main(String[] args)
	{
		log.info("Initializing the application...");

		PROP.init();
		I18N.init();
		macosConfig();

		log.info("Starting " + PROP.getProperty("app.finalName") + " Application...");
		FlatDarkLaf.setup();

		// display the desktop frame :
		new Desktop();

		log.info("Application " + PROP.getProperty("app.finalName") + " started.");
	}

	/**
	 * Special settting for macOS.
	 */
	public static void macosConfig()
	{
		if (System.getProperty("os.name").contains("Mac"))
		{
			log.debug("Special settings for macOS users...");

			// take the menu bar off the jframe :
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
	}
}
