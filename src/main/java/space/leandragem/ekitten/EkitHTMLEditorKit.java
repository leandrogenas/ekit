package space.leandragem.ekitten;

import space.leandragem.ekitten.component.ExtendedHTMLDocument;

import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class EkitHTMLEditorKit extends HTMLEditorKit {
    private static HTMLFactory factory = null;

    public Document createDefaultDocument()
    {
        StyleSheet styles = getStyleSheet();
        StyleSheet ss = new StyleSheet();
        ss.addStyleSheet(styles);
        ExtendedHTMLDocument doc = new ExtendedHTMLDocument(ss);
        doc.setParser(getParser());
        doc.setAsynchronousLoadPriority(4);
        doc.setTokenThreshold(100);
        return doc;
    }

    @Override
    public ViewFactory getViewFactory() {
        if (factory == null) {
            factory = new HTMLFactory() {

                @Override
                public View create(Element elem) {
                    AttributeSet attrs = elem.getAttributes();
                    Object elementName = attrs.getAttribute(AbstractDocument.ElementNameAttribute);
                    Object o = (elementName != null) ? null : attrs.getAttribute(StyleConstants.NameAttribute);
                    if (o instanceof HTML.Tag) {
                        HTML.Tag kind = (HTML.Tag) o;
                        if (kind == HTML.Tag.IMG) {
                            return new EkitImageView(elem);
                        }
                    }
                    return super.create(elem);
                }
            };
        }
        return factory;
    }
}
