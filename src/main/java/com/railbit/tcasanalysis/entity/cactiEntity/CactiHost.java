package com.railbit.tcasanalysis.entity.cactiEntity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "host")
@Data
public class CactiHost {

    @Id
    private Integer id;

    private Integer poller_id;
    private Integer site_id;
    private Integer host_template_id;
    private String description;
    private String hostname;
    private String location;
    private String notes;
    private String external_id;
    private String snmp_community;
    private Integer snmp_version;

    private String snmp_username;
    private String snmp_password;
    private String snmp_auth_protocol;
    private String snmp_priv_passphrase;
    private String snmp_priv_protocol;
    private String snmp_context;
    private String snmp_engine_id;

    private Integer snmp_port;
    private Integer snmp_timeout;

    @Column(columnDefinition = "text")
    private String snmp_sysDescr;

    private String snmp_sysObjectID;

    // XML shows string (not numeric)
    private String snmp_sysUpTimeInstance;

    private String snmp_sysContact;
    private String snmp_sysName;
    private String snmp_sysLocation;

    private Integer availability_method;
    private Integer ping_method;
    private Integer ping_port;
    private Integer ping_timeout;
    private Integer ping_retries;

    private Integer max_oids;
    private Integer bulk_walk_size;
    private Integer device_threads;

    private Integer deleted;
    private Integer disabled;
    private Integer status;
    private Integer status_event_count;

    private String status_fail_date;
    private String status_rec_date;
    private String status_last_error;

    private Double min_time;
    private Double max_time;
    private Double cur_time;
    private Double avg_time;
    private Double polling_time;

    private Integer total_polls;
    private Integer failed_polls;

    private Double availability;

    private String last_updated;
}


