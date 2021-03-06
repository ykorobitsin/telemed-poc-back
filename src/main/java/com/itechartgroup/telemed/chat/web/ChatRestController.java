package com.itechartgroup.telemed.chat.web;

import com.itechartgroup.telemed.chat.dto.ChatMessageDto;
import com.itechartgroup.telemed.chat.dto.ChatMessageSource;
import com.itechartgroup.telemed.chat.dto.ChatRoomDto;
import com.itechartgroup.telemed.chat.service.ChatMessageService;
import com.itechartgroup.telemed.chat.service.ChatRoomService;
import com.itechartgroup.telemed.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;

import static com.itechartgroup.telemed.chat.constant.ChatConstants.SESSION_ATTR_LAST_FETCH;


/**
 * @author s.vareyko
 * @since 07.04.2020
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/chat")
public class ChatRestController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @GetMapping("/room")
    public ResponseEntity<Page<ChatRoomDto>> loadRooms(@PageableDefault final Pageable pageable,
                                                       final HttpSession session,
                                                       @AuthenticationPrincipal final UserPrincipal principal) {
        setLastFetchAttribute(session);
        return new ResponseEntity<>(chatRoomService.load(pageable, principal.getId()), HttpStatus.OK);
    }

    @PostMapping("/room")
    public ResponseEntity<ChatRoomDto> createRoom(@RequestBody final Set<UUID> participants,
                                                  @AuthenticationPrincipal final UserPrincipal principal) {
        participants.add(principal.getId());
        return new ResponseEntity<>(chatRoomService.create(participants), HttpStatus.OK);
    }

    @PostMapping("/message")
    public ResponseEntity<ChatMessageDto> sendMessage(@RequestBody final ChatMessageDto dto,
                                                      @AuthenticationPrincipal final UserPrincipal principal) {

        // only possible to send messages as user
        dto.setAuthor(principal.getId());
        dto.setSource(ChatMessageSource.USER);

        return new ResponseEntity<>(chatMessageService.send(dto), HttpStatus.ACCEPTED);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Page<ChatMessageDto>> loadMessages(@PathVariable final UUID roomId,
                                                             @PageableDefault final Pageable pageable,
                                                             final HttpSession session) {
        setLastFetchAttribute(session);
        return new ResponseEntity<>(chatMessageService.load(roomId, pageable), HttpStatus.OK);
    }

    @GetMapping("/poll")
    public ResponseEntity<SortedSet<ChatRoomDto>> pollMessages(final HttpSession session,
                                                               @AuthenticationPrincipal final UserPrincipal principal) {
        try {
            final long lastFetch = getLastFetchAttribute(session);
            final UUID userId = principal.getId();
            return new ResponseEntity<>(chatMessageService.poll(lastFetch, userId), HttpStatus.OK);
        } finally {
            setLastFetchAttribute(session);
        }
    }

    @PostMapping("/room/{roomId}")
    public ResponseEntity<ChatRoomDto> markAsRead(@PathVariable final UUID roomId,
                                                  @AuthenticationPrincipal final UserPrincipal principal) {
        return new ResponseEntity<>(chatRoomService.markAsRead(principal.getId(), roomId), HttpStatus.OK);
    }

    private long getLastFetchAttribute(final HttpSession session) {
        return (long) Optional.ofNullable(session.getAttribute(SESSION_ATTR_LAST_FETCH))
                .orElseGet(System::currentTimeMillis);
    }

    private void setLastFetchAttribute(final HttpSession session) {
        session.setAttribute(SESSION_ATTR_LAST_FETCH, System.currentTimeMillis());
    }
}
