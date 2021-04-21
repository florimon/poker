package nl.readablecode.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public boolean matches(String candidatePath) {
        return matches(split(normalize(candidatePath)));
    }

    public boolean matches(List<String> candidateElements) {
        if (pathElements.size() != candidateElements.size()) {
            return false;
        } else {
            Iterator<String> iterator = candidateElements.iterator();
            return pathElements.stream().allMatch(e -> e.equals(iterator.next()) || isPlaceholder(e));
        }
    }

    public Map<String, String> getValues(List<String> candidateElements) {
        Iterator<String> iterator = candidateElements.iterator();
        return pathElements.stream().map(e -> Pair.of(e, iterator.next()))
                                    .filter(p -> isPlaceholder(p.getKey()))
                                    .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    private boolean isPlaceholder(String pathElement) {
        return pathElement.matches("\\{.*\\}");
    }
}
