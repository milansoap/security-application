package com.example.websecurity.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Repository
public class UserDao {

    private final BCryptPasswordEncoder passwordEncoder;
    private final List<UserDetails> APPLICATION_USERS;

    @Autowired
    public UserDao(@Lazy BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.APPLICATION_USERS = Arrays.asList(
                new User(
                        "admin@gmail.com",
                        passwordEncoder.encode("password"),
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))
                ),
                new User(
                        "user@gmail.com",
                        passwordEncoder.encode("password"),
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );
    }




    public UserDetails findUserByEmail(String email) {
       return APPLICATION_USERS.stream().filter(u -> u.getUsername().equals(email)).findFirst().orElseThrow(() -> new UsernameNotFoundException("No user was found"));
    }

}
