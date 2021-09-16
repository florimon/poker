package nl.readablecode.zkspring.scopes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ZkScopeTest {

    @Getter @RequiredArgsConstructor
    static class Context {
        final SubContext subContext;
    }

    @Getter @RequiredArgsConstructor
    static class SubContext extends HashMap<String, Object> {
        final String id;
    }

    private String scopeName = "testScope";
    private String conversationId = "subContext";
    private SubContext subContext = new SubContext(conversationId);
    private Context context = new Context(subContext);

    private ZkScope<Context, SubContext> scope = new ZkScope<>(scopeName,
            () -> context, Context::getSubContext, SubContext::get, SubContext::put, SubContext::getId);

    @Test
    public void scopeShouldCacheObjects() {
        String name = "key";
        assertEquals(123, scope.get(name, () -> 123));
        assertEquals(123, scope.get(name, () -> 456));  // already cached in scope
    }

    @Test
    @SuppressWarnings("unchecked")
    public void scopeShouldNotRetainObjectsAfterRemove() {
        String name = "key";
        assertEquals(123, scope.get(name, () -> 123));
        scope.remove(name);
        Map<String, Object> testScope = (Map<String, Object>) context.getSubContext().get(scopeName);
        assertFalse(testScope.containsKey(name));
    }

    @Test
    public void scopeShouldReturnItsConversationId() {
        assertEquals(conversationId, scope.getConversationId());
    }
}
