package uk.ac.ic.doc.scenebeans.animation.parse;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import uk.ac.ic.doc.natutil.StringParser;

class ValueParser extends StringParser {
    ValueParser(URL paramURL) {
        StringParser.TypeSpecificParser typeSpecificParser1 = new StringParser.TypeSpecificParser(this) {
            private final ValueParser this$0;

            public Object parse(String param1String) throws IllegalArgumentException {
                return new Double(ExprUtil.evaluate(param1String));
            }
        };
        addParser(Double.class, typeSpecificParser1);
        addParser(double.class, typeSpecificParser1);
        StringParser.TypeSpecificParser typeSpecificParser2 = new StringParser.TypeSpecificParser(this) {
            private final ValueParser this$0;

            public Object parse(String param1String) throws IllegalArgumentException {
                return new Float((float) ExprUtil.evaluate(param1String));
            }
        };
        addParser(Float.class, typeSpecificParser2);
        addParser(float.class, typeSpecificParser2);
        StringParser.TypeSpecificParser typeSpecificParser3 = new StringParser.TypeSpecificParser(this) {
            private final ValueParser this$0;

            public Object parse(String param1String) throws IllegalArgumentException {
                return new Integer((int) Math.floor(ExprUtil.evaluate(param1String)));
            }
        };
        addParser(Integer.class, typeSpecificParser3);
        addParser(int.class, typeSpecificParser3);
        addParser(Point2D.class, new StringParser.TypeSpecificParser(this) {
            private final ValueParser this$0;

            public Object parse(String param1String) throws IllegalArgumentException {
                check((param1String.charAt(0) == '(' && param1String.charAt(param1String.length() - 1) == ')'));
                StringTokenizer stringTokenizer = new StringTokenizer(param1String.substring(1, param1String.length() - 1), ",");
                String str1 = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : null;
                String str2 = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : null;
                check((str1 != null || str2 != null || !stringTokenizer.hasMoreTokens()));
                double d1 = ExprUtil.evaluate(str1);
                double d2 = ExprUtil.evaluate(str2);
                return new Point2D.Double(d1, d2);
            }

            void check(boolean param1Boolean) {
                if (!param1Boolean)
                    throw new IllegalArgumentException("invalid point value");
            }
        });
        addParser(Font.class, new StringParser.TypeSpecificParser(this) {
            private final ValueParser this$0;

            public Object parse(String param1String) throws IllegalArgumentException {
                return Font.decode(param1String);
            }
        });
        addParser(Color.class, new StringParser.TypeSpecificParser(this) {
            private final ValueParser this$0;

            public Object parse(String param1String) throws IllegalArgumentException {
                check((param1String.length() >= 6 && param1String.length() <= 8 && param1String.length() % 2 == 0));
                int i = Integer.parseInt(param1String.substring(0, 2), 16);
                int j = Integer.parseInt(param1String.substring(2, 4), 16);
                int k = Integer.parseInt(param1String.substring(4, 6), 16);
                boolean bool = (param1String.length() == 8) ? Integer.parseInt(param1String.substring(6, 8), 16) : true;
                return new Color(i, j, k, bool);
            }

            void check(boolean param1Boolean) {
                if (!param1Boolean)
                    throw new IllegalArgumentException("invalid color value");
            }
        });
        addParser(URL.class, new StringParser.TypeSpecificParser(this, paramURL) {
            private final URL val$document_base_url;
            private final ValueParser this$0;

            public Object parse(String param1String) throws IllegalArgumentException {
                try {
                    return new URL(this.val$document_base_url, param1String);
                } catch (MalformedURLException malformedURLException) {
                    throw new IllegalArgumentException("invalid URL value");
                }
            }
        });
    }
}
