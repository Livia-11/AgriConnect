package rw.agriconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rw.agriconnect.model.Group;
import rw.agriconnect.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByGroupId(String groupId);
    
    @Query("SELECT g FROM Group g JOIN g.members m WHERE m = :user AND g.isActive = true ORDER BY g.lastMessageAt DESC")
    List<Group> findActiveGroupsForUser(User user);
    
    @Query("SELECT COUNT(g) FROM Group g JOIN g.members m WHERE m = :user AND g.isActive = true")
    long countActiveGroupsForUser(User user);
    
    boolean existsByName(String name);
    
    @Query("SELECT COUNT(m) FROM Group g JOIN g.members m WHERE g = :group")
    long countMembers(Group group);
} 