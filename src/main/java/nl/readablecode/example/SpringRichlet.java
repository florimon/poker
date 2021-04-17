package nl.readablecode.example;

import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;

public class SpringRichlet extends GenericRichlet {


    @Override
    public void service(Page page) throws Exception {
        SpringUtil.getBean(MainController.class).service(page);
        // create new instance of class that receives the Page reference,
        // and may contain instance fields that are references to Spring beans,
        // which then need to be injected into it

        // OR

        // retrieve from Spring applicationContext a bean whose custom path
        // annotation corresponds to the request path, and inject into its
        // 'service' or whatever method, the Page reference and a map of the
        // request parameters
    }
}
