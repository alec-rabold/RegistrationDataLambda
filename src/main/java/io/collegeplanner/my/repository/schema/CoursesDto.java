package io.collegeplanner.my.repository.schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoursesDto implements Comparable<CoursesDto> {
    private String id;
    private String name;
    private String title;

    @Override
    public int compareTo(final CoursesDto other) {
        return this.id.compareTo(other.id);
    }
}
