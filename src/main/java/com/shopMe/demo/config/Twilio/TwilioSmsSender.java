package com.shopMe.demo.config.Twilio;


import com.twilio.exception.ApiException;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import org.springframework.beans.factory.annotation.Autowired;
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
