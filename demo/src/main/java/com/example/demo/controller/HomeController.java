package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import java.io.*;
import java.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Controller
public class HomeController{

    @GetMapping("/RouletteDinner_home")    //一番最初に起動するやつ
    public String start(){
        return "home";
    }

    @GetMapping("/RouletteDinner_form")
    public String form(){
        return "form";
    }

    @PostMapping("/RouletteDinner_res")
    public ModelAndView result(ModelAndView mav ,@RequestParam String area) throws IOException{
        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode shopsNode =null;
        Random random = new Random();
        try {
            String url = "https://webservice.recruit.co.jp/hotpepper/gourmet/v1/?key=a5c3c9fb001ca296&large_area=" + prefecture() + "&budget+codeB008&results_available&format=json";
            // リクエストの作成
	  	    Request request = new Request.Builder().url(url).build();
            // レスポンスの取得
	  	    Response response = client.newCall(request).execute();
            // レスポンスのBody要素を取得
	  	    String responseBody = response.body().string();
            JsonNode rootNode = mapper.readTree(responseBody);
            int i = 0;
            //json長さチェック
            do {
                shopsNode = rootNode.get("results").get("shop").get(i);
                i++;
            } while (shopsNode != null);
            int randomValue = random.nextInt(i-1);
            //乱数で店を指定
            shopsNode = rootNode.get("results").get("shop").get(randomValue);
            //名前と住所をもらう
            String name = shopsNode.get("name").asText();
            String address = shopsNode.get("address").asText();
            mav.addObject("name", name);
            mav.addObject("address",address);
            mav.setViewName("result");
            return mav;
        } catch(JsonProcessingException e) {
            e.printStackTrace();
            return mav;
        }
    }
    //都道府県コードをもらうためのメソッド
    public String prefecture() throws IOException {
        // リクエストを送るURLを定義する（Json形式に値を修正）
        JsonNode shopsNode =null;
        String url = "https://webservice.recruit.co.jp/hotpepper/large_area/v1/?key=a5c3c9fb001ca296&format=json";
        // http通信を行う
        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper();
        // リクエストの作成
        Request request = new Request.Builder().url(url).build();
        // レスポンスの取得
        Response response = client.newCall(request).execute();
        // レスポンスのBody要素を取得
        String responseBody = response.body().string();
        JsonNode rootNode = mapper.readTree(responseBody);
        int i = 0;
        do {
            shopsNode = rootNode.get("results").get("large_area").get(i);
            //都道府県比べるとこ
            if(shopsNode.get("name").asText().equals("青森"))
                break;
            i++;
        } while (shopsNode != null);
        //都道府県のコードをとる
        String prefe = shopsNode.get("code").asText();
        return prefe;

    }
}
