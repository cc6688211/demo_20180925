package com.example.demo_20180925.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo_20180925.dao.ProductInfoMapper;
import com.example.demo_20180925.pojo.ProductInfo;
import com.example.demo_20180925.service.ProductInfoService;

@Service
public class ProductInfoServiceImpl implements ProductInfoService {
	@Autowired
	ProductInfoMapper productInfoMapper;
	@Autowired
	RedisTemplate<String, Object> redisTemplate;
	private static long threadCount = 0;

	/**
	 * 根据产品代码查询产品
	 */
	@Override
	public ProductInfo selectByCode(String code) {
		return productInfoMapper.findByCode(code);
	}

	/**
	 * 初始化库存
	 */
	@Override
	public boolean updateFirst(ProductInfo productInfo) {
		redisTemplate.opsForHash().put("productCount",
				productInfo.getProductCode(), productInfo.getProductCount());
		return productInfoMapper.updateForFirst(productInfo.getProductCode(),
				productInfo.getProductCount()) > 0 ? true : false;
	}

	/**
	 * 直接减库存
	 */
	@Override
	public Map<String, Object> update(String code, Long buys) {
		threadCount++;
		System.out.println("开启线程：" + threadCount);
		Date date = new Date();
		Map<String, Object> map = new HashMap<String, Object>();
		ProductInfo productInfo = productInfoMapper.findByCode(code);
		if (productInfo.getProductCount() < buys) {
			map.put("result", false);
			Date date1 = new Date();
			map.put("time", date1.getTime() - date.getTime());
			return map;
		}
		map.put("result", productInfoMapper.update(code, buys) > 0 ? true
				: false);
		Date date1 = new Date();
		map.put("time", date1.getTime() - date.getTime());
		return map;
	}

	/**
	 * 加排他锁减库存
	 */
	@Transactional
	@Override
	public Map<String, Object> selectForUpdate(String code, Long buys) {
		threadCount++;
		System.out.println("开启线程：" + threadCount);
		Date date = new Date();
		Map<String, Object> map = new HashMap<String, Object>();
		ProductInfo productInfo = productInfoMapper.selectForUpdate(code);
		if (productInfo.getProductCount() < buys) {
			map.put("result", false);
			Date date1 = new Date();
			map.put("time", date1.getTime() - date.getTime());
			return map;
		}
		map.put("result", productInfoMapper.update(code, buys) > 0 ? true
				: false);
		Date date1 = new Date();
		map.put("time", date1.getTime() - date.getTime());
		return map;
	}

	/**
	 * 根据版本号加乐观锁减库存
	 */
	@Override
	public Map<String, Object> updateByVersion(String code, Long buys) {
		Date date = new Date();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			threadCount++;
			System.out.println("开启线程：" + threadCount);
			ProductInfo productInfo = productInfoMapper.findByCode(code);
			if (productInfo.getProductCount() < buys) {
				map.put("result", false);
				Date date1 = new Date();
				map.put("time", date1.getTime() - date.getTime());
				return map;
			}
			if (productInfoMapper.updateByVersion(code, buys,
					productInfo.getVersion()) > 0 ? true : false) {
				map.put("result", true);
				Date date1 = new Date();
				map.put("time", date1.getTime() - date.getTime());
				return map;
			}
			waitForLock();
			return updateByVersion(code, buys);
		} catch (Exception e) {
			System.err.println(e);
			map.put("result", false);
			Date date1 = new Date();
			map.put("time", date1.getTime() - date.getTime());
			return map;
		}

	}

	/**
	 * 根据库存加乐观锁减库存
	 */
	@Override
	public Map<String, Object> updateByBuys(String code, Long buys) {
		threadCount++;
		System.out.println("开启线程：" + threadCount);
		Date date = new Date();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			ProductInfo productInfo = productInfoMapper.findByCode(code);
			if (productInfo.getProductCount() < buys) {
				map.put("result", false);
				Date date1 = new Date();
				map.put("time", date1.getTime() - date.getTime());
				return map;
			}
			if (productInfoMapper.updateByBuys(code, buys) > 0 ? true : false) {
				map.put("result", true);
			}else{
				map.put("result", false);
			}
			Date date1 = new Date();
			map.put("time", date1.getTime() - date.getTime());
			return map;
//			waitForLock();
//			return updateByBuys(code, buys);
		} catch (Exception e) {
			System.err.println(e);
			map.put("result", false);
			Date date1 = new Date();
			map.put("time", date1.getTime() - date.getTime());
			return map;

		}
	}

	@Override
	public Map<String, Object> updateByRedis(String code, Long buys) {
		threadCount++;
		System.out.println("开启线程：" + threadCount);
		Date date = new Date();
		Map<String, Object> map = new HashMap<String, Object>();
		long count = Long.valueOf(redisTemplate.opsForHash().get(
				"productCount", code)
				+ "");
		if (count > 0) {
			count = Long.valueOf(redisTemplate.opsForHash().increment(
					"productCount", code, -buys));
			if (count >= 0) {
				map.put("result", true);
				Date date1 = new Date();
				map.put("time", date1.getTime() - date.getTime());
				return map;
			} else {
				map.put("result", false);
				Date date1 = new Date();
				map.put("time", date1.getTime() - date.getTime());
				return map;
			}
		} else {
			map.put("result", false);
			Date date1 = new Date();
			map.put("time", date1.getTime() - date.getTime());
			return map;
		}
	}

	// 错峰执行
	private void waitForLock() {
		try {
			Thread.sleep(new Random().nextInt(10) + 1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
