package cn.com.mfish.common.code.config.properties;

import freemarker.template.Version;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: mfish
 * @description: FreemarkerProperties 类用于配置和存储与 Freemarker 模板引擎相关的属性。
 * @date: 2022/8/30 16:15
 */
@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "code.template")
public class FreemarkerProperties {
    /**
     * keys 列表用于存储模板的键值，这些键值可能用于模板的加载或配置。
     */
    private final List<String> keys = new ArrayList<>();

    /**
     * path 字段定义了模板文件存储的路径。
     */
    private String path;

    /**
     * version 字段指定了 Freemarker 模板引擎的版本。
     * 这里使用了 Freemarker 的静态字段 VERSION_2_3_31，表示使用的是 2.3.31 版本。
     */
    private Version version = freemarker.template.Configuration.VERSION_2_3_31;
}
