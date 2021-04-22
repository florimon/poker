package nl.readablecode.util;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.PathVariable;
import org.zkoss.zk.ui.Page;

import static java.util.Arrays.asList;

@Getter
@RequiredArgsConstructor
public class PageMethod {
    private final Method method;
    private final List<String> pathElements;

    public PageMethod(Method method, Optional<String> prefix, String path) {
        this(method, split(normalize(prefix.orElse("/")) + normalize(path)));
    }

    private static List<String> split(String path) {
        return asList(path.split( "/"));
    }

    private static String normalize(String string) {
        return string.replaceAll("//", "/").replaceAll("^([^/]?)", "/$1").replaceFirst("(.)/$", "$1");
    }

    public boolean matches(String requestPath) {
        return matches(split(normalize(requestPath)));
    }

    public void invoke(Object instance, String requestPath, Page page) {
        Map<String, String> values = getValuesByPathVariableName(split(normalize(requestPath)));
        Map<Integer, String> names = getPathVariableNamesByParameterIndex();
        Map<Integer, String> valuesByIndex = names.entrySet().stream().map(e -> Pair.of(e.getKey(), values.get(e.getValue())))
            .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    private boolean matches(List<String> requestPathElements) {
        if (pathElements.size() != requestPathElements.size()) {
            return false;
        } else {
            Iterator<String> iterator = requestPathElements.iterator();
            return pathElements.stream().allMatch(e -> e.equals(iterator.next()) || isPathVariable(e));
        }
    }

    public Map<String, String> getValuesByPathVariableName(List<String> requestPathElements) {
        Iterator<String> iterator = requestPathElements.iterator();
        return pathElements.stream().map(e -> Pair.of(e, iterator.next()))
                                    .filter(pair -> isPathVariable(pair.getKey()))
                                    .collect(Collectors.toMap(pair -> getVariableName(pair.getKey()), Pair::getValue));
    }

    public Map<Integer, String> getPathVariableNamesByParameterIndex() {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        return IntStream.range(0, parameterAnnotations.length)
                    .mapToObj(index -> Pair.of(index, findAnnotation(parameterAnnotations[index], PathVariable.class)))
                    .filter(pair -> pair.getValue() != null)
                    .collect(Collectors.toMap(Pair::getKey, pair -> pair.getValue().value()));
    }

    private <A extends Annotation> A findAnnotation(Annotation[] annotations, Class<A> annotationClass) {
        return Arrays.stream(annotations).filter(a -> annotationClass.isAssignableFrom(a.getClass()))
                                         .findFirst().map(a -> annotationClass.cast(a)).orElse(null);
    }

    private boolean isPathVariable(String pathElement) {
        return pathElement.matches("\\{.*\\}");
    }

    private String getVariableName(String pathVariable) {
        return pathVariable.replaceFirst("\\{(.*)\\}", "$1");
    }
}
