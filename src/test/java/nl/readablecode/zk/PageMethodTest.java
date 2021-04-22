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
        PageMethod pageMethod = new PageMethod(getServiceMethod(), of("prefix"), "{first}/middle/{last}");
        TestClass testInstance = new TestClass();
        Page page = Mockito.mock(Page.class);
        pageMethod.invoke(testInstance, "/prefix/123/middle/456", page);
        assertEquals("123", testInstance.first);
        assertEquals(page, testInstance.page);
        assertEquals("456", testInstance.last);
    }

    private Method getServiceMethod() throws NoSuchMethodException {
        return TestClass.class.getMethod("service", String.class, Page.class, String.class);
    }

    static class TestClass {
        String first;
        Page page;
        String last;

        public void service(@PathVariable("first") String first,
                     Page page,
                     @PathVariable("last") String last) {
            this.first = first;
            this.page = page;
            this.last = last;
        }
    }
}
