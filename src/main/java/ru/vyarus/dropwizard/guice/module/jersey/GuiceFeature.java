package ru.vyarus.dropwizard.guice.module.jersey;

import com.google.common.base.Preconditions;
import com.google.inject.Injector;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.ServiceLocatorProvider;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import ru.vyarus.dropwizard.guice.module.jersey.hk2.InstallerBinder;

import javax.inject.Provider;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

/**
 * Feature activates guice integration.
 * <p>Guice context is created first and it doesn't depend on jersey start. First of all this allow using guice
 * in commands and second, guice is ready in time of jersey initialization and so can provide it's own instances
 * into jersey config.</p>
 * <p>Feature must be registered in jersey before it's start:
 * {@code environment.jersey().register(new GuiceFeature())}</p>
 * <p>During juice context start special jersey bindings module registered
 * {@link ru.vyarus.dropwizard.guice.module.jersey.hk2.GuiceBindingsModule}, which lazily binds jersey specifc
 * types to guice context. This types could be used in guice only after actual integration
 * (this feature activation)</p>
 * <p>HK-guice bridge is activated (not bi-directional), but it's not actually required in most cases: it was
 * developed for cases when bean is created by HK and only need some injections from guice, but here guice
 * controls almost everything and prepared instance is passed to guice. But bridge may be useful together with
 * {@link ru.vyarus.dropwizard.guice.module.installer.feature.jersey.HK2Managed} instances.</p>
 * <p>Feature installs {@code ru.vyarus.dropwizard.guice.module.jersey.hk2.InstallerBinder}, which is HK module.
 * Just like with guice ({@code BindingInstaller)}, it asks all {@code JerseyInstaller} to bind extensions into
 * HK context.</p>
 *
 * @author Vyacheslav Rusakov
 * @see ru.vyarus.dropwizard.guice.module.jersey.support.JerseyComponentProvider
 * @see ru.vyarus.dropwizard.guice.module.jersey.support.GuiceComponentFactory
 * @see ru.vyarus.dropwizard.guice.module.jersey.support.LazyGuiceFactory
 * @since 21.11.2014
 */
public class GuiceFeature implements Feature, Provider<ServiceLocator> {

    private ServiceLocator locator;

    @Override
    public boolean configure(final FeatureContext context) {
        locator = ServiceLocatorProvider.getServiceLocator(context);
        final Injector injector = GuiceBundle.getInjector();

        GuiceBridge.getGuiceBridge().initializeGuiceBridge(locator);
        final GuiceIntoHK2Bridge guiceBridge = locator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(injector);

        context.register(new InstallerBinder());
        return true;
    }

    @Override
    public ServiceLocator get() {
        return Preconditions.checkNotNull(locator, "Service locator is not yet available");
    }
}
