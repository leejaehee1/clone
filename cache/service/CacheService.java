package com.jsoftware.platform.cache.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jsoftware.platform.cache.model.CacheMessageVO;
import com.jsoftware.platform.cache.model.CacheTimeZoneVO;
import com.jsoftware.platform.response.model.MessageParamVO;

import java.util.List;

public interface CacheService {
    String getMessage(String messageCode, String langCode);
    String getMessage(String messageCode);

    CacheTimeZoneVO getMtzInfo(String timeZoneCode) throws JsonProcessingException;

    String getMessageByParam(String messageCode, MessageParamVO messageParamVO);
    String getMessageByParam(String messageCode, MessageParamVO messageParamVO, String langCode);


    List<CacheTimeZoneVO> getAllTimeZoneInfo() throws JsonProcessingException;

    List<CacheMessageVO> getAllMessage() throws JsonProcessingException;

    List<CacheMessageVO> getLangAllMessage(String langCode) throws JsonProcessingException;
}
