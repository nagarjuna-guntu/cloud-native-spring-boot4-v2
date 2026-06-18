package com.bookshop.bookedgeservice.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @GetMapping("/user")
    public Mono<User> getUser(@AuthenticationPrincipal OidcUser oidcUser) {

        IO.println("roles: " + oidcUser.getClaimAsStringList("roles"));
        IO.println("ID Token Claims: " + oidcUser.getClaims());
        return Mono.just(
                new User(oidcUser.getPreferredUsername(), oidcUser.getGivenName(),
                        oidcUser.getFamilyName(), oidcUser.getClaimAsStringList("roles")));
    }
}
