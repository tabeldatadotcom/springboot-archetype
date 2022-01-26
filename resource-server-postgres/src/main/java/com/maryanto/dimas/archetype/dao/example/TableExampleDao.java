package com.maryanto.dimas.archetype.dao.example;

import com.maryanto.dimas.archetype.entity.example.TableExample;
import com.maryanto.dimas.archetype.repository.example.TableExampleRepository;
import com.maryanto.dimas.archetype.utils.QueryComparator;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.DataTablesRequest;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.OrderingByColumns;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.dao.DaoCrudDataTablesPattern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TableExampleDao implements DaoCrudDataTablesPattern<TableExample, String> {

    @Autowired
    private TableExampleRepository repo;
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Optional<TableExample> findId(String s) {
        return repo.findById(s);
    }

    @Override
    public List<TableExample> findAll() {
        return repo.findAll();
    }

    @Override
    public TableExample save(TableExample tableExample) {
        tableExample.setCreatedDate(Date.valueOf(LocalDate.now()));
        tableExample.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
        return repo.save(tableExample);
    }

    @Override
    public TableExample update(TableExample value) {
        repo.update(value.getName(), value.getCounter(), value.getActive(), value.getDescription(), value.getFloating(), value.getId());
        return null;
    }

    @Override
    public boolean remove(TableExample tableExample) {
        repo.delete(tableExample);
        return true;
    }

    @Override
    public boolean removeById(String s) {
        repo.deleteById(s);
        return true;
    }

    @Override
    public List<TableExample> datatables(DataTablesRequest<TableExample> params) {
        //language=PostgreSQL
        String baseQuery = "select id,\n" +
                "       name,\n" +
                "       created_date as createdDate,\n" +
                "       created_time as createdTime,\n" +
                "       is_active as active,\n" +
                "       counter,\n" +
                "       currency,\n" +
                "       description,\n" +
                "       floating\n" +
                "from example_table\n" +
                "where 1 = 1";
        MapSqlParameterSource mapSqlParameters = new MapSqlParameterSource();
        DataTablesMapping dt = new DataTablesMapping(baseQuery, mapSqlParameters);
        mapSqlParameters = dt.getParameters();
        StringBuilder query = dt.getQuery(params.getValue());

        OrderingByColumns columns = new OrderingByColumns("id", "name", "createdDate", "createdTime", "active", "counter", "description", "floating");
        String orderBy = columns.orderBy(params.getColDir(), params.getColOrder());
        query.append(orderBy);

        if (params.getLength() >= 0) {
            query.append(" limit :limit offset :offset ");
            mapSqlParameters.addValue("limit", params.getLength());
            mapSqlParameters.addValue("offset", params.getStart());
        }

        return jdbcTemplate.query(query.toString(), mapSqlParameters, new BeanPropertyRowMapper<>(TableExample.class));
    }

    @Override
    public Long datatables(TableExample value) {
        //language=PostgreSQL
        String baseQuery = "select count(*) as rows\n" +
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
