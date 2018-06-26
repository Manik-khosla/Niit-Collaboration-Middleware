package com.niit.controllers;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.niit.Dao.BlogCommentsDao;
import com.niit.Dao.BlogDao;
import com.niit.Dao.UserDao;
import com.niit.model.BlogComments;
import com.niit.model.Blogs;
import com.niit.model.Errorclass;
import com.niit.model.User;

@RestController
public class BlogCommentController {
	@Autowired
	private BlogCommentsDao blogcommentdao;
	@Autowired
	private BlogDao blogdao;
	@Autowired
	private UserDao userdao;
     
	public BlogCommentController()
	 {
		System.out.println("BlogCommentController class  Instantiated");
	 }
	
	@RequestMapping(value="/Postcomment/{commenttxt}/{id}" ,method=RequestMethod.GET)
	public ResponseEntity<?> PostComment(@PathVariable String commenttxt,@PathVariable int id,HttpSession session)
	{
	System.out.println("In BlogCommentController PostComment function invoked");
	if(session.getAttribute("email")==null)
	{
		Errorclass ec=new Errorclass(75,"Please Login");
		return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
	}
	else
	{
	    try{
		String email=(String) session.getAttribute("email");
		BlogComments blogcomment=new BlogComments();
		Blogs blog=blogdao.GetBlog(id);
		blogcomment.setBlogPost(blog);
		User user=userdao.GetUser(email);
		blogcomment.setCommentedBy(user);
		blogcomment.setCommentedOn(new Date());
		blogcomment.setCommentTxt(commenttxt);
		blogcommentdao.PostBlogComment(blogcomment);
		return new ResponseEntity<BlogComments>(blogcomment,HttpStatus.OK);}
	    catch(Exception e )
	    {
	    	Errorclass ec=new Errorclass(75,"Could not get blog comments due to some error");
	    	return new ResponseEntity<Errorclass>(ec,HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	}
	
	
	@RequestMapping(value="/Getblogcomments/{id}",method=RequestMethod.GET)
	public ResponseEntity<?> GetBlogComments(@PathVariable int id,HttpSession session)
	{
		if(session.getAttribute("email")==null)
		{
			Errorclass ec=new Errorclass(75,"Please Login");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
		}
		List<BlogComments> blogcomments=blogcommentdao.GetBlogComments(id);
		return new ResponseEntity<List<BlogComments>>(blogcomments,HttpStatus.OK);
	}
	
	
	@RequestMapping(value="/Getallblogcomments",method=RequestMethod.GET)
	public ResponseEntity<?> GetBlogComments(HttpSession session)
	{
		if(session.getAttribute("email")==null)
		{
			Errorclass ec=new Errorclass(75,"Please Login");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
		}
		List<BlogComments> allblogcomments=blogcommentdao.GetAllBlogComments();
		return new ResponseEntity<List<BlogComments>>(allblogcomments,HttpStatus.OK);
	}
}
