package nl.readablecode.zk;

import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

/**
 *
 */
@RequiredArgsConstructor
public class SpringBeanLocator {

    @Setter
    private static SpringBeanLocator instance;

    private final ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        setInstance(this);
    }

    public static <T> T getBean(Class<T> aClass) {
        return instance.applicationContext.getBean(aClass);
    }
}
