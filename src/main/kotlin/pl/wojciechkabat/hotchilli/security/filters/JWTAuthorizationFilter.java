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
import pl.wojciechkabat.hotchilli.entities.GuestUser;
import pl.wojciechkabat.hotchilli.entities.Role;
import pl.wojciechkabat.hotchilli.entities.User;
import pl.wojciechkabat.hotchilli.repositories.GuestUserRepository;
import pl.wojciechkabat.hotchilli.repositories.UserRepository;
import pl.wojciechkabat.hotchilli.security.common.RoleEnum;
import pl.wojciechkabat.hotchilli.security.token.JwtSettings;
import pl.wojciechkabat.hotchilli.utils.EmailValidator;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final UserRepository userRepository;
    private final GuestUserRepository guestUserRepository;
    private final JwtSettings jwtSettings;

    private static final String HEADER_STRING = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    public JWTAuthorizationFilter(AuthenticationManager authManager,
                                  UserRepository userRepository,
                                  GuestUserRepository guestUserRepository, JwtSettings jwtSettings) {
        super(authManager);
        this.userRepository = userRepository;
        this.guestUserRepository = guestUserRepository;
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
            final String subject = parseToken(token);
            boolean isGuest = !EmailValidator.EmailValidator.validate(subject);
            UsernamePasswordAuthenticationToken authentication = getAuthentication(subject, isGuest);
            if (checkTokenWithUsersIdentity(subject, isGuest)) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            chain.doFilter(req, res);
        } catch (JWTVerificationException e) {
            chain.doFilter(req, res);
        }

    }

    private boolean checkTokenWithUsersIdentity(String subject, boolean isGuest) {
        if (!isGuest) {
            return userRepository.findByEmail(subject).isPresent();
        } else {
            return guestUserRepository.findByDeviceId(subject).isPresent();
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String subject, boolean isGuest) {
        if (!isGuest) {
            final Optional<User> applicationUser = userRepository.findByEmail(subject);
            if (applicationUser.isPresent() && !CollectionUtils.isEmpty(applicationUser.get().getRoles())) {
                final User user = applicationUser.get();
                final List<GrantedAuthority> grantedAuthorities = getGrantedAuthorities(user.getRoles());
                return new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), grantedAuthorities);
            }
        } else {
            final Optional<GuestUser> guestUser = guestUserRepository.findByDeviceId(subject);
            if (guestUser.isPresent()) {
                final GuestUser user = guestUser.get();
                final List<GrantedAuthority> grantedAuthorities = getGrantedAuthorities(Arrays.asList(new Role(12L, RoleEnum.GUEST)));
                return new UsernamePasswordAuthenticationToken(user.getDeviceId(), null, grantedAuthorities);
            }
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
