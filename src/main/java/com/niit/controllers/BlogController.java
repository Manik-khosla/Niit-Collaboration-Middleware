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
     public ResponseEntity<?> PostBlog(@RequestBody Blogs blog,HttpSession ssn)
     {
    	System.out.println("In BlogController PostBlog function Invoked");
    	if(ssn.getAttribute("email")==null)
    	{
    		Errorclass ec=new Errorclass(24,"Please Login");
    		return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
    	}
    	String email=(String) ssn.getAttribute("email");
    	try{
    	User userpostedblog=userdao.GetUser(email);
    	blog.setPostedBy(userpostedblog);
    	blog.setPostedOn(new Date());
    	blog.setApproved(false);
    	blog.setLikes(0);
    	blogdao.PostBlog(blog);
    	return new ResponseEntity<Void>(HttpStatus.OK);
    	}
    	catch(Exception e)
    	{
    		Errorclass ec=new Errorclass(16,"Unable to post blog due to some error");
    		return new ResponseEntity<Errorclass>(ec,HttpStatus.INTERNAL_SERVER_ERROR); 
    	}
     }
     
     
     @RequestMapping(value="/Getapprovedblogs",method=RequestMethod.GET)
     public ResponseEntity<?> GetApprovedBlogsList(HttpSession session)
     {   
    	System.out.println("In BlogController GetApprovedBlogslist function Invoked");
    	 if(session.getAttribute("email")==null)
     	{
     		Errorclass ec=new Errorclass(07,"Please Login");
     		return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
     	} 
    	 try{
    	 List<Blogs>Approved_Blogs=blogdao.GetApprovedBlogs();
    	 return new ResponseEntity<List<Blogs>>(Approved_Blogs,HttpStatus.OK);
    	 }
    	 catch(Exception e)
    	 {
    		 Errorclass ec=new Errorclass(24,"Unable to get blogs due to some error");
     		return new ResponseEntity<Errorclass>(ec,HttpStatus.INTERNAL_SERVER_ERROR);
    	 }
     }
     
     
     @RequestMapping(value="/Getblogswaitingapproval",method=RequestMethod.GET)
     public ResponseEntity<?>GetBlogsWaitingApprovalList(HttpSession session)
     {   
    	 System.out.println("In BlogController GetBlogsWaitingApproval function Invoked");
    	 if(session.getAttribute("email")==null)
      	{
      		Errorclass ec=new Errorclass(101,"Please Login");
      		return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
      	} 
    	 String email=(String)session.getAttribute("email");
    	 try{
 		  User user=userdao.GetUser(email);
 		  if(user.getRole()=="ADMIN")
 		  {
 			List<Blogs>Blogs_waiting_approval=blogdao.GetBlogsWaitingApproval();  
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
     
     
     
     @RequestMapping(value="/Approveblog/{id}",method=RequestMethod.GET)
     public ResponseEntity<?> UpdateBlog(@PathVariable int id,HttpSession session)
     {
    	System.out.println("In BlogController UpdateBlog function Invoked"); 
    	if(session.getAttribute("email")==null)
       	{
       		Errorclass ec=new Errorclass(75,"Please Login");
       		return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
       	} 
    	String email=(String)session.getAttribute("email");
    	try
    	{
    		User user=userdao.GetUser(email);
    		if(user.getRole()=="ADMIN")
    		{   
    			Blogs blog=blogdao.GetBlog(id);
    			blog.setApproved(true);
    			blogdao.UpdateBlogPost(blog);
    			return new ResponseEntity<Void>(HttpStatus.OK);
    		}
    		else
    		{
    			Errorclass ec=new Errorclass(75,"Access Denied");
           		return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);	
    		}
    	}
    	catch(Exception e)
    	{
    		Errorclass ec=new Errorclass(75,"Could not update blog due to some error");
       		return new ResponseEntity<Errorclass>(ec,HttpStatus.INTERNAL_SERVER_ERROR);		
    	}
     }
     
     
     @RequestMapping(value="Getblog/{id}",method=RequestMethod.GET)
     public ResponseEntity<?> GetBlog(@PathVariable int id,HttpSession session)
     {   
    	 System.out.println("In BlogController GetBlogUsingId function Invoked");
    	 if(session.getAttribute("email")==null)
       	{
       		Errorclass ec=new Errorclass(75,"Please Login");
       		return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
       	}  
    	 else
    	 {
    		 Blogs blog=blogdao.GetBlog(id);
    		 return new ResponseEntity<Blogs>(blog,HttpStatus.OK);
    	 }
     }

}
