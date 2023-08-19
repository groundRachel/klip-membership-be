package com.klipwallet.membership.entity;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * 모든 Entity의 기본이 되는 BaseEntity
 * <p>
 * Entity에서 상속이라는 중요한 기회를 {@code BaseEntity}에 사용하는 것이 아깝긴하나, 개발편의 성을 위해서 Trade-Off.<br/>
 * 아래와 같은 속성을 기본으로 가지고 있다.
 *   <ul>
 *     <li>createdAt: 생성일시</li>
 *     <li>creatorId: 생성자 아이디</li>
 *     <li>updatedAt: 최근 수정일시</li>
 *     <li>updaterId: 최근 수정자 아이디</li>
 *   </ul>
 *   추가로 DomainEvent를 등록할 수 있는 {@link org.springframework.data.domain.AbstractAggregateRoot#registerEvent(Object)} 도 제공한다.
 * </p>
 * <p>
 * <b>예외 사항 </b><br/>
 * 만약 위 4개의 속성이 필요 없으면 BaseEntity를 상속 받지 않아도 된다.<br/>
 * 예를 들면 {@code createdAt}, {@code creatorId} 만 필요한 경우에는 {@link com.klipwallet.membership.entity.CreateBaseEntity}를 사용한다.
 * </p>
 *
 * @see org.springframework.data.domain.AbstractAggregateRoot
 * @see com.klipwallet.membership.entity.CreateBaseEntity
 */
@MappedSuperclass
@Getter
public abstract class BaseEntity<E extends BaseEntity<E>> extends CreateBaseEntity<E> {
    /**
     * 수정일시
     *
     * @see org.springframework.data.annotation.LastModifiedDate
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    @Column(nullable = false)
    private MemberId updaterId;

    /**
     * 생성자 설정
     * <p>
     * <b>{@code Entity}를 생성할 때 설정 해야 한다.</b> 중요한 것은 이 떄 {@link #updaterId}도 설정 된다.
     * 그래서 따로 {@link #updateBy(MemberId)}를 호출할 필요 없다.
     * </p>
     *
     * @param creatorId 생성자 아이디
     */
    @Override
    protected void createBy(@NonNull MemberId creatorId) {
        super.createBy(creatorId);
        this.updaterId = creatorId;
    }

    /**
     * 최근 수정자 설정
     * <p>
     * <b>{@code Entity}를 변경할 때 설정 해야 한다.</b>
     * </p>
     *
     * @param updaterId 수정자 아이디
     */
    protected void updateBy(@NonNull MemberId updaterId) {
        this.updaterId = updaterId;
    }

    /**
     * 생성자, 최근 수정자 아이디들을 조회한다.
     * <p>
     * 자료구조가 {@link java.util.Set} 으로 되어 있기 때문에 중복이 없다.
     * 만약 생성자와 최근 수정자 아이디가 같다면 반환된 {@code Set}의 {@code size}는 1일 될 것이다.
     * </p>
     *
     * @return 생성자, 최근 수정자 아이디 Set
     * @see com.klipwallet.membership.service.MemberAssembler
     */
    public Set<MemberId> getAccessorIds() {
        if (equalCreatorAndUpdater()) {
            return Set.of(this.getCreatorId());
        }
        return Set.of(this.getCreatorId(), this.getUpdaterId());
    }

    private boolean equalCreatorAndUpdater() {
        return this.getCreatorId().equals(this.getUpdaterId());
    }
}
