package com.example.demo_20180925.pojo;

public class ProductInfo {
	private Long id;
	private Long version;
	private String productCode;
	private String productName;
	private Long productCount;
	private Long productBuys;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Long getProductCount() {
		return productCount;
	}
	public void setProductCount(Long productCount) {
		this.productCount = productCount;
	}
	public Long getProductBuys() {
		return productBuys;
	}
	public void setProductBuys(Long productBuys) {
		this.productBuys = productBuys;
	}
	public ProductInfo() {
		super();
	}
	
}
