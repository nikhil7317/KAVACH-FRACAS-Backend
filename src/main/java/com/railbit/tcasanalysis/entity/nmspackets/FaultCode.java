package com.railbit.tcasanalysis.entity.nmspackets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.railbit.tcasanalysis.entity.Firm;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "faultcode")
public class FaultCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int code;

    @ManyToOne
    private Firm firm;

    @JsonProperty("faultname")
    private String faultName;

    private String type;

}
