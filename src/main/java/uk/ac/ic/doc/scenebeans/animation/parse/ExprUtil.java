package uk.ac.ic.doc.scenebeans.animation.parse;

import gnu.jel.CompilationException;
import gnu.jel.CompiledExpression;
import gnu.jel.Evaluator;
import gnu.jel.Library;

public class ExprUtil {
    static Library lib = new Library(new Class[]{Math.class, ExprUtil.class}, null);
    static {
        try {
            lib.markStateDependent("random", null);
        } catch (NoSuchMethodException noSuchMethodException) {
            throw new NoSuchMethodError("no random method in java.lang.Math!");
        }
    }
    public static double pi = Math.PI;
    public static double e = Math.E;

    public static double evaluate(String paramString) throws IllegalArgumentException {
        try {
            CompiledExpression compiledExpression = Evaluator.compile(paramString, lib);
            try {
                Object object = compiledExpression.evaluate(null);
                if (object == null)
                    throw new IllegalArgumentException("void expression");
                if (object instanceof Number)
                    return ((Number) object).doubleValue();
                throw new IllegalArgumentException("not a number");
            } catch (Throwable throwable) {
                throw new IllegalArgumentException("couldn't evaluate expression " + paramString + ": " + throwable.getMessage());
            }
        } catch (CompilationException compilationException) {
            throw new IllegalArgumentException("couldn't compile expression " + paramString + ": " + compilationException.getMessage());
        }
    }
}
