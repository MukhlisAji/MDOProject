package com.bmc.mii.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan(basePackages = {"com.bmc.mii.*"})
@PropertySource("classpath:Config.properties")
public class ConfigFile {

    @Autowired
    private Environment env;

    @Bean
    public ConfigurationValue getConfigurationValue() {
        return new ConfigurationValue(
                env.getProperty("remedy.server"),
                env.getProperty("remedy.username"),
                env.getProperty("remedy.password"),
                env.getProperty("remedy.port"),
                env.getProperty("remedy.middleform.MDO"),
                env.getProperty("remedy.middleform.SRM"),
                env.getProperty("remedy.middleform.OLA"));
    }
}
