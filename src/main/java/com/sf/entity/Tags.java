package com.sf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author leung
 * @since 2022-06-02
 */
@TableName("tb_tags")
@ApiModel(value = "Tags对象", description = "")
public class Tags implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("ID")
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("标签内容")
      private String content;

      @ApiModelProperty("标签热度")
      private Long counts;

      @ApiModelProperty("版本号")
      private Long version;

    
    public Integer getId() {
        return id;
    }

      public void setId(Integer id) {
          this.id = id;
      }
    
    public String getContent() {
        return content;
    }

      public void setContent(String content) {
          this.content = content;
      }
    
    public Long getCounts() {
        return counts;
    }

      public void setCounts(Long counts) {
          this.counts = counts;
      }
    
    public Long getVersion() {
        return version;
    }

      public void setVersion(Long version) {
          this.version = version;
      }

    @Override
    public String toString() {
        return "Tags{" +
              "id=" + id +
                  ", content=" + content +
                  ", counts=" + counts +
                  ", version=" + version +
              "}";
    }
}
