package pl.sdacademy.todolist.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;
import pl.sdacademy.todolist.entity.LeaveDate;
import pl.sdacademy.todolist.service.LeaveService;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final LeaveService leaveService;

    @GetMapping("/appointments")
    public String showappointments(){
        return "appointments";
    }

    @GetMapping("/leave")
    public String getAllLeave(Model model) {
        List<LeaveDate> leaveDate = leaveService.getAllLeave();
        model.addAttribute("leave", leaveDate);
        model.addAttribute("leaveDate", LocalDate.now());
        return "leave";
    }
}
