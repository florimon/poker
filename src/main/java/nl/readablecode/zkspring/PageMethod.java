package nl.readablecode.zkspring;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Method;

import org.springframework.web.bind.annotation.PathVariable;
import org.zkoss.zk.ui.Page;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 *
 */
@Getter
@RequiredArgsConstructor
public class PageMethod {
    private final Method method;
    private final List<String> pathElements;
    private final Map<Integer, String> pathVariablesByParameterIndex;

    public PageMethod(Method method, Optional<String> prefix, String path) {
        this(method, split(normalize(prefix.orElse("/")) + normalize(path)),
             getPathVariableNamesByParameterIndex(method.getParameterAnnotations()));
    }

    private static List<String> split(String path) {
        return asList(path.split("/"));
    }

    static String normalize(String s) {
        return s.replace("//", "/")             // collapse consecutive slashes to a single slash
                .replaceFirst("^/$", "")        // replace a String consisting of just a slash, with empty String
                .replaceFirst("^([^/])", "/$1") // prepend a slash if the first character is not a slash
                .replaceFirst("(.)/$", "$1");   // remove any trailing slash
    }

    private static Map<Integer, String> getPathVariableNamesByParameterIndex(Annotation[][] parameterAnnotations) {
        return IntStream.range(0, parameterAnnotations.length)
                .mapToObj(index -> Pair.of(index, findAnnotation(parameterAnnotations[index], PathVariable.class)))
                .filter(pair -> pair.getValue() != null)
                .collect(toMap(Pair::getKey, pair -> pair.getValue().value()));
    }

    private static <A extends Annotation> A findAnnotation(Annotation[] annotations, Class<A> annotationClass) {
        return Arrays.stream(annotations)
                .filter(a -> annotationClass.isAssignableFrom(a.getClass()))
                .findFirst()
                .map(annotationClass::cast)
                .orElse(null);
    }

    /**
     *
     * @return
     */
    public Class<?> getPageClass() {
        return method.getDeclaringClass();
    }

    /**
     *
     * @param requestPath
     * @return
     */
    public boolean matches(String requestPath) {
        return matches(split(normalize(requestPath)));
    }

    private boolean matches(List<String> requestPathElements) {
        if (pathElements.size() != requestPathElements.size()) {
            return false;
        } else {
            Iterator<String> iterator = requestPathElements.iterator();
            return pathElements.stream().allMatch(pathElement -> pathElement.equals(iterator.next()) || isPathVariable(pathElement));
        }
    }

    /**
     *
     * @param instance
     * @param page
     * @param requestPath
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void invoke(Object instance, Page page, String requestPath) throws InvocationTargetException, IllegalAccessException {
        method.invoke(instance, getArguments(page, getValuesByParameterIndex(split(normalize(requestPath)))));
    }

    private Object[] getArguments(Page page, Map<Integer, String> valuesByIndex) {
       Class<?>[] types = method.getParameterTypes();
       return IntStream.range(0, types.length)
                .mapToObj(index -> getPageOrTargetValue(types[index], page, valuesByIndex.get(index)))
                .collect(toList()).toArray(new Object[0]);
    }

    private Object getPageOrTargetValue(Class<?> targetClass, Page page, String stringValue) {
        return targetClass.equals(Page.class) ? page : convertValue(targetClass, stringValue);
    }

    private Object convertValue(Class<?> targetClass, String stringValue) {
        if (String.class.equals(targetClass)) {
            return stringValue;
        } else if (Integer.class.equals(targetClass)) {
            return Integer.parseInt(stringValue);
        } else if (Long.class.equals(targetClass)) {
            return Long.parseLong(stringValue);
        } else if (Double.class.equals(targetClass)) {
            return Double.parseDouble(stringValue);
        } else if (Float.class.equals(targetClass)) {
            return Float.parseFloat(stringValue);
        } else if (Boolean.class.equals(targetClass)) {
            return Boolean.parseBoolean(stringValue);
        } else if (UUID.class.equals(targetClass)) {
            return UUID.fromString(stringValue);
        } else { // could already check for this at class init time
            throw new IllegalArgumentException("Unsupported @PathVariable parameter type: " + targetClass.getName());
        }
    }

    private Map<Integer, String> getValuesByParameterIndex(List<String> pathElements) {
        return getValuesByParameterIndex(pathVariablesByParameterIndex, getValuesByPathVariableName(pathElements));
    }

    private Map<Integer, String> getValuesByParameterIndex(Map<Integer, String> names, Map<String, String> values) {
        return names.entrySet().stream().map(entry -> Pair.of(entry.getKey(), values.get(entry.getValue())))
                                        .filter(pair -> pair.getValue() != null)
                                        .collect(toMap(Pair::getKey, Pair::getValue));
    }

    public Map<String, String> getValuesByPathVariableName(List<String> requestPathElements) {
        Iterator<String> iterator = requestPathElements.iterator();
        return pathElements.stream().map(pathElement -> Pair.of(pathElement, iterator.next()))
                                    .filter(pair -> isPathVariable(pair.getKey()))
                                    .collect(toMap(pair -> getVariableName(pair.getKey()), Pair::getValue));
    }

    private boolean isPathVariable(String pathElement) {
        return pathElement.matches("\\{.*\\}");
    }

    private String getVariableName(String pathVariable) {
        return pathVariable.replaceFirst("\\{(.*)\\}", "$1");
    }
}
