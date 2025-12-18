package com.gqtian.redis.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 滑动窗口限流服务
 * 
 * 核心实现原理：
 * 1. 使用Redis ZSet存储请求时间戳，score为时间戳（毫秒）
 * 2. 每次请求时：
 *    - 获取当前时间戳
 *    - 删除窗口外（当前时间 - 窗口大小）的所有记录
 *    - 统计窗口内的请求数
 *    - 如果请求数 < 限制数，则通过，并记录当前请求时间戳
 *    - 否则拒绝请求
 * 3. 自动过期时间为窗口时长，避免长期占用内存
 * 
 * 优势：
 * - 精确：完全按照时间窗口计算，没有固定窗口的边界问题
 * - 灵活：可以支持任意时间粒度的限流
 * - 准确：每个请求都有确切的时间戳记录
 * 
 * @className SlidingWindowLimitService
 * @author gqtian
 * @date 2025/12/18 11:09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SlidingWindowLimitService {





}
