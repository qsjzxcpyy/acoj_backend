package com.qsj.acoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qsj.acoj.model.entity.ContestProblem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 比赛题目关联数据库操作
 */
@Mapper
public interface ContestProblemMapper extends BaseMapper<ContestProblem> {

    /**
     * 批量插入比赛题目关联
     * @param contestProblems
     * @return
     */
    @Insert("<script>" +
            "INSERT INTO contest_problem (contestId, problemId, problemOrder) VALUES " +
            "<foreach collection='contestProblems' item='item' separator=','>" +
            "(#{item.contestId}, #{item.problemId}, #{item.problemOrder})" +
            "</foreach>" +
            "</script>")
    boolean insertBatch(@Param("contestProblems") List<ContestProblem> contestProblems);
} 