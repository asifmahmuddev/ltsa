package gnu.jel;

import gnu.jel.generated.EC;
import gnu.jel.generated.ParseException;
import gnu.jel.generated.TokenMgrError;
import java.io.StringReader;

public class Evaluator {
    public static CompiledExpression compile(String paramString, Library paramLibrary) throws CompilationException {
        return compile(paramString, paramLibrary, null);
    }

    public static CompiledExpression compile(String paramString, Library paramLibrary, Class paramClass) throws CompilationException {
        return compileBits(paramString, paramLibrary, paramClass).getExpression();
    }

    public static ExpressionBits compileBits(String paramString, Library paramLibrary) throws CompilationException {
        return compileBits(paramString, paramLibrary, null);
    }

    public static ExpressionBits compileBits(String paramString, Library paramLibrary, Class paramClass) throws CompilationException {
        Optimizer optimizer = fillOptimizer(paramString, paramLibrary, paramClass);
        optimizer.optimize(1);
        return optimizer.compileBits();
    }

    static Optimizer fillOptimizer(String paramString, Library paramLibrary, Class paramClass) throws CompilationException {
        EC eC = new EC(new StringReader(paramString));
        Optimizer optimizer = new Optimizer(paramLibrary);
        try {
            eC.expression(optimizer, paramClass);
        } catch (ParseException parseException) {
            throw new CompilationException(parseException);
        } catch (TokenMgrError tokenMgrError) {
            throw new CompilationException(tokenMgrError);
        }
        return optimizer;
    }
}
