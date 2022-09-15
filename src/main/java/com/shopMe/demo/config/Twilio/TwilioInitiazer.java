package com.shopMe.demo.config.Twilio;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;



@Configuration
public class TwilioInitiazer {

    private final TwilioConfiguration twilioConfiguration;

    @Autowired
    public TwilioInitiazer(TwilioConfiguration twilioConfiguration){
        this.twilioConfiguration = twilioConfiguration;
        Twilio.init(
               twilioConfiguration.getAccountId(),
               twilioConfiguration.getAuthToken()
        );


    }
}
