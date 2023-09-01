package com.klipwallet.membership.adaptor.ses;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

import com.klipwallet.membership.config.AwsSesProperties;
import com.klipwallet.membership.exception.notifier.EmailNotifierException;
import com.klipwallet.membership.service.EmailSendable;

@Primary
@Profile("!local")
@Component
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties({AwsSesProperties.class})
public class SesEmailAdaptor implements EmailSendable {

    private final SesV2Client sesClient;
    private final AwsSesProperties awsSesProperties;

    @Override
    public void sendEmail(SimpleMailMessage message) {
        SendEmailRequest emailRequest = buildEmailRequest(message);

        Optional.of(emailRequest)
                .map(sesClient::sendEmail)
                .filter(sendEmailResponse -> sendEmailResponse.sdkHttpResponse().isSuccessful())
                .orElseThrow(EmailNotifierException::new);
    }

    private SendEmailRequest buildEmailRequest(SimpleMailMessage message) {
        Destination destination = Destination.builder().toAddresses(message.getTo()).build();

        Content sub = Content.builder().data(message.getSubject()).build();

        Content content = Content.builder().data(message.getText()).build();
        Body body = Body.builder().text(content).build();// TODO HTML

        Message msg = Message.builder().subject(sub).body(body).build();
        EmailContent emailContent = EmailContent.builder().simple(msg).build();

        String sender = awsSesProperties.getSenderEmail();

        return SendEmailRequest.builder()
                               .destination(destination)
                               .content(emailContent)
                               .fromEmailAddress(sender)
                               .build();
    }
}
