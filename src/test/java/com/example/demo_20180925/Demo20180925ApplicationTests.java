package com.example.demo_20180925;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo_20180925.pojo.ProductInfo;
import com.example.demo_20180925.service.ProductInfoService;

@RunWith(SpringRunner.class)
// 引入SpringBootTest并生成随机接口
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class Demo20180925ApplicationTests {

	// 商品代码
	private static final String CODE = "IPONE XR";
	// 商品总数
	private static final Long PRODUCTCOUNT = (long) 1000;
	// 并发人数
	private static final int USER_NUM = 1000;
	// 发令枪；用于模拟高并发
	private static CountDownLatch countDownLatch = new CountDownLatch(USER_NUM);
	// 计数器，用于记录成功购买客户人数
	private static int successPerson = 0;
	// 计数器，用于记录卖出去对的商品个数
	private static int saleOutNum = 0;
	// 计数器，用于记录处理总时间
	private static long doTime = 0;
	// 计数器，用于记录处理最长时间
	private static long maxTime = 0;
	@Autowired
	ProductInfoService productInfoService;

	@Before
	public void init() {
		// 初始化库存
		ProductInfo productInfo = new ProductInfo();
		productInfo.setProductCode(CODE);
		productInfo.setProductCount(PRODUCTCOUNT);
		this.productInfoService.updateFirst(productInfo);
	}

	@Test
	public void testSeckill() throws InterruptedException {
		// 循环初始换USER_NUM个请求实例
		for (int i = 0; i < USER_NUM; i++) {
			new Thread(new BuyProduct(CODE, (long) 3)).start();
			if (i == USER_NUM) {
				Thread.currentThread().sleep(2000);// 最后一个子线程时休眠两秒等待所有子线程全部准备好
			}
			countDownLatch.countDown();// 发令枪减1，到0时启动发令枪
		}
		Thread.currentThread().sleep(10 * 1000);// 主线程休眠10秒等待结果
		// Thread.currentThread().join();
		System.out.println("购买成功人数：" + successPerson);
		System.out.println("销售成功个数：" + saleOutNum);
		System.out.println("剩余个数："
				+ productInfoService.selectByCode(CODE).getProductCount());
		System.out.println("处理时间：" + doTime);
		System.out.println("最大处理时间：" + maxTime);
	}

	public class BuyProduct implements Runnable {
		private String code;
		private Long buys;

		public BuyProduct(String code, Long buys) {
			this.code = code;
			this.buys = buys;
		}

		public void run() {
			try {
				countDownLatch.await();// 所有子线程运行到这里休眠，等待发令枪指令
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 直接减库存
			// Map<String, Object> map = productInfoService.update(code, buys);

			// 加排他锁（悲观锁）后减库存
			// Map<String, Object> map =
			// productInfoService.selectForUpdate(code,
			// buys);

			// 根据版本号加乐观锁减库存
			// Map<String, Object> map =
			// productInfoService.updateByVersion(code,
			// buys);

			// 根据库存加乐观锁减库存
			// Map<String, Object> map = productInfoService.updateByBuys(code,
			// buys);

			// 根据缓存减库存
			Map<String, Object> map = productInfoService.updateByRedis(code,
					buys);
			if ((boolean) map.get("result")) {
				synchronized (countDownLatch) {
					// 更新库存成功，修改购买成功人数及销售产品数量
					successPerson++;
					// 记录总购买成功人数
					saleOutNum = (int) (saleOutNum + buys);
					// 记录总消费时间
					doTime = doTime + (long) map.get("time");
					// 记录最大时间
					if (maxTime < (long) map.get("time")) {
						maxTime = (long) map.get("time");
					}
				}
			}
		}
	}
}
