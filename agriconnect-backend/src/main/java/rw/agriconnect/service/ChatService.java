package rw.agriconnect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import rw.agriconnect.model.ChatMessage;
import rw.agriconnect.model.User;
import rw.agriconnect.repository.ChatMessageRepository;
import rw.agriconnect.repository.UserRepository;
import rw.agriconnect.repository.ProductRepository;
import rw.agriconnect.repository.ChatRoomRepository;
import rw.agriconnect.model.Product;
import rw.agriconnect.model.ChatRoom;
import rw.agriconnect.model.MessageType;
import rw.agriconnect.model.ChatMessageResponse;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ProductRepository productRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatMessage sendMessage(Long senderId, Long receiverId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        ChatMessage message = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);

        // Send message to receiver
        messagingTemplate.convertAndSendToUser(
                receiver.getEmail(),
                "/queue/messages",
                savedMessage
        );

        return savedMessage;
    }

    public List<ChatMessage> getChatHistory(Long userId1, Long userId2) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new RuntimeException("User 1 not found"));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new RuntimeException("User 2 not found"));

        return chatMessageRepository.findChatMessagesBetweenUsers(user1, user2);
    }

    public void markMessagesAsRead(Long senderId, Long receiverId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        List<ChatMessage> unreadMessages = chatMessageRepository.findChatMessagesBetweenUsers(sender, receiver)
                .stream()
                .filter(message -> !message.isRead() && message.getReceiver().equals(receiver))
                .toList();

        unreadMessages.forEach(message -> message.setRead(true));
        chatMessageRepository.saveAll(unreadMessages);
    }

    public long getUnreadMessageCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return chatMessageRepository.countUnreadMessages(user);
    }

    public ChatMessage shareProduct(Long senderId, Long receiverId, Long productId) {
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
            .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        ChatRoom chatRoom = chatRoomRepository.findByFarmerAndBuyer(product.getFarmer(), receiver)
            .orElseGet(() -> createChatRoom(product.getFarmer(), receiver));

        ChatMessage message = new ChatMessage();
        message.setChatRoom(chatRoom);
        message.setSender(sender);
        message.setMessageType(MessageType.PRODUCT_SHARE);
        message.setProductId(productId);
        message.setContent("Shared a product: " + product.getName());
        message.setCreatedAt(LocalDateTime.now());

        chatRoom.setLastMessageAt(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);
        
        ChatMessage savedMessage = chatMessageRepository.save(message);
        
        // Notify receiver
        messagingTemplate.convertAndSendToUser(
            receiver.getEmail(),
            "/queue/messages",
            new ChatMessageResponse(savedMessage)
        );

        return savedMessage;
    }
} 