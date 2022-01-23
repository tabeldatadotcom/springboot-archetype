package com.maryanto.dimas.archetype.controller.example;

import com.maryanto.dimas.archetype.dto.example.TableExampleDto;
import com.maryanto.dimas.archetype.entity.example.TableExample;
import com.maryanto.dimas.archetype.mappers.example.TableExampleMapper.ExampleNewMapper;
import com.maryanto.dimas.archetype.mappers.example.TableExampleMapper.ExampleUpdateMapper;
import com.maryanto.dimas.archetype.service.example.TableExampleService;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.DataTablesRequest;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.DataTablesResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/api/example")
@Api(value = "Rest API for example data CRUD")
public class TableExampleController {

    @Autowired
    private TableExampleService service;

    @ApiOperation(
            value = "Pagination system for datatables",
            response = TableExample.class,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = "Request parameter ada yang kurang"),
            @ApiResponse(code = 200,
                    message = "Response list of data",
                    response = DataTablesResponse.class),
            @ApiResponse(code = 500,
                    message = "Terjadi kesalahan system")
    })
    @PostMapping("/datatables")
    public DataTablesResponse<TableExample> datatables(
            @RequestParam(required = false, value = "draw", defaultValue = "0") Long draw,
            @RequestParam(required = false, value = "start", defaultValue = "0") Long start,
            @RequestParam(required = false, value = "length", defaultValue = "10") Long length,
            @RequestParam(required = false, value = "order[0][column]", defaultValue = "0") Long iSortCol0,
            @RequestParam(required = false, value = "order[0][dir]", defaultValue = "asc") String sSortDir0,
            @RequestBody(required = false) TableExample params
    ) {
        if (params == null) params = new TableExample();
        log.info("draw: {}, start: {}, length: {}, orderBy: {}, orderDir: {},  type: {}", draw, start, length, iSortCol0, sSortDir0, params);
        return service.datatables(
                new DataTablesRequest<>(draw, length, start, sSortDir0, iSortCol0, params));
    }

    @ApiOperation(
            value = "Save data",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = "Request body invalid"),
            @ApiResponse(code = 200,
                    message = "Berhasil save data",
                    response = TableExample.class),
            @ApiResponse(code = 500,
                    message = "Terjadi kesalahan system")
    })
    @PostMapping("/created")
    public ResponseEntity<?> createPost(@RequestBody @Valid TableExampleDto.New dto) {
        TableExample value = ExampleNewMapper.converter.convertToEntity(dto);
        try {
            value = service.save(value);
            return ok(value);
        } catch (SQLException sqle) {
            log.error("sql exception: ", sqle);
            return new ResponseEntity<>("Request failed, please try again later!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(
            value = "Update data by id",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = "Request body invalid"),
            @ApiResponse(code = 200,
                    message = "Berhasil save data",
                    response = TableExample.class),
            @ApiResponse(code = 500,
                    message = "Terjadi kesalahan system")
    })
    @PutMapping("/updated")
    public ResponseEntity<?> updatePostById(@RequestBody @Valid TableExampleDto.Update dto) {
        log.info("{}", dto);
        TableExample value = ExampleUpdateMapper.converter.convertToEntity(dto);
        try {
            value = service.update(value);
            return ok(value);
        } catch (SQLException sqle) {
            log.error("sql exception: ", sqle);
            return new ResponseEntity<>("Request failed, please try again later!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(
            value = "find by id",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(code = 400,
                    message = "Request body invalid"),
            @ApiResponse(code = 204,
                    message = "Data not found"),
            @ApiResponse(code = 200,
                    message = "Data ditemukan",
                    response = TableExample.class),
            @ApiResponse(code = 500,
                    message = "Terjadi kesalahan system")
    })
    @GetMapping("/{id}/findById")
    public ResponseEntity<?> findById(@PathVariable("id") String id) {
        Optional<TableExample> tableExampleOptional = service.findId(id);
        if (!tableExampleOptional.isPresent()) {
            return noContent().build();
        }

        TableExample value = tableExampleOptional.get();
        return ok(value);
    }

    @ApiOperation(
            value = "delete by id",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(code = 204,
                    message = "Data not found"),
            @ApiResponse(code = 200,
                    message = "Data berhasil dihapus"),
            @ApiResponse(code = 500,
                    message = "Terjadi kesalahan system")
    })
    @DeleteMapping("/{id}/deleteById")
    public ResponseEntity<?> deleteById(@PathVariable("id") String id) {
        Optional<TableExample> tableExampleOptional = service.findId(id);
        if (!tableExampleOptional.isPresent()) {
            return noContent().build();
        }

        try {
            TableExample value = tableExampleOptional.get();
            service.removeById(value.getId());
            return ok().build();
        } catch (SQLException sqle) {
            log.error("sql exception: ", sqle);
            return new ResponseEntity<>("Request failed, please try again later!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
