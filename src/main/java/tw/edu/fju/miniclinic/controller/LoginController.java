package tw.edu.fju.miniclinic.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.LoginForm;

@Controller
public class LoginController {

    public static final String SESSION_LOGGED_IN_DOCTOR_ID = "loggedInDoctorId";
    public static final String SESSION_LOGGED_IN_DOCTOR_NAME = "loggedInDoctorName";

    @Autowired
    private DoctorRepository doctorRepo;

    @GetMapping("/login")
    public String loginForm(Model model) {
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("loginForm") LoginForm form,
            BindingResult result,
            HttpSession session,
            Model model) {

        if (result.hasErrors()) {
            return "login";
        }

        Doctor doctor = doctorRepo.findById(form.getDoctorId()).orElse(null);
        if (doctor == null || doctor.getPasswordHash() == null || !BCrypt.checkpw(form.getPassword(), doctor.getPasswordHash())) {
            model.addAttribute("errorMessage", "醫師編號或密碼錯誤");
            return "login";
        }

        session.setAttribute(SESSION_LOGGED_IN_DOCTOR_ID, doctor.getDoctorId());
        session.setAttribute(SESSION_LOGGED_IN_DOCTOR_NAME, doctor.getName());

        return "redirect:/dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
