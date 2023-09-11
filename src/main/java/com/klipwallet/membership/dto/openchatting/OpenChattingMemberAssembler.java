package com.klipwallet.membership.dto.openchatting;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.entity.OpenChattingMember;
import com.klipwallet.membership.entity.OpenChattingMember.Role;
import com.klipwallet.membership.exception.operator.HostNotFoundException;
import com.klipwallet.membership.repository.OpenChattingMemberRepository;

@Component
@RequiredArgsConstructor
public class OpenChattingMemberAssembler {
    private final OpenChattingMemberRepository openChattingMemberRepository;

    public OpenChattingOperatorDetail getHostDetail(Long openChattingId) {
        List<OpenChattingMember> operators = openChattingMemberRepository.findByOpenChattingIdAndRole(openChattingId, Role.HOST);
        if (operators.size() != 1) {
            throw new HostNotFoundException(openChattingId);
        }
        OpenChattingMember host = operators.get(0);
        return toOperatorDetail(host);
    }

    public List<OpenChattingOperatorDetail> getOperatorsDetail(Long openChattingId) {
        List<OpenChattingMember> operators = openChattingMemberRepository.findByOpenChattingIdAndRole(openChattingId, Role.OPERATOR);
        return operators.stream().map(this::toOperatorDetail).toList();
    }

    private OpenChattingOperatorDetail toOperatorDetail(OpenChattingMember entity) {
        // TODO: @Winnie Get Klip User Email
        return new OpenChattingOperatorDetail(entity.getId(), entity.getOperatorId(), entity.getNickname(), entity.getProfileImageUrl(),
                                              entity.getRole(),
                                              "test@todoemail.com");
    }
}
