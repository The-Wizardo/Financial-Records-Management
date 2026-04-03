package com.FRM.Record;

import com.FRM.Auth.CustomUserDetails;
import com.FRM.Exception.ApiResponse;
import com.FRM.Exception.ApiResponseUtil;
import com.FRM.Exception.ResourceNotFoundException;
import com.FRM.User.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

}
