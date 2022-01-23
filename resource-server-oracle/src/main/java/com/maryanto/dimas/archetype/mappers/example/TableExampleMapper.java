package com.maryanto.dimas.archetype.mappers.example;

import com.maryanto.dimas.archetype.dto.example.TableExampleDto;
import com.maryanto.dimas.archetype.entity.example.TableExample;
import com.maryanto.dimas.plugins.web.commons.mappers.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

public class TableExampleMapper {

    @Mapper
    public interface ExampleNewMapper extends ObjectMapper<TableExample, TableExampleDto.New> {
        ExampleNewMapper converter = Mappers.getMapper(ExampleNewMapper.class);
    }

    @Mapper
    public interface ExampleUpdateMapper extends ObjectMapper<TableExample, TableExampleDto.Update> {
        ExampleUpdateMapper converter = Mappers.getMapper(ExampleUpdateMapper.class);
    }
}
