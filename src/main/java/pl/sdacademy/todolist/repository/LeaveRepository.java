package pl.sdacademy.todolist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sdacademy.todolist.entity.LeaveDate;

import java.sql.Date;
import java.util.List;

public interface LeaveRepository extends JpaRepository<LeaveDate, Long> {
    List<LeaveDate> findAllByLeaveDateAfter(Date date);
}
