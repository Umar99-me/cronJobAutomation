package com.ibm.cron.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ibm.cron.model.CronModel;
import com.ibm.cron.service.CronService;
import com.ibm.cron.service.ObjectStorageService;

@Controller
public class HomeController {
	
	@Autowired
	CronService cronService;
	
	@Autowired
	ObjectStorageService obj;

	@GetMapping("/")
	public String viewHomePage(Model model) {
		model.addAttribute("home", "Hello World, Welcome the Cron Home!!");
		return "login";
	}
	
	@GetMapping("/home")
	public String viewCronDetails(Model model) {
		CronModel cronModel=new CronModel();
		List<CronModel> crons = cronService.getCronsByUser("shaik.umar.saleem@ibm.com");
		model.addAttribute("cronModel",cronModel);
		model.addAttribute("crons",crons);
		return "home";
	}
	
	@PostMapping("/registeration")
	private String saveCron(@ModelAttribute("cronModel") CronModel cron) {
		cron.setUserID("shaik.umar.saleem@ibm.com");
		cronService.SaveOrUpdate(cron);
		//window.location.href = "/home";
		return "redirect:/home";
	}
	
	@GetMapping("/getDetails")
	public String getCronDetails(Model model,@RequestParam(value = "id") int id) throws FileNotFoundException {
		CronModel cron = cronService.getCron(id);
		String fileName=cronService.getFileNameByID(id)+".txt";
		model.addAttribute("serverid",cron.getServerID());
		model.addAttribute("cronid",cron.getCronID());
		model.addAttribute("cronDetails", obj.getObjects(obj.createClient(), fileName));
		
		model.addAttribute("id",fileName);
		return "cronDetails";
	}

	@GetMapping("/download")
	public ResponseEntity<byte[]> downloadAgent(Model model,@RequestParam(value = "id") int id) throws IOException {
		String filename = cronService.getFileNameByID(id)+".txt";
		Path filePath = Paths.get("src\\main\\resources\\static\\final.sh");
        // Read Bytes
        byte[] data = Files.readAllBytes(filePath);

		return ResponseEntity.ok()
				.contentType(MediaType.TEXT_HTML)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
				.body(data);
	}
	
}

