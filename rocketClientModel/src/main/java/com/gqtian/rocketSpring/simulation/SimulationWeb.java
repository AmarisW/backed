package com.gqtian.rocketSpring.simulation;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 模拟数据生成
 * 
 * @className SimulationWeb
 * @author gqtian
 * @date 2025/1/3 16:14
 */
@Slf4j
@RestController
@RequestMapping("/simulation")
@RequiredArgsConstructor
public class SimulationWeb {

    private final SimulationService simulationService;

    /**
     * 模拟数据 生成
     * 
     * @param rocketId rocketId
     * @param topic topic
     * @param isSend isSend
     */
    @RequestMapping("/simulationGpsData")
    public void simulationGpsData(String rocketId, String topic, boolean isSend) {
        simulationService.simulationGpsData(rocketId, topic, isSend);
    }

}
