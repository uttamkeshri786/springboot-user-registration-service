package com.uttamkeshri.springboot.controller;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uttamkeshri.springboot.service.EmailService;
import com.uttamkeshri.springboot.service.OTPService;
import com.uttamkeshri.springboot.service.EmailTemplate;

@Controller
public class OTPController {
	

@Autowired
public OTPService otpService;

@Autowired
public EmailService emailService;

@GetMapping("/generateOtp")
public String generateOTP(){
	
		Authentication auth = SecurityContextHolder.getContext().getAuthentication(); 
		String username = auth.getName();
		int otp = otpService.generateOTP(username);
		//Generate The Template to send OTP 
		EmailTemplate template = new EmailTemplate("SendOtp.html");
		Map<String,String> replacements = new HashMap<String,String>();
		replacements.put("user", username);
		replacements.put("otpnum", String.valueOf(otp));
		String message = template.getTemplate(replacements);
		try {
			emailService.sendOtpMessage("Logged in Users EmailAddress", "OTP-SpringBoot", message);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "otppage";
}

@RequestMapping(value ="/validateOtp", method = RequestMethod.GET)
public @ResponseBody String validateOtp(@RequestParam("otpnum") int otpnum){
	
		final String SUCCESS = "Entered Otp is valid";
		final String FAIL = "Entered Otp is NOT valid. Please Retry!";
		Authentication auth = SecurityContextHolder.getContext().getAuthentication(); 
		String username = auth.getName();
		//Validate the Otp 
		if(otpnum >= 0){
			
		  int serverOtp = otpService.getOtp(username);
		    if(serverOtp > 0){
		      if(otpnum == serverOtp){
		          otpService.clearOTP(username);
		
                  return (SUCCESS);
                } 
		        else {
                    return FAIL;
                   }
               }else {
              return FAIL;
               }
             }else {
                return FAIL;
         }
      }
}