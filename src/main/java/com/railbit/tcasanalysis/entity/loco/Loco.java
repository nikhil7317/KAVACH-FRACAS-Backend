package com.railbit.tcasanalysis.entity.loco;

import com.railbit.tcasanalysis.entity.Firm;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity(name = "loco")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Loco implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String locoNo;
    @Column(unique = true)
    private String nmsLocoId;
    @ManyToOne
    private LocoType locoType;
    @ManyToOne
    private Firm firm;
    @ManyToOne
    private Shed shed;
    private String month;
    private String version;
    private String condemned;

}
