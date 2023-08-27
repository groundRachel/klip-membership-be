package com.klipwallet.membership.entity;

import java.util.Set;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.lang.Nullable;

import static java.util.stream.Collectors.toUnmodifiableSet;


@Getter
@Schema(name = "ArticleStatus", description = "게시물 상태", example = "live")
public enum ArticleStatus implements Statusable {

    /**
     * 초안: 최초 등록 후 발행 전까지 상태. 이용자가 조회할 수 없음.
     */
    DRAFT(0),
    /**
     * 발행: 이용자가 조회할 수 있음.
     */
    LIVE(1),
    /**
     * 비활성화: 이용자가 조회할 수 없음.
     */
    INACTIVE(2),
    /**
     * (논리적) 삭제
     */
    @Hidden
    DELETE(3);

    private static final Set<ArticleStatus> ENABLES = Stream.of(values())
                                                            .filter(s -> s != DELETE)
                                                            .collect(toUnmodifiableSet());

    private final byte code;

    ArticleStatus(int code) {
        this.code = Statusable.requireVerifiedCode(code);
    }

    @JsonCreator
    @Nullable
    public static ArticleStatus fromDisplay(String display) {
        return Statusable.fromDisplay(ArticleStatus.class, display);
    }

    /**
     * 게시물 유효한 상태들
     * <p>
     * {@link #DELETE} 상태를 제외한 나머지 유효한 상태들 = 관리자가 접근 가능한 상태
     * </p>
     *
     * @return 유효한 공지사항 상태들
     */
    public static Set<ArticleStatus> enables() {
        return ENABLES;
    }

    @JsonValue
    @Override
    public String toDisplay() {
        return Statusable.super.toDisplay();
    }

    public boolean isEnabled() {
        return enables().contains(this);
    }

}
