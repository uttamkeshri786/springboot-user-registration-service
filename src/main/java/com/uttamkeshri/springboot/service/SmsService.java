package com.uttamkeshri.springboot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;


@Component
public class SmsService {

	    //Enter your SID Number from Twilio
	    private final String ACCOUNT_SID ="AC1f1f94dec0cbcafebdb497c83b25af66";

	    //enter your Auth token from Twilio Account
	    private final String AUTH_TOKEN = "eeedd352073f508f4dcb2d713d348ef5";

	    //Enter the phone number generated from Twilio
	    private final String FROM_NUMBER = "+16072845837";

	    public void send(SmsPojo sms) {
	    	Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

	        Message message = Message.creator(new PhoneNumber(sms.getTo()), new PhoneNumber(FROM_NUMBER), sms.getMessage())
	                .create();
	        System.out.println("here is my id:"+message.getSid());// Unique resource ID created to manage this transaction

	    }

	    public void receive(MultiValueMap<String, String> smscallback) {
	    }
	
}