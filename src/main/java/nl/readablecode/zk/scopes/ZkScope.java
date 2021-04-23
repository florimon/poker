package nl.readablecode.zk.scopes;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.WebApp;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

/**
 * A Generic Zk specific provider of Spring scopes.
 *
 * @param <T>   generic type of the 'context' where the scope will be stored:
 * {@link Execution}, {@link WebApp}, {@link Desktop} or {@link Page}
 *
 * @author florimon
 */
@RequiredArgsConstructor
class ZkScope<T> implements Scope {

    interface AttributeGetter<T> {
        Object get(T t, String name);
    }
    interface AttributeSetter<T> {
        Object set(T t, String name, Object value);
    }

    private final String scopeId;
    private final Function<Execution, T> executionFunction;
    private final AttributeGetter<T> attributeGetter;
    private final AttributeSetter<T> attributeSetter;
    private final Function<T, String> idFunction;

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        return getScopeMap(getContext()).computeIfAbsent(name, it -> objectFactory.getObject());
    }

    private Map<String, Object> getScopeMap(T context) {
        return ofNullable((Map<String,Object>) attributeGetter.get(context, scopeId)).orElseGet(() -> {
            HashMap<String, Object> scopeMap = new HashMap<>();
            attributeSetter.set(context, scopeId, scopeMap);
            return scopeMap;
        });
    }

    private T getContext() {
        return ofNullable(Executions.getCurrent()).map(executionFunction)
                .orElseThrow(() -> new IllegalStateException("Unable to get current Execution"));
    }

    @Override
    public Object remove(String name) {
        return getScopeMap(getContext()).remove(name);
    }

    @Override
    public String getConversationId() {
        return idFunction.apply(getContext());
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) { // no implementation.
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }
}
