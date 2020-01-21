package pl.sdacademy.todolist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sdacademy.todolist.dto.AppointmentDto;
import pl.sdacademy.todolist.entity.Appointment;
import pl.sdacademy.todolist.repository.AppointmentRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    public List<AppointmentDto>  getAppointment(){
        return appointmentRepository.findAll()
                .stream()
                .map(value -> new AppointmentDto(value.getAppointmentDate(), value.getAppointmentTime()))
                .collect(Collectors.toList());
    }
}
