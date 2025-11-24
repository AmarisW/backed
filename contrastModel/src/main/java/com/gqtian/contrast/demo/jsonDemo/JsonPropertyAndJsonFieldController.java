package com.gqtian.contrast.demo.jsonDemo;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSON;
import com.gqtian.contrast.demo.jsonDemo.dto.JsonFieldAndJsonPropertyDto;
import com.gqtian.contrast.demo.jsonDemo.dto.JsonFieldDto;
import com.gqtian.contrast.demo.jsonDemo.dto.JsonPropertyDto;

/**
 * 🔔 @JsonField 和 @JsonProperty 区别
 *
 * @author GQTIAN
 * @date 2025/4/28 14:29
 **/
@RestController
@RequestMapping("/jsonDemo")
public class JsonPropertyAndJsonFieldController {

    public static void main(String[] args) {
        JsonFieldDto jsonFieldDto3 = new JsonFieldDto();
        jsonFieldDto3.setName("name");
        jsonFieldDto3.setUnTest("unTest");
        jsonFieldDto3.setAge(1);
        jsonFieldDto3.setJsonOne("jsonOne");
        System.out.println(JSON.toJSONString(jsonFieldDto3));

        // JsonList
        String jsonList = "[{\"na\":\"na\",\"un_test\":\"un_test\",\"ag\":1,\"jsonOne\":\"jsonOne\"},"
            + "{\"name\":\"name\",\"unTest\":\"unTest\",\"age\":1,\"jo\":\"jsonOne\"}]";
        List<JsonFieldDto> jsonFieldDtoList = JSON.parseArray(jsonList, JsonFieldDto.class);
        System.out.println(jsonFieldDtoList);

    }

    /**
     * 测试 jsonFieldAndJsonPropertyDto 参数接收
     */
    @RequestMapping("/jsonFieldAndJsonPropertyDto")

    public Object jsonFieldTest(@Validated @RequestBody JsonFieldAndJsonPropertyDto jsonFieldAndJsonPropertyDto) {
        return jsonFieldAndJsonPropertyDto;
    }

    /**
     * JsonProperty 前端传参参数名和后台接收对象属性名不一致 @RequestBody 用法
     */
    @RequestMapping("/jsonPropertyTest")
    public Object jsonPropertyTest(@RequestBody JsonPropertyDto jsonPropertyDto) {
        return jsonPropertyDto;
    }

    /**
     * JSONField 入参不会转化 返回参数会转化
     */
    @RequestMapping("/JsonFieldTest")
    public Object JsonFieldTest(@RequestBody JsonFieldDto jsonFieldDto1) {
        // 内部JSON转化
        String json = "{\"na\":\"demoData\",\"un_test\":\"demoData\",\"ag\":1," + "\"jsonOne\":\"demoData\"}";
        JsonFieldDto jsonFieldDto2 = JSON.parseObject(json, JsonFieldDto.class);
        System.out.println(jsonFieldDto2);

        // JsonList
        String jsonList = "[{\"na\":\"na\",\"un_test\":\"un_test\",\"ag\":1,\"jsonOne\":\"jsonOne\"},"
            + "{\"name\":\"name\",\"unTest\":\"unTest\",\"age\":1,\"jo\":\"jsonOne\"}]";
        List<JsonFieldDto> jsonFieldDtoList = JSON.parseArray(jsonList, JsonFieldDto.class);
        System.out.println(jsonFieldDtoList);

        JsonFieldDto jsonFieldDto3 = new JsonFieldDto();
        jsonFieldDto3.setName("name");
        jsonFieldDto3.setUnTest("unTest");
        jsonFieldDto3.setAge(1);
        jsonFieldDto3.setJsonOne("jsonOne");
        System.out.println(JSON.toJSONString(jsonFieldDto3));

        return jsonFieldDto1;
    }

}
