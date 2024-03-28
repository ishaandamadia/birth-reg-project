package digit.repository;

import digit.repository.querybuilder.BirthApplicationQueryBuilder;
import digit.repository.rowmapper.BirthApplicationRowMapper;
import digit.web.models.BirthApplicationSearchCriteria;
import digit.web.models.BirthRegistrationApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Repository
public class BirthRegistrationRepository {

    @Autowired
    private BirthApplicationQueryBuilder queryBuilder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BirthApplicationRowMapper rowMapper;

    public List<BirthRegistrationApplication> getApplications(BirthApplicationSearchCriteria searchCriteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getBirthApplicationSearchQuery(searchCriteria, preparedStmtList);
        log.info("Final query: {}", query);
        try {
            return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
        } catch (DataAccessException e) {
            log.error("Error occurred while executing database query: {}", e.getMessage());
            throw e;
        }
    }
}
