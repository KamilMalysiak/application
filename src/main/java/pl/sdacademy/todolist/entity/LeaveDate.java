package pl.sdacademy.todolist.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Date;

@Entity
@Data
public class LeaveDate {
    @Id
    @GeneratedValue
    private long id;
    private Date leaveDate;
}
