package com.project.buylist.buylist;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.format.annotation.DateTimeFormat;

@RestController
@RequestMapping("/api/buylist")
public class BuyListController {

    @Autowired
    private BuyListService service;

    @PostMapping("")
    public ResponseEntity<BuyList> create(@Validated @RequestBody BuyList buyList, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        BuyList saved = service.save(buyList, userId);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuyList> getById(@PathVariable("id") Long id, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return service.getById(id, userId).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id,
            @Validated @RequestBody BuyList buyList, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return service.getById(id, userId)
                .map(existing -> {
                    buyList.setId(id);
                    BuyList updated = service.save(buyList, userId);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        if (service.getById(id, userId).isPresent()) {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("")
    public ResponseEntity<Page<BuyList>> search(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "createdFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdFrom,
            @RequestParam(name = "createdTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTo,
            @RequestParam(name = "updatedFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updatedFrom,
            @RequestParam(name = "updatedTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updatedTo,
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        var filter = BuyListFilterDto.builder()
                .name(name)
                .createdFrom(createdFrom)
                .createdTo(createdTo)
                .updatedFrom(updatedFrom)
                .updatedTo(updatedTo)
                .userId(userId)
                .build();
        Page<BuyList> results = service.search(filter);
        return ResponseEntity.ok(results);
    }
}
