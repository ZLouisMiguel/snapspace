package com.snapspace.service;

import com.snapspace.model.User;
import com.snapspace.dao.UserDAO;
import com.snapspace.util.PasswordUtil;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public User login(String email, String password) {
        User u = userDAO.findByEmail(email);
        if (u != null && PasswordUtil.check(password, u.getPasswordHash())) {
            return u;
        }
        return null;
    }

    public void register(User user) {
        userDAO.save(user);
    }
}