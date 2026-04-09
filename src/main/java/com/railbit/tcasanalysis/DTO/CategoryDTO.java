package com.railbit.tcasanalysis.DTO;



import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long id;           // null for CREATE, required for UPDATE/DELETE
    private String name;       // category name
    private Long severityId;   // maps to severity_master.id
}