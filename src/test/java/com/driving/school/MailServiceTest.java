package com.driving.school;


import com.driving.school.model.Constants;
import com.driving.school.model.Mail;
import com.driving.school.model.SchoolUser;
import com.driving.school.repository.MailRepository;
import com.driving.school.service.MailService;
import com.driving.school.service.SchoolUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private MailRepository mailRepository;
    @Mock
    private SchoolUserService schoolUserService;

    @InjectMocks
    private MailService mailService;

    private SchoolUser sender;
    private SchoolUser recipient;
    private Mail sampleMail;
    private Mail parentMail;

    @BeforeEach
    void setUp() {
        sender = new SchoolUser();
        sender.setId(1L);
        sender.setEmail("sender@test.com");
        sender.setName("SenderName");
        sender.setRoleName(Constants.INSTRUCTOR_ROLE);
        sender.setNumberOfMails(0);

        recipient = new SchoolUser();
        recipient.setId(2L);
        recipient.setEmail("recipient@test.com");
        recipient.setName("RecipientName");
        recipient.setRoleName(Constants.STUDENT_ROLE);
        recipient.setNumberOfMails(0);

        parentMail = new Mail();
        parentMail.setId(10L);
        parentMail.setSubject("Parent Mail Subject");
        parentMail.setBody("Parent Body");
        parentMail.setSender(sender);
        parentMail.setRecipient(recipient);
        parentMail.setStatusSender(Constants.MAIL_READ);
        parentMail.setStatusRecipient(Constants.MAIL_READ);

        sampleMail = new Mail();
        sampleMail.setId(100L);
        sampleMail.setSubject("Test Subject");
        sampleMail.setBody("Test Body");
        sampleMail.setSender(sender);
        sampleMail.setRecipient(recipient);
        sampleMail.setStatusSender(Constants.MAIL_READ);
        sampleMail.setStatusRecipient(Constants.MAIL_UNREAD);
    }

    @Nested
    @DisplayName("Metoda: sendMail(sender, recipient, subject, body, parentMail)")
    class SendMailWithParentTests {

        @Test
        @DisplayName("Powinna wysłać nową wiadomość, ustawić status i dodać do replies parentMaila (jeśli nie jest null)")
        void shouldSendMailAndLinkToParent() {
            when(mailRepository.save(any(Mail.class))).thenAnswer(inv -> inv.getArgument(0));

            Mail sent = mailService.sendMail(sender, recipient, "Subject X", "Body X", parentMail);

            assertThat(sent.getSender()).isEqualTo(sender);
            assertThat(sent.getRecipient()).isEqualTo(recipient);
            assertThat(sent.getSubject()).isEqualTo("Subject X");
            assertThat(sent.getBody()).isEqualTo("Body X");
            assertThat(sent.getStatusSender()).isEqualTo(Constants.MAIL_READ);
            assertThat(sent.getStatusRecipient()).isEqualTo(Constants.MAIL_UNREAD);

            assertThat(parentMail.getReplies()).contains(sent);

            verify(mailRepository, times(1)).save(any(Mail.class));
            verify(schoolUserService, times(1)).saveUser(recipient);
        }

        @Test
        @DisplayName("Jeśli parentMail == null, to nie dodaje do replies żadnej wiadomości")
        void shouldNotAddToParentRepliesIfParentIsNull() {
            when(mailRepository.save(any(Mail.class))).thenAnswer(inv -> inv.getArgument(0));

            Mail sent = mailService.sendMail(sender, recipient, "Hello", "World", null);

            verify(mailRepository, times(1)).save(sent);
            assertThat(sent.getParentMail()).isNull();
        }
    }

    @Nested
    @DisplayName("Metoda: sendMail(sender, sendMail)")
    class SendMailWithMailObjectTests {

        @Test
        @DisplayName("Powinna wysłać wiadomość, jeśli recipient istnieje i nie jest ten sam co sender")
        void shouldSendIfRecipientExistsAndNotSame() {
            when(schoolUserService.findUserByEmail(recipient.getEmail())).thenReturn(recipient);
            when(mailRepository.save(any(Mail.class))).thenReturn(sampleMail);

            boolean success = mailService.sendMail(sender, sampleMail);

            assertThat(success).isTrue();
            verify(mailRepository, times(1)).save(sampleMail);
            verify(schoolUserService, times(1)).saveUser(recipient);
        }

        @Test
        @DisplayName("Nie powinna wysłać, jeśli recipient jest nullem lub taki sam jak sender")
        void shouldNotSendIfRecipientIsSenderOrNull() {
            when(schoolUserService.findUserByEmail(anyString())).thenReturn(sender);

            boolean success = mailService.sendMail(sender, sampleMail);

            assertThat(success).isFalse();
            verify(mailRepository, never()).save(any(Mail.class));
            verify(schoolUserService, never()).saveUser(any());
        }
    }

    @Nested
    @DisplayName("Metoda: replyOnMail")
    class ReplyOnMailTests {

        @Test
        @DisplayName("Powinna dodać reply do parentMail, ustawić statusy, i wywołać updateNumberOfMails")
        void shouldReplyOnMail() {
            Mail reply = new Mail("Odpowiedź na mail");
            reply.setId(200L);

            when(mailRepository.findById(10L)).thenReturn(Optional.of(parentMail));
            when(mailRepository.save(any(Mail.class))).thenAnswer(inv -> inv.getArgument(0));

            SchoolUser loggedUser = recipient;

            boolean result = mailService.replyOnMail(reply, 10L, loggedUser);

            assertThat(result).isTrue();
            assertThat(parentMail.getReplies()).contains(reply);
            assertThat(parentMail.getStatusSender()).isEqualTo(Constants.MAIL_UNREAD);
            assertThat(reply.getStatusRecipient()).isEqualTo(Constants.MAIL_REPLY);
            assertThat(reply.getSender()).isEqualTo(recipient);
            assertThat(reply.getRecipient()).isEqualTo(sender);

            verify(mailRepository, times(1)).save(reply);
            verify(mailRepository, times(1)).save(parentMail);
            verify(schoolUserService, times(1)).saveUser(sender);
        }

        @Test
        @DisplayName("Powinna zwrócić false, jeśli parentMail nie istnieje lub body jest puste")
        void shouldReturnFalseIfInvalid() {
            Mail reply = new Mail("   ");
            when(mailRepository.findById(999L)).thenReturn(Optional.empty());

            boolean notExistingParent = mailService.replyOnMail(reply, 999L, recipient);
            boolean emptyBody = mailService.replyOnMail(reply, 10L, recipient);

            assertThat(notExistingParent).isFalse();
            assertThat(emptyBody).isFalse();
            verify(mailRepository, never()).save(any(Mail.class));
        }

        @Test
        @DisplayName("Powinna zwrócić false, jeśli zalogowany user nie jest senderem lub recipientem parentMaila")
        void shouldReturnFalseIfNotSenderNorRecipient() {
            Mail reply = new Mail("Treść odpowiedzi");
            SchoolUser someUser = new SchoolUser();
            someUser.setId(999L);

            when(mailRepository.findById(10L)).thenReturn(Optional.of(parentMail));

            boolean result = mailService.replyOnMail(reply, 10L, someUser);

            assertThat(result).isFalse();
            verify(mailRepository, never()).save(any(Mail.class));
        }
    }

    @Nested
    @DisplayName("Metoda: markAsRead")
    class MarkAsReadTests {

        @Test
        @DisplayName("Powinna ustawić statusRecipient = MAIL_READ, gdy user jest odbiorcą i mail jest UNREAD")
        void shouldMarkAsReadForRecipient() {
            sampleMail.setStatusRecipient(Constants.MAIL_UNREAD); // bo w setUp() dajemy UNREAD
            when(mailRepository.findById(100L)).thenReturn(Optional.of(sampleMail));
            when(mailRepository.save(any(Mail.class))).thenAnswer(inv -> inv.getArgument(0));

            Optional<Mail> opt = mailService.markAsRead(100L, recipient);

            assertThat(opt).isPresent();
            assertThat(opt.get().getStatusRecipient()).isEqualTo(Constants.MAIL_READ);
            verify(mailRepository, times(1)).save(sampleMail);
            verify(schoolUserService, times(1)).saveUser(recipient);
        }

        @Test
        @DisplayName("Powinna ustawić statusSender = MAIL_READ, gdy user jest nadawcą i mail jest UNREAD")
        void shouldMarkAsReadForSender() {
            sampleMail.setStatusSender(Constants.MAIL_UNREAD);
            when(mailRepository.findById(100L)).thenReturn(Optional.of(sampleMail));

            Optional<Mail> opt = mailService.markAsRead(100L, sender);

            assertThat(opt).isPresent();
            assertThat(opt.get().getStatusSender()).isEqualTo(Constants.MAIL_READ);
            verify(mailRepository, times(1)).save(sampleMail);
            verify(schoolUserService, times(1)).saveUser(sender);
        }

        @Test
        @DisplayName("Jeśli mailId nie istnieje, zwraca Optional.empty()")
        void shouldReturnEmptyIfNotExists() {
            when(mailRepository.findById(999L)).thenReturn(Optional.empty());

            Optional<Mail> opt = mailService.markAsRead(999L, recipient);

            assertThat(opt).isEmpty();
            verify(mailRepository, never()).save(any(Mail.class));
        }
    }

    @Nested
    @DisplayName("Metoda: moveToTrashMail")
    class MoveToTrashMailTests {

        @Test
        @DisplayName("Powinna ustawić statusRecipient na TRASHED, jeśli user to odbiorca")
        void shouldTrashForRecipient() {
            when(mailRepository.findById(100L)).thenReturn(Optional.of(sampleMail));

            mailService.moveToTrashMail(100L, recipient);

            assertThat(sampleMail.getStatusRecipient()).isEqualTo(Constants.MAIL_TRASHED);
            verify(mailRepository, times(1)).save(sampleMail);
        }

        @Test
        @DisplayName("Powinna ustawić statusSender na TRASHED, jeśli user to nadawca")
        void shouldTrashForSender() {
            when(mailRepository.findById(100L)).thenReturn(Optional.of(sampleMail));

            mailService.moveToTrashMail(100L, sender);

            assertThat(sampleMail.getStatusSender()).isEqualTo(Constants.MAIL_TRASHED);
            verify(mailRepository, times(1)).save(sampleMail);
        }
    }

    @Nested
    @DisplayName("Metoda: moveMailFromTrash")
    class MoveMailFromTrashTests {

        @Test
        @DisplayName("Powinna ustawić statusRecipient na READ, jeśli mail był w TRASH, a user to odbiorca")
        void shouldRestoreRecipientMail() {
            sampleMail.setStatusRecipient(Constants.MAIL_TRASHED);
            when(mailRepository.findById(100L)).thenReturn(Optional.of(sampleMail));

            mailService.moveMailFromTrash(100L, recipient);

            assertThat(sampleMail.getStatusRecipient()).isEqualTo(Constants.MAIL_READ);
            verify(mailRepository, times(1)).save(sampleMail);
        }

        @Test
        @DisplayName("Powinna ustawić statusSender na READ, jeśli mail był w TRASH, a user to nadawca")
        void shouldRestoreSenderMail() {
            sampleMail.setStatusSender(Constants.MAIL_TRASHED);
            when(mailRepository.findById(100L)).thenReturn(Optional.of(sampleMail));

            mailService.moveMailFromTrash(100L, sender);

            assertThat(sampleMail.getStatusSender()).isEqualTo(Constants.MAIL_READ);
            verify(mailRepository, times(1)).save(sampleMail);
        }
    }

    @Nested
    @DisplayName("Metoda: deleteMail")
    class DeleteMailTests {

        @Test
        @DisplayName("Powinna ustawić statusRecipient na DELETED, jeśli user to odbiorca")
        void shouldDeleteForRecipient() {
            when(mailRepository.findById(100L)).thenReturn(Optional.of(sampleMail));

            mailService.deleteMail(100L, recipient);

            assertThat(sampleMail.getStatusRecipient()).isEqualTo(Constants.MAIL_DELETED);
            verify(mailRepository, times(1)).save(sampleMail);
        }

        @Test
        @DisplayName("Powinna ustawić statusSender na DELETED, jeśli user to nadawca")
        void shouldDeleteForSender() {
            when(mailRepository.findById(100L)).thenReturn(Optional.of(sampleMail));

            mailService.deleteMail(100L, sender);

            assertThat(sampleMail.getStatusSender()).isEqualTo(Constants.MAIL_DELETED);
            verify(mailRepository, times(1)).save(sampleMail);
        }
    }

    @Nested
    @DisplayName("Metody: getMailsForXByStatus, getEmailsForUser itp.")
    class GetMailQueriesTests {

        @Test
        @DisplayName("Powinny zwracać listę maili na podstawie statusu, posortowaną malejąco po updatedAt")
        void shouldReturnMailsFromRepository() {
            List<Mail> mails = List.of(sampleMail);
            when(mailRepository.findByRecipientAndStatusRecipientOrderByUpdatedAtDesc(recipient, Constants.MAIL_UNREAD))
                    .thenReturn(mails);

            List<Mail> unreadMails = mailService.getMailsForRecipientByStatus(recipient, Constants.MAIL_UNREAD);

            assertThat(unreadMails).containsExactly(sampleMail);
            verify(mailRepository, times(1))
                    .findByRecipientAndStatusRecipientOrderByUpdatedAtDesc(recipient, Constants.MAIL_UNREAD);
        }


    }

    @Nested
    @DisplayName("Metoda: updateNumberOfMails (pośrednio testowana w innych)")
    class UpdateNumberOfMailsTests {

        @Test
        @DisplayName("Gdy jest wywoływana, ustawia user.setNumberOfMails na liczbę UNREAD maili i saveUser(...)")
        void shouldUpdateNumberOfMails() {
            when(mailRepository.findById(100L)).thenReturn(Optional.of(sampleMail));
            when(mailRepository.save(any(Mail.class))).thenReturn(sampleMail);

            when(mailRepository.findByRecipientAndStatusRecipientOrderByUpdatedAtDesc(recipient, Constants.MAIL_UNREAD))
                    .thenReturn(Collections.emptyList());
            when(mailRepository.findBySenderAndStatusSenderOrderByUpdatedAtDesc(recipient, Constants.MAIL_UNREAD))
                    .thenReturn(Collections.emptyList());

            mailService.markAsRead(100L, recipient);

            assertThat(recipient.getNumberOfMails()).isZero();
            verify(schoolUserService, times(1)).saveUser(recipient);
        }
    }
}

