package com.klipwallet.membership.entity;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 이용자 Entity
 * <pre>
 * - 파트너사
 * - 관리자
 * </pre>
 *
 * @see com.klipwallet.membership.entity.Partner
 * @see com.klipwallet.membership.entity.Admin
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
@EntityListeners(AuditingEntityListener.class)
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public abstract class Member extends BaseEntity<Member> {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @Setter(AccessLevel.PACKAGE)
    @Column(unique = true)
    private String email;
    /**
     * oAuthId
     * <p>
     * 구글 oauth2 id(sub)
     * </p>
     */
    @Column(unique = true)
    @Setter(AccessLevel.PACKAGE)
    private String oAuthId;
    /**
     * 표시 이름
     * <p>
     * - partner: 회사명<br/>
     * - admin: 이메일 LocalPart<br/>
     * </p>
     */
    @Setter(AccessLevel.PACKAGE)
    @Column(nullable = false)
    private String name;

    /**
     * 멤버 상태
     * <p>
     * - activated<br/>
     * - withdrawal<br/>
     * </p>
     */
    @Setter(AccessLevel.PACKAGE)
    @Column(nullable = false)
    private Status status;

    @JsonIgnore
    @Transient
    private MemberId memberId;

    @Nullable
    @JsonIgnore
    public MemberId getMemberId() {
        if (id == null) {
            return null;
        }
        if (memberId == null) {
            this.memberId = new MemberId(id);
        }
        return this.memberId;
    }

    /**
     * 멤버 탈퇴
     *
     * @param deleterId 삭제자 ID
     */
    public void withdraw(MemberId deleterId) {
        if (this.status == Status.WITHDRAWAL) {
            return;
        }
        this.status = Status.WITHDRAWAL;
        this.name = toDeletedName();
        this.email = null;
        this.oAuthId = null;
        updateBy(deleterId);
    }

    private String toDeletedName() {
        return this.name + "(탈퇴)";
    }

    public boolean isEnabled() {
        return this.status.isEnabled();
    }

    @Getter
    @Schema(name = "Notice.Status", description = "공지사항 상태", example = "live")
    public enum Status implements Statusable {
        /**
         * 유효한 상태
         */
        ACTIVATED(1),
        /**
         * 탈퇴한 상태
         */
        WITHDRAWAL(2);

        private static final Set<Status> ENABLES = Stream.of(values())
                                                         .filter(s -> s != WITHDRAWAL)
                                                         .collect(Collectors.toUnmodifiableSet());

        private final byte code;

        Status(int code) {
            this.code = Statusable.requireVerifiedCode(code);
        }

        @JsonCreator
        @Nullable
        public static Status fromDisplay(String display) {
            return Statusable.fromDisplay(Status.class, display);
        }

        /**
         * 유효한 멤버 상태들
         * <p>
         * {@link #WITHDRAWAL} 상태를 제외한 나머지 유효한 상태들 = 관리자가 접근 가능한 상태
         * </p>
         *
         * @return 유효한 멤버 상태들
         */
        public static Set<Status> enables() {
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
}
