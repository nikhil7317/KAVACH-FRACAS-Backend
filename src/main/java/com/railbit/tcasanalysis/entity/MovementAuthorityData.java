package com.railbit.tcasanalysis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "movement_authority_data")
@Setter
@Getter
public class MovementAuthorityData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("subpkt-ma")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_packet_id", nullable = false, unique = true)
    private SubPacket subPacket;

    @Column(name = "train_section_type_code")
    private Integer trainSectionTypeCode;

    @Column(name = "train_section_type_str")
    private String trainSectionTypeStr;

    @Column(name = "cur_sig_info_raw")
    private Integer curSigInfoRaw;

    @Column(name = "line_number")
    private Integer lineNumber;

    @Column(name = "line_number_str")
    private String lineNumberStr;

    @Column(name = "line_name_code")
    private Integer lineNameCode;

    @Column(name = "line_name_str")
    private String lineNameStr;

    @Column(name = "signal_type_code")
    private Integer signalTypeCode;

    @Column(name = "signal_type_str")
    private String signalTypeStr;

    @Column(name = "signal_override_permission")
    private Integer signalOverridePermission;

    @Column(name = "signal_override_str")
    private String signalOverrideStr;

    @Column(name = "stop_signal")
    private Integer stopSignal;

    @Column(name = "stop_signal_str")
    private String stopSignalStr;

    @Column(name = "cur_sig_asp_code")
    private Integer curSigAspCode;

    @Column(name = "cur_sig_asp_str")
    private String curSigAspStr;

    @Column(name = "next_sig_asp_code")
    private Integer nextSigAspCode;

    @Column(name = "next_sig_asp_str")
    private String nextSigAspStr;

    @Column(name = "appr_sig_dist")
    private Integer apprSigDist;

    @Column(name = "authority_type_code")
    private Integer authorityTypeCode;

    @Column(name = "authority_type_str")
    private String authorityTypeStr;

    @Column(name = "authorized_speed_code")
    private Integer authorizedSpeedCode;

    @Column(name = "authorized_speed_kmph")
    private Integer authorizedSpeedKmph;

    @Column(name = "ma_wrt_sig")
    private Integer maWrtSig;

    @Column(name = "req_shorten_ma")
    private Integer reqShortenMa;

    @Column(name = "new_ma")
    private Integer newMa;

    @Column(name = "trn_len_info_sts")
    private Integer trnLenInfoSts;

    @Column(name = "trn_len_info_type")
    private Integer trnLenInfoType;

    @Column(name = "ref_frame_num_tlm")
    private Integer refFrameNumTlm;

    @Column(name = "ref_frame_time_tlm")
    private String refFrameTimeTlm;

    @Column(name = "ref_offset_int_tlm")
    private Integer refOffsetIntTlm;

    @Column(name = "next_stn_comm")
    private Integer nextStnComm;

    @Column(name = "appr_stn_id")
    private Integer apprStnId;
}
