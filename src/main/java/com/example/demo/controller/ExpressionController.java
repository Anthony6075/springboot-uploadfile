package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

@RestController
public class ExpressionController {
    private String expressionsPath="/root/expressions";

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
    public BufferedImage getImage(@PathVariable String expressionName) {
        try {
            return ImageIO.read(new FileInputStream(new File(expressionsPath + "/" + expressionName)));
        }
        catch (IOException e){
            /*JSONObject result=new JSONObject();
            result.put("code",-1);
            result.put("msg","显示图片异常");*/
            return null;
        }
    }
}
