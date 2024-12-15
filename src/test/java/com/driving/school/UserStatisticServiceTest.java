package com.driving.school.service;

import com.driving.school.exception.ResourceNotFoundException;
import com.driving.school.model.Category;
import com.driving.school.model.SchoolUser;
import com.driving.school.model.UserStatistic;
import com.driving.school.repository.CategoryRepository;
import com.driving.school.repository.SchoolUserRepository;
import com.driving.school.repository.UserStatisticRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserStatisticServiceTest {

    @Mock
    private UserStatisticRepository userStatisticRepository;

    @Mock
    private SchoolUserRepository schoolUserRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private UserStatisticService userStatisticService;

    private SchoolUser schoolUser;
    private Category category;
    private UserStatistic userStatistic;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        schoolUser = new SchoolUser();
        schoolUser.setId(1L);
        schoolUser.setName("Jan");
        schoolUser.setSurname("Kowalski");
        schoolUser.setEmail("jan.kowalski@example.com");

        category = new Category();
        category.setId(1L);
        category.setNameCategory("A");

        userStatistic = new UserStatistic();
        userStatistic.setId(1L);
        userStatistic.setDeposits(100L);
        userStatistic.setIsExamPassed("Yes");
        userStatistic.setHoursed("10");
        userStatistic.setHoursBuyed("5");
        userStatistic.setSchoolUser(schoolUser);
        userStatistic.setCategory(category);
    }

    @Test
    void testGetAllUserStatistics() {
        List<UserStatistic> statistics = Arrays.asList(userStatistic);
        when(userStatisticRepository.findAll()).thenReturn(statistics);

        List<UserStatistic> result = userStatisticService.getAllUserStatistics();
        assertEquals(1, result.size());
        verify(userStatisticRepository, times(1)).findAll();
    }

    @Test
    void testGetStatisticsByUser() {
        List<UserStatistic> statistics = Arrays.asList(userStatistic);
        when(userStatisticRepository.findBySchoolUser(schoolUser)).thenReturn(statistics);

        List<UserStatistic> result = userStatisticService.getStatisticsByUser(schoolUser);
        assertEquals(1, result.size());
        verify(userStatisticRepository, times(1)).findBySchoolUser(schoolUser);
    }

    @Test
    void testGetStatisticsByCategory() {
        List<UserStatistic> statistics = Arrays.asList(userStatistic);
        when(userStatisticRepository.findByCategory(category)).thenReturn(statistics);

        List<UserStatistic> result = userStatisticService.getStatisticsByCategory(category);
        assertEquals(1, result.size());
        verify(userStatisticRepository, times(1)).findByCategory(category);
    }

    @Test
    void testGetStatisticByUserAndCategory() {
        when(userStatisticRepository.findBySchoolUserAndCategory(schoolUser, category))
                .thenReturn(Optional.of(userStatistic));

        Optional<UserStatistic> result = userStatisticService.getStatisticByUserAndCategory(schoolUser, category);
        assertTrue(result.isPresent());
        assertEquals("Yes", result.get().getIsExamPassed());
        verify(userStatisticRepository, times(1)).findBySchoolUserAndCategory(schoolUser, category);

    }

    @Test
    void testCreateUserStatistic() {
        when(userStatisticRepository.save(userStatistic)).thenReturn(userStatistic);

        UserStatistic result = userStatisticService.createUserStatistic(userStatistic);
        assertNotNull(result);
        assertEquals("Yes", result.getIsExamPassed());
        verify(userStatisticRepository, times(1)).save(userStatistic);
    }

    @Test
    void testUpdateUserStatistic() {
        UserStatistic updatedDetails = new UserStatistic();
        updatedDetails.setDeposits(200L);
        updatedDetails.setIsExamPassed("No");
        updatedDetails.setHoursed("15");
        updatedDetails.setHoursBuyed("10");
        updatedDetails.setSchoolUser(schoolUser);
        updatedDetails.setCategory(category);

        when(userStatisticRepository.findById(1L)).thenReturn(Optional.of(userStatistic));
        when(userStatisticRepository.save(userStatistic)).thenReturn(userStatistic);

        UserStatistic result = userStatisticService.updateUserStatistic(1L, updatedDetails);
        assertEquals(200L, result.getDeposits());
        assertEquals("No", result.getIsExamPassed());
        verify(userStatisticRepository, times(1)).findById(1L);
        verify(userStatisticRepository, times(1)).save(userStatistic);
    }

    @Test
    void testDeleteUserStatistic() {
        when(userStatisticRepository.findById(1L)).thenReturn(Optional.of(userStatistic));
        doNothing().when(userStatisticRepository).delete(userStatistic);

        userStatisticService.deleteUserStatistic(1L);
        verify(userStatisticRepository, times(1)).findById(1L);
        verify(userStatisticRepository, times(1)).delete(userStatistic);
    }

    @Test
    void testUpdateUserStatistic_NotFound() {
        when(userStatisticRepository.findById(1L)).thenReturn(Optional.empty());

        UserStatistic updatedDetails = new UserStatistic();
        assertThrows(ResourceNotFoundException.class, () -> {
            userStatisticService.updateUserStatistic(1L, updatedDetails);
        });

        verify(userStatisticRepository, times(1)).findById(1L);
        verify(userStatisticRepository, times(0)).save(any(UserStatistic.class));
    }
}
