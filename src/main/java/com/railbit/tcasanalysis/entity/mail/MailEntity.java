package com.railbit.tcasanalysis.entity.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailEntity {
    private String subject;
    private String msg;
    private String to;
}
