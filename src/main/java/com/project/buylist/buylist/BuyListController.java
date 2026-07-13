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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.format.annotation.DateTimeFormat;

@RestController
@RequestMapping("/api/buylist")
@Tag(name = "Buy List API", description = "API for managing buy lists")
public class BuyListController {

    @Autowired
    private BuyListService service;

    @PostMapping("")
    @Operation(summary = "Create a new buy list", description = "Creates a new buy list for the authenticated user", security = {
            @SecurityRequirement(name = "bearer-key") })
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Buy list created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })

    public ResponseEntity<BuyList> create(@Validated @RequestBody BuyList buyList, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        BuyList saved = service.save(buyList, userId);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get buy list by ID", description = "Retrieves a buy list by its ID for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Buylist retrieved sucessfully"),
            @ApiResponse(responseCode = "400", description = "No buylist with specified identifier found"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<BuyList> getById(@PathVariable("id") Long id, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return service.getById(id, userId).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update buy list", description = "Updates existing buy list by ID (user authenticated)")
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
    @Operation(summary = "Delete buy list", description = "Deletes a buy list by its ID (user authenticated)")
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
    @Operation(summary = "Search buy lists", description = "Searches for buy lists based on specified criteria. Only returns buy lists created by the authenticated user.")
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
