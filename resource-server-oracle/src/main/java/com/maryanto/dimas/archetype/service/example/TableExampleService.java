package com.maryanto.dimas.archetype.service.example;

import com.maryanto.dimas.archetype.dao.example.TableExampleDao;
import com.maryanto.dimas.archetype.entity.example.TableExample;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.DataTablesRequest;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.DataTablesResponse;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.service.ServiceDataTablesPattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TableExampleService implements ServiceDataTablesPattern<TableExample> {

    @Autowired
    private TableExampleDao dao;

    public Optional<TableExample> findId(String s) {
        return dao.findId(s);
    }

    public List<TableExample> findAll() {
        return dao.findAll();
    }

    @Transactional
    public TableExample save(TableExample value) throws SQLException {
        value = dao.save(value);
        return findId(value.getId()).orElse(null);
    }

    @Transactional
    public TableExample update(TableExample value) throws SQLException {
        value = dao.update(value);
        return findId(value.getId()).orElse(null);
    }

    @Transactional
    public boolean remove(TableExample value) throws SQLException {
        return dao.remove(value);
    }

    @Transactional
    public boolean removeById(String value) throws SQLException {
        return dao.removeById(value);
    }

    @Override
    public DataTablesResponse<TableExample> datatables(DataTablesRequest<TableExample> params) {
        List<TableExample> datas = dao.datatables(params);
        Long rows = dao.datatables(params.getValue());
        return new DataTablesResponse<>(datas, params.getDraw(), rows, rows);
    }
}
