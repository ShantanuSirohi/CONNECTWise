package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	//			Home handler
	
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}
	
	//			About handler
	
	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About Us - Smart Contact Manager");
		return "about";
	}
	
	//			Contact us handler
	
	@GetMapping("/contact")
	public String contact(Model model) {
		model.addAttribute("title", "Contact Us - Smart Contact Manager");
		return "contact";
	}
	
	//			Services handler
	
	@GetMapping("/services")
	public String services(Model model) {
		model.addAttribute("title", "Services - Smart Contact Manager");
		return "services";
	}
	
	//			Login handler
	
	@GetMapping("/signin")
	public String customlogin(Model model) {
		model.addAttribute("title", "Logging - Smart Contact Manager");
		return "login";
	} 
	
	//			Signup handler
	
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Signing - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	//handler for registering user
	
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result1 ,
			@RequestParam(value="agreement",defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {
		
		try {
			if(!agreement) {
				System.out.println("You have not checked the terms and conditions.");
				throw new Exception("You have not checked the terms and conditions.");
			}
			if(result1.hasErrors()) {
				System.out.println("Error "+result1.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setStatus(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			
			System.out.println("Agreement "+agreement);
			System.out.println("User "+user);
			
			User result = this.userRepository.save(user);
			
			model.addAttribute("user", new User());
			
			session.setAttribute("message", new Message("Successfully Registered","alert-success" ));
			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("Something went wrong"+e.getMessage(),"alert-danger" ));
			return "signup";
		}
		
	}
	
}
