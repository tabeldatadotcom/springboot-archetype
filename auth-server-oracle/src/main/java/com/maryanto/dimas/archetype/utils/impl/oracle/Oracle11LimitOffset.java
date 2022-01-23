package com.maryanto.dimas.archetype.utils.impl.oracle;

import com.maryanto.dimas.archetype.utils.PageableLimitOffset;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.validation.constraints.NotNull;

public class Oracle11LimitOffset implements PageableLimitOffset {

    private final MapSqlParameterSource parameterSource;
    private String query;
    private String rowNumber;

    public Oracle11LimitOffset(@NotNull MapSqlParameterSource parameterSource) {
        this.parameterSource = parameterSource;
    }

    public String query(@NotNull String baseQuery,
                        @NotNull String nameOfRowNumber) {
        this.query = baseQuery;
        this.rowNumber = nameOfRowNumber;
        return String.format("select * from (%s) where %s between (:start + 1) and (:page * :limit)", query, rowNumber);
    }

    public MapSqlParameterSource parameter(Long start, Long limit) {
        if (start > 0)
            parameterSource.addValue("page", start);
        else
            parameterSource.addValue("page", 1);

        parameterSource.addValue("start", start);
        parameterSource.addValue("limit", limit);
        return parameterSource;
    }

}
