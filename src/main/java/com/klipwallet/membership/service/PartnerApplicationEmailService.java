package com.klipwallet.membership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.klipwallet.membership.entity.PartnerApplicationApproved;
import com.klipwallet.membership.entity.PartnerApplicationRejected;

@Service
@RequiredArgsConstructor
public class PartnerApplicationEmailService {
    @Autowired
    private EmailSendable emailSendable;

    final String senderEmailAddress = "winnie.byun@groundx.xyz"; //  TODO: check sender email
    final String subject = "Klip Membership Tool 가입 요청 결과"; // TODO: move to mesages.xml
    final String contentApproved = "가입 요청을 승인하였습니다.";
    final String contentRejected = "가입 요청을 거절하였습니다. 거절 사유 : ";

    @TransactionalEventListener(value = PartnerApplicationApproved.class, phase = TransactionPhase.AFTER_COMMIT)
    public void notifyApproveResult(PartnerApplicationApproved event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getEmail());
        message.setSubject(subject);
        message.setText(contentApproved);

        emailSendable.sendEmail(message);
    }

    @TransactionalEventListener(value = PartnerApplicationRejected.class, phase = TransactionPhase.AFTER_COMMIT)
    public void notifyRejectResult(PartnerApplicationRejected event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getEmail());
        message.setSubject(subject);
        message.setText(contentRejected + event.getRejectReason());

        emailSendable.sendEmail(message);
    }
}
