package com.maryanto.dimas.archetype.utils;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public interface QueryComparator<T> {

    StringBuilder getQuery(T params);

    MapSqlParameterSource getParameters();

}
