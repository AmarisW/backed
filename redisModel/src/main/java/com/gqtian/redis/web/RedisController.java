package com.gqtian.redis.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.redis.service.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis控制器
 *
 * 提供Redis各种操作的API端点，包括基本的字符串操作和分布式锁示例
 *
 * @author gqtian
 * @date 2025/12/15 17:28
 */
@Slf4j
@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisController {

  private final RedisService redisService;

  /**
   * 基础字符串操作
   *
   * 演示Redis的String类型操作，包括：
   * - 基本的键值对存取
   * - 自增操作
   * - 对象序列化存储
   * - 批量操作
   */
  @RequestMapping("/stringOps")
  public void stringOps() {
    redisService.stringOps();
  }

  /**
   * 哈希操作
   *
   * 演示Redis的Hash类型操作，包括：
   * - 单个属性的存取
   * - 批量属性操作
   * - 属性删除
   * - 整个哈希数据的删除
   */
  @RequestMapping("/hashOps")
  public void hashOps() {
      redisService.hashOps();
  }

  /**
   * 列表操作
   *
   * 演示Redis的List类型操作，包括：
   * - 队列操作（先进先出）
   * - 栈操作（先进后出）
   * - 列表范围查询
   * - 列表长度修剪
   */
  @RequestMapping("/listOps")
  public void listOps() {
      redisService.listOps();
  }

  /**
   * 集合操作
   *
   * 演示Redis的Set类型操作，包括：
   * - 元素的添加和移除
   * - 集合交集运算
   * - 集合并集运算
   * - 集合差集运算
   */
  @RequestMapping("/setOps")
  public void setOps() {
      redisService.setOps();
  }

  /**
   * 有序集合操作
   *
   * 演示Redis的ZSet类型操作，包括：
   * - 元素的添加和分数管理
   * - 升序和降序排列
   * - 分数增加
   * - 元素排名查询
   */
  @RequestMapping("/zSetOps")
  public void zSetOps() {
      redisService.zSetOps();
  }

}