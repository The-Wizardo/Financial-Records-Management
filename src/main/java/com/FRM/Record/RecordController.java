package com.FRM.Record;

import com.FRM.Auth.CustomUserDetails;
import com.FRM.Exception.ApiResponse;
import com.FRM.Exception.ApiResponseUtil;
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
    private  final  RecordRepository recordRepository;

    @PostMapping("")
    public ResponseEntity<ApiResponse<?>> createRecord(Authentication authentication,@Valid @RequestBody RecordRequest request){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Record record = new Record();
        record.setNote(request.note());
        record.setCategory(request.category());
        record.setAmount(request.amount());
        record.setNote(request.note());
        record.setDate(request.date());
        record.setUser(userDetails.getUser());
        record.setType(request.type());
        recordRepository.save(record);

        Map<String,Object> data= Map.of(
                "amount",record.getAmount(),
                "category",record.getCategory(),
                "note",record.getNote(),
                "date",record.getDate()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseUtil.success("Created Successfully",data));
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<?>> getAllRecords(Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        assert userDetails != null;
        List<RecordRequest> data = recordRepository.findByUser_UserIdAndIsDeleted(userDetails.getUser().getUserId(), false);
        return ResponseEntity.ok().body(ApiResponseUtil.success("Success",data));
    }

 }
