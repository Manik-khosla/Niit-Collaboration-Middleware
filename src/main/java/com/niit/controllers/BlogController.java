package com.niit.controllers;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.niit.Dao.BlogDao;
import com.niit.Dao.UserDao;
import com.niit.model.Blogs;
import com.niit.model.Errorclass;
import com.niit.model.User;

@RestController
public class BlogController {
     @Autowired
	private BlogDao blogdao;
    @Autowired
    private UserDao userdao;
	
     public BlogController()
	 {
		System.out.println("BlogController class  Instantiated");
	 }
     
     @RequestMapping(value="/Postblog",method=RequestMethod.POST)
     public ResponseEntity<?> SaveBlog(@RequestBody Blogs blog,HttpSession ssn)
     {
    	if(ssn.getAttribute("email")==null)
    	{
    		Errorclass ec=new Errorclass(24,"Please Login");
    		return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
    	}
    	String email=(String) ssn.getAttribute("email");
    	try{
    	User userpostedblog=userdao.Getuser(email);
    	blog.setPostedBy(userpostedblog);
    	blog.setPostedOn(new Date());
    	blog.setApproved(false);
    	blog.setLikes(0);
    	blogdao.SaveBlog(blog);
    	return new ResponseEntity<Void>(HttpStatus.OK);
    	}
    	catch(Exception e)
    	{
    		Errorclass ec=new Errorclass(16,"Unable to post blog due to some error");
    		return new ResponseEntity<Errorclass>(ec,HttpStatus.INTERNAL_SERVER_ERROR); 
    	}
     }
     
     
     @RequestMapping(value="/Getapprovedblogs",method=RequestMethod.GET)
     public ResponseEntity<?> GetApprovedBlogslist(HttpSession session)
     {
    	 if(session.getAttribute("email")==null)
     	{
     		Errorclass ec=new Errorclass(07,"Please Login");
     		return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
     	} 
    	 try{
    	 List<Blogs>Approved_Blogs=blogdao.Getapprovedblogs();
    	 return new ResponseEntity<List<Blogs>>(Approved_Blogs,HttpStatus.OK);
    	 }
    	 catch(Exception e)
    	 {
    		 Errorclass ec=new Errorclass(24,"Unable to get blogs due to some error");
     		return new ResponseEntity<Errorclass>(ec,HttpStatus.INTERNAL_SERVER_ERROR);
    	 }
     }
     
     
     @RequestMapping(value="/Getblogswaitingapproval",method=RequestMethod.GET)
     public ResponseEntity<?>GetBlogsWaitingApproval(HttpSession session)
     {
    	 if(session.getAttribute("email")==null)
      	{
      		Errorclass ec=new Errorclass(101,"Please Login");
      		return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
      	} 
    	 String email=(String)session.getAttribute("email");
    	 try{
 		  User user=userdao.Getuser(email);
 		  if(user.getRole()=="ADMIN")
 		  {
 			List<Blogs>Blogs_waiting_approval=blogdao.Getblogswaitingapproval();  
 			return new ResponseEntity<List<Blogs>>(Blogs_waiting_approval,HttpStatus.OK);
 		  }
 		  else
 		  {
 			 Errorclass ec=new Errorclass(110,"Access Denied");
       		return new ResponseEntity<Errorclass>(HttpStatus.UNAUTHORIZED);  
 		  } 
    	 }
    	 catch(Exception e)
    	 {
    		Errorclass ec=new Errorclass(37,"Unable to get blogs due to some error");
     		return new ResponseEntity<Errorclass>(ec,HttpStatus.INTERNAL_SERVER_ERROR);
    	 }
    	 
     }
     
     
     @RequestMapping(value="Getblog/{id}",method=RequestMethod.GET)
     public ResponseEntity<?> Getblog(@PathVariable int id,HttpSession session)
     {
    	 if(session.getAttribute("email")==null)
       	{
       		Errorclass ec=new Errorclass(75,"Please Login");
       		return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
       	}  
    	 else
    	 {
    		 Blogs blog=blogdao.Getblog(id);
    		 return new ResponseEntity<Blogs>(blog,HttpStatus.OK);
    	 }
     }

}
