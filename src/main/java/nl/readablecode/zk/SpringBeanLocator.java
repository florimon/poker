package nl.readablecode.zk;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;

/**
 *
 */
@RequiredArgsConstructor
public class SpringBeanLocator {

    @Getter
    @Setter
    private static SpringBeanLocator instance;

    private final ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        setInstance(this);
    }

    public static <T> T getBean(String name) {
        return (T) instance.applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> aClass) {
        return instance.applicationContext.getBean(aClass);
    }
}
