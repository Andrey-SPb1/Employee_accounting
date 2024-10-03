package org.javacode.employee_accounting.service;

import org.javacode.employee_accounting.model.dto.EmployeeSignInDto;
import org.javacode.employee_accounting.model.dto.create.EmployeeCreateEditDto;

public interface AuthService {

    String signUp(EmployeeCreateEditDto employee);

    String signIn(EmployeeSignInDto employee) throws Exception;

    String refreshToken(EmployeeSignInDto employee);
}
