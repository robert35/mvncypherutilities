package com.epsilon777.maven.plugin;

import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;

import java.util.Iterator;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @goal echopass
 * @phase process-sources
 */

@Slf4j
@Mojo(name = "decrypt-properties", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class CryptoMojo extends AbstractMojo {

	/**
	 * Gives access to the Maven project information.
	 */
	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	MavenProject project;

	/**
	 * Plexus component for the SecDispatcher
	 *
	 * @component roleHint="mng-4384"
	 */
	// @Component(role = SecDispatcher.class, hint = "mng-4384")
	private SecDispatcher secDispatcher;

	private String decrypt ( String input ) {

		//log.info ( "Trying to decrypt " + input );

		try {
			String res = secDispatcher.decrypt ( input );
			//log.info ( "decrypted is " + input );
			return res;
		}
		catch ( SecDispatcherException sde ) {
			log.warn ( sde.getMessage ( ) );
			return input;
		}
	}

	public void execute ( ) throws MojoExecutionException {

		Properties properties = project.getProperties ( );

		//log.info ( "------------AVANT DECHIFFREMENT--------------" );
		//showProperties ( properties );



		Iterator it = properties.keys ( ).asIterator ( );
		while ( it.hasNext ( ) ) {
			String key = ( String ) it.next ( );
			String value = properties.getProperty ( key );
			properties.setProperty ( key, substituteAllTokens ( value ) );
		}

		// String s = decrypt(password);

		//getLog ( ).info ( "------------APRES DECHIFFREMENT--------------" );
		//showProperties ( properties );
	}


	private String substituteAllTokens ( String toInspect ) {

		//log.info("inspecting : " + toInspect );

		//c'est bien le # qu'il faut regarder, il provient du fichier settings.xml

		if (! toInspect.matches ( ".*\\#\\{.*\\}.*" ) ) return toInspect;

		String res = Pattern.compile ( "\\#(\\{.*\\})" ).matcher ( toInspect ).replaceAll ( gr -> {
			String encoded = gr.group ( 1 );
			String decoded = decrypt ( encoded );
			//log.info ("-----> found encoded : "+encoded);
			//log.info ("-----> decoded is    : "+decoded);

			return decoded;
		} );

		//log.info("inspecting decoded is : " + res );
	  return res.matches ( ".*\\#\\{.*\\}.*" ) ? substituteAllTokens(res) : res;
//		return res;

	}
/*
	private void showProperties ( Properties properties ) {
		log.info("***********************" );
		log.info("début des propriétés lues" );
		Iterator it = properties.keys ( ).asIterator ( );
		while ( it.hasNext ( ) ) {
			String key = ( String ) it.next ( );
			String value = properties.getProperty ( key );
			getLog ( ).info ( key + " : " + value );
		}
		log.info("fin des propriétés lues" );
		log.info("***********************" );

	}
	*/


}
