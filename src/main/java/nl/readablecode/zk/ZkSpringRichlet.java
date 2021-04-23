package nl.readablecode.zk;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.readablecode.util.AnnotationFinder;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;

/**
 * @author florimon
 */
@Slf4j
public class ZkSpringRichlet extends GenericRichlet {
    private AnnotationFinder annotationFinder = new AnnotationFinder();
    private List<PageMethod> pageMethods = scanPageMethods();

    @Override
    public void service(Page page) {
        service(page, getRequestPathWithoutRichletPrefix(page));
    }

    private boolean service(Page page, String pathWithoutPrefix) {
        return getFirstMatchingPageMethod(pathWithoutPrefix)
                .flatMap(pageMethod -> invoke(pageMethod, page, pathWithoutPrefix))
                .orElseThrow(() -> new UnsupportedOperationException("Can't service " + page.getRequestPath()));
    }

    private Optional<PageMethod> getFirstMatchingPageMethod(String requestPath) {
        return pageMethods.stream().filter(pageMethod -> pageMethod.matches(requestPath)).findFirst();
    }

    private String getRequestPathWithoutRichletPrefix(Page page) {
        return page.getRequestPath().replaceFirst(page.getDesktop().getCurrentDirectory(), "/");
    }

    private Optional<Boolean> invoke(PageMethod pageMethod, Page page, String pathWithoutPrefix) {
        try {
            pageMethod.invoke(SpringBeanLocator.getBean(pageMethod.getPageClass()), page, pathWithoutPrefix);
            return Optional.of(true);
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.error("Couldn't invoke bean {} for request path {}", pageMethod.getPageClass(), page.getRequestPath());
            return Optional.empty();
        }
    }

    private List<PageMethod> scanPageMethods() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(PageMapping.class, true, true));
        return scanner.findCandidateComponents("nl.readablecode").stream()
                .map(BeanDefinition::getBeanClassName)
                .map(this::getClass)
                .flatMap(this::createPageMethods)
                .collect(toList());
    }

    private Stream<PageMethod> createPageMethods(Class<?> aClass) {
        Optional<String> pathPrefix = annotationFinder.findAnnotation(aClass, PageMapping.class).map(PageMapping::value);
        return getMethodsWithSinglePageParameter(aClass)
                .map(method -> annotationFinder.findAnnotatedMethod(aClass, PageMapping.class, method))
                .filter(Optional::isPresent).map(Optional::get)
                .map(method -> createPageMethod(pathPrefix, method));
    }

    private PageMethod createPageMethod(Optional<String> pathPrefix, Method annotatedMethod) {
        return new PageMethod(annotatedMethod, pathPrefix, annotatedMethod.getAnnotation(PageMapping.class).value());
    }

    private Stream<Method> getMethodsWithSinglePageParameter(Class<?> aClass) {
        return stream(aClass.getMethods()).filter(this::hasSinglePageParameter);
    }

    private boolean hasSinglePageParameter(Method method) {
        return stream(method.getParameterTypes()).filter(Page.class::equals).count() == 1;
    }

    @SneakyThrows
    private Class<?> getClass(String className) {
        return Class.forName(className);
    }
}
