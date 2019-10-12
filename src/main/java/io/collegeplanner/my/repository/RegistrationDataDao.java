package io.collegeplanner.my.repository;

import io.collegeplanner.my.repository.schema.CoursesDto;
import io.collegeplanner.my.repository.schema.ProfessorsDto;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.List;
import java.util.Set;

import static io.collegeplanner.my.util.Constants.*;

@UseStringTemplate3StatementLocator
public interface RegistrationDataDao {

    @SqlUpdate(PREPARED_STATEMENT_SELECT_ALL_FROM_TABLE)
    @RegisterBeanMapper(value = ProfessorsDto.class, prefix = "p")
    List<ProfessorsDto> getProfessorsFromTable(@Define(TABLE_NAME) final String tableName);

    @SqlQuery(PREPARED_STATEMENT_SELECT_ALL_FROM_TABLE)
    @RegisterBeanMapper(value = CoursesDto.class, prefix = "c")
    List<CoursesDto> getCoursesFromTable(@Define("table") final String tableName);

    @SqlBatch(PREPARED_STATEMENT_UPDATE_COURSES_TABLE)
    void updateCoursesTableBulk(@Define("table") final String tableName,
                                @BindBean("course") final Set<CoursesDto> courses);

    @SqlBatch(PREPARED_STATEMENT_UPDATE_PROFESSORS_TABLE)
    void updateProfessorsTableBulk(@Define("table") final String tableName,
                                   @BindBean("professor") final Set<ProfessorsDto> professors);

}

