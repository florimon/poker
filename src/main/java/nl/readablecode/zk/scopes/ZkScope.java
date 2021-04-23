package nl.readablecode.zk.scopes;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.WebApp;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

/**
 * <p>A Generic implementation of a Spring {@link Scope}.</p>
 *
 * @param <C>   generic type of the thread local 'context' that will supply the 'subcontext' S;
 *              In non-test code, the C type parameter will always be {@link Execution}, the
 *              only reason that it is not hardcoded, is to facilitate unit testing.
 *
 * @param <S>   generic type of the subcontext that will store the Map specific to this scope;
 *              this can be either {@link Execution},{@link WebApp}, {@link Desktop} or {@link Page}
 *
 *
 * @author florimon
 */
@RequiredArgsConstructor
class ZkScope<C, S> implements Scope {
    private final String scopeId;
    private final Supplier<C> threadLocalContextSupplier;
    private final Function<C, S> subContextFunction;
    private final AttributeGetter<S> attributeGetter;
    private final AttributeSetter<S> attributeSetter;
    private final Function<S, String> idFunction;

    interface AttributeGetter<T> {
        Object get(T t, String name);
    }
    interface AttributeSetter<T> {
        Object set(T t, String name, Object value);
    }

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        return getScopeMap(getScopeContext()).computeIfAbsent(name, notUsed -> objectFactory.getObject());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getScopeMap(S context) {
        return ofNullable((Map<String,Object>) attributeGetter.get(context, scopeId)).orElseGet(() -> {
            HashMap<String, Object> scopeMap = new HashMap<>();
            attributeSetter.set(context, scopeId, scopeMap);
            return scopeMap;
        });
    }

    private S getScopeContext() {
        return ofNullable(threadLocalContextSupplier.get()).map(subContextFunction)
                .orElseThrow(() -> new IllegalStateException("Unable to get current Execution"));
    }

    @Override
    public Object remove(String name) {
        return getScopeMap(getScopeContext()).remove(name);
    }

    @Override
    public String getConversationId() {
        return idFunction.apply(getScopeContext());
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) { // no implementation.
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }
}
