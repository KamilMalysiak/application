package pl.sdacademy.todolist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.sdacademy.todolist.dto.OrderDto;
import pl.sdacademy.todolist.entity.Order;
import pl.sdacademy.todolist.entity.User;
import pl.sdacademy.todolist.exception.EntityNotFoundException;
import pl.sdacademy.todolist.repository.OrderRepository;
import pl.sdacademy.todolist.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
@Validated
public class OrderService {


    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    private Map<Long, Order> tasks = new ConcurrentHashMap<>();


    public List<Order> findAllByDto(OrderDto orderDto) {
        return orderRepository.findAllByStatus(orderDto.getStatus());
    }

    public List<Order> findAllAsPage() {
        return orderRepository.findAll();
    }

    public List<Order> findAllByPhoneNumber(String phoneNumber) {
        return orderRepository.findAllByPhoneNumber(phoneNumber);
    }

    public Order find(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
    }

    public Order create(OrderDto orderDto) {
        User user = null;
        Optional<User> userOptional = userRepository.findUserByPhoneNumber(orderDto.getPhoneNumber());
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            user = new User();
            user.setPhoneNumber(orderDto.getPhoneNumber());
            userRepository.save(user);
        }

        Order orderEntity = new Order();
        orderEntity.setComments(orderDto.getComments());
        orderEntity.setDateOfOrder(orderDto.getDateOfOrder());
        orderEntity.setEstimatedDate(orderDto.getEstimatedDate());
        orderEntity.setOrderNo(orderDto.getOrderNo());
        orderEntity.setPhoneNumber(orderDto.getPhoneNumber());
        orderEntity.setStatus(orderDto.getStatus());
        orderEntity.setValue(orderDto.getValue());
        orderEntity.setUser(user);
        return orderRepository.save(orderEntity);
    }

    @Transactional
    public Order update(Order order) {
        Order orderEntity = orderRepository.findById(order.getId())
                .orElseThrow(() -> new EntityNotFoundException(order.getId()));
        orderEntity.setComments(order.getComments());
        orderEntity.setDateOfOrder(order.getDateOfOrder());
        orderEntity.setEstimatedDate(order.getEstimatedDate());
        orderEntity.setOrderNo(order.getOrderNo());
        orderEntity.setPhoneNumber(order.getPhoneNumber());
        orderEntity.setStatus(order.getStatus());
        orderEntity.setValue(order.getValue());
        return orderRepository.save(orderEntity);
    }

    public void delete(Long id) {
        Order task = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        orderRepository.delete(task);
    }

    public Page<Order>findAllPages(Pageable pageable, String phoneNumber){
        return orderRepository.findAllByPhoneNumber(pageable, phoneNumber);
    }

    public Page<Order> findAllAsPage(int page, int elementsOnPage, String sortBy, String ascDesc, String phoneNumber) {
        String chooseSortBy;
        switch (sortBy) {
            case "establishdate":
                chooseSortBy = "dateOfOrder";
                break;
            case "expecteddate":
                chooseSortBy = "estimatedDate";
                break;
            case "status":
                chooseSortBy = "status";
                break;
            case "ordervalue":
                chooseSortBy = "value";
                break;
            default:
                chooseSortBy = "orderNo";
        }

        return ascDesc.equals("asc")
                ? orderRepository.findAllByPhoneNumber(PageRequest.of(page, elementsOnPage, Sort.by(chooseSortBy).ascending()), phoneNumber)
                : orderRepository.findAllByPhoneNumber(PageRequest.of(page, elementsOnPage, Sort.by(chooseSortBy).descending()), phoneNumber);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}