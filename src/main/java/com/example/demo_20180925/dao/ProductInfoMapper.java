package com.example.demo_20180925.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.demo_20180925.pojo.ProductInfo;

@Mapper
public interface ProductInfoMapper {
	/**
	 * 根据产品代码查询产品信息
	 * 
	 * @param code
	 *            产品代码
	 * @return
	 */
	@Select("SELECT id,version,product_code as productCode,product_name as productName, product_count AS productCount FROM product_info WHERE product_code = #{code}")
	ProductInfo findByCode(@Param("code") String code);

	/**
	 * 根据产品代码查询产品信息；排他锁
	 * 
	 * @param code
	 *            产品代码
	 * @return
	 */
	@Select("SELECT id,version,product_code as productCode,product_name as productName, product_count AS productCount FROM product_info WHERE product_code = #{code} for update")
	ProductInfo selectForUpdate(@Param("code") String code);

	/**
	 * 根据产品更新产品数量
	 * 
	 * @param code
	 *            产品代码
	 * @param productCount
	 *            购买数量
	 * @return
	 */
	@Update("update product_info SET product_count=#{productCount} WHERE product_code = #{code}")
	int updateForFirst(@Param("code") String code,
			@Param("productCount") Long productCount);

	/**
	 * 根据购买数量直接减少库存
	 * 
	 * @param code
	 *            产品代码
	 * @param buys
	 *            购买数量
	 * @return
	 */
	@Update("update product_info SET product_count=product_count - #{buys} WHERE product_code = #{code}")
	int update(@Param("code") String code, @Param("buys") Long buys);

	/**
	 * 根据购买数量及版本号减少库存
	 * 
	 * @param code
	 *            产品代码
	 * @param buys
	 *            购买数量
	 * @param version
	 *            版本信息
	 * @return
	 */
	@Update("update product_info SET product_count=product_count - #{buys},version=version+1 WHERE product_code = #{code} and version = #{version}")
	int updateByVersion(@Param("code") String code, @Param("buys") Long buys,
			@Param("version") Long version);

	/**
	 * 根据购买数量及剩余库存减少库存
	 * 
	 * @param code
	 *            产品代码
	 * @param buys
	 *            购买数量
	 * @return
	 */
	@Update("update product_info SET product_count=product_count - #{buys} WHERE product_code = #{code} and (product_count - #{buys})>0")
	int updateByBuys(@Param("code") String code, @Param("buys") Long buys);
}
