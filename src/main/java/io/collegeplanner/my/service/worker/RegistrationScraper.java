package io.collegeplanner.my.service.worker;

import io.collegeplanner.my.repository.dto.RegistrationData;
import io.collegeplanner.my.repository.schema.CoursesDto;
import io.collegeplanner.my.repository.schema.ProfessorsDto;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static io.collegeplanner.my.util.Constants.REGISTRATION_SEARCH_PAGE_SDSU;

@Getter
/** VERY old - could use lots of refactoring */
public class RegistrationScraper {
    private Map<String, List<Course>> departments = new TreeMap<>();
    private URL Registration_URL;
    protected String parameters;
    private int sectionMeetingCounter = 0;

    @Getter
    protected class Course {
        String courseID;
        String schedNumber;
        String title;
        String units;
        String seats;
        List<String> locations = new ArrayList<>();
        List<String> times = new ArrayList<>();
        List<String> instructors = new ArrayList<>();
        List<String> days = new ArrayList<>();
        public boolean isComplete() {
            return (courseID != null && schedNumber != null && title != null && units != null &&
                    seats != null && !times.isEmpty());
        }
    }

    public void iterateAll() throws Exception {

        DepartmentScraper departments = new DepartmentScraper();

        String department = "";
        for(Map.Entry<String, String> entry : departments.getDepartmentMap().entrySet() ) {
            department = entry.getKey();
            this.parseDepartmentHTML(department);
        }
    }

    public void parseDepartmentHTML(String department) throws Exception {

        setDepartmentSearch(department);

        Course temp = new Course();
        List<Course> tempList = new ArrayList<Course>();

        System.out.println(Registration_URL.toString());


        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(Registration_URL.openStream()));

            String inputLine, value;

            while((inputLine = in.readLine()) != null) {

                updateCount(inputLine);


                if(inputLine.contains("<a href=\"sectiondetails") && !inputLine.contains("footnote")) {
                    // check that class has all attributes, then add the course & reset it
                    // I put it here so that it only adds the course once it finds the next one
                    if(temp.isComplete()) {
                        tempList.add(temp);
                        temp = new Course();
                    }
                    else {
                        temp = new Course();
                    }

                    int indexStart = inputLine.indexOf("\">") + 2;
                    int indexEnd = inputLine.indexOf("</a>");
                    value = inputLine.substring(indexStart, indexEnd);
                    temp.courseID = value;
                }

                else if(inputLine.contains("sectionFieldSec")) {
                    value = parseSection(inputLine);
                    //if(!value.equals("Sec"))
                    // this.section.add(value);
                }

                else if(inputLine.contains("sectionFieldSched")) {
                    value = parseSection(inputLine);
                    if(!value.equals("Sched #") && (value.matches(".*\\d+.*") || value.contains("***")))
                        temp.schedNumber = value;
                    //this.scheduleNumber.add(value);
                }

                else if(inputLine.contains("sectionFieldTitle")) {
                    value = parseSection(inputLine);
                    if(!value.equals("Course Title") && value.matches(".*[a-zA-Z]+.*"))
                        temp.title = value;
                    //this.courseTitle.add(value);
                }
                else if(inputLine.contains("sectionFieldUnits")) {
                    value = parseSection(inputLine);
                    if(!value.equals("Units") && value.matches(".*\\d+.*"))
                        temp.units = value;
                    //this.units.add(value);
                }
                else if(inputLine.contains("sectionFieldType")) {
                    value = parseSection(inputLine);
                    //if(!value.equals("Format"))
                    // this.format.add(value);
                }
                else if(inputLine.contains("sectionFieldTime")) {
                    value = parseSection(inputLine);
                    if((!value.equals("Time")) && value.matches(".*\\d+.*")){
                        temp.times.add(value);
                    }
                }
                else if(inputLine.contains("sectionFieldDay")) {
                    value = parseSection(inputLine);
                    if(!value.equals("Day") && value.matches(".*[a-zA-Z]+.*") && !value.equals("Arranged"))
                        temp.days.add(value);
                    //this.day.add(value);
                }
                else if(inputLine.contains("sectionFieldLocation") && !inputLine.contains(">Location<")) {
                    if((inputLine = in.readLine()).contains("<a")){
                        updateCount(inputLine);
                        inputLine = in.readLine(); // the HTML data is on the 3rd line due to inconsistent formatting on WebPortal
                        updateCount(inputLine);
                        value = inputLine.trim();
                        temp.locations.add(value);
                    }
                    else {
                        inputLine = in.readLine();
                        updateCount(inputLine);
                        value = inputLine.trim();
                        temp.locations.add(value);

                    }
                }
                else if(inputLine.contains("sectionFieldSeats") && !inputLine.contains(">Seats Open<")) {
                    boolean seatsFound = false;
                    while(!seatsFound) {
                        inputLine = in.readLine();
                        if(inputLine.contains("Waitlisted")) {
                            inputLine = inputLine.trim();
                            int indexStart = 0;
                            int indexEnd = inputLine.indexOf("<br>");
                            value = inputLine.substring(indexStart, indexEnd);
                            temp.seats = value;
                            seatsFound = true;
                        }
                        else if(inputLine.contains("/") && !(inputLine.contains("<"))) {
                            value = inputLine.trim();
                            temp.seats = value;
                            seatsFound = true;
                        }
                    }
                }

                else if(inputLine.contains("sectionFieldInstructor") && !inputLine.contains(">Instructor<")) {
                    for(int i = 0; i < 3; i++) {
                        inputLine = in.readLine();
                        updateCount(inputLine);
                        if(inputLine.contains("<a href=\"search?mode=search&instructor")) {
                            int indexStart = inputLine.indexOf("\">") + 2;
                            int indexEnd = inputLine.indexOf("</a>");
                            value = inputLine.substring(indexStart, indexEnd);
                            if(!value.equals("Instructor") && value.matches(".*[a-zA-Z]+.*")) {
                                //this.instructor.add(value);
                                temp.instructors.add(value);
                            }
                        }
                    }
                }
            }
        }
        catch(NullPointerException e) {
            System.out.print("  <-- No courses for this department in selected ");
        }

        // **NEW**
        if(!departments.containsKey(department))
            departments.put(department, tempList);
        else {
            List<Course> newList = new ArrayList<>(departments.get(department));
            newList.addAll(tempList);
            departments.put(department, newList);
        }
    }

    public void updateCount(String inputLine) {

        // Accounts for courses with a Lecture and Activity class
        if(inputLine.contains("sectionRecordEven") || inputLine.contains("sectionRecordOdd")) {
            this.sectionMeetingCounter = 0;
        }

        // Handles multiple locations, teachers, times, etc. per one class
        if(inputLine.contains("sectionMeeting")) {
            this.sectionMeetingCounter++;
            if((this.sectionMeetingCounter) >= 2) {
                // this.course.add("");
                // this.seatsOpen.add("");
            }
        }
    }

    public String parseSection(String inputLine) {
        int indexStart = inputLine.indexOf("column\">") + 8;
        int indexEnd = inputLine.indexOf("</div>");
        String value = inputLine.substring(indexStart, indexEnd);
        return value;
    }

    public void setDepartmentSearch(String department) throws MalformedURLException {
        String formURL = REGISTRATION_SEARCH_PAGE_SDSU + "&abbrev=" + department;
        if(this.parameters != null) {
            formURL += parameters;
        }
        final String searchURL = formatURL(formURL);
        this.Registration_URL = new URL(searchURL);
    }

    public String formatURL(String url) {
        StringBuilder newURL = new StringBuilder();

        for(int i = 0; i < url.length(); i++) {
            if(url.charAt(i) == ' ')
                newURL.append('+');
            else
                newURL.append(url.charAt(i));
        }
        return newURL.toString();
    }

    public RegistrationData getRegistrationData() {
        final Set<CoursesDto> courses = new TreeSet<>();
        final Set<ProfessorsDto> professors = new TreeSet<>();

        for (final String dept: departments.keySet()){

            /** Get and remove duplicates */

            final List<Course> deptCourseList = departments.get(dept);
            for(Course crs : deptCourseList) {
                courses.add(new CoursesDto(crs.courseID, crs.title, crs.courseID));
                for(int k = 0; k < crs.instructors.size(); k++){
                    // A. Narang --> Narang, A.
                    final String name = crs.instructors.get(k).substring(3) + ", " + crs.instructors.get(k).substring(0, 3).toUpperCase();
                    professors.add(new ProfessorsDto(name, name));
                }
            }
        }
        return new RegistrationData(courses, professors);
    }

    public void setTerm(String season, String year) {
        String query = "&period=";
        checkIfPresent(query);

        String seasonNumber = "";
        switch (season) {
            case "Winter":
                seasonNumber = "1";
                break;
            case "Spring":
                seasonNumber = "2";
                break;
            case "Summer":
                seasonNumber = "3";
                break;
            case "Fall":
                seasonNumber = "4";
                break;
        }
        String addParam = query + year + seasonNumber;
        appendParameter(addParam);
    }

    public void checkIfPresent(String query) {
        int startIndex = 0, endIndex = 0;
        boolean nextParamFound = false;
        if(this.parameters != null) {
            if(this.parameters.contains(query)) {
                StringBuilder removeParam = new StringBuilder(this.parameters);
                startIndex = this.parameters.indexOf(query);
                for(int i = startIndex + 1; i < this.parameters.length(); i++) {
                    if(this.parameters.charAt(i) == '&') {
                        endIndex = i;
                        nextParamFound = true;
                    }
                }
                if(!nextParamFound) {
                    endIndex = this.parameters.length();
                }
                this.parameters = removeParam.delete(startIndex, endIndex).toString();
            }
        }
    }
    public void appendParameter(String addParam) {
        if(parameters != null)
            this.parameters = parameters + addParam;
        else
            this.parameters = addParam;
    }

    public void iterateTermsForYear(final String year) {
        try {
            this.setTerm("Spring", year);
            this.iterateAll();
            this.setTerm("Fall", year);
            this.iterateAll();
        }
        catch(final Exception e) {
            System.out.println("ERROR"); // ehh
        }
    }
}
