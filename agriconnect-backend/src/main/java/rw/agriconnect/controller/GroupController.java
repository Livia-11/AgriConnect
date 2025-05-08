package rw.agriconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import rw.agriconnect.model.Group;
import rw.agriconnect.model.GroupMessage;
import rw.agriconnect.service.GroupService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<Group> createGroup(
            @RequestBody CreateGroupRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(groupService.createGroup(
                request.getName(),
                request.getDescription(),
                Long.parseLong(userDetails.getUsername()),
                request.getMemberIds(),
                request.isPrivate(),
                request.getMaxMembers()
        ));
    }

    @MessageMapping("/group/send")
    public void sendGroupMessage(@Payload GroupMessageRequest request) {
        groupService.sendGroupMessage(
                request.getGroupId(),
                request.getSenderId(),
                request.getContent()
        );
    }

    @GetMapping("/{groupId}/messages")
    public ResponseEntity<List<GroupMessage>> getGroupMessages(
            @PathVariable String groupId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(groupService.getGroupMessages(groupId));
    }

    @PostMapping("/{groupId}/read")
    public ResponseEntity<Void> markGroupMessagesAsRead(
            @PathVariable String groupId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        groupService.markGroupMessagesAsRead(
                groupId,
                Long.parseLong(userDetails.getUsername())
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Void> addMemberToGroup(
            @PathVariable String groupId,
            @PathVariable Long userId
    ) {
        groupService.addMemberToGroup(groupId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Void> removeMemberFromGroup(
            @PathVariable String groupId,
            @PathVariable Long userId
    ) {
        groupService.removeMemberFromGroup(groupId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Group>> getUserGroups(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(groupService.getUserGroups(
                Long.parseLong(userDetails.getUsername())
        ));
    }

    @PostMapping("/{groupId}/archive")
    public ResponseEntity<Void> archiveGroup(
            @PathVariable String groupId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        groupService.archiveGroup(groupId);
        return ResponseEntity.ok().build();
    }
}

record CreateGroupRequest(
    String name,
    String description,
    Set<Long> memberIds,
    boolean isPrivate,
    Integer maxMembers
) {}

record GroupMessageRequest(
    String groupId,
    Long senderId,
    String content
) {} 