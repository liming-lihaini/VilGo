package com.village.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 村民附件实体类
 */
@Data
@TableName("resident_attachments")
public class ResidentAttachment {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 村民ID
     */
    private Long residentId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件类型（MIME类型）
     */
    private String fileType;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件分类：id_card-身份证照片、personal_photo-个人照片、copy-复印件
     */
    private String fileCategory;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 软删除标记
     */
    @TableLogic
    private Integer deleted;
}
