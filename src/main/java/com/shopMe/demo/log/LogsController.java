package com.shopMe.demo.log;

import com.shopMe.demo.exceptions.AuthenticationFailException;
import com.shopMe.demo.user.User;
import com.shopMe.demo.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logs")
public class LogsController {

    @Autowired
    private LogsService logsService;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/user")
    public ResponseEntity<List<Logs>> getLogListByUser(@RequestParam("token") String token) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);
        List<Logs> logs = logsService.getAllLogListByUser(user);
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

//    @GetMapping("/{status}")
//    public ResponseEntity<List<Logs>> getLogListByStatus(@PathVariable("status") String status) throws AuthenticationFailException {
//        List<Logs> logs = logsService.getAllLog(status);
//        return new ResponseEntity<>(logs, HttpStatus.OK);
//    }

    @GetMapping("/")
    public ResponseEntity<List<Logs>> getAllLogs(@RequestParam("status") String status) throws AuthenticationFailException {
        List<Logs> logs = logsService.getAllLogListByStatus(status);
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }
}
