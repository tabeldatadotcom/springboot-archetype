package com.maryanto.dimas.archetype.dao.example;

import com.maryanto.dimas.archetype.entity.example.TableExample;
import com.maryanto.dimas.archetype.utils.QueryComparator;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.DataTablesRequest;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.OrderingByColumns;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.dao.DaoDataTablesPattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
public class TableExampleDao implements DaoDataTablesPattern<TableExample> {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public Optional<TableExample> findId(String value) {
        //language=OracleSqlPlus
        String query = "select ID           as id,\n" +
                "       NAME         as name,\n" +
                "       CREATED_DATE as createdDate,\n" +
                "       CREATED_TIME as createdTime,\n" +
                "       IS_ACTIVE    as active,\n" +
                "       COUNTER      as counter,\n" +
                "       CURRENCY     as currency,\n" +
                "       DESCRIPTION  as description,\n" +
                "       FLOATING     as floating\n" +
                "from EXAMPLE_TABLE\n" +
                "where ID = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", value);
        try {
            TableExample data = this.jdbcTemplate.queryForObject(query, params, new BeanPropertyRowMapper<>(TableExample.class));
            return Optional.ofNullable(data);
        } catch (EmptyResultDataAccessException erdae) {
            return Optional.empty();
        }
    }

    public List<TableExample> findAll() {
        //language=OracleSqlPlus
        String query = "select ID           as id,\n" +
                "       NAME         as name,\n" +
                "       CREATED_DATE as createdDate,\n" +
                "       CREATED_TIME as createdTime,\n" +
                "       IS_ACTIVE    as active,\n" +
                "       COUNTER      as counter,\n" +
                "       CURRENCY     as currency,\n" +
                "       DESCRIPTION  as description,\n" +
                "       FLOATING     as floating\n" +
                "from EXAMPLE_TABLE";
        MapSqlParameterSource params = new MapSqlParameterSource();
        return this.jdbcTemplate.query(query, params, new BeanPropertyRowMapper<>(TableExample.class));
    }

    public TableExample save(TableExample value) throws SQLException {
        value.setId(UUID.randomUUID().toString());
        value.setCreatedDate(Date.valueOf(LocalDate.now()));
        value.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
        //language=OracleSqlPlus
        String query = "insert into EXAMPLE_TABLE (ID, NAME, CREATED_DATE, CREATED_TIME, IS_ACTIVE, COUNTER, CURRENCY, DESCRIPTION, FLOATING)\n" +
                "values (:id, :name, :createdDate, :createdTime, :isActive, :counter, :currency, :description, :floating)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", value.getId());
        params.addValue("name", value.getName());
        params.addValue("createdDate", value.getCreatedDate());
        params.addValue("createdTime", value.getCreatedTime());
        params.addValue("isActive", value.getActive());
        params.addValue("currency", value.getCurrency());
        params.addValue("description", value.getDescription());
        params.addValue("floating", value.getFloating());
        params.addValue("counter", value.getCounter());
        this.jdbcTemplate.update(query, params);
        return value;
    }

    public TableExample update(TableExample value) throws SQLException {
        //language=OracleSqlPlus
        String query = "update EXAMPLE_TABLE\n" +
                "set NAME        = :name,\n" +
                "    IS_ACTIVE   = :isActive,\n" +
                "    CURRENCY    = :currency,\n" +
                "    DESCRIPTION = :description,\n" +
                "    FLOATING    = :floating,\n" +
                "    COUNTER     = :counter\n" +
                "where ID = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", value.getId());
        params.addValue("isActive", value.getActive());
        params.addValue("currency", value.getCurrency());
        params.addValue("name", value.getName());
        params.addValue("description", value.getDescription());
        params.addValue("floating", value.getFloating());
        params.addValue("counter", value.getCounter());
        this.jdbcTemplate.update(query, params);
        return value;
    }

    public boolean remove(TableExample value) throws SQLException {
        return removeById(value.getId());
    }

    public boolean removeById(String s) throws SQLException {
//language=OracleSqlPlus
        String query = "delete from EXAMPLE_TABLE where ID = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", s);
        int updated = this.jdbcTemplate.update(query, params);
        return updated >= 1;
    }

    @Override
    public List<TableExample> datatables(DataTablesRequest<TableExample> params) {
        //language=OracleSqlPlus
        String baseQuery = "select id           as id,\n" +
                "       name         as name,\n" +
                "       created_date as createdDate,\n" +
                "       created_time as createdTime,\n" +
                "       is_active    as active,\n" +
                "       counter      as counter,\n" +
                "       currency     as currency,\n" +
                "       description  as description,\n" +
                "       floating     as floating\n" +
                "from example_table\n" +
                "where 1 = 1";
        MapSqlParameterSource mapSqlParameters = new MapSqlParameterSource();
        DataTablesMapping dt = new DataTablesMapping(baseQuery, mapSqlParameters);
        mapSqlParameters = dt.getParameters();
        StringBuilder query = dt.getQuery(params.getValue());

        OrderingByColumns columns = new OrderingByColumns("id", "name", "created_date", "created_time", "is_active", "currency", "description", "floating");
        String orderBy = columns.orderBy(params.getColDir(), params.getColOrder());

        query.append(orderBy);

        if (params.getLength() >= 0){
            query.append(" offset :offset rows fetch next :limit rows only");
            mapSqlParameters.addValue("offset", params.getStart());
            mapSqlParameters.addValue("limit", params.getLength());
        }
        return jdbcTemplate.query(query.toString(), mapSqlParameters, new BeanPropertyRowMapper<>(TableExample.class));
    }

    @Override
    public Long datatables(TableExample value) {
        String baseQuery = "select count(*) as rows_count\n" +
                "from example_table\n" +
                "where 1 = 1";
        MapSqlParameterSource mapSqlParameters = new MapSqlParameterSource();
        DataTablesMapping dt = new DataTablesMapping(baseQuery, mapSqlParameters);
        mapSqlParameters = dt.getParameters();
        StringBuilder query = dt.getQuery(value);

        return jdbcTemplate.queryForObject(query.toString(), mapSqlParameters, (resultSet, i) -> resultSet.getLong(1));
    }

    private class DataTablesMapping implements QueryComparator<TableExample> {

        private final MapSqlParameterSource parameter;
        private final StringBuilder builder;

        public DataTablesMapping(String query, MapSqlParameterSource parameterSource) {
            this.parameter = parameterSource;
            this.builder = new StringBuilder(query);
        }

        @Override
        public StringBuilder getQuery(TableExample value) {
            if (StringUtils.isNotBlank(value.getId())) {
                this.builder.append(" and id = :id");
                this.parameter.addValue("id", value.getId());
            }

            if (StringUtils.isNotBlank(value.getName())) {
                this.builder.append(" and lower(name) like lower(:name)");
                this.parameter.addValue("name",
                        new StringBuilder("%")
                                .append(value.getName())
                                .append("%").toString());
            }

            if (value.getCreatedDate() != null) {
                this.builder.append(" and created_date = :createdDate");
                this.parameter.addValue("createdDate", value.getCreatedDate());
            }

            if (value.getCreatedTime() != null) {
                this.builder.append(" and created_time = :createdTime ");
                this.parameter.addValue("createdTime", value.getCreatedTime());
            }

            if (value.getActive() != null) {
                this.builder.append(" and is_active = :isActive");
                this.parameter.addValue("isActive", value.getActive());
            }

            if (value.getCounter() != null) {
                this.builder.append(" and counter = :counter");
                this.parameter.addValue("counter", value.getCounter());
            }

            if (value.getCurrency() != null) {
                this.builder.append(" and currency = :currency");
                this.parameter.addValue("currency", value.getCurrency());
            }

            if (StringUtils.isNotBlank(value.getDescription())) {
                this.builder.append(" and lower(description) like lower(:description)");
                this.parameter.addValue("description",
                        new StringBuilder("%")
                                .append(value.getDescription())
                                .append("%").toString());
            }

            if (value.getFloating() != null) {
                this.builder.append(" and floating = :floating");
                this.parameter.addValue("floating", value.getFloating());
            }

            return this.builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return this.parameter;
        }
    }
}
