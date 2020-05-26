package com.hexidec.ekit.editor;

import com.hexidec.ekit.EkitCore;
import com.hexidec.ekit.component.JButtonNoFocus;
import com.hexidec.util.Load;
import com.hexidec.util.Translatrix;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.Hashtable;

import static com.hexidec.ekit.editor.Command.*;

public class Toolbar {
    // Tool Keys
    public static final String KEY_TOOL_SEP       = "SP";
    public static final String KEY_TOOL_NEW       = "NW";
    public static final String KEY_TOOL_NEWSTYLED = "NS";
    public static final String KEY_TOOL_OPEN      = "OP";
    public static final String KEY_TOOL_SAVE      = "SV";
    public static final String KEY_TOOL_PRINT     = "PR";
    public static final String KEY_TOOL_CUT       = "CT";
    public static final String KEY_TOOL_COPY      = "CP";
    public static final String KEY_TOOL_PASTE     = "PS";
    public static final String KEY_TOOL_PASTEX    = "PX";
    public static final String KEY_TOOL_UNDO      = "UN";
    public static final String KEY_TOOL_REDO      = "RE";
    public static final String KEY_TOOL_BOLD      = "BL";
    public static final String KEY_TOOL_ITALIC    = "IT";
    public static final String KEY_TOOL_UNDERLINE = "UD";
    public static final String KEY_TOOL_STRIKE    = "SK";
    public static final String KEY_TOOL_SUPER     = "SU";
    public static final String KEY_TOOL_SUB       = "SB";
    public static final String KEY_TOOL_ULIST     = "UL";
    public static final String KEY_TOOL_OLIST     = "OL";
    public static final String KEY_TOOL_ALIGNL    = "AL";
    public static final String KEY_TOOL_ALIGNC    = "AC";
    public static final String KEY_TOOL_ALIGNR    = "AR";
    public static final String KEY_TOOL_ALIGNJ    = "AJ";
    public static final String KEY_TOOL_UNICODE   = "UC";
    public static final String KEY_TOOL_UNIMATH   = "UM";
    public static final String KEY_TOOL_FIND      = "FN";
    public static final String KEY_TOOL_ANCHOR    = "LK";
    public static final String KEY_TOOL_SOURCE    = "SR";
    public static final String KEY_TOOL_STYLES    = "ST";
    public static final String KEY_TOOL_FONTS     = "FO";
    public static final String KEY_TOOL_INSTABLE  = "TI";
    public static final String KEY_TOOL_EDITTABLE = "TE";
    public static final String KEY_TOOL_EDITCELL  = "CE";
    public static final String KEY_TOOL_INSERTROW = "RI";
    public static final String KEY_TOOL_INSERTCOL = "CI";
    public static final String KEY_TOOL_DELETEROW = "RD";
    public static final String KEY_TOOL_DELETECOL = "CD";

    public static final String TOOLBAR_DEFAULT_MULTI  = "NW|NS|OP|SV|PR|SP|CT|CP|PS|SP|UN|RE|SP|FN|SP|UC|UM|SP|SR|*|BL|IT|UD|SP|SK|SU|SB|SP|AL|AC|AR|AJ|SP|UL|OL|SP|LK|*|ST|SP|FO";
    public static final String TOOLBAR_DEFAULT_SINGLE = "NW|NS|OP|SV|PR|SP|CT|CP|PS|SP|UN|RE|SP|BL|IT|UD|SP|FN|SP|UC|SP|LK|SP|SR|SP|ST";

    public static final int TOOLBAR_SINGLE = 0;
    public static final int TOOLBAR_MAIN   = 1;
    public static final int TOOLBAR_FORMAT = 2;
    public static final int TOOLBAR_STYLES = 3;

    protected EkitCore editor;
    protected static Hashtable<String, JComponent> htTools = new Hashtable<String, JComponent>();

    public Toolbar(EkitCore core)
    {
        editor = core;

        JButtonNoFocus btn = null;
        for(Field f: getClass().getFields()){
            btn = new JButtonNoFocus(Load.EkitIcon("Print"));
            btn.setToolTipText(Translatrix.getTranslationString("PrintDocument"));
            btn.setActionCommand(CMD_DOC_PRINT);
            btn.addActionListener(editor);
            htTools.put(KEY_TOOL_PRINT, btn);
        }
    }

}
