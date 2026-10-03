package edu.cit.tapales.saritrack.core.system;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping({"/", "/api/health"})
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "SariTrack API");
        response.put("environment", "Production / Cloud");
        response.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(response);
    }
}
