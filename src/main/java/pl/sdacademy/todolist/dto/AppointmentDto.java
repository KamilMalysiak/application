package pl.sdacademy.todolist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Time;
import java.sql.Date;

@Data
@AllArgsConstructor
public class AppointmentDto {
    private Date appointmentDate;
    private Time appointmentTime;
}
