package com.kosmos.catalog_service.common.message;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class MessageConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localResolver=new SessionLocaleResolver();
        localResolver.setDefaultLocale(Locale.US);
        return localResolver;
    }

    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(getValidationMessageSource());
        return bean;
    }

    // 모든 클래스들의 검증 관련 스트링을 불러옵니다 (Spring Validator 플러그인의 ExceptionAdvisor 에 사용)
    @Bean
    public static MessageSource getValidationMessageSource() {
        ReloadableResourceBundleMessageSource msgSrc = new ReloadableResourceBundleMessageSource();
        msgSrc.setBasenames(
                "static/messages/product/validation"
        );
        return msgSrc;
    }

    // 파일시스템 IO 에 속하는 클래스들의 에러/반환값/검증 관련 스트링을 불러옵니다
    @Bean
    public static MessageSource getStorageMessageSource() {
        ReloadableResourceBundleMessageSource msgSrc = new ReloadableResourceBundleMessageSource();
        msgSrc.setBasenames(
                "static/messages/storage/error"
        );
        return msgSrc;
    }

    // 카탈로그 Aspect 에 속하는 클래스들의 에러/반환값/검증 관련 스트링을 불러옵니다
    @Bean
    public static MessageSource getProductMessageSource() {
        ReloadableResourceBundleMessageSource msgSrc = new ReloadableResourceBundleMessageSource();
        msgSrc.setBasenames(
                "static/messages/product/error",
                "static/messages/product/response",
                "static/messages/product/validation"
        );
        return msgSrc;
    }
}
