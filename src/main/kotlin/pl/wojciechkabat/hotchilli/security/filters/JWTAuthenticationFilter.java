package pl.wojciechkabat.hotchilli.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.wojciechkabat.hotchilli.entities.User;
import pl.wojciechkabat.hotchilli.repositories.UserRepository;
import pl.wojciechkabat.hotchilli.security.exceptions.AttemptAuthenticationFailedException;
import pl.wojciechkabat.hotchilli.security.model.LoginUserDto;
import pl.wojciechkabat.hotchilli.security.model.TokenService;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Configurable
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final TokenService tokenService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager,
                                   UserRepository userRepository,
                                   ObjectMapper objectMapper,
                                   TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.tokenService = tokenService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            final LoginUserDto credentials = new ObjectMapper().readValue(req.getInputStream(), LoginUserDto.class);
            final List<GrantedAuthority> grantedAuthorities = getGrantedAuthorities(credentials);

            final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(
                        credentials.getLogin(),
                        credentials.getPassword(),
                        grantedAuthorities);

            usernamePasswordAuthenticationToken.setDetails(credentials.getDeviceId());

            return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        } catch (IOException e) {
            logger.error("Attempt authentication failed", e);
            throw new AttemptAuthenticationFailedException();
        }
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException {
        Map<String, String> tokenMap = tokenService.getTokens(auth);

        res.setStatus(HttpStatus.OK.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(res.getWriter(), tokenMap);
    }

    private List<GrantedAuthority> getGrantedAuthorities(LoginUserDto credentials) {
        final Optional<User> applicationUser = userRepository.findByEmail(credentials.getLogin());
        final List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        applicationUser.ifPresent(ap -> ap.getRoles()
                .forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getValue()))));
        return grantedAuthorities;
    }

}
