package pl.martapiatek.profile;

import java.util.Locale;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pl.martapiatek.date.USLocalDateFormatter;

@Controller
public class ProfileController {

	@RequestMapping("/profile")
	public String displayProfile(ProfileForm profileForm) {
		return "profile/profilePage";
	}
	
	@RequestMapping(value="/profile", method=RequestMethod.POST)
	public String saveProfile(@Valid ProfileForm profileForm, BindingResult bindingResult) {
		
		if(bindingResult.hasErrors()) {
			return "profile/profilePage";
		}
		
		System.out.println("pomyœlnie zapisany profil "+ profileForm);
		return "redirect:/profile";
	}
	
	@ModelAttribute("dateFormat")
	public String localeFormat(Locale locale) {
		return USLocalDateFormatter.getPattern(locale);
	}
	
	
}
