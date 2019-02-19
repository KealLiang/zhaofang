package com.kealliang.security;

import com.kealliang.entity.User;
import com.kealliang.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;

/**
 * @author lsr
 * @ClassName AuthProvider
 * @Date 2019-01-31
 * @Desc
 * @Vertion 1.0
 */
public class AuthProvider implements AuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(AuthProvider.class);

    @Autowired
    private UserService userService;

    // spring5(springBoot2.0)之后，
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String inputPassword = (String) authentication.getCredentials();
        User user = userService.findUserByUsername(username);
        if (user == null) {
            throw new AuthenticationCredentialsNotFoundException("authError");
        }

        // 使用BCryptPasswordEncoder匹配
        if (passwordEncoder.matches(inputPassword, user.getPassword())) {
            LOG.info("用户[{}]成功登录了", user.getName());
            AuthUser authUser = new AuthUser();
            BeanUtils.copyProperties(user, authUser);
            return new UsernamePasswordAuthenticationToken(authUser, null, user.getAuthorityList());
        } else {
            LOG.info("用户[{}]登录失败了", user.getName());
        }

        throw new BadCredentialsException("authError");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

    /** 
     * 用户授权主体
     * @author lsr
     * @description 
     * @Date 23:28 2019/1/31
     * @Param 
     * @return 
     */
    public class AuthUser extends User implements UserDetails {

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return null;
        }

        @Override
        public String getUsername() {
            return null;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
