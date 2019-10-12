package io.collegeplanner.my;

import com.amazonaws.services.lambda.runtime.Context;
import io.collegeplanner.my.repository.dto.RegistrationData;
import io.collegeplanner.my.service.RegistrationDataService;
import org.springframework.beans.factory.annotation.Autowired;

public class LambdaMethodHandler {

    public static void main(String[] args) {
        final LambdaMethodHandler lambdaMethodHandler = new LambdaMethodHandler();
        lambdaMethodHandler.handleRequest();
    }

    public void handleRequest() {
        final RegistrationDataService registrationDataService = new RegistrationDataService();
        registrationDataService.updateRegistrationData();
    }
}
