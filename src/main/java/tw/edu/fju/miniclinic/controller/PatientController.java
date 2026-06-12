package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import tw.edu.fju.miniclinic.model.Patient;
import tw.edu.fju.miniclinic.model.PatientRepository;

import java.util.List;
import java.util.Optional;

@Controller
public class PatientController {

    @Autowired
    private PatientRepository patientRepo;

    @GetMapping("/patients")
    public String listPatients(Model model) {
        model.addAttribute("patients", patientRepo.findAll());
        return "patients";
    }

    @GetMapping("/api/patients")
    @ResponseBody
    public List<Patient> getPatients() {
        return patientRepo.findAll();
    }

    @GetMapping("/api/patients/{chartNo}")
    @ResponseBody
    public ResponseEntity<Patient> getPatient(@PathVariable String chartNo) {
        Optional<Patient> patient = patientRepo.findById(chartNo);
        return patient
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}