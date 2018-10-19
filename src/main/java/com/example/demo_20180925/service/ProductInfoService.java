package com.example.demo_20180925.service;

import java.util.Map;

import com.example.demo_20180925.pojo.ProductInfo;

public interface ProductInfoService {
	ProductInfo selectByCode(String code);

	boolean updateFirst(ProductInfo productInfo);

	Map<String, Object> selectForUpdate(String code, Long buys);

	Map<String, Object> update(String code, Long buys);

	Map<String, Object> updateByVersion(String code, Long buys);

	Map<String, Object> updateByBuys(String code, Long buys);

	Map<String, Object> updateByRedis(String code, Long buys);
}
