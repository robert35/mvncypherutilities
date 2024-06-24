package com.epsilon777.mvncypherutilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {

	public static void main ( String[] args ) throws IOException {

		InputStream input = (new Main()).getClass().getClassLoader().getResourceAsStream ("sampleconfig.properties");

			Properties prop = new Properties();

			// load a properties file
			prop.load(input);

			System.out.println ( "Hello MvnCypherUtilities, ressources are decoded : !" );

			// get the property value and print it out
			System.out.println(prop.getProperty("prop.foo.bar.datasource.url"));
			System.out.println(prop.getProperty("prop.foo.bar.datasource.password"));
			System.out.println(prop.getProperty("prop.foo.bar.cleartextpropertie"));


	}
}