package com.village.util;

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.Statement;

/**
 * 数据库升级脚本
 */
public class DbUpgrade {

    public static void main(String[] args) {
        try {
            String dbUrl = "D:/village-data/village.db";
            SQLiteDataSource dataSource = new SQLiteDataSource();
            dataSource.setUrl("jdbc:sqlite:" + dbUrl);

            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();

            // 添加 resident_id 列（如果不存在）
            try {
                stmt.execute("ALTER TABLE party_members ADD COLUMN resident_id INTEGER");
                System.out.println("已添加 resident_id 列");
            } catch (Exception e) {
                if (e.getMessage().contains("duplicate column name")) {
                    System.out.println("resident_id 列已存在");
                } else {
                    throw e;
                }
            }

            stmt.close();
            conn.close();
            System.out.println("数据库升级完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}