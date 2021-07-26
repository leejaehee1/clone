package com.jsoftware.platform.session.service;

import com.jsoftware.platform.session.model.UserSessionVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
@Log4j2
public class UserSessionServiceImpl implements UserSessionService {
    @Autowired
    HttpSession httpSession;

    @Override
    public UserSessionVO getRedisSessionData() {
        UserSessionVO userSessionVO = (UserSessionVO) httpSession.getAttribute("userInfo");
        return userSessionVO;
    }

    @Override
    public String getLangCode() {
        return getRedisSessionData().getLangCode();
    }
//
//    @Override
//    public void setLangCode(String langCode) {
//        UserSessionVO userSessionVO = this.getRedisSessionData();
//        userSessionVO.setLangCode(langCode);
//        setSessionData(userSessionVO);
//
//    }
}
