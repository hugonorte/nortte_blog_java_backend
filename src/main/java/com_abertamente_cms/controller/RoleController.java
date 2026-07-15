package com_abertamente_cms.controller;

import com_abertamente_cms.domain.UserRole;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @GetMapping
    public ResponseEntity<List<Map<String, String>>> getRoles() {
        List<Map<String, String>> roles = Arrays.stream(UserRole.values())
                .map(role -> Map.of(
                        "id", role.name(),
                        "name", role.getDescription()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(roles);
    }
}
