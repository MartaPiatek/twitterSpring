package pl.martapiatek.profile;

import java.io.*;
import java.net.URLConnection;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import pl.martapiatek.config.PictureUploadProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@SessionAttributes("picturePath")
@Controller
public class PictureUploadController {

	private final Resource picturesDir;
	private final Resource anonymousPicture;
	
	
	@Autowired
	public PictureUploadController(PictureUploadProperties uploadProperties) {
		picturesDir = uploadProperties.getUploadPath();
		anonymousPicture = uploadProperties.getAnonymousPicture();
	}
	
	public static final Resource PICTURES_DIR = new FileSystemResource("./pictures");
	
	
	@ModelAttribute
	public Resource picturePath() {
		return anonymousPicture;
	}
	
	@RequestMapping("upload")
	public String uploadPage() {
		return "profile/uploadPage";
	}
	
	@RequestMapping(value="/upload", method=RequestMethod.POST)
	public String onUpload(MultipartFile file, RedirectAttributes redirectAttrs,
			Model model) throws IOException{
		
		if(file.isEmpty() || !isImage(file)) {
			redirectAttrs.addFlashAttribute("error","Niew�a�ciwy plik. Za�aduj plik z obrazem.");
			return "redirect:/upload";
		}
		Resource picturePath = copyFileToPictures(file);
		model.addAttribute("picturePath", picturePath);
		return "profile/uploadPage";

		//	throw new IOException("komnunikat testowy");
	}
	
	
	@RequestMapping(value = "/uploadedPicture")
	public void getUploadedPicture(HttpServletResponse response
			//, @ModelAttribute("picturePath") Path picturePath
			) 
					throws IOException {
		
		//response.setHeader("Content-Type", URLConnection.guessContentTypeFromName(picturePath.toString()));
		//Files.copy(picturePath,response.getOutputStream());
		response.setHeader("Content-Type", URLConnection.guessContentTypeFromName(anonymousPicture.getFilename()));
		IOUtils.copy(anonymousPicture.getInputStream(),response.getOutputStream());
	}
	
	@RequestMapping("uploadError")
	public ModelAndView onUploadError(HttpServletRequest request) {
		
		ModelAndView modelAndView = new ModelAndView("profile/uploadPage");
		
		modelAndView.addObject("error",request.getAttribute(WebUtils.ERROR_MESSAGE_ATTRIBUTE));
		
		return modelAndView;
	}
	
	@ExceptionHandler(IOException.class)
	public ModelAndView handleIOException(IOException exception) {
		ModelAndView modelAndView = new ModelAndView("profile/uploadPage");
		modelAndView.addObject("error", exception.getMessage());
		return modelAndView;
	}
	
	private FileSystemResource copyFileToPictures(MultipartFile file) throws IOException{
		String fileExtension = getFileExtension(file.getOriginalFilename());
		File tempFile = File.createTempFile("pic", fileExtension, picturesDir.getFile());
		
		try(InputStream in = file.getInputStream();
				OutputStream out = new FileOutputStream(tempFile)){
			IOUtils.copy(in, out);
		}
		
		return new FileSystemResource(tempFile);
		
	}

	private boolean isImage(MultipartFile file) {
		
		return file.getContentType().startsWith("image");
	}

	private static String getFileExtension(String name) {
		
		return name.substring(name.lastIndexOf("."));
		
	}
	
	
	
	
}
