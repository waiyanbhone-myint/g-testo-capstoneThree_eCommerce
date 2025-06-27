package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.UserActivityLogger;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("/profile")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class ProfileController {

    private ProfileDao profileDao;
    private UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    // GET /profile - Get current user's profile
    @GetMapping
    public Profile getProfile(Principal principal) {
        try {
            String userName = principal.getName();
            UserActivityLogger.logAction("User %s viewed their profile", userName);

            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            Profile profile = profileDao.getByUserId(userId);

            if (profile == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found");
            }

            return profile;

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    // PUT /profile - Update current user's profile
    @PutMapping
    public Profile updateProfile(Principal principal, @RequestBody Profile profile) {
        try {
            String userName = principal.getName();
            UserActivityLogger.logAction(userName, "Updating profile information");

            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            profileDao.update(userId, profile);

            return profileDao.getByUserId(userId);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}