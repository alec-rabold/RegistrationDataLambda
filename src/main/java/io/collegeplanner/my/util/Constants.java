package io.collegeplanner.my.util;

public class Constants {
    public static final String REGISTRATION_SEARCH_PAGE_SDSU = "https://sunspot.sdsu.edu/schedule/search?mode=search";

    public static final String TABLE_NAME = "table";
    public static final String COURSES_TABLE_SDSU = "course_registration_data_SDSU";
    public static final String PROFESSORS_TABLE_SDSU = "professors_SDSU";
    public static final String PREPARED_STATEMENT_SELECT_ALL_FROM_TABLE = "SELECT * FROM <table>";
    public static final String PREPARED_STATEMENT_UPDATE_COURSES_TABLE = "INSERT INTO <table>(c_name, c_title, c_id) VALUES (:name, :title, :id) ON DUPLICATE KEY UPDATE c_title = VALUES(c_title)";
    public static final String PREPARED_STATEMENT_UPDATE_PROFESSORS_TABLE = "INSERT INTO <table>(p_name, p_value) VALUES (:name, :value) ON DUPLICATE KEY UPDATE p_value = VALUES(p_value)";

}
