package com.maryanto.dimas.archetype.repository.example;

import com.maryanto.dimas.archetype.entity.example.TableExample;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TableExampleRepository extends CrudRepository<TableExample, String> {

    List<TableExample> findAll();

    @Modifying
    @Query("update TableExample set name = ?1, counter = ?2, active = ?3, description =?4 , floating =?5 where id = ?6")
    int update(String name, Integer counter, boolean active, String description, Double floating, String id);

}
