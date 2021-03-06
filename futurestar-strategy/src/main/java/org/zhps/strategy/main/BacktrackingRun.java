package org.zhps.strategy.main;

import com.alibaba.fastjson.JSON;
import org.hbase.async.HBaseClient;
import org.hbase.async.KeyValue;
import org.hbase.async.Scanner;
import org.zhps.base.hbase.BaseHbase;
import org.zhps.strategy.average.Average5d10d;
import org.zhps.strategy.util.LogMapUtil;
import org.zhps.strategy.vo.QuotationVO;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Copyright (c) 2012 Conversant Solutions. All rights reserved.
 * <p>
 * Created on 2017/3/11.
 */
public class BacktrackingRun {
    public static void main(String[] args) {
//        ArrayList<Ave5d10dVO> ave5d10dVOs = new ArrayList<Ave5d10dVO>();
        HBaseClient hBaseClient = BaseHbase.gethBaseClient();
        Scanner scanner = hBaseClient.newScanner("close");
        scanner.setStartKey("rb10|79819385|20180615");
        scanner.setStopKey("rb10|79828880|20171120");
//        scanner.setStartKey("rb|79838770|20161230");
//        scanner.setStopKey("rb|79839899|20160101");
//        scanner.setStartKey("rb|79848769|20151231");
//        scanner.setStopKey("rb|79849895|20150105");
//        scanner.setStartKey("rb|79858769|20141231");
//        scanner.setStopKey("rb|79859899|20140101");
//        scanner.setStartKey("rb|79868769|20131231");
//        scanner.setStopKey("rb|79869899|20130101");
//        scanner.setStartKey("rb|79878769|20121231");
//        scanner.setStopKey("rb|79879899|20120101");
//        scanner.setStartKey("rb|79888769|20111231");
//        scanner.setStopKey("rb|79889899|20110101");
        ArrayList<ArrayList<KeyValue>> datas = null;
        try {
            datas = scanner.nextRows(10000).joinUninterruptibly();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.reverse(datas);
        QuotationVO ave5d10dVO = new QuotationVO();
        if(datas != null){
            for(ArrayList<KeyValue> rows : datas){
                for(KeyValue keyValue : rows){
                    String key = new String(keyValue.key());
                    ave5d10dVO.setTradingDay(key.split("\\|")[2]);
                    if(new String(keyValue.qualifier()).equals("last")){
                        ave5d10dVO.setLast(Double.parseDouble(new String(keyValue.value())));
                    }
                    if(new String(keyValue.qualifier()).equals("ave5d")){
                        ave5d10dVO.setAve5d(Double.parseDouble(new String(keyValue.value())));
                    }
                    if(new String(keyValue.qualifier()).equals("ave10d")){
                        ave5d10dVO.setAve10d(Double.parseDouble(new String(keyValue.value())));
                    }
                    if(new String(keyValue.qualifier()).equals("open")){
                        ave5d10dVO.setOpen(Double.parseDouble(new String(keyValue.value())));
                    }
                    if(new String(keyValue.qualifier()).equals("highest")){
                        ave5d10dVO.setHighest(Double.parseDouble(new String(keyValue.value())));
                    }
                    if(new String(keyValue.qualifier()).equals("lowest")){
                        ave5d10dVO.setLowest(Double.parseDouble(new String(keyValue.value())));
                    }
                }
//                ave5d10dVOs.add(ave5d10dVO);
                ave5d10dVO = Average5d10d.exec(ave5d10dVO);
                ave5d10dVO.setLast5d(ave5d10dVO.getAve5d());
                ave5d10dVO.setLast10d(ave5d10dVO.getAve10d());
            }
//            System.out.println(ave5d10dVOs);
        }
        System.out.println(LogMapUtil.closeProfitList);
        System.out.println(LogMapUtil.tradeList);
        double profit = 0;
        for(double value : LogMapUtil.closeProfitList){
            profit += value;
        }
        System.out.println("profit is: " + profit);
    }
}
