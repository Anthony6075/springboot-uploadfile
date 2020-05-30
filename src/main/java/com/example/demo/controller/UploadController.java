package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
public class UploadController {
    private String  url;

    @RequestMapping("/file")
    public String file(){
        return "file";
    }

    @RequestMapping(value="/user/uploadFile",produces="application/json;charset=UTF-8")
    @ResponseBody
    public JSONObject uploadFile(@RequestParam("fileName") MultipartFile file) {
        JSONObject result=new JSONObject();
        //判断文件是否为空
        if (file.isEmpty()) {
            result.put("code",-1);
            result.put("msg","上传文件不可为空");
            return result;
        }

        String fileName = file.getOriginalFilename();
        //上传路径
        String path = "/root/fileupload/" +fileName;

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
            url="http://123.57.203.185:8088/files/"+fileName;
        } catch (IOException e) {
            result.put("code",-1);
            result.put("msg","上传失败");
            return result;
        }
        result.put("code",0);
        result.put("url",url);
        return result;
    }

    @RequestMapping(value="/files/{fileName}",produces="application/json;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<FileSystemResource> getFile(@PathVariable String fileName) {
        File file=new File("/root/fileupload/"+fileName);
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
