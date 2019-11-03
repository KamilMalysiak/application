package pl.sdacademy.todolist.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Slf4j
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/register", "/", "/index", "/add", "/edit", "/menu").permitAll()
                //.antMatchers("/index","/**/tasks").hasAnyRole("USER", "ADMIN")
                .antMatchers("/**/*.js", "/**/*.css").permitAll()
                .antMatchers("/admin").hasRole("ADMIN")
                .antMatchers("/resources/**").permitAll().anyRequest().permitAll()
                .and()
                .formLogin()
                .and()
                .logout().logoutSuccessUrl("/login")
                .and()
                .httpBasic()
                .and()
                .csrf().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}