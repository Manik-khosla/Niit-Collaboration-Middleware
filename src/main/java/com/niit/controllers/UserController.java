package com.niit.controllers;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.niit.Dao.UserDao;
import com.niit.model.Errorclass;
import com.niit.model.User;

@RestController
public class UserController {
	@Autowired
	private UserDao userdao;
	
	 public UserController()
	 {
		System.out.println("UserController class  Instantiated");
	 }

	  @RequestMapping(value="/Signup",method=RequestMethod.POST)
	  public ResponseEntity<?> Registration(@RequestBody User user)
	 {
	
		System.out.println("In UserController Registration function Invoked");
		try{
		if(userdao.IsEmailUnique(user.getEmail()))
		{
		System.out.println("User with username "+user.getEmail()+" Not Present ---> Registering user");	
		userdao.Registration(user);
		System.out.println("User registered successfully");
		return new ResponseEntity<User>(user,HttpStatus.CREATED);
		}
		else
		{
	    System.err.println("A User with username " + user.getEmail() + " already exists ---> User cannot be registered");
		Errorclass ec=new Errorclass(10,"Username already exists.Please choose a different username");
		return new ResponseEntity<Errorclass>(ec,HttpStatus.CONFLICT);
		}
	  }
		catch(Exception e)
		{
			Errorclass ec=new Errorclass(17,"Unable to register new user");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	 }
	
	  
    	@RequestMapping(value="/Login",method=RequestMethod.POST)
        public ResponseEntity<?> Login(@RequestBody User user,HttpSession session)
	   {
    	System.out.println("In UserController Login function Invoked");
    	System.out.println("Validating  User Credentials");
    	System.out.println("User-Email--->"+user.getEmail()+"   "+"User-password--->"+user.getPassword());
    	try{
		User validuser=userdao.Login(user);
    	
		if(validuser==null)
		{   
			System.err.println("User Credentials are  Incorrect");
			Errorclass ec=new Errorclass(27,"Invalid Credentials");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.NOT_FOUND);
		}
		else
		{
		System.out.println("User Credentials are correct ");
		System.out.println("Setting attribute in session Object.Session id--->"+session.getId());
		session.setAttribute("email", user.getEmail());
		validuser.setOnline(true);
		System.err.print("Changing Online status of user to TRUE");
		userdao.UpdateUserOnlineStatus(validuser);
		validuser.setProfilepicture(null);
		System.out.println("User Online-status Updated");
		return new ResponseEntity<User>(validuser,HttpStatus.OK);
		}
	  }
    	catch(Exception e)
    	{
    		Errorclass ec=new Errorclass(23,"Unable to Login due to Internal server error.Please try again");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.INTERNAL_SERVER_ERROR);	
    	}
	   }
    	
    
    	@RequestMapping(value="/Updateprofile",method=RequestMethod.PUT)
    	public ResponseEntity<?> UpdateProfile(@RequestBody User user,HttpSession session,HttpServletResponse response)
    	{
    		System.out.println("In UserController UpdateProfile function Invoked");
    		String email=(String)session.getAttribute("email");
    		if(email==null)
    		{   
    			System.out.println("Email is null");
    			Errorclass ec=new Errorclass(28,"Please Login Again");
    			return new ResponseEntity<Errorclass>(ec,HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
    		}
    		else{
    			try{
    			System.out.println("Updating user details");
    			userdao.UpdateProfileDetails(user);
    			System.err.println("User details successfully updated");
    		    return new ResponseEntity<User>(user,HttpStatus.OK);
    			}
    			catch(Exception e)
    			{
    				Errorclass ec=new Errorclass(35,"Unable to update details due to some error");
        			return new ResponseEntity<Errorclass>(ec,HttpStatus.INTERNAL_SERVER_ERROR);	
    			}
    	}
    	}
    	
    	
        @RequestMapping(value="/Updateprofilepicture",method=RequestMethod.POST)
    	public void UpdateProfilePicture(@RequestParam MultipartFile Profilepicture,HttpSession session,HttpServletResponse response)
    	{
        	try{
    		System.out.println("In UserController UpdateProfilePicture function Invoked");
    		String email=(String)session.getAttribute("email");
    		if(email==null)
    		{   
    			System.out.println("Email is null");
    			Errorclass ec=new Errorclass(28,"Please Login Again");
    		}
    		
    			User user=userdao.GetUser(email);
    			user.setProfilepicture(Profilepicture.getBytes());
    			System.err.println(Profilepicture.getOriginalFilename());
    			System.err.println(Profilepicture.getSize());
    			userdao.UpdateProfileDetails(user);
    			System.err.println("File successfully uploaded");

    
    		}
          catch(Exception e)
           {
        	System.err.println("Exception occured could not update profile picture");
           }
    	}
    	
        
	    @RequestMapping(value="/Logout",method=RequestMethod.PUT)
    	public ResponseEntity<?>Logout(HttpSession session)
    	{
    	System.out.println("In UserController Logout function Invoked");
    	String email=(String)session.getAttribute("email");	
    	if(email!=null)
    	{
    		System.err.println("Logout user--->"+email);
    		System.out.println("sessionId--->"+session.getId());
    		User user=userdao.GetUser(email);
    		user.setOnline(false);
    		userdao.UpdateUserOnlineStatus(user);
    		session.removeAttribute("email");
    		session.invalidate();
    		return new ResponseEntity<Void>(HttpStatus.OK);
    	}
    	else
    	{
    	Errorclass ec=new Errorclass(17,"Please Login");
    	return new ResponseEntity<Errorclass>(ec,HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
    	}
    	}
    	
    	
    	@RequestMapping(value="/Getprofilepicture/{email:.+}",method=RequestMethod.GET)
    	public byte[] GetProfileImage(@PathVariable String email,HttpSession session)
    	{   
    	    byte[] profilepic=userdao.GetProfilepicture(email);
    		return profilepic;
    
    	}
    	
    	
    	@RequestMapping(value="/Getallusers",method=RequestMethod.GET)
       public ResponseEntity<?>GetAllUsers()
	   {
	    System.out.println("In UserController GetAllUsers function Invoked");
	    System.out.println("Getting all users list");
	    List<User>lou=userdao.GetAllUsers();	
	    
	    if(lou.isEmpty())
	    {
	    Errorclass ec=new Errorclass(15,"List is empty");
	    return new ResponseEntity<Errorclass>(ec,HttpStatus.NO_CONTENT)	;
	    }
	    return new ResponseEntity<List<User>>(lou,HttpStatus.OK);
	   }
    }
