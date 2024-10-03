package org.javacode.employee_accounting.service.impl;

import lombok.RequiredArgsConstructor;
import org.javacode.employee_accounting.exception.BlockedAccountException;
import org.javacode.employee_accounting.model.dto.EmployeeSignInDto;
import org.javacode.employee_accounting.model.dto.create.EmployeeCreateEditDto;
import org.javacode.employee_accounting.model.entity.Employee;
import org.javacode.employee_accounting.repository.EmployeeRepository;
import org.javacode.employee_accounting.security.JwtUtil;
import org.javacode.employee_accounting.service.AuthService;
import org.javacode.employee_accounting.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    public String signUp(EmployeeCreateEditDto employee) {
        employeeService.create(employee);
        UserDetails userDetails = employeeService.loadUserByUsername(employee.username());
        return jwtUtil.generateJwtToken(userDetails);
    }

    public String signIn(EmployeeSignInDto employeeDto) throws Exception {

        UserDetails userDetails = employeeService.loadUserByUsername(employeeDto.username());

        if (!userDetails.isAccountNonLocked()) {
            throw new Exception("User account is locked");
        }

        Employee employee = employeeRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();
        try {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(),
                    userDetails.getPassword());

            authenticationManager.authenticate(authentication);

            employee.setFailedLoginAttempts(0);
            employeeRepository.flush();

            return jwtUtil.generateRefreshJwtToken(new HashMap<>(), userDetails);
        } catch (AuthenticationException e) {
            employee.setFailedLoginAttempts(employee.getFailedLoginAttempts() + 1);

            if (employee.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                employee.setAccountNonLocked(false);
                logger.warn(String.format("%s account is locked", employee.getUsername()));
                throw new BlockedAccountException("User account is locked due to too many failed login attempts");
            }
            employeeRepository.flush();

            throw new Exception("Invalid username or password", e);
        }
    }

    @Override
    public String refreshToken(EmployeeSignInDto employee) {
        UserDetails userDetails = employeeService.loadUserByUsername(employee.username());
        return jwtUtil.generateJwtToken(userDetails);
    }
}
