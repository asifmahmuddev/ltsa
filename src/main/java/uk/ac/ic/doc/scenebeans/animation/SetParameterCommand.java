package uk.ac.ic.doc.scenebeans.animation;

import java.lang.reflect.Method;

public class SetParameterCommand implements Command {
    private Object _bean;
    private Method _set_method;
    private Object[] _set_args;

    public SetParameterCommand(Object paramObject1, Method paramMethod, Object paramObject2) {
        this._bean = paramObject1;
        this._set_method = paramMethod;
        this._set_args = new Object[]{paramObject2};
    }

    public void invoke() throws CommandException {
        try {
            this._set_method.invoke(this._bean, this._set_args);
        } catch (Exception exception) {
            throw new CommandException("failed to set parameter: " + exception.getMessage());
        }
    }
}
