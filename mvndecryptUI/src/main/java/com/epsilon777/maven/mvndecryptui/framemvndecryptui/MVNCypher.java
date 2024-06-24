package com.epsilon777.maven.mvndecryptui.framemvndecryptui;

import org.apache.maven.settings.io.xpp3.SettingsXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.sonatype.plexus.components.cipher.DefaultPlexusCipher;
import org.sonatype.plexus.components.cipher.PlexusCipherException;
import org.sonatype.plexus.components.sec.dispatcher.DefaultSecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;
import org.sonatype.plexus.components.sec.dispatcher.SecUtil;
import org.sonatype.plexus.components.sec.dispatcher.model.SettingsSecurity;

import org.apache.maven.settings.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Jelmer Kuperus
 */
public class MVNCypher {

	private File settingsFile;
	private File securityFile;
	private SettingsSecurity settingsSecurity;
	private Settings settings;


	public void configureSettings(String settingsFileName) throws Exception {
		settingsFile = new File(settingsFileName);
		settings=readSettings(settingsFile);
	}

	public void configureSettingsSecurity(String securityFileName) throws Exception {
		securityFile = new File(securityFileName);
		settingsSecurity=readSettingsSecurity(securityFile);
	}

	private SettingsSecurity readSettingsSecurity(File file) throws SecDispatcherException {
		return SecUtil.read ( file.getAbsolutePath ( ), true );
	}

	private Settings readSettings(File file) throws IOException, XmlPullParserException {
		SettingsXpp3Reader reader = new SettingsXpp3Reader();
		return reader.read(new FileInputStream(file));
	}

	public String getMasterPassWordAsEncoded() {
		return settingsSecurity.getMaster();
	}


	public String getMasterPassWordAsDecoded() throws PlexusCipherException {
		DefaultPlexusCipher cipher = new DefaultPlexusCipher();
		return cipher.decryptDecorated(getMasterPassWordAsEncoded(), DefaultSecDispatcher.SYSTEM_PROPERTY_SEC_LOCATION);
	}


	public String decodePassword(String encodedPassword) throws PlexusCipherException {
		DefaultPlexusCipher cipher = new DefaultPlexusCipher();
		return cipher.decryptDecorated(encodedPassword, getMasterPassWordAsDecoded());
	}

	public String encodeKey(String key) throws PlexusCipherException {
		DefaultPlexusCipher cipher = new DefaultPlexusCipher();
		return cipher.encrypt ( key, getMasterPassWordAsDecoded());
	}


/*
	private void printPasswords(File settingsFile, File securityFile)
						throws IOException, XmlPullParserException, SecDispatcherException, PlexusCipherException {

		Settings settings = readSettings(settingsFile);
		SettingsSecurity settingsSecurity = readSettingsSecurity(securityFile);

		String encodedMasterPassword = settingsSecurity.getMaster();
		String plainTextMasterPassword = decodeMasterPassword(encodedMasterPassword);

		System.out.printf("Master password is : %s%n", plainTextMasterPassword);
		List<Server> servers = settings.getServers();

		for (Server server : servers) {
			String encodedServerPassword = server.getPassword();
			String plainTextServerPassword = decodePassword(encodedServerPassword, plainTextMasterPassword);

			System.out.println("-------------------------------------------------------------------------");
			System.out.printf("Credentials for server %s are :%n", server.getId());
			System.out.printf("Username : %s%n", server.getUsername());
			System.out.printf("Password : %s%n", plainTextServerPassword);
		}

	}

 */


}