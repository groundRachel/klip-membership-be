package com.klipwallet.membership.entity;

import jakarta.persistence.Entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import com.klipwallet.membership.adaptor.jpa.ForJpa;

/**
 * FAQ Entity
 */
@Entity
@DynamicUpdate
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Faq extends AbstractArticle<Faq> {
    @ForJpa
    protected Faq() {
        super();
    }

    public Faq(String title, String body, MemberId creatorId) {
        super(title, body, creatorId);
    }

    public void update(FaqUpdatable command) {
        super.update(command);
    }
}
