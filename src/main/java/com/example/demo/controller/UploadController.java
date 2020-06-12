package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
public class UploadController {
    private String  url;
    //private String serverAndPort="http://123.57.203.185:8088";
    private String uploadPath="/root/fileupload/";

    @Resource
    private ResourceLoader resourceLoader;

    @RequestMapping("/file")
    public String file(){
        return "file";
    }

    @RequestMapping(value="/uploadFile",produces="application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject uploadFile(@RequestParam("fileName") MultipartFile file) {
        System.out.println("uploadFile-start------");
        JSONObject result=new JSONObject();
        //判断文件是否为空
        if (file.isEmpty()) {
            result.put("code",-1);
            result.put("msg","上传文件不可为空");
            return result;
        }

        String fileName = file.getOriginalFilename();
        //加时间戳
        fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_" + fileName;
        //上传路径
        String path = uploadPath +fileName;

        //创建文件路径
        File dest = new File(path);

        //判断文件是否已经存在
        if (dest.exists()) {
            result.put("code",-1);
            result.put("msg","文件已存在");
            return result;
        }

        //判断文件父目录是否存在
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdir();
        }

        try {
            //上传文件
            file.transferTo(dest); //保存文件
            url="/display/"+fileName;
        } catch (IOException e) {
            result.put("code",-1);
            result.put("msg","上传失败");
            System.out.println("uploadFile-fail------");
            return result;
        }
        result.put("code",0);
        result.put("url",url);
        System.out.println("uploadFile-success------");
        return result;
    }

    @Bean
    public BufferedImageHttpMessageConverter bufferedImageHttpMessageConverter(){
        return new BufferedImageHttpMessageConverter();
    }

    @RequestMapping(value = "/display/{fileName}",
            produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE})
    @ResponseBody
    public Object show(@PathVariable String fileName){
        try
        {
            return ResponseEntity.ok(resourceLoader.getResource("file:" + "/root/fileupload/" + fileName));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value="/download/{fileName}",produces="application/json;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<FileSystemResource> getFile(@PathVariable String fileName) {
        File file=new File(uploadPath+fileName);
        return export(file);
    }

    public ResponseEntity<FileSystemResource> export(File file) {
        if (file == null) {
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment; filename=" + file.getName());
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Last-Modified", new Date().toString());
        headers.add("ETag", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(MediaType.parseMediaType("application/octet-stream")).body(new FileSystemResource(file));
    }
}
