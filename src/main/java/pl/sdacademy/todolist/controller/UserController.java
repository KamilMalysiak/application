package pl.sdacademy.todolist.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.sdacademy.todolist.dto.AppointmentDto;
import pl.sdacademy.todolist.dto.MessageType;
import pl.sdacademy.todolist.dto.UserDto;
import pl.sdacademy.todolist.emailService.EmailService;
import pl.sdacademy.todolist.entity.Appointment;
import pl.sdacademy.todolist.entity.LeaveDate;
import pl.sdacademy.todolist.entity.Order;
import pl.sdacademy.todolist.entity.User;
import pl.sdacademy.todolist.repository.AppointmentRepository;
import pl.sdacademy.todolist.service.AppointmentService;
import pl.sdacademy.todolist.service.LeaveService;
import pl.sdacademy.todolist.service.OrderService;
import pl.sdacademy.todolist.service.UserService;
import pl.sdacademy.todolist.utils.AppUtils;
import pl.sdacademy.todolist.validators.RegisterValidator;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Controller
public class UserController {

    private final UserService userService;
    private final RegisterValidator registerValidator;
    private final OrderService orderService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentService appointmentService;
    private final LeaveService leaveService;

    @GetMapping(value = "/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userForm", new UserDto());
        return "register";
    }

    @PostMapping({"/register", "/message"})
    public String registerUser(@ModelAttribute(name = "userForm") UserDto userForm, BindingResult result, Model model) {
        Optional<User> existing = userService.findUserByPhoneNumber(userForm.getPhoneNumber());
        registerValidator.validate(userForm, result);
        registerValidator.validatePhoneExist(existing, result);
        if (result.hasErrors()) {
            return "register";
        }
        userService.create(userForm);
        emailService.sendMessage(userForm.getEmail(), "Witamy w naszym serwisie!", MessageType.MAIL_REGISTRATION);
        model.addAttribute("info", "Rejestracja zakończona sukcesem. Możesz się zalogować.");
        model.addAttribute("login", userForm.getPhoneNumber());
        return "login";
    }

    @GetMapping({"/list/{page}"})
    public String showListOfOrders(@PathVariable Integer page, Model model, Principal principal) {
        log.info("list of orders");
        Page<Order> ordersPage = orderService.findAllAsPage(page, 3, "dateOfOrder", "desc", principal.getName());
        List<Order> ordersList = ordersPage.getContent();
        int currentPage = ordersPage.getNumber();
        int totalPages = ordersPage.getTotalPages();
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("orders", ordersList);
        model.addAttribute("totalPages", totalPages);
        return "list";
    }

    @GetMapping("/list/{page}/sort")
    public String showSortedValues(@PathVariable Integer page, @RequestParam(name = "sortcolumn") String sortColumn, @RequestParam String ascdesc, @RequestParam Integer elements, Principal principal, Model model) {
        Page<Order> ordersPage = orderService.findAllAsPage(page, elements, sortColumn, ascdesc, principal.getName());
        List<Order> ordersList = ordersPage.getContent();
        int currentPage = ordersPage.getNumber();
        int totalPages = ordersPage.getTotalPages();
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("orders", ordersList);
        model.addAttribute("sortcolumn", sortColumn);
        model.addAttribute("ascdesc", ascdesc);
        model.addAttribute("elements", elements);
        model.addAttribute("totalPages", totalPages);
        return "list";
    }

    @GetMapping("resetpassword")
    public String showResetPage() {
        return "resetpassword";
    }


    @PostMapping("resetpassword")
    public String resetPassword(@RequestParam String phoneNumber, @RequestParam String email, Model model) {
        Optional<User> userOptional = userService.findUserByPhoneNumber(phoneNumber);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getEmail().equals(email)) {
                String newPassword = AppUtils.generatePassword();
                user.setPassword(passwordEncoder.encode(newPassword));
                userService.uptade(user);
                model.addAttribute("login", phoneNumber);
                model.addAttribute("info", "Nowe hasło zostało wysłane na Twój adres email. Możesz się nim zalogować.");
                emailService.sendMessage(email, "Twoje nowe hasło: " + newPassword, MessageType.MAIL_RESET_PASSWORD);
                return "login";
            }
        }
        model.addAttribute("info", "Brak konta użytkownika w bazie dla wskazanego adresu email i nr telefonu");
        return "resetpassword";
    }

    @GetMapping("/schedule")
    public String appointmentSchedule(Principal principal, Model model) {
        User user = userService.findByPhoneNumber(principal.getName());
        List<AppointmentDto> appointmentList = appointmentService.getAppointment();
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        model.addAttribute("appointment", appointment);
        model.addAttribute("appointments", appointmentList);
        List<LeaveDate> leaveDate = leaveService.getAllLeave();
        model.addAttribute("leave", leaveDate);
        return "scheduler";
    }

    @PostMapping("/confirmation")
    public String appointmentScheduleForm(@ModelAttribute(name = "appointment") Appointment appointment, Model model, Principal principal) {
        appointmentRepository.save(appointment);
        User user = userService.findByPhoneNumber(principal.getName());
        int hour = appointment.getAppointmentTime().toLocalTime().getHour();
        emailService.sendMessage("imac@wp.pl", "Masz nowe spotkanie umowione na dzień " + appointment.getAppointmentDate() + ", na godzinę " + hour
                + ".\nImię Klienta: " + appointment.getFirstName() + "\nEmail: " + user.getEmail() + "\nTelefon: " + user.getPhoneNumber(), MessageType.MAIL_ADMIN);
        emailService.sendMessage(user.getEmail(), "Twoje spotkanie jest umówione na dzień " + appointment.getAppointmentDate() + ", na godzinę " + hour + ". Prosimy o punktualność.", MessageType.MAIL_APPOINTMENT);
        model.addAttribute(appointment);
        return "confirmation";
    }

    @GetMapping("/services")
    public String showServices() {
        log.info("additional services");
        return "services";
    }

    @PostMapping("/services")
    public String getService(@RequestParam String name, @RequestParam String category, @RequestParam String body, Principal principal) {
        User user = userService.findByPhoneNumber(principal.getName());
        emailService.sendMessage("imac@wp.pl", "Masz nowe zapytanie w kategorii: " + category
                + ".\nImię Klienta: " + name
                + ".\nNr telefonu: " + principal.getName()
                + ".\nEmail: " + user.getEmail()
                + ".\nTreść zapytania: " + body, MessageType.MAIL_ADMIN);
        log.info(">>>>>>>>> Masz nowe zapytanie o usługę w kategorii " + category
                + "\nImię Klienta: " + name
                + "\nNr telefonu " + principal.getName()
                + "\nEmail: " + user.getEmail()
                + "\nTreść zapytania: " + body);
        return "serviceaskconfirmation";
    }

    @GetMapping("/error")
    public String errorsPage() {
        return "error";
    }

}