package com.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author WYX
 * @date 2020/12/8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MsgInfo {

    @JsonIgnore
    private String id;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;
    /**
     * 发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendDate;
    private String longCode;
    private String mobile;
    /**
     * 公司名称
     */
    private String corpName;
    private String smsContent;
    /**
     * 短信发送状态
     */
    private Integer state;
    /**
     * 运营商 1 移动、2 联通、3 电信
     */
    private Integer operatorId;
    private String province;
    private String ipAddr;
    /**
     * 短信报告状态返回时长
     */
    private Integer replyTotal;
    /**
     * 短信扣费 分
     */
    private Integer fee;

}
