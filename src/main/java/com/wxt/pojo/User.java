package com.wxt.pojo;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

//lombok在编译阶段，为实体类自动生成setter,getter,toString
//pom文件中引入依赖  在实体类上添加注释
@Data
public class User {
    @NotNull
    private Integer id;//主键ID
    private String username;//用户名
    @JsonIgnore //让springMvc把当前对象转成JSON字符串的时候，忽略password，最终的JSON字符串中就没有password这个属性
    private String password;//密码
    @NotEmpty
//    正则表达式,大写S
    @Pattern(regexp = "^\\S{1,10}$")
    private String nickname;//昵称
    @NotEmpty
    @Email
    private String email;//邮箱
    private String userPic;//用户头像地址,驼峰命名，数据库是下划线命名
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间
}
