package com.gqtian.rocket.simulation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.gqtian.rocket.rmq.ProducerService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻 模拟数据
 * 
 * @className SimulationService
 * @author gqtian
 * @date 2025/1/3 15:20
 */
@Slf4j
@Service
public class SimulationService {

    public static boolean IS_SEND = false;

    @Resource
    @Qualifier("testThread")
    private ThreadPoolExecutor gpsDataAnalyseThread;

    @Resource
    private ProducerService producerService;

    // 车牌号的组成一般为：省份+地区代码+5位数字/字母
    private static String generateCarId() {

        char[] provinceAbbr = { // 省份简称 4+22+5+3
            '京', '津', '沪', '渝', '冀', '豫', '云', '辽', '黑', '湘', '皖', '鲁', '苏', '浙', '赣', '鄂', '甘', '晋', '陕', '吉', '闽',
            '贵', '粤', '青', '川', '琼', '宁', '新', '藏', '桂', '蒙', '港', '澳', '台'};
        String alphas = "QWERTYUIOPASDFGHJKLZXCVBNM1234567890"; // 26个字母 + 10个数字

        Random random = new Random(); // 随机数生成器

        StringBuilder carId = new StringBuilder();

        // 省份+地区代码+· 如 湘A· 这个点其实是个传感器，不过加上美观一些
        carId.append(provinceAbbr[random.nextInt(34)]); // 注意：分开加，因为加的是2个char
        carId.append(alphas.charAt(random.nextInt(26)));

        // 5位数字/字母
        for (int i = 0; i < 5; i++) {
            carId.append(alphas.charAt(random.nextInt(36)));
        }
        return carId.toString();
    }

    /**
     * 生成模拟数据
     */
    public void simulationGpsData(String rocketId, String topic, boolean isSend) {
        IS_SEND = isSend;
        while (IS_SEND) {
            Date date = new Date();
            gpsDataAnalyseThread.execute(() -> {
                List<GpsData> gpsDataList = new ArrayList<>();
                for (int i = 0; i < 500; i++) {
                    double[] randomLatLng = getRandomLatLng();
                    GpsData gpsData = GpsData.builder().plateNumber(generateCarId()).plateColor(1).terminalTime(date)
                        .originalLongitude(randomLatLng[0]).originalLatitude(randomLatLng[1]).longitude(randomLatLng[0])
                        .latitude(randomLatLng[1]).gpsSpeed(100.1).speed(100.1).mileage(100.1).altitude(100).alarm(100L)
                        .category(1).direction(0).earlyWarning(0).fatigueDriving(0).industry("030").overSpeed(0)
                        .operation(0).platformCode(41070007L).receiveTime(date).status(786562L).mileage(100.1).build();
                    gpsDataList.add(gpsData);
                }
                producerService.sendMessage(rocketId, JSON.toJSONString(gpsDataList), topic);
            });
        }
    }

    /**
     * 生成随机的经纬度（国内）
     */
    public static double[] getRandomLatLng() {
        Random random = new Random();
        double lat = 22.0 + random.nextDouble() * (30.0 - 22.0);
        double lng = 113.0 + random.nextDouble() * (125.0 - 113.0);
        return new double[] {lng, lat};
    }

}
