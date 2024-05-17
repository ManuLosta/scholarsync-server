package com.scholarsync.server.filters;

import com.scholarsync.server.repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.scholarsync.server")
public class FilterConfig {
  @Autowired SessionRepository sessionRepository;

  @Bean
  public FilterRegistrationBean<TokenFilter> filterRegistrationBean() {
    FilterRegistrationBean<TokenFilter> registrationBean = new FilterRegistrationBean<>();
    TokenFilter tokenFilter = new TokenFilter(sessionRepository);

    registrationBean.setFilter(tokenFilter);
    registrationBean.setOrder(-1);

    return registrationBean;
  }
}
