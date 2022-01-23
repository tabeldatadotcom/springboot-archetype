package com.maryanto.dimas.archetype.utils;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.validation.constraints.NotNull;

public interface PageableLimitOffset {

    String query(@NotNull String baseQuery, @NotNull String nameOfRowNumber);

    MapSqlParameterSource parameter(Long start, Long limit);
}
