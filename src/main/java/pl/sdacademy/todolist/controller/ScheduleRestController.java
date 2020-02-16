package pl.sdacademy.todolist.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import pl.sdacademy.todolist.entity.Appointment;
import pl.sdacademy.todolist.repository.AppointmentRepository;
import pl.sdacademy.todolist.service.LeaveService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
@RequestMapping("/services")
public class ScheduleRestController {

    private final AppointmentRepository appointmentRepository;
    private final LeaveService leaveService;

    @PostMapping("/add")
    public String addNewAppointment(@ModelAttribute Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return "Appointment was added succesfully";
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @GetMapping("/all")
    public List<Appointment> getAllAppointments() {
        return sortAppointments(appointmentRepository.findAll());
    }

    @GetMapping("/all/{date}")
    public List<Appointment> getAppointmentsByDate(@PathVariable String date) {
        try {
            Date sqlDate = Date.valueOf(date);
            return sortAppointments(appointmentRepository.findAllByAppointmentDate(sqlDate));
        } catch (Exception ex) {
            Appointment appt = new Appointment();
            List<Appointment> appointments = new ArrayList<>();
            appointments.add(appt);
            return appointments;
        }
    }

    private List<Appointment> sortAppointments(List<Appointment> appointments) {
        appointments.sort(Comparator.comparing(Appointment::getAppointmentDate)
                .thenComparing(Appointment::getAppointmentTime));
        return appointments;
    }

    @PostMapping("/add-leave")
    public String addLeave(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate leaveDate) {
        try {
            leaveService.addLeave( Date.valueOf(leaveDate));
            return "Leave was added successfully<br><a href=\"/leave\" > Back</a>";
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @PostMapping("/delete-leave")
    public String deleteLeave(@RequestParam(required = false) String[] deleteIds) {
        try {
            if(deleteIds!=null){
                for (String id : deleteIds){
                    leaveService.deleteLeave(Long.parseLong(id));
                }
                return "Leave was removed successfully<br><a href=\"/leave\" > Back</a>";
            }
            return "EMPTY<br><a href=\"/leave\" > Back</a>";
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
}
