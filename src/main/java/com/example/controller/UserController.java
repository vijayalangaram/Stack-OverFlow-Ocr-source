package com.example.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Optional;
import java.util.concurrent.Executor;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.domain.User;
import com.example.repository.UserRepository;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

@RestController

//  sync
@Configuration
@EnableAsync

public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	 @Bean(name = "threadPoolTaskExecutor")
	    public Executor threadPoolTaskExecutor() {
	        return new ThreadPoolTaskExecutor();
	    }
	 

	@PostMapping("/users")
	User createUser(@RequestParam String name, @RequestParam MultipartFile file) throws IOException {
		// code from userService
		User user = new User();
		user.setName(name);
		user.setImage(new Binary(file.getBytes()));
		
		return userRepository.save(user);
	}

	@GetMapping("/users")
	String getImage(@RequestParam String id) {
		// code from userService
		Optional<User> user = userRepository.findById(id);
		Encoder encoder = Base64.getEncoder();
		
		return encoder.encodeToString(user.get().getImage().getData());
	}
	
	
	
	
//	// simple ocr mapping   (result error only)
//	@GetMapping("/imageocr")
//	String getImageText(@RequestParam String id) {
//	    Optional<User> user = userRepository.findById(id);
//	    ITesseract instance = new Tesseract();
//	    try {
//	        ByteArrayInputStream bais = new ByteArrayInputStream(user.get().getImage().getData());
//	        BufferedImage bufferImg = ImageIO.read(bais);
//	        String imgText = instance.doOCR(bufferImg);
//	        return imgText;
//	    } catch (Exception e) {
//	        return "Error while reading image";
//	    }
//	}
	
	
	
	// simple ocr mapping with asyn   result Warning: Invalid resolution 0 dpi. Using 70 instead.
//	@Async("threadPoolTaskExecutor")	
//	@GetMapping("/imageocr")	
//	public String getImageText(@RequestParam String id) {		
//		System.out.println("Execute method" + Thread.currentThread().getId());		
//		    Optional<User> user = userRepository.findById(id);
//		    ITesseract instance = new Tesseract();		    
//		    instance.setDatapath("C:\\Users\\Administrator\\Desktop\\tessdata");		    
//		    try {
//		        ByteArrayInputStream bais = new ByteArrayInputStream(user.get().getImage().getData());
//		        BufferedImage bufferImg = ImageIO.read(bais);
//		        String imgText = instance.doOCR(bufferImg);
//		        return imgText;
//		    } catch (Exception e) {
//		        return "Error while reading image";
//		    }		}
	
	
	
	
	// async with buffered data lie weight heigh	
		@Async("threadPoolTaskExecutor")	
		@GetMapping("/imageocr")	
		public String asyncMethodWithConfiguredExecutor(@RequestParam String id, ImageIcon imageIcon , Integer width , Integer height) 
		{ 			System.out.println("Execute method" + Thread.currentThread().getId());		
		    Optional<User> user = userRepository.findById(id);	    
		    ITesseract instance = new Tesseract();
		    	    //	mine
		    instance.setDatapath("C:\\Users\\Administrator\\Desktop\\tessdata");	    
		    try 
		    {	    	
		   	BufferedImage bufferedImage = new BufferedImage( width , height , BufferedImage.TRANSLUCENT );	 
		  	 Graphics2D graphics2D = bufferedImage.createGraphics();	  	 
		    graphics2D.drawImage( imageIcon.getImage() , 0 , 0 , width , height , null );	    
		    graphics2D.dispose();
		        ByteArrayInputStream bais = new ByteArrayInputStream(user.get().getImage().getData());
		        BufferedImage bufferImg = ImageIO.read(bais);
		        String imgText = instance.doOCR(bufferImg);	        
		        return imgText;
		        	    }
		    catch (Exception e) 
		    {	    	
		        return "Error while reading image";
		    }	}	

	
}
