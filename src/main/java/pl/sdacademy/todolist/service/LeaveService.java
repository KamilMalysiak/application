package pl.sdacademy.todolist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sdacademy.todolist.entity.LeaveDate;
import pl.sdacademy.todolist.exception.EntityNotFoundException;
import pl.sdacademy.todolist.repository.LeaveRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LeaveService {
    private final LeaveRepository leaveRepository;

    public List<LeaveDate> getAllLeave() {
        return leaveRepository.findAllByLeaveDateAfter(Date.valueOf(LocalDate.now()));
    }

    public String addLeave(Date leaveDate) {
        LeaveDate leave = new LeaveDate();
        leave.setLeaveDate(leaveDate);
        try {
            leaveRepository.save(leave);
            return "Leave was added succesfully";
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public void deleteLeave(Long leaveId) {
        LeaveDate leave = leaveRepository.findById(leaveId).orElseThrow(() -> new EntityNotFoundException( leaveId));
        leaveRepository.delete(leave);
    }
}
