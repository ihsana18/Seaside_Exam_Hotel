package indocyber.exam.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@Order(2)
public class MvcSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder(){ // To encode password
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/resources/**","/account/**").permitAll()
                .antMatchers("/admin/index","/admin/insertAdmin","/admin/insert","admin/delete").hasAuthority("SuperAdmin")
                .antMatchers("/admin/updateAdmin","/admin/update").hasAuthority("Admin")
                .antMatchers("/customer/index","/room/index","/customer/delete","/room/upsertForm","/room/insert","/room/delete","/room/update","/transaction/delete","/transaction/detail","/transaction/index","/transaction/history").hasAnyAuthority("SuperAdmin","Admin")
                .antMatchers("/customer/updateForm","/customer/update","/room/vacantRoom","/room/reserveForm","/transaction/deleteReservation","/transaction/reserve","/transaction/confirm","/transaction/reservation","/transaction/myHistory").hasAuthority("Customer")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/account/loginForm")
                .loginProcessingUrl("/authenticateUser")
                .permitAll()
                .and()
                .logout().permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/home/access-denied");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }
}
