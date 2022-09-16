package com.shopMe.demo.service;


import com.shopMe.demo.Settings.EmailSettingBag;
import com.shopMe.demo.Utility;
import com.shopMe.demo.config.MessageStrings;
import com.shopMe.demo.dto.user.*;
import com.shopMe.demo.exceptions.AuthenticationFailException;
import com.shopMe.demo.exceptions.CustomException;
import com.shopMe.demo.exceptions.UserNotFoundException;
import com.shopMe.demo.model.AuthenticationToken;
import com.shopMe.demo.model.User;
import com.shopMe.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    WalletService walletService;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Autowired
    SettingService settingService;

    @Autowired
    LogsService logsService;


    public User save2(User user) {
        String rawPassword = user.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    public User save(User user)  {
        boolean isUpdatingUser = (user.getId() != null);
        if (isUpdatingUser) {
            User existingUser = userRepository.findById(user.getId()).get();

            if (user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                encodePassword(user);
            }
        }
        return userRepository.save(user);
    }

    public void updateAvatar(User user){
        userRepository.save(user);
    }

    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }
    public void sendEmail(User user,HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        sendVerificationEmail(request, user);
    }
    private void sendVerificationEmail(HttpServletRequest request, User user)
            throws UnsupportedEncodingException, MessagingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);

        String toAddress = user.getEmail();
        String subject = emailSettings.getCustomerVerifySubject();
        String content = emailSettings.getCustomerVerifyContent();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFullName());

        String verifyURL = Utility.getSiteURL(request) + "/verify?code=" + user.getEmailVerifyCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);

        System.out.println("to Address: " + toAddress);
        System.out.println("Verify URL: " + verifyURL);
    }



    public SignInResponseDto signIn(SignInDto signInDto) throws AuthenticationFailException, CustomException {
        // first find User by email
        User user = userRepository.findByEmail(signInDto.getEmail()).get();
        if(!Objects.nonNull(user)){
            throw new AuthenticationFailException(MessageStrings.USER_NOT_FOUND);
        }
        // check if password is right
        if (!user.getPassword().equals(passwordEncoder.encode(signInDto.getPassword()))){
            // passwords do not match
            throw  new AuthenticationFailException(MessageStrings.USER_PASSWORD_WRONG);
        }

        AuthenticationToken token = authenticationService.getToken(user);

        if(!Objects.nonNull(token)) {
            // token not present
            throw new CustomException(MessageStrings.AUTH_TOEKN_NOT_PRESENT);
        }
        UserDataDto userDataDto = new UserDataDto(user.getId(), user.getFirstName(), user.getLastName(), user.getRoles().toString(), user.getEmail());
        return new SignInResponseDto ("success", token.getToken());
    }

    public User getById(Integer id) throws UserNotFoundException {
        try {
            return userRepository.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new UserNotFoundException(MessageStrings.USER_NOT_FOUND + id);
        }
    }

    public boolean Verify(String code) {
            User customer = userRepository.findByEmailVerifyCode(code);
            if (!customer.isEnabled()) {
                return false;
            } else {
                userRepository.enable(customer.getId());
                return true;
            }

    }

    public User findByEmail(String email) {Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);

    }

    public User findByPhoneNumber(String phoneNumber) {Optional<User> user = userRepository. findByPhoneNumber(phoneNumber);
        return user.orElse(null);

    }

    public boolean isLogin(String phoneNumber, String password) throws AuthenticationFailException {
        User user = findByPhoneNumber(phoneNumber);
        if(!Objects.nonNull(user)){
            throw new AuthenticationFailException(MessageStrings.USER_NOT_FOUND);
        }
        System.out.println(passwordEncoder.matches(password,user.getPassword()));
        if (!passwordEncoder.matches(password,user.getPassword())){
            throw new AuthenticationFailException(MessageStrings.USER_PASSWORD_WRONG);
        }
        return true;
    }




//    public ResponseDto createUser(String token, UserCreateDto userCreateDto) throws CustomException, AuthenticationFailException {
//        User creatingUser = authenticationService.getUser(token);
//        if (!canCrudUser(creatingUser.getRole())) {
//            // user can't create new user
//            throw  new AuthenticationFailException(MessageStrings.USER_NOT_PERMITTED);
//        }
//        String encryptedPassword = userCreateDto.getPassword();
//        try {
//            encryptedPassword = hashPassword(userCreateDto.getPassword());
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//            logger.error("hashing password failed {}", e.getMessage());
//        }
//
//        User user = new User(userCreateDto.getFirstName(), userCreateDto.getLastName(), userCreateDto.getEmail(), userCreateDto.getRole(), encryptedPassword );
//        User createdUser;
//        try {
//            createdUser = userRepository.save(user);
//            final AuthenticationToken authenticationToken = new AuthenticationToken(createdUser);
//            authenticationService.saveConfirmationToken(authenticationToken);
//            return new ResponseDto(ResponseStatus.success.toString(), USER_CREATED);
//        } catch (Exception e) {
//            // handle user creation fail error
//            throw new CustomException(e.getMessage());
//        }
//
//    }

//    boolean canCrudUser(Role role) {
//        if (role == Role.admin || role == Role.manager) {
//            return true;
//        }
//        return false;
//    }

//    boolean canCrudUser(User userUpdating, Integer userIdBeingUpdated) {
//        Role role = userUpdating.getRole();
//        // admin and manager can crud any user
//        if (role == Role.admin || role == Role.manager) {
//            return true;
//        }
//        // user can update his own record, but not his role
//        if (role == Role.user && userUpdating.getId() == userIdBeingUpdated) {
//            return true;
//        }
//        return false;
//    }
//
//    public List<User> findAll() {
//        return userRepository.findAll();
//    }
}
