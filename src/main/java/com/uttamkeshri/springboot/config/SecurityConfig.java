package com.uttamkeshri.springboot.config;

import static com.uttamkeshri.springboot.model.RoleName.ROLE_ADMIN;
import static com.uttamkeshri.springboot.model.RoleName.ROLE_USER_MANAGE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.AccessDeniedHandler;
import static org.springframework.security.config.Customizer.withDefaults;

import com.uttamkeshri.springboot.config.CustomAccessDeniedHandler;
import com.uttamkeshri.springboot.security.CustomUserDetailsService;
import com.uttamkeshri.springboot.security.JwtAuthenticationEntryPoint;
import com.uttamkeshri.springboot.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private AccessDeniedHandler accessDeniedHandler;
	
    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService);
//                .passwordEncoder(passwordEncoder());
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); 
		auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
	}

	@Bean public PasswordEncoder passwordEncoder() { 
		return new BCryptPasswordEncoder(); 
	}
	
	@Bean
	public AccessDeniedHandler accessDeniedHandler(){
		return new CustomAccessDeniedHandler();
	}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
				.antMatchers("/","/aboutus").permitAll()  //dashboard , Aboutus page will be permit to all user 
				.antMatchers("/admin/**").hasAnyRole("ADMIN") //Only admin user can login 
				.antMatchers("/user/**").hasAnyRole("USER") //Only normal user can login 
                .antMatchers("/",
                        "/favicon.ico",
                        "/**/*.png",
                        "/**/*.gif",
                        "/**/*.svg",
                        "/**/*.jpg",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js")
                .permitAll()
                .antMatchers("/api/auth/**")
                .permitAll()
                .antMatchers("/api/user/get-all-user")
                .permitAll()
                .antMatchers("/api/user/checkUsernameAvailability", "/api/user/checkEmailAvailability")
                .permitAll()
                .antMatchers("/api/user/get-all-locked-account","/api/user/get-all-pending-account","/api/user/unlock_account_by_id","/api/user/activate_pending_account_by_id")
                .hasAuthority(String.valueOf(ROLE_ADMIN))
                .antMatchers("/api/user/edit","/api/user/delete")
                .hasAnyAuthority(String.valueOf(ROLE_ADMIN),String.valueOf(ROLE_USER_MANAGE))
                .anyRequest()
                .authenticated() //Rest of all request need authentication 	
				.and()
				.formLogin()
				.loginPage("/login")  //Loginform all can access .. 
				.defaultSuccessUrl("/dashboard")
				.failureUrl("/login?error")
				.permitAll()
				.and()
				.logout()
				.permitAll();

        // Add our custom JWT security filter
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
