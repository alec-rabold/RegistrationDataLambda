package io.collegeplanner.my.repository.schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorsDto implements Comparable<ProfessorsDto> {
    private String name;
    private String value;

    @Override
    public int compareTo(final ProfessorsDto other) {
        return this.name.compareToIgnoreCase(other.name);
    }
}