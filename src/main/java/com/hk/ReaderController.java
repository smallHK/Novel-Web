package com.hk;

import com.hk.reader.ReadContent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * @Author: smallHK
 * @Date: 2019/3/11 11:58
 * 控制器，获取页面请求，返回结果
 */
@Controller("read")
public class ReaderController {

    private ReadContent readContent;



    @GetMapping("content")
    public Map query() {

        readContent.readCapter();

        return null;
    }


}
