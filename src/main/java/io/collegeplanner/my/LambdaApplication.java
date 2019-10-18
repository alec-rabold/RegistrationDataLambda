package io.collegeplanner.my;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import io.collegeplanner.my.service.RegistrationDataService;

public class LambdaApplication {
    public static void handleRequest(final Context context) {
        final LambdaLogger logger = context.getLogger();
        try {
            RegistrationDataService.updateRegistrationData();
        } catch(final Exception e) {
            logger.log(e.toString());
        }
        logger.log("Course registration data updated.");
    }
}
