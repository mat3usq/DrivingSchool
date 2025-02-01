package com.driving.school;


import com.driving.school.model.*;
import com.driving.school.repository.CategoryRepository;
import com.driving.school.repository.PaymentRepository;
import com.driving.school.repository.SchoolUserRepository;
import com.driving.school.repository.UserLikedQuestionRepository;
import com.driving.school.service.EmailSenderService;
import com.driving.school.service.NotificationService;
import com.driving.school.service.SchoolUserService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchoolUserServiceTest {

    @Mock
    private SchoolUserRepository schoolUserRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserLikedQuestionRepository userLikedQuestionRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailSenderService emailSenderService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SchoolUserService schoolUserService;

    private SchoolUser sampleUser;
    private Payment samplePayment;
    private Category sampleCategory;

    @BeforeEach
    void setUp() {
        sampleUser = new SchoolUser();
        sampleUser.setId(1L);
        sampleUser.setEmail("test@test.com");
        sampleUser.setName("Jan");
        sampleUser.setSurname("Kowalski");
        sampleUser.setPassword("12345");
        sampleUser.setRoleName(Constants.STUDENT_ROLE);
        sampleUser.setAvailableCategories(new ArrayList<>());

        sampleCategory = new Category();
        sampleCategory.setId(10L);
        sampleCategory.setNameCategory("B");

        samplePayment = new Payment(100.0, "Opis płatności", List.of(sampleCategory), sampleUser);
        samplePayment.setId(1L);
    }

    @Nested
    @DisplayName("Metoda: createNewUser")
    class CreateNewUserTests {

        @Test
        @DisplayName("Powinna zwrócić true, gdy użytkownik nie istnieje i zostanie zapisany poprawnie")
        void shouldReturnTrueWhenUserIsCreated() {
            when(schoolUserRepository.existsByEmail(sampleUser.getEmail())).thenReturn(false);
            when(passwordEncoder.encode("12345")).thenReturn("encoded");
            when(schoolUserRepository.save(any(SchoolUser.class))).thenAnswer(invocation -> {
                SchoolUser u = invocation.getArgument(0);
                u.setId(1L);
                return u;
            });
            try {
                doNothing().when(emailSenderService).sendWelcomeMail(any(SchoolUser.class));
                when(categoryRepository.findAll()).thenReturn(List.of(sampleCategory));


                boolean result = schoolUserService.createNewUser(sampleUser);

                assertThat(result).isTrue();
                verify(schoolUserRepository, times(1)).save(any(SchoolUser.class));
                verify(emailSenderService, times(1)).sendWelcomeMail(any(SchoolUser.class));
            } catch (MessagingException e) {
            }

        }

        @Test
        @DisplayName("Powinna zwrócić false, gdy użytkownik już istnieje")
        void shouldReturnFalseWhenUserAlreadyExists() {
            try {
                when(schoolUserRepository.existsByEmail(sampleUser.getEmail())).thenReturn(true);

                boolean result = schoolUserService.createNewUser(sampleUser);

                assertThat(result).isFalse();
                verify(schoolUserRepository, never()).save(any(SchoolUser.class));
                verify(emailSenderService, never()).sendWelcomeMail(any(SchoolUser.class));
            } catch (MessagingException e) {
            }
        }

        @Test
        @DisplayName("Powinna przechwycić wyjątek MessagingException, ale dalej zwrócić true, gdy zapis się powiedzie")
        void shouldCatchMessagingExceptionButStillReturnTrue() throws MessagingException {
            when(schoolUserRepository.existsByEmail(sampleUser.getEmail())).thenReturn(false);
            when(passwordEncoder.encode("12345")).thenReturn("encoded");
            when(schoolUserRepository.save(any(SchoolUser.class))).thenAnswer(invocation -> {
                SchoolUser u = invocation.getArgument(0);
                u.setId(1L);
                return u;
            });
            doThrow(new MessagingException("Test exception")).when(emailSenderService).sendWelcomeMail(any(SchoolUser.class));
            when(categoryRepository.findAll()).thenReturn(List.of(sampleCategory));

            boolean result = schoolUserService.createNewUser(sampleUser);

            assertThat(result).isTrue();
            verify(schoolUserRepository, times(1)).save(any(SchoolUser.class));
            verify(emailSenderService, times(1)).sendWelcomeMail(any(SchoolUser.class));
        }
    }

    @Nested
    @DisplayName("Metoda: hasUserCategory")
    class HasUserCategoryTests {

        @Test
        @DisplayName("Powinna zwrócić true, jeśli kategoria należy do użytkownika")
        void shouldReturnTrueIfUserHasCategory() {
            Long userId = 1L;
            String categoryName = "B";
            when(schoolUserRepository.existsByIdAndAvailableCategories_NameCategory(userId, categoryName))
                    .thenReturn(true);

            boolean result = schoolUserService.hasUserCategory(userId, categoryName);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Powinna zwrócić false, jeśli userId lub categoryName jest null/puste lub nie ma takiej kategorii u użytkownika")
        void shouldReturnFalseIfUserDoesNotHaveCategory() {
            Long userId = 1L;
            String categoryName = "B";
            when(schoolUserRepository.existsByIdAndAvailableCategories_NameCategory(anyLong(), anyString()))
                    .thenReturn(false);

            boolean result = schoolUserService.hasUserCategory(userId, categoryName);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Metoda: saveUser")
    class SaveUserTests {

        @Test
        @DisplayName("Powinna wywołać metodę save z repozytorium")
        void shouldCallRepositorySave() {
            doReturn(sampleUser).when(schoolUserRepository).save(sampleUser);

            schoolUserService.saveUser(sampleUser);

            verify(schoolUserRepository, times(1)).save(sampleUser);
        }
    }

    @Nested
    @DisplayName("Metoda: findUserById")
    class FindUserByIdTests {

        @Test
        @DisplayName("Powinna zwrócić użytkownika dla podanego ID")
        void shouldReturnUserIfExists() {
            when(schoolUserRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

            SchoolUser found = schoolUserService.findUserById(1L);

            assertThat(found).isEqualTo(sampleUser);
        }

        @Test
        @DisplayName("Powinna zwrócić null, jeśli użytkownik o danym ID nie istnieje")
        void shouldReturnNullIfNotExists() {
            when(schoolUserRepository.findById(anyLong())).thenReturn(Optional.empty());

            SchoolUser found = schoolUserService.findUserById(999L);

            assertThat(found).isNull();
        }
    }

    @Nested
    @DisplayName("Metoda: findAllInstructors")
    class FindAllInstructorsTests {

        @Test
        @DisplayName("Powinna zwrócić listę wszystkich instruktorów")
        void shouldReturnAllInstructors() {
            SchoolUser instructor = new SchoolUser();
            instructor.setRoleName(Constants.INSTRUCTOR_ROLE);
            SchoolUser student = new SchoolUser();
            student.setRoleName(Constants.STUDENT_ROLE);
            when(schoolUserRepository.findAll()).thenReturn(List.of(instructor, student));

            List<SchoolUser> instructors = schoolUserService.findAllInstructors();

            assertThat(instructors).hasSize(1);
            assertThat(instructors.get(0).getRoleName()).isEqualTo(Constants.INSTRUCTOR_ROLE);
        }
    }

    @Nested
    @DisplayName("Metoda: findAllUsers")
    class FindAllUsersTests {

        @Test
        @DisplayName("Powinna zwrócić stronicowaną listę użytkowników")
        void shouldReturnPageOfUsers() {
            Pageable pageable = PageRequest.of(0, 2);
            Page<SchoolUser> userPage = new PageImpl<>(List.of(sampleUser));
            when(schoolUserRepository.findAll(pageable)).thenReturn(userPage);

            Page<SchoolUser> result = schoolUserService.findAllUsers(pageable);

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent()).contains(sampleUser);
        }
    }

    @Nested
    @DisplayName("Metoda: addLikedQuestionToUser")
    class AddLikedQuestionToUserTests {

        @Test
        @DisplayName("Powinna dodać polubione pytanie, jeśli go wcześniej nie było")
        void shouldAddLikedQuestionIfNotExists() {
            Long questionId = 100L;
            Long testId = 10L;
            when(schoolUserRepository.findById(sampleUser.getId())).thenReturn(Optional.of(sampleUser));
            when(userLikedQuestionRepository.findBySchoolUserAndQuestionIdAndTestId(any(SchoolUser.class), anyLong(), anyLong()))
                    .thenReturn(null);

            schoolUserService.addLikedQuestionToUser(questionId, testId, sampleUser);

            assertThat(sampleUser.getLikedQuestions()).hasSize(1);
            verify(schoolUserRepository, times(1)).save(sampleUser);
        }

        @Test
        @DisplayName("Nie powinna dodać pytania, jeśli już istnieje w polubionych")
        void shouldNotAddIfAlreadyExists() {
            Long questionId = 100L;
            Long testId = 10L;

            UserLikedQuestion liked = new UserLikedQuestion();
            liked.setQuestionId(questionId);
            liked.setTestId(testId);
            sampleUser.getLikedQuestions().add(liked);

            when(schoolUserRepository.findById(sampleUser.getId())).thenReturn(Optional.of(sampleUser));
            when(userLikedQuestionRepository.findBySchoolUserAndQuestionIdAndTestId(sampleUser, questionId, testId))
                    .thenReturn(liked);

            schoolUserService.addLikedQuestionToUser(questionId, testId, sampleUser);

            assertThat(sampleUser.getLikedQuestions()).hasSize(1);
            verify(schoolUserRepository, never()).save(sampleUser);
        }
    }

    @Nested
    @DisplayName("Metoda: deleteLikedQuestionFromUser")
    class DeleteLikedQuestionFromUserTests {

        @Test
        @DisplayName("Powinna usunąć polubione pytanie z listy użytkownika")
        void shouldDeleteLikedQuestion() {
            Long questionId = 100L;
            Long testId = 10L;

            UserLikedQuestion liked = new UserLikedQuestion();
            liked.setQuestionId(questionId);
            liked.setTestId(testId);
            sampleUser.getLikedQuestions().add(liked);

            schoolUserService.deleteLikedQuestionFromUser(questionId, testId, sampleUser);

            assertThat(sampleUser.getLikedQuestions()).isEmpty();
            verify(schoolUserRepository, times(1)).save(sampleUser);
        }
    }

    @Nested
    @DisplayName("Metoda: findAllLikedQuestionsByUserIdAndTestId")
    class FindAllLikedQuestionsByUserIdAndTestIdTests {

        @Test
        @DisplayName("Powinna zwrócić wszystkie polubione pytania użytkownika dla testu")
        void shouldReturnAllLikedQuestions() {
            Long userId = 1L;
            Long testId = 10L;
            List<UserLikedQuestion> likedQuestions = List.of(new UserLikedQuestion());
            when(schoolUserRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
            when(userLikedQuestionRepository.findAllBySchoolUserAndTestId(sampleUser, testId)).thenReturn(likedQuestions);

            List<UserLikedQuestion> result = schoolUserService.findAllLikedQuestionsByUserIdAndTestId(userId, testId);

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Metoda: findUserByEmail")
    class FindUserByEmailTests {

        @Test
        @DisplayName("Powinna zwrócić użytkownika na podstawie e-maila")
        void shouldReturnUserByEmail() {
            String email = "test@test.com";
            when(schoolUserRepository.findByEmail(email)).thenReturn(sampleUser);

            SchoolUser found = schoolUserService.findUserByEmail(email);

            assertThat(found).isEqualTo(sampleUser);
        }
    }

    @Nested
    @DisplayName("Metoda: changeCategory")
    class ChangeCategoryTests {

        @Test
        @DisplayName("Powinna zmienić kategorię, jeśli user ma ją na liście dostępnych")
        void shouldChangeCategoryIfUserHasIt() {
            sampleUser.getAvailableCategories().add(sampleCategory);
            when(schoolUserRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
            when(categoryRepository.findById(10L)).thenReturn(Optional.of(sampleCategory));

            schoolUserService.changeCategory(sampleUser, 10L);

            assertThat(sampleUser.getCurrentCategory()).isEqualTo("B");
            verify(schoolUserRepository, times(1)).save(sampleUser);
        }


        @Nested
        @DisplayName("Metoda: deletePayment")
        class DeletePaymentTests {

            @Test
            @DisplayName("Powinna usunąć płatność oraz zaktualizować listę kategorii użytkownika")
            void shouldDeletePaymentAndUpdateCategories() {
                sampleUser.getAvailableCategories().add(sampleCategory);
                sampleUser.getPayments().add(samplePayment);

                when(paymentRepository.findById(1L)).thenReturn(Optional.of(samplePayment));
                when(paymentRepository.findAllBySchoolUserId(sampleUser.getId())).thenReturn(Collections.emptyList());

                schoolUserService.deletePayment(1L);

                verify(paymentRepository, times(1)).delete(samplePayment);
                assertThat(sampleUser.getAvailableCategories()).isEmpty();
                assertThat(sampleUser.getCurrentCategory()).isEmpty();
                verify(schoolUserRepository, times(1)).save(sampleUser);
            }
        }

        @Nested
        @DisplayName("Metoda: addPayment")
        class AddPaymentTests {

            @Test
            @DisplayName("Powinna dodać nową płatność i przypisać nowe kategorie użytkownikowi")
            void shouldAddPaymentAndAddNewCategories() {
                when(schoolUserRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

                schoolUserService.addPayment(1L, samplePayment);

                verify(paymentRepository, times(1)).save(samplePayment);
                verify(notificationService, times(1)).sendNotificationWhenUserReceivePayment(any(Payment.class));
                assertThat(sampleUser.getAvailableCategories()).contains(sampleCategory);
                verify(schoolUserRepository, times(1)).save(sampleUser);
            }

            @Test
            @DisplayName("Nie powinna dodawać kategorii, jeśli użytkownik już je posiada")
            void shouldNotAddExistingCategories() {
                sampleUser.getAvailableCategories().add(sampleCategory);
                when(schoolUserRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

                schoolUserService.addPayment(1L, samplePayment);

                verify(paymentRepository, times(1)).save(samplePayment);
                assertThat(sampleUser.getAvailableCategories()).hasSize(1);
                verify(schoolUserRepository, never()).save(sampleUser);
            }
        }

        @Nested
        @DisplayName("Metoda: promoteUser")
        class PromoteUserTests {

            @Test
            @DisplayName("Powinna promować studenta na instruktora")
            void shouldPromoteStudentToInstructor() {
                sampleUser.setRoleName(Constants.STUDENT_ROLE);

                schoolUserService.promoteUser(sampleUser);

                assertThat(sampleUser.getRoleName()).isEqualTo(Constants.INSTRUCTOR_ROLE);
                verify(schoolUserRepository, times(1)).save(sampleUser);
                verify(notificationService, times(1)).sendNotificationWhenUserReceiveNewRole(sampleUser);
            }

            @Test
            @DisplayName("Powinna promować instruktora na admina")
            void shouldPromoteInstructorToAdmin() {
                sampleUser.setRoleName(Constants.INSTRUCTOR_ROLE);

                schoolUserService.promoteUser(sampleUser);

                assertThat(sampleUser.getRoleName()).isEqualTo(Constants.ADMIN_ROLE);
                verify(schoolUserRepository, times(1)).save(sampleUser);
                verify(notificationService, times(1)).sendNotificationWhenUserReceiveNewRole(sampleUser);
            }

            @Test
            @DisplayName("Nie powinna zmienić roli, jeśli jest już adminem")
            void shouldNotChangeRoleIfAlreadyAdmin() {
                sampleUser.setRoleName(Constants.ADMIN_ROLE);

                schoolUserService.promoteUser(sampleUser);

                assertThat(sampleUser.getRoleName()).isEqualTo(Constants.ADMIN_ROLE);
                verify(schoolUserRepository, never()).save(sampleUser);
                verify(notificationService, never()).sendNotificationWhenUserReceiveNewRole(sampleUser);
            }
        }

        @Nested
        @DisplayName("Metoda: demoteUser")
        class DemoteUserTests {

            @Test
            @DisplayName("Powinna zdegradować admina do instruktora")
            void shouldDemoteAdminToInstructor() {
                sampleUser.setRoleName(Constants.ADMIN_ROLE);

                schoolUserService.demoteUser(sampleUser);

                assertThat(sampleUser.getRoleName()).isEqualTo(Constants.INSTRUCTOR_ROLE);
                verify(schoolUserRepository, times(1)).save(sampleUser);
                verify(notificationService, times(1)).sendNotificationWhenUserReceiveNewRole(sampleUser);
            }

            @Test
            @DisplayName("Powinna zdegradować instruktora do studenta")
            void shouldDemoteInstructorToStudent() {
                sampleUser.setRoleName(Constants.INSTRUCTOR_ROLE);

                schoolUserService.demoteUser(sampleUser);

                assertThat(sampleUser.getRoleName()).isEqualTo(Constants.STUDENT_ROLE);
                verify(schoolUserRepository, times(1)).save(sampleUser);
                verify(notificationService, times(1)).sendNotificationWhenUserReceiveNewRole(sampleUser);
            }

            @Test
            @DisplayName("Nie powinna zmienić roli, jeśli użytkownik jest już studentem")
            void shouldNotChangeRoleIfAlreadyStudent() {
                sampleUser.setRoleName(Constants.STUDENT_ROLE);

                schoolUserService.demoteUser(sampleUser);

                assertThat(sampleUser.getRoleName()).isEqualTo(Constants.STUDENT_ROLE);
                verify(schoolUserRepository, never()).save(sampleUser);
                verify(notificationService, never()).sendNotificationWhenUserReceiveNewRole(sampleUser);
            }
        }
    }
}
