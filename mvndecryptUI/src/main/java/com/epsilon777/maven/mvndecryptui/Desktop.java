package com.epsilon777.maven.mvndecryptui;

import com.epsilon777.maven.mvndecryptui.config.I18N;
import com.epsilon777.maven.mvndecryptui.framemvndecryptui.FrameMvnDecryptUI;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Desktop class.
 *
 * @author Nafaa Friaa (nafaa.friaa@isetjb.rnu.tn)
 */
@Slf4j
public class Desktop extends JFrame
{


	JDesktopPane jDesktopPane = new JDesktopPane();
	JLabel jLabelFooterState = new JLabel( I18N.lang("desktop.jLabelFooterState") + System.getProperty("os.name"));

	// internal frames :
	FrameAbout frameAbout = new FrameAbout();
	FrameMvnDecryptUI frameMvnDecryptUI = new FrameMvnDecryptUI ();

	// menu :
	MenuBar menuBar;

	/**
	 * Constructor.
	 */
	public Desktop()
	{


		log.debug("START constructor...");

		// init frame :
		setTitle(I18N.lang("desktop.title"));
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(0, 0, screenSize.width, screenSize.height);

		// init desktop :
		getContentPane().add(jDesktopPane, BorderLayout.CENTER);
		getContentPane().add(jLabelFooterState, BorderLayout.SOUTH);

		// add internal frames to desktop :
		jDesktopPane.add(frameAbout);
		jDesktopPane.add( frameMvnDecryptUI );

		// add the menu bar :

		menuBar = new MenuBar( this );
		setJMenuBar(menuBar);

		// menu listeners :
		// jMenuItemQuit :
		menuBar.jMenuItemQuit.addActionListener(( ActionEvent ev) ->
		{
			log.debug("ActionEvent on " + ev.getActionCommand());

			if (confirmBeforeExit())
			{
				System.exit(0);
			}
		});

		// jMenuItemFrameAbout :
		menuBar.jMenuItemFrameAbout.addActionListener(new ActionListener ()
		{
			public void actionPerformed(ActionEvent ev)
			{
				log.debug("ActionEvent on " + ev.getActionCommand());

				frameAbout.setVisible(true);
			}
		});

		// jMenuItemFrame1 :
		menuBar.jMenuItemFrame1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ev)
			{
				log.debug("ActionEvent on " + ev.getActionCommand());

				frameMvnDecryptUI.setVisible(true);
			}
		});

		// window closing event :
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter ()
		{
			public void windowClosing( WindowEvent ev)
			{
				log.debug("WindowEvent on " + ev.paramString());

				if (confirmBeforeExit())
				{
					System.exit(0);
				}
			}
		});

		setVisible(true);

		log.debug("End of constructor.");
	}

	/**
	 * Show confirm dialog before closing the window.
	 *
	 * @return boolean true user answer Yes.
	 */
	public boolean confirmBeforeExit()
	{
		log.debug("Display confirm dialog...");

		if (JOptionPane.showConfirmDialog(this, I18N.lang("desktop.confirmbeforeexitdialog.text"), I18N.lang("desktop.confirmbeforeexitdialog.title"), JOptionPane.YES_NO_OPTION) == 0)
		{
			log.debug("User answer YES.");
			return true;
		}

		log.debug("User answer NO.");
		return false;
	}
}