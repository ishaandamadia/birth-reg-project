package digit.repository;

import digit.repository.querybuilder.BirthApplicationQueryBuilder;
import digit.repository.rowmapper.BirthApplicationRowMapper;
import digit.web.models.BirthApplicationSearchCriteria;
import digit.web.models.BirthRegistrationApplication;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BirthRegistrationRepositoryTest {

    @Mock
    private BirthApplicationQueryBuilder queryBuilder;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private BirthApplicationRowMapper rowMapper;

    @InjectMocks
    private BirthRegistrationRepository repository;

    public BirthRegistrationRepositoryTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetApplications_WithValidCriteria() {
        // Arrange
        BirthApplicationSearchCriteria searchCriteria = new BirthApplicationSearchCriteria();
        List<BirthRegistrationApplication> expectedList = new ArrayList<>();
        when(queryBuilder.getBirthApplicationSearchQuery(searchCriteria, new ArrayList<>())).thenReturn("SELECT * FROM applications WHERE ...");
        when(jdbcTemplate.query(anyString(), any(Object[].class), any(BirthApplicationRowMapper.class))).thenReturn(expectedList);

        // Act
        List<BirthRegistrationApplication> actualList = repository.getApplications(searchCriteria);

        // Assert
        assertEquals(expectedList, actualList);
        verify(queryBuilder, times(1)).getBirthApplicationSearchQuery(searchCriteria, new ArrayList<>());
        verify(jdbcTemplate, times(1)).query(anyString(), any(Object[].class), any(BirthApplicationRowMapper.class));
    }

    @Test
    void testGetApplications_WithNullCriteria() {
        // Arrange
        BirthApplicationSearchCriteria searchCriteria = null;
        List<BirthRegistrationApplication> expectedList = new ArrayList<>();
        when(queryBuilder.getBirthApplicationSearchQuery(null, new ArrayList<>())).thenReturn("SELECT * FROM applications WHERE ...");
        when(jdbcTemplate.query(anyString(), any(Object[].class), any(BirthApplicationRowMapper.class))).thenReturn(expectedList);

        // Act
        List<BirthRegistrationApplication> actualList = repository.getApplications(searchCriteria);

        // Assert
        assertEquals(expectedList, actualList);
        verify(queryBuilder, times(1)).getBirthApplicationSearchQuery(null, new ArrayList<>());
        verify(jdbcTemplate, times(1)).query(anyString(), any(Object[].class), any(BirthApplicationRowMapper.class));
    }

}
