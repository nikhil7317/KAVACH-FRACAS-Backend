package com.railbit.tcasanalysis.controller.authController;



import com.railbit.tcasanalysis.DTO.UserDTO;
import com.railbit.tcasanalysis.entity.User;
import com.railbit.tcasanalysis.entity.jwtEntity.JwtRequest;
import com.railbit.tcasanalysis.entity.jwtEntity.JwtUtil;
import com.railbit.tcasanalysis.service.ReCaptchaService;
import com.railbit.tcasanalysis.service.serviceImpl.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin("*")
@AllArgsConstructor
@RequestMapping("/tcasapi")
public class AuthController {

    private static final Logger log = LogManager.getLogger(AuthController.class);
    private AuthenticationManager authenticationManager;
    private UserServiceImpl userService;
    private JwtUtil jwtUtil;
    private final ModelMapper mapper;
//    private EmailService emailService;

    private final ReCaptchaService recaptchaService;

    @PostMapping("/login/")
    public ResponseEntity<Map<String, Object>> generateToken(@RequestBody JwtRequest jwtRequest) throws Exception {
        try {
//            boolean captchaCheck = jwtRequest.getSplCode() == null || jwtRequest.getSplCode().isEmpty() || !jwtRequest.getSplCode().equals("ahctpaconrofkcah@rk8540");
            boolean captchaCheck = true;
//            if (StringUtils.isEmpty(jwtRequest.getCaptchaToken()) && captchaCheck) {
//                log.error("Captcha token is required for authentication");
//                HashMap<String, Object> payload = new HashMap<>();
//                payload.put("status", HttpStatus.BAD_REQUEST);
//                payload.put("message", "Captcha token is required");
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
//            }
            boolean captchaVerified=true;
//            boolean captchaVerified;
//            if (captchaCheck) {
//                captchaVerified = recaptchaService.verify(jwtRequest.getCaptchaToken());
//            } else {
//                captchaVerified = true; // Skip captcha verification if splCode is provided
//            }

            if (!captchaVerified) {
                log.error("Captcha verification failed for user: {}", jwtRequest.getUsername());
                HashMap<String, Object> payload = new HashMap<>();
                payload.put("status", HttpStatus.NOT_ACCEPTABLE);
                payload.put("message", "Captcha verification failed");
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(payload);
            } else {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword())
                );
            }

        } catch (DisabledException e) {
            HashMap<String, Object> payload = new HashMap<>();
            payload.put("status", HttpStatus.BAD_REQUEST.value());
            payload.put("message", "User is disabled");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", jwtRequest.getUsername());
            HashMap<String, Object> payload = new HashMap<>();
            payload.put("status", HttpStatus.UNAUTHORIZED.value());
            payload.put("message", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payload);
        } catch (UsernameNotFoundException e) {
            log.error("User not found: {}", jwtRequest.getUsername());
            HashMap<String, Object> payload = new HashMap<>();
            payload.put("status", HttpStatus.NOT_FOUND.value());
            payload.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payload);
        } catch (Exception e) {
            log.error("Exception occurred during authentication: {}", e.getMessage());
            HashMap<String, Object> payload = new HashMap<>();
            payload.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            payload.put("message", "An unexpected error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payload);
        }

        UserDetails userDetails = userService.loadUserByUsername(jwtRequest.getUsername());
        String token = jwtUtil.generateToken(userDetails);
        UserDTO user = mapper.map(userDetails, UserDTO.class);

        if (user == null) {
            log.error("User not found: {}", jwtRequest.getUsername());
            HashMap<String, Object> payload = new HashMap<>();
            payload.put("status", HttpStatus.NOT_FOUND.value());
            payload.put("message", "User with " + jwtRequest.getUsername() + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payload);
        }

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("status", HttpStatus.OK.value());
        payload.put("message", "Log In Successful");
        payload.put("token", token);
        payload.put("user", user);
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/current-user/")
    public User getCurrentUser(Principal principal){
        return ((User) this.userService.loadUserByUsername(principal.getName()));
    }
    @GetMapping("/test")
    public String test(){
        return "hello world";

    }
    @DeleteMapping("/test")
    public String test2(){
        return "hello world";
    }

}
