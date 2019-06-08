package pl.wojciechkabat.hotchilli.security.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.CollectionUtils;
import pl.wojciechkabat.hotchilli.entities.Role;
import pl.wojciechkabat.hotchilli.entities.User;
import pl.wojciechkabat.hotchilli.repositories.UserRepository;
import pl.wojciechkabat.hotchilli.security.token.JwtSettings;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final UserRepository userRepository;
    private final JwtSettings jwtSettings;

    private static final String HEADER_STRING = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    public JWTAuthorizationFilter(AuthenticationManager authManager,
                                  UserRepository userRepository,
                                  JwtSettings jwtSettings) {
        super(authManager);
        this.userRepository = userRepository;
        this.jwtSettings = jwtSettings;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        final String token = req.getHeader(HEADER_STRING);

        if (token == null || !token.startsWith(TOKEN_PREFIX) || isRefreshTokenURL(req.getRequestURI())) {
            chain.doFilter(req, res);
            return;
        }
        try {
            UsernamePasswordAuthenticationToken authentication = getAuthentication(token);
            if (checkTokenWithUsersIdentity(token)) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            chain.doFilter(req, res);
        } catch (JWTVerificationException e) {
            chain.doFilter(req, res);
        }

    }

    private boolean checkTokenWithUsersIdentity(String token) {
        final String subject = parseToken(token);
        return userRepository.findByEmail(subject).isPresent();
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        if (token != null) {
            final String subject = parseToken(token);
            final Optional<User> applicationUser = userRepository.findByEmail(subject);

            if (applicationUser.isPresent() && !CollectionUtils.isEmpty(applicationUser.get().getRoles())) {
                final User user = applicationUser.get();
                final List<GrantedAuthority> grantedAuthorities = getGrantedAuthorities(user.getRoles());
                if (subject != null) {
                    return new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), grantedAuthorities);
                }
            }
            return null;
        }
        return null;
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<Role> roles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        roles.forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getValue())));
        return grantedAuthorities;
    }

    private String parseToken(String token) {
        return JWT.require(Algorithm.HMAC512(jwtSettings.getTokenSigningKey()))
                .build()
                .verify(token.replace(TOKEN_PREFIX, ""))
                .getSubject();
    }

    private boolean isRefreshTokenURL(String url) {
        return url.contains("/api/auth/token/");
    }
}
