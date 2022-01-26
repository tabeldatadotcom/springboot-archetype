package com.maryanto.dimas.archetype.service.example;

import com.maryanto.dimas.archetype.dao.example.TableExampleDao;
import com.maryanto.dimas.archetype.entity.example.TableExample;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.DataTablesRequest;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.DataTablesResponse;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.service.ServiceCrudDataTablesPattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TableExampleService implements ServiceCrudDataTablesPattern<TableExample, String> {

    @Autowired
    private TableExampleDao dao;

    @Override
    public Optional<TableExample> findId(String s) {
        return dao.findId(s);
    }

    @Override
    public List<TableExample> findAll() {
        return dao.findAll();
    }

    @Override
    @Transactional
    public TableExample save(TableExample tableExample) {
        return dao.save(tableExample);
    }

    @Override
    @Transactional
    public TableExample update(TableExample tableExample) {
        return dao.update(tableExample);
    }

    @Override
    @Transactional
    public boolean remove(TableExample tableExample) {
        return dao.remove(tableExample);
    }

    @Override
    @Transactional
    public boolean removeById(String s) {
        return dao.removeById(s);
    }

    @Override
    public DataTablesResponse<TableExample> datatables(DataTablesRequest<TableExample> params) {
        List<TableExample> datas = dao.datatables(params);
        Long rows = dao.datatables(params.getValue());
        return new DataTablesResponse<>(datas, params.getDraw(), rows, rows);
    }
}
