package ru.vyarus.dropwizard.guice.order

import com.google.inject.Inject
import ru.vyarus.dropwizard.guice.AbstractTest
import ru.vyarus.dropwizard.guice.module.installer.feature.ManagedInstaller
import ru.vyarus.dropwizard.guice.module.installer.internal.FeaturesHolder
import ru.vyarus.dropwizard.guice.support.order.Ext1
import ru.vyarus.dropwizard.guice.support.order.Ext2
import ru.vyarus.dropwizard.guice.support.order.Ext3
import ru.vyarus.dropwizard.guice.support.order.OrderedApplication
import ru.vyarus.dropwizard.guice.test.spock.UseGuiceyApp

/**
 * @author Vyacheslav Rusakov 
 * @since 13.10.2014
 */
@UseGuiceyApp(OrderedApplication)
class OrderedTest extends AbstractTest {

    @Inject
    FeaturesHolder holder

    def "Check extensions ordering"() {

        when: "application started"

        then: "extensions ordered"
        holder.getFeatures(ManagedInstaller) == [Ext3, Ext1, Ext2]

    }
}