package com.jsoftware.platform.controller;

import com.jsoftware.platform.vo.DataItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@RestController
public class HomeController {

    @Resource(name = "secondaryDataSource")
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static class CustomRowMapper implements RowMapper<DataItem> {

        @Override
        public DataItem mapRow(ResultSet rs, int rowNum) {
            System.out.println(rs.toString());
            try {
                return (DataItem) rs.getObject(rowNum);
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }

            return null;
        }
    }

    @GetMapping("/home")
    public String findUid(HttpSession session) {
        return session.getId();
    }

    @GetMapping("/test/data")
    public String testDataSource(HttpSession session) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String sql = "select * from products limit 10";
        System.out.println(session);
        for (DataItem dataItem : jdbcTemplate.query(sql, new CustomRowMapper())) {
            System.out.println(dataItem.toJson());
        }

        return "";
    }
}
