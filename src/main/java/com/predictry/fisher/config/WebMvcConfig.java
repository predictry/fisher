package com.predictry.fisher.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@ComponentScan("com.predictry.fisher")
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

}
