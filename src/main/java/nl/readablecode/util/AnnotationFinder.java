package nl.readablecode.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

public class AnnotationFinder {

    public <A extends Annotation> Optional<A> getAnnotation(Class<?> aClass, Class<A> annotation) {
        return ofNullable(walkHierarchy(aClass, c -> c.isAnnotationPresent(annotation)))
                .map(c -> c.getAnnotation(annotation));
    }

    public <A extends Annotation> Optional<Method> getAnnotatedMethod(Class<?> aClass, Class<A> annotation, Method method) {
        return getAnnotatedMethod(aClass, annotation, method.getName(), method.getParameterTypes());
    }

    public <A extends Annotation> Optional<Method> getAnnotatedMethod(Class<?> aClass, Class<A> annotation,
                                                                        String methodName, Class<?>... args) {
        Function<Class<?>, Optional<Method>> getMethod = c -> getMethod(c, methodName, args);
        Function<Method, Boolean> methodPredicate = m -> m.isAnnotationPresent(annotation);
        return ofNullable(walkHierarchy(aClass, c -> getMethod.apply(c).map(methodPredicate).orElse(false)))
                .flatMap(getMethod);
    }

    private Optional<Method> getMethod(Class<?> aClass, String name, Class<?>... args) {
        try {
            return ofNullable(aClass.getMethod(name, args));
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
