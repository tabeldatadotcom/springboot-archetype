package com.maryanto.dimas.archetype.components.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "children")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MenuDto {

    @JsonIgnore
    private String id;
    private String title;
    private String url;
    private String icon;
    private List<MenuDto> children = new ArrayList<>();
}
