package com.epsilon777.maven.extensions;

import lombok.extern.slf4j.Slf4j;
import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Named
@Singleton
@Slf4j
public class CryptoExtension extends AbstractMavenLifecycleParticipant {

	@Inject
	private SecDispatcher secDispatcher;

	private void initSecDispatcher() {
		if (secDispatcher != null) {
			try {
				if (secDispatcher instanceof org.sonatype.plexus.components.sec.dispatcher.DefaultSecDispatcher defaultSec) {
					Path settingsPath = Paths.get(System.getProperty("user.home"), ".m2", "settings-security.xml");
					defaultSec.setConfigurationFile(settingsPath.toString());
					System.out.println("✅ SecDispatcher initialisé avec " + settingsPath);
				}
			} catch (Exception e) {
				System.out.println("❌ Échec initialisation du SecDispatcher: " + e.getMessage());
				throw new RuntimeException("Erreur d'initialisation du SecDispatcher", e);
			}
		}
	}

	private String substituteAllTokens(String input) {
		if (input == null) return null;

		Pattern pattern = Pattern.compile("\\#\\{([^}]+)}");
		Matcher matcher = pattern.matcher(input);
		StringBuffer result = new StringBuffer();

		while (matcher.find()) {
			String encrypted = matcher.group(1);
			String tokenWithBraces = "{" + encrypted + "}";

			try {
				String decrypted = secDispatcher.decrypt(tokenWithBraces);
				matcher.appendReplacement(result, Matcher.quoteReplacement(decrypted));
			} catch (Exception e) {
				System.out.println("not decrypted : " + e.getMessage());
				matcher.appendReplacement(result, Matcher.quoteReplacement("#{" + encrypted + "}"));
			}
		}

		matcher.appendTail(result);
		return result.toString();
	}

	@Override
	public void afterProjectsRead(MavenSession session) {
		initSecDispatcher();
		System.out.println(">> ✅ CryptoExtension loaded : " + secDispatcher);

		for (MavenProject project : session.getProjects()) {
			Properties userProps = session.getUserProperties();
			Properties projectProps = project.getProperties();

			Set<String> allKeys = new HashSet<>();
			allKeys.addAll(userProps.stringPropertyNames());
			allKeys.addAll(projectProps.stringPropertyNames());

			for (String key : allKeys) {
				String value = userProps.containsKey(key)
									? userProps.getProperty(key)
									: projectProps.getProperty(key);

				if (value == null) continue;

				String newValue = substituteAllTokens(value);
				if (!newValue.equals(value)) {
					System.out.println("🔐 Decryption for key '" + key + "'");
					System.out.println("   Original: " + value);
					System.out.println("   Decrypted: " + newValue);

					userProps.setProperty(key, newValue);
					projectProps.setProperty(key, newValue);
					System.setProperty(key, newValue);
				}
			}

			// Propagation explicite
			logAndExportProperty("liquibase.username", projectProps);
			logAndExportProperty("liquibase.password", projectProps);
			logAndExportProperty("liquibase.url", projectProps);
		}
	}

	private void logAndExportProperty(String key, Properties props) {
		String value = props.getProperty(key);
		if (value != null) {
			System.setProperty(key, value);
			System.out.println("🔁 Propagated [" + key + "] = " + value);
		}
	}
}
