package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.PatientRepository;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.stream.Collectors;

@RestController
class StatsApiController {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    @GetMapping("/api/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        long totalDoctors = doctorRepo.count();
        long totalPatients = patientRepo.count();
        long totalAppointments = appointmentRepo.count();

        long booked = appointmentRepo.countByStatus("BOOKED");
        long completed = appointmentRepo.countByStatus("COMPLETED");
        long cancelled = appointmentRepo.countByStatus("CANCELLED");

        Map<String, Object> byStatus = new LinkedHashMap<>();
        byStatus.put("BOOKED", booked);
        byStatus.put("COMPLETED", completed);
        byStatus.put("CANCELLED", cancelled);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalDoctors", totalDoctors);
        result.put("totalPatients", totalPatients);
        result.put("totalAppointments", totalAppointments);
        result.put("byStatus", byStatus);

        return ResponseEntity.ok(result);
    }
}

@Controller
class StatsController {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    @GetMapping("/stats")
    public String stats(Model model) {
        model.addAttribute("doctorCount", doctorRepo.count());
        model.addAttribute("patientCount", patientRepo.count());
        model.addAttribute("appointmentCount", appointmentRepo.count());

        Map<String, Long> departmentCounts = new LinkedHashMap<>();
        doctorRepo.findAllDepartments().forEach(department -> departmentCounts.put(department, 0L));

        appointmentRepo.findAll().stream()
            .collect(Collectors.groupingBy(
                appointment -> appointment.getDoctor().getDepartment(),
                Collectors.counting()
            ))
            .forEach((department, count) -> departmentCounts.put(department, count));

        model.addAttribute("departmentCounts", departmentCounts);
        return "stats";
    }
}