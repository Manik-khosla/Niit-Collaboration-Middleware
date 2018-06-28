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
import com.niit.model.Jobs;
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
	public ResponseEntity<?> AddJob(@RequestBody Jobs job,HttpSession session)
	{   
		System.out.println("In JobController AddJob function Invoked");
		if(session.getAttribute("email")==null)
		{
			Errorclass ec=new Errorclass(15,"Please Login");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
		}
		String email=(String) session.getAttribute("email");
		User user=userdao.GetUser(email);
		if(user.getRole().equals("ADMIN"))
		{   
			try{
			job.setPostedOn(new Date());
			job.setActive(true);
			jobdao.AddJob(job);
			return new  ResponseEntity<Void>(HttpStatus.OK);
			}
			catch(Exception e)
			{
				Errorclass ec=new Errorclass(7,"Unable to Add a new job");
				return new ResponseEntity<Errorclass>(ec,HttpStatus.INTERNAL_SERVER_ERROR);	
			}
		}
		else
		{   
			Errorclass ec=new Errorclass(23,"Access Denied");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
		}
	}
	
	
	@RequestMapping(value="/Updatejob",method=RequestMethod.PUT)
	public ResponseEntity<?>UpdateJob(@RequestBody Jobs job,HttpSession session)
	{   
		System.out.println("In JobController UpdateJob function Invoked");
		if(session.getAttribute("email")==null){
    		Errorclass ec=new Errorclass(2,"Please Login");
    		return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
    	}
		String email=(String) session.getAttribute("email");
		User user=userdao.GetUser(email);
		if(user.getRole().equals("ADMIN"))
		{  
			try{
			jobdao.UpdateJob(job);
			return new ResponseEntity<Void>(HttpStatus.OK);
			}
			catch(Exception e)
			{
				Errorclass ec=new Errorclass(18,"Unable to Update Job");
				return new ResponseEntity<Errorclass>(ec,HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		else
		{
			Errorclass ec=new Errorclass(11,"Access Denied Please Login");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
		}
		
	}
	
	
	@RequestMapping(value="/Getactivejobs",method=RequestMethod.GET)
	public ResponseEntity<?>GetActiveJobs(HttpSession session)
	{   
		System.out.println("In JobController GetActiveJobs function Invoked");
		if(session.getAttribute("email")==null)
		{
			Errorclass ec=new Errorclass(1,"Please Login");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
		}
		try
		{
		List<Jobs>Activejobs=jobdao.GetActiveJobs();
		return new ResponseEntity<List<Jobs>> (Activejobs,HttpStatus.OK);
		}
		catch(Exception e)
		{
			Errorclass ec=new Errorclass(3,"Unable to get active jobs");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
	}
	
	
	@RequestMapping(value="/Getinactivejobs",method=RequestMethod.GET)
	public ResponseEntity<?>GetInActiveJobs(HttpSession session)
	{   
		System.out.println("In JobController GetInActiveJobs function Invoked");
		if(session.getAttribute("email")==null)
		{
			Errorclass ec=new Errorclass(9,"Please Login");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
		}
		String email=(String)session.getAttribute("email");
		User user=userdao.GetUser(email);
		
		if(user.getRole().equals("ADMIN"))
		{
		try
		{
		List<Jobs>Inactivejobs=jobdao.GetInActiveJobs();	
		return new ResponseEntity<List<Jobs>>(Inactivejobs,HttpStatus.OK);
		}
		catch(Exception e)
		{   
			Errorclass ec=new Errorclass(27,"Unable to get Inactive Jobs");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		}
		else
		{
			Errorclass ec=new Errorclass(26,"Access Denied");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
		}
		
	}
	
	
	

}
