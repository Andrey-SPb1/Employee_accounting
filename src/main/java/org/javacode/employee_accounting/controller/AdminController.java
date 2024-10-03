package org.javacode.employee_accounting.controller;

import lombok.RequiredArgsConstructor;
import org.javacode.employee_accounting.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final EmployeeService employeeService;

    @GetMapping("block/{userId}")
    public ResponseEntity<Boolean> isUserBlock(@PathVariable Long userId) {
        return ResponseEntity.ok(employeeService.userIsLocked(userId));
    }

    @PutMapping(value = "block/{userId}")
    public ResponseEntity<Boolean> updateUserBlock(@PathVariable Long userId, @RequestBody Boolean block) {
        employeeService.setBlock(userId, block);
        return ResponseEntity.ok(employeeService.userIsLocked(userId));
    }

}
