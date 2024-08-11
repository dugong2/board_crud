package jh.springboot.restapi.service;

import jh.springboot.restapi.dto.MessageDto;
import jh.springboot.restapi.entity.Message;
import jh.springboot.restapi.entity.User;
import jh.springboot.restapi.repository.MessageRepository;
import jh.springboot.restapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Transactional
    public MessageDto write(MessageDto messageDto) {
        User receiver = userRepository.findByName(messageDto.getReceiverName());
        User sender = userRepository.findByName(messageDto.getSenderName());

        Message message = new Message();
        message.setReceiver(receiver);
        message.setSender(sender);
        message.setTitle(messageDto.getTitle());
        message.setContent(messageDto.getContent());
        message.setDeletedByReceiver(false);
        message.setDeletedBySender(false);
        messageRepository.save(message);

        return MessageDto.toDto(message);
    }

    @Transactional(readOnly = true)
    public MessageDto findMessageById(int id) {
        Message message = messageRepository.findById(id).orElseThrow(() -> {
            return new IllegalArgumentException("메시지를 찾을 수 없습니다.");
        });

        return MessageDto.toDto(message);
    }

    @Transactional(readOnly = true)
    public List<MessageDto> receivedMessages(User user) {
        List<Message> messages = messageRepository.findAllByReceiver(user);
        List<MessageDto> messageDtos = new ArrayList<>();

        for (Message message : messages) {
            if (!message.isDeletedByReceiver()) {
                messageDtos.add(MessageDto.toDto(message));
            }
        }
        return messageDtos;
    }

    @Transactional
    public Object deleteMessageByReceiver(MessageDto messageDto, User user) {
        Message message = messageRepository.findById(messageDto.getId()).orElseThrow(() -> {
            return new IllegalArgumentException("메시지를 찾을 수 없습니다.");
        });
        message.deleteByReceiver();
        if (message.isDeleted()) {
            messageRepository.delete(message);
            return "양쪽 모두 삭제";
        }
        return "한쪽만 삭제";
    }

    @Transactional(readOnly = true)
    public List<MessageDto> sentMessages(User user) {
        List<Message> messages = messageRepository.findAllBySender(user);
        List<MessageDto> messageDtos = new ArrayList<>();

        for (Message message : messages) {
            if (!message.isDeletedBySender()) {
                messageDtos.add(MessageDto.toDto(message));
            }
        }
        return messageDtos;
    }

    @Transactional
    public Object deleteMessageBySender(MessageDto messageDto, User user) {
        Message message = messageRepository.findById(messageDto.getId()).orElseThrow(() -> {
            return new IllegalArgumentException("메시지를 찾을 수 없습니다.");
        });
        message.deleteBySender();
        if (message.isDeleted()) {
            messageRepository.delete(message);
            return "양쪽 모두 삭제";
        }
        return "한쪽만 삭제";
    }
}
