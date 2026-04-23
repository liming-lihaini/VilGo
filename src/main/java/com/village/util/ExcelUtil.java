package com.village.util;

import com.village.entity.Resident;
import com.village.entity.SpecialGroup;
import com.alibaba.excel.EasyExcel;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel 导出工具类
 */
public class ExcelUtil {

    /**
     * 获取村民档案表头
     */
    public static List<List<String>> getResidentHead() {
        List<List<String>> head = new ArrayList<>();
        head.add(createHead("序号"));
        head.add(createHead("姓名"));
        head.add(createHead("身份证号"));
        head.add(createHead("性别"));
        head.add(createHead("出生日期"));
        head.add(createHead("户籍状态"));
        head.add(createHead("人员类型"));
        head.add(createHead("保障类型"));
        head.add(createHead("联系电话"));
        head.add(createHead("住址"));
        head.add(createHead("户主姓名"));
        head.add(createHead("与户主关系"));
        head.add(createHead("所属村组"));
        head.add(createHead("是否本村户籍"));
        head.add(createHead("是否户主"));
        head.add(createHead("是否本村常住"));
        head.add(createHead("外地居住地址"));
        head.add(createHead("备注"));
        return head;
    }

    /**
     * 获取特殊人群表头
     */
    public static List<List<String>> getSpecialGroupHead() {
        List<List<String>> head = new ArrayList<>();
        head.add(createHead("姓名"));
        head.add(createHead("身份证号"));
        head.add(createHead("性别"));
        head.add(createHead("出生日期"));
        head.add(createHead("联系电话"));
        head.add(createHead("住址"));
        head.add(createHead("人群类型"));
        head.add(createHead("帮扶责任人"));
        head.add(createHead("帮扶措施"));
        head.add(createHead("帮扶时间"));
        head.add(createHead("帮扶成效"));
        head.add(createHead("帮扶状态"));
        return head;
    }

    /**
     * 创建表头
     */
    private static List<String> createHead(String name) {
        List<String> head = new ArrayList<>();
        head.add(name);
        return head;
    }

    /**
     * 写入数据到响应流
     */
    public static void writeToResponse(List<Resident> data, javax.servlet.http.HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("村民档案", StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream())
                .head(getResidentHead())
                .sheet()
                .doWrite(data);
    }
}