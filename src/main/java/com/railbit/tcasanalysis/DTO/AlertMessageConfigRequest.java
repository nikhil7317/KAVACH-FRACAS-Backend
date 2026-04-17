// ── Request DTO ──────────────────────────────────────────────────────────────
package com.railbit.tcasanalysis.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AlertMessageConfigRequest {

    @NotBlank(message = "alertCategory must not be blank")
    private String alertCategory;

    @NotBlank(message = "alertMessage must not be blank")
    private String alertMessage;

    /** Optional on create (defaults to true); required on update. */
    private Boolean enabled;
}