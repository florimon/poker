package nl.readablecode.example;

import lombok.extern.slf4j.Slf4j;
import nl.readablecode.util.AnnotationFinder;
import nl.readablecode.zk.PageMapping;
import nl.readablecode.zk.PageMethod;
import nl.readablecode.zk.ZkSpringUtil;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;

import java.lang.reflect.Method;
import java.util.*;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Slf4j
public class SpringRichlet extends GenericRichlet {

    private AnnotationFinder annotationFinder = new AnnotationFinder();

    @Override
    public void service(Page page) throws Exception {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(PageMapping.class, true, true));
        Set<BeanDefinition> definitions = scanner.findCandidateComponents("nl.readablecode");
        definitions.forEach(bd -> {
            Class<?> beanClass = getClass(bd.getBeanClassName());
            Method serviceMethod = getServiceMethod(beanClass, "service", Page.class);
            Optional<Method> annotatedMethod = annotationFinder.findAnnotatedMethod(beanClass, PageMapping.class, serviceMethod);
            log.info("{}", annotatedMethod.get().getAnnotation(PageMapping.class));
        });

        ZkSpringUtil.getBean(MainController.class).service(page);

        // get all beandefinitions of beans with PageMapping annotation
        //  for each beandefinition, get all of the class's methods with PageMapping annotation AND a single 'Page' method argument
        //  for each valid combination of class PageMapping and method PageMapping, combine into single regexp,
        //  and store along with method reference

        // /poker/{team}/{player}
    }

    private Method getServiceMethod(Class<?> aClass, String methodName, Class<?>... arguments)  {
        try {
            return aClass.getMethod(methodName, arguments);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private void bla() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(PageMapping.class));
        Set<BeanDefinition> definitions = scanner.findCandidateComponents("nl.readablecode");

    }


    private Object processZkController(Class<?> aClass) {
        AnnotationFinder annotationFinder = new AnnotationFinder();
        Optional<PageMapping> classAnnotation = annotationFinder.findAnnotation(aClass, PageMapping.class);
        List<Method> methods = getMethodsWithSinglePageParameter(aClass);
        if (!methods.isEmpty()) {
            for (Method method : methods) {
                Optional<Method> annotatedMethod = annotationFinder.findAnnotatedMethod(aClass, PageMapping.class, method);
                if (annotatedMethod.isPresent()) {
                    return new PageMethod(annotatedMethod.get(), classAnnotation.map(PageMapping::value),
                                        annotatedMethod.get().getAnnotation(PageMapping.class).value());
                }
            }
        }
        // find optional class level PageMapping annotation
        // find method(s) with Page parameter, for each method (exception if no method found)
        //      find method level PageMapping annotation (exception if not found)
        //      concat class and method level PageMapping path values
        //      store tuple of concatenated path and Method reference


        return null;
    }



    private List<Method> getMethodsWithSinglePageParameter(Class<?> aClass) {
        return stream(aClass.getMethods()).filter(this::hasSinglePageParameter).collect(toList());
    }

    private boolean hasSinglePageParameter(Method method) {
        return stream(method.getParameterTypes()).filter(type -> Page.class.equals(type)).count() == 1;
    }
}
