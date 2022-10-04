package indocyber.exam.configuration;

import indocyber.exam.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Order(1)
public class RestSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Override
    @Bean
    @Primary
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/api/**").csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/authenticate","/api/register").permitAll()
                .antMatchers("/api/admins/index","/api/admins/add","/api/admins/delete").hasAuthority("SuperAdmin")
                .antMatchers("/api/admins/update").hasAuthority("Admin")
                .antMatchers("/api/admins/get","/api/customer/delete","/api/customers/index","/api/rooms/index","/api/rooms/add","/api/rooms/get","/api/rooms/update","/api/customers/get","/api/transactions/index","/api/rooms/detail","/api/rooms/delete","/api/customer/history").hasAnyAuthority("SuperAdmin","Admin")
                .antMatchers("/api/rooms/vacantRoom","/api/customers/update","/api/customer/myHistory","/api/rooms/get/vacant","/api/transactions/reserve","/api/transactions/activeReservation","/api/transactions/cancelReservation","/api/transactions/confirmReservation").hasAuthority("Customer")
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
