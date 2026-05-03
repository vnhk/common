package com.bervan.common.mapper;

import com.bervan.core.model.*;
import com.bervan.core.service.DTOMapper;
import com.bervan.logging.JsonLogger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BervanDTOMapper {
    private final JsonLogger logger = JsonLogger.getLogger(BervanDTOMapper.class, "common");
    private final DTOMapper mapper;

    private final List<? extends DefaultCustomMapper> customMappers;
    private final List<? extends PreMapper> preMappers;
    private final List<? extends PostMapper> postMappers;

    public BervanDTOMapper(List<? extends DefaultCustomMapper> customMappers, List<? extends PreMapper> preMappers, List<? extends PostMapper> postMappers) {
        this.customMappers = customMappers;
        this.preMappers = preMappers;
        this.postMappers = postMappers;
        mapper = new DTOMapper(customMappers, preMappers, postMappers);
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
