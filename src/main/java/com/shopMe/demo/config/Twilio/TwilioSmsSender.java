package com.shopMe.demo.config.Twilio;

import com.shopMe.demo.common.ApiResponse;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsSender{

    private final TwilioConfiguration twilioConfiguration;

    @Autowired
    public TwilioSmsSender(TwilioConfiguration twilioConfiguration) {
        this.twilioConfiguration = twilioConfiguration;
    }


    public VerificationResult SmsSender(String phone) {
        try{
            com.twilio.rest.verify.v2.Service serice = com.twilio.rest.verify.v2.Service.creator("sta otp").create();
            Verification verification  = Verification.creator(
                    twilioConfiguration.getServiceId(),
                    phone,
                    "sms"
            ).create();
        if("approved".equals(verification.getStatus())|| "pending".equals(verification.getStatus())) {
            return new VerificationResult(verification.getSid());
        }
    } catch (ApiException exception) {
        return new VerificationResult(new String[] {exception.getMessage()});
    }
        return null;

        
    }

    private boolean phoneNumberValid(String phoneNumber) {
        return true;
    }


    public VerificationResult checkverification(String phone, String code) {
        try{
            VerificationCheck verification = VerificationCheck.creator(twilioConfiguration.getServiceId())
                    .setTo(phone)
                    .setCode(code)
                    .create();
            if("approved".equals(verification.getStatus())) {
                return new VerificationResult(verification.getSid());
            }
            return new VerificationResult(new String[]{"Invalid code."});
        } catch (ApiException exception){

            return new VerificationResult(new String[]{exception.getMessage()});
        }


    }
}
