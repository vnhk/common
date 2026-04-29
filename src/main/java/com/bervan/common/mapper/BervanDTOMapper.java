package com.bervan.common.mapper;

import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseModel;
import com.bervan.core.service.DTOMapper;
import com.bervan.logging.JsonLogger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class BervanDTOMapper {
    private final JsonLogger logger = JsonLogger.getLogger(BervanDTOMapper.class, "common");
    private final DTOMapper mapper;

    public BervanDTOMapper() {
        mapper = new DTOMapper(new ArrayList<>());
    }

    public <ID, T extends BaseDTO<ID>> T map(BaseModel<ID> dtoTarget, Class<? extends T> dtoClass) throws RuntimeException {
        try {
            return (T) mapper.map(dtoTarget, dtoClass);
        } catch (Exception e) {
            logger.error("Failed to map to DTO", e);
            throw new RuntimeException(e);
        }
    }

    public <ID> BaseModel<ID> map(BaseDTO<ID> dto) {
        try {
            return mapper.map(dto);
        } catch (Exception e) {
            logger.error("Failed to map to model", e);
            throw new RuntimeException(e);
        }
    }
}
