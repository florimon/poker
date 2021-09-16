package nl.readablecode.zkspring.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

/**
 *
 * @author florimon
 */
public class AnnotationFinder {

    /**
     *
     * @param aClass
     * @param annotation
     * @param <A>
     * @return
     */
    public <A extends Annotation> Optional<A> findAnnotation(Class<?> aClass, Class<A> annotation) {
        return ofNullable(walkHierarchy(aClass, c -> c.isAnnotationPresent(annotation)))
                .map(c -> c.getAnnotation(annotation));
    }

    /**
     *
     * @param aClass
     * @param annotation
     * @param method
     * @param <A>
     * @return
     */
    public <A extends Annotation> Optional<Method> findAnnotatedMethod(Class<?> aClass, Class<A> annotation, Method method) {
        Function<Class<?>, Optional<Method>> getMethod = someClass -> getMethod(someClass, method);
        Function<Method, Boolean> methodPredicate = someMethod -> someMethod.isAnnotationPresent(annotation);
        return ofNullable(walkHierarchy(aClass, c -> getMethod.apply(c).map(methodPredicate).orElse(false)))
                .flatMap(getMethod);
    }

    private Optional<Method> getMethod(Class<?> aClass, Method method) {
        try {
            return Optional.of(aClass.getMethod(method.getName(), method.getParameterTypes()));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    private Class<?> walkHierarchy(Class<?> aClass, Predicate<Class<?>> predicate) {
        Function<Class<?>, List<Class<?>>> traverser = c -> asList(c.getInterfaces());
        TreeWalker<Class<?>> interfaceWalker = new TreeWalker<>(traverser, predicate);
        if (predicate.test(aClass)) {
            return aClass;
        } else {
            Class<?> result = interfaceWalker.depthFirst(traverser.apply(aClass));
            if (result != null) {
                return result;
            } else if (aClass.getSuperclass() != null) {
                return walkHierarchy(aClass.getSuperclass(), predicate);
            } else {
                return null;
            }
        }
    }
}
