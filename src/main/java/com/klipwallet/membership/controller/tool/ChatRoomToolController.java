package com.klipwallet.membership.controller.tool;

import java.util.List;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.chatroom.ChatRoomCreate;
import com.klipwallet.membership.dto.chatroom.ChatRoomRow;
import com.klipwallet.membership.dto.chatroom.ChatRoomSummary;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.service.ChatRoomService;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "Tool.ChatRoom", description = "Tool 채팅방 API")
@RestController
@RequestMapping("/tool/v1/chat-rooms")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomToolController {
    private final ChatRoomService chatRoomService;

    @Operation(summary = "채팅방 개설")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "개설 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                         content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "채팅방 개설 권한 없음 or 카카오 연동 안됨",
                         content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public ChatRoomSummary createChatRoom(
            @Valid @RequestBody ChatRoomCreate command,
            @AuthenticationPrincipal AuthenticatedUser member) {
        return chatRoomService.create(command, member);
    }

    @Operation(summary = "채팅방 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public List<ChatRoomRow> chatRoomList() {
        return chatRoomService.getAllChatRooms();
    }
}
