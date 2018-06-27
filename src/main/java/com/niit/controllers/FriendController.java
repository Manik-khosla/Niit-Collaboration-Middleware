package com.niit.controllers;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.niit.Dao.FriendsDao;
import com.niit.Dao.UserDao;
import com.niit.model.Errorclass;
import com.niit.model.Friends;
import com.niit.model.User;

@RestController
public class FriendController {
	@Autowired
	private FriendsDao friendsdao;
	@Autowired
	private UserDao userdao;
	
	public FriendController()
	 {
		System.out.println("FriendController class  Instantiated");
	 }
	
	@RequestMapping(value="/Getsuggestedusers",method=RequestMethod.GET)
	public ResponseEntity<?> GetSuggestedUsers(HttpSession session)
	{
		System.out.println("In FriendController GetSuggestedUsers function Invoked");
		if(session.getAttribute("email")==null)
		{
			Errorclass ec=new Errorclass(75,"Please Login");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
		}
		else
		{   
			String email=(String) session.getAttribute("email");
			List<User>suggestedusers=friendsdao.GetSuggestedUsers(email);
			for(User user: suggestedusers)
			{
			user.setProfilepicture(null);	
			}
			return new ResponseEntity<List<User>>(suggestedusers,HttpStatus.OK);
			
		}
	}
	
	
	@RequestMapping(value="/Sendfriendrequest",method=RequestMethod.POST)
	public ResponseEntity<?> SendFriendRequest(@RequestBody User UserTo,HttpSession session)
	{
		if(session.getAttribute("email")==null)
		{
			Errorclass ec=new Errorclass(75,"Please Login");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
		}
		else
		{   
			String email=(String) session.getAttribute("email");
			Friends friend=new Friends();
			friend.setFrom_Id(userdao.GetUser(email));
			friend.setTo_Id(UserTo);
			friend.setStatus('P');
			friendsdao.SendFriendRequest(friend);
			return new ResponseEntity<Void>(HttpStatus.OK);
			
		}
	}
	
	
	@RequestMapping(value="/Getpendingrequest",method=RequestMethod.GET)
	public ResponseEntity<?>GetPendingRequests(HttpSession session)
	{
		if(session.getAttribute("email")==null)
		{
			Errorclass ec=new Errorclass(75,"Please Login");
			return new ResponseEntity<Errorclass>(ec,HttpStatus.UNAUTHORIZED);
		}
		else
		{
			String email=(String) session.getAttribute("email");
			List<Friends>Pendingrequests=friendsdao.GetPendingRequests(email);
			return new ResponseEntity<List<Friends>>(Pendingrequests,HttpStatus.OK);
		}
	}

}
