package com.hymes.demo1.config;//package com.hymes.demo1.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.w3c.dom.DocumentType;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.Contact;
//import springfox.documentation.service.VendorExtension;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Configuration  //配置信息注解
//@EnableSwagger2 //开启swagger2
//public class SwaggerConfig {
//    /**
//     * 配置摘要(docket)为文档类型是Swagger2的bean实例。
//     * @return
//     */
//    @Bean   //配置了Swagger的Docket的bean实例
//    public Docket docket() {
//        Docket docket = new Docket(DocumentationType.SWAGGER_2);
//        return docket
//               // .groupName("赵浩")//组名
//              //  .apiInfo(customApiInfo())//接口信息
//                .enable(true)//是否开启swagger，默认为true。
//                .select()// 扫描接口的方式为：基础包com.hymes.rbacpractice.controller所有类
//                .apis(RequestHandlerSelectors.basePackage("com.hymes.demo1.controller"))
//                .build();
//    }
//
//    /**
//     * 自定义接口信息
//     * @return
//     */
////    public ApiInfo customApiInfo() {
////        Contact customContact = new Contact("赵浩", "www.baidu.com", "710530054@qq.com");//自定义的联系方式。
////        ApiInfo apiInfo = new ApiInfo("赵浩的的接口文档", "Api Documentation", "1.0", "urn:tos",
////                customContact, "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0", new ArrayList<VendorExtension>());
////        return apiInfo;
////    }
//}

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors
                        .basePackage("com.hymes.demo1.controller"))
                .paths(PathSelectors.any()).build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("spring boot示例接口API")
                .description("spring boot示例接口API")
                .version("1.0").build();
    }
}
