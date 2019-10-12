package io.collegeplanner.my.service;

import io.collegeplanner.my.service.worker.RegistrationScraper;
import io.collegeplanner.my.repository.RegistrationDataDao;
import io.collegeplanner.my.repository.dto.RegistrationData;
import io.collegeplanner.my.repository.schema.CoursesDto;
import io.collegeplanner.my.repository.schema.ProfessorsDto;
import io.collegeplanner.my.util.DatabaseUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class RegistrationDataService {

    final static String COURSES_TABLE_SDSU = "course_registration_data_SDSU";
    final static String PROFESSORS_TABLE_SDSU = "professors_SDSU";

    public void updateRegistrationData() {
        final RegistrationScraper scraper = new RegistrationScraper();
        Stream.iterate(Calendar.getInstance().get(Calendar.YEAR) - 1, year -> year + 1)
                .limit(3)
                .forEach(year -> scraper.iterateTermsForYear(Integer.toString(year)));
        final RegistrationData registrationData = scraper.getRegistrationData();
        updateCoursesTable(registrationData);
        updateProfessorsTable(registrationData);
    }

    private void updateCoursesTable(final RegistrationData registrationData) {
        final List<CoursesDto> oldCourses = DatabaseUtils.getDatabaseConnection().onDemand(RegistrationDataDao.class)
                .getCoursesFromTable(COURSES_TABLE_SDSU);
        final Set<CoursesDto> newCourses = registrationData.getCourses();
        newCourses.addAll(oldCourses);
        DatabaseUtils.getDatabaseConnection().onDemand(RegistrationDataDao.class)
            .updateCoursesTableBulk(COURSES_TABLE_SDSU, newCourses);
    }

    private void updateProfessorsTable(final RegistrationData registrationData) {
        final List<ProfessorsDto> oldProfessors = DatabaseUtils.getDatabaseConnection().onDemand(RegistrationDataDao.class)
                .getProfessorsFromTable(PROFESSORS_TABLE_SDSU);
        final Set<ProfessorsDto> newProfessors = registrationData.getProfessors();
        newProfessors.addAll(oldProfessors);
        DatabaseUtils.getDatabaseConnection().onDemand(RegistrationDataDao.class)
                .updateProfessorsTableBulk(PROFESSORS_TABLE_SDSU, newProfessors);
    }
}
