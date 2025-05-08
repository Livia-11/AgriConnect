package rw.agriconnect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.agriconnect.model.Group;
import rw.agriconnect.model.GroupMessage;
import rw.agriconnect.model.User;
import rw.agriconnect.repository.GroupMessageRepository;
import rw.agriconnect.repository.GroupRepository;
import rw.agriconnect.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMessageRepository groupMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Group createGroup(String name, String description, Long createdById, Set<Long> memberIds, boolean isPrivate, Integer maxMembers) {
        if (groupRepository.existsByName(name)) {
            throw new RuntimeException("Group name already exists");
        }

        User creator = userRepository.findById(createdById)
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        Set<User> members = userRepository.findAllById(memberIds);
        members.add(creator); // Add creator to members

        Group group = Group.builder()
                .name(name)
                .description(description)
                .createdBy(creator)
                .members(members)
                .isPrivate(isPrivate)
                .maxMembers(maxMembers)
                .build();

        return groupRepository.save(group);
    }

    @Transactional
    public GroupMessage sendGroupMessage(String groupId, Long senderId, String content) {
        Group group = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        if (!group.getMembers().contains(sender)) {
            throw new RuntimeException("User is not a member of this group");
        }

        GroupMessage message = GroupMessage.builder()
                .group(group)
                .sender(sender)
                .content(content)
                .build();

        GroupMessage savedMessage = groupMessageRepository.save(message);

        // Update group's last message time
        group.setLastMessageAt(LocalDateTime.now());
        groupRepository.save(group);

        // Send message to all group members
        messagingTemplate.convertAndSend(
                "/topic/group/" + groupId,
                savedMessage
        );

        return savedMessage;
    }

    public List<GroupMessage> getGroupMessages(String groupId) {
        Group group = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        return groupMessageRepository.findByGroupOrderByCreatedAtAsc(group);
    }

    @Transactional
    public void markGroupMessagesAsRead(String groupId, Long userId) {
        Group group = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<GroupMessage> unreadMessages = groupMessageRepository.findUnreadMessages(group, user);
        unreadMessages.forEach(message -> message.getReadBy().add(user));
        groupMessageRepository.saveAll(unreadMessages);
    }

    @Transactional
    public void addMemberToGroup(String groupId, Long userId) {
        Group group = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (group.getMaxMembers() != null && groupRepository.countMembers(group) >= group.getMaxMembers()) {
            throw new RuntimeException("Group has reached maximum member limit");
        }

        group.getMembers().add(user);
        groupRepository.save(group);
    }

    @Transactional
    public void removeMemberFromGroup(String groupId, Long userId) {
        Group group = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (group.getCreatedBy().equals(user)) {
            throw new RuntimeException("Cannot remove group creator");
        }

        group.getMembers().remove(user);
        groupRepository.save(group);
    }

    public List<Group> getUserGroups(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return groupRepository.findActiveGroupsForUser(user);
    }

    @Transactional
    public void archiveGroup(String groupId) {
        Group group = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        group.setActive(false);
        groupRepository.save(group);
    }
} 