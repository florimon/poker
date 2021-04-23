package nl.readablecode.zk;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
public class ZkSpringUtil {

    @Setter
    private static ZkSpringUtil zkSpringUtil;

    private final ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        setZkSpringUtil(this);
    }

    public static <T> T getBean(String name) {
        return (T) zkSpringUtil.applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> aClass) {
        return zkSpringUtil.applicationContext.getBean(aClass);
    }
}
