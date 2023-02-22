package by.iba.vfdbapi.dao.answers;

import com.ibm.cloud.objectstorage.services.s3.AmazonS3;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static org.mockito.Mockito.RETURNS_DEFAULTS;
import static org.mockito.Mockito.mock;

public class AWSBuilderAnswer implements Answer<Object> {

    public Object answer(InvocationOnMock invocation) throws Throwable {
        Object mock = invocation.getMock();
        Method method = invocation.getMethod();
        if(method.getReturnType().isInstance(mock) && !method.getName().equals("build")){
            return mock;
        } else {
            if(method.getName().equals("build")) {
                return mock(AmazonS3.class);
            }
        }
        return RETURNS_DEFAULTS.answer(invocation);
    }
}
