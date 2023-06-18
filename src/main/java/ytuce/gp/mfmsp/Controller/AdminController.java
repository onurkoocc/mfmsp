package ytuce.gp.mfmsp.Controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ytuce.gp.mfmsp.Entity.Representative;
import ytuce.gp.mfmsp.Pojo.ConversationPojo;
import ytuce.gp.mfmsp.Pojo.RepresentativePojo;
import ytuce.gp.mfmsp.Repository.AdminRepository;
import ytuce.gp.mfmsp.Service.ExternalService.AdminService;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/admin")
@AllArgsConstructor
@EnableAutoConfiguration
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    AdminService adminService;

    @Autowired
    AdminRepository adminRepository;

    @GetMapping("/getrepresentativesstatistics")
    public ResponseEntity getRepresentativesStatistics() {
        return ResponseEntity.ok(adminService.getRepresentativeStatistics());
    }
}
