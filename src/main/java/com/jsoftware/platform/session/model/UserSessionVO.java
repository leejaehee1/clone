package com.jsoftware.platform.session.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSessionVO implements Serializable {
    private Long userId;
    private String userNo;
    private String langCode;


}
