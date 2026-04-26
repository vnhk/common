package com.bervan.common.view;

import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.Map;

@Builder
@Deprecated
public class DefaultFilterValuesContainer {
    @Getter
    protected Map<Field, Map<Object, Boolean>> checkboxFiltersMapDefaultValues;

}
