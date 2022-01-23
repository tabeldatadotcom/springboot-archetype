package com.maryanto.dimas.archetype.utils.impl.sqlserver;

import com.maryanto.dimas.archetype.utils.PageableLimitOffset;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.validation.constraints.NotNull;

public class SqlServer2008LimitOffset implements PageableLimitOffset {

    private String query;
    private String rowNumber;
    private MapSqlParameterSource parameterSource;

    public SqlServer2008LimitOffset(@NotNull MapSqlParameterSource parameterSource) {
        this.parameterSource = parameterSource;
    }

    @Override
    public String query(@NotNull String baseQuery, @NotNull String nameOfRowNumber) {
        this.query = baseQuery;
        this.rowNumber = nameOfRowNumber;
        return String.format("with data as (%s) select * from data where %s between (:start + 1) and (:page * :limit)", query, rowNumber);
    }

    @Override
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
