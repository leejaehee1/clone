package com.jsoftware.platform.session.service;

import com.jsoftware.platform.session.model.UserSessionVO;

public interface UserSessionService {
    UserSessionVO getRedisSessionData();
    String getLangCode();
//    void setLangCode(String langCode);
}
