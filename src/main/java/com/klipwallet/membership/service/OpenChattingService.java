package com.klipwallet.membership.service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.config.NftProperties;
import com.klipwallet.membership.dto.openchatting.OpenChattingAssembler;
import com.klipwallet.membership.dto.openchatting.OpenChattingCreate;
import com.klipwallet.membership.dto.openchatting.OpenChattingDetail;
import com.klipwallet.membership.dto.openchatting.OpenChattingMemberCreate;
import com.klipwallet.membership.dto.openchatting.OpenChattingNftCreate;
import com.klipwallet.membership.dto.openchatting.OpenChattingOperatorCreate;
import com.klipwallet.membership.dto.openchatting.OpenChattingStatus;
import com.klipwallet.membership.dto.openchatting.OpenChattingSummary;
import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.KlipUser;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.OpenChatting;
import com.klipwallet.membership.entity.OpenChatting.Status;
import com.klipwallet.membership.entity.OpenChattingNft;
import com.klipwallet.membership.entity.Operator;
import com.klipwallet.membership.entity.TokenId;
import com.klipwallet.membership.entity.kakao.KakaoId;
import com.klipwallet.membership.entity.kakao.KakaoOpenlinkSummary;
import com.klipwallet.membership.entity.kakao.OpenChattingHost;
import com.klipwallet.membership.entity.kas.NftToken;
import com.klipwallet.membership.exception.ForbiddenException;
import com.klipwallet.membership.exception.InvalidRequestException;
import com.klipwallet.membership.exception.MemberNotFoundException;
import com.klipwallet.membership.exception.NotFoundException;
import com.klipwallet.membership.exception.kakao.OperatorAlreadyExistsException;
import com.klipwallet.membership.exception.kas.KasBadRequestInternalApiException;
import com.klipwallet.membership.exception.kas.KasNotFoundInternalApiException;
import com.klipwallet.membership.exception.openchatting.OpenChattingNftNotFoundException;
import com.klipwallet.membership.exception.openchatting.OpenChattingNotFoundException;
import com.klipwallet.membership.exception.operator.OperatorDuplicatedException;
import com.klipwallet.membership.repository.OpenChattingNftRepository;
import com.klipwallet.membership.repository.OpenChattingRepository;
import com.klipwallet.membership.service.kakao.KakaoService;

import static com.klipwallet.membership.exception.ErrorCode.OPEN_CHATTING_ACCESS_DENIED;
import static com.klipwallet.membership.exception.ErrorCode.OPEN_CHATTING_NOT_FOUND;

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
    private final KlipAccountService klipAccountService;
    private final TokenService tokenService;

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
                                       saved.getTitle(), saved.getStatus(),
                                       saved.getCreatedAt().atZone(ZoneId.systemDefault()).toOffsetDateTime(), null);
    }

    @Transactional(readOnly = true)
    public Page<OpenChattingSummary> list(Status status, Pageable pageable) {
        Sort orderByCreatedAtDesc = Sort.sort(OpenChatting.class).by(OpenChatting::getCreatedAt).descending();
        return getPaginationOpenChattingSummaries(status, pageable, orderByCreatedAtDesc);
    }

    private Page<OpenChattingSummary> getPaginationOpenChattingSummaries(Status status, Pageable pageable, Sort forceSort) {
        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), forceSort);
        Page<OpenChatting> openChattings = getPaginationOpenChattings(status, pageRequest);
        return openChattingAssembler.toSummaries(openChattings);
    }

    private Page<OpenChatting> getPaginationOpenChattings(Status status, Pageable pageRequest) {
        if (status == null) {
            return openChattingRepository.findAll(pageRequest);
        }
        return openChattingRepository.findAllByStatus(status, pageRequest);
    }

    @Transactional(readOnly = true)
    public OpenChattingDetail detail(Long openChattingId) {
        OpenChatting openChatting = getOpenChatting(openChattingId);
        List<OpenChattingNft> nfts = openChattingNftRepository.findByOpenChattingId(openChattingId);
        return openChattingAssembler.toDetail(openChatting, nfts);
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

    @Transactional
    public OpenChattingStatus joinChat(Address sca, TokenId tokenId, OpenChattingMemberCreate command, AuthenticatedUser user) {
        // TODO klip A2A으로 사용자 정보 조회하기
        KlipUser klipUser = klipAccountService.getKlipUser(new KakaoId(user.getName()));
        Address klaytnAddress = new Address("");

        // 오픈채팅 참여 가능 여부 확인
        verifyTokenOwnerToJoinOpenChatting(sca, tokenId, klaytnAddress);
        OpenChatting openChatting = getOpenChattingByTokenId(sca, tokenId);
        if (openChatting == null) {
            throw new NotFoundException(OPEN_CHATTING_NOT_FOUND);
        }

        // 오픈채팅 참여하기
        openChattingMemberService.createMember(openChatting, command, klipUser);
        return new OpenChattingStatus(true, true);
    }

    private OpenChatting getOpenChatting(Long id) {
        return openChattingRepository.findById(id).orElseThrow(() -> new OpenChattingNotFoundException(id));
    }

    private OpenChattingNft getOpenChattingNft(Address sca, Long dropId) {
        return openChattingNftRepository.findByScaAndDropId(sca, dropId).orElseThrow(OpenChattingNftNotFoundException::new);
    }

    private OpenChatting getOpenChattingByTokenId(Address sca, TokenId tokenId) {
        OpenChatting openChatting;
        try {
            OpenChattingNft nft = getOpenChattingNft(sca, tokenId.asKlipDropsDropId());
            openChatting = getOpenChatting(nft.getOpenChattingId());
        } catch (NotFoundException | InvalidRequestException e) {
            log.warn("getOpenChattingByTokenId error", e);
            return null;
        }
        return openChatting;
    }

    @Transactional(readOnly = true)
    public OpenChattingStatus getOpenChattingStatusByRequestKey(Address sca, TokenId tokenId, String requestKey) {
        // TODO klip A2A으로 requestKey로 klaytnAddress 알아내기
        Address klaytnAddress = new Address("");
        KlipUser klipUser = klipAccountService.getKlipUser(klaytnAddress);

        return getOpenChattingStatus(sca, tokenId, klaytnAddress, klipUser);
    }

    @Transactional(readOnly = true)
    public OpenChattingStatus getOpenChattingStatusByKlaytnAddress(Address sca, TokenId tokenId, Address klaytnAddress) {
        KlipUser klipUser = klipAccountService.getKlipUser(klaytnAddress);

        return getOpenChattingStatus(sca, tokenId, klaytnAddress, klipUser);
    }

    public OpenChattingStatus getOpenChattingStatus(Address sca, TokenId tokenId, Address klaytnAddress, KlipUser klipUser) {
        verifyTokenOwnerToJoinOpenChatting(sca, tokenId, klaytnAddress);

        OpenChatting openChatting = getOpenChattingByTokenId(sca, tokenId);
        if (openChatting == null) {
            return new OpenChattingStatus(false, false);
        }

        if (!isFirstEntryToOpenChatting(openChatting.getId(), klipUser.getKlipAccountId())) {
            return new OpenChattingStatus(true, false);
        }
        return new OpenChattingStatus(true, true);
    }

    private void verifyTokenOwnerToJoinOpenChatting(Address sca, TokenId tokenId, Address klaytnAddress) {
        NftToken token;
        try {
            token = tokenService.getNftToken(sca, tokenId);
        } catch (KasNotFoundInternalApiException | KasBadRequestInternalApiException e) {
            throw new ForbiddenException(OPEN_CHATTING_ACCESS_DENIED);
        }
        if (!token.isOwner(klaytnAddress)) {
            throw new ForbiddenException(OPEN_CHATTING_ACCESS_DENIED);
        }
    }

    private boolean isFirstEntryToOpenChatting(Long openChattingId, Long klipAccountId) {
        try {
            openChattingMemberService.getOpenChattingMemberByOpenChattingIdAndKlipId(openChattingId, klipAccountId);
        } catch (MemberNotFoundException e) {
            return false;
        }
        return true;
    }
}
