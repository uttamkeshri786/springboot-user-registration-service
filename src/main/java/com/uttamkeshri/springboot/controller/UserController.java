package com.uttamkeshri.springboot.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uttamkeshri.springboot.model.Role;
import com.uttamkeshri.springboot.model.RoleName;
import com.uttamkeshri.springboot.model.User;
import com.uttamkeshri.springboot.payload.ApiResponse;
import com.uttamkeshri.springboot.payload.LoginRequest;
import com.uttamkeshri.springboot.payload.SignUpRequest;
import com.uttamkeshri.springboot.repository.RoleRepository;
import com.uttamkeshri.springboot.repository.UserRepository;

import javax.validation.Valid;

import static com.uttamkeshri.springboot.model.RoleName.ROLE_ADMIN;
import static com.uttamkeshri.springboot.model.RoleName.ROLE_VIEW;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Value("${login.attempt}")
    private int loginAttemptExceed;

    @Value("${login.pending.status}")
    private int loginPendingStatus;

    @GetMapping("/get-all-locked-account")
    public ResponseEntity<?> getAllLockedAccounts() {

        try {
            List<User> users= userRepository.findByLoginAttempt(loginAttemptExceed);
            return ResponseEntity.ok(new ApiResponse(true,users,"Successfully retrieved all locked accounts"));
        }catch (Exception e){
            return ResponseEntity.ok(new ApiResponse(false,"",e.getMessage()));
        }

    }

    @GetMapping("/get-all-pending-account")
    public ResponseEntity<?> getAllPendingAccounts() {

        try {
            List<User> users= userRepository.findByPendingStatus(loginPendingStatus);
            return ResponseEntity.ok(new ApiResponse(true,users,"Successfully retrieved all pending accounts"));
        }catch (Exception e){
            return ResponseEntity.ok(new ApiResponse(false,"",e.getMessage()));
        }

    }

    @PostMapping("/unlock_account_by_id")
    public ResponseEntity<?> unlockAccount(@RequestBody User users){
        try {
            Optional<User> user= userRepository.findById(users.getId());
            user.get().setLoginAttemptCount(0);
            userRepository.save(user.get());
            return ResponseEntity.ok(new ApiResponse(true,"","Successfully account unlocked"));
        }catch (Exception e){
            return ResponseEntity.ok(new ApiResponse(false,"",e.getMessage()));
        }

    }

    @PostMapping("/activate_pending_account_by_id")
    public ResponseEntity<?> activatePendingAccountById(@RequestBody User users){
        try {
            Optional<User> user= userRepository.findById(users.getId());
            user.get().setStatus(3);
            userRepository.save(user.get());
            return ResponseEntity.ok(new ApiResponse(true,"","Successfully account activated"));
        }catch (Exception e){
            return ResponseEntity.ok(new ApiResponse(false,"",e.getMessage()));
        }

    }

    @GetMapping("/get-all-user")
    public ResponseEntity<?> getAllUserWithPublicAccess(){
        try {
            List<User> user= userRepository.findAll();
            Optional<Role> roles= roleRepository.findByName(ROLE_VIEW);
            List<User> result = user.stream().filter(x-> {
                return x.getRoles().stream().allMatch(y-> y.getId() ==roles.get().getId());
            }).collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse(true,result.stream().map(x->x.getName()),"Successfully data retrieved"));
        }catch (Exception e){
            return ResponseEntity.ok(new ApiResponse(false,"",e.getMessage()));
        }

    }


    @PostMapping("/edit")
    public ResponseEntity<?> editUser(@RequestBody User users){
        try {
            Optional<User> user= userRepository.findById(users.getId());
           if (users.getName() !=null)
               user.get().setName(users.getName());
           if (users.getEmail() !=null)
            user.get().setEmail(users.getEmail());

            userRepository.save(user.get());
            return ResponseEntity.ok(new ApiResponse(true,"","Successfully account updated"));
        }catch (Exception e){
            return ResponseEntity.ok(new ApiResponse(false,"",e.getMessage()));
        }

    }


    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody User users){
        try {
            Optional<User> user= userRepository.findById(users.getId());
            Optional<Role> roles= roleRepository.findByName(ROLE_ADMIN);
            if(user.get().getRoles().stream().allMatch(y-> y.getId() ==roles.get().getId())){
                return ResponseEntity.ok(new ApiResponse(false,"","Sorry!,you don't have permission to delete this account"));

            }else {
                user.get().setStatus(1);
                userRepository.save(user.get());
                return ResponseEntity.ok(new ApiResponse(true,"","Successfully account deleted"));

            }
        }catch (Exception e){
            return ResponseEntity.ok(new ApiResponse(false,"",e.getMessage()));
        }

    }

}
