package com.jsoftware.platform.cache.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsoftware.platform.cache.model.CacheMessageVO;
import com.jsoftware.platform.cache.model.CacheTimeZoneVO;
import com.jsoftware.platform.response.model.MessageParamVO;
import com.jsoftware.platform.session.service.UserSessionService;
import com.jsoftware.platform.util.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Log4j2
public class CacheServiceImpl implements CacheService {
    @Autowired
    @Qualifier("redisCacheTemplate")
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserSessionService userSessionService;

    // cache.service.CacheService.java
    // AAA10010  KO  //m=messagecontroller
    @Override
    public String getMessage(String messageCode, String langCode) {
        String message = null;
        String valueString = (String) redisTemplate.opsForValue().get("msgString:" + langCode + ":" + messageCode);
        if (valueString != null) {
            CacheMessageVO cacheMessageVO = null;
            try {
                cacheMessageVO = objectMapper.readValue(valueString, CacheMessageVO.class);
            } catch (JsonProcessingException e) {
                return null;
            }
            message = cacheMessageVO.getMessage();
        }
        return message;
    }

    @Override
    public String getMessage(String messageCode) {
        if (userSessionService.getRedisSessionData()==null) {
            return null;
        }
        String langCode = userSessionService.getLangCode();
        return getMessage(messageCode, langCode);
    }

    @Override
    public CacheTimeZoneVO getMtzInfo(String timeZoneCode) throws JsonProcessingException {
        CacheTimeZoneVO cacheTimeZoneVO = null;
        String valueString = (String) redisTemplate.opsForValue().get("mtzString:" + timeZoneCode);
        if (valueString != null) {
            cacheTimeZoneVO = objectMapper.readValue(valueString, CacheTimeZoneVO.class);
        }
        return cacheTimeZoneVO;
    }

    @Override
    public String getMessageByParam(String messageCode, MessageParamVO messageParamVO, String langCode) {
        String message = null;
        message = this.getMessage(messageCode);
        if(message != null && messageParamVO !=null){
            for(String messageParamCode : messageParamVO.keySet()){
                message = message.replaceAll("\\$\\{"+messageParamCode+"\\}", messageParamVO.get(messageParamCode));
            }
        }
        return message;
    }

    @Override
    public String getMessageByParam(String messageCode, MessageParamVO messageParamVO) {
        return this.getMessageByParam(messageCode, messageParamVO, userSessionService.getLangCode());
    }

    @Override
    public List<CacheTimeZoneVO> getAllTimeZoneInfo() throws JsonProcessingException {
        List<CacheTimeZoneVO> timeZoneVOList = new ArrayList<>();
        Set<String> keys = RedisUtil.scan(redisTemplate, "mtzString:*");
        List<Object> valueSet = redisTemplate.opsForValue().multiGet(keys);
        for (int i = 0; i < valueSet.size(); i++) {
            String valueString = (String) valueSet.get(i);
            CacheTimeZoneVO cacheTimeZoneVO = objectMapper.readValue(valueString, CacheTimeZoneVO.class);
            timeZoneVOList.add(cacheTimeZoneVO);
        }
        return timeZoneVOList;
    }

    @Override
    public List<CacheMessageVO> getAllMessage() throws JsonProcessingException {
        List<CacheMessageVO> msgList = new ArrayList<>();
        Set<String> keys = RedisUtil.scan(redisTemplate, "msgString:*");
        List<Object> valueSet = redisTemplate.opsForValue().multiGet(keys);
        for (int i = 0 ; i < valueSet.size();i++) {
            String valueString = (String) valueSet.get(i);
            CacheMessageVO cacheMessageVO = objectMapper.readValue(valueString, CacheMessageVO.class);
            msgList.add(cacheMessageVO);
        }
        return msgList;
    }

    @Override
    public List<CacheMessageVO> getLangAllMessage(String langCode) throws JsonProcessingException {
        List<CacheMessageVO> msgList = new ArrayList<>();
        Set<String> keys = RedisUtil.scan(redisTemplate, "msgString"+langCode+"*");
        List<Object> valueSet = redisTemplate.opsForValue().multiGet(keys);
        for (int i = 0 ; i < valueSet.size();i++) {
            String valueString = (String) valueSet.get(i);
            CacheMessageVO cacheMessageVO = objectMapper.readValue(valueString, CacheMessageVO.class);
            msgList.add(cacheMessageVO);
        }
        return msgList;
    }
}
