 package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.hibernate.boot.jaxb.mapping.JaxbFetchProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contacts;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@SuppressWarnings("unused")
@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	
	//method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model m,Principal p) {
		String userName = p.getName();
		System.out.println("USERNAME "+userName);
		
		
		//get the user using username(email)

		
		User user = this.userRepository.getUserByusername(userName);		
		System.out.println("USER "+user);
		m.addAttribute("user",user);
	}
	
	
	// home dashboard
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title","User Dashboard");
		return "normal/user_dashboard";
	}
	
	
	//open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact - Smart Contact Manager");
		model.addAttribute("contact", new Contacts());
		
		return "normal/add_contact_form";
	}
	
	//Proccessing add contact form
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contacts contacts,
			@RequestParam("profileImage") MultipartFile file,                   //Mapping profile image for the contact
			Principal principal,
			HttpSession session) {
		
		try {
			String name=principal.getName();
			User user=this.userRepository.getUserByusername(name);
			
			//processing and uploading file
			
			if(file.isEmpty()) {
				//if the file is empty 
				System.out.println("File is empty");
				contacts.setImage_url("contact.png");
			}
			else {
				//upload the file to folder and update the name to contacts
				contacts.setImage_url(file.getOriginalFilename());
				File savefile = new ClassPathResource("static/img").getFile();
				
				Path path = Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				
				System.out.println("Image is uploaded");
				
			}
			
			user.getContacts().add(contacts);
			contacts.setUser(user);
			
			this.userRepository.save(user);
			System.out.println("Data "+contacts);
			System.out.println("Added to the data base");
		} catch (Exception e) {
			System.out.println("Error"+e.getMessage());
			e.printStackTrace();

		}
		return "normal/add_contact_form";
	}
	
	
	
	//show contacts
	
	//per page only 5 contacts in accordance to pagination=5[n]
	//current page=0[current]
	
	
	
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page,Model model,Principal principal) {
		model.addAttribute("title","Contacts-Smart Contact Manager");
		
		
		//contact list
		String userName = principal.getName();
		User user = this.userRepository.getUserByusername(userName);
		
		Pageable pageable = PageRequest.of(page, 3);
		
		Page<Contacts> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		
		
		return "normal/show_contacts";
	}
	
	//showing particular contact details
	@GetMapping("/{c_id}/contact")
	public String showContactDetail(@PathVariable("c_id") Integer c_id,Model model, Principal principal) {
		System.out.println("CID "+c_id);
		Optional<Contacts> contactsOptional = this.contactRepository.findById(c_id);
		Contacts contacts = contactsOptional.get();
		
		//currently which user is logged in
		String userName = principal.getName();
		User user = this.userRepository.getUserByusername(userName);
		
		if(user.getId()==contacts.getUser().getId()) {
			model.addAttribute("contacts", contacts);
			model.addAttribute("title",contacts.getName());
		}
		
		return "normal/contact_detail";
	}
	
	//deleteing the contact 
	@GetMapping("/delete/{c_id}")
	public String deleteContact(@PathVariable("c_id") Integer c_id, Model model,Principal principal) {
		Contacts contacts=this.contactRepository.findById(c_id).get();
		User user=this.userRepository.getUserByusername(principal.getName());
		user.getContacts().remove(contacts);
		this.userRepository.save(user);
		return "redirect:/user/show-contacts/0";
	}
	
	//updating the contact
	@PostMapping("/update-contact/{c_id}")
	public String updateForm(@PathVariable("c_id") Integer c_id ,Model model) {
		model.addAttribute("title","Update Contact");
		Contacts contacts = this.contactRepository.findById(c_id).get();
		model.addAttribute("contacts",contacts);
		return "normal/update_form";
	}
	
	//update contact handler
	@RequestMapping(value="/process-update",method=RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contacts contacts,@RequestParam("profileImage") MultipartFile file,Model model,Principal principal) {
		
		try {
			
			//fetch old contact details
			Contacts oldContactsDetail = this.contactRepository.findById(contacts.getC_id()).get();
			
			
			
			//new image
			if(!file.isEmpty()) {
				//file to be rewrite to the updated contact
				
				//1.delete old photo				
				File deletefile = new ClassPathResource("static/img").getFile();
				File file1=new File(deletefile,oldContactsDetail.getImage_url());
				file1.delete();
				//2.Update new photo
				
				File savefile = new ClassPathResource("static/img").getFile();
				
				Path path = Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				
				contacts.setImage_url(file.getOriginalFilename());
			}
			User user=this.userRepository.getUserByusername(principal.getName());
			contacts.setUser(user);
			this.contactRepository.save(contacts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("Contact "+contacts.getName());
		return "redirect:/user/"+contacts.getC_id()+"/contact";
	}
	
	
	
	//handler for user dashboard
	@GetMapping("/profile")
	public String userProfile(Model model) {
		model.addAttribute("title", "Profile Page");
		return "normal/profile";
	}
}
