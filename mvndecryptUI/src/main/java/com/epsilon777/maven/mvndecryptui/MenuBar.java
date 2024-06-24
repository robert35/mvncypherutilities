package com.epsilon777.maven.mvndecryptui;

import com.epsilon777.maven.mvndecryptui.config.I18N;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Locale;


/**
 * MenuBar class.
 *
 * @author Nafaa Friaa (nafaa.friaa@isetjb.rnu.tn)
 */
@Slf4j
public class MenuBar extends JMenuBar
{

	JFrame app;


	// file :
	JMenu jMenuFile = new JMenu( I18N.lang("menubar.jMenuFile"));
	JMenuItem jMenuItemFrame1 = new JMenuItem(I18N.lang("menubar.jMenuItemFrame1"));
	JMenuItem jMenuItemQuit = new JMenuItem(I18N.lang("menubar.jMenuItemQuit"));

	// help :
	JMenu jMenuHelp = new JMenu(I18N.lang("menubar.jMenuHelp"));
	JMenuItem jMenuItemFrameAbout = new JMenuItem(I18N.lang("menubar.jMenuItemFrameAbout"));

	//i18n
	JMenu languageMenu = new JMenu("Langage");

	/**
	 * Constructor.
	 */
	public MenuBar( JFrame app )
	{
		log.debug("START constructor...");

		// file :
		add(jMenuFile);
		jMenuFile.setMnemonic( KeyEvent.VK_F);

		jMenuItemFrame1.setAccelerator(KeyStroke.getKeyStroke('R', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		jMenuFile.add(jMenuItemFrame1);

		jMenuFile.addSeparator();

		jMenuItemQuit.setAccelerator(KeyStroke.getKeyStroke('Q', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		jMenuFile.add(jMenuItemQuit);

		// help :
		add(jMenuHelp);
		jMenuHelp.setMnemonic(KeyEvent.VK_H);

		jMenuItemFrameAbout.setAccelerator(KeyStroke.getKeyStroke('A', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		jMenuHelp.add(jMenuItemFrameAbout);


		//i18n
		add(languageMenu);

		JMenuItem englishMenuItem = new JMenuItem();
		JMenuItem frenchMenuItem = new JMenuItem();

		englishMenuItem.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed( ActionEvent e) {
				app.setLocale(new Locale("en", "US"));
			}
		});

		frenchMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				app.setLocale(new Locale("fr", "FR"));
			}
		});

		languageMenu.add(englishMenuItem);
		languageMenu.add(frenchMenuItem);
		add(languageMenu);

		languageMenu.setText(I18N.lang ("i18nmenu.language"));
		englishMenuItem.setText(I18N.lang ("i18nmenu.language.english"));
		frenchMenuItem.setText(I18N.lang ("i18nmenu.language.french"));


		log.debug("End of constructor.");
	}
}
