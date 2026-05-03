package com.bervan.common.controller;

import com.bervan.common.config.EntityConfigValidator;
import com.bervan.common.mapper.BervanDTOMapper;
import com.bervan.common.model.BervanOwnedBaseEntity;
import com.bervan.common.search.SearchRequest;
import com.bervan.common.service.BaseService;
import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BaseOwnedController<T extends BervanOwnedBaseEntity<ID> & BaseModel<ID>, ID extends Serializable> {

    protected final BaseService<ID, T> service;
    protected final BervanDTOMapper mapper;
    protected final EntityConfigValidator validator;
    private final String entityName;

    protected BaseOwnedController(BaseService<ID, T> service, BervanDTOMapper mapper, EntityConfigValidator validator, String entityName) {
        this.service = service;
        this.mapper = mapper;
        this.validator = validator;
        this.entityName = entityName;
    }

    private <DTO extends BaseDTO<ID>> List<Field> getModelFieldsUsedInDto(Class<?> modelClass, DTO dto) {
        try {
            Set<String> dtoFields = Arrays.stream(dto.getClass().getDeclaredFields())
                    .filter(field -> !field.getName().equals("id"))
                    .filter(field -> {
                        int mod = field.getModifiers();
                        return !Modifier.isStatic(mod) && !Modifier.isFinal(mod);
                    }).map(e -> e.getName())
                    .collect(Collectors.toSet());

            List<Field> modelFields = Arrays.stream(modelClass.getDeclaredFields())
                    .filter(field -> {
                        int mod = field.getModifiers();
                        return !Modifier.isStatic(mod) && !Modifier.isFinal(mod);
                    })
                    .filter(e -> dtoFields.contains(e.getName()))
                    .collect(Collectors.toList());

            return modelFields;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected <DTO extends BaseDTO<ID>> ResponseEntity<DTO> getById(ID id, Class<DTO> dtoClass) {
        return service.loadById(id)
                .map(i -> ResponseEntity.ok(toDto(i, dtoClass)))
                .orElse(ResponseEntity.notFound().build());
    }

    private <DTO extends BaseDTO<ID>> DTO toDto(T i, Class<DTO> dtoClass) {
        return mapper.map(i, dtoClass);
    }

    protected <DTO extends BaseDTO<ID>> ResponseEntity<?> create(DTO req) {
        return create(req, req.getClass());
    }

    protected <DTO extends BaseDTO<ID>> ResponseEntity<?> create(DTO req, Class<? extends DTO> resDTOClass) {
        T model = (T) mapper.map(req);
        List<EntityConfigValidator.FieldError> errors = validator.validateAll(entityName, model);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(new ValidationErrorResponse(errors));
        }

        T saved = service.save(model);
        return ResponseEntity.ok(toDto(saved, resDTOClass));
    }

    protected <DTO extends BaseDTO<ID>> ResponseEntity<?> update(DTO req) {
        if (req.getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<T> match = service.loadById(req.getId());
        if (match.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        T original = match.get();
        T model = (T) mapper.map(req);

        List<EntityConfigValidator.FieldError> errors = validator.validateAll(entityName, model);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(new ValidationErrorResponse(errors));
        }

        List<Field> declaredFields = getModelFieldsUsedInDto(model.getClass(), req);
        for (Field field : declaredFields) {
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(model);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            try {
                field.set(original, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        original.setModificationDate(LocalDateTime.now());
        T saved = service.save(original);
        return ResponseEntity.ok(toDto(saved, req.getClass()));
    }

    protected <DTO extends BaseDTO<ID>> ResponseEntity<?> patchUpdate(DTO req) {
        if (req.getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<T> match = service.loadById(req.getId());
        if (match.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        T original = match.get();
        T model = (T) mapper.map(req);

        List<EntityConfigValidator.FieldError> errors = validator.validateNotNull(entityName, model);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(new ValidationErrorResponse(errors));
        }

        List<Field> declaredFields = getModelFieldsUsedInDto(model.getClass(), req);
        for (Field field : declaredFields) {
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(model);
                if (value == null) continue;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            try {
                field.set(original, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        original.setModificationDate(LocalDateTime.now());
        T saved = service.save(original);
        return ResponseEntity.ok(toDto(saved, req.getClass()));
    }

    protected ResponseEntity<?> delete(ID id) {
        Optional<T> match = service.loadById(id);
        if (match.isEmpty()) return ResponseEntity.notFound().build();
        service.delete(match.get());
        return ResponseEntity.noContent().build();
    }

    protected <DTO extends BaseDTO<ID>> ResponseEntity<Page<DTO>> load(int page, int size, Class<DTO> dtoClass) {
        Set<T> loaded = service.load(PageRequest.of(page, size));
        List<DTO> dtos = loaded.stream()
                .map(p -> mapper.map(p, dtoClass))
                .toList();
        int total = Math.toIntExact(service.loadCount());
        int fromIndex = Math.min(page * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        return ResponseEntity.ok(new PageImpl<>(dtos, PageRequest.of(page, size), total));
    }

    protected <DTO extends BaseDTO<ID>> ResponseEntity<Page<DTO>> load(SearchRequest request, int page, int size, Class<DTO> dtoClass) {
        Set<T> loaded = service.load(request, PageRequest.of(page, size));
        List<DTO> dtos = loaded.stream()
                .map(p -> mapper.map(p, dtoClass))
                .toList();
        int total = Math.toIntExact(service.loadCount(request));
        int fromIndex = Math.min(page * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        return ResponseEntity.ok(new PageImpl<>(dtos, PageRequest.of(page, size), total));
    }

    record ValidationErrorResponse(List<EntityConfigValidator.FieldError> errors) {
    }
}
