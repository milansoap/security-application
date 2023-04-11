package com.example.websecurity.services;

import com.example.websecurity.models.User;
import com.example.websecurity.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//@Service
//@Transactional
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private UserRepository userRepository;
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User user = userRepository.findByEmail(email);
//        if (user==null) {
//            throw new UsernameNotFoundException("No user fonund");
//        }
//        return org.springframework.security.core.userdetails.User(
//                user.getEmail(),
//                user.getPassword()
//        )
//    }
//
//
//}
