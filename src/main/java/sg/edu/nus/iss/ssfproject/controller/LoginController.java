package sg.edu.nus.iss.ssfproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.edu.nus.iss.ssfproject.models.User;
import sg.edu.nus.iss.ssfproject.service.LoginUserService;



@Controller
public class LoginController {

    @Autowired
    LoginUserService loginUserService;
    
    @GetMapping("/register")
    public String getUserRegisterPage(Model model,HttpSession session) {
        User user = new User();
        model.addAttribute("verifieduser",session.getAttribute("verifieduser"));
        //if a user is logged in and tries to go to the register page, redirect them to landing page
        if (session.getAttribute("verifieduser")!= null) {
            return "redirect:/";
        }
        model.addAttribute("user",user);
        return "register";
    }

    //add bindingresult and verification
    @PostMapping("/register/verify")
    public String verifyUserRegistration(@Valid @ModelAttribute("user") User user,BindingResult result,
    Model model) throws JsonProcessingException {
        //for any form validation errors
        if (result.hasErrors()) {
            return "register";
        }
        //check if username exists
        if (loginUserService.checkIfUsernameExists(user.getUsername())) {
            model.addAttribute("errorMessage","User account already exists.");
            return "register";
        }
        //check if email exists
        if (loginUserService.checkIfEmailExists(user.getEmail())) {
            model.addAttribute("errorMessage","Email already exists.");
            return "register";

        }
        
        loginUserService.register(user);
        return "redirect:/";
    }
    @GetMapping("/login")
    public String getLoginPage(HttpSession session,Model model) {
        model.addAttribute("verifieduser",session.getAttribute("verifieduser"));
         //if a user is logged in and tries to go to the register page, redirect them to landing page
        if (session.getAttribute("verifieduser")!= null) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login/verify")
    public String verifyLogin(@RequestBody MultiValueMap<String,String> loginForm,Model model,HttpSession session) throws JsonProcessingException {
        String username = loginForm.getFirst("username");
        String password = loginForm.getFirst("password");

        //check to see if username exists
        if (!loginUserService.checkIfUsernameExists(username)) {
            model.addAttribute("errorMessage","User account does not exist");
            return "login";
        }
        //verify password if username exists
        if (!loginUserService.checkIfPasswordMatches(username, password)) {
            model.addAttribute("errorMessage","Invalid password");
            return "login";
        }
        User verifiedUser = loginUserService.getVerfiedUser(username);
        session.setAttribute("verifieduser",verifiedUser);
        //add session
        String redirectUrl = (String) session.getAttribute("redirectUrl");
        //this function helps a user to go back to their previous page after logging in, else go back to landing page by default
        if (redirectUrl == null) {
            redirectUrl = "/";
        }

        session.removeAttribute("redirectUrl"); // remove lingering redirectUrl post-login.
        return "redirect:" + redirectUrl;
        
    }

    @GetMapping("/logout")
    public String logoutUser(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
    
}
