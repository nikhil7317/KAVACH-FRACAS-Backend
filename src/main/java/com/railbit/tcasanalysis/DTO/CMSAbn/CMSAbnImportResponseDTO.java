package com.railbit.tcasanalysis.DTO.CMSAbn;

import com.railbit.tcasanalysis.entity.cmsabn.CMSAbn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CMSAbnImportResponseDTO {
    int rowInserted;
    int rowFailed;
    HashMap<String,CMSAbn> failedRows;
//    List<CMSAbn> cmsAbns;
}
