package nl.readablecode.zk;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.readablecode.util.AnnotationFinder;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author florimon
 */
@Slf4j
public class ZkSpringRichlet extends GenericRichlet {
    private final AnnotationFinder annotationFinder = new AnnotationFinder();
    private final List<PageMethod> pageMethods = scanPageMethods();
    private final SpringBeanLocator springBeanLocator = SpringBeanLocator.getInstance();
    private final String richletPrefix = springBeanLocator.getRichletMapping().replace("/*", "");

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
        return page.getRequestPath().replaceFirst(richletPrefix, "/");
    }

    private Optional<Boolean> invoke(PageMethod pageMethod, Page page, String pathWithoutPrefix) {
        try {
            pageMethod.invoke(springBeanLocator.getBean(pageMethod.getPageClass()), page, pathWithoutPrefix);
            return Optional.of(true);
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.error("Couldn't invoke bean {} for request path {}", pageMethod.getPageClass(), page.getRequestPath());
            return Optional.empty();
        }
    }

    private List<PageMethod> scanPageMethods() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(PageController.class, true, true));
        return scanner.findCandidateComponents("nl.readablecode").stream()
                .map(this::getBeanClass)
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
    private Class<?> getBeanClass(BeanDefinition beanDefinition) {
        return Class.forName(beanDefinition.getBeanClassName());
    }
}
