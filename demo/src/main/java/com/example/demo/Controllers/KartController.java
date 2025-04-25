package com.example.demo.Controllers;

import com.example.demo.Entities.KartEntity;
import com.example.demo.Services.KartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/kart")
@CrossOrigin("*")
public class KartController {

    @Autowired
    KartService kartService;

    @PostMapping("/save")
    public ResponseEntity<KartEntity> saveKart(@RequestBody KartEntity kart) {
        KartEntity savedKart = kartService.saveKart(kart);
        return ResponseEntity.ok(savedKart);
    }

    @GetMapping("/available/{availability}")
    public ResponseEntity<List<KartEntity>> getKartsByAvailability(@PathVariable boolean availability) {
        List<KartEntity> karts = kartService.getKartsByAvailability(availability);
        return ResponseEntity.ok(karts);
    }

    @PutMapping("/update")
    public ResponseEntity<KartEntity> updateKart(@RequestBody KartEntity kart) {
        KartEntity updatedKart = kartService.updateKart(kart);
        return ResponseEntity.ok(updatedKart);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteKart(@PathVariable Long id) throws Exception {
        boolean isDeleted = kartService.deleteKart(id);
        return ResponseEntity.ok(isDeleted);
    }

}
