package sg.edu.nus.iss.ssfproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpSession;
import sg.edu.nus.iss.ssfproject.models.User;
import sg.edu.nus.iss.ssfproject.service.UserService;



@Controller
public class UserController {

    @Autowired
    UserService userService;
    
    @GetMapping("/register")
    public String getUserRegisterPage(Model model) {
        User user = new User();
        model.addAttribute("user",user);
        return "register";
    }

    //add bindingresult and verification
    @PostMapping("/register/verify")
    public String verifyUserRegistration(@ModelAttribute("user") User user,Model model) throws JsonProcessingException {
        
        userService.register(user);
        return "redirect:/";
    }
    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @PostMapping("/login/verify")
    public String verifyLogin(@RequestBody MultiValueMap<String,String> loginForm,Model model,HttpSession session) throws JsonProcessingException {
        String username = loginForm.getFirst("username");
        String password = loginForm.getFirst("password");

        //check to see if username exists
        if (!userService.checkIfUsernameExists(username)) {
            model.addAttribute("errorMessage","User account does not exist");
            return "login";
        }
        //verify password if username exists
        if (!userService.checkIfPasswordMatches(username, password)) {
            model.addAttribute("errorMessage","Invalid password");
            return "login";
        }
        //add session
        return "redirect:/";
    }
    
    
}
