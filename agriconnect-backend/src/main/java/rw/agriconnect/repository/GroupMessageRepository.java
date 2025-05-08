package rw.agriconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rw.agriconnect.model.Group;
import rw.agriconnect.model.GroupMessage;
import rw.agriconnect.model.User;

import java.util.List;

@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {
    List<GroupMessage> findByGroupOrderByCreatedAtAsc(Group group);
    
    @Query("SELECT COUNT(m) FROM GroupMessage m WHERE m.group = :group AND :user NOT MEMBER OF m.readBy")
    long countUnreadMessages(Group group, User user);
    
    @Query("SELECT m FROM GroupMessage m WHERE m.group = :group AND :user NOT MEMBER OF m.readBy")
    List<GroupMessage> findUnreadMessages(Group group, User user);
} 