package com.codingdojo.GroupProject.Controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.codingdojo.GroupProject.models.User;
import com.codingdojo.GroupProject.services.UserService;
import com.codingdojo.GroupProject.validation.UserValidation;


@Controller
public class UserController {
	@Autowired
	private UserService uservice;
	
	@Autowired
	private UserValidation uvalidation;
	
	@RequestMapping("/")
	public String index(@ModelAttribute("user")User user) {
		return "login.jsp";
	}
	@RequestMapping(value="/", method=RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
		uvalidation.validate(user, result);
		if (result.hasErrors()) {
			return "login.jsp";
		}
		User newUser = uservice.registerUser(user);
		session.setAttribute("user_id", newUser.getId());
		return "redirect:/dashboard";
	}
	 @RequestMapping(value="/login", method=RequestMethod.POST)
	 public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, HttpSession session, RedirectAttributes redirs) {
	     // if the user is authenticated, save their user id in session
		 boolean isAuthenticated = uservice.authenticateUser(email, password);
		 if(isAuthenticated) {
			 User u = uservice.findUserbyEmail(email);
			 session.setAttribute("user_id", u.getId());
			 return "redirect:/dashboard";
		 }
	     // else, add error messages and return the login page
		 else {
			 redirs.addFlashAttribute("error", "Invalid Email/Password");
			 return "redirect:/";
		 }
	 }
	 
	 @RequestMapping("/logout")
	 public String logout(HttpSession session) {
	     // invalidate session
		 session.invalidate();
	     // redirect to login page
		 return "redirect:/";
	 }
}
