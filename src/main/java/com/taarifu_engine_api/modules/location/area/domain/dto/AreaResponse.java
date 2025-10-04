package com.taarifu_engine_api.modules.location.area.domain.dto;

import com.taarifu_engine_api.modules.common.domain.enums.AreaType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response DTO for Area entity
 */
@Data
public class AreaResponse {
    
    private Long id;
    private String uid;
    private String code;
    private AreaType areaType;
    private Long areaId;
    private String name;                    // Name from the respective location entity
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
