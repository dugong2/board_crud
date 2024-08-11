package jh.springboot.restapi.repository;

import jh.springboot.restapi.entity.Message;
import jh.springboot.restapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findAllByReceiver(User user);
    List<Message> findAllBySender(User user);
}
