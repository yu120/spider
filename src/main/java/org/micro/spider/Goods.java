package org.micro.spider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goods implements Serializable {

    /**
     * 商品ID
     */
    @Attrs({@Attr(app = App.TAO_BAOl, value = "nid")})
    private String id;
    /**
     * 商品标题
     */
    @Attr(app = App.TAO_BAOl, value = "title")
    private String title;
    /**
     * 原始标题
     */
    @Attr(app = App.TAO_BAOl, value = "raw_title")
    private String rawTitle;
    /**
     * 类别
     */
    @Attr(app = App.TAO_BAOl, value = "category")
    private String category;
    /**
     * 商品售价
     */
    @Attr(app = App.TAO_BAOl, value = "view_price")
    private BigDecimal sellingPrice;
    /**
     * 销量
     */
    @Attr(app = App.TAO_BAOl, value = "view_sales")
    private Integer viewSales;

    /**
     * 主图地址
     */
    @Attr(app = App.TAO_BAOl, value = "pic_url")
    private String picUrl;
    /**
     * 商店名称
     */
    @Attr(app = App.TAO_BAOl, value = "nick")
    private String shopName;
    /**
     * 商店地址
     */
    @Attr(app = App.TAO_BAOl, value = "shopLink")
    private String shopLink;
    /**
     * 发货地址
     */
    @Attr(app = App.TAO_BAOl, value = "item_loc")
    private String shopAddress;
    /**
     * 商品详情页面
     */
    @Attr(app = App.TAO_BAOl, value = "detail_url")
    private String detailUrl;

}
