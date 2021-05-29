package Code.assetstore.controllers;

import Code.assetstore.models.Assets;
import Code.assetstore.models.User;
import Code.assetstore.payloads.EditUserRequest;
import Code.assetstore.payloads.MessageResponse;
import Code.assetstore.repository.UserRepository;
import Code.assetstore.security.services.AssetService;
import Code.assetstore.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AssetService assetService;

    @Autowired
    PasswordEncoder encoder;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/getByToken")
    public Optional<User> getUserByToken(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
        return user;
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/update/{userId}")
    public ResponseEntity<?> getUserByToken(@RequestBody EditUserRequest user, @PathVariable(name = "userId") String userId){
        Optional<User>userOld;
        if(userId.equals("0")){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            userOld = userRepository.findById(userDetails.getId());
            System.out.print("yse");
        }else {
            userOld = userRepository.findById(userId);
        }
        if(!userOld.isPresent()){
            return ResponseEntity.notFound().build();
        }
        User newUser = userOld.get();
        if(user.getUsername() != null){
            if(userRepository.existsByUsername(user.getUsername())) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Такое имя уже занято"));
            }else{
                newUser.setUsername(user.getUsername());
            }

        }
        if(user.getEmail() != null){
            if(userRepository.existsByEmail(user.getUsername())) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Этот email уже используется"));
            }else {
                newUser.setEmail(user.getEmail());
            }
        }

        if(user.getPassword() != null){
            newUser.setPassword(encoder.encode(user.getPassword()));
        }
        if(user.getItems() != null){
            newUser.setItems(user.getItems());
        }
        if(user.getCart() != null){
            newUser.setCart(user.getCart());
        }
        userRepository.save(newUser);
        return ResponseEntity.ok(newUser);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/userItems")
    public List<Assets> getAllAssets(){
        List<Assets> getassets = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
        if(user.get().getItems().isEmpty()){
            return null;
        }
        for(int i = 0; i < user.get().getItems().size(); i++){
            getassets.add(assetService.getAsset(user.get().getItems().get(i)));
        }
        return getassets;

    }
}
