package jsc.org.lib.basic.utils;

import android.text.Html;
import android.text.Spanned;

import java.util.Locale;

public final class HtmlUtils {

    public final static String QUOTE = "&quot;";//"
    public final static String AND = "&amp;";//&
    public final static String LESS_THAN = "&lt;";//<
    public final static String LESS_THAN_OR_EQUAL = LESS_THAN + "=";//<=
    public final static String MORE_THAN = "&gt;";//>
    public final static String MORE_THAN_OR_EQUAL = MORE_THAN + "=";//>=
    public final static String NEXT_LINE = "<br/>";//换行
    public final static String PARAGRAPH_INDENT = "&ensp;&ensp;";//段落缩进

    /**
     * 文本颜色
     */
    public static String colorText(String text, String color) {
        return String.format(Locale.US, "<font color=\"%s\">%s</font>", color, text);
    }

    /**
     * 文本加粗
     */
    public static String strongText(String text) {
        return String.format(Locale.US, "<strong>%s</strong>", text);
    }

    /**
     * 文本斜体
     */
    public static String italicsText(String text) {
        return String.format(Locale.US, "<i>%s</i>", text);
    }

    /**
     * 文本下划线
     */
    public static String underlineText(String text) {
        return String.format(Locale.US, "<u>%s</u>", text);
    }

    public static Spanned toHtml(StringBuffer buffer) {
        return toHtml(buffer.toString());
    }

    public static Spanned toHtml(StringBuilder builder) {
        return toHtml(builder.toString());
    }

    public static Spanned toHtml(String html) {
        return Html.fromHtml(html);
    }

    public static class Builder {

        StringBuilder mBuilder = null;

        public Builder() {
            mBuilder = new StringBuilder();
        }

        public Builder paragraphIndent() {
            mBuilder.append(PARAGRAPH_INDENT);
            return this;
        }

        public Builder quote(CharSequence text) {
            mBuilder.append(QUOTE).append(text).append(QUOTE);
            return this;
        }

        public Builder and(CharSequence text) {
            mBuilder.append(AND).append(text);
            return this;
        }

        public Builder lessThan(CharSequence text) {
            mBuilder.append(LESS_THAN).append(text);
            return this;
        }

        public Builder lessThanOrEqual(CharSequence text) {
            mBuilder.append(LESS_THAN_OR_EQUAL).append(text);
            return this;
        }

        public Builder moreThan(CharSequence text) {
            mBuilder.append(MORE_THAN).append(text);
            return this;
        }

        public Builder moreThanOrEqual(CharSequence text) {
            mBuilder.append(MORE_THAN_OR_EQUAL).append(text);
            return this;
        }

        public Builder nextLine() {
            mBuilder.append(NEXT_LINE);
            return this;
        }

        public Builder twoBlanks() {
            mBuilder.append(PARAGRAPH_INDENT);
            return this;
        }

        public Builder text(CharSequence text) {
            mBuilder.append(text);
            return this;
        }

        public Builder labelText(String label, String text) {
            mBuilder.append(String.format(Locale.US, "<%s>%s</%s>", label, text, label));
            return this;
        }

        public Builder colorText(String text, String color) {
            mBuilder.append(HtmlUtils.colorText(text, color));
            return this;
        }

        public Builder strongText(String text) {
            mBuilder.append(HtmlUtils.strongText(text));
            return this;
        }

        public Builder italicsText(String text) {
            mBuilder.append(HtmlUtils.italicsText(text));
            return this;
        }

        public Builder underlineText(String text) {
            mBuilder.append(HtmlUtils.underlineText(text));
            return this;
        }


        public StringBuilder build() {
            return mBuilder;
        }

        public Spanned toHtmlText(){
            return HtmlUtils.toHtml(mBuilder);
        }
    }
}
