package com.epsilon777.maven.mvndecryptui.config;

import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * I18N Singleton class for Internationalization.
 *
 * @author Nafaa Friaa (nafaa.friaa@isetjb.rnu.tn)
 */
@Slf4j
public class I18N
{

	private static Locale locale;

	private static ResourceBundle messages;

	/**
	 * Private constructor so this class cannot be instantiated only by it self.
	 */
	private I18N()
	{
	}

	/**
	 * Method to create the ResourceBundle object if not exists.
	 *
	 * @return The ResourceBundle object if not exist.
	 */
	public static ResourceBundle getInstance()
	{
		if (messages == null)
		{
			log.debug("Internationalization settings...");
			try
			{
				// user default Locale :
				locale = Locale.getDefault();

				log.info("Default user Locale : " + locale.getLanguage() + "_" + locale.getCountry());

				messages = ResourceBundle.getBundle("messages", locale);
			} catch ( MissingResourceException e)
			{
				log.error("Cannot configure Locale, missing resources file => " + e);
				System.exit(0);
			}
		}

		return messages;
	}

	/**
	 * Method to simplify call => I18N.lang("aa.bb") and not
	 * I18N.getInstance().getMessages().getString("aa.bb") and to catch exeption
	 * when key not found.
	 *
	 * @param key
	 * @return The string value of given key.
	 */
	public static String lang(String key)
	{
		String value = "KEY_NOT_FOUND";

		try
		{
			value = I18N.getInstance().getString(key);
		} catch (Exception e)
		{
			log.error("Cannot find value for this key : " + key);
		}

		return value;
	}

	/**
	 * Create the instance for the first time.
	 */
	public static void init()
	{
		if (I18N.getInstance() != null)
		{
			log.info("Locale configured successfully to : " + I18N.lang("tag"));
		}
	}
}
