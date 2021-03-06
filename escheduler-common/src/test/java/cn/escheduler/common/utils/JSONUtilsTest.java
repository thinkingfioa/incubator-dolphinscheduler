/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.escheduler.common.utils;

import cn.escheduler.common.enums.DataType;
import cn.escheduler.common.enums.Direct;
import cn.escheduler.common.process.Property;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JSONUtilsTest {


    @Test
    public void toMap() {

        String jsonStr = "{\"id\":\"1001\",\"name\":\"Jobs\"}";

        Map<String,String> models = JSONUtils.toMap(jsonStr);
        Assert.assertEquals(models.get("id"), "1001");
        Assert.assertEquals(models.get("name"), "Jobs");

    }

    @Test
    public void convert2Property(){
        Property property = new Property();
        property.setProp("ds");
        property.setDirect(Direct.IN);
        property.setType(DataType.VARCHAR);
        property.setValue("sssssss");
        String str = "{\"direct\":\"IN\",\"prop\":\"ds\",\"type\":\"VARCHAR\",\"value\":\"sssssss\"}";
        Property property1 = JSONObject.parseObject(str, Property.class);
        Direct direct = property1.getDirect();
        Assert.assertEquals(direct , Direct.IN);
    }


    @Test
    public void String2MapTest(){
        String str = list2String();

        List<LinkedHashMap> maps = JSONUtils.toList(str,
                LinkedHashMap.class);

        Assert.assertEquals(maps.size(), 1);
        Assert.assertEquals(maps.get(0).get("mysql服务名称"), "mysql200");
        Assert.assertEquals(maps.get(0).get("mysql地址"), "192.168.xx.xx");
        Assert.assertEquals(maps.get(0).get("端口"), "3306");
        Assert.assertEquals(maps.get(0).get("期间内没有使用索引的查询数握"), "80");
        Assert.assertEquals(maps.get(0).get("数据库客户端连接数"), "190");
    }

    public String list2String(){

        LinkedHashMap<String, String> map1 = new LinkedHashMap<>();
        map1.put("mysql服务名称","mysql200");
        map1.put("mysql地址","192.168.xx.xx");
        map1.put("端口","3306");
        map1.put("期间内没有使用索引的查询数握","80");
        map1.put("数据库客户端连接数","190");

        List<LinkedHashMap<String, String>> maps = new ArrayList<>();
        maps.add(0,map1);
        String resultJson = JSONUtils.toJson(maps);
        return resultJson;
    }

    @Test
    public void testToJson() {
        Map<String, String> map = new HashMap<>();
        map.put("foo","bar");

        Assert.assertEquals("{\"foo\":\"bar\"}", JSONUtils.toJson(map));
        Assert.assertEquals(
                String.valueOf((Object) null), JSONUtils.toJson(null));
    }

    @Test
    public void testParseObject() {
        Assert.assertEquals("{\"foo\":\"bar\"}", JSONUtils.parseObject(
                "{\n" + "\"foo\": \"bar\",\n" + "}", String.class));

        Assert.assertNull(JSONUtils.parseObject("", null));
        Assert.assertNull(JSONUtils.parseObject("foo", String.class));
    }

    @Test
    public void testToList() {
        Assert.assertEquals(new ArrayList(),
                JSONUtils.toList("A1B2C3", null));
        Assert.assertEquals(new ArrayList(),
                JSONUtils.toList("", null));
    }

    @Test
    public void testCheckJsonVaild() {
        Assert.assertTrue(JSONUtils.checkJsonVaild("3"));
        Assert.assertFalse(JSONUtils.checkJsonVaild(""));
    }

    @Test
    public void testFindValue() {
        Assert.assertNull(JSONUtils.findValue(
                new ArrayNode(new JsonNodeFactory(true)), null));
    }

    @Test
    public void testToMap() {
        Map<String, String> map = new HashMap<>();
        map.put("foo","bar");

        Assert.assertTrue(map.equals(JSONUtils.toMap(
                "{\n" + "\"foo\": \"bar\",\n" + "}")));

        Assert.assertFalse(map.equals(JSONUtils.toMap(
                "{\n" + "\"bar\": \"foo\",\n" + "}")));

        Assert.assertNull(JSONUtils.toMap("3"));
        Assert.assertNull(JSONUtils.toMap(null));
        Assert.assertNull(JSONUtils.toMap("3", null, null));
        Assert.assertNull(JSONUtils.toMap(null, null, null));
    }

    @Test
    public void testToJsonString() {
        Map<String, Object> map = new HashMap<>();
        map.put("foo", "bar");

        Assert.assertEquals("{\"foo\":\"bar\"}",
                JSONUtils.toJsonString(map));
        Assert.assertEquals(String.valueOf((Object) null),
                JSONUtils.toJsonString(null));
    }
}
