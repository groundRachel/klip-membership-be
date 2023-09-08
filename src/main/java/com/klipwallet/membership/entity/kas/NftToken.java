package com.klipwallet.membership.entity.kas;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import com.klipwallet.membership.adaptor.jpa.ForJpa;
import com.klipwallet.membership.entity.Address;

@Embeddable
@Value
public class NftToken {
    @Column(name = "token_id", nullable = false)
    String tokenId;
    @Column(name = "owner", nullable = false)
    String owner;
    @Column(name = "previous_owner", nullable = false)
    String previousOwner;
    @Column(name = "token_uri")
    String tokenUri;
    @Column(name = "transactoin_hash", nullable = false)
    String transactionHash;
    @Column(name = "created_at", nullable = false)
    int createdAt;
    @Column(name = "updated_at", nullable = false)
    int updatedAt;

    @SuppressWarnings("ProtectedMemberInFinalClass")
    @ForJpa
    protected NftToken() {
        this(null, null, null, null, null, 0, 0);
    }

    @JsonCreator
    public NftToken(@JsonProperty("tokenId") String tokenId, @JsonProperty("owner") String owner, @JsonProperty("previousOwner") String previousOwner,
                    @JsonProperty("tokenUri") String tokenUri, @JsonProperty("transactionHash") String transactionHash,
                    @JsonProperty("createdAt") int createdAt, @JsonProperty("updatedAt") int updatedAt) {
        this.tokenId = tokenId;
        this.owner = owner;
        this.previousOwner = previousOwner;
        this.tokenUri = tokenUri;
        this.transactionHash = transactionHash;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public boolean isOwner(@Nonnull Address candidate) {
        return this.owner.equalsIgnoreCase(candidate.getValue());
    }
}
