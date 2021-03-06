package ru.vyarus.dropwizard.guice.module.jersey.hk2;

import com.google.inject.Injector;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import ru.vyarus.dropwizard.guice.module.installer.FeatureInstaller;
import ru.vyarus.dropwizard.guice.module.installer.install.JerseyInstaller;
import ru.vyarus.dropwizard.guice.module.installer.internal.FeaturesHolder;
import ru.vyarus.dropwizard.guice.module.installer.util.FeatureUtils;

import java.util.List;

/**
 * Hk2 module, which must be registered before hk context start (to properly bind resources).
 * Registers all juice extensions into hk2 context as factory, which delegates to guice provider.
 * Such bridging is required to delegate objects creation to guice and properly use guice scopes.
 * <p>If jersey extension is already a factory (like
 * {@link org.glassfish.jersey.server.internal.inject.AbstractContainerRequestValueFactory}), then factory will
 * be registered directly.</p>
 *
 * @author Vyacheslav Rusakov
 * @see ru.vyarus.dropwizard.guice.module.installer.install.JerseyInstaller
 * @since 16.11.2014
 */
public class InstallerBinder extends AbstractBinder {

    @Override
    @SuppressWarnings("unchecked")
    protected void configure() {
        final Injector injector = GuiceBundle.getInjector();
        final FeaturesHolder holder = injector.getInstance(FeaturesHolder.class);
        for (FeatureInstaller installer : holder.getInstallers()) {
            if (installer instanceof JerseyInstaller) {
                final List<Class<?>> features = holder.getFeatures(FeatureUtils.getInstanceClass(installer));
                if (features != null) {
                    for (Class<?> type : features) {
                        ((JerseyInstaller) installer).install(this, type);
                    }
                }
                installer.report();
            }
        }
    }
}
