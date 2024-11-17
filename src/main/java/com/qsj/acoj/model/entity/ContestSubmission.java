package com.qsj.acoj.model.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("contest_submission")
public class ContestSubmission implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long submissionId;
    
    private Long contestId;
    
    private Long problemId;

    
    private static final long serialVersionUID = 1L;
} 