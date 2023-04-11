package com.example.websecurity.dao;

import com.example.websecurity.exceptions.LoginWayException;
import com.example.websecurity.repository.UserRepository;
import com.example.websecurity.vao.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.security.auth.login.AccountLockedException;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Repository
public class UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Autowired
    public UserDao(@Lazy BCryptPasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDetails findUserByEmail(String email, String loginType) throws SQLException, AccountLockedException, LoginWayException {
        if (loginType.equals("basicLogin")) {
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("Email cannot be null or empty");
            }
            String regex = "^(.+)@(.+)$";
            Pattern pattern = Pattern.compile(regex);
            if (!pattern.matcher(email).matches()) {
                throw new IllegalArgumentException("Invalid email format");
            }
            String sql = "SELECT * FROM user WHERE email = ?";
            PreparedStatement pstmt = jdbcTemplate.getDataSource().getConnection().prepareStatement(sql);
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String emailResult = rs.getString("email");
                String passwordResult = rs.getString("password");
                int failedAttempts = rs.getInt("failed_attempt");
                boolean accountLocked = rs.getBoolean("account_locked");
                Date lockTimeDate = rs.getObject("lock_time", Date.class);
                boolean oauth = rs.getBoolean("oauth");

                // Convert lockTimeDate (java.util.Date) to LocalDateTime
                LocalDateTime lockTime = lockTimeDate != null ? lockTimeDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;

                if (oauth) {
                    throw new LoginWayException("Wrong type of login");
                }
                if (accountLocked) {
                    LocalDateTime currentTime = LocalDateTime.now();
                    if (lockTime != null && currentTime.isAfter(lockTime)) {
                        System.out.println(currentTime.isAfter(lockTime));
                        // Unlock the account and reset the failed attempts
                        unlockAccount(email);
                    } else {
                        throw new AccountLockedException("Account is locked");
                    }
                } else if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                    lockAccount(email);
                }

                return new User(emailResult, passwordResult, Collections.emptyList());
            } else {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }
        } else if (loginType.equals("oauthLogin")) {
            String sql = "SELECT * FROM user WHERE email = ?";
            PreparedStatement pstmt = jdbcTemplate.getDataSource().getConnection().prepareStatement(sql);
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String emailResult = rs.getString("email");
                boolean oauth = rs.getBoolean("oauth");
                String passwordResult = rs.getString("password");

                return new User(emailResult, passwordResult, Collections.emptyList());
            } else {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }
        } else {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
    }


    private void updateUserFailedAttempts(String email) {
        String sql = "UPDATE user SET failed_attempt = failed_attempt + 1 WHERE email = ?";
        jdbcTemplate.update(sql, email);
    }

    public void lockAccount(String email) {
        LocalDateTime lockTime = LocalDateTime.now().plusHours(1);
        String sql = "UPDATE user SET account_locked = 1, failed_attempt = 0, lock_time = ? WHERE email = ?";
        jdbcTemplate.update(sql, lockTime, email);
    }

    private void unlockAccount(String email) {
        String sql = "UPDATE user SET account_locked = 0, failed_attempt = 0, lock_time = NULL WHERE email = ?";
        jdbcTemplate.update(sql, email);
    }


}