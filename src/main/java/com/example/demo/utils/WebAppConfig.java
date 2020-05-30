package com.example.demo.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import javax.servlet.MultipartConfigElement;

//上传配置类
@Configuration
public class WebAppConfig extends WebMvcConfigurerAdapter {
//public class WebAppConfig extends WebMvcConfigurationSupport {
    /**
     * 在配置文件中配置的文件保存路径
     */
    @Value("${cbs.imagesPath}")
    private String filePath;

    @Bean
    public MultipartConfigElement multipartConfigElement(){
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //文件最大KB,MB
        factory.setMaxFileSize("1024MB");
        //设置总上传数据总大小
        factory.setMaxRequestSize("1024MB");
        return factory.createMultipartConfig();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if(filePath.equals("") || filePath.equals("${cbs.imagesPath}")){
            String fPath = WebAppConfig.class.getClassLoader().getResource("").getPath();
            if(fPath.indexOf(".jar")>0){
                fPath = fPath.substring(0, fPath.indexOf(".jar"));
            }else if(fPath.indexOf("classes")>0){
                fPath = "file:"+fPath.substring(0, fPath.indexOf("classes"));
            }
            fPath = fPath.substring(0, fPath.lastIndexOf("/"))+"/images/";
            filePath = fPath;
        }
        registry.addResourceHandler("/files/**").addResourceLocations(filePath);
        // TODO Auto-generated method stub
        super.addResourceHandlers(registry);
    }

}
