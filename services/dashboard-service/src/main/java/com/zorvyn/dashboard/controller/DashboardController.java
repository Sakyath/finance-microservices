package com.zorvyn.dashboard.controller;

import com.zorvyn.common.model.response.ApiResponse;
import com.zorvyn.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard Analytics")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    @Operation(summary = "Total income, expenses and net balance")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSummary() {
        return ResponseEntity.ok(ApiResponse.success(service.getSummary()));
    }

    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    @Operation(summary = "Category-wise totals")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCategoryTotals() {
        return ResponseEntity.ok(ApiResponse.success(service.getCategoryTotals()));
    }

    @GetMapping("/trends")
    @PreAuthorize("hasAnyRole('ANALYST','ADMIN')")
    @Operation(summary = "Monthly income/expense trends (last 6 months)")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMonthlyTrends() {
        return ResponseEntity.ok(ApiResponse.success(service.getMonthlyTrends()));
    }

    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    @Operation(summary = "10 most recent financial activities")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getRecentActivity() {
        return ResponseEntity.ok(ApiResponse.success(service.getRecentActivity()));
    }
}
