package com.niit.controllers;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.niit.Dao.JobDao;
import com.niit.Dao.UserDao;
import com.niit.model.Errorclass;
import com.niit.model.Job;
import com.niit.model.User;

@RestController
public class JobController {
	@Autowired
	UserDao userdao;
	@Autowired
	private JobDao jobdao;
	
	 public JobController()
	 {
		System.out.println("JobController class  Instantiated");
	 }
	
	@RequestMapping(value="/Addjob",method=RequestMethod.POST)
	public ResponseEntity<?> Addjob(@RequestBody Job job,HttpSession session)
	{   
		System.out.println("In JobController Addjob function Invoked");
		if(session.getAttribute("email")==null)
		{
			Errorclass ec=new Errorclass(19,"Please Login");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
		}
		String email=(String) session.getAttribute("email");
		User user=userdao.Getuser(email);
		if(user.getRole().equals("ADMIN"))
		{   
			try{
			job.setPostedOn(new Date());
			job.setActive(true);
			jobdao.Addjob(job);
			return new  ResponseEntity<Void>(HttpStatus.OK);
			}
			catch(Exception e)
			{
				Errorclass ec=new Errorclass(18,"Unable to Add a new job");
				return new ResponseEntity<Errorclass>(ec,HttpStatus.INTERNAL_SERVER_ERROR);	
			}
		}
		else
		{   
			Errorclass ec=new Errorclass(01,"Access Denied");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
		}
	}
	
	
	@RequestMapping(value="/Updatejob",method=RequestMethod.PUT)
	public ResponseEntity<?>Updatejob(@RequestBody Job job,HttpSession session)
	{   
		System.out.println("In JobController Update job function Invoked");
		if(session.getAttribute("email")==null){
    		Errorclass ec=new Errorclass(20,"Please Login");
    		return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
    	}
		String email=(String) session.getAttribute("email");
		User user=userdao.Getuser(email);
		if(user.getRole().equals("ADMIN"))
		{  
			try{
			jobdao.Updatejob(job);
			return new ResponseEntity<Void>(HttpStatus.OK);
			}
			catch(Exception e)
			{
				Errorclass ec=new Errorclass(48,"Unable to Update Job");
				return new ResponseEntity<Errorclass>(ec,HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		else
		{
			Errorclass ec=new Errorclass(45,"Access Denied Please Login");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
		}
		
	}
	
	
	@RequestMapping(value="/Getactivejobs",method=RequestMethod.GET)
	public ResponseEntity<?>GetActiveJobs(HttpSession session)
	{   
		System.out.println("In JobController Getactivejobs function Invoked");
		if(session.getAttribute("email")==null)
		{
			Errorclass ec=new Errorclass(15,"Please Login");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
		}
		try
		{
		List<Job>Activejobs=jobdao.Getactivejobs();
		return new ResponseEntity<List<Job>> (Activejobs,HttpStatus.OK);
		}
		catch(Exception e)
		{
			Errorclass ec=new Errorclass(07,"Unable to get active jobs");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
	}
	
	
	@RequestMapping(value="/GetInactivejobs",method=RequestMethod.GET)
	public ResponseEntity<?>GetInActiveJobs(HttpSession session)
	{   
		System.out.println("In JobController Getinactivejobs function Invoked");
		if(session.getAttribute("email")==null)
		{
			Errorclass ec=new Errorclass(23,"Please Login");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
		}
		String email=(String)session.getAttribute("email");
		User user=userdao.Getuser(email);
		
		if(user.getRole().equals("ADMIN"))
		{
		try
		{
		List<Job>Inactivejobs=jobdao.Getinactivejobs();	
		return new ResponseEntity<List<Job>>(Inactivejobs,HttpStatus.OK);
		}
		catch(Exception e)
		{   
			Errorclass ec=new Errorclass(03,"Unable to get Inactive Jobs");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		}
		else
		{
			Errorclass ec=new Errorclass(9,"Access Denied");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
		}
		
	}
	
	
	

}
