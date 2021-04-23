package nl.readablecode.zk;

import javax.annotation.PostConstruct;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author florimon
 */
@Getter
@RequiredArgsConstructor
public class SpringBeanLocator {
    private final ApplicationContext applicationContext;

    @Value("${zk.richlet-filter-mapping}")
    private String richletMapping;

    @Getter @Setter(AccessLevel.PRIVATE)
    private static SpringBeanLocator instance;

    @PostConstruct
    public void init() {
        setInstance(this);
    }

    public <T> T getBean(Class<T> aClass) {
        return applicationContext.getBean(aClass);
    }
}
