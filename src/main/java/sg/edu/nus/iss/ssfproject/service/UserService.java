package sg.edu.nus.iss.ssfproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.edu.nus.iss.ssfproject.constant.ConstantVar;
import sg.edu.nus.iss.ssfproject.models.User;
import sg.edu.nus.iss.ssfproject.repo.UserRepo;

@Service
public class UserService {
    
    @Autowired
    UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    public void register(User user) throws JsonProcessingException {

        //store an encrypted version of user's password for security
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // TODO Auto-generated method stub
        String userJsonString = objectMapper.writeValueAsString(user);
        
        
        // Store the serialized user in Redis under the category
        
        userRepo.setHash(ConstantVar.usersRedisKey,user.getUsername().trim().toLowerCase(), userJsonString);
    }

    public Boolean checkIfUsernameExists(String username) {
        System.out.println(username.trim().toLowerCase());
        return userRepo.hasKey(ConstantVar.usersRedisKey, username.trim().toLowerCase());

    }

    public Boolean checkIfPasswordMatches(String username, String password) throws JsonProcessingException {
        String userJsonString = userRepo.getValueFromHash(ConstantVar.usersRedisKey,username.trim().toLowerCase());
        User user = objectMapper.readValue(userJsonString, User.class);

        // Verify the password
        //compare the plain text password with the encrypted password
        return passwordEncoder.matches(password, user.getPassword());
    }

    public User getVerfiedUser(String username) throws JsonMappingException, JsonProcessingException {
        String userJsonString = userRepo.getValueFromHash(ConstantVar.usersRedisKey,username.trim().toLowerCase());
        User user = objectMapper.readValue(userJsonString, User.class);
        return user;
    }
    
}
