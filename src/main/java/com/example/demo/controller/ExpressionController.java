package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;

@RestController
public class ExpressionController {
    private String expressionsPath="/root/expressions";

    @Resource
    private ResourceLoader resourceLoader;

    @GetMapping("/expre/getExpression")
    public JSONObject getExpression() {
        File expressions=new File(expressionsPath);
        String[] es=expressions.list();
        ArrayList<JSONObject> resultExpressions=new ArrayList<>();
        for(String e:es){
            JSONObject eJson=new JSONObject();
            eJson.put("name",e);
            eJson.put("url","/expre/"+e);
            resultExpressions.add(eJson);
        }
        JSONObject result=new JSONObject();
        result.put("code",0);
        result.put("data",resultExpressions);
        return result;
    }

    @RequestMapping(value = "/expre/{expressionName}",
            produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE})
    @ResponseBody
    public Object show(@PathVariable String expressionName){
        try
        {
            return ResponseEntity.ok(resourceLoader.getResource("file:" + "/root/expressions/" + expressionName));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
