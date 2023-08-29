package com.klipwallet.membership.controller.manage;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.chatroom.ChatRoomMemberCreate;
import com.klipwallet.membership.dto.chatroom.ChatRoomMemberSummary;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.service.ChatRoomMemberService;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "Tool.ChatRoomMember", description = "Tool 채팅방 멤버 API")
@RestController
@RequestMapping("/tool/v1/chat-room-members")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomMemberManageController {
    private final ChatRoomMemberService chatRoomMemberService;

    @Operation(summary = "채팅방 멤버 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                         content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "멤버 생성 권한 없음 or 클립 연동 안됨",
                         content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public ChatRoomMemberSummary createChatRoomMember(
            @Valid @RequestBody ChatRoomMemberCreate command,
            @AuthenticationPrincipal AuthenticatedUser member) {
        return chatRoomMemberService.create(command);
    }


}
