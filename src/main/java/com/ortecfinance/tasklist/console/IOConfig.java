package com.ortecfinance.tasklist.console;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

@Configuration
public class IOConfig {

    @Bean
    @ConditionalOnMissingBean
    public BufferedReader consoleReader() {
        return new BufferedReader(new InputStreamReader(System.in));
    }

    @Bean
    @ConditionalOnMissingBean
    public PrintWriter consoleWriter() {
        return new PrintWriter(System.out, true);
    }
}

