//Messagee.java - Global class for creating a single message controller for
//views to use for communications. 

package com.apigee.messagee;


import android.app.Application;

import com.apigee.controller.MessageController;

public class Messagee extends Application{

	public MessageController messController = new MessageController();
	
	
}
