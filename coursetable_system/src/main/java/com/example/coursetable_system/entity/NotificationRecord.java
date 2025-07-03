package com.example.coursetable_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notification_record")
public class NotificationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId; // 通知ID（自增主键）

    // 接收通知的学生
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
    private Student student; // 接收用户ID（外键）

    // 关联的课程（可为null，支持非课程相关通知）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "course_id")
    private Course course; // 关联课程ID（外键）

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content; // 通知内容

    @Column(name = "send_time", nullable = false)
    private LocalDateTime sendTime = LocalDateTime.now(); // 发送时间（默认当前时间）

    @Column(name = "notification_type", nullable = false)
    private Integer notificationType; // 通知类型（1=邮件，2=站内消息）
}