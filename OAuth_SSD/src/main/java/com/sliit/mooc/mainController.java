package com.sliit.mooc;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.auth.oauth2.Credential;

@Controller
public class mainController {

	@Autowired
	OAuthService OAuthService;

	@Autowired
	OAuthDriverService OAuthDriverImplementaion;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String mainPage() throws IOException {

		if (OAuthService.userstatus().equals("SUCCESS")) {
			return "redirect:/main";

		} else {
			return "index.html";
		}

	}

	@RequestMapping(value = "/main", method = RequestMethod.GET)
	public String dashboard() throws IOException {

		if (OAuthService.userstatus().equals("SUCCESS")) {
			return "redirect:/main";

		} else {
			return "index.html";
		}
	}

	@RequestMapping(value = "/Userlogin", method = RequestMethod.GET)
	public void googleLogin(HttpServletResponse response) throws IOException {
		response.sendRedirect(OAuthService.authenticateUser());
	}

	@RequestMapping(value = "/Userlogout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request) throws IOException {
		OAuthService.discardUserSession(request);
		return "redirect:/";
	}

	@RequestMapping(value = "/UserFileupload", method = RequestMethod.POST)
	public String upload(HttpServletRequest request, @ModelAttribute File file) throws Exception {

		MultipartFile multipartFile = file.getMultipartFile();
		OAuthDriverImplementaion.uploadFile(multipartFile);
		return "redirect:/main?status=uploaded";
	}

	@RequestMapping(value = "/redirect", method = RequestMethod.GET)
	public String authorizationCode(@RequestParam(name = "code") String code) throws IOException {

		if (code != null) {
			OAuthService.tokenExchange(code);
			return "main.html";
		} else {
			return "index.html";
		}

	}

}
