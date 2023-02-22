package by.iba.vfdbapi.dao.answers;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.RETURNS_DEFAULTS;

public class BuilderAnswer implements Answer<Object> {

    public Object answer(InvocationOnMock invocation) throws Throwable {
        Object mock = invocation.getMock();
        if(invocation.getMethod().getReturnType().isInstance(mock)){
            return mock;
        }
        return RETURNS_DEFAULTS.answer(invocation);
    }
}
