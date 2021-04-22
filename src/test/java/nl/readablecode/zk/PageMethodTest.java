package nl.readablecode.zk;

import static java.util.Optional.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.bind.annotation.PathVariable;
import org.zkoss.zk.ui.Page;

public class PageMethodTest {

    @Test
    public void normalizeShouldStandardizeSlashPlacement() {
        assertEquals("/", PageMethod.normalize(""));
        assertEquals("/", PageMethod.normalize("/"));
        assertEquals("/", PageMethod.normalize("//"));
        assertEquals("/path/{suffix}", PageMethod.normalize("//path//{suffix}//"));
    }

    @Test
    public void matchesShouldOnlyBeTrueForSamePathLengths() {
        PageMethod pageMethod = new PageMethod(null, of("prefix"), "{first}/middle/{last}");
        assertFalse(pageMethod.matches("/prefix"));
        assertFalse(pageMethod.matches("/prefix/123"));
        assertFalse(pageMethod.matches("/prefix/123/middle"));
        assertTrue(pageMethod.matches("/prefix/123/middle/456"));
        assertFalse(pageMethod.matches("/prefix/123/middle/456/789"));
    }

    @Test
    public void invokeShouldInvokeMethodWithRightArguments() throws Exception {
        PageMethod pageMethod = new PageMethod(getServiceMethod(), of("prefix"), "{long}/{string}/{boolean}");
        TestClass testInstance = new TestClass();
        Page page = Mockito.mock(Page.class);
        pageMethod.invoke(testInstance, "/prefix/123/middle/true", page);
        assertEquals(page, testInstance.page);
        assertEquals(123L, (long) testInstance.longVar);
        assertEquals("middle", testInstance.stringVar);
        assertEquals(true, testInstance.booleanVar);
    }

    private Method getServiceMethod() throws NoSuchMethodException {
        return TestClass.class.getMethod("service", Long.class, Page.class, Boolean.class, String.class);
    }

    static class TestClass {
        Long longVar;
        Page page;
        Boolean booleanVar;
        String stringVar;

        public void service( @PathVariable("long") Long longVar,
                             Page page,
                             @PathVariable("boolean") Boolean booleanVar,
                             @PathVariable("string") String stringVar) {
            this.longVar = longVar;
            this.page = page;
            this.booleanVar = booleanVar;
            this.stringVar = stringVar;
        }
    }
}
