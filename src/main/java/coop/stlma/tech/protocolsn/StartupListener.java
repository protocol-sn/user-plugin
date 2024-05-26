package coop.stlma.tech.protocolsn;

import coop.stlma.tech.protocolsn.pluginlib.demo.DemoBean;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.event.ApplicationStartupEvent;
import jakarta.inject.Singleton;

@Singleton
public class StartupListener implements ApplicationEventListener<ApplicationStartupEvent> {

    private final DemoBean demoBean;

    public StartupListener(DemoBean demoBean) {
        this.demoBean = demoBean;
    }

    @Override
    public void onApplicationEvent(ApplicationStartupEvent event) {
        System.out.println(demoBean.hello());
    }
}
