package io.collegeplanner.my.repository.dto;

import io.collegeplanner.my.repository.schema.CoursesDto;
import io.collegeplanner.my.repository.schema.ProfessorsDto;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class RegistrationData {
    final Set<CoursesDto> courses;
    final Set<ProfessorsDto> professors;
}
