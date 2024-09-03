package com.wxt.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Category {
//    不能不传，新增分类的不需要传id，id是自动增长的，添加和更新各有一套规则，更新时必须要有id
    @NotNull(groups = Update.class)
    private Integer id;//主键ID
//    不能不传且字符串不能是空字符串
    @NotEmpty
    private String categoryName;//分类名称
    @NotEmpty
    private String categoryAlias;//分类别名
    private Integer createUser;//创建人ID
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;//创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;//更新时间

//    添加组和更新组
//    如果说某个校验项没有指定分组，默认属于default分组
//    分组之前可以继承，A extends B  那么A中拥有B中所有的继承项
    public interface Add extends Default {

    }
    public interface Update extends Default{

    }
}
