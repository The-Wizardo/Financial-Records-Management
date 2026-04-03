package com.FRM.Record;

import com.FRM.Auth.CustomUserDetails;
import com.FRM.Exception.ApiResponse;
import com.FRM.Exception.ApiResponseUtil;
import com.FRM.Exception.ResourceNotFoundException;
import com.FRM.User.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class RecordController {
    private final RecordRepository recordRepository;

    private User getUserByToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        assert userDetails != null;
        return userDetails.getUser();
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<?>> createRecord(Authentication authentication, @Valid @RequestBody RecordRequest request) {

        Record record = new Record();
        record.setNote(request.note());
        record.setCategory(request.category());
        record.setAmount(request.amount());
        record.setNote(request.note());
        record.setDate(request.date());
        record.setUser(getUserByToken(authentication));
        record.setType(request.type());
        recordRepository.save(record);

        Map<String, Object> data = Map.of(
                "amount", record.getAmount(),
                "category", record.getCategory(),
                "note", record.getNote(),
                "date", record.getDate()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseUtil.success("Created Successfully", data));
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<?>> getAllRecordsByUserId(Authentication authentication) {
        List<RecordResponse> data = recordRepository.findByUser_UserIdAndIsDeleted(getUserByToken(authentication).getUserId(), false);
        return ResponseEntity.ok().body(ApiResponseUtil.success("Success", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getRecordById(@PathVariable("id") Long id, Authentication authentication) {
        RecordResponse record = recordRepository.findByIdAndUser_UserIdAndIsDeletedFalse(id, getUserByToken(authentication).getUserId())
                .map(r -> new RecordResponse(r.getId(), r.getAmount(), r.getType(), r.getCategory(), r.getDate(), r.getNote(), r.isDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("No Record Found"));
        return ResponseEntity.ok(ApiResponseUtil.success("Data Found", record));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> softDeleteRecord(@PathVariable("id") Long id, Authentication authentication) {
        Record record = recordRepository.findByIdAndUser_UserIdAndIsDeletedFalse(id, getUserByToken(authentication).getUserId()).orElseThrow(() -> new ResourceNotFoundException("No Record Found"));
        record.setDeleted(true);
        recordRepository.save(record);
        return ResponseEntity.ok(ApiResponseUtil.success("Record deleted successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateTheRecord(@Valid @RequestBody RecordRequest request, @PathVariable("id") Long id, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        assert userDetails != null;

        Record record = recordRepository.findByIdAndUser_UserIdAndIsDeletedFalse(id, getUserByToken(authentication).getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("No Data Found"));
        record.setNote(request.note());
        record.setDate(request.date());
        record.setAmount(request.amount());
        record.setType(request.type());
        recordRepository.save(record);

        RecordResponse response = new RecordResponse(
                record.getId(),
                record.getAmount(),
                record.getType(),
                record.getCategory(),
                record.getDate(),
                record.getNote(),
                record.isDeleted()
        );
        return ResponseEntity.ok().body(ApiResponseUtil.success("update successfully", response));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    public ResponseEntity<?> getRecords(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) RecordType type,
            @RequestParam(required = false) BigDecimal amount,
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "category",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable,
            Authentication authentication)
    {

        boolean hasFullAccess = authentication.getAuthorities().stream()
                .anyMatch(a ->
                        Objects.equals(a.getAuthority(), "ROLE_ADMIN") ||
                                Objects.equals(a.getAuthority(), "ROLE_ANALYST")
                );

        Long userId = hasFullAccess ? null : getUserByToken(authentication).getUserId();

        Page<Record> page = recordRepository.findRecords(
                userId,
                type,
                search,
                amount,
                pageable
        );

        List<RecordResponse> data = page.getContent().stream()
                .map(r -> new RecordResponse(
                        r.getId(),
                        r.getAmount(),
                        r.getType(),
                        r.getCategory(),
                        r.getDate(),
                        r.getNote(),
                        r.isDeleted()
                ))
                .toList();

        Map<String, Object> response = Map.of(
                "content", data,
                "page", page.getNumber(),
                "size", page.getSize(),
                "totalElements", page.getTotalElements(),
                "totalPages", page.getTotalPages()
        );


        return ResponseEntity.ok(
                ApiResponseUtil.success("Records fetched successfully", response)
        );
    }


}
