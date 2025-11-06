package com.example.springwebauthn4j.controller;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.springwebauthn4j.util.SecurityContextUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
	
	@GetMapping("/")
	public String root() {
		return "redirect:mypage";
	}
	
	@GetMapping("login")
	public String login(
			@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout,
			Model model,
			HttpSession session
	) {
		model.addAttribute("showErrorMsg", false);
		model.addAttribute("showLogoutedMsg", false);
		
		if (error != null) {
			AuthenticationException ex = (AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
			if (ex != null) {
				model.addAttribute("showErrorMsg", true);
				model.addAttribute("errorMsg", ex.getMessage());
			}
		} else if (logout != null) {
			model.addAttribute("showLogoutedMsg", true);
			model.addAttribute("logoutedMsg", "Logouted");
		}
		
		return "login";
	}
	
	@GetMapping("password")
	public String loginPassword(Model model) {
		var user = SecurityContextUtil.getLoginUser();
		model.addAttribute("username", user != null ? user.getUsername() : null);
		return "password";
	}
	
	@GetMapping("mypage")
	public String mypage(HttpServletRequest request, Model model) {
		var user = SecurityContextUtil.getLoginUser();
		model.addAttribute("username", user != null ? user.getUsername() : null);
		return "mypage";
	}
}