package yu.likelion14th.allligo_was.domains.dashboard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import yu.likelion14th.allligo_was.domains.dashboard.api.DashboardAPI;
import yu.likelion14th.allligo_was.domains.dashboard.service.DashboardService;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
public class DashboardController implements DashboardAPI {

    private final DashboardService dashboardService;

    @Override
    @GetMapping("/statistics")
    public ResponseEntity<?> getDashboardStatistics(
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(dashboardService.getStatistics(userId, startDate, endDate));
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}