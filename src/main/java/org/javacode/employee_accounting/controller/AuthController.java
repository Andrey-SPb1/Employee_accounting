package org.javacode.employee_accounting.controller;

import lombok.RequiredArgsConstructor;
import org.javacode.employee_accounting.model.dto.EmployeeSignInDto;
import org.javacode.employee_accounting.model.dto.create.EmployeeCreateEditDto;
import org.javacode.employee_accounting.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody EmployeeCreateEditDto employee){
        return ResponseEntity.ok(authService.signUp(employee));
    }
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody EmployeeSignInDto employee) throws Exception {
        return ResponseEntity.ok(authService.signIn(employee));
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody EmployeeSignInDto employee){
        return ResponseEntity.ok(authService.refreshToken(employee));
    }
}
