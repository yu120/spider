package org.micro.spider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaoBaoGoods implements Serializable {

    /**
     * 商品ID
     */
    private String nid;
    /**
     * 主图地址
     */
    private String pic_url;
    /**
     * 商品标题
     */
    private String title;
    /**
     * 原始标题
     */
    private String raw_title;
    /**
     * 商品售价
     */
    private String view_price;
    /**
     * 销量
     */
    private String view_sales;
    /**
     * 商店名称
     */
    private String nick;
    /**
     * 商店地址
     */
    private String shopLink;
    /**
     * 发货地址
     */
    private String item_loc;
    /**
     * 商品详情页面
     */
    private String detail_url;
    /**
     * 类别
     */
    private String category;

}
