package com.village.util;

import com.village.entity.Resident;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.write.metadata.head.FieldHead;

import java.util.ArrayList;
import java.util.Date;
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
        head.add(createHead("备注"));
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
        String fileName = java.net.URLEncoder.encode("村民档案", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream())
                .head(getResidentHead())
                .sheet()
                .doWrite(data);
    }
}