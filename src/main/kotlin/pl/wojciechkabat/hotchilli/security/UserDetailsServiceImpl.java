package pl.wojciechkabat.hotchilli.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.wojciechkabat.hotchilli.entities.Role;
import pl.wojciechkabat.hotchilli.repositories.UserRepository;
import pl.wojciechkabat.hotchilli.security.common.UserSecurityContext;
import pl.wojciechkabat.hotchilli.security.exceptions.NoUserWithGivenEmailException;

import static java.util.stream.Collectors.toList;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        pl.wojciechkabat.hotchilli.entities.User user = userRepository.findByEmail(login).orElseThrow(NoUserWithGivenEmailException::new);
        return new User(
                user.getEmail(),
                user.getPassword(),
                UserSecurityContext.convertUserRoles(
                        user.getRoles()
                                .stream()
                                .map(Role::getValue)
                                .collect(toList()))
        );
    }
}
