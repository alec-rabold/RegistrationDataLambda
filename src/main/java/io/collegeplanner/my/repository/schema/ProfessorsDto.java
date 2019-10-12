package io.collegeplanner.my.repository.schema;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfessorsDto implements Comparable<ProfessorsDto> {
    private String name;
    private String value;

    @Override
    public int compareTo(final ProfessorsDto other) {
        return this.name.compareTo(other.name);
    }
}