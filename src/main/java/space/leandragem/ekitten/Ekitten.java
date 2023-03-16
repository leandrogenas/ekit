package space.leandragem.ekitten;/*
GNU Lesser General Public License

space.leandragem.ekitten.Ekit - Java Swing HTML Editor & Viewer
Copyright (C) 2000 Howard Kistler

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JFrame;

import static space.leandragem.ekitten.editor.Toolbar.*;

/** space.leandragem.ekitten.Ekit
  * App for editing and saving HTML in a Java text space.leandragem.ekitten.component
  *
  * @author Howard Kistler
  * @version 1.5
  *
  * REQUIREMENTS
  * Java 2 (JDK 1.5 or higher)
  * Swing Library
  */

public class Ekitten extends JFrame
{



	protected EkittenCore ekittenCore;

	protected File currentFile = (File)null;

	public Ekitten(EkittenCore.Builder builder)
	{
//		if(builder.spellCheck())
//		{
//			ekittenCore = new EkittenCoreSpell(false, sDocument, sStyleSheet, sRawDocument, null, urlStyleSheet, includeToolBar, showViewSource, showMenuIcons, editModeExclusive, sLanguage, sCountry, base64, debugMode, true, multiBar, (multiBar ? TOOLBAR_DEFAULT_MULTI : TOOLBAR_DEFAULT_SINGLE), enterBreak);
//		}
//		else
//		{
			ekittenCore = new EkittenCore(builder);
//		}

//		ekittenCore.setFrame(ekittenCore.getFrame());

		/* Add the components to the app */
		if(builder.isIncludeToolBar())
		{
			if(builder.isIncludeMultibar())
			{
				this.getContentPane().setLayout(new GridBagLayout());
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.fill       = GridBagConstraints.HORIZONTAL;
				gbc.anchor     = GridBagConstraints.NORTH;
				gbc.gridheight = 1;
				gbc.gridwidth  = 1;
				gbc.weightx    = 1.0;
				gbc.weighty    = 0.0;
				gbc.gridx      = 1;

				gbc.gridy      = 1;
				this.getContentPane().add(ekittenCore.getToolBarMain(builder.isIncludeToolBar()), gbc);

				gbc.gridy      = 2;
				this.getContentPane().add(ekittenCore.getToolBarFormat(builder.isIncludeToolBar()), gbc);

				gbc.gridy      = 3;
				this.getContentPane().add(ekittenCore.getToolBarStyles(builder.isIncludeToolBar()), gbc);

				gbc.anchor     = GridBagConstraints.SOUTH;
				gbc.fill       = GridBagConstraints.BOTH;
				gbc.weighty    = 1.0;
				gbc.gridy      = 4;
				this.getContentPane().add(ekittenCore, gbc);
			}
			else
			{
				this.getContentPane().setLayout(new BorderLayout());
				this.getContentPane().add(ekittenCore, BorderLayout.CENTER);
				this.getContentPane().add(ekittenCore.getToolBar(builder.isIncludeToolBar()), BorderLayout.NORTH);
			}
		}
		else
		{
			this.getContentPane().setLayout(new BorderLayout());
			this.getContentPane().add(ekittenCore, BorderLayout.CENTER);
		}

		this.setJMenuBar(ekittenCore.getMenuBar());

		this.addWindowListener(this.ekittenCore);

		this.updateTitle();
		this.pack();
		this.setVisible(true);
	}


	/** Convenience method for updating the application title bar
	  */
	protected void updateTitle()
	{
		this.setTitle(ekittenCore.getAppName() + (currentFile == null ? "" : " - " + currentFile.getName()));
	}

	/** Usage method
	  */
	public static void usage()
	{
		System.out.println("usage: Ekitten [-t|t+|T] [-s|S] [-m|M] [-x|X] [-b|B] [-v|V] [-p|P] [-fFILE] [-cCSS] [-rRAW] [-uURL] [-lLANG] [-d|D] [-h|H|?]");
		System.out.println("       Each option contained in [] brackets is optional,");
		System.out.println("       and can be one of the values separated be the | pipe.");
		System.out.println("       Each option must be proceeded by a - hyphen.");
		System.out.println("       The options are:");
		System.out.println("         -t|t+|T : -t = single toolbar, -t+ = multiple toolbars, -T = no toolbar");
		System.out.println("         -s|S    : -s = show source window on startup, -S = hide source window");
		System.out.println("         -m|M    : -m = show com.hexidec.ekit.icons on menus, -M = no menu com.hexidec.ekit.icons");
		System.out.println("         -x|X    : -x = exclusive document/source windows, -X = use split window");
		System.out.println("         -b|B    : -b = use Base64 document encoding, -B = use regular encoding");
		System.out.println("         -v|V    : -v = include spell checker, -V = omit spell checker");
		System.out.println("         -p|P    : -p = ENTER key inserts paragraph, -P = inserts break");
		System.out.println("         -fFILE  : load HTML document on startup (replace FILE with file name)");
		System.out.println("         -cCSS   : load CSS stylesheet on startup (replace CSS with file name)");
		System.out.println("         -rRAW   : load raw document on startup (replace RAW with file name)");
		System.out.println("         -uURL   : load document at URL on startup (replace URL with file URL)");
		System.out.println("         -lLANG  : specify the starting language (defaults to your locale)");
		System.out.println("                    replace LANG with xx_XX format (e.g., US English is en_US)");
		System.out.println("         -d|D    : -d = DEBUG mode on, -D = DEBUG mode off (developers only)");
		System.out.println("         -h|H|?  : print out this help information");
		System.out.println("         ");
		System.out.println("The defaults settings are equivalent to: -t+ -S -m -x -B -V -p -D");
		System.out.println("         ");
		System.out.println("For further information, read the README file.");
	}

	/** Main method
	  */
	public static void main(String[] args)
	{
		EkittenCore.Builder builder = new EkittenCore.Builder("Ekitten", false);

		for (String arg : args) {
			if (arg.equals("-h") ||
					arg.equals("-H") ||
					arg.equals("-?")) {
				usage();
			} else if (arg.equals("-t")) {
				builder.includeToolbar(true);
				builder.includeMultibar(false);
			} else if (arg.equals("-t+")) {
				builder.includeToolbar(true);
				builder.includeMultibar(true);
			} else if (arg.equals("-T")) {
				builder.includeToolbar(false);
				builder.includeMultibar(false);
			} else if (arg.equals("-s")) {
				builder.includeViewSource(true);
			} else if (arg.equals("-S")) {
				builder.includeViewSource(false);
			} else if (arg.equals("-m")) {
				builder.includeMenuIcons(true);
			} else if (arg.equals("-M")) {
				builder.includeMenuIcons(false);
			} else if (arg.equals("-x")) {
				builder.modeExclusive(true);
			} else if (arg.equals("-X")) {
				builder.modeExclusive(false);
			} else if (arg.equals("-b")) {
				builder.base64(true);
			} else if (arg.equals("-B")) {
				builder.base64(false);
			} else if (arg.startsWith("-f")) {
				builder.document(arg.substring(2));
			} else if (arg.startsWith("-c")) {
				builder.stylesheet(arg.substring(2));
			} else if (arg.startsWith("-r")) {
				builder.rawDocument(arg.substring(2));
			} else if (arg.equals("-v")) {
				builder.spellCheck(true);
			} else if (arg.equals("-V")) {
				builder.spellCheck(false);
			} else if (arg.equals("-p")) {
				builder.enterBreak(true);
			} else if (arg.equals("-P")) {
				builder.spellCheck(false);
			}
//			else if(args[i].startsWith("-n"))     { appName = args[i].substring(2); }
			else if (arg.startsWith("-u")) {
				try {
					builder.urlStylesheet(new URL(arg.substring(2)));
				} catch (MalformedURLException murle) {
					murle.printStackTrace(System.err);
				}
			} else if (arg.startsWith("-l")) {
				if (arg.indexOf('_') == 4 && arg.length() >= 7) {
					builder.lang(arg.substring(2, arg.indexOf('_')));
					builder.country(arg.substring(arg.indexOf('_') + 1));
				}
			} else if (arg.equals("-d")) {
				builder.debugOn(true);
			} else if (arg.equals("-D")) {
				builder.debugOn(false);
			}
		}

		Ekitten ekitten = new Ekitten(builder);
	}

}