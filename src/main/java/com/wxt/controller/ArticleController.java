package com.wxt.controller;

import com.wxt.pojo.Article;
import com.wxt.pojo.PageBean;
import com.wxt.pojo.Result;
import com.wxt.service.ArticleService;
import com.wxt.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @PostMapping
    public Result add(@RequestBody @Validated(Article.Add.class) Article article){
        articleService.add(article);
        return Result.success();
    }
    @GetMapping
    public Result<PageBean<Article>> list(
            Integer pageNum,
            Integer pageSize,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false)  String state
    ){
        PageBean<Article> pb=articleService.list(pageNum,pageSize,categoryId,state);
        return Result.success(pb);
    }
    @GetMapping("/detail")
    public Result<Article> detail(Integer id){
        Article ac=articleService.findById(id);
        return Result.success(ac);
    }

    @PutMapping
    public Result<Article> update(@RequestBody @Validated(Article.Update.class) Article article){
        articleService.update(article);
        return Result.success();
    }
    @DeleteMapping
    public Result<Article> delete(Article article){
        articleService.delete(article);
        return Result.success();
    }
}
