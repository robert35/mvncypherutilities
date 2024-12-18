package com.epsilon777.maven.mvndecryptui.framemvndecryptui;

import com.epsilon777.maven.mvndecryptui.config.I18N;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FrameMvnDecryptUI class.
 *
 * @author Nafaa Friaa (nafaa.friaa@isetjb.rnu.tn)
 */
@Slf4j
public class FrameMvnDecryptUI extends JInternalFrame implements ConfigurationChangeListener {

	JLabel settingslabel;
	File settingsfile;
	JButton SETTINGSfileChooserbutton = new JButton ( I18N.lang("mnvcypherui.open.settings.button.label") );

	JLabel settingssecuritylabel;
	File settingssecurityfile;
	JButton SETTINGSSECURITYfileChooserbutton = new JButton ( " Désigner le fichier settings-security necessaire au décryptage >> " );

	JButton decodebutton = new JButton ( "Décode >> " );
	JButton encodebutton = new JButton ( " << Encode" );
	JButton savesettingsbutton = new JButton ( " << Sauver le fichier nouvellement encodé settings.xml >>" );

	// Text component
	String encodedfileasstring="";
	String decodedfileasstring="";

	JTextArea settingssecurityfileeditor = new JTextArea (10, 250);
	JTextArea encodedsettingsfileeditor = new JTextArea (50, 100);
	JTextArea decodedsettingsfileeditor = new JTextArea (50, 100);
	JTextArea erroreditor = new JTextArea (10, 250);

	JScrollPane settingssecurityfileeditorscroll
						= new JScrollPane( settingssecurityfileeditor,
						JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
						JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	JScrollPane encodedfileeditorscroll
						= new JScrollPane( encodedsettingsfileeditor,
						JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
						JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	JScrollPane decodedfileeditorscroll
						= new JScrollPane( decodedsettingsfileeditor,
						JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
						JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

	JScrollPane erroreditorscroll
						= new JScrollPane( erroreditor,
						JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
						JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);




	MVNCypher mvndecoder = new MVNCypher ();

	/**
	 * Constructor.
	 */
	public FrameMvnDecryptUI ( ) throws PropertyVetoException {
		log.debug ( "START constructor..." );

		setTitle ( I18N.lang ( "frame1.title" ) );
		setLocation ( new Random ( ).nextInt ( 120 ) + 10, new Random ( ).nextInt ( 120 ) + 10 );
		setSize ( 2000, 1000 );
		setMaximum ( true );

		setClosable ( true );
		setIconifiable ( true );
		setMaximizable ( true );
		setResizable ( true );
		setDefaultCloseOperation ( HIDE_ON_CLOSE );

		//add compnent to the frame :

		constructPanel ( getContentPane ( ) );

		setVisible ( false );

		log.debug ( "End of constructor." );
	}

	private void constructPanel ( Container pane ) {


		this.decodedfileeditorscroll.setMinimumSize ( new Dimension ( 400, 600 ) );
		this.encodedfileeditorscroll.setMinimumSize ( new Dimension ( 400, 600 ) );
		this.settingssecurityfileeditorscroll.setMinimumSize ( new Dimension ( 1600, 200 ) );
		this.erroreditorscroll.setMinimumSize ( new Dimension ( 1600, 200 ) );



		listeners.add( this);

		pane.setLayout ( new GridBagLayout (  ) );

		GridBagConstraints gbc;

		gbc= new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx= 0;
		gbc.gridy=0;
		gbc.insets = new Insets ( 5, 5, 5, 5 );
		SETTINGSfileChooserbutton.setMargin ( new Insets ( 5, 5, 5, 5 ) );
		pane.add ( SETTINGSfileChooserbutton, gbc );

		gbc= new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx= 0;
		gbc.gridy=1;
		gbc.insets = new Insets ( 5, 5, 5, 5 );
		SETTINGSSECURITYfileChooserbutton.setMargin ( new Insets ( 5, 5, 5, 5 ) );
		pane.add ( SETTINGSSECURITYfileChooserbutton, gbc );

		gbc= new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx= 0;
		gbc.gridy=2;
		gbc.gridwidth=3;
		gbc.gridheight=1;
		gbc.insets = new Insets ( 5, 5, 5, 5 );
		pane.add( settingssecurityfileeditorscroll, gbc );


		gbc= new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx= 0;
		gbc.gridy=3;
		gbc.gridwidth=3;
		gbc.gridheight=1;
		gbc.insets = new Insets ( 5, 5, 5, 5 );
		pane.add( erroreditorscroll, gbc );

		//encodedfileeditor.setWrapStyleWord(true);
		//encodedfileeditor.setLineWrap(false);
		gbc= new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx= 0;
		gbc.gridy=5;
		gbc.gridwidth=1;
		gbc.gridheight=4;
		gbc.insets = new Insets ( 5, 5, 5, 5 );
		pane.add( encodedfileeditorscroll, gbc );

		gbc= new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx= 2;
		gbc.gridy=5;
		gbc.gridwidth=1;
		gbc.gridheight=4;
		gbc.insets = new Insets ( 5, 5, 5, 5 );
		pane.add( decodedfileeditorscroll, gbc );


		gbc= new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx= 1;
		gbc.gridy=5;

		gbc.insets = new Insets ( 5, 5, 5, 5 );
		decodebutton.setMargin ( new Insets ( 5, 5, 5, 5 ) );
		pane.add ( decodebutton, gbc );
		decodebutton.addActionListener (new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					onAskToDecode ();
				} catch ( Exception ex ) {
					throw new RuntimeException ( ex );
				}
			}
		});

		gbc= new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx= 1;
		gbc.gridy=6;
		gbc.insets = new Insets ( 5, 5, 5, 5 );
		encodebutton.setMargin ( new Insets ( 5, 5, 5, 5 ) );
		pane.add ( encodebutton, gbc );
		encodebutton.addActionListener (new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					onAskToEncode ();
				} catch ( Exception ex ) {
					throw new RuntimeException ( ex );
				}
			}
		});

		

		gbc= new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx= 0;
		gbc.gridy=9;
		gbc.insets = new Insets ( 5, 5, 5, 5 );
		savesettingsbutton.setMargin ( new Insets ( 5, 5, 5, 5 ) );
		pane.add ( savesettingsbutton, gbc );
		savesettingsbutton.addActionListener (new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					onSaveSettings ();
				} catch ( Exception ex ) {
					throw new RuntimeException ( ex );
				}
			}
		});


		settingslabel = new JLabel ( );
		gbc= new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx= 1;
		gbc.gridy=0;
		//gbc.gridwidth=2;
		//gbc.gridheight=1;
		gbc.insets = new Insets ( 5, 5, 5, 5 );
		pane.add ( settingslabel, gbc );


		settingssecuritylabel = new JLabel ( );
		gbc= new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx= 1;
		gbc.gridy=1;
		//gbc.gridwidth=2;
		//gbc.gridheight=1;
		gbc.insets = new Insets ( 5, 5, 5, 5 );
		pane.add ( settingssecuritylabel, gbc );

		SETTINGSfileChooserbutton.addActionListener ( new ActionListener ( ) {

			@Override
			public void actionPerformed ( ActionEvent e ) {
				JFileChooser fileChooser = new JFileChooser ( );
				fileChooser.setCurrentDirectory ( new File ( System.getProperty ( "user.home" ) ) );
				fileChooser.setFileSelectionMode ( JFileChooser.FILES_ONLY );
				fileChooser.setFileHidingEnabled ( false );
				int option = fileChooser.showOpenDialog ( pane );
				if ( option == JFileChooser.APPROVE_OPTION ) {

					fireSettingschanged ( fileChooser.getSelectedFile ( ));



				} else {
					fireSettingschanged ( null);
					settingslabel.setText ( "Open command canceled" );
				}
			}
		} );





		SETTINGSSECURITYfileChooserbutton.addActionListener ( new ActionListener ( ) {

			@Override
			public void actionPerformed ( ActionEvent e ) {
				JFileChooser fileChooser = new JFileChooser ( );
				fileChooser.setCurrentDirectory ( new File ( System.getProperty ( "user.home" ) ) );
				fileChooser.setFileSelectionMode ( JFileChooser.FILES_ONLY );
				fileChooser.setFileHidingEnabled ( false );
				int option = fileChooser.showOpenDialog ( pane );
				if ( option == JFileChooser.APPROVE_OPTION ) {

					fireSettingsSecuritychanged ( fileChooser.getSelectedFile ( ));

				} else {
					fireSettingsSecuritychanged ( null );
					settingssecuritylabel.setText ( "Open command canceled" );
				}
			}
		} );

		this.decodedsettingsfileeditor.getDocument().addDocumentListener( new DocumentListener () {

			@Override
			public void removeUpdate( DocumentEvent e) {
				ondecodedfileditorchanged();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				ondecodedfileditorchanged();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				ondecodedfileditorchanged();
			}
		});


		JScrollBar sBar1 = encodedfileeditorscroll.getVerticalScrollBar();
		JScrollBar sBar2 = decodedfileeditorscroll.getVerticalScrollBar();
		sBar2.setModel(sBar1.getModel()); //<--------------synchronize

	}

	private void onSaveSettings ( ) throws FileNotFoundException {

		//bakup first
		Date now = new Date (); // java.util.Date, NOT java.sql.Date or java.sql.Timestamp!
		String format3 = new SimpleDateFormat ("yyyyMMddHHmmss", Locale.ENGLISH).format(now);

		Path source = Paths.get(this.settingsfile.getAbsolutePath ());
		Path target = Paths.get(this.settingsfile.getAbsolutePath ()+".bakupcypher."+format3);
		try {
			Files.copy(source, target);
		} catch ( IOException e1) {
			e1.printStackTrace();
		}

		//save
		PrintWriter out = new PrintWriter(this.settingsfile.getAbsolutePath ());
		out.println(this.encodedfileasstring);
		out.close ();

	}

	private void ondecodedfileditorchanged ( ) {

		this.decodedfileasstring = this.decodedsettingsfileeditor.getText ();


		//validate xml
		try {
			validateXMLString(String.valueOf ( this.decodedfileasstring));
			//this.erroreditor.setBackground(Color.GREEN);
			this.erroreditor.setForeground(Color.BLACK);
			this.erroreditor.setText ( "" );
			this.encodebutton.setEnabled ( true );
		}
		catch ( Exception e ) {
			//this.erroreditor.setBackground(Color.GRAY);
			this.erroreditor.setForeground(Color.RED);
			this.erroreditor.setText ( e.getMessage () );
			this.encodebutton.setEnabled ( false );
		}

	}

	@Override
	public void onsettingschanged ( File settingsfile ) {
		this.settingsfile = settingsfile;
		this.settingslabel.setText ( "File Selected for settings: " + settingsfile.getAbsolutePath () );
		// Set the label to the path of the selected directory

		try {
			// String
			String s1 = "", sl = "";

			// File reader
			FileReader fr = new FileReader ( settingsfile );

			// Buffered reader
			BufferedReader br = new BufferedReader ( fr );

			// Initialize sl
			sl = br.readLine ( );

			// Take the input from the file
			while ( ( s1 = br.readLine ( ) ) != null ) {
				sl = sl + "\n" + s1;
			}

			// Set the text
			encodedsettingsfileeditor.setText ( sl );
			encodedsettingsfileeditor.setEnabled ( false );
			this.encodedfileasstring=sl;

		} catch ( Exception evt ) {

		}

		fireConfigurationChanged ();

	}

	@Override
	public void onsettingssecuritychanged ( File settingssecurityfile ) {
		this.settingssecurityfile = settingssecurityfile;
		this.settingssecuritylabel.setText ( "File Selected for settings security: " + settingssecurityfile.getAbsolutePath () );



		try {
			// String
			String s1 = "", sl = "";

			// File reader
			FileReader fr = new FileReader ( settingssecurityfile );

			// Buffered reader
			BufferedReader br = new BufferedReader ( fr );

			// Initialize sl
			sl = br.readLine ( );

			// Take the input from the file
			while ( ( s1 = br.readLine ( ) ) != null ) {
				sl = sl + "\n" + s1;
			}

			// Set the text
			settingssecurityfileeditor.setText ( sl );
			settingssecurityfileeditor.setEnabled ( false );

		} catch ( Exception evt ) {

		}



		fireConfigurationChanged ();
	}

	@Override
	public void onconfigurationchanged ( ) {
		boolean configok = (this.settingssecurityfile != null) && (this.settingsfile != null);
		this.decodebutton.setEnabled ( configok );
		this.encodebutton.setEnabled ( configok );

	}

	private void onAskToEncode() throws Exception {

		MVNCypher mvncypher = new MVNCypher ();
		mvncypher.configureSettings ( this.settingsfile.getAbsolutePath () );
		mvncypher.configureSettingsSecurity ( this.settingssecurityfile.getAbsolutePath () );

		//look for a pattern to test

		String input = this.decodedfileasstring;
		String regex = "#\\{(.*)\\}";
		//Creating a pattern object
		Pattern pattern = Pattern.compile(regex);


		this.encodedfileasstring = new String(this.decodedfileasstring);

		ArrayList list = new ArrayList();
		//Matching the compiled pattern in the String
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) {
			list.add(matcher.group(1));
		}
		Iterator<String> it = list.iterator();
		System.out.println("List of matches: ");
		while(it.hasNext()){
			String decodeds = it.next ();
			System.out.println(decodeds);
			String encodeds = mvncypher.encodeKey ( decodeds );
			System.out.println("encoded : " + encodeds);

			this.encodedfileasstring = this.encodedfileasstring.replace ( "#{"+decodeds+"}", "#{"+encodeds+"}" );
		}

		this.encodedsettingsfileeditor.setText ( this.encodedfileasstring );

		System.out.println ("this.encodedfileasstring");
		System.out.println (this.encodedfileasstring);



	}


	public static boolean validateXMLSchema(String xsdPath, String xmlPath){

		try {
			SchemaFactory factory =
								SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new File(xsdPath));
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource (new File(xmlPath)));
		} catch (IOException | SAXException e) {
			System.out.println("Exception: "+e.getMessage());
			return false;
		}
		return true;
	}


public static boolean validateXMLFile(String xmlPath) throws ParserConfigurationException, SAXException, IOException {
	SAXParserFactory factory = SAXParserFactory.newInstance();
	factory.setValidating(true);
	factory.setNamespaceAware(true);

	SAXParser parser = factory.newSAXParser();
	parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
						"http://www.w3.org/2001/XMLSchema");

	XMLReader reader = parser.getXMLReader();

	reader.setErrorHandler(new ErrorHandler (){
		public void warning( SAXParseException e) throws SAXException {
			System.out.println(e.getMessage());
		}

		public void error(SAXParseException e) throws SAXException {
			System.out.println(e.getMessage());
		}

		public void fatalError(SAXParseException e) throws SAXException {
			System.out.println(e.getMessage());
		}

	});
	reader.parse(new InputSource (xmlPath));
	return true;
}


	public static boolean validateXMLString(String s) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(true);

		SAXParser parser = factory.newSAXParser();
		parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
							"http://www.w3.org/2001/XMLSchema");

		XMLReader reader = parser.getXMLReader();

		reader.setErrorHandler(new ErrorHandler (){
			public void warning( SAXParseException e) throws SAXException {
				System.out.println(e.getMessage());
			}

			public void error(SAXParseException e) throws SAXException {
				System.out.println(e.getMessage());
			}

			public void fatalError(SAXParseException e) throws SAXException {
				System.out.println(e.getMessage());
			}

		});
		InputSource is = new InputSource(new StringReader(s));
		reader.parse(is);
		return true;
	}





	private void onAskToDecode() throws Exception {

		MVNCypher mvncypher = new MVNCypher ();
		mvncypher.configureSettings ( this.settingsfile.getAbsolutePath () );
		mvncypher.configureSettingsSecurity ( this.settingssecurityfile.getAbsolutePath () );

		//look for a pattern to test
		System.out.println (mvncypher.decodePassword ( "o4+1xGTJmMQHaSKR0wEyGiBMzP+CbcJTO2gECJRj7bpZE6XDRmzhb2hVzYuRa0mPaVTw6zSRCzCHq4+RoV9XelBiB0Pe0YE4RhmHzMbei+I=" ));

		String input = this.encodedfileasstring;
		String regex = "#\\{(.*)\\}";
		//Creating a pattern object
		Pattern pattern = Pattern.compile(regex);


		this.decodedfileasstring = new String(this.encodedfileasstring);

		ArrayList list = new ArrayList();
		//Matching the compiled pattern in the String
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) {
			list.add(matcher.group(1));
		}
		Iterator<String> it = list.iterator();
		System.out.println("List of matches: ");
		while(it.hasNext()){
			String encodeds = it.next ();
			System.out.println(encodeds);
			String decodeds = mvncypher.decodePassword ( encodeds );
			System.out.println("decoded : " + decodeds);

			this.decodedfileasstring = this.decodedfileasstring.replace ( "#{"+encodeds+"}", "#{"+decodeds+"}" );
		}

		this.decodedsettingsfileeditor.setText ( this.decodedfileasstring );

		System.out.println ("this.decodedfileasstring");
		System.out.println (this.decodedfileasstring);

	}


	///listener parts...

	private List<ConfigurationChangeListener> listeners = new ArrayList<ConfigurationChangeListener>();

	public void addObserver(ConfigurationChangeListener listener) {
		this.listeners.add(listener);
	}

	public void removeObserver(ConfigurationChangeListener listener) {
		this.listeners.remove(listener);
	}

	public void fireSettingschanged(File settingsfile) {
		for (ConfigurationChangeListener listener : this.listeners) {
			listener.onsettingschanged ( settingsfile );
		}
	}

	public void fireSettingsSecuritychanged(File settingssecurityfile) {
		for (ConfigurationChangeListener listener : this.listeners) {
			listener.onsettingssecuritychanged ( settingssecurityfile );
		}
	}

	public void fireConfigurationChanged() {
		for (ConfigurationChangeListener listener : this.listeners) {
			listener.onconfigurationchanged( );
		}
	}


}
