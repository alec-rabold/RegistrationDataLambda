package io.collegeplanner.my.service.worker;

import java.net.*;
import java.util.*;
import java.io.*;

/** VERY old - could use lots of refactoring */
public class DepartmentScraper {
    final String DEPARTMENTS_PAGE = "https://sunspot.sdsu.edu/schedule/search?mode=browse_by_department&category=browse_by_department";
    private URL Dept_URL;
    private TreeMap<String, String> departmentMap;

    public DepartmentScraper() {
        try {
            Dept_URL = new URL(DEPARTMENTS_PAGE);
            departmentMap = this.retrieveDepartments();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public TreeMap<String,String> retrieveDepartments() throws IOException {
        final TreeMap<String, String> deptMap = new TreeMap<>();
        final BufferedReader in = new BufferedReader(new InputStreamReader(Dept_URL.openStream()));
        String deptAbbrev, deptFull, inputLine;

        while((inputLine = in.readLine()) != null) {
            // "subjectCode" == Department Abbreviation
            int indexStart = inputLine.indexOf("subjectCode") + 13; // where the text starts in the HTML
            int indexEnd = inputLine.indexOf(":</div>"); // where the HTML text ends
            if(indexStart - 13 != -1) { // -1 indicates not found, subtracting the 13 above
                deptAbbrev = inputLine.substring(indexStart, indexEnd);

                // "subjectTitle" == Department Abbreviation   
                indexStart = inputLine.indexOf("subjectTitle") + 14;
                indexEnd = inputLine.indexOf("</div></a>");
                deptFull = inputLine.substring(indexStart, indexEnd);

                // add both values to the TreeMap
                deptMap.put(deptAbbrev, deptFull);
            }
        }
        return deptMap;
    }

    public TreeMap<String, String> getDepartmentMap() {
        return this.departmentMap;
    }
}