package com.example.coursetable_system.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "notification_setting")
public class NotificationSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    private Integer settingId; // 配置ID（自增主键）

    // 关联学生（一个学生对应一条配置）
    @OneToOne
    @JoinColumn(name = "student_id", referencedColumnName = "student_id", unique = true)
    private Student student; // 所属学生

    @Column(name = "enable_email")
    private Boolean enableEmail = true; // 是否开启邮件通知（默认开启）

    @Column(name = "enable_site_msg")
    private Boolean enableSiteMsg = true; // 是否开启站内消息（默认开启）
}