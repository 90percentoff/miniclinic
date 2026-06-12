package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tw.edu.fju.miniclinic.model.Appointment;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

@RestController
public class AppointmentApiController {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    @GetMapping("/api/appointments")
    public List<Appointment> getAppointments(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String doctorId) {
        List<Appointment> appointments = appointmentRepo.findAll();

        if (date != null && !date.isBlank()) {
            try {
                appointments = appointmentRepo.findByApptDate(LocalDate.parse(date));
            } catch (DateTimeParseException ex) {
                return List.of();
            }
        }

        if (doctorId != null && !doctorId.isBlank()) {
            Optional<Doctor> doctor = doctorRepo.findById(doctorId);
            if (doctor.isEmpty()) {
                return List.of();
            }

            List<Appointment> doctorAppointments = appointmentRepo.findByDoctor(doctor.get());
            appointments = appointments.stream()
                .filter(doctorAppointments::contains)
                .toList();
        }

        return appointments;
    }

    @GetMapping("/api/appointments/count")
    public Map<String, Long> countAppointments() {
        return Map.of("count", appointmentRepo.count());
    }

    @PutMapping("/api/appointments/{apptId}/status")
    public ResponseEntity<Appointment> updateStatus(
            @PathVariable Long apptId,
            @RequestBody Map<String, String> payload,
            HttpSession session) {

        String loggedInDoctorId = (String) session.getAttribute(LoginController.SESSION_LOGGED_IN_DOCTOR_ID);

        Appointment appt = appointmentRepo.findById(apptId).orElse(null);
        if (appt == null) {
            return ResponseEntity.notFound().build();
        }

        if (appt.getDoctor() == null || !appt.getDoctor().getDoctorId().equals(loggedInDoctorId)) {
            return ResponseEntity.status(403).build();
        }

        String newStatus = payload.get("status");
        if (!List.of("BOOKED", "COMPLETED", "CANCELLED").contains(newStatus)) {
            return ResponseEntity.badRequest().build();
        }

        appt.setStatus(newStatus);
        return ResponseEntity.ok(appointmentRepo.save(appt));
    }
}