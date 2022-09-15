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


    public VerificationResult SmsSender(SmsRequest smsRequest) {
        try{
            PhoneNumber to = new PhoneNumber(smsRequest.getPhoneNumber());
            PhoneNumber from = new PhoneNumber(twilioConfiguration.getTrialNumber());
            String message = smsRequest.getMessage();
            Verification verification  = Verification.creator(
                    twilioConfiguration.getServiceId(),
                    smsRequest.getPhoneNumber(),
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
