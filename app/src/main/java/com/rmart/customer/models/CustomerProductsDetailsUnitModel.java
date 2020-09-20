package com.rmart.customer.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Satya Seshu on 08/09/20.
 */
public class CustomerProductsDetailsUnitModel implements Serializable {

    @SerializedName("product_unit_id")
    @Expose
    private Integer productUnitId;
    @SerializedName("product_id")
    @Expose
    private Integer productId;
    @SerializedName("unit_price")
    @Expose
    private String unitPrice;
    @SerializedName("unit_number")
    @Expose
    private String unitNumber;
    @SerializedName("selling_price")
    @Expose
    private String sellingPrice;
    @SerializedName("unit_measure")
    @Expose
    private String unitMeasure;
    @SerializedName("product_unit_quantity")
    @Expose
    private Integer productUnitQuantity;
    @SerializedName("short_unit_measure")
    @Expose
    private String shortUnitMeasure;
    @SerializedName("product_discount")
    @Expose
    private Integer productDiscount;

    public Integer getProductUnitId() {
        return productUnitId;
    }

    public void setProductUnitId(Integer productUnitId) {
        this.productUnitId = productUnitId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getUnitMeasure() {
        return unitMeasure;
    }

    public void setUnitMeasure(String unitMeasure) {
        this.unitMeasure = unitMeasure;
    }

    public Integer getProductUnitQuantity() {
        return productUnitQuantity;
    }

    public void setProductUnitQuantity(Integer productUnitQuantity) {
        this.productUnitQuantity = productUnitQuantity;
    }

    public String getShortUnitMeasure() {
        return shortUnitMeasure;
    }

    public void setShortUnitMeasure(String shortUnitMeasure) {
        this.shortUnitMeasure = shortUnitMeasure;
    }

    public Integer getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(Integer productDiscount) {
        this.productDiscount = productDiscount;
    }
}