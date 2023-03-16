package space.leandragem.ekitten;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.ChangedCharSetException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.CannotUndoException;

import space.leandragem.ekitten.action.*;
import space.leandragem.ekitten.component.*;
import space.leandragem.util.Base64Codec;
import space.leandragem.util.Load;
import space.leandragem.util.Translatrix;
import space.leandragem.ekitten.print.DocumentRenderer;

import static space.leandragem.ekitten.editor.Toolbar.*;
import static space.leandragem.ekitten.editor.Command.*;

/** space.leandragem.ekitten.EkitCore
  * Main application class for editing and saving HTML in a Java text space.leandragem.ekitten.component
  *
  * @author Howard Kistler
  * @version 1.1
  *
  * REQUIREMENTS
  * Java 2 (JDK 1.5 or higher)
  * Swing Library
  */

public class EkittenCore extends JPanel implements ActionListener, KeyListener, FocusListener, DocumentListener, WindowListener
{
	public static class Builder {

		/** Master Constructor
		 * @param sDocument         [String]  A text or HTML document to load in the editor upon startup.
		 * @param sStyleSheet       [String]  A CSS stylesheet to load in the editor upon startup.
		 * @param sRawDocument      [String]  A document encoded as a String to load in the editor upon startup.
		 * @param sdocSource        [StyledDocument] Optional document specification, using javax.swing.text.StyledDocument.
		 * @param urlStyleSheet     [URL]     A URL reference to the CSS style sheet.
		 * @param includeToolBar    [boolean] Specifies whether the app should include the toolbar(s).
		 * @param showViewSource    [boolean] Specifies whether or not to show the View Source window on startup.
		 * @param showMenuIcons     [boolean] Specifies whether or not to show icon pictures in menus.
		 * @param editModeExclusive [boolean] Specifies whether or not to use exclusive edit mode (recommended on).
		 * @param sLanguage         [String]  The language portion of the Internationalization Locale to run space.leandragem.ekitten.Ekit in.
		 * @param sCountry          [String]  The country portion of the Internationalization Locale to run space.leandragem.ekitten.Ekit in.
		 * @param base64            [boolean] Specifies whether the raw document is Base64 encoded or not.
		 * @param debugMode         [boolean] Specifies whether to show the Debug menu or not.
		 * @param hasSpellChecker   [boolean] Specifies whether or not this uses the SpellChecker module
		 * @param multiBar          [boolean] Specifies whether to use multiple toolbars or one big toolbar.
		 * @param toolbarSeq        [String]  Code string specifying the toolbar buttons to show.
		 * @param keepUnknownTags   [boolean] Specifies whether or not the parser should retain unknown tags.
		 * @param enterBreak        [boolean] Specifies whether the ENTER key should insert breaks instead of paragraph tags.
		 */
		private final String appName;
		public final boolean isParentApplet;
		public String toolbarSeq;
		private String document;
		private String stylesheet;
		private String rawDocument;
		private URL urlStyleSheet;
		private boolean includeToolBar = true;
		private boolean includeMultibar = true;
		private boolean includeViewSource = false;
		private boolean includeMenuIcons = true;
		private boolean modeExclusive = true;
		private String lang;
		private String country;
		private boolean base64 = false;
		private boolean debugOn = false;
		private boolean spellCheck = false;
		private boolean enterBreak = true;

		private boolean keepUnknownTags = false;

		public Builder(String appName, boolean isParentApplet) {
			this.appName = appName;
			this.isParentApplet = isParentApplet;
		}

		public boolean isIncludeToolBar() {
			return this.includeToolBar;
		}
		public boolean isIncludeMultibar() {
			return this.includeMultibar;
		}


		public Builder isParentApplet(boolean include){
			this.includeToolBar = include;
			return this;
		}

		public Builder toolbarSeq(String toolbarSeq){
			this.toolbarSeq = toolbarSeq;
			return this;
		}

		public Builder document(String document){
			this.document = document;
			return this;
		}

		public Builder stylesheet(String stylesheet){
			this.stylesheet = stylesheet;
			return this;
		}

		public Builder rawDocument(String rawDocument){
			this.rawDocument = rawDocument;
			return this;
		}

		public Builder urlStylesheet(URL urlStyleSheet){
			this.urlStyleSheet = urlStyleSheet;
			return this;
		}

		public Builder includeToolbar(boolean include){
			this.includeToolBar = include;
			return this;
		}

		public Builder includeMultibar(boolean include){
			this.includeMultibar = include;
			return this;
		}

		public Builder includeViewSource(boolean include){
			this.includeViewSource = include;
			return this;
		}

		public Builder includeMenuIcons(boolean include){
			this.includeMenuIcons = include;
			return this;
		}

		public Builder modeExclusive(boolean exclusive){
			this.modeExclusive = exclusive;
			return this;
		}

		public Builder lang(String lang){
			this.lang = lang;
			return this;
		}

		public Builder country(String country){
			this.country = country;
			return this;
		}

		public Builder base64(boolean base64){
			this.base64 = base64;
			return this;
		}

		public Builder debugOn(boolean debug){
			this.debugOn = debug;
			return this;
		}

		public Builder spellCheck(boolean spellCheck){
			this.spellCheck = spellCheck;
			return this;
		}

		public Builder enterBreak(boolean enterBreak){
			this.enterBreak = enterBreak;
			return this;
		}

		public Builder keepUnknownTags(boolean keepUnknownTags){
			this.keepUnknownTags = keepUnknownTags;
			return this;
		}

	}

	/* Components */
	private JSplitPane jspltDisplay;
	private JTextPane jtpMain;
	private ExtendedHTMLEditorKit htmlKit;
	private ExtendedHTMLDocument htmlDoc;
	private StyleSheet styleSheet;
	private JTextArea jtpSource;
	private JScrollPane jspSource;
	private JToolBar jToolBar;
	private JToolBar jToolBarMain;
	private JToolBar jToolBarFormat;
	private JToolBar jToolBarStyles;

	private JButtonNoFocus jbtnNewHTML;
	private JButtonNoFocus jbtnNewStyledHTML;
	private JButtonNoFocus jbtnOpenHTML;
	private JButtonNoFocus jbtnSaveHTML;
	private JButtonNoFocus jbtnPrint;
	private JButtonNoFocus jbtnCut;
	private JButtonNoFocus jbtnCopy;
	private JButtonNoFocus jbtnPaste;
	private JButtonNoFocus jbtnPasteX;
	private JButtonNoFocus jbtnUndo;
	private JButtonNoFocus jbtnRedo;
	private JButtonNoFocus jbtnBold;
	private JButtonNoFocus jbtnItalic;
	private JButtonNoFocus jbtnUnderline;
	private JButtonNoFocus jbtnStrike;
	private JButtonNoFocus jbtnSuperscript;
	private JButtonNoFocus jbtnSubscript;
	private JButtonNoFocus jbtnUList;
	private JButtonNoFocus jbtnOList;
	private JButtonNoFocus jbtnAlignLeft;
	private JButtonNoFocus jbtnAlignCenter;
	private JButtonNoFocus jbtnAlignRight;
	private JButtonNoFocus jbtnAlignJustified;
	private JButtonNoFocus jbtnFind;
	private JButtonNoFocus jbtnUnicode;
	private JButtonNoFocus jbtnUnicodeMath;
	private JButtonNoFocus jbtnAnchor;
	private JButtonNoFocus jbtnInsertTable;
	private JButtonNoFocus jbtnEditTable;
	private JButtonNoFocus jbtnEditCell;
	private JButtonNoFocus jbtnInsertRow;
	private JButtonNoFocus jbtnInsertColumn;
	private JButtonNoFocus jbtnDeleteRow;
	private JButtonNoFocus jbtnDeleteColumn;
	private JToggleButtonNoFocus jtbtnViewSource;
	private JComboBoxNoFocus jcmbStyleSelector;
	private JComboBoxNoFocus jcmbFontSelector;

	private Frame frameHandler;

	private HTMLUtilities htmlUtilities = new HTMLUtilities(this);

	/* Actions */
	private StyledEditorKit.BoldAction actionFontBold;
	private StyledEditorKit.ItalicAction actionFontItalic;
	private StyledEditorKit.UnderlineAction actionFontUnderline;
	private FormatAction actionFontStrike;
	private FormatAction actionFontSuperscript;
	private FormatAction actionFontSubscript;
	private ListAutomationAction actionListUnordered;
	private ListAutomationAction actionListOrdered;
	private final SetFontFamilyAction actionSelectFont;
	private final AlignAction actionAlignLeft;
	private final AlignAction actionAlignCenter;
	private final AlignAction actionAlignRight;
	private final AlignAction actionAlignJustified;
	private final CustomAction actionInsertAnchor;

	protected UndoManager undoMngr;
	protected UndoAction undoAction;
	protected RedoAction redoAction;

	/* Menus */
	private JMenuBar jMenuBar;
	private JMenu jMenuFile;
	private JMenu jMenuEdit;
	private JMenu jMenuView;
	private JMenu jMenuFont;
	private JMenu jMenuFormat;
	private JMenu jMenuInsert;
	private JMenu jMenuTable;
	private JMenu jMenuForms;
	private JMenu jMenuSearch;
	private JMenu jMenuTools;
	private JMenu jMenuHelp;
	private JMenu jMenuDebug;

	private JMenu jMenuToolbars;
	private JCheckBoxMenuItem jcbmiViewToolbar;
	private JCheckBoxMenuItem jcbmiViewToolbarMain;
	private JCheckBoxMenuItem jcbmiViewToolbarFormat;
	private JCheckBoxMenuItem jcbmiViewToolbarStyles;
	private JCheckBoxMenuItem jcbmiViewSource;
	private JCheckBoxMenuItem jcbmiEnterKeyParag;
	private JCheckBoxMenuItem jcbmiEnterKeyBreak;

	/* Constants */
	// Menu Keys
	public static final String KEY_MENU_FILE   = "file";
	public static final String KEY_MENU_EDIT   = "edit";
	public static final String KEY_MENU_VIEW   = "view";
	public static final String KEY_MENU_FONT   = "font";
	public static final String KEY_MENU_FORMAT = "format";
	public static final String KEY_MENU_INSERT = "insert";
	public static final String KEY_MENU_TABLE  = "table";
	public static final String KEY_MENU_FORMS  = "forms";
	public static final String KEY_MENU_SEARCH = "search";
	public static final String KEY_MENU_TOOLS  = "tools";
	public static final String KEY_MENU_HELP   = "help";
	public static final String KEY_MENU_DEBUG  = "debug";

	// Menu & Tool Key Arrays
	protected static Hashtable<String, JMenu>      htMenus = new Hashtable<String, JMenu>();
	protected static Hashtable<String, JComponent> htTools = new Hashtable<String, JComponent>();

	private String appName;
	private final String menuDialog = "..."; /* text to append to a MenuItem label when menu item opens a dialog */

	private final boolean useFormIndicator = true; /* Creates a highlighted background on a new FORM so that it may be more easily edited */
	private final String clrFormIndicator = "#cccccc";

	// System Clipboard Settings
	private Clipboard sysClipboard; // pointer to system clipboard, if available

	private DataFlavor dfPlainText;

	/* Variables */
	private int iSplitPos = 0;

	private boolean exclusiveEdit = true;
	private boolean preserveUnknownTags = false;

	private String lastSearchFindTerm     = null;
	private String lastSearchReplaceTerm  = null;
	private boolean lastSearchCaseSetting = false;
	private boolean lastSearchTopSetting  = false;

	private File currentFile = null;
	private String imageChooserStartDir = ".";

	private int indent = 0;
	private final int indentStep = 4;

	private boolean enterIsBreak = true;

	// File extensions for MutableFilter
	private final String[] extsHTML = { "html", "htm", "shtml" };
	private final String[] extsCSS  = { "css" };
	private final String[] extsIMG  = { "jpg", "jpeg", "png" };
	private final String[] extsRTF  = { "rtf" };
	private final String[] extsB64  = { "b64" };
	private final String[] extsSer  = { "ser" };

	// Control key equivalents on different systems
	private int CTRLKEY = KeyEvent.CTRL_MASK;
	private boolean modified = false;


	public EkittenCore(Builder builder)
	{
		super();

		exclusiveEdit = builder.modeExclusive;
		preserveUnknownTags = builder.keepUnknownTags;
		enterIsBreak = builder.enterBreak;
		this.appName = builder.appName;

		frameHandler = new Frame();

		// Determine if system clipboard is available (SecurityManager version)
/*
		SecurityManager secManager = System.getSecurityManager();
		if(secManager != null)
		{
			try
			{
				secManager.checkSystemClipboardAccess();
				sysClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			}
			catch(SecurityException se)
			{
				sysClipboard = null;
			}
		}
*/

		// Obtain system clipboard if available
		try
		{
			sysClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		}
		catch(Exception ex)
		{
			sysClipboard = null;
		}

		// Plain text DataFlavor for unformatted paste
		try
		{
			dfPlainText = new DataFlavor("text/plain; class=java.lang.String; charset=Unicode"); // Charsets usually available include Unicode, UTF-16, UTF-8, & US-ASCII
		}
		catch(ClassNotFoundException cnfe)
		{
			// it would be nice to use DataFlavor.plainTextFlavor, but that is deprecated
			// this will not work as desired, but it will prevent errors from being thrown later
			// alternately, we could flag up here that Unformatted Paste is not available and adjust the UI accordingly
			// however, the odds of java.lang.String not being found are pretty slim one imagines
			dfPlainText = DataFlavor.stringFlavor;
		}

		/* Localize for language */
		Translatrix.setBundleName("space.leandragem.ekitten.LanguageResources");
		Locale baseLocale = (Locale)null;
		if(builder.lang != null && builder.country != null)
		{
			baseLocale = new Locale(builder.lang, builder.country);
		}
		Translatrix.setLocale(baseLocale);

		/* Initialise system-specific control key value */
		if(!(GraphicsEnvironment.isHeadless()))
		{
			CTRLKEY = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		}

		/* Create the editor kit, document, and stylesheet */
		jtpMain = new JTextPane();
		htmlKit = new ExtendedHTMLEditorKit();
		htmlDoc = (ExtendedHTMLDocument)(htmlKit.createDefaultDocument());
		htmlDoc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
		htmlDoc.setPreservesUnknownTags(preserveUnknownTags);
		styleSheet = htmlDoc.getStyleSheet();
		htmlKit.setDefaultCursor(new Cursor(Cursor.TEXT_CURSOR));
		jtpMain.setCursor(new Cursor(Cursor.TEXT_CURSOR));

		/* Set up the text pane */
		jtpMain.setEditorKit(htmlKit);
		jtpMain.setDocument(htmlDoc);
		jtpMain.setMargin(new Insets(4, 4, 4, 4));
		jtpMain.addKeyListener(this);
		jtpMain.addFocusListener(this);
//		jtpMain.setDragEnabled(true); // this causes an error in older Java versions

		/* Create the source text area */
//		if(sdocSource == null)
//		{
			jtpSource = new JTextArea();
			jtpSource.setText(jtpMain.getText());
//		}
//		else
//		{
//			jtpSource = new JTextArea(sdocSource);
//			jtpMain.setText(jtpSource.getText());
//		}
		jtpSource.setBackground(new Color(212, 212, 212));
		jtpSource.setSelectionColor(new Color(255, 192, 192));
		jtpSource.setMargin(new Insets(4, 4, 4, 4));
		jtpSource.getDocument().addDocumentListener(this);
		jtpSource.addFocusListener(this);
		jtpSource.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		jtpSource.setColumns(1024);

		/* Add CaretListener for tracking caret location events */
		jtpMain.addCaretListener(this::handleCaretPositionChange);

		/* Set up the undo features */
		undoMngr = new UndoManager();
		undoAction = new UndoAction();
		redoAction = new RedoAction();
		jtpMain.getDocument().addUndoableEditListener(new CustomUndoableEditListener());

		/* Insert raw document, if exists */
		if(builder.rawDocument != null && builder.rawDocument.length() > 0)
		{
			if(builder.base64)
			{
				jtpMain.setText(Base64Codec.decode(builder.rawDocument));
			}
			else
			{
				jtpMain.setText(builder.rawDocument);
			}
		}
		jtpMain.setCaretPosition(0);
		jtpMain.getDocument().addDocumentListener(this);

		/* Import CSS from reference, if exists */
		if(builder.urlStyleSheet != null)
		{
			try
			{
				String currDocText = jtpMain.getText();
				htmlDoc = (ExtendedHTMLDocument)(htmlKit.createDefaultDocument());
				htmlDoc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
				htmlDoc.setPreservesUnknownTags(preserveUnknownTags);
				styleSheet = htmlDoc.getStyleSheet();
				BufferedReader br = new BufferedReader(new InputStreamReader(builder.urlStyleSheet.openStream()));
				styleSheet.loadRules(br, builder.urlStyleSheet);
				br.close();
				htmlDoc = new ExtendedHTMLDocument(styleSheet);
				registerDocument(htmlDoc);
				jtpMain.setText(currDocText);
				jtpSource.setText(jtpMain.getText());
			}
			catch(Exception e)
			{
				e.printStackTrace(System.out);
			}
		}

		/* Preload the specified HTML document, if exists */
		if(builder.document != null)
		{
			File defHTML = new File(builder.document);
			if(defHTML.exists())
			{
				try
				{
					openDocument(defHTML);
				}
				catch(Exception e)
				{
					logException("Exception in preloading HTML document", e);
				}
			}
		}

		/* Preload the specified CSS document, if exists */
		if(builder.stylesheet != null)
		{
			File defCSS = new File(builder.stylesheet);
			if(defCSS.exists())
			{
				try
				{
					openStyleSheet(defCSS);
				}
				catch(Exception e)
				{
					logException("Exception in preloading CSS stylesheet", e);
				}
			}
		}

		/* Collect the actions that the JTextPane is naturally aware of */
		Hashtable<Object, Action> actions = new Hashtable<Object, Action>();
		Action[] actionsArray = jtpMain.getActions();
		for(Action a : actionsArray)
		{
			actions.put(a.getValue(Action.NAME), a);
		}

		/* Create shared actions */
		actionFontBold        = new StyledEditorKit.BoldAction();
		actionFontItalic      = new StyledEditorKit.ItalicAction();
		actionFontUnderline   = new StyledEditorKit.UnderlineAction();
		actionFontStrike      = new FormatAction(this, Translatrix.getTranslationString("FontStrike"), HTML.Tag.STRIKE);
		actionFontSuperscript = new FormatAction(this, Translatrix.getTranslationString("FontSuperscript"), HTML.Tag.SUP);
		actionFontSubscript   = new FormatAction(this, Translatrix.getTranslationString("FontSubscript"), HTML.Tag.SUB);
		actionListUnordered   = new ListAutomationAction(this, Translatrix.getTranslationString("ListUnordered"), HTML.Tag.UL);
		actionListOrdered     = new ListAutomationAction(this, Translatrix.getTranslationString("ListOrdered"), HTML.Tag.OL);
		actionSelectFont      = new SetFontFamilyAction(this, "[MENUFONTSELECTOR]");
		actionAlignLeft       = new AlignAction(this, Translatrix.getTranslationString("AlignLeft"), StyleConstants.ALIGN_LEFT);
		actionAlignCenter     = new AlignAction(this, Translatrix.getTranslationString("AlignCenter"), StyleConstants.ALIGN_CENTER);
		actionAlignRight      = new AlignAction(this, Translatrix.getTranslationString("AlignRight"), StyleConstants.ALIGN_RIGHT);
		actionAlignJustified  = new AlignAction(this, Translatrix.getTranslationString("AlignJustified"), StyleConstants.ALIGN_JUSTIFIED);
		actionInsertAnchor    = new CustomAction(this, Translatrix.getTranslationString("InsertAnchor") + menuDialog, HTML.Tag.A);

		/* Build the menus */
		/* FILE Menu */
		jMenuFile              = new JMenu(Translatrix.getTranslationString("File"));
		htMenus.put(KEY_MENU_FILE, jMenuFile);
		JMenuItem jmiNew       = new JMenuItem(Translatrix.getTranslationString("NewDocument"));                     jmiNew.setActionCommand(CMD_DOC_NEW);              jmiNew.addActionListener(this);       jmiNew.setAccelerator(KeyStroke.getKeyStroke('N', CTRLKEY, false));      if(builder.includeMenuIcons) { jmiNew.setIcon(Load.EkitIcon("New")); }; jMenuFile.add(jmiNew);
		JMenuItem jmiNewStyled = new JMenuItem(Translatrix.getTranslationString("NewStyledDocument"));               jmiNewStyled.setActionCommand(CMD_DOC_NEW_STYLED); jmiNewStyled.addActionListener(this); if(builder.includeMenuIcons) { jmiNewStyled.setIcon(Load.EkitIcon("NewStyled")); };   jMenuFile.add(jmiNewStyled);
		JMenuItem jmiOpenHTML  = new JMenuItem(Translatrix.getTranslationString("OpenDocument") + menuDialog);       jmiOpenHTML.setActionCommand(CMD_DOC_OPEN_HTML);   jmiOpenHTML.addActionListener(this);  jmiOpenHTML.setAccelerator(KeyStroke.getKeyStroke('O', CTRLKEY, false)); if(builder.includeMenuIcons) { jmiOpenHTML.setIcon(Load.EkitIcon("Open")); }; jMenuFile.add(jmiOpenHTML);
		JMenuItem jmiOpenCSS   = new JMenuItem(Translatrix.getTranslationString("OpenStyle") + menuDialog);          jmiOpenCSS.setActionCommand(CMD_DOC_OPEN_CSS);     jmiOpenCSS.addActionListener(this);   jMenuFile.add(jmiOpenCSS);
		JMenuItem jmiOpenB64   = new JMenuItem(Translatrix.getTranslationString("OpenBase64Document") + menuDialog); jmiOpenB64.setActionCommand(CMD_DOC_OPEN_BASE64);  jmiOpenB64.addActionListener(this);   jMenuFile.add(jmiOpenB64);
		jMenuFile.addSeparator();
		JMenuItem jmiSave      = new JMenuItem(Translatrix.getTranslationString("Save"));                  jmiSave.setActionCommand(CMD_DOC_SAVE);           jmiSave.addActionListener(this);     jmiSave.setAccelerator(KeyStroke.getKeyStroke('S', CTRLKEY, false)); if(builder.includeMenuIcons) { jmiSave.setIcon(Load.EkitIcon("Save")); }; jMenuFile.add(jmiSave);
		JMenuItem jmiSaveAs    = new JMenuItem(Translatrix.getTranslationString("SaveAs") + menuDialog);   jmiSaveAs.setActionCommand(CMD_DOC_SAVE_AS);      jmiSaveAs.addActionListener(this);   jMenuFile.add(jmiSaveAs);
		JMenuItem jmiSaveBody  = new JMenuItem(Translatrix.getTranslationString("SaveBody") + menuDialog); jmiSaveBody.setActionCommand(CMD_DOC_SAVE_BODY);  jmiSaveBody.addActionListener(this); jMenuFile.add(jmiSaveBody);
		JMenuItem jmiSaveRTF   = new JMenuItem(Translatrix.getTranslationString("SaveRTF") + menuDialog);  jmiSaveRTF.setActionCommand(CMD_DOC_SAVE_RTF);    jmiSaveRTF.addActionListener(this);  jMenuFile.add(jmiSaveRTF);
		JMenuItem jmiSaveB64   = new JMenuItem(Translatrix.getTranslationString("SaveB64") + menuDialog);  jmiSaveB64.setActionCommand(CMD_DOC_SAVE_BASE64); jmiSaveB64.addActionListener(this);  jMenuFile.add(jmiSaveB64);
		jMenuFile.addSeparator();
		JMenuItem jmiPrint     = new JMenuItem(Translatrix.getTranslationString("Print")); jmiPrint.setActionCommand(CMD_DOC_PRINT); jmiPrint.addActionListener(this); jMenuFile.add(jmiPrint);
		jMenuFile.addSeparator();
		JMenuItem jmiSerialOut = new JMenuItem(Translatrix.getTranslationString("Serialize") + menuDialog);   jmiSerialOut.setActionCommand(CMD_DOC_SERIALIZE_OUT); jmiSerialOut.addActionListener(this); jMenuFile.add(jmiSerialOut);
		JMenuItem jmiSerialIn  = new JMenuItem(Translatrix.getTranslationString("ReadFromSer") + menuDialog); jmiSerialIn.setActionCommand(CMD_DOC_SERIALIZE_IN);   jmiSerialIn.addActionListener(this);  jMenuFile.add(jmiSerialIn);
		jMenuFile.addSeparator();
		JMenuItem jmiExit      = new JMenuItem(Translatrix.getTranslationString("Exit")); jmiExit.setActionCommand(CMD_EXIT); jmiExit.addActionListener(this); jMenuFile.add(jmiExit);

		/* EDIT Menu */
		jMenuEdit            = new JMenu(Translatrix.getTranslationString("Edit"));
		htMenus.put(KEY_MENU_EDIT, jMenuEdit);
		if(sysClipboard != null)
		{
			// System Clipboard versions of menu commands
			JMenuItem jmiCut   = new JMenuItem(Translatrix.getTranslationString("Cut"));               jmiCut.setActionCommand(CMD_CLIP_CUT);            jmiCut.addActionListener(this);    jmiCut.setAccelerator(KeyStroke.getKeyStroke('X', CTRLKEY, false));   if(builder.includeMenuIcons) { jmiCut.setIcon(Load.EkitIcon("Cut")); }     jMenuEdit.add(jmiCut);
			JMenuItem jmiCopy  = new JMenuItem(Translatrix.getTranslationString("Copy"));              jmiCopy.setActionCommand(CMD_CLIP_COPY);          jmiCopy.addActionListener(this);   jmiCopy.setAccelerator(KeyStroke.getKeyStroke('C', CTRLKEY, false));  if(builder.includeMenuIcons) { jmiCopy.setIcon(Load.EkitIcon("Copy")); }   jMenuEdit.add(jmiCopy);
			JMenuItem jmiPaste = new JMenuItem(Translatrix.getTranslationString("Paste"));             jmiPaste.setActionCommand(CMD_CLIP_PASTE);        jmiPaste.addActionListener(this);  jmiPaste.setAccelerator(KeyStroke.getKeyStroke('V', CTRLKEY, false)); if(builder.includeMenuIcons) { jmiPaste.setIcon(Load.EkitIcon("Paste")); } jMenuEdit.add(jmiPaste);
			JMenuItem jmiPasteX = new JMenuItem(Translatrix.getTranslationString("PasteUnformatted")); jmiPasteX.setActionCommand(CMD_CLIP_PASTE_PLAIN); jmiPasteX.addActionListener(this); jmiPasteX.setAccelerator(KeyStroke.getKeyStroke('V', CTRLKEY + KeyEvent.SHIFT_MASK, false)); if(builder.includeMenuIcons) { jmiPasteX.setIcon(Load.EkitIcon("PasteUnformatted")); } jMenuEdit.add(jmiPasteX);
		}
		else
		{
			// DefaultEditorKit versions of menu commands
			JMenuItem jmiCut   = new JMenuItem(new DefaultEditorKit.CutAction());   jmiCut.setText(Translatrix.getTranslationString("Cut"));             jmiCut.setAccelerator(KeyStroke.getKeyStroke('X', CTRLKEY, false));   if(builder.includeMenuIcons) { jmiCut.setIcon(Load.EkitIcon("Cut")); }     jMenuEdit.add(jmiCut);
			JMenuItem jmiCopy  = new JMenuItem(new DefaultEditorKit.CopyAction());  jmiCopy.setText(Translatrix.getTranslationString("Copy"));           jmiCopy.setAccelerator(KeyStroke.getKeyStroke('C', CTRLKEY, false));  if(builder.includeMenuIcons) { jmiCopy.setIcon(Load.EkitIcon("Copy")); }   jMenuEdit.add(jmiCopy);
			JMenuItem jmiPaste = new JMenuItem(new DefaultEditorKit.PasteAction()); jmiPaste.setText(Translatrix.getTranslationString("Paste"));         jmiPaste.setAccelerator(KeyStroke.getKeyStroke('V', CTRLKEY, false)); if(builder.includeMenuIcons) { jmiPaste.setIcon(Load.EkitIcon("Paste")); } jMenuEdit.add(jmiPaste);
			JMenuItem jmiPasteX = new JMenuItem(Translatrix.getTranslationString("PasteUnformatted")); jmiPasteX.setActionCommand(CMD_CLIP_PASTE_PLAIN); jmiPasteX.addActionListener(this); jmiPasteX.setAccelerator(KeyStroke.getKeyStroke('V', CTRLKEY + KeyEvent.SHIFT_MASK, false)); if(builder.includeMenuIcons) { jmiPasteX.setIcon(Load.EkitIcon("PasteUnformatted")); } jMenuEdit.add(jmiPasteX);
		}
		jMenuEdit.addSeparator();
		JMenuItem jmiUndo    = new JMenuItem(undoAction); jmiUndo.setAccelerator(KeyStroke.getKeyStroke('Z', CTRLKEY, false)); if(builder.includeMenuIcons) { jmiUndo.setIcon(Load.EkitIcon("Undo")); } jMenuEdit.add(jmiUndo);
		JMenuItem jmiRedo    = new JMenuItem(redoAction); jmiRedo.setAccelerator(KeyStroke.getKeyStroke('Y', CTRLKEY, false)); if(builder.includeMenuIcons) { jmiRedo.setIcon(Load.EkitIcon("Redo")); } jMenuEdit.add(jmiRedo);
		jMenuEdit.addSeparator();
		JMenuItem jmiSelAll  = new JMenuItem((Action)actions.get(DefaultEditorKit.selectAllAction));       jmiSelAll.setText(Translatrix.getTranslationString("SelectAll"));        jmiSelAll.setAccelerator(KeyStroke.getKeyStroke('A', CTRLKEY, false)); jMenuEdit.add(jmiSelAll);
		JMenuItem jmiSelPara = new JMenuItem((Action)actions.get(DefaultEditorKit.selectParagraphAction)); jmiSelPara.setText(Translatrix.getTranslationString("SelectParagraph")); jMenuEdit.add(jmiSelPara);
		JMenuItem jmiSelLine = new JMenuItem((Action)actions.get(DefaultEditorKit.selectLineAction));      jmiSelLine.setText(Translatrix.getTranslationString("SelectLine"));      jMenuEdit.add(jmiSelLine);
		JMenuItem jmiSelWord = new JMenuItem((Action)actions.get(DefaultEditorKit.selectWordAction));      jmiSelWord.setText(Translatrix.getTranslationString("SelectWord"));      jMenuEdit.add(jmiSelWord);
		jMenuEdit.addSeparator();
		JMenu jMenuEnterKey  = new JMenu(Translatrix.getTranslationString("EnterKeyMenu"));
		jcbmiEnterKeyParag   = new JCheckBoxMenuItem(Translatrix.getTranslationString("EnterKeyParag"), !enterIsBreak); jcbmiEnterKeyParag.setActionCommand(CMD_ENTER_PARAGRAPH); jcbmiEnterKeyParag.addActionListener(this); jMenuEnterKey.add(jcbmiEnterKeyParag);
		jcbmiEnterKeyBreak   = new JCheckBoxMenuItem(Translatrix.getTranslationString("EnterKeyBreak"), enterIsBreak);  jcbmiEnterKeyBreak.setActionCommand(CMD_ENTER_BREAK);     jcbmiEnterKeyBreak.addActionListener(this); jMenuEnterKey.add(jcbmiEnterKeyBreak);
		jMenuEdit.add(jMenuEnterKey);

		/* VIEW Menu */
		jMenuView = new JMenu(Translatrix.getTranslationString("View"));
		htMenus.put(KEY_MENU_VIEW, jMenuView);
		if(builder.includeToolBar)
		{
			if(builder.includeMultibar)
			{
				jMenuToolbars = new JMenu(Translatrix.getTranslationString("ViewToolbars"));

				jcbmiViewToolbarMain = new JCheckBoxMenuItem(Translatrix.getTranslationString("ViewToolbarMain"), false);
					jcbmiViewToolbarMain.setActionCommand(CMD_TOGGLE_TOOLBAR_MAIN);
					jcbmiViewToolbarMain.addActionListener(this);
					jMenuToolbars.add(jcbmiViewToolbarMain);

				jcbmiViewToolbarFormat = new JCheckBoxMenuItem(Translatrix.getTranslationString("ViewToolbarFormat"), false);
					jcbmiViewToolbarFormat.setActionCommand(CMD_TOGGLE_TOOLBAR_FORMAT);
					jcbmiViewToolbarFormat.addActionListener(this);
					jMenuToolbars.add(jcbmiViewToolbarFormat);

				jcbmiViewToolbarStyles = new JCheckBoxMenuItem(Translatrix.getTranslationString("ViewToolbarStyles"), false);
					jcbmiViewToolbarStyles.setActionCommand(CMD_TOGGLE_TOOLBAR_STYLES);
					jcbmiViewToolbarStyles.addActionListener(this);
					jMenuToolbars.add(jcbmiViewToolbarStyles);

				jMenuView.add(jMenuToolbars);
			}
			else
			{
				jcbmiViewToolbar = new JCheckBoxMenuItem(Translatrix.getTranslationString("ViewToolbar"), false);
					jcbmiViewToolbar.setActionCommand(CMD_TOGGLE_TOOLBAR_SINGLE);
					jcbmiViewToolbar.addActionListener(this);

				jMenuView.add(jcbmiViewToolbar);
			}
		}
		jcbmiViewSource = new JCheckBoxMenuItem(Translatrix.getTranslationString("ViewSource"), false);  jcbmiViewSource.setActionCommand(CMD_TOGGLE_SOURCE_VIEW);     jcbmiViewSource.addActionListener(this);  jMenuView.add(jcbmiViewSource);

		/* FONT Menu */
		jMenuFont              = new JMenu(Translatrix.getTranslationString("Font"));
		htMenus.put(KEY_MENU_FONT, jMenuFont);
		JMenuItem jmiBold      = new JMenuItem(actionFontBold);      jmiBold.setText(Translatrix.getTranslationString("FontBold"));           jmiBold.setAccelerator(KeyStroke.getKeyStroke('B', CTRLKEY, false));      if(builder.includeMenuIcons) { jmiBold.setIcon(Load.EkitIcon("Bold")); }           jMenuFont.add(jmiBold);
		JMenuItem jmiItalic    = new JMenuItem(actionFontItalic);    jmiItalic.setText(Translatrix.getTranslationString("FontItalic"));       jmiItalic.setAccelerator(KeyStroke.getKeyStroke('I', CTRLKEY, false));    if(builder.includeMenuIcons) { jmiItalic.setIcon(Load.EkitIcon("Italic")); }       jMenuFont.add(jmiItalic);
		JMenuItem jmiUnderline = new JMenuItem(actionFontUnderline); jmiUnderline.setText(Translatrix.getTranslationString("FontUnderline")); jmiUnderline.setAccelerator(KeyStroke.getKeyStroke('U', CTRLKEY, false)); if(builder.includeMenuIcons) { jmiUnderline.setIcon(Load.EkitIcon("Underline")); } jMenuFont.add(jmiUnderline);
		JMenuItem jmiStrike    = new JMenuItem(actionFontStrike);    jmiStrike.setText(Translatrix.getTranslationString("FontStrike"));                                                                                 if(builder.includeMenuIcons) { jmiStrike.setIcon(Load.EkitIcon("Strike")); }       jMenuFont.add(jmiStrike);
		JMenuItem jmiSupscript = new JMenuItem(actionFontSuperscript); if(builder.includeMenuIcons) { jmiSupscript.setIcon(Load.EkitIcon("Super")); } jMenuFont.add(jmiSupscript);
		JMenuItem jmiSubscript = new JMenuItem(actionFontSubscript);   if(builder.includeMenuIcons) { jmiSubscript.setIcon(Load.EkitIcon("Sub")); }   jMenuFont.add(jmiSubscript);
		jMenuFont.addSeparator();
		jMenuFont.add(new JMenuItem(new FormatAction(this, Translatrix.getTranslationString("FormatBig"), HTML.Tag.BIG)));
		jMenuFont.add(new JMenuItem(new FormatAction(this, Translatrix.getTranslationString("FormatSmall"), HTML.Tag.SMALL)));
		JMenu jMenuFontSize = new JMenu(Translatrix.getTranslationString("FontSize"));
			jMenuFontSize.add(new JMenuItem(new StyledEditorKit.FontSizeAction(Translatrix.getTranslationString("FontSize1"), 8)));
			jMenuFontSize.add(new JMenuItem(new StyledEditorKit.FontSizeAction(Translatrix.getTranslationString("FontSize2"), 10)));
			jMenuFontSize.add(new JMenuItem(new StyledEditorKit.FontSizeAction(Translatrix.getTranslationString("FontSize3"), 12)));
			jMenuFontSize.add(new JMenuItem(new StyledEditorKit.FontSizeAction(Translatrix.getTranslationString("FontSize4"), 14)));
			jMenuFontSize.add(new JMenuItem(new StyledEditorKit.FontSizeAction(Translatrix.getTranslationString("FontSize5"), 18)));
			jMenuFontSize.add(new JMenuItem(new StyledEditorKit.FontSizeAction(Translatrix.getTranslationString("FontSize6"), 24)));
			jMenuFontSize.add(new JMenuItem(new StyledEditorKit.FontSizeAction(Translatrix.getTranslationString("FontSize7"), 32)));
		jMenuFont.add(jMenuFontSize);
		jMenuFont.addSeparator();
		JMenu jMenuFontSub      = new JMenu(Translatrix.getTranslationString("Font"));
		JMenuItem jmiSelectFont = new JMenuItem(actionSelectFont);                              jmiSelectFont.setText(Translatrix.getTranslationString("FontSelect") + menuDialog); if(builder.includeMenuIcons) { jmiSelectFont.setIcon(Load.EkitIcon("FontFaces")); }      jMenuFontSub.add(jmiSelectFont);
		JMenuItem jmiSerif      = new JMenuItem((Action)actions.get("font-family-Serif"));      jmiSerif.setText(Translatrix.getTranslationString("FontSerif"));                    jMenuFontSub.add(jmiSerif);
		JMenuItem jmiSansSerif  = new JMenuItem((Action)actions.get("font-family-SansSerif"));  jmiSansSerif.setText(Translatrix.getTranslationString("FontSansserif"));            jMenuFontSub.add(jmiSansSerif);
		JMenuItem jmiMonospaced = new JMenuItem((Action)actions.get("font-family-Monospaced")); jmiMonospaced.setText(Translatrix.getTranslationString("FontMonospaced"));          jMenuFontSub.add(jmiMonospaced);
		jMenuFont.add(jMenuFontSub);
		jMenuFont.addSeparator();
		JMenu jMenuFontColor = new JMenu(Translatrix.getTranslationString("Color"));
			Hashtable<String, String> customAttr = new Hashtable<String, String>(); customAttr.put("color", "black");
			jMenuFontColor.add(new JMenuItem(new CustomAction(this, Translatrix.getTranslationString("CustomColor") + menuDialog, HTML.Tag.FONT, customAttr)));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorAqua"),    new Color(  0,255,255))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorBlack"),   new Color(  0,  0,  0))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorBlue"),    new Color(  0,  0,255))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorFuschia"), new Color(255,  0,255))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorGray"),    new Color(128,128,128))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorGreen"),   new Color(  0,128,  0))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorLime"),    new Color(  0,255,  0))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorMaroon"),  new Color(128,  0,  0))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorNavy"),    new Color(  0,  0,128))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorOlive"),   new Color(128,128,  0))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorPurple"),  new Color(128,  0,128))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorRed"),     new Color(255,  0,  0))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorSilver"),  new Color(192,192,192))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorTeal"),    new Color(  0,128,128))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorWhite"),   new Color(255,255,255))));
			jMenuFontColor.add(new JMenuItem(new StyledEditorKit.ForegroundAction(Translatrix.getTranslationString("ColorYellow"),  new Color(255,255,  0))));
		jMenuFont.add(jMenuFontColor);

		/* FORMAT Menu */
		jMenuFormat            = new JMenu(Translatrix.getTranslationString("Format"));
		htMenus.put(KEY_MENU_FORMAT, jMenuFormat);
		JMenu jMenuFormatAlign = new JMenu(Translatrix.getTranslationString("Align"));
			JMenuItem jmiAlignLeft = new JMenuItem(actionAlignLeft);           if(builder.includeMenuIcons) { jmiAlignLeft.setIcon(Load.EkitIcon("AlignLeft")); };           jMenuFormatAlign.add(jmiAlignLeft);
			JMenuItem jmiAlignCenter = new JMenuItem(actionAlignCenter);       if(builder.includeMenuIcons) { jmiAlignCenter.setIcon(Load.EkitIcon("AlignCenter")); };       jMenuFormatAlign.add(jmiAlignCenter);
			JMenuItem jmiAlignRight = new JMenuItem(actionAlignRight);         if(builder.includeMenuIcons) { jmiAlignRight.setIcon(Load.EkitIcon("AlignRight")); };         jMenuFormatAlign.add(jmiAlignRight);
			JMenuItem jmiAlignJustified = new JMenuItem(actionAlignJustified); if(builder.includeMenuIcons) { jmiAlignJustified.setIcon(Load.EkitIcon("AlignJustified")); }; jMenuFormatAlign.add(jmiAlignJustified);
		jMenuFormat.add(jMenuFormatAlign);
		jMenuFormat.addSeparator();
		JMenu jMenuFormatHeading = new JMenu(Translatrix.getTranslationString("Heading"));
			jMenuFormatHeading.add(new JMenuItem(new FormatAction(this, Translatrix.getTranslationString("Heading1"), HTML.Tag.H1)));
			jMenuFormatHeading.add(new JMenuItem(new FormatAction(this, Translatrix.getTranslationString("Heading2"), HTML.Tag.H2)));
			jMenuFormatHeading.add(new JMenuItem(new FormatAction(this, Translatrix.getTranslationString("Heading3"), HTML.Tag.H3)));
			jMenuFormatHeading.add(new JMenuItem(new FormatAction(this, Translatrix.getTranslationString("Heading4"), HTML.Tag.H4)));
			jMenuFormatHeading.add(new JMenuItem(new FormatAction(this, Translatrix.getTranslationString("Heading5"), HTML.Tag.H5)));
			jMenuFormatHeading.add(new JMenuItem(new FormatAction(this, Translatrix.getTranslationString("Heading6"), HTML.Tag.H6)));
		jMenuFormat.add(jMenuFormatHeading);
		jMenuFormat.addSeparator();
		JMenuItem jmiUList = new JMenuItem(actionListUnordered); if(builder.includeMenuIcons) { jmiUList.setIcon(Load.EkitIcon("UList")); } jMenuFormat.add(jmiUList);
		JMenuItem jmiOList = new JMenuItem(actionListOrdered);   if(builder.includeMenuIcons) { jmiOList.setIcon(Load.EkitIcon("OList")); } jMenuFormat.add(jmiOList);
		jMenuFormat.add(new JMenuItem(new FormatAction(this, Translatrix.getTranslationString("ListItem"), HTML.Tag.LI)));
		jMenuFormat.addSeparator();
		jMenuFormat.add(new JMenuItem(new FormatAction(this, Translatrix.getTranslationString("FormatBlockquote"), HTML.Tag.BLOCKQUOTE)));
		jMenuFormat.add(new JMenuItem(new FormatAction(this, Translatrix.getTranslationString("FormatPre"), HTML.Tag.PRE)));
		jMenuFormat.add(new JMenuItem(new FormatAction(this, Translatrix.getTranslationString("FormatStrong"), HTML.Tag.STRONG)));
		jMenuFormat.add(new JMenuItem(new FormatAction(this, Translatrix.getTranslationString("FormatEmphasis"), HTML.Tag.EM)));
		jMenuFormat.add(new JMenuItem(new FormatAction(this, Translatrix.getTranslationString("FormatTT"), HTML.Tag.TT)));
		jMenuFormat.add(new JMenuItem(new FormatAction(this, Translatrix.getTranslationString("FormatSpan"), HTML.Tag.SPAN)));

		/* INSERT Menu */
		jMenuInsert               = new JMenu(Translatrix.getTranslationString("Insert"));
		htMenus.put(KEY_MENU_INSERT, jMenuInsert);
		JMenuItem jmiInsertAnchor = new JMenuItem(actionInsertAnchor); if(builder.includeMenuIcons) { jmiInsertAnchor.setIcon(Load.EkitIcon("Anchor")); }; jMenuInsert.add(jmiInsertAnchor);
		JMenuItem jmiBreak        = new JMenuItem(Translatrix.getTranslationString("InsertBreak"));  jmiBreak.setActionCommand(CMD_INSERT_BREAK);   jmiBreak.addActionListener(this);   jmiBreak.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_MASK, false)); jMenuInsert.add(jmiBreak);
		JMenuItem jmiNBSP         = new JMenuItem(Translatrix.getTranslationString("InsertNBSP"));   jmiNBSP.setActionCommand(CMD_INSERT_NBSP);     jmiNBSP.addActionListener(this);    jmiNBSP.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.SHIFT_MASK, false)); jMenuInsert.add(jmiNBSP);
		JMenu jMenuUnicode        = new JMenu(Translatrix.getTranslationString("InsertUnicodeCharacter")); if(builder.includeMenuIcons) { jMenuUnicode.setIcon(Load.EkitIcon("Unicode")); };
		JMenuItem jmiUnicodeAll   = new JMenuItem(Translatrix.getTranslationString("InsertUnicodeCharacterAll") + menuDialog);  if(builder.includeMenuIcons) { jmiUnicodeAll.setIcon(Load.EkitIcon("Unicode")); }; jmiUnicodeAll.setActionCommand(CMD_INSERT_UNICODE_CHAR);      jmiUnicodeAll.addActionListener(this);   jMenuUnicode.add(jmiUnicodeAll);
		JMenuItem jmiUnicodeMath  = new JMenuItem(Translatrix.getTranslationString("InsertUnicodeCharacterMath") + menuDialog); if(builder.includeMenuIcons) { jmiUnicodeMath.setIcon(Load.EkitIcon("Math")); };   jmiUnicodeMath.setActionCommand(CMD_INSERT_UNICODE_MATH); jmiUnicodeMath.addActionListener(this);  jMenuUnicode.add(jmiUnicodeMath);
		JMenuItem jmiUnicodeDraw  = new JMenuItem(Translatrix.getTranslationString("InsertUnicodeCharacterDraw") + menuDialog); if(builder.includeMenuIcons) { jmiUnicodeDraw.setIcon(Load.EkitIcon("Draw")); };   jmiUnicodeDraw.setActionCommand(CMD_INSERT_UNICODE_DRAW); jmiUnicodeDraw.addActionListener(this);  jMenuUnicode.add(jmiUnicodeDraw);
		JMenuItem jmiUnicodeDing  = new JMenuItem(Translatrix.getTranslationString("InsertUnicodeCharacterDing") + menuDialog); jmiUnicodeDing.setActionCommand(CMD_INSERT_UNICODE_DING); jmiUnicodeDing.addActionListener(this);  jMenuUnicode.add(jmiUnicodeDing);
		JMenuItem jmiUnicodeSigs  = new JMenuItem(Translatrix.getTranslationString("InsertUnicodeCharacterSigs") + menuDialog); jmiUnicodeSigs.setActionCommand(CMD_INSERT_UNICODE_SIGS); jmiUnicodeSigs.addActionListener(this);  jMenuUnicode.add(jmiUnicodeSigs);
		JMenuItem jmiUnicodeSpec  = new JMenuItem(Translatrix.getTranslationString("InsertUnicodeCharacterSpec") + menuDialog); jmiUnicodeSpec.setActionCommand(CMD_INSERT_UNICODE_SPEC); jmiUnicodeSpec.addActionListener(this);  jMenuUnicode.add(jmiUnicodeSpec);
		jMenuInsert.add(jMenuUnicode);
		JMenuItem jmiHRule        = new JMenuItem(Translatrix.getTranslationString("InsertHorizontalRule")); jmiHRule.setActionCommand(CMD_INSERT_HR); jmiHRule.addActionListener(this); jMenuInsert.add(jmiHRule);
		jMenuInsert.addSeparator();
		if(!builder.isParentApplet)
		{
			JMenuItem jmiImageLocal = new JMenuItem(Translatrix.getTranslationString("InsertLocalImage") + menuDialog);  jmiImageLocal.setActionCommand(CMD_INSERT_IMAGE_LOCAL); jmiImageLocal.addActionListener(this); jMenuInsert.add(jmiImageLocal);
		}
		JMenuItem jmiImageURL     = new JMenuItem(Translatrix.getTranslationString("InsertURLImage") + menuDialog);    jmiImageURL.setActionCommand(CMD_INSERT_IMAGE_URL);     jmiImageURL.addActionListener(this);   jMenuInsert.add(jmiImageURL);

		/* TABLE Menu */
		jMenuTable              = new JMenu(Translatrix.getTranslationString("Table"));
		htMenus.put(KEY_MENU_TABLE, jMenuTable);
		JMenuItem jmiTable       = new JMenuItem(Translatrix.getTranslationString("InsertTable") + menuDialog); if(builder.includeMenuIcons) { jmiTable.setIcon(Load.EkitIcon("TableCreate")); }; jmiTable.setActionCommand(CMD_TABLE_INSERT);             jmiTable.addActionListener(this);       jMenuTable.add(jmiTable);
		jMenuTable.addSeparator();
		JMenuItem jmiEditTable	 = new JMenuItem(Translatrix.getTranslationString("TableEdit") + menuDialog); if(builder.includeMenuIcons) { jmiEditTable.setIcon(Load.EkitIcon("TableEdit")); } jmiEditTable.setActionCommand(CMD_TABLE_EDIT);	jmiEditTable.addActionListener(this);	jMenuTable.add(jmiEditTable);
		JMenuItem jmiEditCell	 = new JMenuItem(Translatrix.getTranslationString("TableCellEdit") + menuDialog); if(builder.includeMenuIcons) { jmiEditCell.setIcon(Load.EkitIcon("CellEdit")); } jmiEditCell.setActionCommand(CMD_TABLE_CELL_EDIT);	jmiEditCell.addActionListener(this);	jMenuTable.add(jmiEditCell);
		jMenuTable.addSeparator();
		JMenuItem jmiTableRow    = new JMenuItem(Translatrix.getTranslationString("InsertTableRow"));           if(builder.includeMenuIcons) { jmiTableRow.setIcon(Load.EkitIcon("InsertRow")); }; jmiTableRow.setActionCommand(CMD_TABLE_ROW_INSERT);       jmiTableRow.addActionListener(this);    jMenuTable.add(jmiTableRow);
		JMenuItem jmiTableCol    = new JMenuItem(Translatrix.getTranslationString("InsertTableColumn"));        if(builder.includeMenuIcons) { jmiTableCol.setIcon(Load.EkitIcon("InsertColumn")); }; jmiTableCol.setActionCommand(CMD_TABLE_COLUMN_INSERT);    jmiTableCol.addActionListener(this);    jMenuTable.add(jmiTableCol);
		jMenuTable.addSeparator();
		JMenuItem jmiTableRowDel = new JMenuItem(Translatrix.getTranslationString("DeleteTableRow"));           if(builder.includeMenuIcons) { jmiTableRowDel.setIcon(Load.EkitIcon("DeleteRow")); }; jmiTableRowDel.setActionCommand(CMD_TABLE_ROW_DELETE);    jmiTableRowDel.addActionListener(this); jMenuTable.add(jmiTableRowDel);
		JMenuItem jmiTableColDel = new JMenuItem(Translatrix.getTranslationString("DeleteTableColumn"));        if(builder.includeMenuIcons) { jmiTableColDel.setIcon(Load.EkitIcon("DeleteColumn")); }; jmiTableColDel.setActionCommand(CMD_TABLE_COLUMN_DELETE); jmiTableColDel.addActionListener(this); jMenuTable.add(jmiTableColDel);

		/* FORMS Menu */
		jMenuForms                    = new JMenu(Translatrix.getTranslationString("Forms"));
		htMenus.put(KEY_MENU_FORMS, jMenuForms);
		JMenuItem jmiFormInsertForm   = new JMenuItem(Translatrix.getTranslationString("FormInsertForm")); jmiFormInsertForm.setActionCommand(CMD_FORM_INSERT);     jmiFormInsertForm.addActionListener(this); jMenuForms.add(jmiFormInsertForm);
		jMenuForms.addSeparator();
		JMenuItem jmiFormTextfield    = new JMenuItem(Translatrix.getTranslationString("FormTextfield"));  jmiFormTextfield.setActionCommand(CMD_FORM_TEXTFIELD); jmiFormTextfield.addActionListener(this);  jMenuForms.add(jmiFormTextfield);
		JMenuItem jmiFormTextarea     = new JMenuItem(Translatrix.getTranslationString("FormTextarea"));   jmiFormTextarea.setActionCommand(CMD_FORM_TEXTAREA);   jmiFormTextarea.addActionListener(this);   jMenuForms.add(jmiFormTextarea);
		JMenuItem jmiFormCheckbox     = new JMenuItem(Translatrix.getTranslationString("FormCheckbox"));   jmiFormCheckbox.setActionCommand(CMD_FORM_CHECKBOX);   jmiFormCheckbox.addActionListener(this);   jMenuForms.add(jmiFormCheckbox);
		JMenuItem jmiFormRadio        = new JMenuItem(Translatrix.getTranslationString("FormRadio"));      jmiFormRadio.setActionCommand(CMD_FORM_RADIO);   jmiFormRadio.addActionListener(this);      jMenuForms.add(jmiFormRadio);
		JMenuItem jmiFormPassword     = new JMenuItem(Translatrix.getTranslationString("FormPassword"));   jmiFormPassword.setActionCommand(CMD_FORM_PASSWORD);   jmiFormPassword.addActionListener(this);   jMenuForms.add(jmiFormPassword);
		jMenuForms.addSeparator();
		JMenuItem jmiFormButton       = new JMenuItem(Translatrix.getTranslationString("FormButton"));       jmiFormButton.setActionCommand(CMD_FORM_BUTTON);             jmiFormButton.addActionListener(this);       jMenuForms.add(jmiFormButton);
		JMenuItem jmiFormButtonSubmit = new JMenuItem(Translatrix.getTranslationString("FormButtonSubmit")); jmiFormButtonSubmit.setActionCommand(CMD_FORM_SUBMIT); jmiFormButtonSubmit.addActionListener(this); jMenuForms.add(jmiFormButtonSubmit);
		JMenuItem jmiFormButtonReset  = new JMenuItem(Translatrix.getTranslationString("FormButtonReset"));  jmiFormButtonReset.setActionCommand(CMD_FORM_RESET);   jmiFormButtonReset.addActionListener(this);  jMenuForms.add(jmiFormButtonReset);

		/* TOOLS Menu */
		if(builder.spellCheck)
		{
			jMenuTools = new JMenu(Translatrix.getTranslationString("Tools"));
			htMenus.put(KEY_MENU_TOOLS, jMenuTools);
			JMenuItem jmiSpellcheck = new JMenuItem(Translatrix.getTranslationString("ToolSpellcheck")); jmiSpellcheck.setActionCommand(CMD_SPELLCHECK); jmiSpellcheck.addActionListener(this); jMenuTools.add(jmiSpellcheck);
		}

		/* SEARCH Menu */
		jMenuSearch            = new JMenu(Translatrix.getTranslationString("Search"));
		htMenus.put(KEY_MENU_SEARCH, jMenuSearch);
		JMenuItem jmiFind      = new JMenuItem(Translatrix.getTranslationString("SearchFind"));      if(builder.includeMenuIcons) { jmiFind.setIcon(Load.EkitIcon("Find")); };           jmiFind.setActionCommand(CMD_SEARCH_FIND);           jmiFind.addActionListener(this);      jmiFind.setAccelerator(KeyStroke.getKeyStroke('F', CTRLKEY, false));      jMenuSearch.add(jmiFind);
		JMenuItem jmiFindAgain = new JMenuItem(Translatrix.getTranslationString("SearchFindAgain")); if(builder.includeMenuIcons) { jmiFindAgain.setIcon(Load.EkitIcon("FindAgain")); }; jmiFindAgain.setActionCommand(CMD_SEARCH_FIND_AGAIN); jmiFindAgain.addActionListener(this); jmiFindAgain.setAccelerator(KeyStroke.getKeyStroke('G', CTRLKEY, false)); jMenuSearch.add(jmiFindAgain);
		JMenuItem jmiReplace   = new JMenuItem(Translatrix.getTranslationString("SearchReplace"));   if(builder.includeMenuIcons) { jmiReplace.setIcon(Load.EkitIcon("Replace")); };     jmiReplace.setActionCommand(CMD_SEARCH_REPLACE);     jmiReplace.addActionListener(this);   jmiReplace.setAccelerator(KeyStroke.getKeyStroke('R', CTRLKEY, false));   jMenuSearch.add(jmiReplace);

		/* HELP Menu */
		jMenuHelp = new JMenu(Translatrix.getTranslationString("Help"));
		htMenus.put(KEY_MENU_HELP, jMenuHelp);
		JMenuItem jmiAbout = new JMenuItem(Translatrix.getTranslationString("About")); jmiAbout.setActionCommand(CMD_HELP_ABOUT); jmiAbout.addActionListener(this); jMenuHelp.add(jmiAbout);

		/* DEBUG Menu */
		jMenuDebug           = new JMenu(Translatrix.getTranslationString("Debug"));
		htMenus.put(KEY_MENU_DEBUG, jMenuDebug);
		JMenuItem jmiDesc    = new JMenuItem(Translatrix.getTranslationString("DescribeDoc")); jmiDesc.setActionCommand(CMD_DEBUG_DESCRIBE_DOC);       jmiDesc.addActionListener(this);    jMenuDebug.add(jmiDesc);
		JMenuItem jmiDescCSS = new JMenuItem(Translatrix.getTranslationString("DescribeCSS")); jmiDescCSS.setActionCommand(CMD_DEBUG_DESCRIBE_CSS); jmiDescCSS.addActionListener(this); jMenuDebug.add(jmiDescCSS);
		JMenuItem jmiTag     = new JMenuItem(Translatrix.getTranslationString("WhatTags"));    jmiTag.setActionCommand(CMD_DEBUG_CURRENT_TAGS);        jmiTag.addActionListener(this);     jMenuDebug.add(jmiTag);

		/* Create menubar and add menus */
		jMenuBar = new JMenuBar();
		jMenuBar.add(jMenuFile);
		jMenuBar.add(jMenuEdit);
		jMenuBar.add(jMenuView);
		jMenuBar.add(jMenuFont);
		jMenuBar.add(jMenuFormat);
		jMenuBar.add(jMenuSearch);
		jMenuBar.add(jMenuInsert);
		jMenuBar.add(jMenuTable);
		jMenuBar.add(jMenuForms);
		if(jMenuTools != null) { jMenuBar.add(jMenuTools); }
		jMenuBar.add(jMenuHelp);
		if(builder.debugOn)
		{
			jMenuBar.add(jMenuDebug);
		}

		/* Create toolbar tool objects */
		jbtnNewHTML = new JButtonNoFocus(Load.EkitIcon("New"));
			jbtnNewHTML.setToolTipText(Translatrix.getTranslationString("NewDocument"));
			jbtnNewHTML.setActionCommand(CMD_DOC_NEW);
			jbtnNewHTML.addActionListener(this);
			htTools.put(KEY_TOOL_NEW, jbtnNewHTML);
		jbtnNewStyledHTML = new JButtonNoFocus(Load.EkitIcon("NewStyled"));
			jbtnNewStyledHTML.setToolTipText(Translatrix.getTranslationString("NewStyledDocument"));
			jbtnNewStyledHTML.setActionCommand(CMD_DOC_NEW_STYLED);
			jbtnNewStyledHTML.addActionListener(this);
			htTools.put(KEY_TOOL_NEWSTYLED, jbtnNewStyledHTML);
		jbtnOpenHTML = new JButtonNoFocus(Load.EkitIcon("Open"));
			jbtnOpenHTML.setToolTipText(Translatrix.getTranslationString("OpenDocument"));
			jbtnOpenHTML.setActionCommand(CMD_DOC_OPEN_HTML);
			jbtnOpenHTML.addActionListener(this);
			htTools.put(KEY_TOOL_OPEN, jbtnOpenHTML);
		jbtnSaveHTML = new JButtonNoFocus(Load.EkitIcon("Save"));
			jbtnSaveHTML.setToolTipText(Translatrix.getTranslationString("SaveDocument"));
			jbtnSaveHTML.setActionCommand(CMD_DOC_SAVE_AS);
			jbtnSaveHTML.addActionListener(this);
			htTools.put(KEY_TOOL_SAVE, jbtnSaveHTML);
		jbtnPrint = new JButtonNoFocus(Load.EkitIcon("Print"));
			jbtnPrint.setToolTipText(Translatrix.getTranslationString("PrintDocument"));
			jbtnPrint.setActionCommand(CMD_DOC_PRINT);
			jbtnPrint.addActionListener(this);
			htTools.put(KEY_TOOL_PRINT, jbtnPrint);
//		jbtnCut = new JButtonNoFocus(new DefaultEditorKit.CutAction());
		jbtnCut = new JButtonNoFocus();
			jbtnCut.setActionCommand(CMD_CLIP_CUT);
			jbtnCut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					EkittenCore.this.actionPerformed(evt);
				}
			});
			jbtnCut.setIcon(Load.EkitIcon("Cut"));
			jbtnCut.setText(null);
			jbtnCut.setToolTipText(Translatrix.getTranslationString("Cut"));
			htTools.put(KEY_TOOL_CUT, jbtnCut);
//		jbtnCopy = new JButtonNoFocus(new DefaultEditorKit.CopyAction());
		jbtnCopy = new JButtonNoFocus();
			jbtnCopy.setActionCommand(CMD_CLIP_COPY);
			jbtnCopy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					EkittenCore.this.actionPerformed(evt);
				}
			});
			jbtnCopy.setIcon(Load.EkitIcon("Copy"));
			jbtnCopy.setText(null);
			jbtnCopy.setToolTipText(Translatrix.getTranslationString("Copy"));
			htTools.put(KEY_TOOL_COPY, jbtnCopy);
//		jbtnPaste = new JButtonNoFocus(new DefaultEditorKit.PasteAction());
		jbtnPaste = new JButtonNoFocus();
			jbtnPaste.setActionCommand(CMD_CLIP_PASTE);
			jbtnPaste.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					EkittenCore.this.actionPerformed(evt);
				}
			});
			jbtnPaste.setIcon(Load.EkitIcon("Paste"));
			jbtnPaste.setText(null);
			jbtnPaste.setToolTipText(Translatrix.getTranslationString("Paste"));
			htTools.put(KEY_TOOL_PASTE, jbtnPaste);
		jbtnPasteX = new JButtonNoFocus();
			jbtnPasteX.setActionCommand(CMD_CLIP_PASTE_PLAIN);
			jbtnPasteX.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					EkittenCore.this.actionPerformed(evt);
				}
			});
			jbtnPasteX.setIcon(Load.EkitIcon("PasteUnformatted"));
			jbtnPasteX.setText(null);
			jbtnPasteX.setToolTipText(Translatrix.getTranslationString("PasteUnformatted"));
			htTools.put(KEY_TOOL_PASTEX, jbtnPasteX);
		jbtnUndo = new JButtonNoFocus(undoAction);
			jbtnUndo.setIcon(Load.EkitIcon("Undo"));
			jbtnUndo.setText(null);
			jbtnUndo.setToolTipText(Translatrix.getTranslationString("Undo"));
			htTools.put(KEY_TOOL_UNDO, jbtnUndo);
		jbtnRedo = new JButtonNoFocus(redoAction);
			jbtnRedo.setIcon(Load.EkitIcon("Redo"));
			jbtnRedo.setText(null);
			jbtnRedo.setToolTipText(Translatrix.getTranslationString("Redo"));
			htTools.put(KEY_TOOL_REDO, jbtnRedo);
		jbtnBold = new JButtonNoFocus(actionFontBold);
			jbtnBold.setIcon(Load.EkitIcon("Bold"));
			jbtnBold.setText(null);
			jbtnBold.setToolTipText(Translatrix.getTranslationString("FontBold"));
			htTools.put(KEY_TOOL_BOLD, jbtnBold);
		jbtnItalic = new JButtonNoFocus(actionFontItalic);
			jbtnItalic.setIcon(Load.EkitIcon("Italic"));
			jbtnItalic.setText(null);
			jbtnItalic.setToolTipText(Translatrix.getTranslationString("FontItalic"));
			htTools.put(KEY_TOOL_ITALIC, jbtnItalic);
		jbtnUnderline = new JButtonNoFocus(actionFontUnderline);
			jbtnUnderline.setIcon(Load.EkitIcon("Underline"));
			jbtnUnderline.setText(null);
			jbtnUnderline.setToolTipText(Translatrix.getTranslationString("FontUnderline"));
			htTools.put(KEY_TOOL_UNDERLINE, jbtnUnderline);
		jbtnStrike = new JButtonNoFocus(actionFontStrike);
			jbtnStrike.setIcon(Load.EkitIcon("Strike"));
			jbtnStrike.setText(null);
			jbtnStrike.setToolTipText(Translatrix.getTranslationString("FontStrike"));
			htTools.put(KEY_TOOL_STRIKE, jbtnStrike);
		jbtnSuperscript = new JButtonNoFocus(actionFontSuperscript);
			jbtnSuperscript.setIcon(Load.EkitIcon("Super"));
			jbtnSuperscript.setText(null);
			jbtnSuperscript.setToolTipText(Translatrix.getTranslationString("FontSuperscript"));
			htTools.put(KEY_TOOL_SUPER, jbtnSuperscript);
		jbtnSubscript = new JButtonNoFocus(actionFontSubscript);
			jbtnSubscript.setIcon(Load.EkitIcon("Sub"));
			jbtnSubscript.setText(null);
			jbtnSubscript.setToolTipText(Translatrix.getTranslationString("FontSubscript"));
			htTools.put(KEY_TOOL_SUB, jbtnSubscript);
		jbtnUList = new JButtonNoFocus(actionListUnordered);
			jbtnUList.setIcon(Load.EkitIcon("UList"));
			jbtnUList.setText(null);
			jbtnUList.setToolTipText(Translatrix.getTranslationString("ListUnordered"));
			htTools.put(KEY_TOOL_ULIST, jbtnUList);
		jbtnOList = new JButtonNoFocus(actionListOrdered);
			jbtnOList.setIcon(Load.EkitIcon("OList"));
			jbtnOList.setText(null);
			jbtnOList.setToolTipText(Translatrix.getTranslationString("ListOrdered"));
			htTools.put(KEY_TOOL_OLIST, jbtnOList);
		jbtnAlignLeft = new JButtonNoFocus(actionAlignLeft);
			jbtnAlignLeft.setIcon(Load.EkitIcon("AlignLeft"));
			jbtnAlignLeft.setText(null);
			jbtnAlignLeft.setToolTipText(Translatrix.getTranslationString("AlignLeft"));
			htTools.put(KEY_TOOL_ALIGNL, jbtnAlignLeft);
		jbtnAlignCenter = new JButtonNoFocus(actionAlignCenter);
			jbtnAlignCenter.setIcon(Load.EkitIcon("AlignCenter"));
			jbtnAlignCenter.setText(null);
			jbtnAlignCenter.setToolTipText(Translatrix.getTranslationString("AlignCenter"));
			htTools.put(KEY_TOOL_ALIGNC, jbtnAlignCenter);
		jbtnAlignRight = new JButtonNoFocus(actionAlignRight);
			jbtnAlignRight.setIcon(Load.EkitIcon("AlignRight"));
			jbtnAlignRight.setText(null);
			jbtnAlignRight.setToolTipText(Translatrix.getTranslationString("AlignRight"));
			htTools.put(KEY_TOOL_ALIGNR, jbtnAlignRight);
		jbtnAlignJustified = new JButtonNoFocus(actionAlignJustified);
			jbtnAlignJustified.setIcon(Load.EkitIcon("AlignJustified"));
			jbtnAlignJustified.setText(null);
			jbtnAlignJustified.setToolTipText(Translatrix.getTranslationString("AlignJustified"));
			htTools.put(KEY_TOOL_ALIGNJ, jbtnAlignJustified);
		jbtnUnicode = new JButtonNoFocus();
			jbtnUnicode.setActionCommand(CMD_INSERT_UNICODE_CHAR);
			jbtnUnicode.addActionListener(this);
			jbtnUnicode.setIcon(Load.EkitIcon("Unicode"));
			jbtnUnicode.setText(null);
			jbtnUnicode.setToolTipText(Translatrix.getTranslationString("ToolUnicode"));
			htTools.put(KEY_TOOL_UNICODE, jbtnUnicode);
		jbtnUnicodeMath = new JButtonNoFocus();
			jbtnUnicodeMath.setActionCommand(CMD_INSERT_UNICODE_MATH);
			jbtnUnicodeMath.addActionListener(this);
			jbtnUnicodeMath.setIcon(Load.EkitIcon("Math"));
			jbtnUnicodeMath.setText(null);
			jbtnUnicodeMath.setToolTipText(Translatrix.getTranslationString("ToolUnicodeMath"));
			htTools.put(KEY_TOOL_UNIMATH, jbtnUnicodeMath);
		jbtnFind = new JButtonNoFocus();
			jbtnFind.setActionCommand(CMD_SEARCH_FIND);
			jbtnFind.addActionListener(this);
			jbtnFind.setIcon(Load.EkitIcon("Find"));
			jbtnFind.setText(null);
			jbtnFind.setToolTipText(Translatrix.getTranslationString("SearchFind"));
			htTools.put(KEY_TOOL_FIND, jbtnFind);
		jbtnAnchor = new JButtonNoFocus(actionInsertAnchor);
			jbtnAnchor.setIcon(Load.EkitIcon("Anchor"));
			jbtnAnchor.setText(null);
			jbtnAnchor.setToolTipText(Translatrix.getTranslationString("ToolAnchor"));
			htTools.put(KEY_TOOL_ANCHOR, jbtnAnchor);
		jtbtnViewSource = new JToggleButtonNoFocus(Load.EkitIcon("Source"));
			jtbtnViewSource.setText(null);
			jtbtnViewSource.setToolTipText(Translatrix.getTranslationString("ViewSource"));
			jtbtnViewSource.setActionCommand(CMD_TOGGLE_SOURCE_VIEW);
			jtbtnViewSource.addActionListener(this);
			jtbtnViewSource.setPreferredSize(jbtnAnchor.getPreferredSize());
			jtbtnViewSource.setMinimumSize(jbtnAnchor.getMinimumSize());
			jtbtnViewSource.setMaximumSize(jbtnAnchor.getMaximumSize());
			htTools.put(KEY_TOOL_SOURCE, jtbtnViewSource);
		jcmbStyleSelector = new JComboBoxNoFocus();
			jcmbStyleSelector.setToolTipText(Translatrix.getTranslationString("PickCSSStyle"));
			jcmbStyleSelector.setAction(new StylesAction(jcmbStyleSelector));
			htTools.put(KEY_TOOL_STYLES, jcmbStyleSelector);
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
			Vector<String> vcFontnames = new Vector<String>(fonts.length + 1);
			vcFontnames.add(Translatrix.getTranslationString("SelectorToolFontsDefaultFont"));
			for(String fontname : fonts)
			{
				vcFontnames.add(fontname);
			}
			Collections.sort(vcFontnames);
			jcmbFontSelector = new JComboBoxNoFocus(vcFontnames);
			jcmbFontSelector.setAction(new SetFontFamilyAction(this, "[EKITFONTSELECTOR]"));
			htTools.put(KEY_TOOL_FONTS, jcmbFontSelector);
		jbtnInsertTable = new JButtonNoFocus();
			jbtnInsertTable.setActionCommand(CMD_TABLE_INSERT);
			jbtnInsertTable.addActionListener(this);
			jbtnInsertTable.setIcon(Load.EkitIcon("TableCreate"));
			jbtnInsertTable.setText(null);
			jbtnInsertTable.setToolTipText(Translatrix.getTranslationString("InsertTable"));
			htTools.put(KEY_TOOL_INSTABLE, jbtnInsertTable);
		jbtnEditTable = new JButtonNoFocus();
			jbtnEditTable.setActionCommand(CMD_TABLE_EDIT);
			jbtnEditTable.addActionListener(this);
			jbtnEditTable.setIcon(Load.EkitIcon("TableEdit"));
			jbtnEditTable.setText(null);
			jbtnEditTable.setToolTipText(Translatrix.getTranslationString("TableEdit"));
			htTools.put(KEY_TOOL_EDITTABLE, jbtnEditTable);
		jbtnEditCell = new JButtonNoFocus();
			jbtnEditCell.setActionCommand(CMD_TABLE_CELL_EDIT);
			jbtnEditCell.addActionListener(this);
			jbtnEditCell.setIcon(Load.EkitIcon("CellEdit"));
			jbtnEditCell.setText(null);
			jbtnEditCell.setToolTipText(Translatrix.getTranslationString("TableCellEdit"));
			htTools.put(KEY_TOOL_EDITCELL, jbtnEditCell);			
		jbtnInsertRow = new JButtonNoFocus();
			jbtnInsertRow.setActionCommand(CMD_TABLE_ROW_INSERT);
			jbtnInsertRow.addActionListener(this);
			jbtnInsertRow.setIcon(Load.EkitIcon("InsertRow"));
			jbtnInsertRow.setText(null);
			jbtnInsertRow.setToolTipText(Translatrix.getTranslationString("InsertTableRow"));
			htTools.put(KEY_TOOL_INSERTROW, jbtnInsertRow);
		jbtnInsertColumn = new JButtonNoFocus();
			jbtnInsertColumn.setActionCommand(CMD_TABLE_COLUMN_INSERT);
			jbtnInsertColumn.addActionListener(this);
			jbtnInsertColumn.setIcon(Load.EkitIcon("InsertColumn"));
			jbtnInsertColumn.setText(null);
			jbtnInsertColumn.setToolTipText(Translatrix.getTranslationString("InsertTableColumn"));
			htTools.put(KEY_TOOL_INSERTCOL, jbtnInsertColumn);
		jbtnDeleteRow = new JButtonNoFocus();
			jbtnDeleteRow.setActionCommand(CMD_TABLE_ROW_DELETE);
			jbtnDeleteRow.addActionListener(this);
			jbtnDeleteRow.setIcon(Load.EkitIcon("DeleteRow"));
			jbtnDeleteRow.setText(null);
			jbtnDeleteRow.setToolTipText(Translatrix.getTranslationString("DeleteTableRow"));
			htTools.put(KEY_TOOL_DELETEROW, jbtnDeleteRow);
		jbtnDeleteColumn = new JButtonNoFocus();
			jbtnDeleteColumn.setActionCommand(CMD_TABLE_COLUMN_DELETE);
			jbtnDeleteColumn.addActionListener(this);
			jbtnDeleteColumn.setIcon(Load.EkitIcon("DeleteColumn"));
			jbtnDeleteColumn.setText(null);
			jbtnDeleteColumn.setToolTipText(Translatrix.getTranslationString("DeleteTableColumn"));
			htTools.put(KEY_TOOL_DELETECOL, jbtnDeleteColumn);

		/* Create the toolbar */
		if(builder.includeMultibar)
		{
			jToolBarMain = new JToolBar(JToolBar.HORIZONTAL);
			jToolBarMain.setFloatable(false);
			jToolBarFormat = new JToolBar(JToolBar.HORIZONTAL);
			jToolBarFormat.setFloatable(false);
			jToolBarStyles = new JToolBar(JToolBar.HORIZONTAL);
			jToolBarStyles.setFloatable(false);

			initializeMultiToolbars(TOOLBAR_DEFAULT_MULTI);

			// fix the weird size preference of toggle buttons
			jtbtnViewSource.setPreferredSize(jbtnAnchor.getPreferredSize());
			jtbtnViewSource.setMinimumSize(jbtnAnchor.getMinimumSize());
			jtbtnViewSource.setMaximumSize(jbtnAnchor.getMaximumSize());
		}
		else if(builder.includeToolBar)
		{
			jToolBar = new JToolBar(JToolBar.HORIZONTAL);
			jToolBar.setFloatable(false);

			initializeSingleToolbar(TOOLBAR_DEFAULT_SINGLE);

			// fix the weird size preference of toggle buttons
			jtbtnViewSource.setPreferredSize(jbtnAnchor.getPreferredSize());
			jtbtnViewSource.setMinimumSize(jbtnAnchor.getMinimumSize());
			jtbtnViewSource.setMaximumSize(jbtnAnchor.getMaximumSize());
		}

		/* Create the scroll area for the text pane */
		JScrollPane jspViewport = new JScrollPane(jtpMain);
		jspViewport.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jspViewport.setPreferredSize(new Dimension(400, 400));
		jspViewport.setMinimumSize(new Dimension(32, 32));

		/* Create the scroll area for the source viewer */
		jspSource = new JScrollPane(jtpSource);
		jspSource.setPreferredSize(new Dimension(400, 100));
		jspSource.setMinimumSize(new Dimension(32, 32));

		jspltDisplay = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		jspltDisplay.setTopComponent(jspViewport);
		if(builder.includeViewSource)
		{
			jspltDisplay.setBottomComponent(jspSource);
		}
		else
		{
			jspltDisplay.setBottomComponent(null);
		}

		iSplitPos = jspltDisplay.getDividerLocation();

		registerDocumentStyles();

		/* Add the components to the app */
		this.setLayout(new BorderLayout());
		this.add(jspltDisplay, BorderLayout.CENTER);
	}


	/* ActionListener method */
	public void actionPerformed(ActionEvent ae)
	{
		try
		{
			String command = ae.getActionCommand();
			switch (command) {
				case CMD_DOC_NEW:
				case CMD_DOC_NEW_STYLED:
					SimpleInfoDialog sidAsk = new SimpleInfoDialog(this.getFrame(), "", true, Translatrix.getTranslationString("AskNewDocument"), SimpleInfoDialog.QUESTION);
					String decision = sidAsk.getDecisionValue();
					if (decision.equals(Translatrix.getTranslationString("DialogAccept"))) {
						if (styleSheet != null && command.equals(CMD_DOC_NEW_STYLED)) {
							htmlDoc = new ExtendedHTMLDocument(styleSheet);
						} else {
							htmlDoc = (ExtendedHTMLDocument) (htmlKit.createDefaultDocument());
							htmlDoc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
							htmlDoc.setPreservesUnknownTags(preserveUnknownTags);
						}
//					jtpMain.setText("<HTML><BODY></BODY></HTML>");
						registerDocument(htmlDoc);
						jtpSource.setText(jtpMain.getText());
						currentFile = null;
						updateTitle();
					}
					break;
				case CMD_DOC_OPEN_HTML:
					openDocument(null);
					break;
				case CMD_DOC_OPEN_CSS:
					openStyleSheet(null);
					break;
				case CMD_DOC_OPEN_BASE64:
					openDocumentBase64(null);
					break;
				case CMD_DOC_SAVE:
					writeOut((HTMLDocument) (jtpMain.getDocument()), currentFile);
					updateTitle();
					break;
				case CMD_DOC_SAVE_AS:
					writeOut((HTMLDocument) (jtpMain.getDocument()), null);
					break;
				case CMD_DOC_SAVE_BODY:
					writeOutFragment((HTMLDocument) (jtpMain.getDocument()), "body");
					break;
				case CMD_DOC_SAVE_RTF:
					writeOutRTF((StyledDocument) (jtpMain.getStyledDocument()));
					break;
				case CMD_DOC_SAVE_BASE64:
					writeOutBase64(jtpSource.getText());
					break;
				case CMD_CLIP_CUT:
					if (jspSource.isShowing() && jtpSource.hasFocus()) {
						jtpSource.cut();
					} else {
						jtpMain.cut();
					}
					break;
				case CMD_CLIP_COPY:
					if (jspSource.isShowing() && jtpSource.hasFocus()) {
						jtpSource.copy();
					} else {
						jtpMain.copy();
					}
					break;
				case CMD_CLIP_PASTE:
					if (jspSource.isShowing() && jtpSource.hasFocus()) {
						jtpSource.paste();
					} else {
						jtpMain.paste();
					}
					break;
				case CMD_CLIP_PASTE_PLAIN:
					if (jspSource.isShowing() && jtpSource.hasFocus()) {
						jtpSource.paste();
					} else {
						try {
							if (sysClipboard != null) {
								jtpMain.getDocument().insertString(jtpMain.getCaretPosition(), sysClipboard.getData(dfPlainText).toString(), null);
							} else {
								jtpMain.getDocument().insertString(jtpMain.getCaretPosition(), Toolkit.getDefaultToolkit().getSystemClipboard().getData(dfPlainText).toString(), null);
							}
							refreshOnUpdate();
						} catch (Exception e) {
							e.printStackTrace(System.out);
						}
					}
					break;
				case CMD_DOC_PRINT:
					DocumentRenderer dr = new DocumentRenderer();
					dr.print(htmlDoc);

					break;
				case CMD_DEBUG_DESCRIBE_DOC:
					System.out.println("------------DOCUMENT------------");
					System.out.println("Content Type : " + jtpMain.getContentType());
					System.out.println("Editor Kit   : " + jtpMain.getEditorKit());
					System.out.println("Doc Tree     :");
					System.out.println();
					describeDocument(jtpMain.getStyledDocument());
					System.out.println("--------------------------------");
					System.out.println();
					break;
				case CMD_DEBUG_DESCRIBE_CSS:
					System.out.println("-----------STYLESHEET-----------");
					System.out.println("Stylesheet Rules");
					Enumeration<?> rules = styleSheet.getStyleNames();
					while (rules.hasMoreElements()) {
						String ruleName = (String) (rules.nextElement());
						Style styleRule = styleSheet.getStyle(ruleName);
						System.out.println(styleRule.toString());
					}
					System.out.println("--------------------------------");
					System.out.println();
					break;
				case CMD_DEBUG_CURRENT_TAGS:
					System.out.println("Caret Position : " + jtpMain.getCaretPosition());
					AttributeSet attribSet = jtpMain.getCharacterAttributes();
					Enumeration attribs = attribSet.getAttributeNames();
					System.out.println("Attributes     : ");
					while (attribs.hasMoreElements()) {
						String attribName = attribs.nextElement().toString();
						System.out.println("                 " + attribName + " | " + attribSet.getAttribute(attribName));
					}
					break;
				case CMD_TOGGLE_TOOLBAR_SINGLE:
					jToolBar.setVisible(jcbmiViewToolbar.isSelected());
					break;
				case CMD_TOGGLE_TOOLBAR_MAIN:
					jToolBarMain.setVisible(jcbmiViewToolbarMain.isSelected());
					break;
				case CMD_TOGGLE_TOOLBAR_FORMAT:
					jToolBarFormat.setVisible(jcbmiViewToolbarFormat.isSelected());
					break;
				case CMD_TOGGLE_TOOLBAR_STYLES:
					jToolBarStyles.setVisible(jcbmiViewToolbarStyles.isSelected());
					break;
				case CMD_TOGGLE_SOURCE_VIEW:
					toggleSourceWindow();
					break;
				case CMD_DOC_SERIALIZE_OUT:
					serializeOut((HTMLDocument) (jtpMain.getDocument()));
					break;
				case CMD_DOC_SERIALIZE_IN:
					serializeIn();
					break;
				case CMD_TABLE_INSERT: {
					String[] fieldNames = {"rows", "cols", "border", "cellspacing", "cellpadding", "width", "valign"};
					String[] fieldTypes = {"text", "text", "text", "text", "text", "text", "combo"};
					String[] fieldValues = {"3", "3", "1", "2", "4", "100%", "top,middle,bottom"};
					insertTable(null, fieldNames, fieldTypes, fieldValues);
					break;
				}
				case CMD_TABLE_EDIT:
					editTable();
					break;
				case CMD_TABLE_CELL_EDIT:
					editCell();
					break;
				case CMD_TABLE_ROW_INSERT:
					insertTableRow();
					break;
				case CMD_TABLE_COLUMN_INSERT:
					insertTableColumn();
					break;
				case CMD_TABLE_ROW_DELETE:
					deleteTableRow();
					break;
				case CMD_TABLE_COLUMN_DELETE:
					deleteTableColumn();
					break;
				case CMD_INSERT_BREAK:
					insertBreak();
					break;
				case CMD_INSERT_NBSP:
					insertNonbreakingSpace();
					break;
				case CMD_INSERT_HR:
					insertHR();
					break;
				case CMD_INSERT_IMAGE_LOCAL:
					insertLocalImage(null);
					break;
				case CMD_INSERT_IMAGE_URL:
					insertURLImage();
					break;
				case CMD_INSERT_UNICODE_CHAR:
					insertUnicode(UnicodeDialog.UNICODE_BASE);
					break;
				case CMD_INSERT_UNICODE_MATH:
					insertUnicode(UnicodeDialog.UNICODE_MATH);
					break;
				case CMD_INSERT_UNICODE_DRAW:
					insertUnicode(UnicodeDialog.UNICODE_DRAW);
					break;
				case CMD_INSERT_UNICODE_DING:
					insertUnicode(UnicodeDialog.UNICODE_DING);
					break;
				case CMD_INSERT_UNICODE_SIGS:
					insertUnicode(UnicodeDialog.UNICODE_SIGS);
					break;
				case CMD_INSERT_UNICODE_SPEC:
					insertUnicode(UnicodeDialog.UNICODE_SPEC);
					break;
				case CMD_FORM_INSERT: {
					String[] fieldNames = {"name", "method", "enctype"};
					String[] fieldTypes = {"text", "combo", "text"};
					String[] fieldValues = {"", "POST,GET", "text"};
					insertFormElement(HTML.Tag.FORM, "form", (Hashtable) null, fieldNames, fieldTypes, fieldValues, true);
					break;
				}
				case CMD_FORM_TEXTFIELD: {
					Hashtable<String, String> htAttribs = new Hashtable<String, String>();
					htAttribs.put("type", "text");
					String[] fieldNames = {"name", "value", "size", "maxlength"};
					String[] fieldTypes = {"text", "text", "text", "text"};
					insertFormElement(HTML.Tag.INPUT, "input", htAttribs, fieldNames, fieldTypes, false);
					break;
				}
				case CMD_FORM_TEXTAREA: {
					String[] fieldNames = {"name", "rows", "cols"};
					String[] fieldTypes = {"text", "text", "text"};
					insertFormElement(HTML.Tag.TEXTAREA, "textarea", (Hashtable) null, fieldNames, fieldTypes, true);
					break;
				}
				case CMD_FORM_CHECKBOX: {
					Hashtable<String, String> htAttribs = new Hashtable<String, String>();
					htAttribs.put("type", "checkbox");
					String[] fieldNames = {"name", "checked"};
					String[] fieldTypes = {"text", "bool"};
					insertFormElement(HTML.Tag.INPUT, "input", htAttribs, fieldNames, fieldTypes, false);
					break;
				}
				case CMD_FORM_RADIO: {
					Hashtable<String, String> htAttribs = new Hashtable<String, String>();
					htAttribs.put("type", "radio");
					String[] fieldNames = {"name", "checked"};
					String[] fieldTypes = {"text", "bool"};
					insertFormElement(HTML.Tag.INPUT, "input", htAttribs, fieldNames, fieldTypes, false);
					break;
				}
				case CMD_FORM_PASSWORD: {
					Hashtable<String, String> htAttribs = new Hashtable<String, String>();
					htAttribs.put("type", "password");
					String[] fieldNames = {"name", "value", "size", "maxlength"};
					String[] fieldTypes = {"text", "text", "text", "text"};
					insertFormElement(HTML.Tag.INPUT, "input", htAttribs, fieldNames, fieldTypes, false);
					break;
				}
				case CMD_FORM_BUTTON: {
					Hashtable<String, String> htAttribs = new Hashtable<String, String>();
					htAttribs.put("type", "button");
					String[] fieldNames = {"name", "value"};
					String[] fieldTypes = {"text", "text"};
					insertFormElement(HTML.Tag.INPUT, "input", htAttribs, fieldNames, fieldTypes, false);
					break;
				}
				case CMD_FORM_SUBMIT: {
					Hashtable<String, String> htAttribs = new Hashtable<String, String>();
					htAttribs.put("type", "submit");
					String[] fieldNames = {"name", "value"};
					String[] fieldTypes = {"text", "text"};
					insertFormElement(HTML.Tag.INPUT, "input", htAttribs, fieldNames, fieldTypes, false);
					break;
				}
				case CMD_FORM_RESET: {
					Hashtable<String, String> htAttribs = new Hashtable<String, String>();
					htAttribs.put("type", "reset");
					String[] fieldNames = {"name", "value"};
					String[] fieldTypes = {"text", "text"};
					insertFormElement(HTML.Tag.INPUT, "input", htAttribs, fieldNames, fieldTypes, false);
					break;
				}
				case CMD_SEARCH_FIND:
					doSearch((String) null, (String) null, false, lastSearchCaseSetting, lastSearchTopSetting);
					break;
				case CMD_SEARCH_FIND_AGAIN:
					doSearch(lastSearchFindTerm, (String) null, false, lastSearchCaseSetting, false);
					break;
				case CMD_SEARCH_REPLACE:
					doSearch((String) null, (String) null, true, lastSearchCaseSetting, lastSearchTopSetting);
					break;
				case CMD_EXIT:
					this.dispose();
					break;
				case CMD_HELP_ABOUT:
					new SimpleInfoDialog(this.getFrame(), Translatrix.getTranslationString("About"), true, Translatrix.getTranslationString("AboutMessage"), SimpleInfoDialog.INFO);
					break;
				case CMD_ENTER_PARAGRAPH:
					setEnterKeyIsBreak(false);
					break;
				case CMD_ENTER_BREAK:
					setEnterKeyIsBreak(true);
					break;
				case CMD_SPELLCHECK:
					checkDocumentSpelling(jtpMain.getDocument());
					break;
			}
		}
		catch(IOException ioe)
		{
			logException("IOException in actionPerformed method", ioe);
			new SimpleInfoDialog(this.getFrame(), Translatrix.getTranslationString("Error"), true, Translatrix.getTranslationString("ErrorIOException"), SimpleInfoDialog.ERROR);
		}
		catch(BadLocationException ble)
		{
			logException("BadLocationException in actionPerformed method", ble);
			new SimpleInfoDialog(this.getFrame(), Translatrix.getTranslationString("Error"), true, Translatrix.getTranslationString("ErrorBadLocationException"), SimpleInfoDialog.ERROR);
		}
		catch(NumberFormatException nfe)
		{
			logException("NumberFormatException in actionPerformed method", nfe);
			new SimpleInfoDialog(this.getFrame(), Translatrix.getTranslationString("Error"), true, Translatrix.getTranslationString("ErrorNumberFormatException"), SimpleInfoDialog.ERROR);
		}
		catch(ClassNotFoundException cnfe)
		{
			logException("ClassNotFound Exception in actionPerformed method", cnfe);
			new SimpleInfoDialog(this.getFrame(), Translatrix.getTranslationString("Error"), true, Translatrix.getTranslationString("ErrorClassNotFoundException "), SimpleInfoDialog.ERROR);
		}
		catch(RuntimeException re)
		{
			logException("RuntimeException in actionPerformed method", re);
			new SimpleInfoDialog(this.getFrame(), Translatrix.getTranslationString("Error"), true, Translatrix.getTranslationString("ErrorRuntimeException"), SimpleInfoDialog.ERROR);
		}
	}

	/* KeyListener methods */
	public void keyTyped(KeyEvent ke)
	{
		modified = true;

		Element elem;
		int pos = this.getCaretPosition();
		int repos = -1;
		if(ke.getKeyChar() == KeyEvent.VK_BACK_SPACE)
		{
			try
			{
				if(pos > 0)
				{
					if(jtpMain.getSelectedText() != null)
					{
						htmlUtilities.delete();
						refreshOnUpdate();
						return;
					}
					else
					{
						int sOffset = htmlDoc.getParagraphElement(pos).getStartOffset();
						if(sOffset == jtpMain.getSelectionStart())
						{
							boolean content = true;
							if(htmlUtilities.checkParentsTag(HTML.Tag.LI))
							{
								elem = htmlUtilities.getListItemParent();
								content = false;
								int so = elem.getStartOffset();
								int eo = elem.getEndOffset();
								if(so + 1 < eo)
								{
									char[] temp = jtpMain.getText(so, eo - so).toCharArray();
									for(char c : temp)
									{
										if(!(new Character(c)).isWhitespace(c))
										{
											content = true;
										}
									}
								}
								if(!content)
								{
									htmlUtilities.removeTag(elem, true);
									this.setCaretPosition(sOffset - 1);
									refreshOnUpdate();
									return;
								}
								else
								{
									jtpMain.replaceSelection("");
									refreshOnUpdate();
									return;
								}
							}
							else if(htmlUtilities.checkParentsTag(HTML.Tag.TABLE))
							{
								jtpMain.setCaretPosition(jtpMain.getCaretPosition() - 1);
								ke.consume();
								refreshOnUpdate();
								return;
							}
						}
						jtpMain.replaceSelection("");
						refreshOnUpdate();
						return;
					}
				}
			}
			catch(BadLocationException ble)
			{
				logException("BadLocationException in keyTyped method", ble);
				new SimpleInfoDialog(this.getFrame(), Translatrix.getTranslationString("Error"), true, Translatrix.getTranslationString("ErrorBadLocationException"), SimpleInfoDialog.ERROR);
			}
			catch(IOException ioe)
			{
				logException("IOException in keyTyped method", ioe);
				new SimpleInfoDialog(this.getFrame(), Translatrix.getTranslationString("Error"), true, Translatrix.getTranslationString("ErrorIOException"), SimpleInfoDialog.ERROR);
			}
		}
		else if(ke.getKeyChar() == KeyEvent.VK_ENTER)
		{
			try
			{
				if(htmlUtilities.checkParentsTag(HTML.Tag.UL) == true | htmlUtilities.checkParentsTag(HTML.Tag.OL) == true)
				{
					elem = htmlUtilities.getListItemParent();
					int so = elem.getStartOffset();
					int eo = elem.getEndOffset();
					char[] temp = this.getTextPane().getText(so,eo-so).toCharArray();
					boolean content = false;
					for(char c : temp)
					{
						if(!(new Character(c)).isWhitespace(c))
						{
							content = true;
						}
					}
					if(content)
					{
						int end = -1;
						int j = temp.length;
						do
						{
							j--;
							if(new Character(temp[j]).isLetterOrDigit(temp[j]))
							{
								end = j;
							}
						} while (end == -1 && j >= 0);
						j = end;
						do
						{
							j++;
							if(!new Character(temp[j]).isSpaceChar(temp[j]))
							{
								repos = j - end -1;
							}
						} while (repos == -1 && j < temp.length);
						if(repos == -1)
						{
							repos = 0;
						}
					}
					if(!content)
					{
						removeEmptyListElement(elem);
					}
					else
					{
						if(this.getCaretPosition() + 1 == elem.getEndOffset())
						{
							insertListStyle(elem);
							this.setCaretPosition(pos - repos);
						}
						else
						{
							int caret = this.getCaretPosition();
							String tempString = this.getTextPane().getText(caret, eo - caret);
							if(tempString != null && tempString.length() > 0)
							{
								this.getTextPane().select(caret, eo - 1);
								this.getTextPane().replaceSelection("");
								htmlUtilities.insertListElement(tempString);
								Element newLi = htmlUtilities.getListItemParent();
								this.setCaretPosition(newLi.getEndOffset() - 1);
							}
						}
					}
				}
//				else if(enterIsBreak)
				else if(true)
				{
					insertBreak();
					ke.consume();
				}
			}
			catch(BadLocationException ble)
			{
				logException("BadLocationException in keyTyped method", ble);
				new SimpleInfoDialog(this.getFrame(), Translatrix.getTranslationString("Error"), true, Translatrix.getTranslationString("ErrorBadLocationException"), SimpleInfoDialog.ERROR);
			}
			catch(IOException ioe)
			{
				logException("IOException in keyTyped method", ioe);
				new SimpleInfoDialog(this.getFrame(), Translatrix.getTranslationString("Error"), true, Translatrix.getTranslationString("ErrorIOException"), SimpleInfoDialog.ERROR);
			}
		}else {
			System.out.println("Digitou " + ke.getKeyChar());
			boolean numaTabela = htmlUtilities.checkParentsTag(HTML.Tag.TABLE);
			System.out.println(numaTabela? "numa tabela": "");
		}
	}
	public void keyPressed(KeyEvent ke) { if(ke.getKeyChar() == KeyEvent.VK_ENTER && enterIsBreak) { ke.consume(); } }
	public void keyReleased(KeyEvent ke) { if(ke.getKeyChar() == KeyEvent.VK_ENTER && enterIsBreak) { ke.consume(); } }

	/* FocusListener methods */
	public void focusGained(FocusEvent fe)
	{
		if(fe.getSource() == jtpMain)
		{
			setFormattersActive(true);
		}
		else if(fe.getSource() == jtpSource)
		{
			setFormattersActive(false);
		}
	}
	public void focusLost(FocusEvent fe) {}

	/* DocumentListener methods */
	public void changedUpdate(DocumentEvent de)	{ handleDocumentChange(de); }
	public void insertUpdate(DocumentEvent de)	{ handleDocumentChange(de); }
	public void removeUpdate(DocumentEvent de)	{ handleDocumentChange(de); }
	public void handleDocumentChange(DocumentEvent de)
	{
		if(!exclusiveEdit)
		{
			if(isSourceWindowActive())
			{
				if(de.getDocument() instanceof HTMLDocument || de.getDocument() instanceof ExtendedHTMLDocument)
				{
					jtpSource.getDocument().removeDocumentListener(this);
					jtpSource.setText(jtpMain.getText());
					jtpSource.getDocument().addDocumentListener(this);
				}
				else if(de.getDocument() instanceof PlainDocument || de.getDocument() instanceof DefaultStyledDocument)
				{
					jtpMain.getDocument().removeDocumentListener(this);
					jtpMain.setText(jtpSource.getText());
					jtpMain.getDocument().addDocumentListener(this);
				}
			}
		}
	}

	/** Method for setting a document as the current document for the text pane
	  * and re-registering the controls and settings for it
	  */
	public void registerDocument(ExtendedHTMLDocument htmlDoc)
	{
		jtpMain.setDocument(htmlDoc);
		jtpMain.getDocument().addUndoableEditListener(new CustomUndoableEditListener());
		jtpMain.getDocument().addDocumentListener(this);
		jtpMain.setCaretPosition(0);
		purgeUndos();
		registerDocumentStyles();
	}

	/** Method for locating the available CSS style for the document and adding
	  * them to the styles selector
	  */
	public void registerDocumentStyles()
	{
		if(jcmbStyleSelector == null || htmlDoc == null)
		{
			return;
		}
		jcmbStyleSelector.setEnabled(false);
		jcmbStyleSelector.removeAllItems();
		jcmbStyleSelector.addItem(Translatrix.getTranslationString("NoCSSStyle"));
		for(Enumeration e = htmlDoc.getStyleNames(); e.hasMoreElements();)
		{
			String name = (String) e.nextElement();
			if(name.length() > 0 && name.charAt(0) == '.')
			{
				jcmbStyleSelector.addItem(name.substring(1));
			}
		}
		jcmbStyleSelector.setEnabled(true);
	}

	/** Method for inserting list elements
	  */
	public void insertListStyle(Element element)
	throws BadLocationException,IOException
	{
		if(element.getParentElement().getName() == "ol")
		{
			actionListOrdered.actionPerformed(new ActionEvent(new Object(), 0, "newListPoint"));
		}
		else
		{
			actionListUnordered.actionPerformed(new ActionEvent(new Object(), 0, "newListPoint"));
		}
	}

	/** Method for inserting an HTML Table
	  */
	private void insertTable(Hashtable attribs, String[] fieldNames, String[] fieldTypes, String[] fieldValues)
	throws IOException, BadLocationException, RuntimeException, NumberFormatException
	{
		int caretPos = jtpMain.getCaretPosition();
		StringBuffer compositeElement = new StringBuffer("<TABLE");
		if(attribs != null && attribs.size() > 0)
		{
			Enumeration attribEntries = attribs.keys();
			while(attribEntries.hasMoreElements())
			{
				Object entryKey   = attribEntries.nextElement();
				Object entryValue = attribs.get(entryKey);
				if(entryValue != null && entryValue != "")
				{
					compositeElement.append(" " + entryKey + "=" + '"' + entryValue + '"');
				}
			}
		}
		int rows = 0;
		int cols = 0;
		if(fieldNames != null && fieldNames.length > 0)
		{
			PropertiesDialog propertiesDialog = new PropertiesDialog(this.getFrame(), fieldNames, fieldTypes, fieldValues, Translatrix.getTranslationString("TableDialogTitle"), true);
			propertiesDialog.setVisible(true);
			String decision = propertiesDialog.getDecisionValue();
			if(decision.equals(Translatrix.getTranslationString("DialogCancel")))
			{
				propertiesDialog.dispose();
				propertiesDialog = null;
				return;
			}
			else
			{
				for(String fieldName : fieldNames)
				{
					String propValue = propertiesDialog.getFieldValue(fieldName);
					if(propValue != null && propValue != "" && propValue.length() > 0)
					{
						if(fieldName.equals("rows"))
						{
							rows = Integer.parseInt(propValue);
						}
						else if(fieldName.equals("cols"))
						{
							cols = Integer.parseInt(propValue);
						}
						else
						{
							compositeElement.append(" " + fieldName + "=" + '"' + propValue + '"');
						}
					}
				}
			}
			propertiesDialog.dispose();
			propertiesDialog = null;
		}
		compositeElement.append(">");
		for(int i = 0; i < rows; i++)
		{
			compositeElement.append("<TR>");
			for(int j = 0; j < cols; j++)
			{
				compositeElement.append("<TD></TD>");
			}
			compositeElement.append("</TR>");
		}
		compositeElement.append("</TABLE>&nbsp;");
		htmlKit.insertHTML(htmlDoc, caretPos, compositeElement.toString(), 0, 0, HTML.Tag.TABLE);
		jtpMain.setCaretPosition(caretPos + 1);
		refreshOnUpdate();
	}

	/** Method for editing an HTML Table
	  */
	private void editTable()
	{
		int caretPos = jtpMain.getCaretPosition();
		Element	element = htmlDoc.getCharacterElement(caretPos);
		Element elementParent = element.getParentElement();
		while(elementParent != null && !elementParent.getName().equals("table"))
		{
		elementParent = elementParent.getParentElement();
		}
		if (elementParent != null)
		{
			HTML.Attribute[] fieldKeys = { HTML.Attribute.BORDER, HTML.Attribute.CELLSPACING, HTML.Attribute.CELLPADDING, HTML.Attribute.WIDTH, HTML.Attribute.VALIGN };
			String[] fieldNames  = { "border", "cellspacing", "cellpadding", "width", "valign" };
			String[] fieldTypes  = { "text",   "text",        "text",        "text",  "combo" };
			String[] fieldValues = { "",       "",            "",            "",      "top,middle,bottom," };
			MutableAttributeSet myatr = (MutableAttributeSet)elementParent.getAttributes();
			for(int i = 0; i < fieldNames.length; i++)
			{
				if(myatr.isDefined(fieldKeys[i]))
				{
					if(fieldTypes[i].equals("combo"))
					{
						fieldValues[i] = myatr.getAttribute(fieldKeys[i]).toString() + "," + fieldValues[i];
					}
					else
					{
						fieldValues[i] = myatr.getAttribute(fieldKeys[i]).toString();
					}
				}
			}
			PropertiesDialog propertiesDialog = new PropertiesDialog(this.getFrame(), fieldNames, fieldTypes, fieldValues, Translatrix.getTranslationString("TableEdit"), true);
			propertiesDialog.setVisible(true);
			if(!propertiesDialog.getDecisionValue().equals(Translatrix.getTranslationString("DialogCancel")))
			{
				String myAtributes = "";
				SimpleAttributeSet mynew = new SimpleAttributeSet();
				for(int i = 0; i < fieldNames.length; i++)
				{
					String propValue = propertiesDialog.getFieldValue(fieldNames[i]);
					if(propValue != null && propValue.length() > 0)
					{
						myAtributes = myAtributes + fieldNames[i] + "=\"" + propValue + "\" ";
						mynew.addAttribute(fieldKeys[i],propValue);
					}
				}
				htmlDoc.replaceAttributes(elementParent, mynew, HTML.Tag.TABLE);
				refreshOnUpdate();
			}
			propertiesDialog.dispose();
		}
		else
		{
			new SimpleInfoDialog(this.getFrame(), Translatrix.getTranslationString("Table"), true, Translatrix.getTranslationString("CursorNotInTable"));
		}
	}

	/** Method for editing HTML Table cells
	  */
	private void editCell()
	{
		int caretPos = jtpMain.getCaretPosition();
		Element	element = htmlDoc.getCharacterElement(caretPos);
		Element elementParent = element.getParentElement();
		while(elementParent != null && !elementParent.getName().equals("td"))
		{
			elementParent = elementParent.getParentElement();
		}
		if(elementParent != null)
		{
			HTML.Attribute[] fieldKeys = { HTML.Attribute.WIDTH,HTML.Attribute.HEIGHT,HTML.Attribute.ALIGN,HTML.Attribute.VALIGN,HTML.Attribute.BGCOLOR };
			String[] fieldNames  = { "width", "height", "align", "valign", "bgcolor" };
			String[] fieldTypes  = { "text",  "text",   "combo", "combo",  "combo" };
			String[] fieldValues = { "",      "",       "left,right,center", "top,middle,bottom", "none,aqua,black,fuchsia,gray,green,lime,maroon,navy,olive,purple,red,silver,teal,white,yellow" };
			MutableAttributeSet myatr = (MutableAttributeSet)elementParent.getAttributes();
			for(int i = 0; i < fieldNames.length; i++)
			{
				if(myatr.isDefined(fieldKeys[i]))
				{
					if(fieldTypes[i].equals("combo"))
					{
						fieldValues[i] = myatr.getAttribute(fieldKeys[i]).toString() + "," + fieldValues[i];
					}
					else
					{
						fieldValues[i] = myatr.getAttribute(fieldKeys[i]).toString();
					}
				}
			}
			PropertiesDialog propertiesDialog = new PropertiesDialog(this.getFrame(), fieldNames, fieldTypes, fieldValues, Translatrix.getTranslationString("TableCellEdit"), true);
			propertiesDialog.setVisible(true);
			if(!propertiesDialog.getDecisionValue().equals(Translatrix.getTranslationString("DialogCancel")))
			{
				String myAtributes = "";
				SimpleAttributeSet mynew = new SimpleAttributeSet();
				for(int i = 0; i < fieldNames.length; i++)
				{
					String propValue = propertiesDialog.getFieldValue(fieldNames[i]);
					if(propValue != null && propValue.length() > 0)
					{
						myAtributes = myAtributes + fieldNames[i] + "=\"" + propValue + "\" ";
						mynew.addAttribute(fieldKeys[i],propValue);
					}
				}
				htmlDoc.replaceAttributes(elementParent, mynew, HTML.Tag.TD);
				refreshOnUpdate();
			}
			propertiesDialog.dispose();
		}
		else
		{
			new SimpleInfoDialog(this.getFrame(), Translatrix.getTranslationString("Cell"), true, Translatrix.getTranslationString("CursorNotInCell"));
		}
	}

	/** Method for inserting a row into an HTML Table
	  */
	private void insertTableRow()
	{
		int caretPos = jtpMain.getCaretPosition();
		Element	element = htmlDoc.getCharacterElement(jtpMain.getCaretPosition());
		Element elementParent = element.getParentElement();
		int startPoint  = -1;
		int columnCount = -1;
		while(elementParent != null && !elementParent.getName().equals("body"))
		{
			if(elementParent.getName().equals("tr"))
			{
				startPoint  = elementParent.getStartOffset();
				columnCount = elementParent.getElementCount();
				break;
			}
			else
			{
				elementParent = elementParent.getParentElement();
			}
		}
		if(startPoint > -1 && columnCount > -1)
		{
			jtpMain.setCaretPosition(startPoint);
	 		StringBuffer sRow = new StringBuffer();
 			sRow.append("<TR>");
 			for(int i = 0; i < columnCount; i++)
 			{
 				sRow.append("<TD></TD>");
 			}
 			sRow.append("</TR>");
 			ActionEvent actionEvent = new ActionEvent(jtpMain, 0, "insertTableRow");
 			new HTMLEditorKit.InsertHTMLTextAction("insertTableRow", sRow.toString(), HTML.Tag.TABLE, HTML.Tag.TR).actionPerformed(actionEvent);
 			refreshOnUpdate();
 			jtpMain.setCaretPosition(caretPos);
 		}
	}

	/** Method for inserting a column into an HTML Table
	  */
	private void insertTableColumn()
	{
		int caretPos = jtpMain.getCaretPosition();
		Element	element = htmlDoc.getCharacterElement(jtpMain.getCaretPosition());
		Element elementParent = element.getParentElement();
		int startPoint = -1;
		int rowCount   = -1;
		int cellOffset =  0;
		while(elementParent != null && !elementParent.getName().equals("body"))
		{
			if(elementParent.getName().equals("table"))
			{
				startPoint = elementParent.getStartOffset();
				rowCount   = elementParent.getElementCount();
				break;
			}
			else if(elementParent.getName().equals("tr"))
			{
				int rowCells = elementParent.getElementCount();
				for(int i = 0; i < rowCells; i++)
				{
					Element currentCell = elementParent.getElement(i);
					if(jtpMain.getCaretPosition() >= currentCell.getStartOffset() && jtpMain.getCaretPosition() <= currentCell.getEndOffset())
					{
						cellOffset = i;
					}
				}
				elementParent = elementParent.getParentElement();
			}
			else
			{
				elementParent = elementParent.getParentElement();
			}
		}
		if(startPoint > -1 && rowCount > -1)
		{
			jtpMain.setCaretPosition(startPoint);
			String sCell = "<TD></TD>";
			ActionEvent actionEvent = new ActionEvent(jtpMain, 0, "insertTableCell");
 			for(int i = 0; i < rowCount; i++)
 			{
 				Element row = elementParent.getElement(i);
 				Element whichCell = row.getElement(cellOffset);
 				jtpMain.setCaretPosition(whichCell.getStartOffset());
				new HTMLEditorKit.InsertHTMLTextAction("insertTableCell", sCell, HTML.Tag.TR, HTML.Tag.TD, HTML.Tag.TH, HTML.Tag.TD).actionPerformed(actionEvent);
 			}
 			refreshOnUpdate();
 			jtpMain.setCaretPosition(caretPos);
 		}
	}

	/** Method for inserting a cell into an HTML Table
	  */
	private void insertTableCell()
	{
		String sCell = "<TD></TD>";
		ActionEvent actionEvent = new ActionEvent(jtpMain, 0, "insertTableCell");
		new HTMLEditorKit.InsertHTMLTextAction("insertTableCell", sCell, HTML.Tag.TR, HTML.Tag.TD, HTML.Tag.TH, HTML.Tag.TD).actionPerformed(actionEvent);
		refreshOnUpdate();
	}

	/** Method for deleting a row from an HTML Table
	  */
	private void deleteTableRow()
	throws BadLocationException
	{
		int caretPos = jtpMain.getCaretPosition();
		Element	element = htmlDoc.getCharacterElement(jtpMain.getCaretPosition());
		Element elementParent = element.getParentElement();
		int startPoint = -1;
		int endPoint   = -1;
		while(elementParent != null && !elementParent.getName().equals("body"))
		{
			if(elementParent.getName().equals("tr"))
			{
				startPoint = elementParent.getStartOffset();
				endPoint   = elementParent.getEndOffset();
				break;
			}
			else
			{
				elementParent = elementParent.getParentElement();
			}
		}
		if(startPoint > -1 && endPoint > startPoint)
		{
			htmlDoc.remove(startPoint, endPoint - startPoint);
			jtpMain.setDocument(htmlDoc);
			registerDocument(htmlDoc);
 			refreshOnUpdate();
 			if(caretPos >= htmlDoc.getLength())
 			{
 				caretPos = htmlDoc.getLength() - 1;
 			}
 			jtpMain.setCaretPosition(caretPos);
 		}
	}

	/** Method for deleting a column from an HTML Table
	  */
	private void deleteTableColumn()
	throws BadLocationException
	{
		int caretPos = jtpMain.getCaretPosition();
		Element	element       = htmlDoc.getCharacterElement(jtpMain.getCaretPosition());
		Element elementParent = element.getParentElement();
		Element	elementCell   = (Element)null;
		Element	elementRow    = (Element)null;
		Element	elementTable  = (Element)null;
		// Locate the table, row, and cell location of the cursor
		while(elementParent != null && !elementParent.getName().equals("body"))
		{
			if(elementParent.getName().equals("td"))
			{
				elementCell = elementParent;
			}
			else if(elementParent.getName().equals("tr"))
			{
				elementRow = elementParent;
			}
			else if(elementParent.getName().equals("table"))
			{
				elementTable = elementParent;
			}
			elementParent = elementParent.getParentElement();
		}
		int whichColumn = -1;
		if(elementCell != null && elementRow != null && elementTable != null)
		{
			// Find the column the cursor is in
			int myOffset = 0;
			for(int i = 0; i < elementRow.getElementCount(); i++)
			{
				if(elementCell == elementRow.getElement(i))
				{
					whichColumn = i;
					myOffset = elementCell.getEndOffset();
				}
			}
			if(whichColumn > -1)
			{
				// Iterate through the table rows, deleting cells from the indicated column
				int mycaretPos = caretPos;
				for(int i = 0; i < elementTable.getElementCount(); i++)
				{
					elementRow  = elementTable.getElement(i);
					elementCell = (elementRow.getElementCount() > whichColumn ? elementRow.getElement(whichColumn) : elementRow.getElement(elementRow.getElementCount() - 1));
					int columnCellStart = elementCell.getStartOffset();
					int columnCellEnd   = elementCell.getEndOffset();
					int dif	= columnCellEnd - columnCellStart;
					if(columnCellStart < myOffset)
					{
						mycaretPos = mycaretPos - dif;
						myOffset = myOffset-dif;
					}
					if(whichColumn==0)
					{
						htmlDoc.remove(columnCellStart, dif);
					}
					else
					{
						htmlDoc.remove(columnCellStart-1, dif);
					}
				}
				jtpMain.setDocument(htmlDoc);
				registerDocument(htmlDoc);
	 			refreshOnUpdate();
	 			if(mycaretPos >= htmlDoc.getLength())
	 			{
	 				mycaretPos = htmlDoc.getLength() - 1;
	 			}
	 			if(mycaretPos < 1)
	 			{
	 				mycaretPos =  1;
 				}
	 			jtpMain.setCaretPosition(mycaretPos);
			}
		}
	}

	/** Method for inserting a break (BR) element
	  */
	private void insertBreak()
	throws IOException, BadLocationException, RuntimeException
	{
		int caretPos = jtpMain.getCaretPosition();
		htmlKit.insertHTML(htmlDoc, caretPos, "<BR>", 0, 0, HTML.Tag.BR);
		jtpMain.setCaretPosition(caretPos + 1);
	}

	/** Method for inserting a horizontal rule (HR) element
	  */
	private void insertHR()
	throws IOException, BadLocationException, RuntimeException
	{
		int caretPos = jtpMain.getCaretPosition();
		htmlKit.insertHTML(htmlDoc, caretPos, "<HR>", 0, 0, HTML.Tag.HR);
		jtpMain.setCaretPosition(caretPos + 1);
	}

	/** Method for opening the Unicode dialog
	  */
	private void insertUnicode(int index)
	throws IOException, BadLocationException, RuntimeException
	{
		new UnicodeDialog(this, Translatrix.getTranslationString("UnicodeDialogTitle"), false, index);
	}

	/** Method for inserting Unicode characters via the UnicodeDialog class
	  */
	public void insertUnicodeChar(String sChar)
	throws IOException, BadLocationException, RuntimeException
	{
		int caretPos = jtpMain.getCaretPosition();
		if(sChar != null)
		{
			htmlDoc.insertString(caretPos, sChar, jtpMain.getInputAttributes());
			jtpMain.setCaretPosition(caretPos + 1);
		}
	}

	/** Method for inserting a non-breaking space (&nbsp;)
	  */
	private void insertNonbreakingSpace()
	throws IOException, BadLocationException, RuntimeException
	{
		int caretPos = jtpMain.getCaretPosition();
		htmlDoc.insertString(caretPos, "\240", jtpMain.getInputAttributes());
		jtpMain.setCaretPosition(caretPos + 1);
	}

	/** Method for inserting a form element
	  */
	private void insertFormElement(HTML.Tag baseTag, String baseElement, Hashtable attribs, String[] fieldNames, String[] fieldTypes, String[] fieldValues, boolean hasClosingTag)
	throws IOException, BadLocationException, RuntimeException
	{
		int caretPos = jtpMain.getCaretPosition();
		StringBuffer compositeElement = new StringBuffer("<" + baseElement);
		if(attribs != null && attribs.size() > 0)
		{
			Enumeration attribEntries = attribs.keys();
			while(attribEntries.hasMoreElements())
			{
				Object entryKey   = attribEntries.nextElement();
				Object entryValue = attribs.get(entryKey);
				if(entryValue != null && entryValue != "")
				{
					compositeElement.append(" " + entryKey + "=" + '"' + entryValue + '"');
				}
			}
		}
		if(fieldNames != null && fieldNames.length > 0)
		{
			PropertiesDialog propertiesDialog = new PropertiesDialog(this.getFrame(), fieldNames, fieldTypes, fieldValues, Translatrix.getTranslationString("FormDialogTitle"), true);
			propertiesDialog.setVisible(true);
			String decision = propertiesDialog.getDecisionValue();
			if(decision.equals(Translatrix.getTranslationString("DialogCancel")))
			{
				propertiesDialog.dispose();
				propertiesDialog = null;
				return;
			}
			else
			{
				for(String fieldName : fieldNames)
				{
					String propValue = propertiesDialog.getFieldValue(fieldName);
					if(propValue != null && propValue.length() > 0)
					{
						if(fieldName.equals("checked"))
						{
							if(propValue.equals("true"))
							{
								compositeElement.append(" " + fieldName + "=" + '"' + propValue + '"');
							}
						}
						else
						{
							compositeElement.append(" " + fieldName + "=" + '"' + propValue + '"');
						}
					}
				}
			}
			propertiesDialog.dispose();
			propertiesDialog = null;
		}
		// --- Convenience for editing, this makes the FORM visible
		if(useFormIndicator && baseElement.equals("form"))
		{
			compositeElement.append(" bgcolor=" + '"' + clrFormIndicator + '"');
		}
		// --- END
		compositeElement.append(">");
		if(baseTag == HTML.Tag.FORM)
		{
			compositeElement.append("<P>&nbsp;</P>");
			compositeElement.append("<P>&nbsp;</P>");
			compositeElement.append("<P>&nbsp;</P>");
		}
		if(hasClosingTag)
		{
			compositeElement.append("</" + baseElement + ">");
		}
		if(baseTag == HTML.Tag.FORM)
		{
			compositeElement.append("<P>&nbsp;</P>");
		}
		htmlKit.insertHTML(htmlDoc, caretPos, compositeElement.toString(), 0, 0, baseTag);
		// jtpMain.setCaretPosition(caretPos + 1);
		refreshOnUpdate();
	}

	/** Alternate method call for inserting a form element
	  */
	private void insertFormElement(HTML.Tag baseTag, String baseElement, Hashtable attribs, String[] fieldNames, String[] fieldTypes, boolean hasClosingTag)
	throws IOException, BadLocationException, RuntimeException
	{
		insertFormElement(baseTag, baseElement, attribs, fieldNames, fieldTypes, new String[fieldNames.length], hasClosingTag);
	}

	/** Method that handles initial list insertion and deletion
	  */
	public void removeEmptyListElement(Element element)
	{
		Element h = htmlUtilities.getListItemParent();
		Element listPar = h.getParentElement();
		if(h != null)
		{
			htmlUtilities.removeTag(h, true);
			removeEmptyLists();
			refreshOnUpdate();
		}
	}

	public void removeEmptyLists()
	{
		javax.swing.text.ElementIterator ei = new javax.swing.text.ElementIterator(htmlDoc);
		Element ele;
		while((ele = ei.next()) != null)
		{
			if(ele.getName().equals("ul") || ele.getName().equals("ol"))
			{
				int listChildren = 0;
				for(int i = 0; i < ele.getElementCount(); i++)
				{
					if(ele.getElement(i).getName().equals("li"))
					{
						listChildren++;
					}
				}
				if(listChildren <= 0)
				{
					htmlUtilities.removeTag(ele, true);
				}
			}
		}
		refreshOnUpdate();
	}

	/** Method to initiate a find/replace operation
	  */
	private void doSearch(String searchFindTerm, String searchReplaceTerm, boolean bIsFindReplace, boolean bCaseSensitive, boolean bStartAtTop)
	{
		boolean bReplaceAll = false;
		JTextComponent searchPane = (JTextComponent)jtpMain;
		if(jspSource.isShowing() || jtpSource.hasFocus())
		{
			searchPane = (JTextComponent)jtpSource;
		}
		if(searchFindTerm == null || (bIsFindReplace && searchReplaceTerm == null))
		{
			SearchDialog sdSearchInput = new SearchDialog(this.getFrame(), Translatrix.getTranslationString("SearchDialogTitle"), true, bIsFindReplace, bCaseSensitive, bStartAtTop);
			searchFindTerm    = sdSearchInput.getFindTerm();
			searchReplaceTerm = sdSearchInput.getReplaceTerm();
			bCaseSensitive    = sdSearchInput.getCaseSensitive();
			bStartAtTop       = sdSearchInput.getStartAtTop();
			bReplaceAll       = sdSearchInput.getReplaceAll();
		}
		if(searchFindTerm != null && (!bIsFindReplace || searchReplaceTerm != null))
		{
			if(bReplaceAll)
			{
				int results = findText(searchFindTerm, searchReplaceTerm, bCaseSensitive, 0);
				int findOffset = 0;
				if(results > -1)
				{
					while(results > -1)
					{
						findOffset = findOffset + searchReplaceTerm.length();
						results    = findText(searchFindTerm, searchReplaceTerm, bCaseSensitive, findOffset);
					}
				}
				else
				{
					new SimpleInfoDialog(this.getFrame(), "", true, Translatrix.getTranslationString("ErrorNoOccurencesFound") + ":\n" + searchFindTerm, SimpleInfoDialog.WARNING);
				}
			}
			else
			{
				int results = findText(searchFindTerm, searchReplaceTerm, bCaseSensitive, (bStartAtTop ? 0 : searchPane.getCaretPosition()));
				if(results == -1)
				{
					new SimpleInfoDialog(this.getFrame(), "", true, Translatrix.getTranslationString("ErrorNoMatchFound") + ":\n" + searchFindTerm, SimpleInfoDialog.WARNING);
				}
			}
			lastSearchFindTerm    = new String(searchFindTerm);
			if(searchReplaceTerm != null)
			{
				lastSearchReplaceTerm = new String(searchReplaceTerm);
			}
			else
			{
				lastSearchReplaceTerm = (String)null;
			}
			lastSearchCaseSetting = bCaseSensitive;
			lastSearchTopSetting  = bStartAtTop;
		}
	}

	/** Method for finding (and optionally replacing) a string in the text
	  */
	private int findText(String findTerm, String replaceTerm, boolean bCaseSenstive, int iOffset)
	{
		JTextComponent jtpFindSource;
		if(isSourceWindowActive() || jtpSource.hasFocus())
		{
			jtpFindSource = (JTextComponent)jtpSource;
		}
		else
		{
			jtpFindSource = (JTextComponent)jtpMain;
		}
		int searchPlace = -1;
		try
		{
			Document baseDocument = jtpFindSource.getDocument();
			searchPlace =
				(bCaseSenstive ?
					baseDocument.getText(0, baseDocument.getLength()).indexOf(findTerm, iOffset) :
					baseDocument.getText(0, baseDocument.getLength()).toLowerCase().indexOf(findTerm.toLowerCase(), iOffset)
				);
			if(searchPlace > -1)
			{
				if(replaceTerm != null)
				{
					AttributeSet attribs = null;
					if(baseDocument instanceof HTMLDocument)
					{
						Element element = ((HTMLDocument)baseDocument).getCharacterElement(searchPlace);
						attribs = element.getAttributes();
					}
					baseDocument.remove(searchPlace, findTerm.length());
					baseDocument.insertString(searchPlace, replaceTerm, attribs);
					jtpFindSource.setCaretPosition(searchPlace + replaceTerm.length());
					jtpFindSource.requestFocus();
					jtpFindSource.select(searchPlace, searchPlace + replaceTerm.length());
				}
				else
				{
					jtpFindSource.setCaretPosition(searchPlace + findTerm.length());
					jtpFindSource.requestFocus();
					jtpFindSource.select(searchPlace, searchPlace + findTerm.length());
				}
			}
		}
		catch(BadLocationException ble)
		{
			logException("BadLocationException in actionPerformed method", ble);
			new SimpleInfoDialog(this.getFrame(), Translatrix.getTranslationString("Error"), true, Translatrix.getTranslationString("ErrorBadLocationException"), SimpleInfoDialog.ERROR);
		}
		return searchPlace;
	}

	/** Method for inserting an image from a file
	  */
	private void insertLocalImage(File whatImage)
	throws IOException, BadLocationException, RuntimeException
	{
		if(whatImage == null)
		{
			getImageFromChooser(imageChooserStartDir, extsIMG, Translatrix.getTranslationString("FiletypeIMG"));
		}
		else
		{
			imageChooserStartDir = whatImage.getParent().toString();
			int caretPos = jtpMain.getCaretPosition();
			htmlKit.insertHTML(htmlDoc, caretPos, "<IMG SRC=\"" + whatImage + "\">", 0, 0, HTML.Tag.IMG);
			jtpMain.setCaretPosition(caretPos + 1);
			refreshOnUpdate();
		}
	}

	/** Method for inserting an image from a URL
	  */
	public void insertURLImage()
	throws IOException, BadLocationException, RuntimeException
	{
		ImageURLDialog imgUrlDialog = new ImageURLDialog(this.getFrame(), Translatrix.getTranslationString("ImageURLDialogTitle"), true);
		imgUrlDialog.pack();
		imgUrlDialog.setVisible(true);
		String whatImage = imgUrlDialog.getImageUrl();
		if(whatImage != null && whatImage.length() > 0)
		{
			int caretPos = jtpMain.getCaretPosition();
			String sImgTag = "<img src=\"" + whatImage + '"';
			if(imgUrlDialog.getImageAlt() != null && imgUrlDialog.getImageAlt().length() > 0) { sImgTag = sImgTag + " alt=\"" + imgUrlDialog.getImageAlt() + '"'; }
			if(imgUrlDialog.getImageWidth() != null && imgUrlDialog.getImageWidth().length() > 0) { sImgTag = sImgTag + " width=\"" + imgUrlDialog.getImageWidth() + '"'; }
			if(imgUrlDialog.getImageHeight() != null && imgUrlDialog.getImageHeight().length() > 0) { sImgTag = sImgTag + " height=\"" + imgUrlDialog.getImageHeight() + '"'; }
			sImgTag = sImgTag + "/>";
			htmlKit.insertHTML(htmlDoc, caretPos, sImgTag, 0, 0, HTML.Tag.IMG);
			jtpMain.setCaretPosition(caretPos + 1);
			refreshOnUpdate();
		}
	}

	/** Empty spell check method, overwritten by spell checker extension class
	  */
	public void checkDocumentSpelling(Document doc) { ; }

	/** Method for saving text as a complete HTML document
	  */
	public void writeOut(HTMLDocument doc, File whatFile)
	throws IOException, BadLocationException
	{
		if(whatFile == null)
		{
			whatFile = getFileFromChooser(".", JFileChooser.SAVE_DIALOG, extsHTML, Translatrix.getTranslationString("FiletypeHTML"));
		}
		if(whatFile != null)
		{
			FileWriter fw = new FileWriter(whatFile);
			htmlKit.write(fw, doc, 0, doc.getLength());
			fw.flush();
			fw.close();
			currentFile = whatFile;
			updateTitle();
		}
		refreshOnUpdate();
		modified = false;
	}

	/** Method for saving text as an HTML fragment
	  */
	public void writeOutFragment(HTMLDocument doc, String containingTag, File fragFile)
	throws IOException, BadLocationException
	{
		FileWriter fw = new FileWriter(fragFile);
		String docTextCase = jtpSource.getText().toLowerCase();
		int tagStart       = docTextCase.indexOf("<" + containingTag.toLowerCase());
		int tagStartClose  = docTextCase.indexOf(">", tagStart) + 1;
		String closeTag    = "</" + containingTag.toLowerCase() + ">";
		int tagEndOpen     = docTextCase.indexOf(closeTag);
		if(tagStartClose < 0) { tagStartClose = 0; }
		if(tagEndOpen < 0 || tagEndOpen > docTextCase.length()) { tagEndOpen = docTextCase.length(); }
		String bodyText = jtpSource.getText().substring(tagStartClose, tagEndOpen);
		fw.write(bodyText, 0, bodyText.length());
		fw.flush();
		fw.close();
		refreshOnUpdate();
	}

	public void writeOutFragment(HTMLDocument doc, String containingTag)
	throws IOException, BadLocationException
	{
		File whatFile = getFileFromChooser(".", JFileChooser.SAVE_DIALOG, extsHTML, Translatrix.getTranslationString("FiletypeHTML"));
		if(whatFile != null)
		{
			writeOutFragment(doc, containingTag, whatFile);
		}
	}

	/** Method for saving text as an RTF document
	  */
	public void writeOutRTF(StyledDocument doc, File rtfFile)
	throws IOException, BadLocationException
	{
		FileOutputStream fos = new FileOutputStream(rtfFile);
		RTFEditorKit rtfKit = new RTFEditorKit();
		rtfKit.write(fos, doc, 0, doc.getLength());
		fos.flush();
		fos.close();
		refreshOnUpdate();
	}

	public void writeOutRTF(StyledDocument doc)
	throws IOException, BadLocationException
	{
		File whatFile = getFileFromChooser(".", JFileChooser.SAVE_DIALOG, extsRTF, Translatrix.getTranslationString("FiletypeRTF"));
		if(whatFile != null)
		{
			writeOutRTF(doc, whatFile);
		}
	}

	public String getRTFDocument()
	throws IOException, BadLocationException
	{
		StyledDocument doc = (StyledDocument)(jtpMain.getStyledDocument());
		StringWriter strwriter = new StringWriter();
		RTFEditorKit rtfKit = new RTFEditorKit();
		rtfKit.write(strwriter, doc, 0, doc.getLength());
		return strwriter.toString();
	}

	/** Method for saving text as a Base64 encoded document
	  */
	public void writeOutBase64(String text, File b64File)
	throws IOException, BadLocationException
	{
		String base64text = Base64Codec.encode(text);
		FileWriter fw = new FileWriter(b64File);
		fw.write(base64text, 0, base64text.length());
		fw.flush();
		fw.close();
		refreshOnUpdate();
	}

	public void writeOutBase64(String text)
	throws IOException, BadLocationException
	{
		File whatFile = getFileFromChooser(".", JFileChooser.SAVE_DIALOG, extsB64, Translatrix.getTranslationString("FiletypeB64"));
		if(whatFile != null)
		{
			writeOutBase64(text, whatFile);
		}
	}

	/**
	 * Method for saving the HTML document
	 * JJ: As writeOut() is private I added this because I found no easy way just to save the open document
	 * 
	 */
	public void saveDocument()
	throws IOException, BadLocationException
	{
		writeOut((HTMLDocument)(jtpMain.getDocument()), currentFile);
	}

	/**
	 * Method to invoke loading HTML into the app HTMLEditorKit.ParserCallback
	 * cb can be - null or - new EkitStandardParserCallback() or - another
	 * ParserCallback overwrite
	 * 
	 * If cb is not null the loaded Document will be parsed before it is
	 * inserted into the htmlKit and the JTextArea and the ParserCallback can
	 * be used to analyse the errors. This might carry an performance penalty but
	 * makes loading safer in certain situations.
	 * 
	 */
	private void openDocument(File whatFile)
	throws IOException, BadLocationException
	{
		this.openDocument(whatFile, null);
	}

	private void openDocument(File whatFile, HTMLEditorKit.ParserCallback cb)
	throws IOException, BadLocationException
	{
		if(whatFile == null)
		{
			whatFile = getFileFromChooser(".", JFileChooser.OPEN_DIALOG, extsHTML, Translatrix.getTranslationString("FiletypeHTML"));
		}
		if(whatFile != null)
		{
			try
			{
				loadDocument(whatFile, null, cb);
			}
			catch(ChangedCharSetException ccse)
			{
				String charsetType = ccse.getCharSetSpec().toLowerCase();
				int pos = charsetType.indexOf("charset");
				if(pos == -1)
				{
					throw ccse;
				}
				while(pos < charsetType.length() && charsetType.charAt(pos) != '=')
				{
					pos++;
				}
				pos++; // Places file cursor past the equals sign (=)
				String whatEncoding = charsetType.substring(pos).trim();
				loadDocument(whatFile, whatEncoding, cb);
			}
		}
		refreshOnUpdate();
	}

	/**
	 * Method for loading HTML document
	 */
	public void loadDocument(File whatFile)
	throws IOException, BadLocationException
	{
		this.loadDocument(whatFile, (HTMLEditorKit.ParserCallback)null);
	}

	private void loadDocument(File whatFile, String whatEncoding)
	throws IOException, BadLocationException
	{
		this.loadDocument(whatFile, whatEncoding, (HTMLEditorKit.ParserCallback)null);
	}

	public void loadDocument(File whatFile, HTMLEditorKit.ParserCallback cb)
	throws IOException, BadLocationException
	{
		loadDocument(whatFile, null, cb);
	}

	/**
	 * Method for loading HTML document into the app, including document
	 * encoding setting
	 */
	private void loadDocument(File whatFile, String whatEncoding, HTMLEditorKit.ParserCallback cb)
	throws IOException, BadLocationException
	{
		Reader rp = null;
		Reader rr = null;
		htmlDoc = (ExtendedHTMLDocument)(htmlKit.createDefaultDocument());
		htmlDoc.putProperty("com.hexidec.ekit.docsource", whatFile.toString());
		try
		{
			if(whatEncoding == null)
			{
				rp = new InputStreamReader(new FileInputStream(whatFile));
				rr = new InputStreamReader(new FileInputStream(whatFile));
			}
			else
			{
				rp = new InputStreamReader(new FileInputStream(whatFile), whatEncoding);
				rr = new InputStreamReader(new FileInputStream(whatFile), whatEncoding);
			}
			htmlDoc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
			htmlDoc.setPreservesUnknownTags(preserveUnknownTags);
			if(cb != null)
			{
				HTMLEditorKit.Parser parser = htmlDoc.getParser();
				parser.parse(rp, cb, true);
				rp.close();
			}
			htmlKit.read(rr, htmlDoc, 0);
			registerDocument(htmlDoc);
			jtpSource.setText(jtpMain.getText());
			currentFile = whatFile;
			updateTitle();
		}
		finally
		{
			if(rr != null)
			{
				rr.close();
			}
		}
	}

	/** Method for loading a Base64 encoded document
	  */
	private void openDocumentBase64(File whatFile)
	throws IOException, BadLocationException
	{
		if(whatFile == null)
		{
			whatFile = getFileFromChooser(".", JFileChooser.OPEN_DIALOG, extsB64, Translatrix.getTranslationString("FiletypeB64"));
		}
		if(whatFile != null)
		{
			FileReader fr = new FileReader(whatFile);
			int nextChar = 0;
			StringBuffer encodedText = new StringBuffer();
			try
			{
				while((nextChar = fr.read()) != -1)
				{
					encodedText.append((char)nextChar);
				}
				fr.close();
				jtpSource.setText(Base64Codec.decode(encodedText.toString()));
				jtpMain.setText(jtpSource.getText());
				registerDocument((ExtendedHTMLDocument)(jtpMain.getDocument()));
			}
			finally
			{
				if(fr != null)
				{
					fr.close();
				}
			}
		}
	}

	/** Method for loading a Stylesheet into the app
	  */
	private void openStyleSheet(File fileCSS)
	throws IOException
	{
		if(fileCSS == null)
		{
			fileCSS = getFileFromChooser(".", JFileChooser.OPEN_DIALOG, extsCSS, Translatrix.getTranslationString("FiletypeCSS"));
		}
		if(fileCSS != null)
		{
			String currDocText = jtpMain.getText();
			htmlDoc = (ExtendedHTMLDocument)(htmlKit.createDefaultDocument());
			htmlDoc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
			htmlDoc.setPreservesUnknownTags(preserveUnknownTags);
			styleSheet = htmlDoc.getStyleSheet();
			URL cssUrl = fileCSS.toURI().toURL();
			InputStream is = cssUrl.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			styleSheet.loadRules(br, cssUrl);
			br.close();
			htmlDoc = new ExtendedHTMLDocument(styleSheet);
			registerDocument(htmlDoc);
			jtpMain.setText(currDocText);
			jtpSource.setText(jtpMain.getText());
		}
		refreshOnUpdate();
	}

	/** Method for serializing the document out to a file
	  */
	public void serializeOut(HTMLDocument doc)
	throws IOException
	{
		File whatFile = getFileFromChooser(".", JFileChooser.SAVE_DIALOG, extsSer, Translatrix.getTranslationString("FiletypeSer"));
		if(whatFile != null)
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(whatFile));
			oos.writeObject(doc);
			oos.flush();
			oos.close();
		}
		refreshOnUpdate();
	}

	/** Method for reading in a serialized document from a file
	  */
	public void serializeIn()
	throws IOException, ClassNotFoundException
	{
		File whatFile = getFileFromChooser(".", JFileChooser.OPEN_DIALOG, extsSer, Translatrix.getTranslationString("FiletypeSer"));
		if(whatFile != null)
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(whatFile));
			htmlDoc = (ExtendedHTMLDocument)(ois.readObject());
			ois.close();
			registerDocument(htmlDoc);
			validate();
		}
		refreshOnUpdate();
	}

	/** Method for obtaining a File for input/output using a JFileChooser dialog
	  */
	private File getFileFromChooser(String startDir, int dialogType, String[] exts, String desc)
	{
		JFileChooser jfileDialog = new JFileChooser(startDir);
		jfileDialog.setDialogType(dialogType);
		jfileDialog.setFileFilter(new MutableFilter(exts, desc));
		int optionSelected = JFileChooser.CANCEL_OPTION;
		if(dialogType == JFileChooser.OPEN_DIALOG)
		{
			optionSelected = jfileDialog.showOpenDialog(this);
		}
		else if(dialogType == JFileChooser.SAVE_DIALOG)
		{
			optionSelected = jfileDialog.showSaveDialog(this);
		}
		else // default to an OPEN_DIALOG
		{
			optionSelected = jfileDialog.showOpenDialog(this);
		}
		if(optionSelected == JFileChooser.APPROVE_OPTION)
		{
			return jfileDialog.getSelectedFile();
		}
		return (File)null;
	}

	/** Method for constructing an IMG tag from a local image using a custom dialog
	  */
	private void getImageFromChooser(String startDir, String[] exts, String desc)
	{
		ImageFileDialog imgFileDialog = new ImageFileDialog(this.getFrame(), startDir, exts, desc, "", Translatrix.getTranslationString("ImageDialogTitle"), true);
		imgFileDialog.setVisible(true);
		String decision = imgFileDialog.getDecisionValue();
		if(decision.equals(Translatrix.getTranslationString("DialogAccept")))
		{
			try
			{
				File whatImage = imgFileDialog.getImageFile();
				if(whatImage != null)
				{
					imageChooserStartDir = whatImage.getParent().toString();
					int caretPos = jtpMain.getCaretPosition();
					String sImgTag = "";
					if(imgFileDialog.getIncorporate())
						sImgTag = "<img src=\"data:image/jpeg;base64," + Load.FileInBase64(whatImage) + '"';
					else
						sImgTag = "<img src=\"" + whatImage + '"';
					if(imgFileDialog.getImageAlt() != null && imgFileDialog.getImageAlt().length() > 0) { sImgTag = sImgTag + " alt=\"" + imgFileDialog.getImageAlt() + '"'; }
					if(imgFileDialog.getImageWidth() != null && imgFileDialog.getImageWidth().length() > 0) { sImgTag = sImgTag + " width=\"" + imgFileDialog.getImageWidth() + '"'; }
					if(imgFileDialog.getImageHeight() != null && imgFileDialog.getImageHeight().length() > 0) { sImgTag = sImgTag + " height=\"" + imgFileDialog.getImageHeight() + '"'; }
					sImgTag = sImgTag + "/>";
					htmlKit.insertHTML(htmlDoc, caretPos, sImgTag, 0, 0, HTML.Tag.IMG);
					jtpMain.setCaretPosition(caretPos + 1);
					refreshOnUpdate();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace(System.out);
			}
		}
		imgFileDialog.dispose();
		imgFileDialog = null;
	}

	/** Method for describing the node hierarchy of the document
	  */
	private void describeDocument(StyledDocument doc)
	{
		Element[] elements = doc.getRootElements();
		for(Element elem : elements)
		{
			indent = indentStep;
			for(int j = 0; j < indent; j++) { System.out.print(" "); }
			System.out.print(elem);
			traverseElement(elem);
			System.out.println("");
		}
	}

	/** Traverses nodes for the describing method
	  */
	private void traverseElement(Element element)
	{
		indent += indentStep;
		for(int i = 0; i < element.getElementCount(); i++)
		{
			for(int j = 0; j < indent; j++) { System.out.print(" "); }
			System.out.print(element.getElement(i));
			traverseElement(element.getElement(i));
		}
		indent -= indentStep;
	}

	/** Convenience method for obtaining the WYSIWYG JTextPane
	  */
	public JTextPane getTextPane()
	{
		return jtpMain;
	}

	/** Convenience method for obtaining the Source JTextPane
	  */
	public JTextArea getSourcePane()
	{
		return jtpSource;
	}

	/** Convenience method for obtaining the application as a Frame
	  */
	public Frame getFrame()
	{
		return frameHandler;
	}

	/** Convenience method for setting the parent Frame
	  */
	public void setFrame(Frame parentFrame)
	{
		frameHandler = parentFrame;
	}

	/** Convenience method for obtaining the pre-generated menu bar
	  */
	public JMenuBar getMenuBar()
	{
		return jMenuBar;
	}

	/** Convenience method for obtaining a custom menu bar
	  */
	public JMenuBar getCustomMenuBar(Vector<String> vcMenus)
	{
		jMenuBar = new JMenuBar();
		for(int i = 0; i < vcMenus.size(); i++)
		{
			String menuToAdd = vcMenus.elementAt(i).toLowerCase();
			if(htMenus.containsKey(menuToAdd))
			{
				jMenuBar.add((JMenu)(htMenus.get(menuToAdd)));
			}
		}
		return jMenuBar;
	}

	/** Convenience method for creating the multiple toolbar set from a sequence string
	  */
	public void initializeMultiToolbars(String toolbarSeq)
	{
		ArrayList<Vector<String>> vcToolPicks = new ArrayList<Vector<String>>(3);
		vcToolPicks.add(0, new Vector<String>());
		vcToolPicks.add(1, new Vector<String>());
		vcToolPicks.add(2, new Vector<String>());

		int whichBar = 0;
		StringTokenizer stToolbars = new StringTokenizer(toolbarSeq.toUpperCase(), "|");
		while(stToolbars.hasMoreTokens())
		{
			String sKey = stToolbars.nextToken();
			if(sKey.equals("*"))
			{
				whichBar++;
				if(whichBar > 2)
				{
					whichBar = 2;
				}
			}
			else
			{
				vcToolPicks.get(whichBar).add(sKey);
			}
		}

		customizeToolBar(TOOLBAR_MAIN,   vcToolPicks.get(0), true);
		customizeToolBar(TOOLBAR_FORMAT, vcToolPicks.get(1), true);
		customizeToolBar(TOOLBAR_STYLES, vcToolPicks.get(2), true);
	}

	/** Convenience method for creating the single toolbar from a sequence string
	  */
	public void initializeSingleToolbar(String toolbarSeq)
	{
		Vector<String> vcToolPicks = new Vector<String>();
		StringTokenizer stToolbars = new StringTokenizer(toolbarSeq.toUpperCase(), "|");
		while(stToolbars.hasMoreTokens())
		{
			String sKey = stToolbars.nextToken();
			if(sKey.equals("*"))
			{
				// ignore "next toolbar" symbols in single toolbar processing
			}
			else
			{
				vcToolPicks.add(sKey);
			}
		}

		customizeToolBar(TOOLBAR_SINGLE, vcToolPicks, true);
	}

	/** Convenience method for obtaining the pre-generated toolbar
	  */
	public JToolBar getToolBar(boolean isShowing)
	{
		if(jToolBar != null)
		{
			jcbmiViewToolbar.setState(isShowing);
			return jToolBar;
		}
		return (JToolBar)null;
	}

	/** Convenience method for obtaining the pre-generated main toolbar
	  */
	public JToolBar getToolBarMain(boolean isShowing)
	{
		if(jToolBarMain != null)
		{
			jcbmiViewToolbarMain.setState(isShowing);
			return jToolBarMain;
		}
		return (JToolBar)null;
	}

	/** Convenience method for obtaining the pre-generated main toolbar
	  */
	public JToolBar getToolBarFormat(boolean isShowing)
	{
		if(jToolBarFormat != null)
		{
			jcbmiViewToolbarFormat.setState(isShowing);
			return jToolBarFormat;
		}
		return (JToolBar)null;
	}

	/** Convenience method for obtaining the pre-generated main toolbar
	  */
	public JToolBar getToolBarStyles(boolean isShowing)
	{
		if(jToolBarStyles != null)
		{
			jcbmiViewToolbarStyles.setState(isShowing);
			return jToolBarStyles;
		}
		return (JToolBar)null;
	}

	/** Convenience method for obtaining a custom toolbar
	  */
	public JToolBar customizeToolBar(int whichToolBar, Vector<String> vcTools, boolean isShowing)
	{
		JToolBar jToolBarX = new JToolBar(JToolBar.HORIZONTAL);
		jToolBarX.setFloatable(false);
		for(int i = 0; i < vcTools.size(); i++)
		{
			String toolToAdd = vcTools.elementAt(i).toUpperCase();
			if(toolToAdd.equals(KEY_TOOL_SEP))
			{
				jToolBarX.add(new JToolBar.Separator());
			}
			else if(htTools.containsKey(toolToAdd))
			{
				if(htTools.get(toolToAdd) instanceof JButtonNoFocus)
				{
					jToolBarX.add((JButtonNoFocus)(htTools.get(toolToAdd)));
				}
				else if(htTools.get(toolToAdd) instanceof JToggleButtonNoFocus)
				{
					jToolBarX.add((JToggleButtonNoFocus)(htTools.get(toolToAdd)));
				}
				else if(htTools.get(toolToAdd) instanceof JComboBoxNoFocus)
				{
					jToolBarX.add((JComboBoxNoFocus)(htTools.get(toolToAdd)));
				}
				else
				{
					jToolBarX.add((JComponent)(htTools.get(toolToAdd)));
				}
			}
		}
		if(whichToolBar == TOOLBAR_SINGLE)
		{
			jToolBar = jToolBarX;
			jToolBar.setVisible(isShowing);
			jcbmiViewToolbar.setSelected(isShowing);
			return jToolBar;
		}
		else if(whichToolBar == TOOLBAR_MAIN)
		{
			jToolBarMain = jToolBarX;
			jToolBarMain.setVisible(isShowing);
			jcbmiViewToolbarMain.setSelected(isShowing);
			return jToolBarMain;
		}
		else if(whichToolBar == TOOLBAR_FORMAT)
		{
			jToolBarFormat = jToolBarX;
			jToolBarFormat.setVisible(isShowing);
			jcbmiViewToolbarFormat.setSelected(isShowing);
			return jToolBarFormat;
		}
		else if(whichToolBar == TOOLBAR_STYLES)
		{
			jToolBarStyles = jToolBarX;
			jToolBarStyles.setVisible(isShowing);
			jcbmiViewToolbarStyles.setSelected(isShowing);
			return jToolBarStyles;
		}
		else
		{
			jToolBarMain = jToolBarX;
			jToolBarMain.setVisible(isShowing);
			jcbmiViewToolbarMain.setSelected(isShowing);
			return jToolBarMain;
		}
	}

	/** Convenience method for activating/deactivating formatting commands
	  * depending on the active editing pane
	  */
	private void setFormattersActive(boolean state)
	{
		actionFontBold.setEnabled(state);
		actionFontItalic.setEnabled(state);
		actionFontUnderline.setEnabled(state);
		actionFontStrike.setEnabled(state);
		actionFontSuperscript.setEnabled(state);
		actionFontSubscript.setEnabled(state);
		actionListUnordered.setEnabled(state);
		actionListOrdered.setEnabled(state);
		actionSelectFont.setEnabled(state);
		actionAlignLeft.setEnabled(state);
		actionAlignCenter.setEnabled(state);
		actionAlignRight.setEnabled(state);
		actionAlignJustified.setEnabled(state);
		actionInsertAnchor.setEnabled(state);
		jbtnUnicode.setEnabled(state);
		jbtnUnicodeMath.setEnabled(state);
		jcmbStyleSelector.setEnabled(state);
		jcmbFontSelector.setEnabled(state);
		jMenuFont.setEnabled(state);
		jMenuFormat.setEnabled(state);
		jMenuInsert.setEnabled(state);
		jMenuTable.setEnabled(state);
		jMenuForms.setEnabled(state);
	}

	/** Convenience method for obtaining the current file handle
	  */
	public File getCurrentFile()
	{
		return currentFile;
	}

	/** Convenience method for obtaining the application name
	  */
	public String getAppName()
	{
		return appName;
	}

	/** Convenience method for obtaining the document text
	  */
	public String getDocumentText()
	{
		if(isSourceWindowActive())
		{
			return jtpSource.getText();
		}
		else
		{
			return jtpMain.getText();
		}
	}

	/** Convenience method for obtaining the document text
	  * contained within a tag pair
	  */
	public String getDocumentSubText(String tagBlock)
	{
		return getSubText(tagBlock);
	}

	/** Method for extracting the text within a tag
	  */
	private String getSubText(String containingTag)
	{
		jtpSource.setText(jtpMain.getText());
		String docTextCase = jtpSource.getText().toLowerCase();
		int tagStart       = docTextCase.indexOf("<" + containingTag.toLowerCase());
		int tagStartClose  = docTextCase.indexOf(">", tagStart) + 1;
		String closeTag    = "</" + containingTag.toLowerCase() + ">";
		int tagEndOpen     = docTextCase.indexOf(closeTag);
		if(tagStartClose < 0) { tagStartClose = 0; }
		if(tagEndOpen < 0 || tagEndOpen > docTextCase.length()) { tagEndOpen = docTextCase.length(); }
		return jtpSource.getText().substring(tagStartClose, tagEndOpen);
	}

	/** Convenience method for obtaining the document text
		* contained within the BODY tags (a common request)
	  */
	public String getDocumentBody()
	{
		return getSubText("body");
	}

	/** Convenience method for setting the document text
	  */
	public void setDocumentText(String sText)
	{
		jtpMain.setText(sText);
		((HTMLEditorKit)(jtpMain.getEditorKit())).setDefaultCursor(new Cursor(Cursor.TEXT_CURSOR));
		jtpSource.setText(jtpMain.getText());
	}

	/** Convenience method for setting the source document
	  */
	public void setSourceDocument(StyledDocument sDoc)
	{
		jtpSource.getDocument().removeDocumentListener(this);
		jtpSource.setDocument(sDoc);
		jtpSource.getDocument().addDocumentListener(this);
		jtpMain.setText(jtpSource.getText());
		((HTMLEditorKit)(jtpMain.getEditorKit())).setDefaultCursor(new Cursor(Cursor.TEXT_CURSOR));
	}

	/** Convenience method for communicating the current font selection to the CustomAction class
	  */
	public String getFontNameFromSelector()
	{
		if(jcmbFontSelector == null || jcmbFontSelector.getSelectedItem().equals(Translatrix.getTranslationString("SelectorToolFontsDefaultFont")))
		{
			return (String)null;
		}
		else
		{
			return jcmbFontSelector.getSelectedItem().toString();
		}
	}

	/** Convenience method for obtaining the document text
	  */
	private void updateTitle()
	{
		frameHandler.setTitle(appName + (currentFile == null ? "" : " - " + currentFile.getName()));
	}

	/** Convenience method for clearing out the UndoManager
	  */
	public void purgeUndos()
	{
		if(undoMngr != null)
		{
			undoMngr.discardAllEdits();
			undoAction.updateUndoState();
			redoAction.updateRedoState();
		}
	}

	/** Convenience method for refreshing and displaying changes
	  */
	public void refreshOnUpdate()
	{
		int caretPos = jtpMain.getCaretPosition();
		jtpMain.setText(jtpMain.getText());
		jtpSource.setText(jtpMain.getText());
		jtpMain.setText(jtpSource.getText());
		try { jtpMain.setCaretPosition(caretPos); } catch(IllegalArgumentException iea) { /* caret position bad, probably follows a deletion */ }
		this.repaint();
	}

	/* WindowListener methods */
	public void windowClosing(WindowEvent we)
	{
		this.dispose();
	}

	public void windowOpened(WindowEvent we)      { ; }
	public void windowClosed(WindowEvent we)      { ; }
	public void windowActivated(WindowEvent we)   { ; }
	public void windowDeactivated(WindowEvent we) { ; }
	public void windowIconified(WindowEvent we)   { ; }
	public void windowDeiconified(WindowEvent we) { ; }

	protected boolean shouldExitAndSave()
	{
		if(!modified)
			return true;

		Object[] options = {
			Translatrix.getTranslationString("SaveDialogYes"),
			Translatrix.getTranslationString("SaveDialogNo"),
			Translatrix.getTranslationString("SaveDialogCancel")
		};

		int save = JOptionPane.showOptionDialog(this,
			Translatrix.getTranslationString("SaveDialogBody"),
			Translatrix.getTranslationString("SaveDialogTitle"),
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[2]
		);

		if(save == 1)
			return true;
		if(save == 2)
			return false;

		try {
			saveDocument();
		}catch (Exception e){
			System.err.println(e.getMessage());
		}

		return true;
	}

	/** Convenience method for deallocating the app resources
	  */
	public void dispose()
	{
		if(shouldExitAndSave()) {
			frameHandler.dispose();
			System.exit(0);
		}
	}

	/** Convenience method for outputting exceptions
	  */
	private void logException(String internalMessage, Exception e)
	{
		System.err.println(internalMessage);
		e.printStackTrace(System.err);
	}

	/** Convenience method for determining if the source window is active
	  */
	private boolean isSourceWindowActive()
	{
		return (jspSource != null && jspSource == jspltDisplay.getRightComponent());
	}

	/** Method for toggling source window visibility
	  */
	private void toggleSourceWindow()
	{
		if(!(isSourceWindowActive()))
		{
			jtpSource.setText(jtpMain.getText());
			jspltDisplay.setRightComponent(jspSource);
			if(exclusiveEdit)
			{
				jspltDisplay.setDividerLocation(0);
				jspltDisplay.setEnabled(false);
				jtpSource.requestFocus();
			}
			else
			{
				jspltDisplay.setDividerLocation(iSplitPos);
				jspltDisplay.setEnabled(true);
			}
		}
		else
		{
			jtpMain.setText(jtpSource.getText());
			iSplitPos = jspltDisplay.getDividerLocation();
			jspltDisplay.remove(jspSource);
			jtpMain.requestFocus();
		}
		this.validate();
		jcbmiViewSource.setSelected(isSourceWindowActive());
		jtbtnViewSource.setSelected(isSourceWindowActive());
	}

	/** Searches the specified element for CLASS attribute setting
	  */
	private String findStyle(Element element)
	{
		AttributeSet as = element.getAttributes();
		if(as == null)
		{
			return null;
		}
		Object val = as.getAttribute(HTML.Attribute.CLASS);
		if(val != null && (val instanceof String))
		{
			return (String)val;
		}
		for(Enumeration e = as.getAttributeNames(); e.hasMoreElements();)
		{
			Object key = e.nextElement();
			if(key instanceof HTML.Tag)
			{
				AttributeSet eas = (AttributeSet)(as.getAttribute(key));
				if(eas != null)
				{
					val = eas.getAttribute(HTML.Attribute.CLASS);
					if(val != null)
					{
						return (String)val;
					}
				}
			}

		}
		return null;
	}

	/** Handles caret tracking and related events, such as displaying the current style
	  * of the text under the caret
	  */
	private void handleCaretPositionChange(CaretEvent ce)
	{
		int caretPos = ce.getDot();
		Element	element = htmlDoc.getCharacterElement(caretPos);
/*
//---- TAG EXPLICATOR CODE -------------------------------------------
		javax.swing.text.ElementIterator ei = new javax.swing.text.ElementIterator(htmlDoc);
		Element ele;
		while((ele = ei.next()) != null)
		{
			System.out.println("ELEMENT : " + ele.getName());
		}
		System.out.println("ELEMENT:" + element.getName());
		Element elementParent = element.getParentElement();
		System.out.println("ATTRS:");
		AttributeSet attribs = elementParent.getAttributes();
		for(Enumeration eAttrs = attribs.getAttributeNames(); eAttrs.hasMoreElements();)
		{
			System.out.println("  " + eAttrs.nextElement().toString());
		}
		while(elementParent != null && !elementParent.getName().equals("body"))
		{
			String parentName = elementParent.getName();
			System.out.println("PARENT:" + parentName);
			System.out.println("ATTRS:");
			attribs = elementParent.getAttributes();
			for(Enumeration eAttr = attribs.getAttributeNames(); eAttr.hasMoreElements();)
			{
				System.out.println("  " + eAttr.nextElement().toString());
			}
			elementParent = elementParent.getParentElement();
		}
//---- END -------------------------------------------
*/
		if(jtpMain.hasFocus())
		{
			if(element == null)
			{
				return;
			}
			String style = null;
			Vector<Element> vcStyles = new Vector<Element>();
			while(element != null)
			{
				if(style == null)
				{
					style = findStyle(element);
				}
				vcStyles.add(element);
				element = element.getParentElement();
			}
			int stylefound = -1;
			if(style != null)
			{
				for(int i = 0; i < jcmbStyleSelector.getItemCount(); i++)
				{
					String in = (String)(jcmbStyleSelector.getItemAt(i));
					if(in.equalsIgnoreCase(style))
					{
						stylefound = i;
						break;
					}
				}
			}
			if(stylefound > -1)
			{
				jcmbStyleSelector.getAction().setEnabled(false);
				jcmbStyleSelector.setSelectedIndex(stylefound);
				jcmbStyleSelector.getAction().setEnabled(true);
			}
			else
			{
				jcmbStyleSelector.setSelectedIndex(0);
			}
			// see if current font face is set
			if(jcmbFontSelector != null && jcmbFontSelector.isVisible())
			{
				AttributeSet mainAttrs = jtpMain.getCharacterAttributes();
				Enumeration e = mainAttrs.getAttributeNames();
				Object activeFontName = (Object)(Translatrix.getTranslationString("SelectorToolFontsDefaultFont"));
				while(e.hasMoreElements())
				{
					Object nexte = e.nextElement();
					if(nexte.toString().toLowerCase().equals("face") || nexte.toString().toLowerCase().equals("font-family"))
					{
						activeFontName = mainAttrs.getAttribute(nexte);
						break;
					}
				}
				jcmbFontSelector.getAction().setEnabled(false);
				jcmbFontSelector.getModel().setSelectedItem(activeFontName);
				jcmbFontSelector.getAction().setEnabled(true);
			}
		}
	}

	/** Utility methods
	  */
	public ExtendedHTMLDocument getExtendedHtmlDoc()
	{
		return (ExtendedHTMLDocument)htmlDoc;
	}

	public int getCaretPosition()
	{
		return jtpMain.getCaretPosition();
	}

	public void setCaretPosition(int newPositon)
	{
		boolean end;
		do
		{
			end = true;
			try
			{
				jtpMain.setCaretPosition(newPositon);
			}
			catch(IllegalArgumentException iae)
			{
				end = false;
				newPositon--;
			}
		} while(!end && newPositon >= 0);
	}

	/** Accessors for enter key behaviour flag
	  */
	  public boolean getEnterKeyIsBreak()
	  {
	  	return enterIsBreak;
	  }

	  public void setEnterKeyIsBreak(boolean b)
	  {
	  	enterIsBreak = b;
		jcbmiEnterKeyParag.setSelected(!enterIsBreak);
		jcbmiEnterKeyBreak.setSelected(enterIsBreak);
	  }

/* Inner Classes --------------------------------------------- */

	/** Class for implementing Undo as an autonomous space.leandragem.ekitten.action
	  */
	class UndoAction extends AbstractAction
	{
		public UndoAction()
		{
			super(Translatrix.getTranslationString("Undo"));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e)
		{
			try
			{
				undoMngr.undo();
			}
			catch(CannotUndoException ex)
			{
				ex.printStackTrace();
			}
			updateUndoState();
			redoAction.updateRedoState();
		}

		protected void updateUndoState()
		{
			setEnabled(undoMngr.canUndo());
		}
	}

	/** Class for implementing Redo as an autonomous space.leandragem.ekitten.action
	  */
	class RedoAction extends AbstractAction
	{
		public RedoAction()
		{
			super(Translatrix.getTranslationString("Redo"));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e)
		{
			try
			{
				undoMngr.redo();
			}
			catch(CannotUndoException ex)
			{
				ex.printStackTrace();
			}
			updateRedoState();
			undoAction.updateUndoState();
		}

		protected void updateRedoState()
		{
			setEnabled(undoMngr.canRedo());
		}
	}

	/** Class for implementing the Undo listener to handle the Undo and Redo actions
	  */
	class CustomUndoableEditListener implements UndoableEditListener
	{
		public void undoableEditHappened(UndoableEditEvent uee)
		{
			undoMngr.addEdit(uee.getEdit());
			undoAction.updateUndoState();
			redoAction.updateRedoState();
		}
	}

}
