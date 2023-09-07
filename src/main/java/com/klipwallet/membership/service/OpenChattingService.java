package com.klipwallet.membership.service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.config.NftProperties;
import com.klipwallet.membership.dto.openchatting.OpenChattingAssembler;
import com.klipwallet.membership.dto.openchatting.OpenChattingCreate;
import com.klipwallet.membership.dto.openchatting.OpenChattingNftCreate;
import com.klipwallet.membership.dto.openchatting.OpenChattingOperatorCreate;
import com.klipwallet.membership.dto.openchatting.OpenChattingRow;
import com.klipwallet.membership.dto.openchatting.OpenChattingSummary;
import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.OpenChatting;
import com.klipwallet.membership.entity.OpenChattingNft;
import com.klipwallet.membership.entity.Operator;
import com.klipwallet.membership.entity.kakao.KakaoId;
import com.klipwallet.membership.entity.kakao.KakaoOpenlinkSummary;
import com.klipwallet.membership.entity.kakao.OpenChattingHost;
import com.klipwallet.membership.exception.kakao.OperatorAlreadyExistsException;
import com.klipwallet.membership.exception.operator.OperatorDuplicatedException;
import com.klipwallet.membership.repository.OpenChattingNftRepository;
import com.klipwallet.membership.repository.OpenChattingRepository;
import com.klipwallet.membership.service.kakao.KakaoService;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableConfigurationProperties(NftProperties.class)
public class OpenChattingService {
    private final NftProperties nftProperties;
    private final KakaoService kakaoService;
    private final OpenChattingMemberService openChattingMemberService;
    private final OperatorService operatorService;
    private final OpenChattingRepository openChattingRepository;
    private final OpenChattingNftRepository openChattingNftRepository;
    private final OpenChattingAssembler openChattingAssembler;

    @Transactional
    public OpenChattingSummary create(OpenChattingCreate command, AuthenticatedUser user) {
        checkOperators(command.operators(), command.host().operatorId());
        Operator host = operatorService.tryGetOperator(command.host().operatorId());
        KakaoOpenlinkSummary summary = kakaoService.createOpenChatting(command.title(), command.description(), command.coverImageUrl(),
                                                                       new OpenChattingHost(new KakaoId(host.getKakaoUserId()),
                                                                                            command.host().nickname(),
                                                                                            command.host().profileImageUrl()));
        OpenChatting saved = createOpenChatting(command, summary, new Address(nftProperties.getKlipDropsSca()), user.getMemberId());
        registerHost(saved, command.host(), user);
        registerOperators(saved, command.operators(), user);
        registerNfts(saved, command.nfts(), user);
        return new OpenChattingSummary(saved.getId(), saved.getKakaoOpenlinkSummary().getId(), saved.getKakaoOpenlinkSummary().getUrl(),
                                       saved.getTitle(),
                                       user.getMemberId().value(),
                                       saved.getCreatedAt().atZone(ZoneId.systemDefault()).toOffsetDateTime());
    }

    private OpenChatting createOpenChatting(OpenChattingCreate command, KakaoOpenlinkSummary summary, Address contractAddress, MemberId memberId) {
        OpenChatting entity = command.toOpenChatting(summary, contractAddress, memberId);
        return openChattingRepository.save(entity);
    }

    private void registerHost(OpenChatting openChatting, OpenChattingOperatorCreate hostCommand, AuthenticatedUser user) {
        openChattingMemberService.createHost(openChatting, hostCommand, user);
    }

    private void registerOperators(OpenChatting openChatting, List<OpenChattingOperatorCreate> operatorsCommand, AuthenticatedUser user) {
        openChattingMemberService.createOperators(openChatting, operatorsCommand, user);
    }

    private void registerNfts(OpenChatting openChatting, List<OpenChattingNftCreate> nftCommands, AuthenticatedUser user) {
        List<OpenChattingNft> nfts = new ArrayList<>();
        for (OpenChattingNftCreate nftCommand : nftCommands) {
            nfts.add(nftCommand.toOpenChattingNft(openChatting.getId(), user.getMemberId()));
        }
        openChattingNftRepository.saveAll(nfts);
    }

    @Transactional(readOnly = true)
    public List<OpenChattingRow> getAllOpenChattings() {
        List<OpenChatting> entities = openChattingRepository.findAll();
        return openChattingAssembler.toRows(entities);
    }

    private void checkOperators(List<OpenChattingOperatorCreate> operatorsCommand, Long hostOperatorId) {
        Set<Long> operatorIds = new HashSet<>();
        for (OpenChattingOperatorCreate command : operatorsCommand) {
            if (command.operatorId().equals(hostOperatorId)) {
                throw new OperatorAlreadyExistsException(command.operatorId());
            }
            if (operatorIds.contains(command.operatorId())) {
                throw new OperatorDuplicatedException(command.operatorId());
            } else {
                operatorIds.add(command.operatorId());
            }
        }
    }
}
