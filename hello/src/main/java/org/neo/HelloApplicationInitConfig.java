package org.neo;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class HelloApplicationInitConfig {
    /**
     * 可以在这里配置启动需要的bean
     */
}
