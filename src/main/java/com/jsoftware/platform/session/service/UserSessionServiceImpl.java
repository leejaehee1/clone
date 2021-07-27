package com.jsoftware.platform.session.service;

import com.jsoftware.platform.session.model.UserSessionVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
@Log4j2
public class UserSessionServiceImpl implements UserSessionService {
    final HttpSession httpSession;

    public UserSessionServiceImpl(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Override
    public UserSessionVO getRedisSessionData() {
        return (UserSessionVO) httpSession.getAttribute("userInfo");
    }

    @Override
    public String getLangCode() {
        return getRedisSessionData().getLangCode();
    }

//    @Override
//    public void setLangCode(String langCode) {
//        UserSessionVO userSessionVO = this.getRedisSessionData();
//        userSessionVO.setLangCode(langCode);
//        setSessionData(userSessionVO);
//
//    }
}
