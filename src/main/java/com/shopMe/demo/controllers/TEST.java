package com.shopMe.demo.controllers;


import com.shopMe.demo.config.Twilio.SmsRequest;
import com.shopMe.demo.config.Twilio.TwilioSmsSender;
import com.shopMe.demo.config.Twilio.VerificationResult;
import com.shopMe.demo.model.Market;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.shopMe.demo.repository.*;
import org.springframework.web.servlet.function.EntityResponse;

import javax.annotation.security.RolesAllowed;
import javax.swing.text.html.parser.Entity;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TEST {


@Autowired
    private TwilioSmsSender twilioSmsSender;




    @Autowired
    private MarketRepository marketRepository;
    @GetMapping("/sms")
    public ResponseEntity<String> TEST(SmsRequest smsRequest){

        VerificationResult result=twilioSmsSender.SmsSender(smsRequest);
        if(result.isValid())
        {
            return new ResponseEntity<>("Otp Sent..",HttpStatus.OK);
        }
        return new ResponseEntity<>("Otp failed to sent..",HttpStatus.BAD_REQUEST);

    }

    @GetMapping("/sms/verify")
    public ResponseEntity<String> TEST(String phone, String code){

        VerificationResult result = twilioSmsSender.checkverification(phone,code);
        if(result.isValid())
        {
            return new ResponseEntity<>("Your number is Verified",HttpStatus.OK);
        }
        return new ResponseEntity<>("Something wrong/ Otp incorrect",HttpStatus.BAD_REQUEST);

    }
}
