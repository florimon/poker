package nl.readablecode.example;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class SpringUtil {

    @Setter
    private static SpringUtil springUtil;

    private final ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        setSpringUtil(this);
    }

    public static <T> T getBean(String name) {
        return (T) springUtil.applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> aClass) {
        return springUtil.applicationContext.getBean(aClass);
    }
}
