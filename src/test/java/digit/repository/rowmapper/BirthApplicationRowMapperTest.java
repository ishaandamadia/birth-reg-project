package digit.repository.rowmapper;

import digit.web.models.Address;
import digit.web.models.BirthRegistrationApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BirthApplicationRowMapperTest {

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private BirthApplicationRowMapper rowMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testExtractData_WithResultSetContainingData_ShouldReturnList() throws SQLException {
        // Arrange
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString("bapplicationnumber")).thenReturn("123");
        when(resultSet.getString("btenantid")).thenReturn("tenant123");
        when(resultSet.getString("bid")).thenReturn("1");
        when(resultSet.getString("bbabyfirstname")).thenReturn("John");
        when(resultSet.getString("bbabylastname")).thenReturn("Doe");
        when(resultSet.getString("bfatherid")).thenReturn("father123");
        when(resultSet.getString("bmotherid")).thenReturn("mother123");
        when(resultSet.getString("bdoctorname")).thenReturn("Dr. Smith");
        when(resultSet.getString("bhospitalname")).thenReturn("Hospital A");
        when(resultSet.getString("bplaceofbirth")).thenReturn("City A");
        when(resultSet.getInt("btimeofbirth")).thenReturn(8);
        when(resultSet.getString("bcreatedBy")).thenReturn("user123");
        when(resultSet.getLong("bcreatedTime")).thenReturn(123456L);
        when(resultSet.getString("blastModifiedBy")).thenReturn("user456");
        when(resultSet.getLong("blastModifiedTime")).thenReturn(123457L);
        when(resultSet.getString("aid")).thenReturn("2");
        when(resultSet.getString("atenantid")).thenReturn("tenant123");
        when(resultSet.getString("adoorno")).thenReturn("123");
        when(resultSet.getDouble("alatitude")).thenReturn(1.0);
        when(resultSet.getDouble("alongitude")).thenReturn(2.0);
        when(resultSet.getString("abuildingname")).thenReturn("Building A");
        when(resultSet.getString("aaddressid")).thenReturn("address123");
        when(resultSet.getString("aaddressnumber")).thenReturn("456");
        when(resultSet.getString("atype")).thenReturn("Type A");
        when(resultSet.getString("aaddressline1")).thenReturn("Address Line 1");
        when(resultSet.getString("aaddressline2")).thenReturn("Address Line 2");
        when(resultSet.getString("alandmark")).thenReturn("Landmark A");
        when(resultSet.getString("astreet")).thenReturn("Street A");
        when(resultSet.getString("acity")).thenReturn("City A");
        when(resultSet.getString("apincode")).thenReturn("12345");
        when(resultSet.getString("adetail")).thenReturn("Detail A");
        when(resultSet.getString("aregistrationid")).thenReturn("registration123");

        // Act
        List<BirthRegistrationApplication> result = rowMapper.extractData(resultSet);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        BirthRegistrationApplication application = result.get(0);
        assertNotNull(application);
        assertEquals("123", application.getApplicationNumber());
        assertEquals("tenant123", application.getTenantId());
        assertEquals("1", application.getId());
        assertEquals("John", application.getBabyFirstName());
        assertEquals("Doe", application.getBabyLastName());
        assertNotNull(application.getFather());
        assertEquals("father123", application.getFather().getUuid());
        assertNotNull(application.getMother());
        assertEquals("mother123", application.getMother().getUuid());
        assertEquals("Dr. Smith", application.getDoctorName());
        assertEquals("Hospital A", application.getHospitalName());
        assertEquals("City A", application.getPlaceOfBirth());
        assertEquals(8, application.getTimeOfBirth());
        assertNotNull(application.getAuditDetails());
        assertEquals("user123", application.getAuditDetails().getCreatedBy());
        assertEquals(123456L, application.getAuditDetails().getCreatedTime());
        assertEquals("user456", application.getAuditDetails().getLastModifiedBy());
        assertEquals(123457L, application.getAuditDetails().getLastModifiedTime());
        assertNotNull(application.getAddress());
        assertEquals("2", application.getAddress().getId());
        assertEquals("tenant123", application.getAddress().getTenantId());
        assertNotNull(application.getAddress().getApplicantAddress());
        Address address = application.getAddress().getApplicantAddress();
        assertEquals("123", address.getDoorNo());
        assertEquals(1.0, address.getLatitude());
        assertEquals(2.0, address.getLongitude());
        assertEquals("Building A", address.getBuildingName());
        assertEquals("address123", address.getAddressId());
        assertEquals("456", address.getAddressNumber());
        assertEquals("Type A", address.getType());
        assertEquals("Address Line 1", address.getAddressLine1());
        assertEquals("Address Line 2", address.getAddressLine2());
        assertEquals("Landmark A", address.getLandmark());
        assertEquals("Street A", address.getStreet());
        assertEquals("City A", address.getCity());
        assertEquals("12345", address.getPincode());
        assertEquals("Detail A", address.getDetail());
        assertEquals("registration123", address.getRegistrationId());

        verify(resultSet, times(22)).getString(anyString());
        verify(resultSet, times(1)).next();
        verifyNoMoreInteractions(resultSet);
    }

    @Test
    void testExtractData_WithEmptyResultSet_ShouldReturnEmptyList() throws SQLException {
        when(resultSet.next()).thenReturn(false);

        List<BirthRegistrationApplication> result = rowMapper.extractData(resultSet);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}

