package com.github.iyboklee.config;

import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.eviction.lru.LruEvictionPolicyFactory;
import org.apache.ignite.cache.spring.SpringCacheManager;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class IgniteConfig {

    @Value("#{'${ignite.cluster.nodes}'.split(',')}") private List<String> nodes;

    @Bean
    IgniteConfiguration igniteConfiguration() {
        IgniteConfiguration igniteCfg = new IgniteConfiguration();
        igniteCfg.setClientMode(false);
        igniteCfg.setPeerClassLoadingEnabled(true);

        // Logger
        igniteCfg.setGridLogger(new Slf4jLogger());
        igniteCfg.setMetricsLogFrequency(1000 * 10);

        // Cluster Discovery
        TcpDiscoverySpi spi  = new TcpDiscoverySpi();
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(nodes);
        spi.setIpFinder(ipFinder);
        spi.setSocketTimeout(100);
        igniteCfg.setDiscoverySpi(spi);

        // Cluster Communication
        TcpCommunicationSpi commSpi = new TcpCommunicationSpi();
        commSpi.setConnectTimeout(3000);
        commSpi.setTcpNoDelay(true);
        commSpi.setMessageQueueLimit(10000);
        igniteCfg.setCommunicationSpi(commSpi);

        // Memory Configuration
        DataStorageConfiguration storageCfg = new DataStorageConfiguration();
        DataRegionConfiguration defaultDataRegionCfg = storageCfg.getDefaultDataRegionConfiguration();
        defaultDataRegionCfg.setPersistenceEnabled(false);   // Only Memory
        defaultDataRegionCfg.setMaxSize(1024 * 1024 * 256);  // 256MB
        defaultDataRegionCfg.setMetricsEnabled(true);
        igniteCfg.setDataStorageConfiguration(storageCfg);

        return igniteCfg;
    }

    @Bean(destroyMethod = "close")
    public Ignite igniteInstance(IgniteConfiguration configuration) throws IgniteException {
        Ignite ignite = Ignition.start(configuration);
        ignite.cluster().active(true);
        return ignite;
    }

    @Bean
    public CacheManager cacheManager() {
        SpringCacheManager cacheManager = new SpringCacheManager();
        CacheConfiguration<Object, Object> dynamicCacheCfg = new CacheConfiguration<>();
        dynamicCacheCfg.setCacheMode(CacheMode.REPLICATED);
        dynamicCacheCfg.setOnheapCacheEnabled(true);
        dynamicCacheCfg.setEvictionPolicyFactory(new LruEvictionPolicyFactory<>(10_000));
        cacheManager.setDynamicCacheConfiguration(dynamicCacheCfg);
        return cacheManager;
    }

}
